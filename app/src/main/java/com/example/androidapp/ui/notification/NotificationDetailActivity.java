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
    private TextView tvTitle, tvMsg, tvTime, tvType;
    private Button btnRead, btnDel;
    private AppDatabase db;
    private SessionManager sm;
    private String notifId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_detail);

        db = AppDatabase.getInstance(this);
        sm = new SessionManager(this);

        tvTitle = findViewById(R.id.tvTitle);
        tvMsg   = findViewById(R.id.tvMessage);
        tvTime  = findViewById(R.id.tvTimestamp);
        tvType  = findViewById(R.id.tvType);
        btnRead = findViewById(R.id.btnMarkAsRead);
        btnDel  = findViewById(R.id.btnDelete);

        notifId = getIntent().getStringExtra("notification_id");
        if (notifId != null) load();

        btnRead.setOnClickListener(v -> markRead());
        btnDel .setOnClickListener(v -> delete());
    }

    private void load() {
        db.notificationDao().getNotificationById(notifId).observe(this, n -> {
            if (n != null) {
                tvTitle.setText(n.getTitle());
                tvMsg  .setText(n.getMessage());
                tvTime .setText(n.getTimestamp());
                tvType .setText(n.getNotificationType());
            }
        });
    }

    private void markRead() {
        Executors.newSingleThreadExecutor().execute(() -> {
            Notification n = db.notificationDao().getNotificationByIdSync(notifId);
            if (n != null) {
                n.setRead(true);
                db.notificationDao().update(n);
            }
            runOnUiThread(() -> Toast.makeText(this, "تم التحديد كمقروء", Toast.LENGTH_SHORT).show());
        });
    }

    private void delete() {
        Executors.newSingleThreadExecutor().execute(() -> {
            Notification n = db.notificationDao().getNotificationByIdSync(notifId);
            if (n != null) db.notificationDao().delete(n);
            runOnUiThread(() -> { Toast.makeText(this, "تم الحذف", Toast.LENGTH_SHORT).show(); finish(); });
        });
    }
}
