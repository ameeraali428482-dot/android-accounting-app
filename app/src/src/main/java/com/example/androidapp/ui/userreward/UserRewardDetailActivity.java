package com.example.androidapp.ui.userreward;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.androidapp.R;
import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.entities.Reward;
import com.example.androidapp.data.entities.User;
import com.example.androidapp.data.entities.UserReward;
import com.example.androidapp.utils.SessionManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class UserRewardDetailActivity extends AppCompatActivity {
    private Spinner spinnerUser, spinnerReward;
    private EditText etRedemptionDate;
    private CheckBox cbIsRedeemed;
    private Button btnSave;
    private UserReward currentUserReward;
    private AppDatabase database;
    private SessionManager sessionManager;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private Date selectedDate;
    private List<User> users = new ArrayList<>();
    private List<Reward> rewards = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_reward_detail);

        database = AppDatabase.getDatabase(this);
        sessionManager = new SessionManager(this);

        initViews();
        setupDatePicker();
        loadSpinnerData();
        loadUserRewardData();
        setupSaveButton();
    }

    private void initViews() {
        spinnerUser = findViewById(R.id.spinner_user);
        spinnerReward = findViewById(R.id.spinner_reward);
        etRedemptionDate = findViewById(R.id.et_redemption_date);
        cbIsRedeemed = findViewById(R.id.cb_is_redeemed);
        btnSave = findViewById(R.id.btn_save);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setupDatePicker() {
        etRedemptionDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            if (selectedDate != null) {
                calendar.setTime(selectedDate);
            }

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    this,
                    (view, year, month, dayOfMonth) -> {
                        calendar.set(year, month, dayOfMonth);
                        selectedDate = calendar.getTime();
                        etRedemptionDate.setText(dateFormat.format(selectedDate));
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.show();
        });
    }

    private void loadSpinnerData() {
        // Load users
        database.userDao().getAllUsers().observe(this, userList -> {
            if (userList != null) {
                users.clear();
                users.addAll(userList);
                
                List<String> userNames = new ArrayList<>();
                for (User user : users) {
                    userNames.add(user.getName());
                }
                
                ArrayAdapter<String> userAdapter = new ArrayAdapter<>(this,
                        android.R.layout.simple_spinner_item, userNames);
                userAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerUser.setAdapter(userAdapter);
            }
        });

        // Load rewards
        database.rewardDao().getAllRewards(sessionManager.getCurrentCompanyId()).observe(this, rewardList -> {
            if (rewardList != null) {
                rewards.clear();
                rewards.addAll(rewardList);
                
                List<String> rewardNames = new ArrayList<>();
                for (Reward reward : rewards) {
                    rewardNames.add(reward.getName());
                }
                
                ArrayAdapter<String> rewardAdapter = new ArrayAdapter<>(this,
                        android.R.layout.simple_spinner_item, rewardNames);
                rewardAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerReward.setAdapter(rewardAdapter);
            }
        });
    }

    private void loadUserRewardData() {
        int userRewardId = getIntent().getIntExtra("user_reward_id", -1);
        if (userRewardId != -1) {
            setTitle("تعديل مكافأة المستخدم");
            database.userRewardDao().getUserRewardById(userRewardId, sessionManager.getCurrentCompanyId())
                    .observe(this, userReward -> {
                        if (userReward != null) {
                            currentUserReward = userReward;
                            populateFields();
                        }
                    });
        } else {
            setTitle("إضافة مكافأة مستخدم جديدة");
            currentUserReward = null;
        }
    }

    private void populateFields() {
        if (currentUserReward != null) {
            // Set user spinner selection
            for (int i = 0; i < users.size(); i++) {
                if (users.get(i).getId() == currentUserReward.getUserId()) {
                    spinnerUser.setSelection(i);
                    break;
                }
            }

            // Set reward spinner selection
            for (int i = 0; i < rewards.size(); i++) {
                if (rewards.get(i).getId() == currentUserReward.getRewardId()) {
                    spinnerReward.setSelection(i);
                    break;
                }
            }

            cbIsRedeemed.setChecked(currentUserReward.isRedeemed());

            if (currentUserReward.getRedemptionDate() != null) {
                selectedDate = currentUserReward.getRedemptionDate();
                etRedemptionDate.setText(dateFormat.format(selectedDate));
            }
        }
    }

    private void setupSaveButton() {
        btnSave.setOnClickListener(v -> saveUserReward());
    }

    private void saveUserReward() {
        if (spinnerUser.getSelectedItemPosition() == -1 || users.isEmpty()) {
            Toast.makeText(this, "يرجى اختيار المستخدم", Toast.LENGTH_SHORT).show();
            return;
        }

        if (spinnerReward.getSelectedItemPosition() == -1 || rewards.isEmpty()) {
            Toast.makeText(this, "يرجى اختيار المكافأة", Toast.LENGTH_SHORT).show();
            return;
        }

        User selectedUser = users.get(spinnerUser.getSelectedItemPosition());
        Reward selectedReward = rewards.get(spinnerReward.getSelectedItemPosition());
        boolean isRedeemed = cbIsRedeemed.isChecked();

        new Thread(() -> {
            try {
                if (currentUserReward == null) {
                    // إنشاء مكافأة مستخدم جديدة
                    UserReward newUserReward = new UserReward(
                            selectedUser.getId(),
                            selectedReward.getId(),
                            sessionManager.getCurrentCompanyId(),
                            selectedDate,
                            isRedeemed
                    );
                    database.userRewardDao().insert(newUserReward);
                } else {
                    // تحديث مكافأة المستخدم الحالية
                    currentUserReward.setUserId(selectedUser.getId());
                    currentUserReward.setRewardId(selectedReward.getId());
                    currentUserReward.setRedemptionDate(selectedDate);
                    currentUserReward.setRedeemed(isRedeemed);
                    database.userRewardDao().update(currentUserReward);
                }

                runOnUiThread(() -> {
                    Toast.makeText(this, "تم حفظ مكافأة المستخدم بنجاح", Toast.LENGTH_SHORT).show();
                    finish();
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "خطأ في حفظ مكافأة المستخدم: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (currentUserReward != null) {
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
                deleteUserReward();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void deleteUserReward() {
        if (currentUserReward != null) {
            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("حذف مكافأة المستخدم")
                    .setMessage("هل أنت متأكد من حذف مكافأة المستخدم هذه؟")
                    .setPositiveButton("حذف", (dialog, which) -> {
                        new Thread(() -> {
                            database.userRewardDao().delete(currentUserReward);
                            runOnUiThread(() -> {
                                Toast.makeText(this, "تم حذف مكافأة المستخدم", Toast.LENGTH_SHORT).show();
                                finish();
                            });
                        }).start();
                    })
                    .setNegativeButton("إلغاء", null)
                    .show();
        }
    }
}
