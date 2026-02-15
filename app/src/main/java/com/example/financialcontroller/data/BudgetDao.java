package com.example.financialcontroller.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface BudgetDao {
    @Insert
    void insert(BudgetEntity budget);

    @Update
    void update(BudgetEntity budget);

    @Delete
    void delete(BudgetEntity budget);

    @Query("SELECT * FROM budgets")
    List<BudgetEntity> getAllBudgets();

    @Query("SELECT * FROM budgets WHERE category = :category LIMIT 1")
    BudgetEntity getBudgetByCategory(String category);
}