import java.util.Date;
package com.example.androidapp.ui.notification;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
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
        recyclerView = findViewById(R.id.recycler_view);

        setTitle("الإشعارات");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        adapter = new GenericAdapter<>(
                new ArrayList<>(),
                R.layout.notification_list_row,
                (notification, view) -> {
                    TextView tvTitle = notification.findViewById(R.id.tv_notification_title);
                    TextView tvMessage = notification.findViewById(R.id.tv_notification_message);
                    TextView tvTimestamp = notification.findViewById(R.id.tv_notification_timestamp);
                    TextView tvType = notification.findViewById(R.id.tv_notification_type);

                    tvTitle.setText(view.getTitle());
                    tvMessage.setText(view.getMessage());
                    tvTimestamp.setText(dateFormat.format(view.getCreatedAt()));
                    tvType.setText(view.getType());

                    // Set background based on read status
                    if (view.isRead()) {
                        notification.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                    } else {
                        notification.setBackgroundColor(getResources().getColor(R.color.light_gray));
                    }

                    // Set type background
                    int typeBackground;
                    switch (view.getType()) {
                        case "Info":
                            typeBackground = R.drawable.status_active_background;
                            break;
                        case "Warning":
                            typeBackground = R.drawable.status_draft_background;
                            break;
                        case "Error":
                            typeBackground = R.drawable.status_inactive_background;
                            break;
                        default:
                            typeBackground = R.drawable.status_pending_background;
                            break;
                    }
                    tvType.setBackgroundResource(typeBackground);
                },
                notification -> {
                    // Mark as read when clicked
                    if (!((Notification)itemView.getTag()).isRead()) {
                        notification.setRead(true);
                        AppDatabase.databaseWriteExecutor.execute(() -> {
                            database.notificationDao().update(notification);
                        });
                        adapter.notifyDataSetChanged();
                    }
                }
        );
        
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
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case android.R.id.home: // Fixed constant expression
                loadNotifications();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadNotifications();
    }
}
