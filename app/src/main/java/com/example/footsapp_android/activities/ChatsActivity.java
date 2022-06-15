package com.example.footsapp_android.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.footsapp_android.AppDB;
import com.example.footsapp_android.ContactDao;
import com.example.footsapp_android.R;
import com.example.footsapp_android.adapters.ContactsListAdapter;
import com.example.footsapp_android.entities.Contact;
import com.example.footsapp_android.web.ContactAPI;
import com.example.footsapp_android.web.LoginAPI;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class ChatsActivity extends AppCompatActivity implements ContactsListAdapter.OnContactClickListener {

    private AppDB db;
    private ContactDao contactDao;
    private List<Contact> contacts;
    private ContactsListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats);

        db = AppDB.getDatabase(this);
        contactDao = db.contactDao();
        ContactAPI contactAPI = new ContactAPI(contactDao, LoginAPI.getToken());

        FloatingActionButton btnNew = findViewById(R.id.btnNew);
        btnNew.setOnClickListener(view -> {
            Intent i = new Intent(this, AddChatActivity.class);
            startActivity(i);
        });

        contacts = new ArrayList<>();

        RecyclerView lvContacts = findViewById(R.id.lvContacts);
        adapter = new ContactsListAdapter(this, this);
        lvContacts.setAdapter(adapter);
        lvContacts.setLayoutManager(new LinearLayoutManager(this));

        contacts = contactDao.index();
        adapter.setContacts(contacts);

        Thread thread = new Thread(contactAPI);
        thread.start();
        try {
            thread.join();
            contacts.clear();
            contacts.addAll(contactDao.index());
            adapter.notifyDataSetChanged();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        contacts.clear();
        contacts.addAll(contactDao.index());
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onContactClick(int position) {
        Intent i = new Intent(this, ChatActivity.class);
        i.putExtra("contact_id", contacts.get(position).getId());
        startActivity(i);
    }
}