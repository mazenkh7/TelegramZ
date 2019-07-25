package com.maze.telegramz;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.maze.telegramz.holders.IncomingImageHolder;
import com.maze.telegramz.holders.IncomingTextMessageHolder;
import com.maze.telegramz.holders.OutcomingTextMessageHolder;
import com.r0adkll.slidr.Slidr;
import com.squareup.picasso.Picasso;
import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

import org.drinkless.td.libcore.telegram.TdApi;

import java.io.File;
import java.util.Arrays;

import static com.maze.telegramz.Telegram.client;
import static com.maze.telegramz.Telegram.getMe;

public class ConvoActivity extends AppCompatActivity {

    public static long chatId;
    private long lastLoadedMsgId = 0;
    private MessagesList messagesList;
    public static MessagesListAdapter<TdApi.Message> msgListAdptr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_convo);
        Slidr.attach(this);
        Bundle extras = getIntent().getExtras();
        setTitle(extras.getString("title"));
        chatId = extras.getLong("id");
        messagesList = findViewById(R.id.messagesList);
        MessagesListAdapter.HoldersConfig holdersConfig = new MessagesListAdapter.HoldersConfig();
        holdersConfig.setIncomingTextHolder(IncomingTextMessageHolder.class);
        holdersConfig.setIncomingTextLayout(R.layout.viewholder_incoming);

        holdersConfig.setIncomingImageHolder(IncomingImageHolder.class);
        holdersConfig.setIncomingImageLayout(R.layout.viewholder_incoming_image);

        holdersConfig.setOutcomingTextHolder(OutcomingTextMessageHolder.class);
        holdersConfig.setOutcomingTextLayout(R.layout.viewholder_outcoming);

        msgListAdptr = new MessagesListAdapter<>(""+getMe().id,holdersConfig, (imageView, url, payload) -> Picasso.get().load(new File(url)).placeholder(R.drawable.circular_progress_bar).into(imageView));
        messagesList.setAdapter(msgListAdptr);
        MessageInput inputView = findViewById(R.id.input);
        inputView.setInputListener(input -> {
            TdApi.InputMessageText inputMessageText = new TdApi.InputMessageText(new TdApi.FormattedText(input.toString(),null),false,true);
            client.send(new TdApi.SendMessage(chatId, 0, false, false, null, inputMessageText),
                    object -> runOnUiThread(() -> msgListAdptr.addToStart((TdApi.Message)object,true)), null);
            return true;
        });
        loadMessages();
        msgListAdptr.setLoadMoreListener((page, totalItemsCount) -> loadMessages());
    }

    private void loadMessages(){
        client.send(new TdApi.GetChatHistory(chatId,lastLoadedMsgId,0,100,false), object -> {
            TdApi.Messages msgs = (TdApi.Messages) object;
            lastLoadedMsgId = msgs.messages[msgs.totalCount-1].id;
            runOnUiThread(() -> msgListAdptr.addToEnd(Arrays.asList(msgs.messages),false));
            for(TdApi.Message message : msgs.messages){
                if(message.content.getConstructor() == TdApi.MessagePhoto.CONSTRUCTOR)
                    loadImages(message);
            }
        });
    }

    private void loadImages(TdApi.Message m){
        TdApi.MessagePhoto mph = (TdApi.MessagePhoto)m.content;
        client.send(new TdApi.DownloadFile(mph.photo.sizes[mph.photo.sizes.length-1].photo.id, 1, 0, 0, false), object -> {
            if (object.getConstructor() == TdApi.File.CONSTRUCTOR) {
                TdApi.File f = (TdApi.File) object;
                m.setImageUrl(f.local.path);
                if (f.local.isDownloadingCompleted) {
                    runOnUiThread(()->msgListAdptr.update(m));
                }else{
                    runOnUiThread(()->msgListAdptr.update(m));
                }
            }
        });
    }

}
