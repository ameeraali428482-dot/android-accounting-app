package com.example.androidapp.ui.pointtransaction;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.androidapp.R;
import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.dao.PointTransactionDao;
import com.example.androidapp.data.entities.PointTransaction;
import com.example.androidapp.utils.SessionManager;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class PointTransactionDetailActivity extends AppCompatActivity {
    private EditText typeEditText, pointsEditText, dateEditText, descriptionEditText;
    private Button saveButton, deleteButton;
    
    private PointTransactionDao pointTransactionDao;
    private SessionManager sessionManager;
    private String pointTransactionId;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pointtransaction_detail);

        pointTransactionDao = AppDatabase.getDatabase(this).pointTransactionDao();
        sessionManager = new SessionManager(this);

        initViews();
        
        pointTransactionId = getIntent().getStringExtra("pointtransaction_id");
        if (pointTransactionId != null) {
            loadPointTransactionData();
            deleteButton.setVisibility(Button.VISIBLE);
        } else {
            deleteButton.setVisibility(Button.GONE);
        }

        saveButton.setOnClickListener(v -> savePointTransaction());
        deleteButton.setOnClickListener(v -> deletePointTransaction());
    }

    private void initViews() {
        typeEditText = findViewById(R.id.typeEditText);
        pointsEditText = findViewById(R.id.pointsEditText);
        dateEditText = findViewById(R.id.dateEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        saveButton = findViewById(R.id.saveButton);
        deleteButton = findViewById(R.id.deleteButton);
    }

    private void loadPointTransactionData() {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            PointTransaction pt = pointTransactionDao.getPointTransactionByIdSync(pointTransactionId, sessionManager.getCurrentCompanyId());
            runOnUiThread(() -> {
                if (pt != null) {
                    typeEditText.setText(pt.getType());
                    pointsEditText.setText(String.valueOf(pt.getPoints()));
                    if (pt.getDate() != null) {
                        dateEditText.setText(dateFormat.format(pt.getDate()));
                    }
                    if (pt.getDescription() != null) {
                        descriptionEditText.setText(pt.getDescription());
                    }
                }
            });
        });
    }

    private void savePointTransaction() {
        String type = typeEditText.getText().toString().trim();
        String pointsStr = pointsEditText.getText().toString().trim();
        String dateStr = dateEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();

        if (type.isEmpty() || pointsStr.isEmpty() || dateStr.isEmpty()) {
            Toast.makeText(this, "الرجاء ملء جميع الحقول المطلوبة", Toast.LENGTH_SHORT).show();
            return;
        }

        int points = Integer.parseInt(pointsStr);
        String companyId = sessionManager.getCurrentCompanyId();
        String userId = sessionManager.getCurrentUserId();

        AppDatabase.databaseWriteExecutor.execute(() -> {
            try {
                Date date = dateFormat.parse(dateStr);
                PointTransaction pointTransaction;
                
                if (pointTransactionId == null) {
                    pointTransaction = new PointTransaction(
                        UUID.randomUUID().toString(),
                        companyId,
                        type,
                        points,
                        date,
                        userId
                    );
                    pointTransaction.setDescription(description);
                    pointTransactionDao.insert(pointTransaction);
                } else {
                    pointTransaction = new PointTransaction(
                        pointTransactionId,
                        companyId,
                        type,
                        points,
                        date,
                        userId
                    );
                    pointTransaction.setDescription(description);
                    pointTransactionDao.update(pointTransaction);
                }
                
                runOnUiThread(() -> {
                    Toast.makeText(this, "تم الحفظ بنجاح", Toast.LENGTH_SHORT).show();
                    finish();
                });
            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(this, "خطأ: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void deletePointTransaction() {
        if (pointTransactionId != null) {
            AppDatabase.databaseWriteExecutor.execute(() -> {
                PointTransaction pt = pointTransactionDao.getPointTransactionByIdSync(pointTransactionId, sessionManager.getCurrentCompanyId());
                if (pt != null) {
                    pointTransactionDao.delete(pt);
                    runOnUiThread(() -> {
                        Toast.makeText(this, "تم الحذف بنجاح", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                }
            });
        }
    }
}
