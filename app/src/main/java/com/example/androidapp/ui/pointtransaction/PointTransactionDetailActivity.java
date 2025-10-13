package com.example.androidapp.ui.pointtransaction;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.androidapp.R;
import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.entities.PointTransaction;
import com.example.androidapp.utils.SessionManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.Executors;

public class PointTransactionDetailActivity extends AppCompatActivity {
    private EditText etUserId;
    private EditText etTransactionType;
    private EditText etPoints;
    private EditText etDate;
    private EditText etDescription;
    private Button btnSave;
    private Button btnDelete;

    private AppDatabase database;
    private SessionManager sessionManager;
    private String pointTransactionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_point_transaction_detail);

        database = AppDatabase.getInstance(this);
        sessionManager = new SessionManager(this);

        etUserId = findViewById(R.id.etUserId);
        etTransactionType = findViewById(R.id.etTransactionType);
        etPoints = findViewById(R.id.etPoints);
        etDate = findViewById(R.id.etDate);
        etDescription = findViewById(R.id.etDescription);
        btnSave = findViewById(R.id.btnSave);
        btnDelete = findViewById(R.id.btnDelete);

        pointTransactionId = getIntent().getStringExtra("pointtransaction_id");

        if (pointTransactionId != null) {
            loadPointTransactionDetails();
        }

        btnSave.setOnClickListener(v -> savePointTransaction());
        btnDelete.setOnClickListener(v -> deletePointTransaction());
    }

    private void loadPointTransactionDetails() {
        Executors.newSingleThreadExecutor().execute(() -> {
            PointTransaction pointTransaction = database.pointTransactionDao().getPointTransactionByIdSync(pointTransactionId);
            if (pointTransaction != null) {
                runOnUiThread(() -> {
                    etUserId.setText(pointTransaction.getUserId());
                    etTransactionType.setText(pointTransaction.getType());
                    etPoints.setText(String.valueOf(pointTransaction.getPoints()));
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                    etDate.setText(sdf.format(pointTransaction.getDate()));
                    etDescription.setText(pointTransaction.getDescription());
                });
            }
        });
    }

    private void savePointTransaction() {
        String userId = etUserId.getText().toString().trim();
        String transactionType = etTransactionType.getText().toString().trim();
        String pointsStr = etPoints.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String companyId = sessionManager.getUserDetails().get(SessionManager.KEY_COMPANY_ID);

        if (userId.isEmpty() || transactionType.isEmpty() || pointsStr.isEmpty()) {
            Toast.makeText(this, "الرجاء إدخال جميع الحقول المطلوبة", Toast.LENGTH_SHORT).show();
            return;
        }

        int points = Integer.parseInt(pointsStr);

        Executors.newSingleThreadExecutor().execute(() -> {
            if (pointTransactionId == null) {
                PointTransaction pointTransaction = new PointTransaction(
                    UUID.randomUUID().toString(),
                    companyId,
                    transactionType,
                    points,
                    new Date(),
                    userId
                );
                pointTransaction.setDescription(description);
                database.pointTransactionDao().insert(pointTransaction);
            } else {
                PointTransaction pointTransaction = database.pointTransactionDao().getPointTransactionByIdSync(pointTransactionId);
                if (pointTransaction != null) {
                    pointTransaction.setUserId(userId);
                    pointTransaction.setType(transactionType);
                    pointTransaction.setPoints(points);
                    pointTransaction.setDescription(description);
                    database.pointTransactionDao().update(pointTransaction);
                }
            }

            runOnUiThread(() -> {
                Toast.makeText(this, "تم حفظ المعاملة بنجاح", Toast.LENGTH_SHORT).show();
                finish();
            });
        });
    }

    private void deletePointTransaction() {
        if (pointTransactionId != null) {
            Executors.newSingleThreadExecutor().execute(() -> {
                PointTransaction pointTransaction = database.pointTransactionDao().getPointTransactionByIdSync(pointTransactionId);
                if (pointTransaction != null) {
                    database.pointTransactionDao().delete(pointTransaction);
                    runOnUiThread(() -> {
                        Toast.makeText(this, "تم حذف المعاملة بنجاح", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                }
            });
        }
    }
}
