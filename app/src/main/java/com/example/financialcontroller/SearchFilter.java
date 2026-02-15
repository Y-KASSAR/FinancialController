package com.example.financialcontroller;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class SearchFilter extends AppCompatActivity {

    // Views
    private EditText etKeyword, etDateFrom, etDateTo;
    private CheckBox chipAll, chipIncome, chipExpense;
    private Spinner spinnerCategory;
    private Button btnApply;

    // Data
    private final Calendar calendarFrom = Calendar.getInstance();
    private final Calendar calendarTo = Calendar.getInstance();
    private String selectedType = "All";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_filter);

        // 1. Init Views
        etKeyword = findViewById(R.id.et_search_keyword);
        etDateFrom = findViewById(R.id.et_date_from);
        etDateTo = findViewById(R.id.et_date_to);

        chipAll = findViewById(R.id.chip_all);
        chipIncome = findViewById(R.id.chip_income);
        chipExpense = findViewById(R.id.chip_expense);

        spinnerCategory = findViewById(R.id.spinner_filter_category);
        btnApply = findViewById(R.id.btn_apply_filter);

        // 2. Setup Date Pickers
        setupDatePickers();

        // 3. Setup Chips (Mutually Exclusive)
        setupChips();

        // 4. Setup Category Spinner
        setupSpinner();

        // 5. Apply Button
        btnApply.setOnClickListener(v -> applyFilters());
    }

    private void setupDatePickers() {
        // Default: From = 1 month ago, To = Today
        calendarFrom.add(Calendar.MONTH, -1);
        updateDateLabel(etDateFrom, calendarFrom);
        updateDateLabel(etDateTo, calendarTo);

        etDateFrom.setOnClickListener(v -> showDatePicker(etDateFrom, calendarFrom));
        etDateTo.setOnClickListener(v -> showDatePicker(etDateTo, calendarTo));
    }

    private void showDatePicker(EditText field, Calendar calendar) {
        new DatePickerDialog(this, (view, year, month, day) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, day);
            updateDateLabel(field, calendar);
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void updateDateLabel(EditText field, Calendar calendar) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        field.setText(sdf.format(calendar.getTime()));
    }

    private void setupChips() {
        chipAll.setOnClickListener(v -> setType("All"));
        chipIncome.setOnClickListener(v -> setType("INCOME"));
        chipExpense.setOnClickListener(v -> setType("EXPENSE"));
    }

    private void setType(String type) {
        selectedType = type;
        chipAll.setChecked(type.equals("All"));
        chipIncome.setChecked(type.equals("INCOME"));
        chipExpense.setChecked(type.equals("EXPENSE"));
    }

    private void setupSpinner() {
        List<String> categories = new ArrayList<>();
        categories.add("All");
        categories.add("Food");
        categories.add("Rent");
        categories.add("Salary");
        categories.add("Entertainment");
        categories.add("Transport");
        categories.add("Health");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);
    }

    private void applyFilters() {
        String keyword = etKeyword.getText().toString().trim();
        String category = spinnerCategory.getSelectedItem().toString();
        long fromDate = calendarFrom.getTimeInMillis();
        long toDate = calendarTo.getTimeInMillis();

        // Launch Results
        Intent intent = new Intent(this, SearchResultsActivity.class);
        intent.putExtra("KEYWORD", keyword);
        intent.putExtra("TYPE", selectedType);
        intent.putExtra("CATEGORY", category);
        intent.putExtra("FROM", fromDate);
        intent.putExtra("TO", toDate);
        startActivity(intent);
    }
}