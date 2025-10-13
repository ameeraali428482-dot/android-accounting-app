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
    private FloatingActionButton fab;

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
        recyclerView = findViewById(R.id.recyclerView);
        fab = findViewById(R.id.fab);
        setTitle("الأدوار");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fab.setOnClickListener(v -> {
            Intent intent = new Intent(this, RoleDetailActivity.class);
            startActivity(intent);
        });
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new GenericAdapter<Role>(new ArrayList<>(), role -> {
            Intent intent = new Intent(RoleListActivity.this, RoleDetailActivity.class);
            intent.putExtra("role_id", role.getId());
            startActivity(intent);
        }) {
            @Override
            protected int getLayoutResId() {
                return R.layout.role_list_row;
            }

            @Override
            protected void bindView(View view, Role role) {
                TextView tvRoleNameDisplay = view.findViewById(R.id.tvRoleNameDisplay);
                TextView tvRoleDescDisplay = view.findViewById(R.id.tvRoleDescDisplay);

                if (tvRoleNameDisplay != null) tvRoleNameDisplay.setText(role.getName());
                if (tvRoleDescDisplay != null) tvRoleDescDisplay.setText(role.getDescription());
            }
        };
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
