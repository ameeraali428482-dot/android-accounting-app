package com.example.androidapp.ui.reward;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.androidapp.R;
import com.example.androidapp.data.entities.Reward;
import com.example.androidapp.ui.common.GenericAdapter;
import com.example.androidapp.utils.SessionManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;

public class RewardListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private GenericAdapter<Reward> adapter;
    private RewardViewModel viewModel;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reward_list);

        sessionManager = new SessionManager(this);
        viewModel = new ViewModelProvider(this).get(RewardViewModel.class);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(RewardListActivity.this, RewardDetailActivity.class);
            startActivity(intent);
        });

        loadRewards();
    }

    private void loadRewards() {
        String companyId = sessionManager.getCurrentCompanyId();
        if (companyId == null) return;

        adapter = new GenericAdapter<Reward>(new ArrayList<>(), reward -> {
            Intent intent = new Intent(RewardListActivity.this, RewardDetailActivity.class);
            intent.putExtra("reward_id", reward.getId());
            startActivity(intent);
        }) {
            @Override
            protected int getLayoutResId() {
                return R.layout.reward_list_row;
            }

            @Override
            protected void bindView(View itemView, Reward reward) {
                TextView rewardName = itemView.findViewById(R.id.tv_reward_name);
                TextView rewardPoints = itemView.findViewById(R.id.tv_points_required);
                TextView rewardDescription = itemView.findViewById(R.id.tv_reward_description);

                if (rewardName != null) rewardName.setText(reward.getName());
                if (rewardPoints != null) rewardPoints.setText("النقاط: " + reward.getPointsRequired());
                if (rewardDescription != null) rewardDescription.setText(reward.getDescription());
            }
        };
        recyclerView.setAdapter(adapter);

        viewModel.getAllRewards(companyId).observe(this, rewards -> {
            if (rewards != null) {
                adapter.updateData(rewards);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadRewards();
    }
}
