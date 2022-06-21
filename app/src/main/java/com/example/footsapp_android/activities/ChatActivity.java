package com.example.footsapp_android.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.footsapp_android.AppDB;
import com.example.footsapp_android.ContactDao;
import com.example.footsapp_android.MessageDao;
import com.example.footsapp_android.adapters.MessageListAdapter;
import com.example.footsapp_android.databinding.ActivityChatBinding;
import com.example.footsapp_android.entities.Contact;
import com.example.footsapp_android.entities.Message;
import com.example.footsapp_android.entities.Transfer;
import com.example.footsapp_android.web.LoginAPI;
import com.example.footsapp_android.web.MessageAPI;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ChatActivity extends AppCompatActivity implements MessageListAdapter.OnMessageClickListener {

    private AppDB db;
    private Contact contact;
    private List<Message> messages = new ArrayList<>();
    private MessageListAdapter adapter;
    private ActivityChatBinding binding;
    private PreferenceManager prefManager;
    ContactDao contactDao;
    MessageDao messageDao;
    public static final String NOTIFY_ACTIVITY_ACTION = "notify_activity";
    private BroadcastReceiver broadcastReceiver;

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            startActivity(new Intent(this, ChatLandscapeActivity.class).
                    putExtra("username", contact.getUsername()).
                    putExtra("fromChat", true));
        }

        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            startActivity(new Intent(this, ChatsActivity.class));
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
        setListeners();

        //Button buttonSend = findViewById(R.id.button_send);

    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void init() {
        //this.prefManager = new PreferenceManager(getApplicationContext());
        db = AppDB.getDatabase(this);
        this.contactDao = db.contactDao();
        this.messageDao = db.messageDao();
        // get the nickname from the intent extra "contact_nickname"
        Integer contactId = getIntent().getIntExtra("contact_id", -1);
        // get the contact from the db
        this.contact = contactDao.get(contactId);
        this.messages = messageDao.get(contact.getUsername());
        adapter = new MessageListAdapter(this, this);
        binding.lvMessages.setAdapter(adapter);
        binding.lvMessages.setLayoutManager(new LinearLayoutManager(this));
        // make the view atart aleady from the bottom of the messages
        binding.lvMessages.scrollToPosition(messages.size() - 1);
        adapter.setMessages(messages);
        binding.contactName.setText(this.contact.getNickname());


    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String getCurrentTime() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm");
        LocalDateTime now = LocalDateTime.now();
        String currentTime = dtf.format(now);
        return currentTime;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void sendMessage() {
        String content = binding.inputMsg.getText().toString();
        String currentTime = getCurrentTime();
        Message message = new Message(content, currentTime, true, contact.getUsername()); // find a way to generate an id number from db
        messageDao.insert(message);
        messages.clear();
        messages.addAll(messageDao.get(contact.getUsername()));
        adapter.notifyItemRangeInserted(messages.size(), messages.size());
        binding.lvMessages.smoothScrollToPosition(messages.size() - 1);
        binding.lvMessages.setVisibility(View.VISIBLE);
        binding.inputMsg.setText(null);

        contact.setLastMessage(content);
        contact.setTime(currentTime);
        contactDao.update(contact);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setListeners() {
        binding.buttonSend.setOnClickListener(view -> {

            MessageAPI messageAPI = new MessageAPI(messageDao, contactDao, LoginAPI.getToken(), contact.getUsername(), contact.getServer());
            String myUser = getApplicationContext().
                    getSharedPreferences("user", MODE_PRIVATE).
                    getString("my_user", null);
            Transfer transfer = new Transfer(myUser, contact.getUsername(), binding.inputMsg.getText().toString());
            if (messageAPI.post(binding.inputMsg.getText().toString(), transfer)) {
                sendMessage();
            }


        });


    }

    @Override
    public void onMessageClick(int position) {
        return;
    }

    @Override
    protected void onStart() {
        super.onStart();
        broadcastReceiver = new BroadcastReceiver() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(NOTIFY_ACTIVITY_ACTION)) {
                    String content = intent.getExtras().getString("content", null);
                    String[] info = content.split(": ");
                    String from = info[0];
                    String text = info[1];
                    // TODO: set current time
                    String currentTime = getCurrentTime();
                    Message m = new Message(text, currentTime, false, from);
                    messageDao.insert(m);
                    if (Objects.equals(from, contact.getUsername())) {
                        messages.clear();
                        messages.addAll(messageDao.get(contact.getUsername()));
                        adapter.notifyDataSetChanged();
                    }
                    Contact contact = contactDao.index().stream().filter(c -> Objects.equals(c.getUsername(), from))
                            .collect(Collectors.toList()).get(0);

                    contact.setLastMessage(text);
                    contact.setTime(currentTime);
                    contactDao.update(contact);
                }
            }
        };

        IntentFilter filter = new IntentFilter(NOTIFY_ACTIVITY_ACTION);
        registerReceiver(broadcastReceiver, filter);
    }

    private void launchEvent() {
        Intent eventIntent = new Intent("ReceiveMessage");
        this.sendBroadcast(eventIntent);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(broadcastReceiver);
    }


}