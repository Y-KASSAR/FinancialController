package com.example.financialcontroller;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import java.util.concurrent.TimeUnit;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. Initialize Views
        // Note: Ensure these IDs match your activity_main.xml
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);

        // 2. Handle Login Click
        btnLogin.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (validateLogin(username, password)) {
                // Login Success
                Toast.makeText(MainActivity.this, "Welcome back!", Toast.LENGTH_SHORT).show();

                // Navigate to Dashboard
                Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
                startActivity(intent);
                finish(); // Prevent going back to login screen
            }
        });
        // Schedule the Bill Reminder Worker to run once every 24 hours
        PeriodicWorkRequest reminderRequest = new PeriodicWorkRequest.Builder(BillReminderWorker.class, 24, TimeUnit.HOURS)
                .build();

        WorkManager.getInstance(this).enqueue(reminderRequest);
    }

    private boolean validateLogin(String username, String password) {
        if (username.isEmpty()) {
            etUsername.setError("Username is required");
            return false;
        }
        if (password.isEmpty()) {
            etPassword.setError("Password is required");
            return false;
        }
        // For a final project, you can add a hardcoded check like:
        // if (username.equals("admin") && password.equals("1234")) return true;

        return true; // Allow any input for now
    }
}