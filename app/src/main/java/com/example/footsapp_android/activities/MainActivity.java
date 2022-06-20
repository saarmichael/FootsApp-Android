package com.example.footsapp_android.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.footsapp_android.databinding.ActivityMainBinding;
import com.example.footsapp_android.web.LoginAPI;
import com.google.firebase.iid.FirebaseInstanceId;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;


    // private method for username and password validation
    private boolean validateLogin(String username, String password) {
        // check if username is empty
        boolean validInput = true;
        if (username.isEmpty()) {
            binding.etUsername.setError("Username is required");
            binding.etUsername.requestFocus();
            validInput = false;
        }
        // check if password is empty
        if (password.isEmpty()) {
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

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(MainActivity.this, instanceIdResult -> {
            String deviceToken = instanceIdResult.getToken();
        });

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
                LoginAPI loginAPI = new LoginAPI(username, password);
                Thread thread = new Thread(loginAPI);
                thread.start();
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                String token = LoginAPI.getToken();
                if (token != null) {
                    // move the error message from etPassword
                    binding.etPassword.setError(null);
                    // move to contacts activity
                    Intent intent = new Intent(this, ChatsActivity.class);
                    startActivity(intent);
                } else {
                    binding.etPassword.setError("Invalid username or password");
                }
            }
        });

        binding.buttonSettings.setOnClickListener(v -> {
            // start ChangeThemeActivity
            Intent intent = new Intent(this, ChangeThemeActivity.class);
            startActivity(intent);
        });



    }

}