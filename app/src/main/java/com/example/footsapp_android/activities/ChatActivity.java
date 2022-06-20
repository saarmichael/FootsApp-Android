package com.example.footsapp_android.activities;

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

public class ChatActivity extends AppCompatActivity implements MessageListAdapter.OnMessageClickListener {

    private AppDB db;
    private Contact contact;
    private List<Message> messages = new ArrayList<>();
    private MessageListAdapter adapter;
    private ActivityChatBinding binding;
    private PreferenceManager prefManager;
    ContactDao contactDao;
    MessageDao messageDao;


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
        this.messages = messageDao.index();
        messages.removeIf(m -> !m.getSentFrom().equals(contact.getUsername()));
        adapter = new MessageListAdapter(this, this);
        binding.lvMessages.setAdapter(adapter);
        binding.lvMessages.setLayoutManager(new LinearLayoutManager(this));
        // make the view atart aleady from the bottom of the messages
        binding.lvMessages.scrollToPosition(messages.size() - 1);
        adapter.setMessages(messages);
        binding.contactName.setText(this.contact.getNickname());


    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void sendMessage() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm");
        LocalDateTime now = LocalDateTime.now();
        String currentTime = dtf.format(now);
        String content = binding.inputMsg.getText().toString();
        Message message = new Message(content, currentTime, true, contact.getUsername()); // find a way to generate an id number from db
        messageDao.insert(message);
        messages.clear();
        messages.addAll(messageDao.index());
        messages.removeIf(m -> !m.getSentFrom().equals(contact.getUsername()));
        adapter.notifyItemRangeInserted(messages.size(), messages.size());
        binding.lvMessages.smoothScrollToPosition(messages.size() - 1);
        binding.lvMessages.setVisibility(View.VISIBLE);
        binding.inputMsg.setText(null);

        contact.setLastMessage(content);
        contact.setTime(currentTime.toString());
        contactDao.update(contact);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setListeners() {
        binding.buttonSend.setOnClickListener(view -> {
            MessageAPI messageAPI = new MessageAPI(messageDao, contactDao, LoginAPI.getToken(), contact.getUsername());
            String myUser = getApplicationContext().
                    getSharedPreferences("user", MODE_PRIVATE).
                    getString("my_user", null);
            Transfer transfer = new Transfer(myUser, contact.getUsername(), binding.inputMsg.getText().toString());
            messageAPI.post(binding.inputMsg.getText().toString(), transfer);

            sendMessage();
        });



    }

    @Override
    public void onMessageClick(int position) {
        return;
    }


}