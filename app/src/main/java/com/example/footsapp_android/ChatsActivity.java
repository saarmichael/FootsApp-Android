package com.example.footsapp_android;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class ChatsActivity extends AppCompatActivity {

    private AppDB db;
    private ContactDao contactDao;
    private List<Contact> contacts;
    private ArrayAdapter<Contact> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats);

        db = Room.databaseBuilder(getApplicationContext(), AppDB.class, "FooDB")
                .allowMainThreadQueries().build();
        contactDao = db.contactDao();

        FloatingActionButton btnNew = findViewById(R.id.btnNew);
        btnNew.setOnClickListener(view -> {
            Intent i = new Intent(this, AddChatActivity.class);
            startActivity(i);
        });

        contacts = new ArrayList<>();

        ListView lvContacts = findViewById(R.id.lvContacts);
        adapter = new ArrayAdapter<Contact>(this,
                                            android.R.layout.simple_list_item_1,
                                            contacts);
        lvContacts.setAdapter(adapter);

        lvContacts.setOnItemLongClickListener((adapterView, view, i, l) -> {
            Contact contact = contacts.remove(i);
            contactDao.delete(contact);
            adapter.notifyDataSetChanged();
            return true;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        contacts.clear();
        contacts.addAll(contactDao.index());
        adapter.notifyDataSetChanged();
    }
}