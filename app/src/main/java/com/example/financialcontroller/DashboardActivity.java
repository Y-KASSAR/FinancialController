package com.example.financialcontroller;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.financialcontroller.data.DatabaseClient;
import com.example.financialcontroller.data.TransactionEntity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class DashboardActivity extends AppCompatActivity {

    // Views
    private TextView tvTotalBalance, tvIncome, tvExpense;
    private RecyclerView rvRecentTransactions;
    private FloatingActionButton fabAdd;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // 1. Initialize Views
        tvTotalBalance = findViewById(R.id.tvTotalBalance);
        tvIncome = findViewById(R.id.tvIncome);
        tvExpense = findViewById(R.id.tvExpense);
        rvRecentTransactions = findViewById(R.id.rvRecentTransactions);
        fabAdd = findViewById(R.id.fabAdd);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // 2. Setup RecyclerView
        rvRecentTransactions.setLayoutManager(new LinearLayoutManager(this));

        // 3. Setup Add Button
        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, AddTransactionActivity.class);
            startActivity(intent);
        });

        // 4. Setup Bottom Navigation
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                return true;
            } else if (id == R.id.nav_analysis) {
                startActivity(new Intent(this, AnalysisActivity.class));
                return true;
            } else if (id == R.id.nav_budget) {
                // startActivity(new Intent(this, BudgetsActivity.class));
                return true;
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            }
            return false;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh data every time the screen appears
        loadData();
    }

    private void loadData() {
        // FIX: Run database operations in a background thread
        new Thread(() -> {
            // 1. Get Data from DB
            List<TransactionEntity> transactionList = DatabaseClient.getInstance(getApplicationContext())
                    .getAppDatabase()
                    .transactionDao()
                    .getAllTransactions();

            // 2. Calculate Totals
            double totalIncome = 0;
            double totalExpense = 0;

            for (TransactionEntity t : transactionList) {
                if ("INCOME".equals(t.type)) {
                    totalIncome += t.amount;
                } else {
                    totalExpense += t.amount;
                }
            }
            double balance = totalIncome - totalExpense;

            // Final variables to pass to UI thread
            double finalTotalIncome = totalIncome;
            double finalTotalExpense = totalExpense;
            double finalBalance = balance;

            // 3. Update UI on Main Thread
            runOnUiThread(() -> {
                tvTotalBalance.setText(String.format("$%.2f", finalBalance));
                tvIncome.setText(String.format("$%.2f", finalTotalIncome));
                tvExpense.setText(String.format("$%.2f", finalTotalExpense));

                // 4. Update List
                // Ensure your Adapter constructor matches (Context, List)
                TransactionAdapter adapter = new TransactionAdapter(DashboardActivity.this, transactionList);
                rvRecentTransactions.setAdapter(adapter);
            });
        }).start();
    }
}