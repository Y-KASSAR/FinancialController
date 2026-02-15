package com.example.financialcontroller;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.financialcontroller.data.BudgetEntity;
import com.example.financialcontroller.data.DatabaseClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.widget.Button;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class Budgets extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FloatingActionButton fabAdd;
    private BudgetAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budgets);

        // 1. Setup RecyclerView
        recyclerView = findViewById(R.id.rv_budgets);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 2. Setup FAB
        fabAdd = findViewById(R.id.fab_set_budget);
        fabAdd.setOnClickListener(v -> showAddBudgetDialog());

        // 3. Setup Bottom Navigation (MUST BE HERE)
        setupBottomNavigation();

        // 4. Load Data
        loadBudgets();
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_budget); // Highlight "Budgets"

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(getApplicationContext(), DashboardActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (id == R.id.nav_analysis) {
                startActivity(new Intent(getApplicationContext(), AnalysisActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (id == R.id.nav_budget) {
                return true; // Already here
            }
            return false;
        });
    }

    private void loadBudgets() {
        Executors.newSingleThreadExecutor().execute(() -> {
            // 1. Get all budgets
            List<BudgetEntity> budgets = DatabaseClient.getInstance(getApplicationContext())
                    .getAppDatabase()
                    .budgetDao()
                    .getAllBudgets();

            List<BudgetAdapter.BudgetViewItem> viewItems = new ArrayList<>();

            for (BudgetEntity b : budgets) {
                // FIX: Use Double wrapper to catch NULLs
                Double spentObj = DatabaseClient.getInstance(getApplicationContext())
                        .getAppDatabase()
                        .transactionDao()
                        .getSpentByCategory(b.category);

                // If null (no expenses yet), default to 0.0
                double spent = (spentObj == null) ? 0.0 : spentObj;

                viewItems.add(new BudgetAdapter.BudgetViewItem(b, spent));
            }

            // 2. Update UI
            new Handler(Looper.getMainLooper()).post(() -> {
                adapter = new BudgetAdapter(viewItems);
                recyclerView.setAdapter(adapter);
            });
        });
    }

    private void showAddBudgetDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View v = getLayoutInflater().inflate(R.layout.dialog_add_budget, null);

        // 1. Init views
        Spinner spinner = v.findViewById(R.id.spinner_budget_category);
        EditText etLimit = v.findViewById(R.id.et_budget_limit);
        Button btnSave = v.findViewById(R.id.btn_save_budget);
        Button btnCancel = v.findViewById(R.id.btn_cancel_budget);

        // 2. Setup Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.categories_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        // 3. Create Dialog
        AlertDialog dialog = builder.setView(v).create();

        // 4. Button Logic
        btnSave.setOnClickListener(view -> {
            String category = spinner.getSelectedItem().toString();
            String limitStr = etLimit.getText().toString();

            if (!limitStr.isEmpty()) {
                saveBudget(category, Double.parseDouble(limitStr));
                dialog.dismiss();
            } else {
                etLimit.setError("Required");
            }
        });

        btnCancel.setOnClickListener(view -> dialog.dismiss());

        dialog.show();
    }
    private void saveBudget(String category, double limit) {
        Executors.newSingleThreadExecutor().execute(() -> {
            // Check if budget exists
            BudgetEntity existing = DatabaseClient.getInstance(getApplicationContext())
                    .getAppDatabase()
                    .budgetDao()
                    .getBudgetByCategory(category);

            if (existing != null) {
                existing.limitAmount = limit;
                DatabaseClient.getInstance(getApplicationContext())
                        .getAppDatabase()
                        .budgetDao()
                        .update(existing);
            } else {
                BudgetEntity newBudget = new BudgetEntity(category, limit);
                DatabaseClient.getInstance(getApplicationContext())
                        .getAppDatabase()
                        .budgetDao()
                        .insert(newBudget);
            }

            // Refresh List
            loadBudgets();
        });
    }
}