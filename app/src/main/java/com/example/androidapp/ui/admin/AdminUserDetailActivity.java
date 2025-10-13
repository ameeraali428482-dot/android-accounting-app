package com.example.androidapp.ui.admin;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.androidapp.R;
import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.entities.User;

public class AdminUserDetailActivity extends AppCompatActivity {
    private TextView tvUserName, tvUserEmail;
    private AppDatabase database;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_user_detail);

        database = AppDatabase.getDatabase(this);
        userId = getIntent().getStringExtra("user_id");

        initViews();
        if (userId != null) {
            loadUser();
        }
    }

    private void initViews() {
        tvUserName = findViewById(R.id.tvUserName);
        tvUserEmail = findViewById(R.id.tvUserEmail);
    }

    private void loadUser() {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            User user = database.userDao().getUserByIdSync(userId);
            runOnUiThread(() -> {
                if (user != null) {
                    if (tvUserName != null) tvUserName.setText(user.getName());
                    if (tvUserEmail != null) tvUserEmail.setText(user.getEmail());
                }
            });
        });
    }
}
