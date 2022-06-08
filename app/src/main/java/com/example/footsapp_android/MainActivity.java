package com.example.footsapp_android;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.example.footsapp_android.afterLogin.ContactsActivity;
import com.example.footsapp_android.databinding.ActivityMainBinding;
import androidx.appcompat.app.AppCompatActivity;

import com.example.footsapp_android.sign.RegisterActivity;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;


    // private method for username and password validation
    private boolean validateLogin(String username, String Password) {
        // check if username is empty
        boolean validInput = true;
        if (username.isEmpty()) {
            binding.etUsername.setError("Username is required");
            binding.etUsername.requestFocus();
            validInput = false;
        }
        // check if password is empty
        if (Password.isEmpty()) {
            binding.etPassword.setError("Password is required");
            binding.etPassword.requestFocus();
            validInput = false;
        }

        // TODO implement username validation
        return validInput;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //setContentView(R.layout.activity_main);

        //TextView tvRegisterActivity = findViewById(R.id.tvRegisterActivity);
        // on click, go to RegisterActivity
        binding.tvRegisterActivity.setOnClickListener(v -> {
            // start RegisterActivity
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        });

        // set listener for Login_btn that checks if the username and password are correct
        binding.btnLogin.setOnClickListener(v -> {
            // check if username and password are correct
            String username = binding.etUsername.getText().toString();
            String password = binding.etPassword.getText().toString();
            if (validateLogin(username, password)) {
                // move to contacts activity
                Intent intent = new Intent(this, ContactsActivity.class);
                startActivity(intent);
            }

            /*if (binding.etUsername.getText().toString().equals("admin") && binding.etPassword.getText().toString().equals("admin")) {
                // start HomeActivity
                Intent intent = new Intent(this, HomeActivity.class);
                startActivity(intent);
            }*/
        });



    }

}