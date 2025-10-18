package com.example.androidapp.ui.supplier;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.androidapp.R;
import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.dao.SupplierDao;
import com.example.androidapp.data.entities.Supplier;
import com.example.androidapp.utils.SessionManager;
import java.util.UUID;

public class SupplierDetailActivity extends AppCompatActivity {

    private EditText supplierNameEditText, supplierEmailEditText, supplierPhoneEditText, supplierAddressEditText;
    private Button saveSupplierButton, deleteSupplierButton;
    private SupplierDao supplierDao;
    private SessionManager sessionManager;
    private String currentSupplierId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supplier_detail);

        supplierDao = AppDatabase.getDatabase(this).supplierDao();
        sessionManager = new SessionManager(this);

        supplierNameEditText = findViewById(R.id.supplier_name_edit_text);
        supplierEmailEditText = findViewById(R.id.supplier_email_edit_text);
        supplierPhoneEditText = findViewById(R.id.supplier_phone_edit_text);
        supplierAddressEditText = findViewById(R.id.supplier_address_edit_text);
        saveSupplierButton = findViewById(R.id.save_supplier_button);
        deleteSupplierButton = findViewById(R.id.delete_supplier_button);

        if (getIntent().hasExtra("supplierId")) {
            currentSupplierId = getIntent().getStringExtra("supplierId");
            loadSupplierDetails(currentSupplierId);
            deleteSupplierButton.setVisibility(View.VISIBLE);
        } else {
            deleteSupplierButton.setVisibility(View.GONE);
        }

        saveSupplierButton.setOnClickListener(v -> saveSupplier());
        deleteSupplierButton.setOnClickListener(v -> deleteSupplier());
    }

    private void loadSupplierDetails(String supplierId) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            Supplier supplier = supplierDao.getById(supplierId);
            runOnUiThread(() -> {
                if (supplier != null) {
                    supplierNameEditText.setText(supplier.getName());
                    supplierEmailEditText.setText(supplier.getEmail());
                    supplierPhoneEditText.setText(supplier.getPhone());
                    supplierAddressEditText.setText(supplier.getAddress());
                } else {
                    Toast.makeText(this, "المورد غير موجود.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        });
    }

    private void saveSupplier() {
        String name = supplierNameEditText.getText().toString().trim();
        String email = supplierEmailEditText.getText().toString().trim();
        String phone = supplierPhoneEditText.getText().toString().trim();
        String address = supplierAddressEditText.getText().toString().trim();

        if (name.isEmpty()) {
            supplierNameEditText.setError("اسم المورد مطلوب.");
            supplierNameEditText.requestFocus();
            return;
        }

        String companyId = sessionManager.getCurrentCompanyId();
        if (companyId == null) {
            Toast.makeText(this, "لا توجد شركة محددة.", Toast.LENGTH_LONG).show();
            return;
        }

        AppDatabase.databaseWriteExecutor.execute(() -> {
            if (currentSupplierId == null) {
                Supplier supplier = new Supplier(UUID.randomUUID().toString(), companyId, name, email, phone, address);
                supplierDao.insert(supplier);
            } else {
                Supplier supplier = supplierDao.getById(currentSupplierId);
                if (supplier != null) {
                    supplier.setName(name);
                    supplier.setEmail(email);
                    supplier.setPhone(phone);
                    supplier.setAddress(address);
                    supplierDao.update(supplier);
                }
            }
            runOnUiThread(() -> {
                Toast.makeText(this, "تم الحفظ بنجاح.", Toast.LENGTH_SHORT).show();
                finish();
            });
        });
    }

    private void deleteSupplier() {
        if (currentSupplierId != null) {
            AppDatabase.databaseWriteExecutor.execute(() -> {
                Supplier supplier = supplierDao.getById(currentSupplierId);
                if (supplier != null) {
                    supplierDao.delete(supplier);
                }
                runOnUiThread(() -> {
                    Toast.makeText(this, "تم الحذف بنجاح.", Toast.LENGTH_SHORT).show();
                    finish();
                });
            });
        }
    }
}
