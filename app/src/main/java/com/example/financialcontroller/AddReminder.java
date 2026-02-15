package com.example.financialcontroller;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.financialcontroller.data.DatabaseClient;
import com.example.financialcontroller.data.ReminderEntity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddReminder extends AppCompatActivity {

    // Views
    private EditText etTitle, etAmount, etDate;
    private CheckBox cbWeekBefore, cbDayBefore, cbOnDueDate;
    private Button btnSave;

    // Calendar for Date Picker
    private final Calendar myCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reminder);

        // 1. Initialize Views (Matching your XML IDs)
        etTitle = findViewById(R.id.et_bill_title);
        etAmount = findViewById(R.id.et_bill_amount);
        etDate = findViewById(R.id.et_bill_date);

        cbWeekBefore = findViewById(R.id.cb_week_before);
        cbDayBefore = findViewById(R.id.cb_day_before);
        cbOnDueDate = findViewById(R.id.cb_on_due_date);

        btnSave = findViewById(R.id.btn_save_reminder);

        // 2. Setup Date Picker Logic
        DatePickerDialog.OnDateSetListener date = (view, year, month, day) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, month);
            myCalendar.set(Calendar.DAY_OF_MONTH, day);
            updateDateLabel();
        };

        etDate.setOnClickListener(v -> {
            new DatePickerDialog(AddReminder.this, date,
                    myCalendar.get(Calendar.YEAR),
                    myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        // 3. Handle Save Button
        btnSave.setOnClickListener(v -> saveReminder());
    }

    private void updateDateLabel() {
        String myFormat = "MM/dd/yyyy"; // Format: 12/31/2023
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        etDate.setText(sdf.format(myCalendar.getTime()));
    }

    private void saveReminder() {
        // Get Inputs
        String title = etTitle.getText().toString().trim();
        String amountStr = etAmount.getText().toString().trim();
        String dateStr = etDate.getText().toString().trim();

        // Validation
        if (title.isEmpty() || amountStr.isEmpty() || dateStr.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount = Double.parseDouble(amountStr);
        long dueDate = myCalendar.getTimeInMillis();

        // Get Checkbox values
        boolean weekBefore = cbWeekBefore.isChecked();
        boolean dayBefore = cbDayBefore.isChecked();
        boolean onDate = cbOnDueDate.isChecked();

        // Create Entity with ALL 7 arguments
        ReminderEntity reminder = new ReminderEntity(
                title,
                amount,
                dueDate,
                false,       // isPaid (default false)
                weekBefore,  // remindWeekBefore
                dayBefore,   // remindDayBefore
                onDate       // remindOnDate
        );

        // Save to Database
        DatabaseClient.getInstance(getApplicationContext())
                .getAppDatabase()
                .reminderDao()
                .insert(reminder);

        // TODO: We will add the Notification Scheduler here later!

        Toast.makeText(this, "Reminder Saved!", Toast.LENGTH_SHORT).show();
        finish(); // Close screen
    }
}