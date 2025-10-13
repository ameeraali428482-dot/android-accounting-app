package com.example.androidapp.ui.notification;

import java.util.Date;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.androidapp.R;
import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.entities.Notification;
import java.text.SimpleDateFormat;
import java.util.Locale;






public class NotificationDetailActivity extends AppCompatActivity {

    private TextView tvTitle, tvMessage, tvType, tvTimestamp;
    private AppDatabase database;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_detail);

        database = AppDatabase.getDatabase(this);

        initViews();
        loadNotificationDetails();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("تفاصيل الإشعار");
    }

    private void initViews() {
    }

    private void loadNotificationDetails() {
        int notificationId = getIntent().getIntExtra("notification_id", -1);
        if (notificationId != -1) {
            database.notificationDao().getNotificationById(String.valueOf(notificationId))
                    .observe(this, notification -> {
                        if (notification != null) {
                            tvTitle.setText(notification.getTitle());
                            tvMessage.setText(notification.getMessage());
                            tvType.setText(notification.getType());
                            tvTimestamp.setText(dateFormat.format(notification.getCreatedAt()));
                        }
                    });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
