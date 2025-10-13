package com.example.androidapp.ui.reminder;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.androidapp.R;
import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.entities.Reminder;
import com.example.androidapp.ui.common.GenericAdapter;
import com.example.androidapp.utils.SessionManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class ReminderListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private GenericAdapter<Reminder> adapter;
    private AppDatabase database;
    private SessionManager sessionManager;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder_list);

        database = AppDatabase.getDatabase(this);
        sessionManager = new SessionManager(this);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FloatingActionButton fab = findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(v -> {
                Intent intent = new Intent(this, ReminderDetailActivity.class);
                startActivity(intent);
            });
        }

        setupRecyclerView();
        loadReminders();
    }

    private void setupRecyclerView() {
        adapter = new GenericAdapter<Reminder>(new ArrayList<>(), reminder -> {
            Intent intent = new Intent(ReminderListActivity.this, ReminderDetailActivity.class);
            intent.putExtra("reminder_id", reminder.getId());
            startActivity(intent);
        }) {
            @Override
            protected int getLayoutResId() {
                return R.layout.reminder_list_row;
            }

            @Override
            protected void bindView(View itemView, Reminder reminder) {
                TextView tvReminderTitle = itemView.findViewById(R.id.tvReminderTitle);
                TextView tvReminderDueDate = itemView.findViewById(R.id.tvReminderDueDate);
                TextView tvReminderPriority = itemView.findViewById(R.id.tvReminderPriority);

                if (tvReminderTitle != null) tvReminderTitle.setText(reminder.getTitle());
                if (tvReminderDueDate != null) tvReminderDueDate.setText(reminder.getDueDate());
                if (tvReminderPriority != null) tvReminderPriority.setText(reminder.getPriority());
            }
        };
        recyclerView.setAdapter(adapter);
    }

    private void loadReminders() {
        String companyId = sessionManager.getCurrentCompanyId();
        if (companyId != null) {
            database.reminderDao().getAllReminders(companyId).observe(this, reminders -> {
                if (reminders != null) {
                    adapter.updateData(reminders);
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadReminders();
    }
}
