package com.example.financialcontroller;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.financialcontroller.data.BudgetEntity;
import java.util.List;

public class BudgetAdapter extends RecyclerView.Adapter<BudgetAdapter.BudgetViewHolder> {

    // Helper class to hold data
    public static class BudgetViewItem {
        public BudgetEntity budget;
        public double spent;

        public BudgetViewItem(BudgetEntity budget, double spent) {
            this.budget = budget;
            this.spent = spent;
        }
    }

    private List<BudgetViewItem> budgetList;

    public BudgetAdapter(List<BudgetViewItem> budgetList) {
        this.budgetList = budgetList;
    }

    @NonNull
    @Override
    public BudgetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_budget, parent, false);
        return new BudgetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BudgetViewHolder holder, int position) {
        BudgetViewItem item = budgetList.get(position);

        holder.tvCategory.setText(item.budget.category);

        // Calculate progress
        int progress = 0;
        if (item.budget.limitAmount > 0) {
            progress = (int) ((item.spent / item.budget.limitAmount) * 100);
        }
        holder.progressBar.setProgress(progress);
        holder.tvPercent.setText(progress + "%");

        // Format text: "$425 spent of $500"
        String status = String.format("$%.0f spent of $%.0f", item.spent, item.budget.limitAmount);
        holder.tvAmount.setText(status);

        // Change color if over budget
        if (item.spent > item.budget.limitAmount) {
            holder.tvPercent.setTextColor(0xFFFF0000); // Red
        } else {
            holder.tvPercent.setTextColor(0xFFFF9800); // Orange
        }
    }

    @Override
    public int getItemCount() {
        return budgetList != null ? budgetList.size() : 0;
    }

    static class BudgetViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategory, tvPercent, tvAmount;
        ProgressBar progressBar;

        public BudgetViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategory = itemView.findViewById(R.id.tv_budget_category);
            tvPercent = itemView.findViewById(R.id.tv_budget_percent);
            tvAmount = itemView.findViewById(R.id.tv_budget_amount);
            progressBar = itemView.findViewById(R.id.pb_budget);
        }
    }
}