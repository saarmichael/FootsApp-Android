package com.example.footsapp_android.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.footsapp_android.AppDB;
import com.example.footsapp_android.ContactDao;
import com.example.footsapp_android.R;
import com.example.footsapp_android.entities.Contact;
import com.example.footsapp_android.web.ContactAPI;
import com.example.footsapp_android.web.LoginAPI;

public class AddChatActivity extends AppCompatActivity {

    private AppDB db;
    private ContactDao contactDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_chat);

        db = AppDB.getDatabase(this);
        contactDao = db.contactDao();

        Button btnNewContact = findViewById(R.id.btnNewContact);
        btnNewContact.setOnClickListener(view -> {
            EditText etUser = findViewById(R.id.etUsername);
            EditText etNickname = findViewById(R.id.etNickname);
            EditText etServer = findViewById(R.id.etServer);


            Contact contact = new Contact(etUser.getText().toString(),
                    etNickname.getText().toString(),
                    etServer.getText().toString(), "", ""); // find a way to generate an id number from db
            contactDao.insert(contact);

            ContactAPI contactAPI = new ContactAPI(contactDao, LoginAPI.getToken());
            contactAPI.post(contact);
            finish();
        });

        TextView btnBack = findViewById(R.id.tvChatsActivity);
        btnBack.setOnClickListener(view -> {
            finish();
        });

    }
}