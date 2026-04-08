package com.mad.currencyconverterapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Load the preference first
        SharedPreferences sharedPref = getSharedPreferences("ThemePrefs", MODE_PRIVATE);
        boolean isDark = sharedPref.getBoolean("isDarkMode", false);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // This manages the title bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Settings");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        SwitchMaterial switchDarkMode = findViewById(R.id.switchDarkMode);
        switchDarkMode.setChecked(isDark);

        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean("isDarkMode", isChecked);
            editor.apply();

            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish(); // Returns to MainActivity
        return true;
    }
}