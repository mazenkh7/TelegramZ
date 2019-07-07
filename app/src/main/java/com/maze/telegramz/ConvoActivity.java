package com.maze.telegramz;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

import org.drinkless.td.libcore.telegram.Client;
import org.drinkless.td.libcore.telegram.TdApi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.maze.telegramz.Telegram.client;
import static com.maze.telegramz.Telegram.getMe;

public class ConvoActivity extends AppCompatActivity {

    private long chatId;
    private long lastLoadedMsgId = 0;
    private MessagesList messagesList;
    public MessagesListAdapter<TdApi.Message> msgListAdptr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_convo);
        Bundle extras = getIntent().getExtras();
        setTitle(extras.getString("title"));
        chatId = extras.getLong("id");
        messagesList = findViewById(R.id.messagesList);
        msgListAdptr = new MessagesListAdapter<>(""+getMe().id, null);
        messagesList.setAdapter(msgListAdptr);
        MessageInput inputView = findViewById(R.id.input);
        inputView.setInputListener(new MessageInput.InputListener() {
            @Override
            public boolean onSubmit(CharSequence input) {
                TdApi.InputMessageText inputMessageText = new TdApi.InputMessageText(new TdApi.FormattedText(input.toString(),null),false,true);
                client.send(new TdApi.SendMessage(chatId, 0, false, false, null, inputMessageText), new Client.ResultHandler() {
                    @Override
                    public void onResult(final TdApi.Object object) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                msgListAdptr.addToStart((TdApi.Message)object,true);
                            }
                        });
                    }
                }, null);
                return true;
            }
        });
        loadMessages();
        msgListAdptr.setLoadMoreListener(new MessagesListAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                loadMessages();
            }
        });
    }

    private void loadMessages(){
        client.send(new TdApi.GetChatHistory(chatId,lastLoadedMsgId,0,100,false), new Client.ResultHandler() {
            @Override
            public void onResult(TdApi.Object object) {
                TdApi.Messages msgs = (TdApi.Messages) object;
                final List msgsList = Arrays.asList(msgs.messages);
                lastLoadedMsgId = msgs.messages[msgs.totalCount-1].id;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        msgListAdptr.addToEnd(msgsList,false);
                    }
                });
            }
        });
    }
}
