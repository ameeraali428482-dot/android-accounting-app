package com.example.androidapp.ui.campaign;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.androidapp.App;
import com.example.androidapp.R;
import com.example.androidapp.data.dao.CampaignDao;
import com.example.androidapp.data.entities.Campaign;
import com.example.androidapp.ui.common.GenericAdapter;
import com.example.androidapp.utils.SessionManager;
import java.util.List;






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

        campaignDao = new CampaignDao(App.getDatabaseHelper());
        sessionManager = new SessionManager(this);

        findViewById(R.id.add_campaign_button).setOnClickListener(v -> {
            Intent intent = new Intent(CampaignListActivity.this, CampaignDetailActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCampaigns();
    }

    private void loadCampaigns() {
        String companyId = sessionManager.getUserDetails().get(SessionManager.KEY_COMPANY_ID);
        if (companyId == null) {
            // Handle error: no company ID found
            return;
        }

        List<Campaign> campaigns = campaignDao.getCampaignsByCompanyId(companyId);

        adapter = new GenericAdapter<Campaign>(campaigns) {
            @Override
            protected int getLayoutResId() {
                return R.layout.campaign_list_row;
            }

            @Override
            protected void bindView(View itemView, Campaign campaign) {
                TextView campaignName = itemView.findViewById(R.id.campaign_name);
                TextView campaignType = itemView.findViewById(R.id.campaign_type);
                TextView campaignStatus = itemView.findViewById(R.id.campaign_status);

                campaignName.setText(campaign.getName());
                campaignType.setText("النوع: " + campaign.getType());
                campaignStatus.setText("الحالة: " + campaign.getStatus());

                itemView.setOnClickListener(v -> {
                    Intent intent = new Intent(CampaignListActivity.this, CampaignDetailActivity.class);
                    intent.putExtra("campaign_id", campaign.getId());
                    startActivity(intent);
                });
            }
        };
        campaignRecyclerView.setAdapter(adapter);
    }
}
