package com.example.androidapp.ui.employee;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.androidapp.R;
import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.dao.EmployeeDao;
import com.example.androidapp.data.entities.Employee;
import com.example.androidapp.utils.SessionManager;

import java.util.UUID;
import java.util.concurrent.Executors;

public class EmployeeDetailActivity extends AppCompatActivity {
    private EditText nameEditText, emailEditText, phoneEditText, positionEditText, salaryEditText, hireDateEditText;
    private Button saveButton, deleteButton;
    private EmployeeDao employeeDao;
    private SessionManager sessionManager;
    private String employeeId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_detail);

        employeeDao = AppDatabase.getDatabase(this).employeeDao();
        sessionManager = new SessionManager(this);

        nameEditText = findViewById(R.id.employee_name_edit_text);
        emailEditText = findViewById(R.id.employee_email_edit_text);
        phoneEditText = findViewById(R.id.employee_phone_edit_text);
        positionEditText = findViewById(R.id.employee_position_edit_text);
        salaryEditText = findViewById(R.id.employee_salary_edit_text);
        hireDateEditText = findViewById(R.id.employee_hire_date_edit_text);
        saveButton = findViewById(R.id.save_employee_button);
        deleteButton = findViewById(R.id.delete_employee_button);

        if (getIntent().hasExtra("employee_id")) {
            employeeId = getIntent().getStringExtra("employee_id");
            loadEmployeeData(employeeId);
            deleteButton.setVisibility(View.VISIBLE);
        } else {
            deleteButton.setVisibility(View.GONE);
        }

        saveButton.setOnClickListener(v -> saveEmployee());
        deleteButton.setOnClickListener(v -> deleteEmployee());
    }

    private void loadEmployeeData(String id) {
        Executors.newSingleThreadExecutor().execute(() -> {
            Employee employee = employeeDao.getById(id);
            if (employee != null) {
                runOnUiThread(() -> {
                    nameEditText.setText(employee.getName());
                    emailEditText.setText(employee.getEmail());
                    phoneEditText.setText(employee.getPhone());
                    positionEditText.setText(employee.getPosition());
                    salaryEditText.setText(String.valueOf(employee.getSalary()));
                    hireDateEditText.setText(employee.getHireDate());
                });
            }
        });
    }

    private void saveEmployee() {
        String name = nameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();
        String position = positionEditText.getText().toString().trim();
        String hireDate = hireDateEditText.getText().toString().trim();
        double salary;
        try {
            salary = Double.parseDouble(salaryEditText.getText().toString().trim());
        } catch (NumberFormatException e) {
            salaryEditText.setError("الرجاء إدخال قيمة صحيحة للراتب");
            return;
        }

        String companyId = sessionManager.getCurrentCompanyId();
        if (companyId == null) {
            Toast.makeText(this, "خطأ: لم يتم العثور على معرف الشركة.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || position.isEmpty() || hireDate.isEmpty()) {
            Toast.makeText(this, "الرجاء تعبئة جميع الحقول.", Toast.LENGTH_SHORT).show();
            return;
        }

        Executors.newSingleThreadExecutor().execute(() -> {
            Employee employee;
            if (employeeId == null) {
                employee = new Employee(UUID.randomUUID().toString(), companyId, name, email, phone, position, salary, hireDate);
                employeeDao.insert(employee);
            } else {
                employee = employeeDao.getById(employeeId);
                if (employee != null) {
                    employee = new Employee(employeeId, companyId, name, email, phone, position, salary, hireDate);
                    employeeDao.update(employee);
                }
            }
            runOnUiThread(() -> {
                Toast.makeText(this, "تم الحفظ بنجاح.", Toast.LENGTH_SHORT).show();
                finish();
            });
        });
    }

    private void deleteEmployee() {
        if (employeeId != null) {
            Executors.newSingleThreadExecutor().execute(() -> {
                employeeDao.delete(employeeId);
                runOnUiThread(() -> {
                    Toast.makeText(this, "تم حذف الموظف بنجاح.", Toast.LENGTH_SHORT).show();
                    finish();
                });
            });
        }
    }
}
