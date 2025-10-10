package com.example.androidapp.ui.voucher;

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
import com.example.androidapp.data.dao.VoucherDao;
import com.example.androidapp.data.entities.Voucher;
import com.example.androidapp.ui.common.GenericAdapter;
import com.example.androidapp.utils.SessionManager;
import java.util.List;






public class VoucherListActivity extends AppCompatActivity {

    private RecyclerView voucherRecyclerView;
    private VoucherDao voucherDao;
    private SessionManager sessionManager;
    private GenericAdapter<Voucher> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voucher_list);

        voucherRecyclerView = findViewById(R.id.voucher_recycler_view);
        voucherRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        voucherDao = new VoucherDao(App.getDatabaseHelper());
        sessionManager = new SessionManager(this);

        findViewById(R.id.add_voucher_button).setOnClickListener(v -> {
            Intent intent = new Intent(VoucherListActivity.this, VoucherDetailActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadVouchers();
    }

    private void loadVouchers() {
        String companyId = sessionManager.getUserDetails().get(SessionManager.KEY_COMPANY_ID);
        if (companyId == null) {
            // Handle error: no company ID found
            return;
        }

        List<Voucher> vouchers = voucherDao.getVouchersByCompanyId(companyId);

        adapter = new GenericAdapter<Voucher>(vouchers) {
            @Override
            protected int getLayoutResId() {
                return R.layout.voucher_list_row;
            }

            @Override
            protected void bindView(View itemView, Voucher voucher) {
                TextView voucherType = itemView.findViewById(R.id.voucher_type);
                TextView voucherAmount = itemView.findViewById(R.id.voucher_amount);
                TextView voucherDate = itemView.findViewById(R.id.voucher_date);

                voucherType.setText("النوع: " + voucher.getType());
                voucherAmount.setText(String.format("المبلغ: %.2f", voucher.getAmount()));
                voucherDate.setText("التاريخ: " + voucher.getDate());

                itemView.setOnClickListener(v -> {
                    Intent intent = new Intent(VoucherListActivity.this, VoucherDetailActivity.class);
                    intent.putExtra("voucher_id", voucher.getId());
                    startActivity(intent);
                });
            }
        };
        voucherRecyclerView.setAdapter(adapter);
    }
}
