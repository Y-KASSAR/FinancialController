package com.example.financialcontroller.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "transactions")
public class TransactionEntity {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public double amount;
    public String type;
    public String category;
    public long date;
    public String note;

    // Added missing fields
    public String time;
    public boolean isRecurring;
    public String status;

    // Empty constructor required by Room
    public TransactionEntity() {
    }

    // Constructor
    public TransactionEntity(double amount, String type, String category, long date, String note, String time, boolean isRecurring, String status) {
        this.amount = amount;
        this.type = type;
        this.category = category;
        this.date = date;
        this.note = note;
        this.time = time;
        this.isRecurring = isRecurring;
        this.status = status;
    }
}