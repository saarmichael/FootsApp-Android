package com.example.footsapp_android.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.footsapp_android.AppDB;
import com.example.footsapp_android.ContactDao;
import com.example.footsapp_android.adapters.ContactsListAdapter;
import com.example.footsapp_android.databinding.ActivityChatsBinding;
import com.example.footsapp_android.entities.Contact;
import com.example.footsapp_android.web.ContactAPI;
import com.example.footsapp_android.web.LoginAPI;

import java.util.ArrayList;
import java.util.List;

public class ChatsActivity extends AppCompatActivity implements ContactsListAdapter.OnContactClickListener {

    private AppDB db;
    private ContactDao contactDao;
    private List<Contact> contacts;
    private ContactsListAdapter adapter;
    // binding
    private ActivityChatsBinding binding;

    private void loadProfileImage() {
        byte[] bytes = Base64.decode(
                                        getApplicationContext().
                                        getSharedPreferences("user", MODE_PRIVATE).
                                        getString("image", null), Base64.DEFAULT
                                    );
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        binding.userImage.setImageBitmap(bitmap);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = AppDB.getDatabase(this);
        contactDao = db.contactDao();
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

        loadProfileImage();


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