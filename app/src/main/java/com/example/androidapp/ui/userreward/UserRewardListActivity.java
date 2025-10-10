package com.example.androidapp.ui.userreward;

import java.util.Date;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.androidapp.R;
import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.entities.UserReward;
import com.example.androidapp.ui.common.GenericAdapter;
import com.example.androidapp.utils.SessionManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;






public class UserRewardListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private GenericAdapter<UserReward> adapter;
    private AppDatabase database;
    private SessionManager sessionManager;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_reward_list);

        database = AppDatabase.getDatabase(this);
        sessionManager = new SessionManager(this);

        initViews();
        setupRecyclerView();
        loadUserRewards();
    }

    private void initViews() {
        recyclerView = // TODO: Fix findViewById;
        FloatingActionButton fab = // TODO: Fix findViewById;

        setTitle("مكافآت المستخدمين");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fab.setOnClickListener(v -> {
            Intent intent = new Intent(this, UserRewardDetailActivity.class);
            startActivity(intent);
        });
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        adapter = new GenericAdapter<Object>(new ArrayList<>(), null) {(
                new ArrayList<>(),
                R.layout.user_reward_list_row,
                (userReward, view) -> {
                    // Bind data to views - this would need to be implemented based on the layout
                    // For now, we'll use a simple approach
                },
                userReward -> {
                    Intent intent = new Intent(this, UserRewardDetailActivity.class);
                    intent.putExtra("user_reward_id", userReward.getId());
                    startActivity(intent);
                }
        );
        
        recyclerView.setAdapter(adapter);
    }

    private void loadUserRewards() {
        database.userRewardDao().getAllUserRewards(sessionManager.getCurrentCompanyId())
                .observe(this, userRewards -> {
                    if (userRewards != null) {
                        adapter.updateData(userRewards);
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
                loadUserRewards();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUserRewards();
    }
}
