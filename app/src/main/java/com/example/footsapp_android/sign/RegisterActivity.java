package com.example.footsapp_android.sign;

import androidx.appcompat.app.AppCompatActivity;

import com.example.footsapp_android.MainActivity;
import com.example.footsapp_android.R;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        TextView tvRegisterActivity = findViewById(R.id.tvLoginActivity);
        // on click, go to LoginActivity
        tvRegisterActivity.setOnClickListener(v -> {
            // start LoginActivity (Main)
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });
    }
}