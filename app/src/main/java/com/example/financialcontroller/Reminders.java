package com.example.financialcontroller;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.financialcontroller.data.DatabaseClient;
import com.example.financialcontroller.data.ReminderEntity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class Reminders extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ReminderAdapter adapter;
    private List<ReminderEntity> reminderList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminders);

        // 1. Setup RecyclerView
        recyclerView = findViewById(R.id.rv_reminders);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 2. Setup FAB (Add Button)
        FloatingActionButton fab = findViewById(R.id.fab_add_reminder);
        fab.setOnClickListener(v -> {
            startActivity(new Intent(Reminders.this, AddReminder.class));
        });

        // 3. Load Data
        loadReminders();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh the list when returning from the "Add" screen
        loadReminders();
    }

    private void loadReminders() {
        // Run database query in background thread
        new Thread(() -> {
            List<ReminderEntity> reminders = DatabaseClient.getInstance(getApplicationContext())
                    .getAppDatabase()
                    .reminderDao()
                    .getAllReminders();

            // Update UI on Main Thread
            runOnUiThread(() -> {
                reminderList = reminders;
                adapter = new ReminderAdapter(reminderList);
                recyclerView.setAdapter(adapter);
            });
        }).start();
    }
}