package com.example.androidapp.ui.reminder;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.androidapp.R;
import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.entities.Reminder;
import com.example.androidapp.utils.SessionManager;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.Executors;

public class ReminderDetailActivity extends AppCompatActivity {
    private EditText etTitle, etDesc, etDate, etTime;
    private Button btnSave, btnDel;
    private AppDatabase db;
    private SessionManager sm;
    private String reminderId;
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder_detail);

        db = AppDatabase.getInstance(this);
        sm = new SessionManager(this);

        etTitle = findViewById(R.id.reminder_title_edit_text);
        etDesc  = findViewById(R.id.reminder_description_edit_text);
        etDate  = findViewById(R.id.reminder_date_edit_text);
        etTime  = findViewById(R.id.reminder_time_edit_text);
        btnSave = findViewById(R.id.save_reminder_button);
        btnDel  = findViewById(R.id.delete_reminder_button);

        reminderId = getIntent().getStringExtra("reminder_id");
        if (reminderId != null) load();

        etDate.setOnClickListener(v -> pickDateTime());
        etTime.setOnClickListener(v -> pickDateTime());
        btnSave.setOnClickListener(v -> save());
        btnDel .setOnClickListener(v -> delete());
    }

    private void pickDateTime() {
        Calendar c = Calendar.getInstance();
        new DatePickerDialog(this, (d, y, m, day) -> {
            c.set(y, m, day);
            new TimePickerDialog(this, (t, hh, mm) -> {
                c.set(Calendar.HOUR_OF_DAY, hh);
                c.set(Calendar.MINUTE, mm);
                etDate.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(c.getTime()));
                etTime.setText(new SimpleDateFormat("HH:mm", Locale.getDefault()).format(c.getTime()));
            }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show();
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void load() {
        db.reminderDao().getReminderById(reminderId).observe(this, r -> {
            if (r != null) {
                etTitle.setText(r.getTitle());
                etDesc .setText(r.getDescription());
                if (r.getReminderDateTime() != null) {
                    etDate.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(r.getReminderDateTime()));
                    etTime.setText(new SimpleDateFormat("HH:mm", Locale.getDefault()).format(r.getReminderDateTime()));
                }
            }
        });
    }

    private void save() {
        String title = etTitle.getText().toString().trim();
        String desc  = etDesc.getText().toString().trim();
        String date  = etDate.getText().toString().trim();
        String time  = etTime.getText().toString().trim();
        String companyId = sm.getCurrentCompanyId();
        String userId    = sm.getCurrentUserId();

        if (title.isEmpty() || date.isEmpty() || time.isEmpty()) {
            Toast.makeText(this, "أكمل الحقول", Toast.LENGTH_SHORT).show();
            return;
        }

        Date dt;
        try { dt = sdf.parse(date + " " + time); }
        catch (Exception e) { Toast.makeText(this, "صيغة تاريخ/وقت خاطئة", Toast.LENGTH_SHORT).show(); return; }

        Executors.newSingleThreadExecutor().execute(() -> {
            if (reminderId == null) {
                Reminder r = new Reminder(UUID.randomUUID().toString(), companyId, userId, title, desc, dt, true, "NOTIFICATION");
                db.reminderDao().insert(r);
            } else {
                Reminder r = db.reminderDao().getReminderByIdSync(reminderId);
                if (r != null) {
                    r.setTitle(title);
                    r.setDescription(desc);
                    r.setReminderDateTime(dt);
                    db.reminderDao().update(r);
                }
            }
            runOnUiThread(() -> { Toast.makeText(this, "تم الحفظ", Toast.LENGTH_SHORT).show(); finish(); });
        });
    }

    private void delete() {
        if (reminderId != null) {
            Executors.newSingleThreadExecutor().execute(() -> {
                Reminder r = db.reminderDao().getReminderByIdSync(reminderId);
                if (r != null) db.reminderDao().delete(r);
                runOnUiThread(() -> { Toast.makeText(this, "تم الحذف", Toast.LENGTH_SHORT).show(); finish(); });
            });
        }
    }
}
