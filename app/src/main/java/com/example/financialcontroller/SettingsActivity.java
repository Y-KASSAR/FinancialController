package com.example.financialcontroller;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    private LinearLayout btnExportCsv, btnExportPdf;
    private Switch switchNotifications, switchBudgetAlerts;
    private Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // 1. Initialize Views
        btnExportCsv = findViewById(R.id.btn_export_csv);
        btnExportPdf = findViewById(R.id.btn_export_pdf);
        switchNotifications = findViewById(R.id.switch_notifications);
        switchBudgetAlerts = findViewById(R.id.switch_budget_alerts);
        btnLogout = findViewById(R.id.btn_logout);

        // 2. Handle Export Buttons
        btnExportCsv.setOnClickListener(v -> {
            Toast.makeText(this, "Exporting CSV... (Feature coming soon)", Toast.LENGTH_SHORT).show();
        });

        btnExportPdf.setOnClickListener(v -> {
            Toast.makeText(this, "Exporting PDF... (Feature coming soon)", Toast.LENGTH_SHORT).show();
        });

        // 3. Handle Switches
        switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            String status = isChecked ? "Enabled" : "Disabled";
            Toast.makeText(this, "Bill Reminders " + status, Toast.LENGTH_SHORT).show();
        });

        switchBudgetAlerts.setOnCheckedChangeListener((buttonView, isChecked) -> {
            String status = isChecked ? "Enabled" : "Disabled";
            Toast.makeText(this, "Budget Alerts " + status, Toast.LENGTH_SHORT).show();
        });

        // 4. Handle Logout
        btnLogout.setOnClickListener(v -> {
            Toast.makeText(this, "Logged Out", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}