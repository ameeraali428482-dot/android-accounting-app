package com.example.androidapp.ui.repair;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.androidapp.R;
import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.entities.Repair;
import com.example.androidapp.ui.common.GenericAdapter;
import com.example.androidapp.utils.SessionManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class RepairListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private GenericAdapter<Repair> adapter;
    private AppDatabase database;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repair_list);

        database = AppDatabase.getDatabase(this);
        sessionManager = new SessionManager(this);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RepairListActivity.this, RepairDetailActivity.class);
                startActivity(intent);
            }
        });

        loadRepairs();
    }

    private void loadRepairs() {
        String companyId = sessionManager.getCurrentCompanyId();
        if (companyId == null) {
            return;
        }

        adapter = new GenericAdapter<Repair>(new ArrayList<Repair>()) {
            @Override
            protected int getLayoutResId() {
                return R.layout.repair_list_row;
            }

            @Override
            protected void bindView(View itemView, Repair repair) {
                TextView repairTitle = itemView.findViewById(R.id.repairTitle);
                TextView repairStatus = itemView.findViewById(R.id.repairStatus);
                TextView repairDate = itemView.findViewById(R.id.repairDate);

                if (repairTitle != null) repairTitle.setText(repair.getTitle());
                if (repairStatus != null) repairStatus.setText("الحالة: " + repair.getStatus());
                if (repairDate != null) repairDate.setText("التاريخ: " + repair.getRepairDate());

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(RepairListActivity.this, RepairDetailActivity.class);
                        intent.putExtra("repair_id", repair.getId());
                        startActivity(intent);
                    }
                });
            }
        };

        recyclerView.setAdapter(adapter);

        database.repairDao().getAllRepairs(companyId).observe(this, new androidx.lifecycle.Observer<List<Repair>>() {
            @Override
            public void onChanged(List<Repair> repairs) {
                if (repairs != null) {
                    adapter.updateData(repairs);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadRepairs();
    }
}
