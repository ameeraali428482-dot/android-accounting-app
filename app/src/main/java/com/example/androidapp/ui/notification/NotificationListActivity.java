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
    private AppDatabase db;
    private SessionManager sm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_list);

        db = AppDatabase.getInstance(this);
        sm = new SessionManager(this);

        recyclerView = findViewById(R.id.notification_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadNotifs();
    }

    private void loadNotifs() {
        String companyId = sm.getUserDetails().get(SessionManager.KEY_COMPANY_ID);
        if (companyId == null) return;

        adapter = new GenericAdapter<>(new ArrayList<>(), item -> {
            Intent i = new Intent(NotificationListActivity.this, NotificationDetailActivity.class);
            i.putExtra("notification_id", item.getId());
            startActivity(i);
        }) {
            @Override
            protected int getLayoutResId() {
                return R.layout.notification_list_row;
            }

            @Override
            protected void bindView(View itemView, Notification n) {
                TextView tvTitle = itemView.findViewById(R.id.tvTitle);
                TextView tvMsg   = itemView.findViewById(R.id.tvMessage);
                TextView tvTime  = itemView.findViewById(R.id.tvTimestamp);
                TextView tvType  = itemView.findViewById(R.id.tvType);

                tvTitle.setText(n.getTitle());
                tvMsg  .setText(n.getMessage());
                tvTime .setText(n.getTimestamp());
                tvType .setText(n.getNotificationType());
            }
        };

        recyclerView.setAdapter(adapter);
        db.notificationDao().getAllNotifications().observe(this, list -> adapter.updateData(list));
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadNotifs();
    }
}
