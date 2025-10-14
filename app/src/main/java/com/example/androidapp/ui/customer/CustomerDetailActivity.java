package com.example.androidapp.ui.customer;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.androidapp.R;
import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.dao.CustomerDao;
import com.example.androidapp.data.entities.Customer;
import com.example.androidapp.utils.SessionManager;
import java.util.UUID;

public class CustomerDetailActivity extends AppCompatActivity {

    private EditText customerNameEditText, customerEmailEditText, customerPhoneEditText, customerAddressEditText;
    private Button saveCustomerButton, deleteCustomerButton;
    private CustomerDao customerDao;
    private SessionManager sessionManager;
    private String currentCustomerId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_detail);

        customerDao = AppDatabase.getDatabase(this).customerDao();
        sessionManager = new SessionManager(this);

        customerNameEditText = findViewById(R.id.customer_name_edit_text);
        customerEmailEditText = findViewById(R.id.customer_email_edit_text);
        customerPhoneEditText = findViewById(R.id.customer_phone_edit_text);
        customerAddressEditText = findViewById(R.id.customer_address_edit_text);
        saveCustomerButton = findViewById(R.id.save_customer_button);
        deleteCustomerButton = findViewById(R.id.delete_customer_button);

        if (getIntent().hasExtra("customerId")) {
            currentCustomerId = getIntent().getStringExtra("customerId");
            loadCustomerDetails(currentCustomerId);
            deleteCustomerButton.setVisibility(View.VISIBLE);
        } else {
            deleteCustomerButton.setVisibility(View.GONE);
        }

        saveCustomerButton.setOnClickListener(v -> saveCustomer());
        deleteCustomerButton.setOnClickListener(v -> deleteCustomer());
    }

    private void loadCustomerDetails(String customerId) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            Customer customer = customerDao.getById(customerId);
            runOnUiThread(() -> {
                if (customer != null) {
                    customerNameEditText.setText(customer.getName());
                    customerEmailEditText.setText(customer.getEmail());
                    customerPhoneEditText.setText(customer.getPhone());
                    customerAddressEditText.setText(customer.getAddress());
                } else {
                    Toast.makeText(this, "العميل غير موجود.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        });
    }

    private void saveCustomer() {
        String name = customerNameEditText.getText().toString().trim();
        String email = customerEmailEditText.getText().toString().trim();
        String phone = customerPhoneEditText.getText().toString().trim();
        String address = customerAddressEditText.getText().toString().trim();

        if (name.isEmpty()) {
            customerNameEditText.setError("اسم العميل مطلوب.");
            customerNameEditText.requestFocus();
            return;
        }

        String companyId = sessionManager.getCurrentCompanyId();
        if (companyId == null) {
            Toast.makeText(this, "لا توجد شركة محددة.", Toast.LENGTH_LONG).show();
            return;
        }

        AppDatabase.databaseWriteExecutor.execute(() -> {
            if (currentCustomerId == null) {
                Customer customer = new Customer(UUID.randomUUID().toString(), companyId, name, email, phone, address);
                customerDao.insert(customer);
            } else {
                Customer customer = customerDao.getById(currentCustomerId);
                if (customer != null) {
                    customer.setName(name);
                    customer.setEmail(email);
                    customer.setPhone(phone);
                    customer.setAddress(address);
                    customerDao.update(customer);
                }
            }
            runOnUiThread(() -> {
                Toast.makeText(this, "تم الحفظ بنجاح.", Toast.LENGTH_SHORT).show();
                finish();
            });
        });
    }

    private void deleteCustomer() {
        if (currentCustomerId != null) {
            AppDatabase.databaseWriteExecutor.execute(() -> {
                Customer customer = customerDao.getById(currentCustomerId);
                if (customer != null) {
                    customerDao.delete(customer);
                }
                runOnUiThread(() -> {
                    Toast.makeText(this, "تم الحذف بنجاح.", Toast.LENGTH_SHORT).show();
                    finish();
                });
            });
        }
    }
}
