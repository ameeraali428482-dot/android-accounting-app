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
    private AppDatabase db;
    private SessionManager sm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_user_list);

        db = AppDatabase.getInstance(this);
        sm = new SessionManager(this);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadUsers();
    }

    private void loadUsers() {
        String companyId = sm.getUserDetails().get(SessionManager.KEY_COMPANY_ID);
        if (companyId == null) return;

        adapter = new GenericAdapter<>(new ArrayList<>(), item -> {
            Intent i = new Intent(AdminUserListActivity.this, AdminUserDetailActivity.class);
            i.putExtra("user_id", item.getId());
            startActivity(i);
        }) {
            @Override
            protected int getLayoutResId() {
                return R.layout.admin_user_list_row;
            }

            @Override
            protected void bindView(View itemView, User u) {
                TextView tvName = itemView.findViewById(R.id.tvUserNameDisplay);
                TextView tvMail = itemView.findViewById(R.id.tvUserEmailDisplay);
                tvName.setText(u.getUsername());
                tvMail.setText(u.getEmail());
            }
        };

        recyclerView.setAdapter(adapter);
        db.userDao().getAllUsers().observe(this, list -> adapter.updateData(list));
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUsers();
    }
}
