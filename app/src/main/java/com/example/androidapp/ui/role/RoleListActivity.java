package com.example.androidapp.ui.role;

import android.content.Intent;
import android.os.Bundle;
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
    private FloatingActionButton fabAddRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role_list);

        database = AppDatabase.getInstance(this);
        sessionManager = new SessionManager(this);

        recyclerView = findViewById(R.id.recyclerView);
        fabAddRole = findViewById(R.id.fabAddRole);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        fabAddRole.setOnClickListener(v -> {
            Intent intent = new Intent(RoleListActivity.this, RoleDetailActivity.class);
            startActivity(intent);
        });

        loadRoles();
    }

    private void loadRoles() {
        String companyId = sessionManager.getUserDetails().get(SessionManager.KEY_COMPANY_ID);

        adapter = new GenericAdapter<>(new ArrayList<>(), new GenericAdapter.OnItemClickListener<Role>() {
            @Override
            public void onItemClick(Role item) {
                Intent intent = new Intent(RoleListActivity.this, RoleDetailActivity.class);
                intent.putExtra("role_id", item.getId());
                startActivity(intent);
            }
        }) {
            @Override
            protected int getLayoutResId() {
                return R.layout.role_list_row;
            }

            @Override
            protected void bindView(View view, Role role) {
                TextView tvRoleName = view.findViewById(R.id.tvRoleName);
                TextView tvRoleDescription = view.findViewById(R.id.tvRoleDescription);

                tvRoleName.setText(role.getName());
                tvRoleDescription.setText(role.getDescription());
            }
        };

        recyclerView.setAdapter(adapter);

        database.roleDao().getAllRoles(companyId).observe(this, roles -> {
            if (roles != null) {
                adapter.updateData(roles);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadRoles();
    }
}
