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
    private EditText etUserId, etType, etPoints, etDate, etDesc;
    private Button btnSave, btnDel;
    private AppDatabase db;
    private SessionManager sm;
    private String trxId;
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_point_transaction_detail);

        db = AppDatabase.getInstance(this);
        sm = new SessionManager(this);

        etUserId = findViewById(R.id.etUserId);
        etType   = findViewById(R.id.etTransactionType);
        etPoints = findViewById(R.id.etPoints);
        etDate   = findViewById(R.id.etDate);
        etDesc   = findViewById(R.id.etDescription);
        btnSave  = findViewById(R.id.btnSave);
        btnDel   = findViewById(R.id.btnDelete);

        trxId = getIntent().getStringExtra("pointtransaction_id");
        if (trxId != null) load();

        btnSave.setOnClickListener(v -> save());
        btnDel .setOnClickListener(v -> delete());
    }

    private void load() {
        db.pointTransactionDao().getPointTransactionById(trxId).observe(this, p -> {
            if (p != null) {
                etUserId.setText(p.getUserId());
                etType  .setText(p.getTransactionType());
                etPoints.setText(String.valueOf(p.getPoints()));
                etDate  .setText(sdf.format(p.getTransactionDate()));
                etDesc  .setText(p.getDescription());
            }
        });
    }

    private void save() {
        String userId = etUserId.getText().toString().trim();
        String type   = etType  .getText().toString().trim();
        String ptsStr = etPoints.getText().toString().trim();
        String desc   = etDesc  .getText().toString().trim();
        String companyId = sm.getUserDetails().get(SessionManager.KEY_COMPANY_ID);

        if (userId.isEmpty() || type.isEmpty() || ptsStr.isEmpty()) {
            Toast.makeText(this, "أكمل الحقول المطلوبة", Toast.LENGTH_SHORT).show();
            return;
        }

        int pts = Integer.parseInt(ptsStr);

        Executors.newSingleThreadExecutor().execute(() -> {
            if (trxId == null) {
                PointTransaction p = new PointTransaction();
                p.setId(UUID.randomUUID().toString());
                p.setCompanyId(companyId);
                p.setUserId(userId);
                p.setTransactionType(type);
                p.setPoints(pts);
                p.setTransactionDate(new Date());
                p.setDescription(desc);
                db.pointTransactionDao().insert(p);
            } else {
                PointTransaction p = db.pointTransactionDao().getPointTransactionByIdSync(trxId);
                if (p != null) {
                    p.setUserId(userId);
                    p.setTransactionType(type);
                    p.setPoints(pts);
                    p.setDescription(desc);
                    db.pointTransactionDao().update(p);
                }
            }
            runOnUiThread(() -> { Toast.makeText(this, "تم الحفظ", Toast.LENGTH_SHORT).show(); finish(); });
        });
    }

    private void delete() {
        if (trxId != null) {
            Executors.newSingleThreadExecutor().execute(() -> {
                PointTransaction p = db.pointTransactionDao().getPointTransactionByIdSync(trxId);
                if (p != null) db.pointTransactionDao().delete(p);
                runOnUiThread(() -> { Toast.makeText(this, "تم الحذف", Toast.LENGTH_SHORT).show(); finish(); });
            });
        }
    }
}
