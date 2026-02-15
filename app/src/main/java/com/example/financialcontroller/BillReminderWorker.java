package com.example.financialcontroller;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.financialcontroller.data.DatabaseClient;
import com.example.financialcontroller.data.ReminderEntity;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class BillReminderWorker extends Worker {

    private static final String CHANNEL_ID = "bills_alerts";

    public BillReminderWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        // 1. Get all reminders from DB
        List<ReminderEntity> reminders = DatabaseClient.getInstance(getApplicationContext())
                .getAppDatabase()
                .reminderDao()
                .getAllReminders();

        // 2. Check each reminder
        long today = System.currentTimeMillis();

        for (ReminderEntity reminder : reminders) {
            if (reminder.isPaid) continue; // Skip paid bills

            long diffInMillis = reminder.dueDate - today;
            long daysDiff = TimeUnit.MILLISECONDS.toDays(diffInMillis);

            // Logic: Check the checkboxes the user selected
            boolean shouldNotify = false;
            String message = "";

            // Case 1: Due Today (approx 0 days diff)
            if (daysDiff == 0 && reminder.remindOnDate) {
                shouldNotify = true;
                message = "Due Today: " + reminder.title + " ($" + reminder.amount + ")";
            }
            // Case 2: Due Tomorrow (approx 1 day diff)
            else if (daysDiff == 1 && reminder.remindDayBefore) {
                shouldNotify = true;
                message = "Due Tomorrow: " + reminder.title;
            }
            // Case 3: Due in a Week (approx 7 days diff)
            else if (daysDiff == 7 && reminder.remindWeekBefore) {
                shouldNotify = true;
                message = "Due in 1 Week: " + reminder.title;
            }

            if (shouldNotify) {
                sendNotification(reminder.id, "Bill Reminder", message);
            }
        }

        return Result.success();
    }

    private void sendNotification(int id, String title, String message) {
        NotificationManager manager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        // Create Channel (Required for Android 8+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Bill Alerts", NotificationManager.IMPORTANCE_HIGH);
            manager.createNotificationChannel(channel);
        }

        // Intent to open App when clicked
        Intent intent = new Intent(getApplicationContext(), Reminders.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_calendar) // Ensure this icon exists
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        manager.notify(id, builder.build());
    }
}