package com.example.androidapp.ui.sharedlink;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.androidapp.R;
import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.dao.SharedLinkDao;
import com.example.androidapp.data.entities.SharedLink;
import com.example.androidapp.ui.common.GenericAdapter;
import com.example.androidapp.utils.SessionManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class SharedLinkListActivity extends AppCompatActivity {

    private RecyclerView sharedLinkRecyclerView;
    private SharedLinkDao sharedLinkDao;
    private SessionManager sessionManager;
    private GenericAdapter<SharedLink> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shared_link_list);

        sharedLinkRecyclerView = findViewById(R.id.shared_link_recycler_view);
        sharedLinkRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        sharedLinkDao = AppDatabase.getDatabase(this).sharedLinkDao();
        sessionManager = new SessionManager(this);

        FloatingActionButton fab = findViewById(R.id.add_shared_link_button);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(SharedLinkListActivity.this, SharedLinkDetailActivity.class);
            startActivity(intent);
        });

        loadSharedLinks();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSharedLinks();
    }

    private void loadSharedLinks() {
        String companyId = sessionManager.getCurrentCompanyId();
        if (companyId == null) {
            return;
        }

        AppDatabase.databaseWriteExecutor.execute(() -> {
            List<SharedLink> sharedLinks = sharedLinkDao.getSharedLinksByCompanyId(companyId);
            runOnUiThread(() -> {
                adapter = new GenericAdapter<>(sharedLinks, item -> {
                    Intent i = new Intent(SharedLinkListActivity.this, SharedLinkDetailActivity.class);
                    i.putExtra("shared_link_id", item.getId());
                    startActivity(i);
                }) {
                    @Override
                    protected int getLayoutResId() {
                        return R.layout.shared_link_list_row;
                    }

                    @Override
                    protected void bindView(View itemView, SharedLink sharedLink) {
                        TextView sharedLinkName = itemView.findViewById(R.id.shared_link_name);
                        TextView sharedLinkUrl = itemView.findViewById(R.id.shared_link_url);
                        TextView sharedLinkExpiresAt = itemView.findViewById(R.id.shared_link_expires_at);

                        sharedLinkName.setText(sharedLink.getName());
                        sharedLinkUrl.setText(sharedLink.getUrl());
                        sharedLinkExpiresAt.setText("ينتهي في: " + sharedLink.getExpiresAt());
                    }
                };
                sharedLinkRecyclerView.setAdapter(adapter);
            });
        });
    }
}
