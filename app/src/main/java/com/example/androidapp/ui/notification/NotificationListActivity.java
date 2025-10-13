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

public class NotificationListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private GenericAdapter<Notification> adapter;
    private AppDatabase database;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_list);

        database = AppDatabase.getInstance(this);
        sessionManager = new SessionManager(this);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadNotifications();
    }

    private void loadNotifications() {
        String companyId = sessionManager.getUserDetails().get(SessionManager.KEY_COMPANY_ID);

        adapter = new GenericAdapter<>(new ArrayList<>(), new GenericAdapter.OnItemClickListener<Notification>() {
            @Override
            public void onItemClick(Notification item) {
                Intent intent = new Intent(NotificationListActivity.this, NotificationDetailActivity.class);
                intent.putExtra("notification_id", item.getId());
                startActivity(intent);
            }
        }) {
            @Override
            protected int getLayoutResId() {
                return R.layout.notification_list_row;
            }

            @Override
            protected void bindView(View itemView, Notification notification) {
                TextView tvTitle = itemView.findViewById(R.id.tvTitle);
                TextView tvMessage = itemView.findViewById(R.id.tvMessage);
                TextView tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
                TextView tvType = itemView.findViewById(R.id.tvType);

                tvTitle.setText(notification.getTitle());
                tvMessage.setText(notification.getMessage());
                tvTimestamp.setText(notification.getTimestamp());
                tvType.setText(notification.getNotificationType());
            }
        };

        recyclerView.setAdapter(adapter);

        database.notificationDao().getAllNotifications(companyId).observe(this, notifications -> {
            if (notifications != null) {
                adapter.updateData(notifications);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadNotifications();
    }
}
