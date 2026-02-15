package com.example.financialcontroller;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.financialcontroller.data.DatabaseClient;
import com.example.financialcontroller.data.TransactionEntity;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

public class AnalysisActivity extends AppCompatActivity {

    private PieChart pieChart;
    private BarChart barChart;
    private BottomNavigationView bottomNavigationView;
    private Button btnWeek, btnMonth, btnYear;

    // Filter State
    private enum TimeFilter { WEEK, MONTH, YEAR }
    private TimeFilter currentFilter = TimeFilter.MONTH; // Default

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analysis);

        // 1. Init Views
        pieChart = findViewById(R.id.chart_pie);
        barChart = findViewById(R.id.chart_bar);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        btnWeek = findViewById(R.id.btn_tab_week);
        btnMonth = findViewById(R.id.btn_tab_month);
        btnYear = findViewById(R.id.btn_tab_year);

        // 2. Setup Navigation
        setupBottomNav();

        // 3. Load Initial Data (Month)
        loadCharts();

        // 4. Setup Tabs Logic
        btnWeek.setOnClickListener(v -> {
            currentFilter = TimeFilter.WEEK;
            updateTabVisuals(btnWeek);
            loadCharts();
        });

        btnMonth.setOnClickListener(v -> {
            currentFilter = TimeFilter.MONTH;
            updateTabVisuals(btnMonth);
            loadCharts();
        });

        btnYear.setOnClickListener(v -> {
            currentFilter = TimeFilter.YEAR;
            updateTabVisuals(btnYear);
            loadCharts();
        });
    }

    private void loadCharts() {
        // Move DB work to background thread to prevent Main Thread crash
        Executors.newSingleThreadExecutor().execute(() -> {

            // 1. Fetch Data (Background)
            List<TransactionEntity> allTransactions = DatabaseClient.getInstance(getApplicationContext())
                    .getAppDatabase()
                    .transactionDao()
                    .getAllTransactions();

            // 2. Process Data (Background)
            List<TransactionEntity> filteredList = new ArrayList<>();
            long currentTime = System.currentTimeMillis();
            long cutoffTime = 0;
            long oneDay = 24 * 60 * 60 * 1000L;

            // Determine cutoff based on current filter
            if (currentFilter == TimeFilter.WEEK) {
                cutoffTime = currentTime - (7 * oneDay);
            } else if (currentFilter == TimeFilter.MONTH) {
                cutoffTime = currentTime - (30 * oneDay);
            } else if (currentFilter == TimeFilter.YEAR) {
                cutoffTime = currentTime - (365 * oneDay);
            }

            // Filter loop
            for (TransactionEntity t : allTransactions) {
                if (t.date >= cutoffTime) {
                    filteredList.add(t);
                }
            }

            // Calculate totals for Pie/Bar charts
            Map<String, Float> categoryMap = new HashMap<>();
            float totalIncome = 0;
            float totalExpense = 0;

            for (TransactionEntity t : filteredList) {
                if ("EXPENSE".equals(t.type)) {
                    float current = categoryMap.getOrDefault(t.category, 0f);
                    categoryMap.put(t.category, current + (float) t.amount);
                    totalExpense += (float) t.amount;
                } else if ("INCOME".equals(t.type)) {
                    totalIncome += (float) t.amount;
                }
            }

            // Prepare Pie Entries
            ArrayList<PieEntry> pieEntries = new ArrayList<>();
            for (Map.Entry<String, Float> entry : categoryMap.entrySet()) {
                pieEntries.add(new PieEntry(entry.getValue(), entry.getKey()));
            }

            // Prepare Bar Entries
            ArrayList<BarEntry> barEntries = new ArrayList<>();
            barEntries.add(new BarEntry(0f, totalIncome));
            barEntries.add(new BarEntry(1f, totalExpense));

            // 3. Update UI (Main Thread)
            new Handler(Looper.getMainLooper()).post(() -> {
                // --- Setup Pie Chart ---
                PieDataSet pieDataSet = new PieDataSet(pieEntries, "");
                pieDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
                pieDataSet.setValueTextSize(14f);
                pieDataSet.setValueTextColor(Color.WHITE);

                PieData pieData = new PieData(pieDataSet);
                pieChart.setData(pieData);
                pieChart.getDescription().setEnabled(false);
                pieChart.setCenterText("Expenses");
                pieChart.setCenterTextSize(16f);
                pieChart.animateY(800);
                pieChart.invalidate();

                // --- Setup Bar Chart ---
                BarDataSet barDataSet = new BarDataSet(barEntries, "Income vs Expense");
                barDataSet.setColors(
                        ContextCompat.getColor(AnalysisActivity.this, R.color.income_green),
                        ContextCompat.getColor(AnalysisActivity.this, R.color.expense_red)
                );
                barDataSet.setValueTextSize(14f);

                BarData barData = new BarData(barDataSet);
                barChart.setData(barData);
                barChart.getDescription().setEnabled(false);

                // X-Axis Labels
                String[] labels = {"Income", "Expense"};
                XAxis xAxis = barChart.getXAxis();
                xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                xAxis.setGranularity(1f);
                xAxis.setDrawGridLines(false);

                barChart.animateY(800);
                barChart.invalidate();
            });
        });
    }

    private void setupBottomNav() {
        bottomNavigationView.setSelectedItemId(R.id.nav_analysis);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                // Ensure this matches your Dashboard class name (Dashboard.class or DashboardActivity.class)
                startActivity(new Intent(this, DashboardActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (id == R.id.nav_analysis) {
                return true;
            } else if (id == R.id.nav_budget) {
                // <--- THIS WAS MISSING
                startActivity(new Intent(this, Budgets.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (id == R.id.nav_profile) { // Assuming you have a Settings/Profile activity
                // startActivity(new Intent(this, Settings.class));
                return true;
            }
            return false;
        });
    }

    private void updateTabVisuals(Button selected) {
        // Reset all to transparent
        btnWeek.setBackgroundResource(android.R.color.transparent);
        btnMonth.setBackgroundResource(android.R.color.transparent);
        btnYear.setBackgroundResource(android.R.color.transparent);

        btnWeek.setTextColor(Color.parseColor("#757575"));
        btnMonth.setTextColor(Color.parseColor("#757575"));
        btnYear.setTextColor(Color.parseColor("#757575"));

        // Highlight selected
        selected.setBackgroundResource(R.drawable.bg_tab_selected_white);
        selected.setTextColor(Color.parseColor("#212121"));
    }
}