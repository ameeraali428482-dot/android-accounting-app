package com.example.androidapp.ui.campaign;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.androidapp.R;
import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.dao.CampaignDao;
import com.example.androidapp.data.entities.Campaign;
import com.example.androidapp.ui.common.GenericAdapter;
import com.example.androidapp.utils.SessionManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;

public class CampaignListActivity extends AppCompatActivity {

    private RecyclerView campaignRecyclerView;
    private CampaignDao campaignDao;
    private SessionManager sessionManager;
    private GenericAdapter<Campaign> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_campaign_list);

        campaignRecyclerView = findViewById(R.id.campaign_recycler_view);
        campaignRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        campaignDao = AppDatabase.getDatabase(this).campaignDao();
        sessionManager = new SessionManager(this);

        FloatingActionButton fab = findViewById(R.id.add_campaign_button);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(CampaignListActivity.this, CampaignDetailActivity.class);
            startActivity(intent);
        });

        loadCampaigns();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCampaigns();
    }

    private void loadCampaigns() {
        String companyId = sessionManager.getCurrentCompanyId();
        if (companyId == null) return;

        adapter = new GenericAdapter<>(new ArrayList<>(), item -> {
            Intent intent = new Intent(CampaignListActivity.this, CampaignDetailActivity.class);
            intent.putExtra("campaign_id", item.getId());
            startActivity(intent);
        });
        campaignRecyclerView.setAdapter(adapter);

        campaignDao.getAllCampaigns(companyId).observe(this, campaigns -> {
            if (campaigns != null) {
                adapter.updateData(campaigns);
            }
        });
    }
}
