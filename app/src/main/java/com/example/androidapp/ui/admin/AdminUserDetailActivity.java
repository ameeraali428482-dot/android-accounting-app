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
    private TextView tvName, tvEmail;
    private Button btnDeact, btnAct;
    private AppDatabase db;
    private SessionManager sm;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_user_detail);

        db = AppDatabase.getInstance(this);
        sm = new SessionManager(this);

        tvName  = findViewById(R.id.tv_admin_user_detail_name);
        tvEmail = findViewById(R.id.tv_admin_user_detail_email);
        btnDeact= findViewById(R.id.btnDeactivate);
        btnAct  = findViewById(R.id.btnActivate);

        userId = getIntent().getStringExtra("user_id");
        if (userId != null) load();

        btnDeact.setOnClickListener(v -> toggle(false));
        btnAct  .setOnClickListener(v -> toggle(true));
    }

    private void load() {
        db.userDao().getUserById(userId).observe(this, u -> {
            if (u != null) {
                tvName .setText(u.getUsername());
                tvEmail.setText(u.getEmail());
            }
        });
    }

    private void toggle(boolean active) {
        Executors.newSingleThreadExecutor().execute(() -> {
            User u = db.userDao().getUserByIdSync(userId);
            if (u != null) {
                u.setActive(active); // Corrected from setIsActive to setActive
                db.userDao().update(u);
            }
            runOnUiThread(() -> Toast.makeText(this, active ? "تم التفعيل" : "تم التعطيل", Toast.LENGTH_SHORT).show());
        });
    }
}
