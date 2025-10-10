package com.example.androidapp.ui.role;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.androidapp.R;
import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.entities.Role;
import com.example.androidapp.ui.common.GenericAdapter;
import com.example.androidapp.utils.SessionManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;



public class RoleListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private GenericAdapter<Role> adapter;
    private AppDatabase database;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role_list);

        database = AppDatabase.getDatabase(this);
        sessionManager = new SessionManager(this);

        initViews();
        setupRecyclerView();
        loadRoles();
    }

    private void initViews() {
        recyclerView = // TODO: Fix findViewById;
        FloatingActionButton fab = // TODO: Fix findViewById;

        setTitle("إدارة الأدوار");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fab.setOnClickListener(v -> {
            Intent intent = new Intent(this, RoleDetailActivity.class);
            startActivity(intent);
        });
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        adapter = new GenericAdapter<Object>(new ArrayList<>(), null) {(
                new ArrayList<>(),
                R.layout.role_list_row,
                (view, role) -> {
                    TextView tvRoleName = view.// TODO: Fix findViewById;
                    TextView tvRoleDescription = view.// TODO: Fix findViewById;

                    tvRoleName.setText(role.getName());
                    tvRoleDescription.setText(role.getDescription());
                },
                role -> {
                    Intent intent = new Intent(this, RoleDetailActivity.class);
                    intent.putExtra("role_id", role.getId());
                    startActivity(intent);
                }
        );
        
        recyclerView.setAdapter(adapter);
    }

    private void loadRoles() {
        database.roleDao().getAllRoles(sessionManager.getCurrentCompanyId()).observe(this, roles -> {
            if (roles != null) {
                adapter.updateData(roles);
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
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else if (item.getItemId() == R.id.action_refresh) {
            loadRoles();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadRoles();
    }
}
