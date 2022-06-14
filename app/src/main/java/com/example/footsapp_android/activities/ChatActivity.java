package com.example.footsapp_android.activities;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.footsapp_android.AppDB;
import com.example.footsapp_android.ContactDao;
import com.example.footsapp_android.MessageDao;
import com.example.footsapp_android.R;
import com.example.footsapp_android.adapters.MessageListAdapter;
import com.example.footsapp_android.databinding.ActivityChatBinding;
import com.example.footsapp_android.entities.Contact;
import com.example.footsapp_android.entities.Message;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();

        Button buttonSend = findViewById(R.id.button_send);

    }


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
        adapter = new MessageListAdapter(this, this);
        binding.lvMessages.setAdapter(adapter);
        binding.lvMessages.setLayoutManager(new LinearLayoutManager(this));
        adapter.setMessages(messages);
        binding.contactName.setText(this.contact.getNickname());
    }

    private void sendMessage() {
        // TODO send message to the server
        binding.inputMsg.setText(null);
        int size = messageDao.index().size() + 1;
        Message message = new Message(size, binding.inputMsg.getText().toString(), true); // find a way to generate an id number from db
        messageDao.insert(message);
        messages.clear();
        messages.addAll(messageDao.index());
        adapter.notifyItemRangeInserted(messages.size(), messages.size());
        binding.lvMessages.smoothScrollToPosition(messages.size() - 1);
        binding.lvMessages.setVisibility(View.VISIBLE);
    }

    private void setListeners() {
        binding.buttonSend.setOnClickListener(view -> sendMessage());


    }

    @Override
    public void onMessageClick(int position) {
        return;
    }
}