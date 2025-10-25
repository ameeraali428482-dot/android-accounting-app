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
    private AppDatabase db;
    private SessionManager sm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder_list);

        db = AppDatabase.getInstance(this);
        sm = new SessionManager(this);

        recyclerView = findViewById(R.id.reminder_recycler_view);
        FloatingActionButton fab = findViewById(R.id.add_reminder_button);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        fab.setOnClickListener(v -> {
            Intent i = new Intent(ReminderListActivity.this, ReminderDetailActivity.class);
            startActivity(i);
        });

        loadReminders();
    }

    private void loadReminders() {
        String companyId = sm.getCurrentCompanyId();
        if (companyId == null) return;

        adapter = new GenericAdapter<>(new ArrayList<>(), item -> {
            Intent i = new Intent(ReminderListActivity.this, ReminderDetailActivity.class);
            i.putExtra("reminder_id", item.getId());
            startActivity(i);
        }) {
            @Override
            protected int getLayoutResId() {
                return R.layout.reminder_list_row;
            }

            @Override
            protected void bindView(View itemView, Reminder r) {
                TextView tvTitle = itemView.findViewById(R.id.reminder_title);
                TextView tvDate  = itemView.findViewById(R.id.reminder_date);
                TextView tvTime  = itemView.findViewById(R.id.reminder_time);

                tvTitle.setText(r.getTitle());
                if (r.getReminderDateTime() != null) {
                    tvDate.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(r.getReminderDateTime()));
                    tvTime.setText(new SimpleDateFormat("HH:mm", Locale.getDefault()).format(r.getReminderDateTime()));
                }
            }
        };

        recyclerView.setAdapter(adapter);
        db.reminderDao().getRemindersByCompanyId(companyId).observe(this, list -> adapter.updateData(list));
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadReminders();
    }
}
