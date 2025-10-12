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
import java.util.ArrayList;
import java.util.List;

public class ReminderListActivity extends AppCompatActivity {
    private RecyclerView reminderRecyclerView;
    private GenericAdapter<Reminder> adapter;
    private AppDatabase database;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder_list);

        database = AppDatabase.getDatabase(this);
        sessionManager = new SessionManager(this);

        reminderRecyclerView = findViewById(R.id.reminderRecyclerView);
        reminderRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReminderListActivity.this, ReminderDetailActivity.class);
                startActivity(intent);
            }
        });

        loadReminders();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadReminders();
    }

    private void loadReminders() {
        String companyId = sessionManager.getCurrentCompanyId();
        if (companyId == null) {
            return;
        }

        adapter = new GenericAdapter<Reminder>(new ArrayList<Reminder>()) {
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
                reminderDate.setText(reminder.getDate());
                reminderTime.setText(reminder.getTime());

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(ReminderListActivity.this, ReminderDetailActivity.class);
                        intent.putExtra("reminder_id", reminder.getId());
                        startActivity(intent);
                    }
                });
            }
        };

        reminderRecyclerView.setAdapter(adapter);

        database.reminderDao().getRemindersByCompanyId(companyId).observe(this, reminders -> {
            if (reminders != null) {
                adapter.updateData(reminders);
            }
        });
    }
}
