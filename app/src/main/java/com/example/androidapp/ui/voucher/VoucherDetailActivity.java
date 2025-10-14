package com.example.androidapp.ui.voucher;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.androidapp.R;
import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.dao.VoucherDao;
import com.example.androidapp.data.entities.Voucher;
import com.example.androidapp.database.DatabaseContract.VoucherType;
import com.example.androidapp.utils.SessionManager;
import java.util.Arrays;
import java.util.UUID;

public class VoucherDetailActivity extends AppCompatActivity {

    private EditText dateEditText, amountEditText, descriptionEditText;
    private Spinner typeSpinner;
    private Button saveButton, deleteButton;
    private VoucherDao voucherDao;
    private SessionManager sessionManager;
    private String voucherId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voucher_detail);

        dateEditText = findViewById(R.id.voucher_date_edit_text);
        amountEditText = findViewById(R.id.voucher_amount_edit_text);
        descriptionEditText = findViewById(R.id.voucher_description_edit_text);
        typeSpinner = findViewById(R.id.voucher_type_spinner);
        saveButton = findViewById(R.id.save_voucher_button);
        deleteButton = findViewById(R.id.delete_voucher_button);

        voucherDao = AppDatabase.getDatabase(this).voucherDao();
        sessionManager = new SessionManager(this);

        setupTypeSpinner();

        if (getIntent().hasExtra("voucher_id")) {
            voucherId = getIntent().getStringExtra("voucher_id");
            loadVoucherData(voucherId);
            deleteButton.setVisibility(View.VISIBLE);
        } else {
            deleteButton.setVisibility(View.GONE);
        }

        saveButton.setOnClickListener(v -> saveVoucher());
        deleteButton.setOnClickListener(v -> deleteVoucher());
    }

    private void setupTypeSpinner() {
        ArrayAdapter<VoucherType> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, VoucherType.values());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(adapter);
    }

    private void loadVoucherData(String id) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            Voucher voucher = voucherDao.getById(id);
            runOnUiThread(() -> {
                if (voucher != null) {
                    dateEditText.setText(voucher.getDate());
                    amountEditText.setText(String.valueOf(voucher.getAmount()));
                    descriptionEditText.setText(voucher.getDescription());
                    typeSpinner.setSelection(voucher.getType().ordinal());
                }
            });
        });
    }

    private void saveVoucher() {
        String date = dateEditText.getText().toString().trim();
        String amountStr = amountEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();
        VoucherType type = (VoucherType) typeSpinner.getSelectedItem();
        String companyId = sessionManager.getCurrentCompanyId();

        if (companyId == null) {
            Toast.makeText(this, "خطأ: لم يتم العثور على معرف الشركة.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (date.isEmpty() || amountStr.isEmpty()) {
            Toast.makeText(this, "الرجاء تعبئة التاريخ والمبلغ.", Toast.LENGTH_SHORT).show();
            return;
        }

        float amount = Float.parseFloat(amountStr);

        AppDatabase.databaseWriteExecutor.execute(() -> {
            if (voucherId == null) {
                Voucher voucher = new Voucher(UUID.randomUUID().toString(), companyId, type, date, amount, description, null);
                voucherDao.insert(voucher);
            } else {
                Voucher voucher = voucherDao.getById(voucherId);
                if (voucher != null) {
                    voucher.setDate(date);
                    voucher.setAmount(amount);
                    voucher.setDescription(description);
                    voucher.setType(type);
                    voucherDao.update(voucher);
                }
            }
            runOnUiThread(() -> {
                Toast.makeText(this, "تم الحفظ بنجاح.", Toast.LENGTH_SHORT).show();
                finish();
            });
        });
    }

    private void deleteVoucher() {
        if (voucherId != null) {
            AppDatabase.databaseWriteExecutor.execute(() -> {
                Voucher voucher = voucherDao.getById(voucherId);
                if (voucher != null) {
                    voucherDao.delete(voucher);
                }
                runOnUiThread(() -> {
                    Toast.makeText(this, "تم الحذف بنجاح.", Toast.LENGTH_SHORT).show();
                    finish();
                });
            });
        }
    }
}
