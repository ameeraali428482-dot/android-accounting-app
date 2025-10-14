package com.example.androidapp.ui.employee;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.androidapp.R;
import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.dao.EmployeeDao;
import com.example.androidapp.data.entities.Employee;
import com.example.androidapp.ui.common.GenericAdapter;
import com.example.androidapp.utils.SessionManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class EmployeeListActivity extends AppCompatActivity {

    private RecyclerView employeeRecyclerView;
    private EmployeeDao employeeDao;
    private SessionManager sessionManager;
    private GenericAdapter<Employee> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_list);

        employeeRecyclerView = findViewById(R.id.employee_recycler_view);
        employeeRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        employeeDao = AppDatabase.getDatabase(this).employeeDao();
        sessionManager = new SessionManager(this);

        FloatingActionButton fab = findViewById(R.id.add_employee_button);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(EmployeeListActivity.this, EmployeeDetailActivity.class);
            startActivity(intent);
        });

        loadEmployees();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadEmployees();
    }

    private void loadEmployees() {
        String companyId = sessionManager.getCurrentCompanyId();
        if (companyId == null) return;

        adapter = new GenericAdapter<>(new ArrayList<>(), item -> {
            Intent intent = new Intent(EmployeeListActivity.this, EmployeeDetailActivity.class);
            intent.putExtra("employee_id", item.getId());
            startActivity(intent);
        });
        employeeRecyclerView.setAdapter(adapter);

        AppDatabase.databaseWriteExecutor.execute(() -> {
            List<Employee> employees = employeeDao.getEmployeesByCompanyId(companyId);
            runOnUiThread(() -> adapter.updateData(employees));
        });
    }
}
