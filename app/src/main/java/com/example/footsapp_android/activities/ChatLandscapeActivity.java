package com.example.footsapp_android.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.footsapp_android.AppDB;
import com.example.footsapp_android.ContactDao;
import com.example.footsapp_android.MessageDao;
import com.example.footsapp_android.R;
import com.example.footsapp_android.adapters.ContactsListAdapter;
import com.example.footsapp_android.adapters.MessageListAdapter;
import com.example.footsapp_android.databinding.ActivityChatLandcapeBinding;
import com.example.footsapp_android.entities.Contact;
import com.example.footsapp_android.entities.Message;
import com.example.footsapp_android.entities.Transfer;
import com.example.footsapp_android.web.ContactAPI;
import com.example.footsapp_android.web.LoginAPI;
import com.example.footsapp_android.web.MessageAPI;

import java.util.ArrayList;
import java.util.List;

public class ChatLandscapeActivity extends AppCompatActivity implements ContactsListAdapter.OnContactClickListener, MessageListAdapter.OnMessageClickListener {

    private AppDB db;
    // contacts panel
    private ContactDao contactDao;
    private ContactAPI contactAPI;
    private List<Contact> contacts;
    private ContactsListAdapter cAdapter;

    // chat panel
    private Contact chosenContact;
    MessageDao messageDao;
    private List<Message> messages = new ArrayList<>();
    private MessageListAdapter mAdapter;

    // binding
    private ActivityChatLandcapeBinding binding;


    private void loadProfileImage() {
        Boolean hasImg = getApplicationContext().
                getSharedPreferences("user", MODE_PRIVATE).
                getBoolean("has_img", false);
        if (hasImg) {
            byte[] bytes = Base64.decode(
                    getApplicationContext().
                            getSharedPreferences("user", MODE_PRIVATE).
                            getString("image", null), Base64.DEFAULT
            );
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            binding.userImage.setImageBitmap(bitmap);
        } else {
            binding.userImage.setImageResource(R.drawable.footsapp_home_logo);
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onContactClick(int position) {
        // TODO: load the messages to chat panel
        chosenContact = contacts.get(position);
        showChat();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void showChat() {
        if (chosenContact != null) {
            binding.lvMessages.setVisibility(View.VISIBLE);
            binding.lvMessages.scrollToPosition(messages.size() - 1);
            List<Message> chosenMessages = new ArrayList<>(messages);
            chosenMessages.removeIf(m -> !m.getSentFrom().equals(chosenContact.getUsername()));
            mAdapter.setMessages(chosenMessages);
            binding.contactName.setText(this.chosenContact.getNickname());
        }
    }

    private void init() {
        db = AppDB.getDatabase(this);
        contactDao = db.contactDao();
        contactAPI = new ContactAPI(contactDao, LoginAPI.getToken());
        contacts = new ArrayList<>();

        messageDao = db.messageDao();
        messages = messageDao.index();
        mAdapter = new MessageListAdapter(this, this);
        binding.lvMessages.setAdapter(mAdapter);
        binding.lvMessages.setLayoutManager(new LinearLayoutManager(this));
        binding.lvMessages.setVisibility(View.GONE);
        // make the view atart aleady from the bottom of the messages


        cAdapter = new ContactsListAdapter(this, this);
        binding.lvContacts.setAdapter(cAdapter);
        binding.lvContacts.setLayoutManager(new LinearLayoutManager(this));
        contacts = contactDao.index();
        cAdapter.setContacts(contacts);
        Thread thread = new Thread(contactAPI);
        thread.start();
        try {
            thread.join();
            contacts.clear();
            contacts.addAll(contactDao.index());
            cAdapter.notifyDataSetChanged();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        loadProfileImage();
    }

    private void sendMessage() {
        // TODO send message to the server

        //int size = messageDao.index().size() + 1;
        // generate random sender or not sender
        // TODO: set current time and send to constructor
        String content = binding.inputMsg.getText().toString();
        String time = "12:00";
        Message message = new Message(content,time, true, chosenContact.getUsername()); // find a way to generate an id number from db
        messageDao.insert(message);
        messages.clear();
        messages.addAll(messageDao.index());
        mAdapter.notifyItemRangeInserted(messages.size(), messages.size());
        binding.lvMessages.smoothScrollToPosition(messages.size() - 1);
        binding.lvMessages.setVisibility(View.VISIBLE);
        binding.inputMsg.setText(null);

        chosenContact.setLastMessage(content);
        chosenContact.setTime(time);
        contactDao.update(chosenContact);
    }

    private void setListeners() {
        // adding a new contact
        binding.btnNew.setOnClickListener(view -> {
            Intent i = new Intent(this, AddChatActivity.class);
            startActivity(i);
        });

        binding.buttonSend.setOnClickListener(view -> {
            MessageAPI messageAPI = new MessageAPI(messageDao, contactDao, LoginAPI.getToken(), chosenContact.getUsername());
            String myUser = getApplicationContext().
                    getSharedPreferences("user", MODE_PRIVATE).
                    getString("my_user", null);
            Transfer transfer = new Transfer(myUser, chosenContact.getUsername(), binding.inputMsg.getText().toString());
            messageAPI.post(binding.inputMsg.getText().toString(), transfer);
            sendMessage();
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatLandcapeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
        setListeners();

    }

    @Override
    protected void onResume() {
        super.onResume();
        contacts.clear();
        contacts.addAll(contactDao.index());
        cAdapter.notifyDataSetChanged();
    }

    @Override
    public void onMessageClick(int position) {
        return;
    }
}