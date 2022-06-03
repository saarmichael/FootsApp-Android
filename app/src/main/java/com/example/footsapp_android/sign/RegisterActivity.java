package com.example.footsapp_android.sign;

import androidx.appcompat.app.AppCompatActivity;

import com.example.footsapp_android.MainActivity;
import com.example.footsapp_android.R;
import com.example.footsapp_android.afterLogin.ContactsActivity;
import com.example.footsapp_android.databinding.ActivityRegisterBinding;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;

    // private method for register validation
    private boolean validateRegister(String username, String nickname, String password, String confirmPassword) {
        // check if any of the fields are empty
        boolean validInput = true;
        if (username.isEmpty() || nickname.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            binding.etUsername.setError("Please fill all the fields");
            binding.etUsername.requestFocus();
            return false;
        }
        // check if password and confirm password are the same
        if (!password.equals(confirmPassword)) {
            binding.etConfirmPassword.setError("Passwords do not match");
            binding.etConfirmPassword.requestFocus();
            return false;
        }
        // check that password is at least 8 characters long and contains at least one number, capital letter and special character
        if (!password.matches("(?=.*[0-9])(?=.*[A-Z])(?=.*[!@#$%^&*])(?=\\S+$).{8,}")) {
            binding.etPassword.setError("Password must be at least 8 characters long and contain at least one number, capital letter and special character");
            binding.etPassword.requestFocus();
            return false;
        }
        // TODO check with the server if the username is already taken
        return validInput;
    }

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

        // set listener for Register_btn that checks if the username and password are correct
        binding.btnRegister.setOnClickListener(v -> {
            // check if username and password are correct
            String username = binding.etUsername.getText().toString();
            String nickname = binding.etNickname.getText().toString();
            String password = binding.etPassword.getText().toString();
            String confirmPassword = binding.etConfirmPassword.getText().toString();
            if (validateRegister(username, nickname, password, confirmPassword)) {
                // TODO register the user
                // move to contacts activity
                Intent intent = new Intent(this, ContactsActivity.class);
                startActivity(intent);
            }
        });

    }
}