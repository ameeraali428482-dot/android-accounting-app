import java.util.Date;
package com.example.androidapp.ui.reminder;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.androidapp.App;
import com.example.androidapp.R;
import com.example.androidapp.data.dao.ReminderDao;
import com.example.androidapp.data.entities.Reminder;
import com.example.androidapp.utils.SessionManager;

import java.util.UUID;

public class ReminderDetailActivity extends AppCompatActivity {

    private EditText titleEditText, descriptionEditText, dateEditText, timeEditText;
    private Button saveButton, deleteButton;
    private ReminderDao reminderDao;
    private SessionManager sessionManager;
    private String reminderId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder_detail);

        titleEditText = findViewById(R.id.reminder_title_edit_text);
        descriptionEditText = findViewById(R.id.reminder_description_edit_text);
        dateEditText = findViewById(R.id.reminder_date_edit_text);
        timeEditText = findViewById(R.id.reminder_time_edit_text);
        saveButton = findViewById(R.id.save_reminder_button);
        deleteButton = findViewById(R.id.delete_reminder_button);

        reminderDao = new ReminderDao(App.getDatabaseHelper());
        sessionManager = new SessionManager(this);

        if (getIntent().hasExtra("reminder_id")) {
            reminderId = getIntent().getStringExtra("reminder_id");
            loadReminderData(reminderId);
            deleteButton.setVisibility(View.VISIBLE);
        } else {
            deleteButton.setVisibility(View.GONE);
        }

        saveButton.setOnClickListener(v -> saveReminder());
        deleteButton.setOnClickListener(v -> deleteReminder());
    }

    private void loadReminderData(String id) {
        Reminder reminder = reminderDao.getById(id);
        if (reminder != null) {
            titleEditText.setText(reminder.getTitle());
            descriptionEditText.setText(reminder.getDescription());
            dateEditText.setText(reminder.getDate());
            timeEditText.setText(reminder.getTime());
        }
    }

    private void saveReminder() {
        String title = titleEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();
        String date = dateEditText.getText().toString().trim();
        String time = timeEditText.getText().toString().trim();
        String companyId = sessionManager.getUserDetails().get(SessionManager.KEY_COMPANY_ID);

        if (companyId == null) {
            Toast.makeText(this, "خطأ: لم يتم العثور على معرف الشركة.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (title.isEmpty() || date.isEmpty() || time.isEmpty()) {
            Toast.makeText(this, "الرجاء تعبئة جميع الحقول المطلوبة (العنوان، التاريخ، الوقت).", Toast.LENGTH_SHORT).show();
            return;
        }

        Reminder reminder;
        if (reminderId == null) {
            // New reminder
            reminder = new Reminder(UUID.randomUUID().toString(), companyId, title, description, date, time);
            reminderDao.insert(reminder);
            Toast.makeText(this, "تم إضافة التذكير بنجاح.", Toast.LENGTH_SHORT).show();
        } else {
            // Existing reminder
            reminder = new Reminder(reminderId, companyId, title, description, date, time);
            reminderDao.update(reminder);
            Toast.makeText(this, "تم تحديث التذكير بنجاح.", Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    private void deleteReminder() {
        if (reminderId != null) {
            reminderDao.delete(reminderId);
            Toast.makeText(this, "تم حذف التذكير بنجاح.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
