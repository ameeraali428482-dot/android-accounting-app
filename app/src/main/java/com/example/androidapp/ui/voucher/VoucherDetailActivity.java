package com.example.androidapp.ui.voucher;

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
import com.example.androidapp.data.dao.VoucherDao;
import com.example.androidapp.data.entities.Voucher;
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


        voucherDao = new VoucherDao(App.getDatabaseHelper());
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
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.voucher_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(adapter);
    }

    private void loadVoucherData(String id) {
        Voucher voucher = voucherDao.getById(id);
        if (voucher != null) {
            dateEditText.setText(voucher.getDate());
            amountEditText.setText(String.valueOf(voucher.getAmount()));
            descriptionEditText.setText(voucher.getDescription());

            // Set spinner selection
            String[] types = getResources().getStringArray(R.array.voucher_types);
            int spinnerPosition = Arrays.asList(types).indexOf(voucher.getType());
            if (spinnerPosition >= 0) {
                typeSpinner.setSelection(spinnerPosition);
            }
        }
    }

    private void saveVoucher() {
        String date = dateEditText.getText().toString().trim();
        String amountStr = amountEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();
        String type = typeSpinner.getSelectedItem().toString();
        String companyId = sessionManager.getUserDetails().get(SessionManager.KEY_COMPANY_ID);

        if (companyId == null) {
            Toast.makeText(this, "خطأ: لم يتم العثور على معرف الشركة.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (date.isEmpty() || amountStr.isEmpty() || type.isEmpty()) {
            Toast.makeText(this, "الرجاء تعبئة جميع الحقول المطلوبة (التاريخ، المبلغ، النوع).", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount = Double.parseDouble(amountStr);

        Voucher voucher;
        if (voucherId == null) {
            // New voucher
            voucher = new Voucher(UUID.randomUUID().toString(), companyId, type, date, amount, description);
            voucherDao.insert(voucher);
            Toast.makeText(this, "تم إضافة السند بنجاح.", Toast.LENGTH_SHORT).show();
        } else {
            // Existing voucher
            voucher = new Voucher(voucherId, companyId, type, date, amount, description);
            voucherDao.update(voucher);
            Toast.makeText(this, "تم تحديث السند بنجاح.", Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    private void deleteVoucher() {
        if (voucherId != null) {
            voucherDao.delete(voucherId);
            Toast.makeText(this, "تم حذف السند بنجاح.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
