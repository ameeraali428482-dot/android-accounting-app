package com.example.androidapp.ui.trophy;

import android.content.Intent;
import android.os.Bundle;
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
    private FloatingActionButton fabAddTrophy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trophy_list);

        database = AppDatabase.getInstance(this);
        sessionManager = new SessionManager(this);

        recyclerView = findViewById(R.id.recyclerView);
        fabAddTrophy = findViewById(R.id.fabAddTrophy);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        fabAddTrophy.setOnClickListener(v -> {
            Intent intent = new Intent(TrophyListActivity.this, TrophyDetailActivity.class);
            startActivity(intent);
        });

        loadTrophies();
    }

    private void loadTrophies() {
        String companyId = sessionManager.getUserDetails().get(SessionManager.KEY_COMPANY_ID);

        adapter = new GenericAdapter<>(new ArrayList<>(), new GenericAdapter.OnItemClickListener<Trophy>() {
            @Override
            public void onItemClick(Trophy item) {
                Intent intent = new Intent(TrophyListActivity.this, TrophyDetailActivity.class);
                intent.putExtra("trophy_id", item.getId());
                startActivity(intent);
            }
        }) {
            @Override
            protected int getLayoutResId() {
                return R.layout.trophy_list_row;
            }

            @Override
            protected void bindView(View view, Trophy trophy) {
                TextView tvName = view.findViewById(R.id.tvName);
                TextView tvDescription = view.findViewById(R.id.tvDescription);
                TextView tvPointsRequired = view.findViewById(R.id.tvPointsRequired);
                ImageView ivTrophyImage = view.findViewById(R.id.ivTrophyImage);

                tvName.setText(trophy.getTrophyName());
                tvDescription.setText(trophy.getTrophyDescription());
                tvPointsRequired.setText(String.valueOf(trophy.getPointsRequired()));
                
                // يمكنك تحميل صورة هنا باستخدام Glide أو Picasso
                // Glide.with(view.getContext()).load(trophy.getImageUrl()).into(ivTrophyImage);
            }
        };

        recyclerView.setAdapter(adapter);

        database.trophyDao().getAllTrophies(companyId).observe(this, trophies -> {
            if (trophies != null) {
                adapter.updateData(trophies);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTrophies();
    }
}
