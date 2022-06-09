package com.example.footsapp_android.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.footsapp_android.R;
import com.example.footsapp_android.databinding.ActivityChangeThemeBinding;

public class ChangeThemeActivity extends AppCompatActivity {

    private ActivityChangeThemeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_theme);
        binding = ActivityChangeThemeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // set listener for the switch that changes the theme
        binding.changeThemeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        });

    }
}