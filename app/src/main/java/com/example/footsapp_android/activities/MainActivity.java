package com.example.footsapp_android.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.footsapp_android.MyApplication;
import com.example.footsapp_android.databinding.ActivityMainBinding;
import com.example.footsapp_android.web.LoginAPI;
import com.google.firebase.iid.FirebaseInstanceId;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private static String deviceToken;
    public static String CURRENT_USER = "";

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


        return validInput;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //setContentView(R.layout.activity_main);


        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(MainActivity.this, instanceIdResult -> {
            deviceToken = instanceIdResult.getToken();
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
                getApplicationContext().
                        getSharedPreferences("user", MODE_PRIVATE).
                        edit().
                        putBoolean("has_img", false).
                        apply();

                getApplicationContext().
                        getSharedPreferences("user", MODE_PRIVATE).
                        edit().
                        putString("my_user", username).
                        apply();
                loginThroughAPI(username, password);

                String token = LoginAPI.getToken();
                if (token != null) {
                    // move the error message from etPassword
                    binding.etPassword.setError(null);
                    CURRENT_USER = username;
                    // move to contacts activity
                    // check if configuration is landscape
                    if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                        // start ChatLandscapeActivity
                        Intent i = new Intent(this, ChatLandscapeActivity.class);
                        startActivity(i);
                    } else {
                        // start ChatsActivity
                        Intent i = new Intent(this, ChatsActivity.class);
                        startActivity(i);
                    }

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


    public static void loginThroughAPI(String username, String password) {
        LoginAPI loginAPI = new LoginAPI(username, password, deviceToken);
        if (!LoginAPI.validURL) {
            Toast.makeText(MyApplication.context, "couldn't connect to the server", Toast.LENGTH_LONG).show();
            return;
        }
        Thread thread = new Thread(loginAPI);
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}