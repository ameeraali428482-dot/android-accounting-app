package com.example.androidapp.ui.reminder;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.androidapp.R;
import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.entities.Reminder;
import com.example.androidapp.utils.SessionManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.Executors;

public class ReminderDetailActivity extends AppCompatActivity {
    private EditText etTitle;
    private EditText etDescription;
    private EditText etDate;
    private EditText etTime;
    private Button btnSave;
    private Button btnDelete;

    private AppDatabase database;
    private SessionManager sessionManager;
    private String reminderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder_detail);

        database = AppDatabase.getInstance(this);
        sessionManager = new SessionManager(this);

        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        etDate = findViewById(R.id.etDate);
        etTime = findViewById(R.id.etTime);
        btnSave = findViewById(R.id.btnSave);
        btnDelete = findViewById(R.id.btnDelete);

        reminderId = getIntent().getStringExtra("reminder_id");

        if (reminderId != null) {
            loadReminderDetails();
        }

        btnSave.setOnClickListener(v -> saveReminder());
        btnDelete.setOnClickListener(v -> deleteReminder());
    }

    private void loadReminderDetails() {
        Executors.newSingleThreadExecutor().execute(() -> {
            String companyId = sessionManager.getUserDetails().get(SessionManager.KEY_COMPANY_ID);
            database.reminderDao().getReminderById(reminderId, companyId).observeForever(reminder -> {
                if (reminder != null) {
                    runOnUiThread(() -> {
                        etTitle.setText(reminder.getTitle());
                        etDescription.setText(reminder.getDescription());
                        
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
                        
                        etDate.setText(dateFormat.format(reminder.getReminderDateTime()));
                        etTime.setText(timeFormat.format(reminder.getReminderDateTime()));
                    });
                }
            });
        });
    }

    private void saveReminder() {
        String title = etTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String dateStr = etDate.getText().toString().trim();
        String timeStr = etTime.getText().toString().trim();
        String companyId = sessionManager.getUserDetails().get(SessionManager.KEY_COMPANY_ID);
        String userId = sessionManager.getUserDetails().get(SessionManager.KEY_USER_ID);

        if (title.isEmpty() || dateStr.isEmpty() || timeStr.isEmpty()) {
            Toast.makeText(this, "الرجاء إدخال جميع الحقول المطلوبة", Toast.LENGTH_SHORT).show();
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        Date reminderDateTime;
        try {
            reminderDateTime = sdf.parse(dateStr + " " + timeStr);
        } catch (ParseException e) {
            Toast.makeText(this, "صيغة التاريخ أو الوقت غير صحيحة", Toast.LENGTH_SHORT).show();
            return;
        }

        Executors.newSingleThreadExecutor().execute(() -> {
            if (reminderId == null) {
                Reminder reminder = new Reminder(
                    UUID.randomUUID().toString(),
                    companyId,
                    userId,
                    title,
                    description,
                    reminderDateTime,
                    false,
                    true,
                    "NOTIFICATION",
                    null,
                    null,
                    null
                );
                database.reminderDao().insert(reminder);
            } else {
                database.reminderDao().getReminderById(reminderId, companyId).observeForever(reminder -> {
                    if (reminder != null) {
                        reminder.setTitle(title);
                        reminder.setDescription(description);
                        reminder.setReminderDateTime(reminderDateTime);
                        database.reminderDao().update(reminder);
                    }
                });
            }

            runOnUiThread(() -> {
                Toast.makeText(this, "تم حفظ التذكير بنجاح", Toast.LENGTH_SHORT).show();
                finish();
            });
        });
    }

    private void deleteReminder() {
        if (reminderId != null) {
            Executors.newSingleThreadExecutor().execute(() -> {
                String companyId = sessionManager.getUserDetails().get(SessionManager.KEY_COMPANY_ID);
                database.reminderDao().getReminderById(reminderId, companyId).observeForever(reminder -> {
                    if (reminder != null) {
                        database.reminderDao().delete(reminder);
                        runOnUiThread(() -> {
                            Toast.makeText(this, "تم حذف التذكير بنجاح", Toast.LENGTH_SHORT).show();
                            finish();
                        });
                    }
                });
            });
        }
    }
}
