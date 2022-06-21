package com.example.footsapp_android.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.footsapp_android.databinding.ActivityRegisterBinding;
import com.example.footsapp_android.entities.User;
import com.example.footsapp_android.web.RegisterAPI;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private String encodedImage;

    // set Listeners
    private void setListeners() {
        binding.tvLoginActivity.setOnClickListener(v -> {
            // start LoginActivity (Main)
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });

        binding.btnRegister.setOnClickListener(v -> {
            // check if username and password are correct
            String username = binding.etUsername.getText().toString();
            String nickname = binding.etNickname.getText().toString();
            String password = binding.etPassword.getText().toString();
            String confirmPassword = binding.etConfirmPassword.getText().toString();
            if (validateRegister(username, nickname, password, confirmPassword)) {
                // move to contacts activity
                // add to shared preferences the user's encodedImage
                getApplicationContext().
                        getSharedPreferences("user", MODE_PRIVATE).
                        edit().
                        putString("image", this.encodedImage).
                        apply();
                User user = new User(username, nickname, password);
                RegisterAPI registerAPI = new RegisterAPI(user);
                Thread thread = new Thread(registerAPI);
                thread.start();
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (registerAPI.isSuccess()) {
                    MainActivity.CURRENT_USER = username;
                    MainActivity.loginThroughAPI(username, password);
                    Intent intent = new Intent(this, ChatsActivity.class);
                    startActivity(intent);
                } else {
                    binding.etPassword.setError("Register Failed.");
                }
            }
        });
        // get the image from devices gallery
        binding.layoutImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pickImage.launch(intent);
        });
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private String encodeImage(Bitmap bitmap) {
        int previewWidth = 80;
        int previewHeight = bitmap.getHeight() * previewWidth / bitmap.getWidth();
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    if (result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        try {
                            InputStream inputStream = getContentResolver().openInputStream(imageUri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            // binding.imageProfile.setImageBitmap(bitmap);
                            binding.textImage.setVisibility(View.GONE);
                            binding.imageProfile.setVisibility(View.VISIBLE);
                            binding.imageProfile.setImageBitmap(bitmap);
                            this.encodedImage = encodeImage(bitmap);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

    // private method for register validation
    private boolean validateRegister(String username, String nickname, String password, String confirmPassword) {
        // check if any of the fields are empty
        boolean validInput = true;
        if (username.isEmpty() || nickname.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showToast("Please fill all the fields");
            binding.etUsername.requestFocus();
            return false;
        }
        if (this.encodedImage == null) {
            /*showToast("Please upload a profile picture");
            return false;*/
            getApplicationContext().
                    getSharedPreferences("user", MODE_PRIVATE).
                    edit().
                    putBoolean("has_img", false).
                    apply();
        } else {
            getApplicationContext().
                    getSharedPreferences("user", MODE_PRIVATE).
                    edit().
                    putBoolean("has_img", true).
                    apply();
        }

        // check if password and confirm password are the same
        if (!password.equals(confirmPassword)) {
            showToast("Passwords do not match");
            binding.etConfirmPassword.requestFocus();
            return false;
        }
        // check that password is at least 8 characters long and contains at least one number, capital letter and special character
        /*if (!password.matches("(?=.*[0-9])(?=.*[A-Z])(?=.*[!@#$%^&*])(?=\\S+$).{8,}")) {
            binding.etPassword.setError("Password must be at least 8 characters long and contain at least one number, capital letter and special character");
            binding.etPassword.requestFocus();
            return false;
        }*/
        // TODO check with the server if the username is already taken
        return validInput;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListeners();
        //setContentView(R.layout.activity_register);
        //TextView tvRegisterActivity = findViewById(R.id.tvLoginActivity);
        // on click, go to LoginActivity


        // set listener for Register_btn that checks if the username and password are correct


    }
}