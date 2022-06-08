package com.example.footsapp_android;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

public class AddChatActivity extends AppCompatActivity {

    private AppDB db;
    private ContactDao contactDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_chat);

        db = Room.databaseBuilder(getApplicationContext(), AppDB.class, "FooDB")
                .allowMainThreadQueries().build();
        contactDao = db.contactDao();

        Button btnNewContact = findViewById(R.id.btnNewContact);
        btnNewContact.setOnClickListener(view -> {
            EditText etUser = findViewById(R.id.etUsername);
            EditText etNickname = findViewById(R.id.etNickname);
            EditText etServer = findViewById(R.id.etServer);


            int size = contactDao.index().size() + 1;
            Contact contact = new Contact(size, etUser.getText().toString(),
                                          etNickname.getText().toString(),
                                          etServer.getText().toString()); // find a way to generate an id number from db
            contactDao.insert(contact);

            finish();
        });
    }
}