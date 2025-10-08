package com.example.androidapp.ui.reminder;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidapp.App;
import com.example.androidapp.R;
import com.example.androidapp.data.dao.ReminderDao;
import com.example.androidapp.models.Reminder;
import com.example.androidapp.ui.common.GenericAdapter;
import com.example.androidapp.utils.SessionManager;

import java.util.List;

public class ReminderListActivity extends AppCompatActivity {

    private RecyclerView reminderRecyclerView;
    private ReminderDao reminderDao;
    private SessionManager sessionManager;
    private GenericAdapter<Reminder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder_list);

        reminderRecyclerView = findViewById(R.id.reminder_recycler_view);
        reminderRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        reminderDao = new ReminderDao(App.getDatabaseHelper());
        sessionManager = new SessionManager(this);

        findViewById(R.id.add_reminder_button).setOnClickListener(v -> {
            Intent intent = new Intent(ReminderListActivity.this, ReminderDetailActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadReminders();
    }

    private void loadReminders() {
        String companyId = sessionManager.getUserDetails().get(SessionManager.KEY_COMPANY_ID);
        if (companyId == null) {
            // Handle error: no company ID found
            return;
        }

        List<Reminder> reminders = reminderDao.getRemindersByCompanyId(companyId);

        adapter = new GenericAdapter<Reminder>(reminders) {
            @Override
            protected int getLayoutResId() {
                return R.layout.reminder_list_row;
            }

            @Override
            protected void bindView(View itemView, Reminder reminder) {
                TextView reminderTitle = itemView.findViewById(R.id.reminder_title);
                TextView reminderDate = itemView.findViewById(R.id.reminder_date);
                TextView reminderTime = itemView.findViewById(R.id.reminder_time);

                reminderTitle.setText(reminder.getTitle());
                reminderDate.setText(reminder.getDate());
                reminderTime.setText(reminder.getTime());

                itemView.setOnClickListener(v -> {
                    Intent intent = new Intent(ReminderListActivity.this, ReminderDetailActivity.class);
                    intent.putExtra("reminder_id", reminder.getId());
                    startActivity(intent);
                });
            }
        };
        reminderRecyclerView.setAdapter(adapter);
    }
}
