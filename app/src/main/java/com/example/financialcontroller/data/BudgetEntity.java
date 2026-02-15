package com.example.financialcontroller.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "budgets") // This MUST match the name in your @Query
public class BudgetEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String category; // This MUST match the name in your @Query
    public double limitAmount;

    public BudgetEntity() {} // Room needs an empty constructor

    public BudgetEntity(String category, double limitAmount) {
        this.category = category;
        this.limitAmount = limitAmount;
    }
}