package com.example.footsapp_android.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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

    // notifications
    public static final String NOTIFY_ACTIVITY_ACTION = "notify_activity";
    public static final String NOTIFY_ACTIVITY_ACTION2 = "notify_activity2";
    private BroadcastReceiver broadcastReceiver;


    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE)
        {
            startActivity(new Intent(this, ChatsActivity.class));
        }

        if(newConfig.orientation == Configuration.ORIENTATION_PORTRAIT)
        {
            startActivity(new Intent(this, ChatsActivity.class));
        }
    }

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
        chosenContact = contacts.get(position);
        showChat();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void showChat() {
        if (chosenContact != null) {
            binding.lvMessages.setVisibility(View.VISIBLE);
            binding.lvMessages.scrollToPosition(messages.size() - 1);
            messages = messageDao.get(chosenContact.getUsername());
            mAdapter.setMessages(messages);
            binding.contactName.setText(this.chosenContact.getNickname());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void init(boolean isFromChat) {
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
        // make the view start already from the bottom of the messages


        cAdapter = new ContactsListAdapter(this, this);
        binding.lvContacts.setAdapter(cAdapter);
        binding.lvContacts.setLayoutManager(new LinearLayoutManager(this));
        contacts = contactDao.index();
        cAdapter.setContacts(contacts);

        if (isFromChat) {
            String username = getIntent().getStringExtra("username");
            // get from contactDao the specific contact according to username
            chosenContact = contactDao.index().stream().filter(c -> c.getUsername().equals(username)).findFirst().get();
            showChat();
        }

        try {
            Thread contactsThread = new Thread(contactAPI);
            contactsThread.start();
            contactsThread.join();
            Log.d("contactdao", "asd");
            contacts.clear();
            contacts.addAll(contactDao.index());
            cAdapter.notifyItemRangeInserted(contacts.size(), contacts.size());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        messageDao.nukeTable();
        for (Contact c : contacts) {
            MessageAPI messageAPI = new MessageAPI(messageDao, contactDao, LoginAPI.getToken(), c.getUsername(), null);
            Thread thread = new Thread(messageAPI);
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        loadProfileImage();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void sendMessage() {
        //int size = messageDao.index().size() + 1;
        // generate random sender or not sender
        String content = binding.inputMsg.getText().toString();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm");
        LocalDateTime now = LocalDateTime.now();
        String currentTime = dtf.format(now);
        Message message = new Message(content, currentTime, true, chosenContact.getUsername()); // find a way to generate an id number from db
        messageDao.insert(message);
        messages.clear();
        messages.addAll(messageDao.get(chosenContact.getUsername()));
        mAdapter.notifyItemRangeInserted(messages.size(), messages.size());
        //mAdapter.notifyDataSetChanged();
        binding.lvMessages.smoothScrollToPosition(messages.size() - 1);
        binding.lvMessages.setVisibility(View.VISIBLE);
        binding.inputMsg.setText(null);

        chosenContact.setLastMessage(content);
        chosenContact.setTime(currentTime);
        contactDao.update(chosenContact);
        cAdapter.notifyDataSetChanged();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setListeners() {
        // adding a new contact
        binding.btnNew.setOnClickListener(view -> {
            Intent i = new Intent(this, AddChatActivity.class);
            startActivity(i);
        });

        binding.buttonSend.setOnClickListener(view -> {
            String content = binding.inputMsg.getText().toString();

            MessageAPI messageAPI = new MessageAPI(messageDao, contactDao, LoginAPI.getToken(), chosenContact.getUsername(), chosenContact.getServer());
            String myUser = getApplicationContext().
                    getSharedPreferences("user", MODE_PRIVATE).
                    getString("my_user", null);
            Transfer transfer = new Transfer(myUser, chosenContact.getUsername(), content);
            if (messageAPI.post(content, transfer)) {
                sendMessage();
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatLandcapeBinding.inflate(getLayoutInflater());
        binding.chatsHeadline.setText(MainActivity.CURRENT_USER);
        setContentView(binding.getRoot());
        // get the intent's extra
        init(getIntent().getBooleanExtra("fromChat", false));
        setListeners();

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        try {
            Thread contactsThread = new Thread(contactAPI);
            contactsThread.start();
            contactsThread.join();

            messageDao.nukeTable();
            for (Contact c : contacts) {
                MessageAPI messageAPI = new MessageAPI(messageDao, contactDao, LoginAPI.getToken(), c.getUsername(), null);
                Thread thread = new Thread(messageAPI);
                thread.start();
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            contacts.clear();
            contacts.addAll(contactDao.index());
            messages.clear();
            messages.addAll(messageDao.index());
            cAdapter.notifyDataSetChanged();
            mAdapter.notifyDataSetChanged();
            binding.lvMessages.setVisibility(View.GONE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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

    @Override
    protected void onStart() {
        super.onStart();
        broadcastReceiver = new BroadcastReceiver() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onReceive(Context context, Intent intent) {
                // receiving new message
                if (intent.getAction().equals(NOTIFY_ACTIVITY_ACTION ))
                {
                    String content = intent.getExtras().getString("content", null);
                    String[] info = content.split(": ");
                    String from = info[1];
                    String text = info[2];
                    Contact contact = contacts.stream().filter(c -> Objects.equals(c.getUsername(), from))
                            .collect(Collectors.toList()).get(0);
                    String currentTime = ChatActivity.getCurrentTime();
                    Message m = new Message(text, currentTime, false, from);
                    messageDao.insert(m);
                    contact.setLastMessage(text);
                    contact.setTime(currentTime);
                    contactDao.update(contact);
                    contacts.clear();
                    contacts.addAll(contactDao.index());
                    messages.clear();
                    messages.addAll(messageDao.get(chosenContact.getUsername()));
                    cAdapter.notifyDataSetChanged();
                    mAdapter.notifyDataSetChanged();
                }

                // receiving new chat
                if (intent.getAction().equals(NOTIFY_ACTIVITY_ACTION2)) {
                    try {
                        Thread contactsThread = new Thread(contactAPI);
                        contactsThread.start();
                        contactsThread.join();
                        contacts.clear();
                        contacts.addAll(contactDao.index());
                        cAdapter.notifyItemRangeInserted(contacts.size(), contacts.size());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        IntentFilter filter = new IntentFilter( NOTIFY_ACTIVITY_ACTION );
        filter.addAction(NOTIFY_ACTIVITY_ACTION2);
        registerReceiver(broadcastReceiver, filter);
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        unregisterReceiver(broadcastReceiver);
    }

}