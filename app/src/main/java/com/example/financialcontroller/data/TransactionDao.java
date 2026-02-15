package com.example.financialcontroller.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import com.example.financialcontroller.data.CategorySpending;
import java.util.List;

@Dao
public interface TransactionDao {

    @Insert
    void insert(TransactionEntity transaction);

    @Delete
    void delete(TransactionEntity transaction);

    // Get all transactions sorted by newest first
    @Query("SELECT * FROM transactions ORDER BY date DESC")
    List<TransactionEntity> getAllTransactions();

    // Get total Income
    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'INCOME'")
    Double getTotalIncome();

    // Get total Expense
    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'EXPENSE'")
    Double getTotalExpense();

    // --- NEW: Search Query ---
    // Filters by Type, Category, Date Range, and Keyword (Note)
    @Query("SELECT * FROM transactions WHERE " +
            "(:type = 'All' OR type = :type) AND " +
            "(:category = 'All' OR category = :category) AND " +
            "(date BETWEEN :fromDate AND :toDate) AND " +
            "(note LIKE '%' || :keyword || '%') " +
            "ORDER BY date DESC")
    List<TransactionEntity> searchTransactions(String type, String category, long fromDate, long toDate, String keyword);
    @Query("SELECT category, SUM(amount) as total FROM transactions WHERE type = 'EXPENSE' GROUP BY category")
    List<CategorySpending> getCategorySpending();
    // Add this inside your existing TransactionDao interface
    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'EXPENSE' AND category = :category")
    Double getSpentByCategory(String category);
}