package com.example.androidapp.ui.notification;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.androidapp.R;
import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.entities.Notification;

public class NotificationDetailActivity extends AppCompatActivity {
    private TextView tvTitle, tvMessage, tvTimestamp;
    private AppDatabase database;
    private String notificationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_detail);

        database = AppDatabase.getDatabase(this);
        notificationId = getIntent().getStringExtra("notification_id");

        initViews();
        if (notificationId != null) {
            loadNotification();
        }
    }

    private void initViews() {
        tvTitle = findViewById(R.id.tvTitle);
        tvMessage = findViewById(R.id.tvMessage);
        tvTimestamp = findViewById(R.id.tvTimestamp);
    }

    private void loadNotification() {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            Notification notification = database.notificationDao().getNotificationByIdSync(notificationId);
            runOnUiThread(() -> {
                if (notification != null) {
                    if (tvTitle != null) tvTitle.setText(notification.getType());
                    if (tvMessage != null) tvMessage.setText(notification.getMessage());
                    if (tvTimestamp != null) tvTimestamp.setText(notification.getCreatedAt());
                    
                    // Mark as read
                    if (!notification.isRead()) {
                        notification.setRead(true);
                        AppDatabase.databaseWriteExecutor.execute(() -> 
                            database.notificationDao().update(notification)
                        );
                    }
                }
            });
        });
    }
}
