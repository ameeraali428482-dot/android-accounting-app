package com.example.androidapp.ui.trophy;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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
    private AppDatabase db;
    private SessionManager sm;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trophy_list);

        db = AppDatabase.getInstance(this);
        sm = new SessionManager(this);

        recyclerView = findViewById(R.id.recyclerView);
        fab          = findViewById(R.id.fab);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        fab.setOnClickListener(v -> {
            Intent i = new Intent(TrophyListActivity.this, TrophyDetailActivity.class);
            startActivity(i);
        });

        loadTrophies();
    }

    private void loadTrophies() {
        String companyId = sm.getUserDetails().get(SessionManager.KEY_COMPANY_ID);
        if (companyId == null) return;

        adapter = new GenericAdapter<>(new ArrayList<>(), item -> {
            Intent i = new Intent(TrophyListActivity.this, TrophyDetailActivity.class);
            i.putExtra("trophy_id", item.getId());
            startActivity(i);
        }) {
            @Override
            protected int getLayoutResId() {
                return R.layout.trophy_list_row;
            }

            @Override
            protected void bindView(View itemView, Trophy t) {
                TextView tvName  = itemView.findViewById(R.id.tvName);
                TextView tvDesc  = itemView.findViewById(R.id.tvDescription);
                TextView tvPts   = itemView.findViewById(R.id.tvPointsRequired);

                tvName .setText(t.getTrophyName());
                tvDesc .setText(t.getTrophyDescription());
                tvPts  .setText(String.valueOf(t.getPointsRequired()));
            }
        };

        recyclerView.setAdapter(adapter);
        db.trophyDao().getAllTrophies(companyId).observe(this, list -> adapter.updateData(list));
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTrophies();
    }
}
