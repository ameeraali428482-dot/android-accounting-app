package com.example.androidapp.ui.trophy;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.androidapp.R;
import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.entities.Trophy;
import com.example.androidapp.ui.common.GenericAdapter;
import com.example.androidapp.utils.SessionManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;

public class TrophyListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private GenericAdapter<Trophy> adapter;
    private AppDatabase database;
    private SessionManager sessionManager;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trophy_list);

        database = AppDatabase.getDatabase(this);
        sessionManager = new SessionManager(this);

        initViews();
        setupRecyclerView();
        loadTrophies();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerView);
        fab = findViewById(R.id.fab);
        setTitle("الجوائز");
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        fab.setOnClickListener(v -> {
            Intent intent = new Intent(this, TrophyDetailActivity.class);
            startActivity(intent);
        });
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new GenericAdapter<Trophy>(new ArrayList<>(), trophy -> {
            Intent intent = new Intent(TrophyListActivity.this, TrophyDetailActivity.class);
            intent.putExtra("trophy_id", trophy.getId());
            startActivity(intent);
        }) {
            @Override
            protected int getLayoutResId() {
                return R.layout.trophy_list_row;
            }

            @Override
            protected void bindView(View view, Trophy trophy) {
                TextView tvTrophyName = view.findViewById(R.id.tvTrophyName);
                TextView tvTrophyDescription = view.findViewById(R.id.tvTrophyDescription);
                TextView tvTrophyPoints = view.findViewById(R.id.tvTrophyPoints);

                if (tvTrophyName != null) tvTrophyName.setText(trophy.getName());
                if (tvTrophyDescription != null) tvTrophyDescription.setText(trophy.getDescription());
                if (tvTrophyPoints != null) tvTrophyPoints.setText(trophy.getPointsRequired() + " نقطة");
            }
        };
        recyclerView.setAdapter(adapter);
    }

    private void loadTrophies() {
        database.trophyDao().getAllTrophies(sessionManager.getCurrentCompanyId())
            .observe(this, trophies -> {
                if (trophies != null) {
                    adapter.updateData(trophies);
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
            loadTrophies();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTrophies();
    }
}
