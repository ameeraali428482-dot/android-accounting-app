package com.example.androidapp.ui.supplier;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.androidapp.App;
import com.example.androidapp.R;
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

        supplierDao = new SupplierDao(App.getDatabaseHelper());
        sessionManager = new SessionManager(this);


        // Check if we are editing an existing supplier
        if (getIntent().hasExtra("supplierId")) {
            currentSupplierId = getIntent().getStringExtra("supplierId");
            loadSupplierDetails(currentSupplierId);
            deleteSupplierButton.setVisibility(View.VISIBLE);
        } else {
            deleteSupplierButton.setVisibility(View.GONE);
        }

        saveSupplierButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSupplier();
            }
        });

        deleteSupplierButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteSupplier();
            }
        });
    }

    private void loadSupplierDetails(String supplierId) {
        Supplier supplier = supplierDao.getById(supplierId);
        if (supplier != null) {
            supplierNameEditText.setText(supplier.getName());
            supplierEmailEditText.setText(supplier.getEmail());
            supplierPhoneEditText.setText(supplier.getPhone());
            supplierAddressEditText.setText(supplier.getAddress());
        } else {
            Toast.makeText(this, "المورد غير موجود.", Toast.LENGTH_SHORT).show();
            finish();
        }
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

        String companyId = sessionManager.getUserDetails().get(SessionManager.KEY_CURRENT_ORG_ID);
        if (companyId == null) {
            Toast.makeText(this, "لا توجد شركة محددة. يرجى تسجيل الدخول واختيار شركة.", Toast.LENGTH_LONG).show();
            return;
        }

        Supplier supplier;
        if (currentSupplierId == null) {
            // New supplier
            supplier = new Supplier(UUID.randomUUID().toString(), companyId, name, email, phone, address);
            long result = supplierDao.insert(supplier);
            if (result != -1) {
                Toast.makeText(this, "تم إضافة المورد بنجاح.", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "فشل إضافة المورد.", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Existing supplier
            supplier = new Supplier(currentSupplierId, companyId, name, email, phone, address);
            int result = supplierDao.update(supplier);
            if (result > 0) {
                Toast.makeText(this, "تم تحديث المورد بنجاح.", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "فشل تحديث المورد.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void deleteSupplier() {
        if (currentSupplierId != null) {
            int result = supplierDao.delete(currentSupplierId);
            if (result > 0) {
                Toast.makeText(this, "تم حذف المورد بنجاح.", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "فشل حذف المورد.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

