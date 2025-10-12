package com.example.androidapp.ui.employee;

import java.util.Date;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.androidapp.App;
import com.example.androidapp.R;
import com.example.androidapp.data.dao.EmployeeDao;
import com.example.androidapp.data.entities.Employee;
import com.example.androidapp.utils.SessionManager;
import java.util.UUID;






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


        employeeDao = new EmployeeDao(App.getDatabaseHelper());
        sessionManager = new SessionManager(this);

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
        Employee employee = employeeDao.getById(id);
        if (employee != null) {
            nameEditText.setText(employee.getName());
            emailEditText.setText(employee.getEmail());
            phoneEditText.setText(employee.getPhone());
            positionEditText.setText(employee.getPosition());
            salaryEditText.setText(String.valueOf(employee.getSalary()));
            hireDateEditText.setText(employee.getHireDate());
        }
    }

    private void saveEmployee() {
        String name = nameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();
        String position = positionEditText.getText().toString().trim();
        double salary = Double.parseDouble(salaryEditText.getText().toString().trim());
        String hireDate = hireDateEditText.getText().toString().trim();
        String companyId = sessionManager.getUserDetails().get(SessionManager.KEY_COMPANY_ID);

        if (companyId == null) {
            Toast.makeText(this, "خطأ: لم يتم العثور على معرف الشركة.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || position.isEmpty() || hireDate.isEmpty()) {
            Toast.makeText(this, "الرجاء تعبئة جميع الحقول.", Toast.LENGTH_SHORT).show();
            return;
        }

        Employee employee;
        if (employeeId == null) {
            // New employee
            employee = new Employee(UUID.randomUUID().toString(), companyId, name, email, phone, position, salary, hireDate);
            employeeDao.insert(employee);
            Toast.makeText(this, "تم إضافة الموظف بنجاح.", Toast.LENGTH_SHORT).show();
        } else {
            // Existing employee
            employee = new Employee(employeeId, companyId, name, email, phone, position, salary, hireDate);
            employeeDao.update(employee);
            Toast.makeText(this, "تم تحديث الموظف بنجاح.", Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    private void deleteEmployee() {
        if (employeeId != null) {
            employeeDao.delete(employeeId);
            Toast.makeText(this, "تم حذف الموظف بنجاح.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
