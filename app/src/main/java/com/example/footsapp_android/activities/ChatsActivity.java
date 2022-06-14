package com.example.footsapp_android.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.footsapp_android.AppDB;
import com.example.footsapp_android.ContactDao;
import com.example.footsapp_android.adapters.ContactsListAdapter;
import com.example.footsapp_android.databinding.ActivityChatsBinding;
import com.example.footsapp_android.entities.Contact;

import java.util.ArrayList;
import java.util.List;

public class ChatsActivity extends AppCompatActivity implements ContactsListAdapter.OnContactClickListener {

    private AppDB db;
    private ContactDao contactDao;
    private List<Contact> contacts;
    private ContactsListAdapter adapter;
    // binding
    private ActivityChatsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = AppDB.getDatabase(this);
        contactDao = db.contactDao();


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

        // probably doesn't work with the new adapter
        /*lvContacts.setOnItemLongClickListener((adapterView, view, i, l) -> {
            Contact contact = contacts.remove(i);
            contactDao.delete(contact);
            adapter.notifyDataSetChanged();
            return true;
        });*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        //loading(true);
        contacts.clear();
        contacts.addAll(contactDao.index());
        adapter.notifyDataSetChanged();
        //loading(false);
    }

    @Override
    public void onContactClick(int position) {
        Intent i = new Intent(this, ChatActivity.class);
        i.putExtra("contact_id", contacts.get(position).getId());
        startActivity(i);
    }

    /*private void loading(Boolean isLoading){
        binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.INVISIBLE);
    }*/
}