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
    private RewardViewModel viewModel;
    private SessionManager sessionManager;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private Date selectedDate;
    private String rewardId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reward_detail);

        viewModel = new ViewModelProvider(this).get(RewardViewModel.class);
        sessionManager = new SessionManager(this);

        initViews();
        setupDatePicker();
        loadRewardData();
        setupSaveButton();
    }

    private void initViews() {
        etName = findViewById(R.id.et_name);
        etDescription = findViewById(R.id.et_description);
        etPointsRequired = findViewById(R.id.et_points_required);
        etValidUntil = findViewById(R.id.et_valid_until);
        cbIsActive = findViewById(R.id.cb_is_active);
        btnSave = findViewById(R.id.btn_save);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
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
        rewardId = getIntent().getStringExtra("reward_id");
        if (rewardId != null) {
            setTitle("تعديل المكافأة");
            viewModel.getRewardById(rewardId, sessionManager.getCurrentCompanyId())
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

        if (name.isEmpty() || pointsRequiredStr.isEmpty()) {
            Toast.makeText(this, "الاسم والنقاط مطلوبة", Toast.LENGTH_SHORT).show();
            return;
        }

        int pointsRequired = Integer.parseInt(pointsRequiredStr);

        if (currentReward == null) {
            Reward newReward = new Reward(sessionManager.getCurrentCompanyId(), name, description, pointsRequired, selectedDate, isActive);
            viewModel.insert(newReward);
        } else {
            currentReward.setName(name);
            currentReward.setDescription(description);
            currentReward.setPointsRequired(pointsRequired);
            currentReward.setValidUntil(selectedDate);
            currentReward.setActive(isActive);
            viewModel.update(currentReward);
        }
        Toast.makeText(this, "تم حفظ المكافأة بنجاح", Toast.LENGTH_SHORT).show();
        finish();
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
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            finish();
            return true;
        } else if (itemId == R.id.action_delete) {
            deleteReward();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteReward() {
        if (currentReward != null) {
            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("حذف المكافأة")
                    .setMessage("هل أنت متأكد من حذف هذه المكافأة؟")
                    .setPositiveButton("حذف", (dialog, which) -> {
                        viewModel.delete(currentReward);
                        Toast.makeText(this, "تم حذف المكافأة", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .setNegativeButton("إلغاء", null)
                    .show();
        }
    }
}
