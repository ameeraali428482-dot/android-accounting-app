package com.example.androidapp.ui.payroll;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.androidapp.R;
import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.dao.PayrollDao;
import com.example.androidapp.data.entities.Payroll;
import com.example.androidapp.ui.common.GenericAdapter;
import com.example.androidapp.utils.SessionManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;

public class PayrollListActivity extends AppCompatActivity {

    private RecyclerView payrollRecyclerView;
    private PayrollDao payrollDao;
    private SessionManager sessionManager;
    private GenericAdapter<Payroll> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payroll_list);

        payrollRecyclerView = findViewById(R.id.payroll_recycler_view);
        payrollRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        payrollDao = AppDatabase.getDatabase(this).payrollDao();
        sessionManager = new SessionManager(this);

        FloatingActionButton fab = findViewById(R.id.add_payroll_button);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(PayrollListActivity.this, PayrollDetailActivity.class);
            startActivity(intent);
        });

        loadPayrolls();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPayrolls();
    }

    private void loadPayrolls() {
        String companyId = sessionManager.getCurrentCompanyId();
        if (companyId == null) return;

        adapter = new GenericAdapter<Payroll>(new ArrayList<>(), item -> {
            Intent intent = new Intent(PayrollListActivity.this, PayrollDetailActivity.class);
            intent.putExtra("payroll_id", item.getId());
            startActivity(intent);
        }) {
            @Override
            protected int getLayoutResId() {
                return R.layout.payroll_list_row;
            }

            @Override
            protected void bindView(View itemView, Payroll payroll) {
                TextView payrollId = itemView.findViewById(R.id.payroll_id);
                TextView payrollEmployeeId = itemView.findViewById(R.id.payroll_employee_id);
                TextView payrollDate = itemView.findViewById(R.id.payroll_date);
                TextView payrollAmount = itemView.findViewById(R.id.payroll_amount);

                payrollId.setText("ID: " + payroll.getId());
                payrollEmployeeId.setText("معرف الموظف: " + payroll.getEmployeeId());
                payrollDate.setText("التاريخ: " + payroll.getDate());
                payrollAmount.setText(String.format("المبلغ: %.2f", payroll.getAmount()));
            }
        };
        payrollRecyclerView.setAdapter(adapter);

        payrollDao.getPayrollsByCompanyId(companyId).observe(this, payrolls -> {
            if (payrolls != null) {
                adapter.updateData(payrolls);
            }
        });
    }
}
