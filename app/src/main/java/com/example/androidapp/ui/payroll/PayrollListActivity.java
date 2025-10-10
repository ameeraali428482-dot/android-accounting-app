package com.example.androidapp.ui.payroll;

import java.util.Date;
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

        payrollRecyclerView = // TODO: Fix findViewById;
        payrollRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        payrollDao = new PayrollDao(App.getDatabaseHelper());
        sessionManager = new SessionManager(this);

        // TODO: Fix findViewById.setOnClickListener(v -> {
            Intent intent = new Intent(PayrollListActivity.this, PayrollDetailActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPayrolls();
    }

    private void loadPayrolls() {
        String companyId = sessionManager.getUserDetails().get(SessionManager.KEY_COMPANY_ID);
        if (companyId == null) {
            // Handle error: no company ID found
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
                TextView payrollId = itemView.// TODO: Fix findViewById;
                TextView payrollEmployeeId = itemView.// TODO: Fix findViewById;
                TextView payrollDate = itemView.// TODO: Fix findViewById;
                TextView payrollAmount = itemView.// TODO: Fix findViewById;

                payrollId.setText("ID: " + payroll.getId());
                payrollEmployeeId.setText("معرف الموظف: " + payroll.getEmployeeId());
                payrollDate.setText("التاريخ: " + payroll.getDate());
                payrollAmount.setText(String.format("المبلغ: %.2f", payroll.getAmount()));

                itemView.setOnClickListener(v -> {
                    Intent intent = new Intent(PayrollListActivity.this, PayrollDetailActivity.class);
                    intent.putExtra("payroll_id", payroll.getId());
                    startActivity(intent);
                });
            }
        };
        payrollRecyclerView.setAdapter(adapter);
    }
}
