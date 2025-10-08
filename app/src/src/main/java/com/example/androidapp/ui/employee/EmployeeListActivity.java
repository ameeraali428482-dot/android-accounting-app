package com.example.androidapp.ui.employee;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidapp.App;
import com.example.androidapp.R;
import com.example.androidapp.data.dao.EmployeeDao;
import com.example.androidapp.models.Employee;
import com.example.androidapp.ui.common.GenericAdapter;
import com.example.androidapp.utils.SessionManager;

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

        employeeDao = new EmployeeDao(App.getDatabaseHelper());
        sessionManager = new SessionManager(this);

        findViewById(R.id.add_employee_button).setOnClickListener(v -> {
            Intent intent = new Intent(EmployeeListActivity.this, EmployeeDetailActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadEmployees();
    }

    private void loadEmployees() {
        String companyId = sessionManager.getUserDetails().get(SessionManager.KEY_COMPANY_ID);
        if (companyId == null) {
            // Handle error: no company ID found
            return;
        }

        List<Employee> employees = employeeDao.getEmployeesByCompanyId(companyId);

        adapter = new GenericAdapter<Employee>(employees) {
            @Override
            protected int getLayoutResId() {
                return R.layout.employee_list_row;
            }

            @Override
            protected void bindView(View itemView, Employee employee) {
                TextView employeeName = itemView.findViewById(R.id.employee_name);
                TextView employeePosition = itemView.findViewById(R.id.employee_position);
                TextView employeePhone = itemView.findViewById(R.id.employee_phone);

                employeeName.setText(employee.getName());
                employeePosition.setText(employee.getPosition());
                employeePhone.setText(employee.getPhone());

                itemView.setOnClickListener(v -> {
                    Intent intent = new Intent(EmployeeListActivity.this, EmployeeDetailActivity.class);
                    intent.putExtra("employee_id", employee.getId());
                    startActivity(intent);
                });
            }
        };
        employeeRecyclerView.setAdapter(adapter);
    }
}
