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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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

        sharedLinkRecyclerView = findViewById(R.id.sharedLinkRecyclerView);
        sharedLinkRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        sharedLinkDao = new SharedLinkDao(App.getDatabaseHelper());
        sessionManager = new SessionManager(this);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SharedLinkListActivity.this, SharedLinkDetailActivity.class);
                startActivity(intent);
            }
        });

        loadSharedLinks();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSharedLinks();
    }

    private void loadSharedLinks() {
        String companyId = sessionManager.getUserDetails().get(SessionManager.KEY_COMPANY_ID);
        if (companyId == null) {
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
                TextView sharedLinkName = itemView.findViewById(R.id.sharedLinkName);
                TextView sharedLinkUrl = itemView.findViewById(R.id.sharedLinkUrl);
                TextView sharedLinkExpiresAt = itemView.findViewById(R.id.sharedLinkExpiresAt);

                sharedLinkName.setText(sharedLink.getName());
                sharedLinkUrl.setText(sharedLink.getUrl());
                sharedLinkExpiresAt.setText("ينتهي في: " + sharedLink.getExpiresAt());

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(SharedLinkListActivity.this, SharedLinkDetailActivity.class);
                        intent.putExtra("shared_link_id", sharedLink.getId());
                        startActivity(intent);
                    }
                });
            }
        };
        sharedLinkRecyclerView.setAdapter(adapter);
    }
}
