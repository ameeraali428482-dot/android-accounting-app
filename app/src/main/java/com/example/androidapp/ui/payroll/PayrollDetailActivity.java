package com.example.androidapp.ui.payroll;

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
import com.example.androidapp.data.dao.EmployeeDao;
import com.example.androidapp.data.dao.PayrollDao;
import com.example.androidapp.data.entities.Employee;
import com.example.androidapp.data.entities.Payroll;
import com.example.androidapp.utils.SessionManager;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;






public class PayrollDetailActivity extends AppCompatActivity {

    private EditText dateEditText, amountEditText, notesEditText;
    private Spinner employeeSpinner;
    private Button saveButton, deleteButton;
    private PayrollDao payrollDao;
    private EmployeeDao employeeDao;
    private SessionManager sessionManager;
    private String payrollId = null;
    private List<Employee> employees;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payroll_detail);

        dateEditText = // TODO: Fix findViewById;
        amountEditText = // TODO: Fix findViewById;
        notesEditText = // TODO: Fix findViewById;
        employeeSpinner = // TODO: Fix findViewById;
        saveButton = // TODO: Fix findViewById;
        deleteButton = // TODO: Fix findViewById;

        payrollDao = new PayrollDao(App.getDatabaseHelper());
        employeeDao = new EmployeeDao(App.getDatabaseHelper());
        sessionManager = new SessionManager(this);

        loadEmployeesIntoSpinner();

        if (getIntent().hasExtra("payroll_id")) {
            payrollId = getIntent().getStringExtra("payroll_id");
            loadPayrollData(payrollId);
            deleteButton.setVisibility(View.VISIBLE);
        } else {
            deleteButton.setVisibility(View.GONE);
        }

        saveButton.setOnClickListener(v -> savePayroll());
        deleteButton.setOnClickListener(v -> deletePayroll());
    }

    private void loadEmployeesIntoSpinner() {
        String companyId = sessionManager.getUserDetails().get(SessionManager.KEY_COMPANY_ID);
        if (companyId == null) {
            Toast.makeText(this, "خطأ: لم يتم العثور على معرف الشركة.", Toast.LENGTH_SHORT).show();
            return;
        }
        employees = employeeDao.getEmployeesByCompanyId(companyId);
        List<String> employeeNames = new ArrayList<>();
        for (Employee employee : employees) {
            employeeNames.add(employee.getName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, employeeNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        employeeSpinner.setAdapter(adapter);
    }

    private void loadPayrollData(String id) {
        Payroll payroll = payrollDao.getById(id);
        if (payroll != null) {
            dateEditText.setText(payroll.getDate());
            amountEditText.setText(String.valueOf(payroll.getAmount()));
            notesEditText.setText(payroll.getNotes());

            // Set selected employee in spinner
            for (int i = 0; i < employees.size(); i++) {
                if (employees.get(i).getId().equals(payroll.getEmployeeId())) {
                    employeeSpinner.setSelection(i);
                    break;
                }
            }
        }
    }

    private void savePayroll() {
        String date = dateEditText.getText().toString().trim();
        String amountStr = amountEditText.getText().toString().trim();
        String notes = notesEditText.getText().toString().trim();
        String companyId = sessionManager.getUserDetails().get(SessionManager.KEY_COMPANY_ID);

        if (companyId == null) {
            Toast.makeText(this, "خطأ: لم يتم العثور على معرف الشركة.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (date.isEmpty() || amountStr.isEmpty() || employeeSpinner.getSelectedItem() == null) {
            Toast.makeText(this, "الرجاء تعبئة جميع الحقول المطلوبة.", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount = Double.parseDouble(amountStr);
        String selectedEmployeeId = employees.get(employeeSpinner.getSelectedItemPosition()).getId();

        Payroll payroll;
        if (payrollId == null) {
            // New payroll
            payroll = new Payroll(UUID.randomUUID().toString(), companyId, selectedEmployeeId, date, amount, notes);
            payrollDao.insert(payroll);
            Toast.makeText(this, "تم إضافة كشف المرتب بنجاح.", Toast.LENGTH_SHORT).show();
        } else {
            // Existing payroll
            payroll = new Payroll(payrollId, companyId, selectedEmployeeId, date, amount, notes);
            payrollDao.update(payroll);
            Toast.makeText(this, "تم تحديث كشف المرتب بنجاح.", Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    private void deletePayroll() {
        if (payrollId != null) {
            payrollDao.delete(payrollId);
            Toast.makeText(this, "تم حذف كشف المرتب بنجاح.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
