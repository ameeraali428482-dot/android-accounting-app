package com.example.androidapp.ui.campaign;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.androidapp.R;
import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.dao.CampaignDao;
import com.example.androidapp.data.entities.Campaign;
import com.example.androidapp.utils.SessionManager;
import java.util.UUID;

public class CampaignDetailActivity extends AppCompatActivity {

    private EditText nameEditText, typeEditText, descriptionEditText, startDateEditText, endDateEditText, statusEditText;
    private Button saveButton, deleteButton;
    private CampaignDao campaignDao;
    private SessionManager sessionManager;
    private String campaignId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_campaign_detail);

        campaignDao = AppDatabase.getDatabase(this).campaignDao();
        sessionManager = new SessionManager(this);

        nameEditText = findViewById(R.id.campaign_name_edit_text);
        typeEditText = findViewById(R.id.campaign_type_edit_text);
        descriptionEditText = findViewById(R.id.campaign_description_edit_text);
        startDateEditText = findViewById(R.id.campaign_start_date_edit_text);
        endDateEditText = findViewById(R.id.campaign_end_date_edit_text);
        statusEditText = findViewById(R.id.campaign_status_edit_text);
        saveButton = findViewById(R.id.save_campaign_button);
        deleteButton = findViewById(R.id.delete_campaign_button);

        if (getIntent().hasExtra("campaign_id")) {
            campaignId = getIntent().getStringExtra("campaign_id");
            loadCampaignData(campaignId);
            deleteButton.setVisibility(View.VISIBLE);
        } else {
            deleteButton.setVisibility(View.GONE);
        }

        saveButton.setOnClickListener(v -> saveCampaign());
        deleteButton.setOnClickListener(v -> deleteCampaign());
    }

    private void loadCampaignData(String id) {
        campaignDao.getCampaignById(id, sessionManager.getCurrentCompanyId()).observe(this, campaign -> {
            if (campaign != null) {
                nameEditText.setText(campaign.getName());
                typeEditText.setText(campaign.getType());
                descriptionEditText.setText(campaign.getDescription());
                startDateEditText.setText(campaign.getStartDate());
                endDateEditText.setText(campaign.getEndDate());
                statusEditText.setText(campaign.getStatus());
            }
        });
    }

    private void saveCampaign() {
        String name = nameEditText.getText().toString().trim();
        String type = typeEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();
        String startDate = startDateEditText.getText().toString().trim();
        String endDate = endDateEditText.getText().toString().trim();
        String status = statusEditText.getText().toString().trim();
        String companyId = sessionManager.getCurrentCompanyId();

        if (companyId == null) {
            Toast.makeText(this, "خطأ: لم يتم العثور على معرف الشركة.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (name.isEmpty() || type.isEmpty() || startDate.isEmpty() || endDate.isEmpty() || status.isEmpty()) {
            Toast.makeText(this, "الرجاء تعبئة جميع الحقول المطلوبة.", Toast.LENGTH_SHORT).show();
            return;
        }

        AppDatabase.databaseWriteExecutor.execute(() -> {
            if (campaignId == null) {
                Campaign campaign = new Campaign(UUID.randomUUID().toString(), companyId, name, type, description, startDate, endDate, status);
                campaignDao.insert(campaign);
            } else {
                campaignDao.getCampaignById(campaignId, companyId).observe(this, campaign -> {
                    if (campaign != null) {
                        campaign.setName(name);
                        campaign.setType(type);
                        campaign.setDescription(description);
                        campaign.setStartDate(startDate);
                        campaign.setEndDate(endDate);
                        campaign.setStatus(status);
                        AppDatabase.databaseWriteExecutor.execute(() -> campaignDao.update(campaign));
                    }
                });
            }
            runOnUiThread(() -> {
                Toast.makeText(this, "تم الحفظ بنجاح.", Toast.LENGTH_SHORT).show();
                finish();
            });
        });
    }

    private void deleteCampaign() {
        if (campaignId != null) {
            campaignDao.getCampaignById(campaignId, sessionManager.getCurrentCompanyId()).observe(this, campaign -> {
                if (campaign != null) {
                    AppDatabase.databaseWriteExecutor.execute(() -> campaignDao.delete(campaign));
                    runOnUiThread(() -> {
                        Toast.makeText(this, "تم الحذف بنجاح.", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                }
            });
        }
    }
}
