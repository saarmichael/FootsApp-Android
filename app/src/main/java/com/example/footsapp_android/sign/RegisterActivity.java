package com.example.footsapp_android.sign;

import androidx.appcompat.app.AppCompatActivity;

import com.example.footsapp_android.MainActivity;
import com.example.footsapp_android.R;
import com.example.footsapp_android.databinding.ActivityRegisterBinding;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //setContentView(R.layout.activity_register);
        //TextView tvRegisterActivity = findViewById(R.id.tvLoginActivity);
        // on click, go to LoginActivity
        binding.tvLoginActivity.setOnClickListener(v -> {
            // start LoginActivity (Main)
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });
    }
}