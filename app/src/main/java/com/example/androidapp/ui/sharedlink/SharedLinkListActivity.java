package com.example.androidapp.ui.sharedlink;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidapp.App;
import com.example.androidapp.R;
import com.example.androidapp.data.dao.SharedLinkDao;
import com.example.androidapp.data.entities.SharedLink;
import com.example.androidapp.ui.common.GenericAdapter;
import com.example.androidapp.utils.SessionManager;

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

        sharedLinkDao = new SharedLinkDao(App.getDatabaseHelper());
        sessionManager = new SessionManager(this);

        findViewById(R.id.add_shared_link_button).setOnClickListener(v -> {
            Intent intent = new Intent(SharedLinkListActivity.this, SharedLinkDetailActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSharedLinks();
    }

    private void loadSharedLinks() {
        String companyId = sessionManager.getUserDetails().get(SessionManager.KEY_COMPANY_ID);
        if (companyId == null) {
            // Handle error: no company ID found
            return;
        }

        List<SharedLink> sharedLinks = sharedLinkDao.getSharedLinksByCompanyId(companyId);

        adapter = new GenericAdapter<SharedLink>(sharedLinks) {
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

                itemView.setOnClickListener(v -> {
                    Intent intent = new Intent(SharedLinkListActivity.this, SharedLinkDetailActivity.class);
                    intent.putExtra("shared_link_id", sharedLink.getId());
                    startActivity(intent);
                });
            }
        };
        sharedLinkRecyclerView.setAdapter(adapter);
    }
}
