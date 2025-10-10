package com.example.androidapp.ui.trophy;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
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
import com.bumptech.glide.Glide;
import java.util.ArrayList;






public class TrophyListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private GenericAdapter<Trophy> adapter;
    private AppDatabase database;
    private SessionManager sessionManager;

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
        recyclerView = // TODO: Fix findViewById;
        FloatingActionButton fab = // TODO: Fix findViewById;

        setTitle("إدارة الكؤوس");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fab.setOnClickListener(v -> {
            Intent intent = new Intent(this, TrophyDetailActivity.class);
            startActivity(intent);
        });
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        adapter = new GenericAdapter<Object>(new ArrayList<>(), null) {(
                new ArrayList<>(),
                R.layout.trophy_list_row,
                (trophy, view) -> {
                    TextView tvName = trophy.// TODO: Fix findViewById;
                    TextView tvDescription = trophy.// TODO: Fix findViewById;
                    TextView tvPointsRequired = trophy.// TODO: Fix findViewById;
                    ImageView ivTrophyImage = trophy.// TODO: Fix findViewById;

                    tvName.setText(view.getName());
                    tvDescription.setText(view.getDescription());
                    tvPointsRequired.setText("النقاط المطلوبة: " + view.getPointsRequired());

                    if (view.getImageUrl() != null && !view.getImageUrl().isEmpty()) {
                        Glide.with(trophy.getContext())
                                .load(view.getImageUrl())
                                .placeholder(R.drawable.ic_trophy_placeholder)
                                .error(R.drawable.ic_trophy_placeholder)
                                .into(ivTrophyImage);
                    } else {
                        ivTrophyImage.setImageResource(R.drawable.ic_trophy_placeholder);
                    }
                },
                trophy -> {
                    Intent intent = new Intent(this, TrophyDetailActivity.class);
                    intent.putExtra("trophy_id", trophy.getId());
                    startActivity(intent);
                }
        );
        
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
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case android.R.id.home: // Fixed constant expression
                loadTrophies();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTrophies();
    }
}
