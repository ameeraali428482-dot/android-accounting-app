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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role_list);

        database = AppDatabase.getDatabase(this);
        sessionManager = new SessionManager(this);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(this, RoleDetailActivity.class);
            startActivity(intent);
        });

        setupRecyclerView();
        loadRoles();
    }

    private void setupRecyclerView() {
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
                // IDs من role_list_row.xml
                TextView tvrolename = view.findViewById(R.id.tvrolename);
                TextView tvroledescription = view.findViewById(R.id.tvroledescription);

                if (tvrolename != null) tvrolename.setText(role.getName());
                if (tvroledescription != null) tvroledescription.setText(role.getDescription());
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
}
