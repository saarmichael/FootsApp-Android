package com.example.footsapp_android;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.footsapp_android.sign.RegisterActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView tvRegisterActivity = findViewById(R.id.tvRegisterActivity);
        // on click, go to RegisterActivity
        tvRegisterActivity.setOnClickListener(v -> {
            // start RegisterActivity
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        });


    }

}