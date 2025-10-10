package com.example.androidapp.ui.trophy;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.androidapp.R;
import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.entities.Trophy;
import com.example.androidapp.utils.SessionManager;





public class TrophyDetailActivity extends AppCompatActivity {
    private EditText etName, etDescription, etImageUrl, etPointsRequired;
    private ImageView ivTrophyPreview;
    private AppDatabase database;
    private SessionManager sessionManager;
    private int trophyId = -1;
    private Trophy currentTrophy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trophy_detail);

        database = AppDatabase.getDatabase(this);
        sessionManager = new SessionManager(this);

        initViews();
        
        trophyId = getIntent().getIntExtra("trophy_id", -1);
        if (trophyId != -1) {
            setTitle("تعديل الكأس");
            loadTrophy();
        } else {
            setTitle("إضافة كأس جديد");
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initViews() {
        etName = // TODO: Fix findViewById;
        etDescription = // TODO: Fix findViewById;
        etImageUrl = // TODO: Fix findViewById;
        etPointsRequired = // TODO: Fix findViewById;
        ivTrophyPreview = // TODO: Fix findViewById;

        etImageUrl.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                loadImagePreview(etImageUrl.getText().toString());
            }
        });
    }

    private void loadImagePreview(String imageUrl) {
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_trophy_placeholder)
                    .error(R.drawable.ic_trophy_placeholder)
                    .into(ivTrophyPreview);
        } else {
            ivTrophyPreview.setImageResource(R.drawable.ic_trophy_placeholder);
        }
    }

    private void loadTrophy() {
        database.trophyDao().getTrophyById(trophyId, sessionManager.getCurrentCompanyId())
                .observe(this, trophy -> {
                    if (trophy != null) {
                        currentTrophy = trophy;
                        populateFields();
                    }
                });
    }

    private void populateFields() {
        etName.setText(currentTrophy.getName());
        etDescription.setText(currentTrophy.getDescription());
        etImageUrl.setText(currentTrophy.getImageUrl());
        etPointsRequired.setText(String.valueOf(currentTrophy.getPointsRequired()));
        loadImagePreview(currentTrophy.getImageUrl());
    }

    private void saveTrophy() {
        String name = etName.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String imageUrl = etImageUrl.getText().toString().trim();
        String pointsRequiredStr = etPointsRequired.getText().toString().trim();

        if (name.isEmpty()) {
            etName.setError("اسم الكأس مطلوب");
            return;
        }
        if (pointsRequiredStr.isEmpty()) {
            etPointsRequired.setError("النقاط المطلوبة مطلوبة");
            return;
        }

        int pointsRequired = Integer.parseInt(pointsRequiredStr);

        AppDatabase.databaseWriteExecutor.execute(() -> {
            if (trophyId == -1) {
                // Create new trophy
                Trophy trophy = new Trophy(
                        sessionManager.getCurrentCompanyId(),
                        name,
                        description,
                        imageUrl,
                        pointsRequired
                );
                database.trophyDao().insert(trophy);
            } else {
                // Update existing trophy
                currentTrophy.setName(name);
                currentTrophy.setDescription(description);
                currentTrophy.setImageUrl(imageUrl);
                currentTrophy.setPointsRequired(pointsRequired);
                database.trophyDao().update(currentTrophy);
            }

            runOnUiThread(() -> {
                Toast.makeText(this, "تم حفظ الكأس بنجاح", Toast.LENGTH_SHORT).show();
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
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_save:
                saveTrophy();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
