package com.example.androidapp.ui.trophy;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.androidapp.R;
import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.entities.Trophy;
import com.example.androidapp.utils.SessionManager;
import java.util.UUID;

public class TrophyDetailActivity extends AppCompatActivity {
    private EditText etName, etDescription, etImageUrl, etPointsRequired;
    private AppDatabase database;
    private SessionManager sessionManager;
    private String trophyId;
    private Trophy currentTrophy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trophy_detail);

        database = AppDatabase.getDatabase(this);
        sessionManager = new SessionManager(this);

        initViews();

        trophyId = getIntent().getStringExtra("trophy_id");
        if (trophyId != null) {
            setTitle("تعديل جائزة");
            loadTrophy();
        } else {
            setTitle("جائزة جديدة");
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initViews() {
        etName = findViewById(R.id.etName);
        etDescription = findViewById(R.id.etDescription);
        etImageUrl = findViewById(R.id.etImageUrl);
        etPointsRequired = findViewById(R.id.etPointsRequired);
    }

    private void loadTrophy() {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            Trophy trophy = database.trophyDao().getTrophyByIdSync(trophyId, sessionManager.getCurrentCompanyId());
            runOnUiThread(() -> {
                if (trophy != null) {
                    currentTrophy = trophy;
                    populateFields();
                }
            });
        });
    }

    private void populateFields() {
        etName.setText(currentTrophy.getName());
        etDescription.setText(currentTrophy.getDescription());
        etImageUrl.setText(currentTrophy.getImageUrl());
        etPointsRequired.setText(String.valueOf(currentTrophy.getPointsRequired()));
    }

    private void saveTrophy() {
        String name = etName.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String imageUrl = etImageUrl.getText().toString().trim();
        String pointsRequiredStr = etPointsRequired.getText().toString().trim();

        if (name.isEmpty()) {
            etName.setError("الرجاء إدخال الاسم");
            return;
        }

        if (pointsRequiredStr.isEmpty()) {
            etPointsRequired.setError("الرجاء إدخال النقاط المطلوبة");
            return;
        }

        int pointsRequired = Integer.parseInt(pointsRequiredStr);

        AppDatabase.databaseWriteExecutor.execute(() -> {
            if (trophyId == null) {
                Trophy trophy = new Trophy(
                    UUID.randomUUID().toString(),
                    name,
                    description,
                    sessionManager.getCurrentCompanyId(),
                    pointsRequired,
                    imageUrl
                );
                database.trophyDao().insert(trophy);
            } else {
                Trophy trophy = new Trophy(
                    trophyId,
                    name,
                    description,
                    sessionManager.getCurrentCompanyId(),
                    pointsRequired,
                    imageUrl
                );
                database.trophyDao().update(trophy);
            }

            runOnUiThread(() -> {
                Toast.makeText(this, "تم الحفظ بنجاح", Toast.LENGTH_SHORT).show();
                finish();
            });
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        } else if (id == R.id.action_save) {
            saveTrophy();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
