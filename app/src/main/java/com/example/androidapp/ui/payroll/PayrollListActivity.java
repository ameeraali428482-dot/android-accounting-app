package com.example.androidapp.ui.payroll;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.androidapp.App;
import com.example.androidapp.R;
import com.example.androidapp.data.dao.PayrollDao;
import com.example.androidapp.data.entities.Payroll;
import com.example.androidapp.ui.common.GenericAdapter;
import com.example.androidapp.utils.SessionManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.List;

public class PayrollListActivity extends AppCompatActivity {

    private RecyclerView payrollRecyclerView;
    private PayrollDao payrollDao;
    private SessionManager sessionManager;
    private GenericAdapter<Payroll> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payroll_list);

        payrollRecyclerView = findViewById(R.id.payrollRecyclerView);
        payrollRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        payrollDao = new PayrollDao(App.getDatabaseHelper());
        sessionManager = new SessionManager(this);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PayrollListActivity.this, PayrollDetailActivity.class);
                startActivity(intent);
            }
        });

        loadPayrolls();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPayrolls();
    }

    private void loadPayrolls() {
        String companyId = sessionManager.getUserDetails().get(SessionManager.KEY_COMPANY_ID);
        if (companyId == null) {
            return;
        }

        List<Payroll> payrolls = payrollDao.getPayrollsByCompanyId(companyId);

        adapter = new GenericAdapter<Payroll>(payrolls) {
            @Override
            protected int getLayoutResId() {
                return R.layout.payroll_list_row;
            }

            @Override
            protected void bindView(View itemView, Payroll payroll) {
                TextView payrollId = itemView.findViewById(R.id.payrollId);
                TextView payrollEmployeeId = itemView.findViewById(R.id.payrollEmployeeId);
                TextView payrollDate = itemView.findViewById(R.id.payrollDate);
                TextView payrollAmount = itemView.findViewById(R.id.payrollAmount);

                payrollId.setText("ID: " + payroll.getId());
                payrollEmployeeId.setText("معرف الموظف: " + payroll.getEmployeeId());
                payrollDate.setText("التاريخ: " + payroll.getDate());
                payrollAmount.setText(String.format("المبلغ: %.2f", payroll.getAmount()));

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(PayrollListActivity.this, PayrollDetailActivity.class);
                        intent.putExtra("payroll_id", payroll.getId());
                        startActivity(intent);
                    }
                });
            }
        };
        payrollRecyclerView.setAdapter(adapter);
    }
}
