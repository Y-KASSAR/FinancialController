package com.example.financialcontroller.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "reminders")
public class ReminderEntity {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String title;
    public double amount;
    public long dueDate;
    public boolean isPaid;

    // New fields to store checkbox choices
    public boolean remindWeekBefore;
    public boolean remindDayBefore;
    public boolean remindOnDate;

    public ReminderEntity(String title, double amount, long dueDate, boolean isPaid,
                          boolean remindWeekBefore, boolean remindDayBefore, boolean remindOnDate) {
        this.title = title;
        this.amount = amount;
        this.dueDate = dueDate;
        this.isPaid = isPaid;
        this.remindWeekBefore = remindWeekBefore;
        this.remindDayBefore = remindDayBefore;
        this.remindOnDate = remindOnDate;
    }
}