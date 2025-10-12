package com.example.androidapp.ui.sharedlink;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.androidapp.App;
import com.example.androidapp.R;
import com.example.androidapp.data.dao.SharedLinkDao;
import com.example.androidapp.data.entities.SharedLink;
import com.example.androidapp.utils.SessionManager;
import java.util.UUID;






public class SharedLinkDetailActivity extends AppCompatActivity {

    private EditText nameEditText, urlEditText, expiresAtEditText;
    private Button saveButton, deleteButton;
    private SharedLinkDao sharedLinkDao;
    private SessionManager sessionManager;
    private String sharedLinkId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shared_link_detail);


        sharedLinkDao = new SharedLinkDao(App.getDatabaseHelper());
        sessionManager = new SessionManager(this);

        if (getIntent().hasExtra("shared_link_id")) {
            sharedLinkId = getIntent().getStringExtra("shared_link_id");
            loadSharedLinkData(sharedLinkId);
            deleteButton.setVisibility(View.VISIBLE);
        } else {
            deleteButton.setVisibility(View.GONE);
        }

        saveButton.setOnClickListener(v -> saveSharedLink());
        deleteButton.setOnClickListener(v -> deleteSharedLink());
    }

    private void loadSharedLinkData(String id) {
        SharedLink sharedLink = sharedLinkDao.getById(id);
        if (sharedLink != null) {
            nameEditText.setText(sharedLink.getName());
            urlEditText.setText(sharedLink.getUrl());
            expiresAtEditText.setText(sharedLink.getExpiresAt());
        }
    }

    private void saveSharedLink() {
        String name = nameEditText.getText().toString().trim();
        String url = urlEditText.getText().toString().trim();
        String expiresAt = expiresAtEditText.getText().toString().trim();
        String companyId = sessionManager.getUserDetails().get(SessionManager.KEY_COMPANY_ID);

        if (companyId == null) {
            Toast.makeText(this, "خطأ: لم يتم العثور على معرف الشركة.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (name.isEmpty() || url.isEmpty() || expiresAt.isEmpty()) {
            Toast.makeText(this, "الرجاء تعبئة جميع الحقول المطلوبة.", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedLink sharedLink;
        if (sharedLinkId == null) {
            // New shared link
            sharedLink = new SharedLink(UUID.randomUUID().toString(), companyId, name, url, expiresAt);
            sharedLinkDao.insert(sharedLink);
            Toast.makeText(this, "تم إضافة الرابط المشترك بنجاح.", Toast.LENGTH_SHORT).show();
        } else {
            // Existing shared link
            sharedLink = new SharedLink(sharedLinkId, companyId, name, url, expiresAt);
            sharedLinkDao.update(sharedLink);
            Toast.makeText(this, "تم تحديث الرابط المشترك بنجاح.", Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    private void deleteSharedLink() {
        if (sharedLinkId != null) {
            sharedLinkDao.delete(sharedLinkId);
            Toast.makeText(this, "تم حذف الرابط المشترك بنجاح.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
