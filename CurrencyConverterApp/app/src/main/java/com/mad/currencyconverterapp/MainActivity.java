package com.mad.currencyconverterapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class MainActivity extends AppCompatActivity {

    private EditText etInputAmount;
    private Spinner spinnerFrom, spinnerTo;
    private TextView tvConvertedResult;
    private final String[] currencies = {"INR", "USD", "JPY", "EUR"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        applyTheme();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etInputAmount = findViewById(R.id.etInputAmount);
        spinnerFrom = findViewById(R.id.spinnerFrom);
        spinnerTo = findViewById(R.id.spinnerTo);
        tvConvertedResult = findViewById(R.id.tvConvertedResult);
        Button btnConvert = findViewById(R.id.btnConvert);
        Button btnSettings = findViewById(R.id.btnSettings);

        // Fix: Use simple_spinner_item to prevent text clipping
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, currencies);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFrom.setAdapter(adapter);
        spinnerTo.setAdapter(adapter);

        btnConvert.setOnClickListener(v -> calculateConversion());
        btnSettings.setOnClickListener(v -> startActivity(new Intent(this, SettingsActivity.class)));
    }

    private void applyTheme() {
        SharedPreferences sharedPref = getSharedPreferences("ThemePrefs", MODE_PRIVATE);
        boolean isDark = sharedPref.getBoolean("isDarkMode", false);
        if (isDark) AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        else AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
    }

    private void calculateConversion() {
        String inputStr = etInputAmount.getText().toString();
        if (inputStr.isEmpty()) return;

        double amount = Double.parseDouble(inputStr);
        String from = spinnerFrom.getSelectedItem().toString();
        String to = spinnerTo.getSelectedItem().toString();

        double toUSD;
        switch (from) {
            case "INR": toUSD = amount / 83.0; break;
            case "JPY": toUSD = amount / 151.0; break;
            case "EUR": toUSD = amount / 0.92; break;
            default: toUSD = amount;
        }

        double finalResult;
        switch (to) {
            case "INR": finalResult = toUSD * 83.0; break;
            case "JPY": finalResult = toUSD * 151.0; break;
            case "EUR": finalResult = toUSD * 0.92; break;
            default: finalResult = toUSD;
        }

        tvConvertedResult.setText(String.format("%.2f %s = %.2f %s", amount, from, finalResult, to));
    }
}