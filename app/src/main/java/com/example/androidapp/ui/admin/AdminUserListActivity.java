package com.example.androidapp.ui.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidapp.R;
import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.entities.User;
import com.example.androidapp.ui.common.GenericAdapter;
import com.example.androidapp.utils.SessionManager;

import java.util.ArrayList;

public class AdminUserListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private GenericAdapter<User> adapter;
    private AppDatabase database;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_user_list);

        database = AppDatabase.getInstance(this);
        sessionManager = new SessionManager(this);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadUsers();
    }

    private void loadUsers() {
        String companyId = sessionManager.getUserDetails().get(SessionManager.KEY_COMPANY_ID);

        adapter = new GenericAdapter<>(new ArrayList<>(), new GenericAdapter.OnItemClickListener<User>() {
            @Override
            public void onItemClick(User item) {
                Intent intent = new Intent(AdminUserListActivity.this, AdminUserDetailActivity.class);
                intent.putExtra("user_id", item.getId());
                startActivity(intent);
            }
        }) {
            @Override
            protected int getLayoutResId() {
                return R.layout.admin_user_list_row;
            }

            @Override
            protected void bindView(View view, User user) {
                TextView tvUserNameDisplay = view.findViewById(R.id.tvUserNameDisplay);
                TextView tvUserEmailDisplay = view.findViewById(R.id.tvUserEmailDisplay);

                tvUserNameDisplay.setText(user.getUsername());
                tvUserEmailDisplay.setText(user.getEmail());
            }
        };

        recyclerView.setAdapter(adapter);

        database.userDao().getAllUsers(companyId).observe(this, users -> {
            if (users != null) {
                adapter.updateData(users);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUsers();
    }
}
