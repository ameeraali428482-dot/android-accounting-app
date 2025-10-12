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
import com.example.androidapp.data.entities.Employee;
import com.example.androidapp.ui.common.GenericAdapter;
import com.example.androidapp.utils.SessionManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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

        employeeRecyclerView = findViewById(R.id.employeeRecyclerView);
        employeeRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        employeeDao = new EmployeeDao(App.getDatabaseHelper());
        sessionManager = new SessionManager(this);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EmployeeListActivity.this, EmployeeDetailActivity.class);
                startActivity(intent);
            }
        });

        loadEmployees();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadEmployees();
    }

    private void loadEmployees() {
        String companyId = sessionManager.getUserDetails().get(SessionManager.KEY_COMPANY_ID);
        if (companyId == null) {
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
                TextView employeeName = itemView.findViewById(R.id.employeeName);
                TextView employeePosition = itemView.findViewById(R.id.employeePosition);
                TextView employeePhone = itemView.findViewById(R.id.employeePhone);

                employeeName.setText(employee.getName());
                employeePosition.setText(employee.getPosition());
                employeePhone.setText(employee.getPhone());

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(EmployeeListActivity.this, EmployeeDetailActivity.class);
                        intent.putExtra("employee_id", employee.getId());
                        startActivity(intent);
                    }
                });
            }
        };
        employeeRecyclerView.setAdapter(adapter);
    }
}
