package com.example.financialcontroller;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;

import com.example.financialcontroller.data.DatabaseClient;
import com.example.financialcontroller.data.TransactionEntity;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.Executors;

public class AddTransactionActivity extends AppCompatActivity {

    private EditText etAmount, etDate, etTime, etNote;
    private Spinner spinnerCategory;
    private MaterialButton btnIncome, btnExpense, btnSave;
    private SwitchCompat switchRecurring;

    private String selectedType = "EXPENSE"; // Default
    private Calendar calendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);

        // 1. Init Views
        etAmount = findViewById(R.id.et_amount);
        etDate = findViewById(R.id.et_date);
        etTime = findViewById(R.id.et_time);
        etNote = findViewById(R.id.et_note);
        spinnerCategory = findViewById(R.id.spinner_category);
        btnIncome = findViewById(R.id.btn_type_income);
        btnExpense = findViewById(R.id.btn_type_expense);
        btnSave = findViewById(R.id.btn_save_transaction);
        switchRecurring = findViewById(R.id.switch_recurring);

        // 2. Setup Category Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.categories_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);

        // 3. Setup Date/Time Pickers
        updateDateLabel();
        updateTimeLabel();

        etDate.setOnClickListener(v -> showDatePicker());
        etTime.setOnClickListener(v -> showTimePicker());

        // 4. Type Selection Logic
        btnIncome.setOnClickListener(v -> setTransactionType("INCOME"));
        btnExpense.setOnClickListener(v -> setTransactionType("EXPENSE"));

        // 5. Save Button Logic
        btnSave.setOnClickListener(v -> saveTransaction());
    }

    private void setTransactionType(String type) {
        selectedType = type;
        if ("INCOME".equals(type)) {
            btnIncome.setBackgroundColor(ContextCompat.getColor(this, R.color.income_green));
            btnExpense.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent));
        } else {
            btnExpense.setBackgroundColor(ContextCompat.getColor(this, R.color.expense_red));
            btnIncome.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent));
        }
    }

    private void showDatePicker() {
        // FIX: Check if activity is dying before showing dialog
        if (isFinishing() || isDestroyed()) return;

        new DatePickerDialog(this, (view, year, month, day) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, day);
            updateDateLabel();
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void showTimePicker() {
        // FIX: Check if activity is dying before showing dialog
        if (isFinishing() || isDestroyed()) return;

        new TimePickerDialog(this, (view, hour, minute) -> {
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
            updateTimeLabel();
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
    }

    private void updateDateLabel() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        etDate.setText(sdf.format(calendar.getTime()));
    }

    private void updateTimeLabel() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        etTime.setText(sdf.format(calendar.getTime()));
    }

    private void saveTransaction() {
        String amountStr = etAmount.getText().toString();
        String note = etNote.getText().toString();
        String category = spinnerCategory.getSelectedItem().toString();

        if (amountStr.isEmpty()) {
            etAmount.setError("Amount required");
            return;
        }

        // FIX: Disable button immediately to prevent double-clicks or interactions during save
        btnSave.setEnabled(false);

        double amount = Double.parseDouble(amountStr);

        TransactionEntity transaction = new TransactionEntity();
        transaction.amount = amount;
        transaction.type = selectedType;
        transaction.category = category;
        transaction.date = calendar.getTimeInMillis();
        transaction.time = etTime.getText().toString();
        transaction.note = note;
        transaction.isRecurring = switchRecurring.isChecked();
        transaction.status = "SETTLED";

        Executors.newSingleThreadExecutor().execute(() -> {
            // 1. Perform DB Operation
            DatabaseClient.getInstance(getApplicationContext())
                    .getAppDatabase()
                    .transactionDao()
                    .insert(transaction);

            // 2. Notify UI on Main Thread
            new Handler(Looper.getMainLooper()).post(() -> {
                // FIX: Check if activity is still valid before finishing
                if (!isFinishing() && !isDestroyed()) {
                    Toast.makeText(AddTransactionActivity.this, "Saved!", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        });
    }
}