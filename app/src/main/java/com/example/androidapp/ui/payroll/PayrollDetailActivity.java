package com.example.androidapp.ui.payroll;

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
import com.example.androidapp.data.dao.EmployeeDao;
import com.example.androidapp.data.dao.PayrollDao;
import com.example.androidapp.data.entities.Employee;
import com.example.androidapp.data.entities.Payroll;
import com.example.androidapp.utils.SessionManager;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.Calendar;

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

        dateEditText = findViewById(R.id.payroll_date_edit_text);
        amountEditText = findViewById(R.id.payroll_amount_edit_text);
        notesEditText = findViewById(R.id.payroll_notes_edit_text);
        employeeSpinner = findViewById(R.id.employee_spinner);
        saveButton = findViewById(R.id.save_payroll_button);
        deleteButton = findViewById(R.id.delete_payroll_button);

        payrollDao = AppDatabase.getDatabase(this).payrollDao();
        employeeDao = AppDatabase.getDatabase(this).employeeDao();
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
        String companyId = sessionManager.getCurrentCompanyId();
        if (companyId == null) {
            Toast.makeText(this, "خطأ: لم يتم العثور على معرف الشركة.", Toast.LENGTH_SHORT).show();
            return;
        }
        AppDatabase.databaseWriteExecutor.execute(() -> {
            employees = employeeDao.getEmployeesByCompanyId(companyId);
            List<String> employeeNames = new ArrayList<>();
            for (Employee employee : employees) {
                employeeNames.add(employee.getName());
            }
            runOnUiThread(() -> {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, employeeNames);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                employeeSpinner.setAdapter(adapter);
            });
        });
    }

    private void loadPayrollData(String id) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            Payroll payroll = payrollDao.getById(id);
            runOnUiThread(() -> {
                if (payroll != null) {
                    dateEditText.setText(payroll.getDate());
                    amountEditText.setText(String.valueOf(payroll.getAmount()));
                    notesEditText.setText(payroll.getNotes());

                    for (int i = 0; i < employees.size(); i++) {
                        if (employees.get(i).getId().equals(payroll.getEmployeeId())) {
                            employeeSpinner.setSelection(i);
                            break;
                        }
                    }
                }
            });
        });
    }

    private void savePayroll() {
        String date = dateEditText.getText().toString().trim();
        String amountStr = amountEditText.getText().toString().trim();
        String notes = notesEditText.getText().toString().trim();
        String companyId = sessionManager.getCurrentCompanyId();

        if (companyId == null || date.isEmpty() || amountStr.isEmpty() || employeeSpinner.getSelectedItem() == null) {
            Toast.makeText(this, "الرجاء تعبئة جميع الحقول المطلوبة.", Toast.LENGTH_SHORT).show();
            return;
        }

        float amount = Float.parseFloat(amountStr);
        String selectedEmployeeId = employees.get(employeeSpinner.getSelectedItemPosition()).getId();
        Calendar cal = Calendar.getInstance();

        AppDatabase.databaseWriteExecutor.execute(() -> {
            if (payrollId == null) {
                Payroll payroll = new Payroll(UUID.randomUUID().toString(), companyId, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, "PROCESSED", amount, 0, 0, amount, null);
                payroll.setEmployeeId(selectedEmployeeId);
                payroll.setDate(date);
                payroll.setNotes(notes);
                payrollDao.insert(payroll);
            } else {
                Payroll payroll = payrollDao.getById(payrollId);
                if (payroll != null) {
                    payroll.setEmployeeId(selectedEmployeeId);
                    payroll.setDate(date);
                    payroll.setAmount(amount);
                    payroll.setNotes(notes);
                    payrollDao.update(payroll);
                }
            }
            runOnUiThread(() -> {
                Toast.makeText(this, "تم الحفظ بنجاح.", Toast.LENGTH_SHORT).show();
                finish();
            });
        });
    }

    private void deletePayroll() {
        if (payrollId != null) {
            AppDatabase.databaseWriteExecutor.execute(() -> {
                Payroll payroll = payrollDao.getById(payrollId);
                if (payroll != null) {
                    payrollDao.delete(payroll);
                }
                runOnUiThread(() -> {
                    Toast.makeText(this, "تم الحذف بنجاح.", Toast.LENGTH_SHORT).show();
                    finish();
                });
            });
        }
    }
}
