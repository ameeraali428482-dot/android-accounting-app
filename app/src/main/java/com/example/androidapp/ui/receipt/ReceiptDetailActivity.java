package com.example.androidapp.ui.receipt;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.androidapp.R;
import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.dao.ReceiptDao;
import com.example.androidapp.data.entities.Receipt;
import com.example.androidapp.utils.SessionManager;
import java.util.UUID;

public class ReceiptDetailActivity extends AppCompatActivity {
    private EditText etReceiptNumber;
    private EditText etReceiptDate;
    private EditText etTotalAmount;
    private EditText etCustomerName;
    private Button btnSave;
    private Button btnDelete;

    private ReceiptDao receiptDao;
    private SessionManager sessionManager;
    private String receiptId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt_detail);

        database = AppDatabase.getInstance(this);
        sessionManager = new SessionManager(this);

        etReceiptNumber = findViewById(R.id.et_receipt_number);
        etReceiptDate = findViewById(R.id.et_receipt_date);
        etTotalAmount = findViewById(R.id.et_total_amount);
        etCustomerName = findViewById(R.id.et_customer_name);
        btnSave = findViewById(R.id.btn_save_receipt);
        btnDelete = findViewById(R.id.btn_delete_receipt);

        receiptId = getIntent().getStringExtra("receipt_id");

        if (receiptId != null) {
            loadReceiptDetails();
        }

        btnSave.setOnClickListener(v -> saveReceipt());
        btnDelete.setOnClickListener(v -> deleteReceipt());
    }

    private void loadReceiptDetails() {
        String companyId = sessionManager.getCurrentCompanyId();
        AppDatabase.databaseWriteExecutor.execute(() -> {
            Receipt receipt = receiptDao.getReceiptById(receiptId, companyId);
            runOnUiThread(() -> {
                if (receipt != null) {
                    etReceiptNumber.setText(receipt.getReferenceNumber());
                    etReceiptDate.setText(receipt.getReceiptDate());
                    etTotalAmount.setText(String.valueOf(receipt.getAmount()));
                    etCustomerName.setText(receipt.getPayerId());
                }
            });
        });
    }

    private void saveReceipt() {
        String receiptNumber = etReceiptNumber.getText().toString().trim();
        String receiptDate = etReceiptDate.getText().toString().trim();
        String totalAmountStr = etTotalAmount.getText().toString().trim();
        String customerName = etCustomerName.getText().toString().trim();
        String companyId = sessionManager.getCurrentCompanyId();

        if (receiptNumber.isEmpty() || totalAmountStr.isEmpty()) {
            Toast.makeText(this, "الرجاء إدخال جميع الحقول المطلوبة", Toast.LENGTH_SHORT).show();
            return;
        }

        float totalAmount = Float.parseFloat(totalAmountStr);

        AppDatabase.databaseWriteExecutor.execute(() -> {
            if (receiptId == null) {
                Receipt receipt = new Receipt(UUID.randomUUID().toString(), companyId, receiptDate, customerName, "Customer", totalAmount, "CASH", receiptNumber, "", null);
                receiptDao.insert(receipt);
            } else {
                Receipt receipt = receiptDao.getReceiptById(receiptId, companyId);
                if (receipt != null) {
                    receipt.setReferenceNumber(receiptNumber);
                    receipt.setReceiptDate(receiptDate);
                    receipt.setAmount(totalAmount);
                    receipt.setPayerId(customerName);
                    receiptDao.update(receipt);
                }
            }
            runOnUiThread(() -> {
                Toast.makeText(this, "تم حفظ الإيصال بنجاح", Toast.LENGTH_SHORT).show();
                finish();
            });
        });
    }

    private void deleteReceipt() {
        if (receiptId != null) {
            AppDatabase.databaseWriteExecutor.execute(() -> {
                Receipt receipt = receiptDao.getReceiptById(receiptId, sessionManager.getCurrentCompanyId());
                if (receipt != null) {
                    receiptDao.delete(receipt);
                    runOnUiThread(() -> {
                        Toast.makeText(this, "تم حذف الإيصال بنجاح", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                }
            });
        }
    }
}
