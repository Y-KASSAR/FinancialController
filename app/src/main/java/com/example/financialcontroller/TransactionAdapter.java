package com.example.financialcontroller;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.financialcontroller.data.TransactionEntity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {

    private Context context;
    private List<TransactionEntity> transactionList;

    public TransactionAdapter(Context context, List<TransactionEntity> transactionList) {
        this.context = context;
        this.transactionList = transactionList;
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_transaction, parent, false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        TransactionEntity transaction = transactionList.get(position);

        // 1. Set Category & Note
        holder.tvCategory.setText(transaction.category);

        // 2. Format Date (e.g., "Today, 10:30 AM")
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, hh:mm a", Locale.getDefault());
        holder.tvDate.setText(sdf.format(new Date(transaction.date)));

        // 3. Set Amount & Color
        if (transaction.type.equals("INCOME")) {
            holder.tvAmount.setText("+ $" + String.format("%.2f", transaction.amount));
            holder.tvAmount.setTextColor(ContextCompat.getColor(context, R.color.income_green));
        } else {
            holder.tvAmount.setText("- $" + String.format("%.2f", transaction.amount));
            holder.tvAmount.setTextColor(ContextCompat.getColor(context, R.color.expense_red));
        }

        // 4. Set Icon (Optional: You can add logic here to change icon based on category)
        // holder.ivIcon.setImageResource(getIconForCategory(transaction.category));
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    public class TransactionViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategory, tvDate, tvAmount;
        ImageView ivIcon;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategory = itemView.findViewById(R.id.tv_category);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvAmount = itemView.findViewById(R.id.tv_amount);
            ivIcon = itemView.findViewById(R.id.iv_category_icon);
        }
    }
}