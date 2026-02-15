package com.example.financialcontroller.data;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {
        TransactionEntity.class,
        BudgetEntity.class,
        CategoryEntity.class,
        ReminderEntity.class
}, version = 3, exportSchema = false) // <--- CHANGE TO 3
public abstract class AppDatabase extends RoomDatabase {

    public abstract TransactionDao transactionDao();
    public abstract BudgetDao budgetDao();
    public abstract CategoryDao categoryDao();
    public abstract ReminderDao reminderDao();
}