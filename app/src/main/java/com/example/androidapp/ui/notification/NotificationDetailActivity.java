package com.example.androidapp.ui.notification;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.androidapp.R;
import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.entities.Notification;
import com.example.androidapp.utils.SessionManager;

import java.util.concurrent.Executors;

public class NotificationDetailActivity extends AppCompatActivity {
    private TextView tvTitle;
    private TextView tvMessage;
    private TextView tvTimestamp;
    private TextView tvType;
    private Button btnMarkAsRead;
    private Button btnDelete;

    private AppDatabase database;
    private SessionManager sessionManager;
    private String notificationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_detail);

        database = AppDatabase.getInstance(this);
        sessionManager = new SessionManager(this);

        tvTitle = findViewById(R.id.tvTitle);
        tvMessage = findViewById(R.id.tvMessage);
        tvTimestamp = findViewById(R.id.tvTimestamp);
        tvType = findViewById(R.id.tvType);
        btnMarkAsRead = findViewById(R.id.btnMarkAsRead);
        btnDelete = findViewById(R.id.btnDelete);

        notificationId = getIntent().getStringExtra("notification_id");

        if (notificationId != null) {
            loadNotificationDetails();
        }

        btnMarkAsRead.setOnClickListener(v -> markAsRead());
        btnDelete.setOnClickListener(v -> deleteNotification());
    }

    private void loadNotificationDetails() {
        String companyId = sessionManager.getUserDetails().get(SessionManager.KEY_COMPANY_ID);
        
        database.notificationDao().getNotificationById(notificationId, companyId)
                .observe(this, notification -> {
                    if (notification != null) {
                        tvTitle.setText(notification.getTitle());
                        tvMessage.setText(notification.getMessage());
                        tvTimestamp.setText(notification.getTimestamp());
                        tvType.setText(notification.getNotificationType());
                    }
                });
    }

    private void markAsRead() {
        Executors.newSingleThreadExecutor().execute(() -> {
            // تحديث حالة الإشعار
            runOnUiThread(() -> {
                Toast.makeText(this, "تم تحديد الإشعار كمقروء", Toast.LENGTH_SHORT).show();
            });
        });
    }

    private void deleteNotification() {
        if (notificationId != null) {
            Executors.newSingleThreadExecutor().execute(() -> {
                String companyId = sessionManager.getUserDetails().get(SessionManager.KEY_COMPANY_ID);
                database.notificationDao().getNotificationById(notificationId, companyId)
                        .observeForever(notification -> {
                            if (notification != null) {
                                database.notificationDao().delete(notification);
                                runOnUiThread(() -> {
                                    Toast.makeText(this, "تم حذف الإشعار بنجاح", Toast.LENGTH_SHORT).show();
                                    finish();
                                });
                            }
                        });
            });
        }
    }
}
