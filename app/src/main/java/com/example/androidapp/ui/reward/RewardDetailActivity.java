package com.example.androidapp.ui.reward;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.androidapp.R;
import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.entities.Reward;
import com.example.androidapp.utils.SessionManager;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;






public class RewardDetailActivity extends AppCompatActivity {
    private EditText etName, etDescription, etPointsRequired, etValidUntil;
    private CheckBox cbIsActive;
    private Button btnSave;
    private Reward currentReward;
    private AppDatabase database;
    private SessionManager sessionManager;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private Date selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reward_detail);

        database = AppDatabase.getDatabase(this);
        sessionManager = new SessionManager(this);

        initViews();
        setupDatePicker();
        loadRewardData();
        setupSaveButton();
    }

    private void initViews() {
        etName = // TODO: Fix findViewById;
        etDescription = // TODO: Fix findViewById;
        etPointsRequired = // TODO: Fix findViewById;
        etValidUntil = // TODO: Fix findViewById;
        cbIsActive = // TODO: Fix findViewById;
        btnSave = // TODO: Fix findViewById;

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setupDatePicker() {
        etValidUntil.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            if (selectedDate != null) {
                calendar.setTime(selectedDate);
            }

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    this,
                    (view, year, month, dayOfMonth) -> {
                        calendar.set(year, month, dayOfMonth);
                        selectedDate = calendar.getTime();
                        etValidUntil.setText(dateFormat.format(selectedDate));
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.show();
        });
    }

    private void loadRewardData() {
        int rewardId = getIntent().getIntExtra("reward_id", -1);
        if (rewardId != -1) {
            setTitle("تعديل المكافأة");
            database.rewardDao().getRewardById(rewardId, sessionManager.getCurrentCompanyId())
                    .observe(this, reward -> {
                        if (reward != null) {
                            currentReward = reward;
                            populateFields();
                        }
                    });
        } else {
            setTitle("إضافة مكافأة جديدة");
            currentReward = null;
        }
    }

    private void populateFields() {
        if (currentReward != null) {
            etName.setText(currentReward.getName());
            etDescription.setText(currentReward.getDescription());
            etPointsRequired.setText(String.valueOf(currentReward.getPointsRequired()));
            cbIsActive.setChecked(currentReward.isActive());

            if (currentReward.getValidUntil() != null) {
                selectedDate = currentReward.getValidUntil();
                etValidUntil.setText(dateFormat.format(selectedDate));
            }
        }
    }

    private void setupSaveButton() {
        btnSave.setOnClickListener(v -> saveReward());
    }

    private void saveReward() {
        String name = etName.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String pointsRequiredStr = etPointsRequired.getText().toString().trim();
        boolean isActive = cbIsActive.isChecked();

        if (name.isEmpty()) {
            etName.setError("اسم المكافأة مطلوب");
            return;
        }

        if (pointsRequiredStr.isEmpty()) {
            etPointsRequired.setError("النقاط المطلوبة مطلوبة");
            return;
        }

        int pointsRequired;
        try {
            pointsRequired = Integer.parseInt(pointsRequiredStr);
        } catch (NumberFormatException e) {
            etPointsRequired.setError("يرجى إدخال رقم صحيح");
            return;
        }

        new Thread(() -> {
            try {
                if (currentReward == null) {
                    // إنشاء مكافأة جديدة
                    Reward newReward = new Reward(
                            sessionManager.getCurrentCompanyId(),
                            name,
                            description,
                            pointsRequired,
                            selectedDate,
                            isActive
                    );
                    database.rewardDao().insert(newReward);
                } else {
                    // تحديث المكافأة الحالية
                    currentReward.setName(name);
                    currentReward.setDescription(description);
                    currentReward.setPointsRequired(pointsRequired);
                    currentReward.setValidUntil(selectedDate);
                    currentReward.setActive(isActive);
                    database.rewardDao().update(currentReward);
                }

                runOnUiThread(() -> {
                    Toast.makeText(this, "تم حفظ المكافأة بنجاح", Toast.LENGTH_SHORT).show();
                    finish();
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "خطأ في حفظ المكافأة: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (currentReward != null) {
            getMenuInflater().inflate(R.menu.menu_detail, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_delete:
                deleteReward();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void deleteReward() {
        if (currentReward != null) {
            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("حذف المكافأة")
                    .setMessage("هل أنت متأكد من حذف هذه المكافأة؟")
                    .setPositiveButton("حذف", (dialog, which) -> {
                        new Thread(() -> {
                            database.rewardDao().delete(currentReward);
                            runOnUiThread(() -> {
                                Toast.makeText(this, "تم حذف المكافأة", Toast.LENGTH_SHORT).show();
                                finish();
                            });
                        }).start();
                    })
                    .setNegativeButton("إلغاء", null)
                    .show();
        }
    }
}
