package com.example.androidapp.ui.receipt;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.androidapp.R;
import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.entities.Receipt;
import com.example.androidapp.utils.SessionManager;

import java.util.UUID;
import java.util.concurrent.Executors;

public class ReceiptDetailActivity extends AppCompatActivity {
    private EditText etReceiptNumber;
    private EditText etReceiptDate;
    private EditText etTotalAmount;
    private EditText etCustomerName;
    private Button btnSave;
    private Button btnDelete;

    private AppDatabase database;
    private SessionManager sessionManager;
    private String receiptId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt_detail);

        database = AppDatabase.getInstance(this);
        sessionManager = new SessionManager(this);

        etReceiptNumber = findViewById(R.id.etReceiptNumber);
        etReceiptDate = findViewById(R.id.etReceiptDate);
        etTotalAmount = findViewById(R.id.etTotalAmount);
        etCustomerName = findViewById(R.id.etCustomerName);
        btnSave = findViewById(R.id.btnSave);
        btnDelete = findViewById(R.id.btnDelete);

        receiptId = getIntent().getStringExtra("receipt_id");

        if (receiptId != null) {
            loadReceiptDetails();
        }

        btnSave.setOnClickListener(v -> saveReceipt());
        btnDelete.setOnClickListener(v -> deleteReceipt());
    }

    private void loadReceiptDetails() {
        String companyId = sessionManager.getUserDetails().get(SessionManager.KEY_COMPANY_ID);
        
        database.receiptDao().getReceiptById(receiptId, companyId).observe(this, receipt -> {
            if (receipt != null) {
                etReceiptNumber.setText(receipt.getReceiptNumber());
                etReceiptDate.setText(receipt.getReceiptDate());
                etTotalAmount.setText(String.valueOf(receipt.getTotalAmount()));
                etCustomerName.setText(receipt.getCustomerId());
            }
        });
    }

    private void saveReceipt() {
        String receiptNumber = etReceiptNumber.getText().toString().trim();
        String receiptDate = etReceiptDate.getText().toString().trim();
        String totalAmountStr = etTotalAmount.getText().toString().trim();
        String customerName = etCustomerName.getText().toString().trim();
        String companyId = sessionManager.getUserDetails().get(SessionManager.KEY_COMPANY_ID);

        if (receiptNumber.isEmpty() || totalAmountStr.isEmpty()) {
            Toast.makeText(this, "الرجاء إدخال جميع الحقول المطلوبة", Toast.LENGTH_SHORT).show();
            return;
        }

        double totalAmount = Double.parseDouble(totalAmountStr);

        Executors.newSingleThreadExecutor().execute(() -> {
            if (receiptId == null) {
                Receipt receipt = new Receipt(
                    UUID.randomUUID().toString(),
                    companyId,
                    receiptNumber,
                    customerName,
                    receiptDate,
                    totalAmount,
                    ""
                );
                database.receiptDao().insert(receipt);
            } else {
                database.receiptDao().getReceiptById(receiptId, companyId).observeForever(receipt -> {
                    if (receipt != null) {
                        receipt.setReceiptNumber(receiptNumber);
                        receipt.setReceiptDate(receiptDate);
                        receipt.setTotalAmount(totalAmount);
                        receipt.setCustomerId(customerName);
                        database.receiptDao().update(receipt);
                    }
                });
            }

            runOnUiThread(() -> {
                Toast.makeText(this, "تم حفظ الإيصال بنجاح", Toast.LENGTH_SHORT).show();
                finish();
            });
        });
    }

    private void deleteReceipt() {
        if (receiptId != null) {
            Executors.newSingleThreadExecutor().execute(() -> {
                String companyId = sessionManager.getUserDetails().get(SessionManager.KEY_COMPANY_ID);
                database.receiptDao().getReceiptById(receiptId, companyId).observeForever(receipt -> {
                    if (receipt != null) {
                        database.receiptDao().delete(receipt);
                        runOnUiThread(() -> {
                            Toast.makeText(this, "تم حذف الإيصال بنجاح", Toast.LENGTH_SHORT).show();
                            finish();
                        });
                    }
                });
            });
        }
    }
}
