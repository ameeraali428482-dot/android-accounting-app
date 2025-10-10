package com.example.androidapp.ui.customer;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.androidapp.App;
import com.example.androidapp.R;
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

        customerDao = new CustomerDao(App.getDatabaseHelper());
        sessionManager = new SessionManager(this);

        customerNameEditText = findViewById(R.id.customer_name_edit_text);
        customerEmailEditText = findViewById(R.id.customer_email_edit_text);
        customerPhoneEditText = findViewById(R.id.customer_phone_edit_text);
        customerAddressEditText = findViewById(R.id.customer_address_edit_text);
        saveCustomerButton = findViewById(R.id.save_customer_button);
        deleteCustomerButton = findViewById(R.id.delete_customer_button);

        // Check if we are editing an existing customer
        if (getIntent().hasExtra("customerId")) {
            currentCustomerId = getIntent().getStringExtra("customerId");
            loadCustomerDetails(currentCustomerId);
            deleteCustomerButton.setVisibility(View.VISIBLE);
        } else {
            deleteCustomerButton.setVisibility(View.GONE);
        }

        saveCustomerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveCustomer();
            }
        });

        deleteCustomerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteCustomer();
            }
        });
    }

    private void loadCustomerDetails(String customerId) {
        Customer customer = customerDao.getById(customerId);
        if (customer != null) {
            customerNameEditText.setText(customer.getName());
            customerEmailEditText.setText(customer.getEmail());
            customerPhoneEditText.setText(customer.getPhone());
            customerAddressEditText.setText(customer.getAddress());
        } else {
            Toast.makeText(this, "العميل غير موجود.", Toast.LENGTH_SHORT).show();
            finish();
        }
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

        String companyId = sessionManager.getUserDetails().get(SessionManager.KEY_CURRENT_ORG_ID);
        if (companyId == null) {
            Toast.makeText(this, "لا توجد شركة محددة. يرجى تسجيل الدخول واختيار شركة.", Toast.LENGTH_LONG).show();
            return;
        }

        Customer customer;
        if (currentCustomerId == null) {
            // New customer
            customer = new Customer(UUID.randomUUID().toString(), companyId, name, email, phone, address);
            long result = customerDao.insert(customer);
            if (result != -1) {
                Toast.makeText(this, "تم إضافة العميل بنجاح.", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "فشل إضافة العميل.", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Existing customer
            customer = new Customer(currentCustomerId, companyId, name, email, phone, address);
            int result = customerDao.update(customer);
            if (result > 0) {
                Toast.makeText(this, "تم تحديث العميل بنجاح.", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "فشل تحديث العميل.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void deleteCustomer() {
        if (currentCustomerId != null) {
            int result = customerDao.delete(currentCustomerId);
            if (result > 0) {
                Toast.makeText(this, "تم حذف العميل بنجاح.", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "فشل حذف العميل.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

