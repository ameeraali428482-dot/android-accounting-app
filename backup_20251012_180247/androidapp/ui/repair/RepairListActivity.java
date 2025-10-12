package com.example.androidapp.ui.repair;

import java.util.Date;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.androidapp.R;
import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.entities.Repair;
import com.example.androidapp.ui.common.GenericAdapter;
import com.example.androidapp.utils.SessionManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;






public class RepairListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private GenericAdapter<Repair> adapter;
    private AppDatabase database;
    private SessionManager sessionManager;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repair_list);

        database = AppDatabase.getDatabase(this);
        sessionManager = new SessionManager(this);

        initViews();
        setupRecyclerView();
        loadRepairs();
    }

    private void initViews() {

        setTitle("إدارة الإصلاحات");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fab.setOnClickListener(v -> {
            Intent intent = new Intent(this, RepairDetailActivity.class);
            startActivity(intent);
        });
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        adapter = new GenericAdapter<Object>(new ArrayList<>(), null) {
                new ArrayList<>(),
                R.layout.repair_list_row,
                (repair, view) -> {

                    tvTitle.setText(repair.getTitle());
                    tvDescription.setText(repair.getDescription());
                    tvRequestDate.setText("تاريخ الطلب: " + dateFormat.format(repair.getRequestDate()));
                    tvStatus.setText(repair.getStatus());
                    tvAssignedTo.setText("مُكلف: " + repair.getAssignedTo());

                    // Set status background based on status
                    int statusBackground;
                    switch (repair.getStatus()) {
                        case "Completed":
                            statusBackground = R.drawable.status_active_background;
                            break;
                        case "In Progress":
                            statusBackground = R.drawable.status_draft_background;
                            break;
                        case "Cancelled":
                            statusBackground = R.drawable.status_inactive_background;
                            break;
                        default: // Pending
                            statusBackground = R.drawable.status_pending_background;
                            break;
                    }
                    tvStatus.setBackgroundResource(statusBackground);
                },
                repair -> {
                    Intent intent = new Intent(this, RepairDetailActivity.class);
                    intent.putExtra("repair_id", repair.getId());
                    startActivity(intent);
                }
        );
        
        recyclerView.setAdapter(adapter);
    }

    private void loadRepairs() {
        database.repairDao().getAllRepairs(sessionManager.getCurrentCompanyId())
                .observe(this, repairs -> {
                    if (repairs != null) {
                        adapter.updateData(repairs);
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_refresh:
                loadRepairs();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadRepairs();
    }
}
