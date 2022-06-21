package com.example.footsapp_android.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.footsapp_android.MyApplication;
import com.example.footsapp_android.R;
import com.example.footsapp_android.databinding.ActivityChangeThemeBinding;

public class ChangeThemeActivity extends AppCompatActivity {

    private ActivityChangeThemeBinding binding;
    private static Boolean dark = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_theme);
        binding = ActivityChangeThemeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.changeThemeSwitch.setChecked(dark);

        // set listener for the switch that changes the theme
        binding.changeThemeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                dark = true;
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                dark = false;
            }
        });

        binding.btnServerUpdate.setOnClickListener(v -> {
            // set R.string.server_url to the server url
            MyApplication.changeUrl(binding.serverInput.getText().toString());
            // go back
            finish();
        });

    }

}