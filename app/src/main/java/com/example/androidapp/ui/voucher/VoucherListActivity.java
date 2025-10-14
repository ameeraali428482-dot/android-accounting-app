package com.example.androidapp.ui.voucher;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.androidapp.R;
import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.dao.VoucherDao;
import com.example.androidapp.data.entities.Voucher;
import com.example.androidapp.ui.common.GenericAdapter;
import com.example.androidapp.utils.SessionManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;

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

        voucherDao = AppDatabase.getDatabase(this).voucherDao();
        sessionManager = new SessionManager(this);

        FloatingActionButton fab = findViewById(R.id.add_voucher_button);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(VoucherListActivity.this, VoucherDetailActivity.class);
            startActivity(intent);
        });

        loadVouchers();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadVouchers();
    }

    private void loadVouchers() {
        String companyId = sessionManager.getCurrentCompanyId();
        if (companyId == null) {
            return;
        }

        adapter = new GenericAdapter<>(new ArrayList<>(), item -> {
            Intent intent = new Intent(VoucherListActivity.this, VoucherDetailActivity.class);
            intent.putExtra("voucher_id", item.getId());
            startActivity(intent);
        });
        voucherRecyclerView.setAdapter(adapter);

        voucherDao.getVouchersByCompanyId(companyId).observe(this, vouchers -> {
            if (vouchers != null) {
                adapter.updateData(vouchers);
            }
        });
    }
}
