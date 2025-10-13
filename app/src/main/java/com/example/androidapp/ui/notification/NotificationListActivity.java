package com.example.androidapp.ui.notification;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.androidapp.R;
import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.entities.Notification;
import com.example.androidapp.ui.common.GenericAdapter;
import com.example.androidapp.utils.SessionManager;
import java.util.ArrayList;
import java.util.List;

public class NotificationListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private GenericAdapter<Notification> adapter;
    private AppDatabase database;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_list);

        database = AppDatabase.getDatabase(this);
        sessionManager = new SessionManager(this);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        setupRecyclerView();
        loadNotifications();
    }

    private void setupRecyclerView() {
        adapter = new GenericAdapter<Notification>(new ArrayList<>(), notification -> {
            Intent intent = new Intent(NotificationListActivity.this, NotificationDetailActivity.class);
            intent.putExtra("notification_id", notification.getId());
            startActivity(intent);
        }) {
            @Override
            protected int getLayoutResId() {
                return R.layout.notification_list_row;
            }

            @Override
            protected void bindView(View itemView, Notification notification) {
                TextView tvType = itemView.findViewById(R.id.tvType);
                TextView tvMessagePreview = itemView.findViewById(R.id.tvMessagePreview);
                TextView tvTimePreview = itemView.findViewById(R.id.tvTimePreview);

                if (tvType != null) tvType.setText(notification.getType());
                if (tvMessagePreview != null) tvMessagePreview.setText(notification.getMessage());
                if (tvTimePreview != null) tvTimePreview.setText(notification.getCreatedAt());
            }
        };
        recyclerView.setAdapter(adapter);
    }

    private void loadNotifications() {
        String userId = sessionManager.getCurrentUserId();
        if (userId != null) {
            AppDatabase.databaseWriteExecutor.execute(() -> {
                List<Notification> notifications = database.notificationDao().getNotificationsByUserIdSync(userId);
                runOnUiThread(() -> {
                    if (notifications != null) {
                        adapter.setData(notifications);
                    }
                });
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadNotifications();
    }
}
