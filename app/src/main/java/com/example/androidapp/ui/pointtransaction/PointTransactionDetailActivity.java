package com.example.androidapp.ui.pointtransaction;

import java.util.Date;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.androidapp.App;
import com.example.androidapp.R;
import com.example.androidapp.data.dao.PointTransactionDao;
import com.example.androidapp.data.entities.PointTransaction;
import com.example.androidapp.utils.SessionManager;
import java.util.Arrays;
import java.util.UUID;






public class PointTransactionDetailActivity extends AppCompatActivity {

    private EditText pointsEditText, dateEditText;
    private Spinner typeSpinner;
    private Button saveButton, deleteButton;
    private PointTransactionDao pointTransactionDao;
    private SessionManager sessionManager;
    private String pointTransactionId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_point_transaction_detail);

        pointsEditText = findViewById(R.id.point_transaction_points_edit_text);
        dateEditText = findViewById(R.id.point_transaction_date_edit_text);
        typeSpinner = findViewById(R.id.point_transaction_type_spinner);
        saveButton = findViewById(R.id.save_point_transaction_button);
        deleteButton = findViewById(R.id.delete_point_transaction_button);

        pointTransactionDao = AppDatabase.getInstance(this).pointTransactionDao();
        sessionManager = new SessionManager(this);

        setupTypeSpinner();

        if (getIntent().hasExtra("point_transaction_id")) {
            pointTransactionId = getIntent().getStringExtra("point_transaction_id");
            loadPointTransactionData(pointTransactionId);
            deleteButton.setVisibility(View.VISIBLE);
        } else {
            deleteButton.setVisibility(View.GONE);
        }

        saveButton.setOnClickListener(v -> savePointTransaction());
        deleteButton.setOnClickListener(v -> deletePointTransaction());
    }

    private void setupTypeSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.point_transaction_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(adapter);
    }

    private void loadPointTransactionData(String id) {
        PointTransaction pointTransaction = pointTransactionDao.getById(id);
        if (pointTransaction != null) {
            pointsEditText.setText(String.valueOf(pointTransaction.getPoints()));
            dateEditText.setText(pointTransaction.getDate());

            // Set spinner selection
            String[] types = getResources().getStringArray(R.array.point_transaction_types);
            int spinnerPosition = Arrays.asList(types).indexOf(pointTransaction.getType());
            if (spinnerPosition >= 0) {
                typeSpinner.setSelection(spinnerPosition);
            }
        }
    }

    private void savePointTransaction() {
        String pointsStr = pointsEditText.getText().toString().trim();
        String date = dateEditText.getText().toString().trim();
        String type = typeSpinner.getSelectedItem().toString();
        String companyId = sessionManager.getUserDetails().get(SessionManager.KEY_COMPANY_ID);

        if (companyId == null) {
            Toast.makeText(this, "خطأ: لم يتم العثور على معرف الشركة.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (pointsStr.isEmpty() || date.isEmpty() || type.isEmpty()) {
            Toast.makeText(this, "الرجاء تعبئة جميع الحقول المطلوبة.", Toast.LENGTH_SHORT).show();
            return;
        }

        int points = Integer.parseInt(pointsStr);

        PointTransaction pointTransaction;
        if (pointTransactionId == null) {
            // New point transaction
            pointTransaction = new PointTransaction(UUID.randomUUID().toString(), companyId, type, points, new Date(), "DEFAULT_USER");
            pointTransactionDao.insert(pointTransaction);
            Toast.makeText(this, "تم إضافة معاملة النقاط بنجاح.", Toast.LENGTH_SHORT).show();
        } else {
            // Existing point transaction
            pointTransaction = new PointTransaction(pointTransactionId, companyId, type, points, date);
            pointTransactionDao.update(pointTransaction);
            Toast.makeText(this, "تم تحديث معاملة النقاط بنجاح.", Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    private void deletePointTransaction() {
        if (pointTransactionId != null) {
            pointTransactionDao.delete(pointTransactionId);
            Toast.makeText(this, "تم حذف معاملة النقاط بنجاح.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
