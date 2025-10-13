package com.example.androidapp.ui.notification;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class NotificationListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private GenericAdapter<Notification> adapter;
    private AppDatabase database;
    private SessionManager sessionManager;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_list);

        database = AppDatabase.getDatabase(this);
        sessionManager = new SessionManager(this);

        initViews();
        setupRecyclerView();
        loadNotifications();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerView);
        setTitle("الإشعارات");
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        adapter = new GenericAdapter<Notification>(new ArrayList<Notification>()) {
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

                if (tvTitle != null) tvTitle.setText(notification.getTitle());
                if (tvMessage != null) tvMessage.setText(notification.getMessage());
                if (tvTimestamp != null) tvTimestamp.setText(dateFormat.format(notification.getCreatedAt()));
                if (tvType != null) tvType.setText(notification.getType());

                if (!notification.isRead()) {
                    itemView.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
                } else {
                    itemView.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                }

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(NotificationListActivity.this, NotificationDetailActivity.class);
                        intent.putExtra("notification_id", notification.getId());
                        startActivity(intent);
                    }
                });
            }
        };
        
        recyclerView.setAdapter(adapter);
    }

    private void loadNotifications() {
        database.notificationDao().getAllNotifications()
                .observe(this, notifications -> {
                    if (notifications != null) {
                        adapter.updateData(notifications);
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadNotifications();
    }
}
