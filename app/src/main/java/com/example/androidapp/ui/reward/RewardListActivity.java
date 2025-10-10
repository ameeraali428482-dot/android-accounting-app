package com.example.androidapp.ui.reward;

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
import com.example.androidapp.data.entities.Reward;
import com.example.androidapp.ui.common.GenericAdapter;
import com.example.androidapp.utils.SessionManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;






public class RewardListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private GenericAdapter<Reward> adapter;
    private AppDatabase database;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reward_list);

        database = AppDatabase.getDatabase(this);
        sessionManager = new SessionManager(this);

        initViews();
        setupRecyclerView();
        loadRewards();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recycler_view);
        FloatingActionButton fab = findViewById(R.id.fab);

        setTitle("إدارة المكافآت");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fab.setOnClickListener(v -> {
            Intent intent = new Intent(this, RewardDetailActivity.class);
            startActivity(intent);
        });
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        adapter = new GenericAdapter<>(
                new ArrayList<>(),
                R.layout.reward_list_row,
                (reward, view) -> {
                    // Bind data to views
                    TextView tvName = view.findViewById(R.id.tv_reward_name);
                    TextView tvPointsRequired = view.findViewById(R.id.tv_points_required);
                    TextView tvStatus = view.findViewById(R.id.tv_status);

                    tvName.setText(reward.getName());
                    tvPointsRequired.setText(String.format("النقاط المطلوبة: %d", reward.getPointsRequired()));
                    tvStatus.setText(reward.isActive() ? "نشطة" : "غير نشطة");
                    tvStatus.setBackgroundResource(reward.isActive() ? R.drawable.status_active_background : R.drawable.status_inactive_background);
                },
                reward -> {
                    Intent intent = new Intent(this, RewardDetailActivity.class);
                    intent.putExtra("reward_id", reward.getId());
                    startActivity(intent);
                }
        );
        
        recyclerView.setAdapter(adapter);
    }

    private void loadRewards() {
        database.rewardDao().getAllRewards(sessionManager.getCurrentCompanyId())
                .observe(this, rewards -> {
                    if (rewards != null) {
                        adapter.updateData(rewards);
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
                loadRewards();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadRewards();
    }
}
