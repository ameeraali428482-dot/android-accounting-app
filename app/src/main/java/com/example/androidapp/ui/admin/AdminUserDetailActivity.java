package com.example.androidapp.ui.admin;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.androidapp.R;
import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.entities.User;
import com.example.androidapp.utils.SessionManager;

import java.util.concurrent.Executors;

public class AdminUserDetailActivity extends AppCompatActivity {
    private TextView tvUserName;
    private TextView tvUserEmail;
    private Button btnDeactivate;
    private Button btnActivate;

    private AppDatabase database;
    private SessionManager sessionManager;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_user_detail);

        database = AppDatabase.getInstance(this);
        sessionManager = new SessionManager(this);

        tvUserName = findViewById(R.id.tvUserNameDisplay);
        tvUserEmail = findViewById(R.id.tvUserEmailDisplay);
        btnDeactivate = findViewById(R.id.btnDeactivate);
        btnActivate = findViewById(R.id.btnActivate);

        userId = getIntent().getStringExtra("user_id");

        if (userId != null) {
            loadUserDetails();
        }

        btnDeactivate.setOnClickListener(v -> deactivateUser());
        btnActivate.setOnClickListener(v -> activateUser());
    }

    private void loadUserDetails() {
        String companyId = sessionManager.getUserDetails().get(SessionManager.KEY_COMPANY_ID);
        
        database.userDao().getUserById(userId, companyId).observe(this, user -> {
            if (user != null) {
                tvUserName.setText(user.getUsername());
                tvUserEmail.setText(user.getEmail());
            }
        });
    }

    private void deactivateUser() {
        Executors.newSingleThreadExecutor().execute(() -> {
            String companyId = sessionManager.getUserDetails().get(SessionManager.KEY_COMPANY_ID);
            database.userDao().getUserById(userId, companyId).observeForever(user -> {
                if (user != null) {
                    user.setIsActive(false);
                    database.userDao().update(user);
                    runOnUiThread(() -> {
                        Toast.makeText(this, "تم تعطيل المستخدم", Toast.LENGTH_SHORT).show();
                    });
                }
            });
        });
    }

    private void activateUser() {
        Executors.newSingleThreadExecutor().execute(() -> {
            String companyId = sessionManager.getUserDetails().get(SessionManager.KEY_COMPANY_ID);
            database.userDao().getUserById(userId, companyId).observeForever(user -> {
                if (user != null) {
                    user.setIsActive(true);
                    database.userDao().update(user);
                    runOnUiThread(() -> {
                        Toast.makeText(this, "تم تفعيل المستخدم", Toast.LENGTH_SHORT).show();
                    });
                }
            });
        });
    }
}
