package com.example.androidapp.ui.reminder;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.androidapp.R;
import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.dao.ReminderDao;
import com.example.androidapp.data.entities.Reminder;
import com.example.androidapp.utils.SessionManager;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class ReminderDetailActivity extends AppCompatActivity {
    private EditText titleEditText, descriptionEditText;
    private Button dueDateButton;
    private Spinner prioritySpinner;
    private Button saveButton, deleteButton;
    
    private ReminderDao reminderDao;
    private SessionManager sessionManager;
    private String reminderId;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
    private String dueDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder_detail);

        reminderDao = AppDatabase.getDatabase(this).reminderDao();
        sessionManager = new SessionManager(this);

        initViews();
        
        reminderId = getIntent().getStringExtra("reminder_id");
        if (reminderId != null) {
            loadReminder();
            deleteButton.setVisibility(Button.VISIBLE);
        } else {
            deleteButton.setVisibility(Button.GONE);
            dueDate = dateFormat.format(new Date());
        }

        dueDateButton.setOnClickListener(v -> showDatePicker());
        saveButton.setOnClickListener(v -> saveReminder());
        deleteButton.setOnClickListener(v -> deleteReminder());
    }

    private void initViews() {
        titleEditText = findViewById(R.id.titleEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        dueDateButton = findViewById(R.id.dueDateButton);
        prioritySpinner = findViewById(R.id.prioritySpinner);
        saveButton = findViewById(R.id.saveButton);
        deleteButton = findViewById(R.id.deleteButton);
    }

    private void loadReminder() {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            Reminder reminder = reminderDao.getReminderByIdSync(reminderId, sessionManager.getCurrentCompanyId());
            runOnUiThread(() -> {
                if (reminder != null) {
                    titleEditText.setText(reminder.getTitle());
                    descriptionEditText.setText(reminder.getDescription());
                    dueDate = reminder.getDueDate();
                    dueDateButton.setText(dueDate);
                }
            });
        });
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
            this,
            (view, year, month, dayOfMonth) -> {
                calendar.set(year, month, dayOfMonth);
                dueDate = dateFormat.format(calendar.getTime());
                dueDateButton.setText(dueDate);
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void saveReminder() {
        String title = titleEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();
        String priority = prioritySpinner.getSelectedItem().toString();

        if (title.isEmpty()) {
            Toast.makeText(this, "الرجاء إدخال العنوان", Toast.LENGTH_SHORT).show();
            return;
        }

        String companyId = sessionManager.getCurrentCompanyId();

        AppDatabase.databaseWriteExecutor.execute(() -> {
            Reminder reminder;
            if (reminderId == null) {
                reminder = new Reminder(
                    UUID.randomUUID().toString(),
                    companyId,
                    title,
                    description,
                    dueDate,
                    priority,
                    false,
                    false,
                    "NOTIFICATION",
                    null,
                    sessionManager.getCurrentUserId(),
                    dateFormat.format(new Date())
                );
                reminderDao.insert(reminder);
            } else {
                reminder = new Reminder(
                    reminderId,
                    companyId,
                    title,
                    description,
                    dueDate,
                    priority,
                    false,
                    false,
                    "NOTIFICATION",
                    null,
                    sessionManager.getCurrentUserId(),
                    dateFormat.format(new Date())
                );
                reminderDao.update(reminder);
            }
            
            runOnUiThread(() -> {
                Toast.makeText(this, "تم الحفظ بنجاح", Toast.LENGTH_SHORT).show();
                finish();
            });
        });
    }

    private void deleteReminder() {
        if (reminderId != null) {
            AppDatabase.databaseWriteExecutor.execute(() -> {
                Reminder reminder = reminderDao.getReminderByIdSync(reminderId, sessionManager.getCurrentCompanyId());
                if (reminder != null) {
                    reminderDao.delete(reminder);
                    runOnUiThread(() -> {
                        Toast.makeText(this, "تم الحذف بنجاح", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                }
            });
        }
    }
}
