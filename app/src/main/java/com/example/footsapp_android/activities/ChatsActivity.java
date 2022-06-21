package com.example.footsapp_android.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.footsapp_android.AppDB;
import com.example.footsapp_android.ContactDao;
import com.example.footsapp_android.MessageDao;
import com.example.footsapp_android.R;
import com.example.footsapp_android.adapters.ContactsListAdapter;
import com.example.footsapp_android.databinding.ActivityChatsBinding;
import com.example.footsapp_android.entities.Contact;
import com.example.footsapp_android.web.ContactAPI;
import com.example.footsapp_android.web.LoginAPI;
import com.example.footsapp_android.web.MessageAPI;

import java.util.ArrayList;
import java.util.List;

public class ChatsActivity extends AppCompatActivity implements ContactsListAdapter.OnContactClickListener {

    private AppDB db;
    private ContactDao contactDao;
    private MessageDao messageDao;
    private List<Contact> contacts;
    private ContactsListAdapter adapter;
    // binding
    private ActivityChatsBinding binding;
    public static final String NOTIFY_ACTIVITY_ACTION = "notify_activity";
    private BroadcastReceiver broadcastReceiver;

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

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE)
        {
            startActivity(new Intent(this, ChatLandscapeActivity.class));
        }

        if(newConfig.orientation == Configuration.ORIENTATION_PORTRAIT)
        {
            startActivity(new Intent(this, ChatsActivity.class));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = AppDB.getDatabase(this);
        contactDao = db.contactDao();
        messageDao = db.messageDao();
        ContactAPI contactAPI = new ContactAPI(contactDao, LoginAPI.getToken());


        binding.btnNew.setOnClickListener(view -> {
            Intent i = new Intent(this, AddChatActivity.class);
            startActivity(i);
        });

        contacts = new ArrayList<>();


        adapter = new ContactsListAdapter(this, this);
        binding.lvContacts.setAdapter(adapter);
        binding.lvContacts.setLayoutManager(new LinearLayoutManager(this));
        //binding.lvContacts.setVisibility(View.VISIBLE);

        contacts = contactDao.index();
        adapter.setContacts(contacts);

        Thread contactsThread = new Thread(contactAPI);
        contactsThread.start();
        try {
            contactsThread.join();
            contacts.clear();
            contacts.addAll(contactDao.index());
            adapter.notifyItemRangeInserted(contacts.size(), contacts.size());
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

/*
    @Override
    protected void onResume() {
        super.onResume();
        messageDao.nukeTable();
        for (Contact c : contacts) {
            MessageAPI messageAPI = new MessageAPI(messageDao, contactDao, LoginAPI.getToken(), c.getUsername());
            Thread thread = new Thread(messageAPI);
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        contactDao.nukeTable();
        ContactAPI contactAPI = new ContactAPI(contactDao, LoginAPI.getToken());
        Thread contactsThread = new Thread(contactAPI);
        contactsThread.start();
        try {
            contactsThread.join();
            contacts.clear();
            contacts.addAll(contactDao.index());
            adapter.notifyDataSetChanged();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

*/

    @Override
    protected void onStart() {
        super.onStart();
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(NOTIFY_ACTIVITY_ACTION ))
                {
                    contacts.clear();
                    contacts.addAll(contactDao.index());
                    adapter.notifyDataSetChanged();
                }
            }
        };

        IntentFilter filter = new IntentFilter( NOTIFY_ACTIVITY_ACTION );
        registerReceiver(broadcastReceiver, filter);
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        unregisterReceiver(broadcastReceiver);
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        contacts.clear();
        contacts.addAll(contactDao.index());
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onContactClick(int position) {
        Intent i = new Intent(this, ChatActivity.class);
        i.putExtra("contact_id", contactDao.index().get(position).getId());
        i.putExtra("contact_username", contacts.get(position).getUsername());
        startActivity(i);
    }


    /*private void loading(Boolean isLoading){
        binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.INVISIBLE);
    }*/
}