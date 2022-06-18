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
import com.example.footsapp_android.web.LoginAPI;
import com.example.footsapp_android.web.MessageAPI;

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

        MessageAPI messageAPI = new MessageAPI(messageDao, contactDao, LoginAPI.getToken(), contact.getUsername());
        Thread thread = new Thread(messageAPI);
        thread.start();
        try {
            thread.join();
            messages.clear();
            messages.addAll(messageDao.index());
            messages.removeIf(m -> !m.getSentFrom().equals(contact.getUsername()));
            adapter.notifyDataSetChanged();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        thread.interrupt();

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
        adapter.setMessages(messages);
        binding.contactName.setText(this.contact.getNickname());


    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void sendMessage() {
        // TODO send message to the server

        // generate random sender or not sender
        boolean sender = (int) (Math.random() * 2) > 1;
        //DateTimeFormatter dtf = DateTimeFormatter.ofPattern("uuuu/MM/ddTHH:mm:ss");
        //LocalDateTime now = LocalDateTime.now();
        Message message = new Message(binding.inputMsg.getText().toString(), "12:00", sender, contact.getUsername()); // find a way to generate an id number from db
        messageDao.insert(message);
        messages.clear();
        messages.addAll(messageDao.index());
        adapter.notifyItemRangeInserted(messages.size(), messages.size());
        binding.lvMessages.smoothScrollToPosition(messages.size() - 1);
        binding.lvMessages.setVisibility(View.VISIBLE);
        binding.inputMsg.setText(null);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setListeners() {
        binding.buttonSend.setOnClickListener(view -> {
            MessageAPI messageAPI = new MessageAPI(messageDao, contactDao, LoginAPI.getToken(), contact.getUsername());
            messageAPI.post(binding.inputMsg.getText().toString());
            sendMessage();
        });



    }

    @Override
    public void onMessageClick(int position) {
        return;
    }


}