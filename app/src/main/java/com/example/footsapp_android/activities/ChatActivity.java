package com.example.footsapp_android.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.footsapp_android.AppDB;
import com.example.footsapp_android.ContactDao;
import com.example.footsapp_android.MessageDao;
import com.example.footsapp_android.R;
import com.example.footsapp_android.entities.Contact;
import com.example.footsapp_android.entities.Message;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private AppDB db;
    private Contact contact;
    private List<Message> messages = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        db = AppDB.getDatabase(this);
        ContactDao contactDao = db.contactDao();
        MessageDao messageDao = db.messageDao();

        // get the nickname from the intent extra "contact_nickname"
        Integer contactId = getIntent().getIntExtra("contact_id", -1);
        // get the contact from the db
        this.contact = contactDao.get(contactId);
        this.messages = messageDao.index();


        // change text of contact name
        TextView contactName = findViewById(R.id.contact_name);
        contactName.setText(this.contact.getNickname());
        //

        Button buttonSend = findViewById(R.id.button_send);
        buttonSend.setOnClickListener(view -> {
            EditText inputMsg = findViewById(R.id.input_msg);


            int size = messageDao.index().size() + 1;
            Message message = new Message(size, inputMsg.getText().toString(), true); // find a way to generate an id number from db
            messageDao.insert(message);
            messages.clear();
            messages.addAll(messageDao.index());
        });
    }
}