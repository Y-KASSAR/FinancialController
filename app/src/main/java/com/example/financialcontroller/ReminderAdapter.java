package com.example.financialcontroller;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.financialcontroller.data.ReminderEntity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ReminderViewHolder> {

    private List<ReminderEntity> reminderList;

    public ReminderAdapter(List<ReminderEntity> reminderList) {
        this.reminderList = reminderList;
    }

    @NonNull
    @Override
    public ReminderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reminder, parent, false);
        return new ReminderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReminderViewHolder holder, int position) {
        ReminderEntity reminder = reminderList.get(position);

        holder.tvTitle.setText(reminder.title);
        holder.tvAmount.setText(String.format(Locale.US, "$%.2f", reminder.amount));

        // Format Date (Long -> String)
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.US);
        holder.tvDate.setText("Due: " + sdf.format(new Date(reminder.dueDate)));
    }

    @Override
    public int getItemCount() {
        return reminderList.size();
    }

    public static class ReminderViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDate, tvAmount;

        public ReminderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_bill_title);
            tvDate = itemView.findViewById(R.id.tv_bill_date);
            tvAmount = itemView.findViewById(R.id.tv_bill_amount);
        }
    }
}