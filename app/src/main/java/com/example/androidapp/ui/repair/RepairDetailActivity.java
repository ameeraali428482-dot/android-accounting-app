package com.example.androidapp.ui.repair;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.androidapp.R;
import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.entities.Repair;
import com.example.androidapp.utils.SessionManager;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.Executors;

public class RepairDetailActivity extends AppCompatActivity {
    private EditText etTitle, etDescription, etAssignedTo, etTotalCost;
    private Button btnRequestDate, btnCompletionDate;
    private Spinner spinnerStatus;
    private AppDatabase database;
    private SessionManager sessionManager;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private Date requestDate, completionDate;
    private String repairId = null;
    private Repair currentRepair;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repair_detail);

        database = AppDatabase.getDatabase(this);
        sessionManager = new SessionManager(this);

        initViews();
        setupSpinner();
        
        repairId = getIntent().getStringExtra("repair_id");
        if (repairId != null) {
            setTitle("تعديل الإصلاح");
            loadRepair();
        } else {
            setTitle("إضافة إصلاح جديد");
            requestDate = new Date();
            btnRequestDate.setText(dateFormat.format(requestDate));
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initViews() {
        etTitle = findViewById(R.id.et_repair_title);
        etDescription = findViewById(R.id.et_repair_description);
        etAssignedTo = findViewById(R.id.et_repair_assigned_to);
        etTotalCost = findViewById(R.id.et_repair_total_cost);
        btnRequestDate = findViewById(R.id.btn_repair_request_date);
        btnCompletionDate = findViewById(R.id.btn_repair_completion_date);
        spinnerStatus = findViewById(R.id.spinner_repair_status);

        btnRequestDate.setOnClickListener(v -> showDatePicker(true));
        btnCompletionDate.setOnClickListener(v -> showDatePicker(false));
    }

    private void setupSpinner() {
        String[] statuses = {"Pending", "In Progress", "Completed", "Cancelled"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, statuses);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(adapter);
    }

    private void showDatePicker(boolean isRequestDate) {
        Calendar calendar = Calendar.getInstance();
        Date initialDate = isRequestDate ? requestDate : completionDate;
        if (initialDate != null) {
            calendar.setTime(initialDate);
        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    Calendar selectedCal = Calendar.getInstance();
                    selectedCal.set(year, month, dayOfMonth);
                    if (isRequestDate) {
                        requestDate = selectedCal.getTime();
                        btnRequestDate.setText(dateFormat.format(requestDate));
                    } else {
                        completionDate = selectedCal.getTime();
                        btnCompletionDate.setText(dateFormat.format(completionDate));
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void loadRepair() {
        database.repairDao().getRepairById(repairId, sessionManager.getCurrentCompanyId())
                .observe(this, repair -> {
                    if (repair != null) {
                        currentRepair = repair;
                        populateFields();
                    }
                });
    }

    private void populateFields() {
        etTitle.setText(currentRepair.getTitle());
        etDescription.setText(currentRepair.getIssueDescription());
        etAssignedTo.setText(currentRepair.getAssignedTo());
        etTotalCost.setText(String.valueOf(currentRepair.getTotalCost()));
        
        requestDate = currentRepair.getRequestDate();
        btnRequestDate.setText(dateFormat.format(requestDate));
        
        if (currentRepair.getCompletionDate() != null) {
            completionDate = currentRepair.getCompletionDate();
            btnCompletionDate.setText(dateFormat.format(completionDate));
        }

        String[] statuses = {"Pending", "In Progress", "Completed", "Cancelled"};
        for (int i = 0; i < statuses.length; i++) {
            if (statuses[i].equals(currentRepair.getStatus())) {
                spinnerStatus.setSelection(i);
                break;
            }
        }
    }

    private void saveRepair() {
        String title = etTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String assignedTo = etAssignedTo.getText().toString().trim();
        String totalCostStr = etTotalCost.getText().toString().trim();
        String status = spinnerStatus.getSelectedItem().toString();

        if (title.isEmpty()) {
            etTitle.setError("العنوان مطلوب");
            return;
        }

        final float finalTotalCost;
        if (!totalCostStr.isEmpty()) {
            try {
                finalTotalCost = Float.parseFloat(totalCostStr);
            } catch (NumberFormatException e) {
                etTotalCost.setError("قيمة غير صحيحة");
                return;
            }
        } else {
            finalTotalCost = 0;
        }

        Executors.newSingleThreadExecutor().execute(() -> {
            if (repairId == null) {
                Repair repair = new Repair(sessionManager.getCurrentCompanyId(), title, description, requestDate, completionDate, status, assignedTo, finalTotalCost);
                database.repairDao().insert(repair);
            } else {
                currentRepair.setTitle(title);
                currentRepair.setIssueDescription(description);
                currentRepair.setRequestDate(requestDate);
                currentRepair.setCompletionDate(completionDate);
                currentRepair.setStatus(status);
                currentRepair.setAssignedTo(assignedTo);
                currentRepair.setTotalCost(finalTotalCost);
                database.repairDao().update(currentRepair);
            }

            runOnUiThread(() -> {
                Toast.makeText(this, "تم حفظ الإصلاح بنجاح", Toast.LENGTH_SHORT).show();
                finish();
            });
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            finish();
            return true;
        } else if (itemId == R.id.action_save) {
            saveRepair();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
