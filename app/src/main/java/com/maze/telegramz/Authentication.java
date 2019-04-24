package com.maze.telegramz;

import android.content.Context;
import android.os.Environment;

import java.util.NavigableSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.drinkless.td.libcore.telegram.Client;
import org.drinkless.td.libcore.telegram.TdApi;

public class Authentication {
    private static final TreeSet<OrderedChat> chatList = new TreeSet<>();
    private static final ConcurrentHashMap<Long, TdApi.Chat> chats = new ConcurrentHashMap<>();
    private static boolean haveFullChatList = false;

    private static Client client;
    private static final Lock authorizationLock = new ReentrantLock();
    private static final Condition gotAuthorization = authorizationLock.newCondition();
    private static TdApi.AuthorizationState authorizationState;
    private static String phoneNum, verfCode, password;
    private static boolean haveAuthorization;

    private static int onAuthorizationStateUpdated(TdApi.AuthorizationState authorizationState) {
        if (authorizationState != null) {
            Authentication.authorizationState = authorizationState;
        }
        switch (Authentication.authorizationState.getConstructor()) {
            case TdApi.AuthorizationStateWaitTdlibParameters.CONSTRUCTOR:
                TdApi.TdlibParameters parameters = new TdApi.TdlibParameters();
                parameters.databaseDirectory = Environment.getDataDirectory().getPath() + "/data/com.maze.telegramz/tdlib/";
                parameters.useMessageDatabase = true;
                parameters.useSecretChats = true;
                parameters.apiId = 619181;
                parameters.apiHash = "2d6c9c0d28c2118da3ab0b2091bc5a6d";
                parameters.systemLanguageCode = "en";
                parameters.deviceModel = "Android";
                parameters.systemVersion = "Unknown";
                parameters.applicationVersion = "1.0";
                parameters.enableStorageOptimizer = true;
                client.send(new TdApi.SetTdlibParameters(parameters), new Authentication.AuthorizationRequestHandler());
                break;

            case TdApi.AuthorizationStateWaitEncryptionKey.CONSTRUCTOR:
                client.send(new TdApi.CheckDatabaseEncryptionKey(), new Authentication.AuthorizationRequestHandler());
                break;

            case TdApi.AuthorizationStateWaitPhoneNumber.CONSTRUCTOR:
                client.send(new TdApi.SetAuthenticationPhoneNumber(phoneNum, false, false), new Authentication.AuthorizationRequestHandler());
                break;

            case TdApi.AuthorizationStateWaitCode.CONSTRUCTOR:
                client.send(new TdApi.CheckAuthenticationCode(verfCode, "", ""), new Authentication.AuthorizationRequestHandler());
                break;

            case TdApi.AuthorizationStateReady.CONSTRUCTOR:
                haveAuthorization = true;
                authorizationLock.lock();
                try {
                    gotAuthorization.signal();
                } finally {
                    authorizationLock.unlock();
                }
                break;

            case TdApi.AuthorizationStateClosed.CONSTRUCTOR:
                break;

            case TdApi.AuthorizationStateClosing.CONSTRUCTOR:
                break;

            case TdApi.AuthorizationStateLoggingOut.CONSTRUCTOR:
                break;

            case TdApi.AuthorizationStateWaitPassword.CONSTRUCTOR:
                break;

        }
        return Authentication.authorizationState.getConstructor();
    }


    private static class AuthorizationRequestHandler implements Client.ResultHandler {
        @Override
        public void onResult(TdApi.Object object) {
            switch (object.getConstructor()) {
                case TdApi.Error.CONSTRUCTOR:
                    onAuthorizationStateUpdated(null); // repeat last action
                    break;
            }
        }
    }


    private static class AuthUpdatesHandler implements Client.ResultHandler {
        @Override
        public void onResult(TdApi.Object object) {
            onAuthorizationStateUpdated(((TdApi.UpdateAuthorizationState) object).authorizationState);
        }
    }

    private static void setPhoneNum(String phoneNum) {
        Authentication.phoneNum = phoneNum;
    }

    private static void setVerfCode(String verfCode) {
        Authentication.verfCode = verfCode;
    }

    static void sendPhoneNum(String phoneNum) {
        setPhoneNum(phoneNum);
        client = Client.create(new AuthUpdatesHandler(), null, null);
    }

    static void sendVerfCode(String verfCode) {
        setVerfCode(verfCode);
//        client = Client.create(new AuthUpdatesHandler(), null, null);
    }


    private static void getChatList(final int limit) {
        synchronized (chatList) {
            if (!haveFullChatList && limit > chatList.size()) {
                // have enough chats in the chat list or chat list is too small
                long offsetOrder = Long.MAX_VALUE;
                long offsetChatId = 0;
                if (!chatList.isEmpty()) {
                    OrderedChat last = chatList.last();
                    offsetOrder = last.order;
                    offsetChatId = last.chatId;
                }
                client.send(new TdApi.GetChats(offsetOrder, offsetChatId, limit - chatList.size()), new Client.ResultHandler() {
                    @Override
                    public void onResult(TdApi.Object object) {
                        switch (object.getConstructor()) {
                            case TdApi.Error.CONSTRUCTOR:
//                                System.err.println("Receive an error for GetChats:" + newLine + object);
                                break;
                            case TdApi.Chats.CONSTRUCTOR:
                                long[] chatIds = ((TdApi.Chats) object).chatIds;
                                if (chatIds.length == 0) {
                                    synchronized (chatList) {
                                        haveFullChatList = true;
                                    }
                                }
                                // chats had already been received through updates, let's retry request
                                getChatList(limit);
                                break;
                            default:
//                                System.err.println("Receive wrong response from TDLib:" + newLine + object);
                        }
                    }
                });
                return;
            }

        }
    }

    private static class OrderedChat implements Comparable<OrderedChat> {
        final long order;
        final long chatId;

        OrderedChat(long order, long chatId) {
            this.order = order;
            this.chatId = chatId;
        }

        @Override
        public int compareTo(OrderedChat o) {
            if (this.order != o.order) {
                return o.order < this.order ? -1 : 1;
            }
            if (this.chatId != o.chatId) {
                return o.chatId < this.chatId ? -1 : 1;
            }
            return 0;
        }

        @Override
        public boolean equals(Object obj) {
            OrderedChat o = (OrderedChat) obj;
            return this.order == o.order && this.chatId == o.chatId;
        }
    }

}