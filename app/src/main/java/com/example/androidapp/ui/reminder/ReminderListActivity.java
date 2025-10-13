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
    private RecyclerView reminderRecyclerView;
    private GenericAdapter<Reminder> adapter;
    private AppDatabase database;
    private SessionManager sessionManager;
    private FloatingActionButton fabAddReminder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder_list);

        database = AppDatabase.getInstance(this);
        sessionManager = new SessionManager(this);

        reminderRecyclerView = findViewById(R.id.reminderRecyclerView);
        fabAddReminder = findViewById(R.id.fabAddReminder);

        reminderRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        fabAddReminder.setOnClickListener(v -> {
            Intent intent = new Intent(ReminderListActivity.this, ReminderDetailActivity.class);
            startActivity(intent);
        });

        loadReminders();
    }

    private void loadReminders() {
        String companyId = sessionManager.getUserDetails().get(SessionManager.KEY_COMPANY_ID);

        adapter = new GenericAdapter<>(new ArrayList<>(), new GenericAdapter.OnItemClickListener<Reminder>() {
            @Override
            public void onItemClick(Reminder item) {
                Intent intent = new Intent(ReminderListActivity.this, ReminderDetailActivity.class);
                intent.putExtra("reminder_id", item.getId());
                startActivity(intent);
            }
        }) {
            @Override
            protected int getLayoutResId() {
                return R.layout.reminder_list_row;
            }

            @Override
            protected void bindView(View itemView, Reminder reminder) {
                TextView reminderTitle = itemView.findViewById(R.id.reminderTitle);
                TextView reminderDate = itemView.findViewById(R.id.reminderDate);
                TextView reminderTime = itemView.findViewById(R.id.reminderTime);

                reminderTitle.setText(reminder.getTitle());
                
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
                
                reminderDate.setText(dateFormat.format(reminder.getReminderDateTime()));
                reminderTime.setText(timeFormat.format(reminder.getReminderDateTime()));
            }
        };

        reminderRecyclerView.setAdapter(adapter);

        database.reminderDao().getAllReminders(companyId).observe(this, reminders -> {
            if (reminders != null) {
                adapter.updateData(reminders);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadReminders();
    }
}
