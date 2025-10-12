package com.example.androidapp.ui.admin;

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

        database = AppDatabase.getDatabase(this);
        sessionManager = new SessionManager(this);

        initViews();
        setupRecyclerView();
        loadUsers();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerView);

        setTitle("إدارة المستخدمين (المسؤولين)");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        adapter = new GenericAdapter<User>(new ArrayList<>(), R.layout.admin_user_list_row) {
            @Override
            protected void bindView(View view, User user) {
                TextView tvUserName = view.findViewById(R.id.tvUserName);
                TextView tvUserEmail = view.findViewById(R.id.tvUserEmail);

                tvUserName.setText(user.getUsername());
                tvUserEmail.setText(user.getEmail());
            }

            @Override
            protected void onItemClick(User user) {
                Intent intent = new Intent(AdminUserListActivity.this, AdminUserDetailActivity.class);
                intent.putExtra("user_id", user.getId());
                startActivity(intent);
            }
        };
        
        recyclerView.setAdapter(adapter);
    }

    private void loadUsers() {
        database.userDao().getAllUsers()
                .observe(this, users -> {
                    if (users != null) {
                        adapter.updateData(users);
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
            loadUsers();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUsers();
    }
}
