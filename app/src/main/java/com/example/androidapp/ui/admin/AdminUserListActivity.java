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
import java.util.List;

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

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        setupRecyclerView();
        loadUsers();
    }

    private void setupRecyclerView() {
        adapter = new GenericAdapter<User>(new ArrayList<>(), user -> {
            Intent intent = new Intent(AdminUserListActivity.this, AdminUserDetailActivity.class);
            intent.putExtra("user_id", user.getId());
            startActivity(intent);
        }) {
            @Override
            protected int getLayoutResId() {
                return R.layout.admin_user_list_row;
            }

            @Override
            protected void bindView(View view, User user) {
                TextView tvUserName = view.findViewById(R.id.tvUserName);
                TextView tvUserEmail = view.findViewById(R.id.tvUserEmail);

                if (tvUserName != null) tvUserName.setText(user.getName());
                if (tvUserEmail != null) tvUserEmail.setText(user.getEmail());
            }
        };
        recyclerView.setAdapter(adapter);
    }

    private void loadUsers() {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            List<User> users = database.userDao().getAllUsersSync();
            runOnUiThread(() -> {
                if (users != null) {
                    adapter.setData(users);
                }
            });
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUsers();
    }
}
