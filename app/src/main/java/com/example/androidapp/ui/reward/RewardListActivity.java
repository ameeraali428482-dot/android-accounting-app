package com.example.androidapp.ui.reward;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
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

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RewardListActivity.this, RewardDetailActivity.class);
                startActivity(intent);
            }
        });

        loadRewards();
    }

    private void loadRewards() {
        String companyId = sessionManager.getCurrentCompanyId();
        if (companyId == null) {
            return;
        }

        adapter = new GenericAdapter<Reward>(new ArrayList<Reward>()) {
            @Override
            protected int getLayoutResId() {
                return R.layout.reward_list_row;
            }

            @Override
            protected void bindView(View itemView, Reward reward) {
                TextView rewardName = itemView.findViewById(R.id.rewardName);
                TextView rewardPoints = itemView.findViewById(R.id.rewardPoints);
                TextView rewardDescription = itemView.findViewById(R.id.rewardDescription);

                if (rewardName != null) rewardName.setText(reward.getName());
                if (rewardPoints != null) rewardPoints.setText("النقاط: " + reward.getPointsRequired());
                if (rewardDescription != null) rewardDescription.setText(reward.getDescription());

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(RewardListActivity.this, RewardDetailActivity.class);
                        intent.putExtra("reward_id", reward.getId());
                        startActivity(intent);
                    }
                });
            }
        };

        recyclerView.setAdapter(adapter);

        database.rewardDao().getAllRewards(companyId).observe(this, new androidx.lifecycle.Observer<List<Reward>>() {
            @Override
            public void onChanged(List<Reward> rewards) {
                if (rewards != null) {
                    adapter.updateData(rewards);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadRewards();
    }
}
