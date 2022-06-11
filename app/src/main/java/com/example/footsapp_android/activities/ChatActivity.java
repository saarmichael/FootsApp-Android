package com.example.footsapp_android.activities;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.footsapp_android.R;

public class ChatActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // get the nickname from the intent extra "contact_nickname"
        String nickname = getIntent().getStringExtra("contact_nickname");

        // change text of contact name
        TextView contactName = findViewById(R.id.contact_name);
        contactName.setText(nickname);
    }
}