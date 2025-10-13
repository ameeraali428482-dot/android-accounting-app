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
import java.util.concurrent.Executors;

public class TrophyDetailActivity extends AppCompatActivity {
    private EditText etTrophyName;
    private EditText etTrophyDescription;
    private EditText etPointsRequired;
    private EditText etImageUrl;

    private AppDatabase database;
    private SessionManager sessionManager;
    private String trophyId;
    private Trophy currentTrophy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trophy_detail);

        database = AppDatabase.getInstance(this);
        sessionManager = new SessionManager(this);

        etTrophyName = findViewById(R.id.etTrophyName);
        etTrophyDescription = findViewById(R.id.etTrophyDescription);
        etPointsRequired = findViewById(R.id.etPointsRequired);
        etImageUrl = findViewById(R.id.etImageUrl);

        trophyId = getIntent().getStringExtra("trophy_id");

        if (trophyId != null) {
            loadTrophyDetails();
        }
    }

    private void loadTrophyDetails() {
        String companyId = sessionManager.getUserDetails().get(SessionManager.KEY_COMPANY_ID);
        
        database.trophyDao().getTrophyById(trophyId, companyId)
                .observe(this, trophy -> {
                    if (trophy != null) {
                        currentTrophy = trophy;
                        etTrophyName.setText(trophy.getTrophyName());
                        etTrophyDescription.setText(trophy.getTrophyDescription());
                        etPointsRequired.setText(String.valueOf(trophy.getPointsRequired()));
                        etImageUrl.setText(trophy.getImageUrl());
                    }
                });
    }

    private void saveTrophy() {
        String name = etTrophyName.getText().toString().trim();
        String description = etTrophyDescription.getText().toString().trim();
        String pointsStr = etPointsRequired.getText().toString().trim();
        String imageUrl = etImageUrl.getText().toString().trim();
        String companyId = sessionManager.getUserDetails().get(SessionManager.KEY_COMPANY_ID);

        if (name.isEmpty() || pointsStr.isEmpty()) {
            Toast.makeText(this, "الرجاء إدخال الاسم والنقاط المطلوبة", Toast.LENGTH_SHORT).show();
            return;
        }

        int pointsRequired = Integer.parseInt(pointsStr);

        Executors.newSingleThreadExecutor().execute(() -> {
            if (trophyId == null) {
                Trophy trophy = new Trophy(
                    UUID.randomUUID().toString(),
                    companyId,
                    name,
                    description,
                    pointsRequired,
                    imageUrl
                );
                database.trophyDao().insert(trophy);
            } else {
                if (currentTrophy != null) {
                    currentTrophy.setTrophyName(name);
                    currentTrophy.setTrophyDescription(description);
                    currentTrophy.setPointsRequired(pointsRequired);
                    currentTrophy.setImageUrl(imageUrl);
                    database.trophyDao().update(currentTrophy);
                }
            }

            runOnUiThread(() -> {
                Toast.makeText(this, "تم حفظ الجائزة بنجاح", Toast.LENGTH_SHORT).show();
                finish();
            });
        });
    }

    private void deleteTrophy() {
        if (currentTrophy != null) {
            Executors.newSingleThreadExecutor().execute(() -> {
                database.trophyDao().delete(currentTrophy);
                runOnUiThread(() -> {
                    Toast.makeText(this, "تم حذف الجائزة بنجاح", Toast.LENGTH_SHORT).show();
                    finish();
                });
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_trophy_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        
        if (id == R.id.action_save) {
            saveTrophy();
            return true;
        } else if (id == R.id.action_delete) {
            deleteTrophy();
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }
}
