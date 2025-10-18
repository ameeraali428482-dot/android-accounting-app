package com.example.androidapp.ui.userreward;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.androidapp.R;
import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.entities.UserReward;
import com.example.androidapp.ui.common.GenericAdapter;
import com.example.androidapp.utils.SessionManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;

public class UserRewardListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private GenericAdapter<UserReward> adapter;
    private AppDatabase database;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_reward_list);

        database = AppDatabase.getDatabase(this);
        sessionManager = new SessionManager(this);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(UserRewardListActivity.this, UserRewardDetailActivity.class);
            startActivity(intent);
        });

        loadUserRewards();
    }

    private void loadUserRewards() {
        String companyId = sessionManager.getCurrentCompanyId();
        if (companyId == null) {
            return;
        }

        adapter = new GenericAdapter<UserReward>(new ArrayList<>(), item -> {
            Intent intent = new Intent(UserRewardListActivity.this, UserRewardDetailActivity.class);
            intent.putExtra("user_reward_id", item.getId());
            startActivity(intent);
        }) {
            @Override
            protected int getLayoutResId() {
                return R.layout.user_reward_list_row;
            }

            @Override
            protected void bindView(View itemView, UserReward userReward) {
                // Bind data to views
            }
        };
        recyclerView.setAdapter(adapter);

        database.userRewardDao().getAllUserRewards(companyId).observe(this, userRewards -> {
            if (userRewards != null) {
                adapter.updateData(userRewards);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUserRewards();
    }
}
