package com.maze.telegramz;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;

import org.drinkless.td.libcore.telegram.Client;
import org.drinkless.td.libcore.telegram.TdApi;

import java.io.File;
import java.util.Collections;
import java.util.NavigableSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import static com.maze.telegramz.ChatsAdapter.makeDateString;
import static com.maze.telegramz.ChatsAdapter.makeLastMsgStr;
import static com.maze.telegramz.ChatsFragment.chatsArrayList;
import static com.maze.telegramz.ConvoActivity.chatId;
import static com.maze.telegramz.ConvoActivity.msgListAdptr;
import static com.maze.telegramz.HomeActivity.ic;

public class Telegram {
    private static final ConcurrentMap<Integer, TdApi.User> users = new ConcurrentHashMap<>();
    private static final ConcurrentMap<Integer, TdApi.BasicGroup> basicGroups = new ConcurrentHashMap<>();
    private static final ConcurrentMap<Integer, TdApi.Supergroup> supergroups = new ConcurrentHashMap<>();
    private static final ConcurrentMap<Integer, TdApi.SecretChat> secretChats = new ConcurrentHashMap<>();
    private static final ConcurrentMap<Integer, TdApi.Message> fileMessageMap = new ConcurrentHashMap<>();
    private static TdApi.User me;

    static final ConcurrentMap<Long, TdApi.Chat> chats = new ConcurrentHashMap<Long, TdApi.Chat>();
    static final NavigableSet<OrderedChat> chatList = new TreeSet<OrderedChat>();
    private static boolean haveFullChatList = false;

    private static final ConcurrentMap<Integer, TdApi.UserFullInfo> usersFullInfo = new ConcurrentHashMap<Integer, TdApi.UserFullInfo>();
    private static final ConcurrentMap<Integer, TdApi.BasicGroupFullInfo> basicGroupsFullInfo = new ConcurrentHashMap<Integer, TdApi.BasicGroupFullInfo>();
    private static final ConcurrentMap<Integer, TdApi.SupergroupFullInfo> supergroupsFullInfo = new ConcurrentHashMap<Integer, TdApi.SupergroupFullInfo>();

    public static Client client;
    private static final Lock authorizationLock = new ReentrantLock();
    private static final Condition gotAuthorization = authorizationLock.newCondition();
    static TdApi.AuthorizationState authorizationState;
    private static String phoneNum, verfCode, password;
    static boolean haveAuthorization;

    private static void onAuthorizationStateUpdated(TdApi.AuthorizationState authorizationState) {
        if (authorizationState != null) {
            Telegram.authorizationState = authorizationState;
        }
        switch (Telegram.authorizationState.getConstructor()) {
            case TdApi.AuthorizationStateWaitTdlibParameters.CONSTRUCTOR:
                TdApi.TdlibParameters parameters = new TdApi.TdlibParameters();
                parameters.databaseDirectory = Environment.getDataDirectory().getPath() + "/data/com.maze.telegramz/files/";
                parameters.useMessageDatabase = true;
                parameters.useSecretChats = true;
                parameters.apiId = 619181;
                parameters.apiHash = "2d6c9c0d28c2118da3ab0b2091bc5a6d";
                parameters.systemLanguageCode = "en";
                parameters.deviceModel = "HUAWEI P9 LITE";
                parameters.systemVersion = "7.0";
                parameters.applicationVersion = "1.0";
                parameters.enableStorageOptimizer = true;
                client.send(new TdApi.SetTdlibParameters(parameters), new Telegram.AuthorizationRequestHandler());
                break;

            case TdApi.AuthorizationStateWaitEncryptionKey.CONSTRUCTOR:
                client.send(new TdApi.CheckDatabaseEncryptionKey(), new Telegram.AuthorizationRequestHandler());
                break;
            case TdApi.AuthorizationStateWaitPhoneNumber.CONSTRUCTOR:
                client.send(new TdApi.SetAuthenticationPhoneNumber(phoneNum, false, false), new Telegram.AuthorizationRequestHandler());
                break;
            case TdApi.AuthorizationStateWaitCode.CONSTRUCTOR:
                client.send(new TdApi.CheckAuthenticationCode(verfCode, "", ""), new Telegram.AuthorizationRequestHandler());
                break;
            case TdApi.AuthorizationStateReady.CONSTRUCTOR:
                client.send(new TdApi.GetMe(), object -> {
                    if (object.getConstructor() == TdApi.User.CONSTRUCTOR) {
                        setMe((TdApi.User) object);
                    }
                }, null);
                client.send(new TdApi.SetOption("notification_group_count_max", new TdApi.OptionValueInteger(5)), null, null);
                client.send(new TdApi.SetOption("notification_group_size_max", new TdApi.OptionValueInteger(5)), null, null);
                SharedPreferences sp = IntroActivity.context.getSharedPreferences("TZSP", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putBoolean("Loggedin", true);
                editor.apply();
//                authorizationLock.lock();
//                try {
//                    gotAuthorization.signal();
//                } finally {
//                    authorizationLock.unlock();
//                }
                getChatList(100);
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
    }

    public static TdApi.User getMe() {
        return me;
    }

    public static void setMe(TdApi.User me) {
        Telegram.me = me;
    }


    private static class AuthorizationRequestHandler implements Client.ResultHandler {
        @Override
        public void onResult(TdApi.Object object) {
            switch (object.getConstructor()) {
                case TdApi.Error.CONSTRUCTOR:
                    onAuthorizationStateUpdated(null);
                    break;
            }
        }
    }


    private static class UpdatesHandler implements Client.ResultHandler {
        @Override
        public void onResult(TdApi.Object object) {
            switch (object.getConstructor()) {
                case TdApi.UpdateAuthorizationState.CONSTRUCTOR:
                    onAuthorizationStateUpdated(((TdApi.UpdateAuthorizationState) object).authorizationState);
                    break;
                case TdApi.UpdateNewMessage.CONSTRUCTOR:
                    TdApi.UpdateNewMessage newMessage = (TdApi.UpdateNewMessage) object;
                    if (chatId == newMessage.message.chatId && newMessage.message.senderUserId != getMe().id)
                        msgListAdptr.addToStart(newMessage.message, true);
                    break;
                case TdApi.UpdateNotificationGroup.CONSTRUCTOR:
                    TdApi.UpdateNotificationGroup notificationGroup = (TdApi.UpdateNotificationGroup) object;
                    NotificationService.notify(notificationGroup);
                    break;
                case TdApi.UpdateNotification.CONSTRUCTOR:
                    TdApi.UpdateNotification updateNotification = (TdApi.UpdateNotification) object;
                    break;
                case TdApi.UpdateActiveNotifications.CONSTRUCTOR:
                    TdApi.UpdateActiveNotifications updateActiveNotifications = (TdApi.UpdateActiveNotifications) object;
                    break;
                case TdApi.UpdateHavePendingNotifications.CONSTRUCTOR:
                    TdApi.UpdateHavePendingNotifications updateHavePendingNotifications =
                            (TdApi.UpdateHavePendingNotifications) object;
                    break;
                case TdApi.UpdateUser.CONSTRUCTOR:
                    TdApi.UpdateUser updateUser = (TdApi.UpdateUser) object;
                    users.put(updateUser.user.id, updateUser.user);
                    break;
                case TdApi.UpdateUserStatus.CONSTRUCTOR: {
                    TdApi.UpdateUserStatus updateUserStatus = (TdApi.UpdateUserStatus) object;
                    TdApi.User user = users.get(updateUserStatus.userId);
                    synchronized (user) {
                        user.status = updateUserStatus.status;
                    }
                    break;
                }
                case TdApi.UpdateBasicGroup.CONSTRUCTOR:
                    TdApi.UpdateBasicGroup updateBasicGroup = (TdApi.UpdateBasicGroup) object;
                    basicGroups.put(updateBasicGroup.basicGroup.id, updateBasicGroup.basicGroup);
                    break;
                case TdApi.UpdateSupergroup.CONSTRUCTOR:
                    TdApi.UpdateSupergroup updateSupergroup = (TdApi.UpdateSupergroup) object;
                    supergroups.put(updateSupergroup.supergroup.id, updateSupergroup.supergroup);
                    break;
                case TdApi.UpdateSecretChat.CONSTRUCTOR:
                    TdApi.UpdateSecretChat updateSecretChat = (TdApi.UpdateSecretChat) object;
                    secretChats.put(updateSecretChat.secretChat.id, updateSecretChat.secretChat);
                    break;

                case TdApi.UpdateNewChat.CONSTRUCTOR: {
                    TdApi.UpdateNewChat updateNewChat = (TdApi.UpdateNewChat) object;
                    TdApi.Chat chat = updateNewChat.chat;
                    synchronized (chat) {
                        chats.put(chat.id, chat);
                        long order = chat.order;
                        chat.order = 0;
                        setChatOrder(chat, order);
                    }
                    updateChatOrder(chat);
                    break;
                }
                case TdApi.UpdateChatTitle.CONSTRUCTOR: {
                    TdApi.UpdateChatTitle updateChat = (TdApi.UpdateChatTitle) object;
                    TdApi.Chat chat = chats.get(updateChat.chatId);
                    synchronized (chat) {
                        chat.title = updateChat.title;
                    }
                    getRecyclerChatsItem(chat.id).setTitle(chat.title);
                    ic.refreshChatsRecycler();
                    break;
                }
                case TdApi.UpdateChatPhoto.CONSTRUCTOR: {
                    TdApi.UpdateChatPhoto updateChat = (TdApi.UpdateChatPhoto) object;
                    TdApi.Chat chat = chats.get(updateChat.chatId);
                    synchronized (chat) {
                        chat.photo = updateChat.photo;
                    }
                    client.send(new TdApi.DownloadFile(chat.photo.small.id, 1, 0, 0, true), new displayPicDownloadHandler());
                    break;
                }
                case TdApi.UpdateChatLastMessage.CONSTRUCTOR: {
                    TdApi.UpdateChatLastMessage updateChat = (TdApi.UpdateChatLastMessage) object;
                    TdApi.Chat chat = chats.get(updateChat.chatId);
                    synchronized (chat) {
                        chat.lastMessage = updateChat.lastMessage;
                        setChatOrder(chat, updateChat.order);
                    }
                    ChatsItem i = getRecyclerChatsItem(chat.id);
                    i.setLastMsg(makeLastMsgStr(chat));
                    i.setDate(makeDateString(chat.lastMessage.date));
                    updateChatOrder(chat);
                    break;
                }
                case TdApi.UpdateChatOrder.CONSTRUCTOR: {
                    TdApi.UpdateChatOrder updateChat = (TdApi.UpdateChatOrder) object;
                    TdApi.Chat chat = chats.get(updateChat.chatId);
                    synchronized (chat) {
                        setChatOrder(chat, updateChat.order);
                    }
                    updateChatOrder(chat);
                    break;
                }
                case TdApi.UpdateChatIsPinned.CONSTRUCTOR: {
                    TdApi.UpdateChatIsPinned updateChat = (TdApi.UpdateChatIsPinned) object;
                    TdApi.Chat chat = chats.get(updateChat.chatId);
                    synchronized (chat) {
                        chat.isPinned = updateChat.isPinned;
                        setChatOrder(chat, updateChat.order);
                    }
                    updateChatOrder(chat);
                    break;
                }
                case TdApi.UpdateChatReadInbox.CONSTRUCTOR: {
                    TdApi.UpdateChatReadInbox updateChat = (TdApi.UpdateChatReadInbox) object;
                    TdApi.Chat chat = chats.get(updateChat.chatId);
                    synchronized (chat) {
                        chat.lastReadInboxMessageId = updateChat.lastReadInboxMessageId;
                        chat.unreadCount = updateChat.unreadCount;
                    }
                    updateChatOrder(chat);
                    break;
                }
                case TdApi.UpdateChatReadOutbox.CONSTRUCTOR: {
                    TdApi.UpdateChatReadOutbox updateChat = (TdApi.UpdateChatReadOutbox) object;
                    TdApi.Chat chat = chats.get(updateChat.chatId);
                    synchronized (chat) {
                        chat.lastReadOutboxMessageId = updateChat.lastReadOutboxMessageId;
                    }
                    updateChatOrder(chat);
                    break;
                }
                case TdApi.UpdateChatUnreadMentionCount.CONSTRUCTOR: {
                    TdApi.UpdateChatUnreadMentionCount updateChat = (TdApi.UpdateChatUnreadMentionCount) object;
                    TdApi.Chat chat = chats.get(updateChat.chatId);
                    synchronized (chat) {
                        chat.unreadMentionCount = updateChat.unreadMentionCount;
                    }
                    updateChatOrder(chat);
                    break;
                }
                case TdApi.UpdateMessageMentionRead.CONSTRUCTOR: {
                    TdApi.UpdateMessageMentionRead updateChat = (TdApi.UpdateMessageMentionRead) object;
                    TdApi.Chat chat = chats.get(updateChat.chatId);
                    synchronized (chat) {
                        chat.unreadMentionCount = updateChat.unreadMentionCount;
                    }
                    updateChatOrder(chat);
                    break;
                }
                case TdApi.UpdateChatReplyMarkup.CONSTRUCTOR: {
                    TdApi.UpdateChatReplyMarkup updateChat = (TdApi.UpdateChatReplyMarkup) object;
                    TdApi.Chat chat = chats.get(updateChat.chatId);
                    synchronized (chat) {
                        chat.replyMarkupMessageId = updateChat.replyMarkupMessageId;
                    }
                    updateChatOrder(chat);
                    break;
                }
                case TdApi.UpdateChatDraftMessage.CONSTRUCTOR: {
                    TdApi.UpdateChatDraftMessage updateChat = (TdApi.UpdateChatDraftMessage) object;
                    TdApi.Chat chat = chats.get(updateChat.chatId);
                    synchronized (chat) {
                        chat.draftMessage = updateChat.draftMessage;
                        setChatOrder(chat, updateChat.order);
                    }
                    updateChatOrder(chat);
                    break;
                }
                case TdApi.UpdateChatNotificationSettings.CONSTRUCTOR: {
                    TdApi.UpdateChatNotificationSettings update = (TdApi.UpdateChatNotificationSettings) object;
                    TdApi.Chat chat = chats.get(update.chatId);
                    synchronized (chat) {
                        chat.notificationSettings = update.notificationSettings;
                    }
                    break;
                }
                case TdApi.UpdateChatDefaultDisableNotification.CONSTRUCTOR: {
                    TdApi.UpdateChatDefaultDisableNotification update = (TdApi.UpdateChatDefaultDisableNotification) object;
                    TdApi.Chat chat = chats.get(update.chatId);
                    synchronized (chat) {
                        chat.defaultDisableNotification = update.defaultDisableNotification;
                    }
                    break;
                }
                case TdApi.UpdateChatIsMarkedAsUnread.CONSTRUCTOR: {
                    TdApi.UpdateChatIsMarkedAsUnread update = (TdApi.UpdateChatIsMarkedAsUnread) object;
                    TdApi.Chat chat = chats.get(update.chatId);
                    synchronized (chat) {
                        chat.isMarkedAsUnread = update.isMarkedAsUnread;
                    }
                    break;
                }
                case TdApi.UpdateChatIsSponsored.CONSTRUCTOR: {
                    TdApi.UpdateChatIsSponsored updateChat = (TdApi.UpdateChatIsSponsored) object;
                    TdApi.Chat chat = chats.get(updateChat.chatId);
                    synchronized (chat) {
                        chat.isSponsored = updateChat.isSponsored;
                        setChatOrder(chat, updateChat.order);
                    }
                    updateChatOrder(chat);
                    break;
                }
                case TdApi.UpdateUserFullInfo.CONSTRUCTOR:
                    TdApi.UpdateUserFullInfo updateUserFullInfo = (TdApi.UpdateUserFullInfo) object;
                    usersFullInfo.put(updateUserFullInfo.userId, updateUserFullInfo.userFullInfo);
                    break;
                case TdApi.UpdateBasicGroupFullInfo.CONSTRUCTOR:
                    TdApi.UpdateBasicGroupFullInfo updateBasicGroupFullInfo = (TdApi.UpdateBasicGroupFullInfo) object;
                    basicGroupsFullInfo.put(updateBasicGroupFullInfo.basicGroupId, updateBasicGroupFullInfo.basicGroupFullInfo);
                    break;
                case TdApi.UpdateFile.CONSTRUCTOR:
                    TdApi.UpdateFile updateFile = (TdApi.UpdateFile) object;
//                    updateFile.file.id
                    //                    if (updateFile.file.local.isDownloadingCompleted) {
//                        for (ChatsItem i : chatsArrayList) {
//                            if (i.getDisplayPicID() == updateFile.file.id) {
//                                i.setDisplayPic(new File(updateFile.file.local.path));
//                            }
//                        }
//                        ic.refreshChatsRecycler();
//                    }
                    break;

                case TdApi.UpdateSupergroupFullInfo.CONSTRUCTOR:
                    TdApi.UpdateSupergroupFullInfo updateSupergroupFullInfo = (TdApi.UpdateSupergroupFullInfo) object;
                    supergroupsFullInfo.put(updateSupergroupFullInfo.supergroupId, updateSupergroupFullInfo.supergroupFullInfo);
                    break;
                default:
                    // print("Unsupported update:" + newLine + object);
            }
        }
    }

    private static void setChatOrder(TdApi.Chat chat, long order) {
        synchronized (chatList) {
            if (chat.order != 0) {
                chatList.remove(new OrderedChat(chat.order, chat.id));
            }

            chat.order = order;

            if (chat.order != 0) {
                chatList.add(new OrderedChat(chat.order, chat.id));
            }
        }
    }

    static void setPhoneNum(String phoneNum) {
        Telegram.phoneNum = phoneNum;
    }

    static void setVerfCode(String verfCode) {
        Telegram.verfCode = verfCode;
    }


    static void startClient() {
        client = Client.create(new UpdatesHandler(), null, null);
//        experimenting registering device with FCM
        NotificationService.updateToken();
//        end experimenting
    }

    static void getChatList(final int limit) {
        synchronized (chatList) {
            if (!haveFullChatList && limit > chatList.size()) {
                long offsetOrder = Long.MAX_VALUE;
                long offsetChatId = 0;
                if (!chatList.isEmpty()) {
                    OrderedChat last = chatList.last();
                    offsetOrder = last.order;
                    offsetChatId = last.chatId;
                }
                client.send(new TdApi.GetChats(offsetOrder, offsetChatId, limit), new Client.ResultHandler() {
                    @Override
                    public void onResult(TdApi.Object object) {
                        switch (object.getConstructor()) {
                            case TdApi.Error.CONSTRUCTOR:
                                break;
                            case TdApi.Chats.CONSTRUCTOR:
                                long[] chatIds = ((TdApi.Chats) object).chatIds;
                                if (chatIds.length == 0) {
                                    synchronized (chatList) {
                                        haveFullChatList = true;
                                    }
                                }
                                getChatList(limit);
                                break;
                            default:
                        }
                    }
                });
            }
        }
        ic.refreshChatsRecycler();
    }


    public static void updateChatOrder(TdApi.Chat chat) {
        ChatsItem i = getRecyclerChatsItem(chat.id);
        if (i == null) {
            ChatsItem ch = ChatsItem.build(chat);
            chatsArrayList.add(ch);
            File f;
            if (chat.photo != null) {
                f = new File(chat.photo.small.local.path);
                ch.setDisplayPicID(chat.photo.small.id);
                if (!f.exists())
                    client.send(new TdApi.DownloadFile(chat.photo.small.id, 1, 0, 0, true), new Telegram.displayPicDownloadHandler());
                else
                    ch.setDisplayPic(f);
            }
        }
        i.setOrder(chat.order);
        Collections.sort(chatsArrayList);
        ic.refreshChatsRecycler();
    }

    public static ChatsItem getRecyclerChatsItem(long id) {
        for (ChatsItem i : chatsArrayList) {
            if (id == i.getId())
                return i;
        }
        return null;
    }

    static class OrderedChat implements Comparable<OrderedChat> {
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

    public static class displayPicDownloadHandler implements Client.ResultHandler {
        @Override
        public void onResult(TdApi.Object object) {
            if (object.getConstructor() == TdApi.File.CONSTRUCTOR) {
                TdApi.File f = (TdApi.File) object;
                if (f.local.isDownloadingCompleted) {
                    for (ChatsItem i : chatsArrayList) {
                        if (i.getDisplayPicID() == f.id && i.getId() != getMe().id) {
                            i.setDisplayPic(new File(f.local.path));
                            ic.refreshChatsRecycler();
                        }
                    }
                }
            }
        }
    }

}