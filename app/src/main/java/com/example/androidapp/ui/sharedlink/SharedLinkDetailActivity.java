package com.example.androidapp.ui.sharedlink;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.androidapp.data.AppDatabase;
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

        sharedLinkDao = AppDatabase.getDatabase(this).sharedLinkDao();
        sessionManager = new SessionManager(this);

        nameEditText = findViewById(R.id.shared_link_name_edit_text);
        urlEditText = findViewById(R.id.shared_link_url_edit_text);
        expiresAtEditText = findViewById(R.id.shared_link_expires_at_edit_text);
        saveButton = findViewById(R.id.save_shared_link_button);
        deleteButton = findViewById(R.id.delete_shared_link_button);

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
        AppDatabase.databaseWriteExecutor.execute(() -> {
            SharedLink sharedLink = sharedLinkDao.getById(id);
            runOnUiThread(() -> {
                if (sharedLink != null) {
                    nameEditText.setText(sharedLink.getName());
                    urlEditText.setText(sharedLink.getUrl());
                    expiresAtEditText.setText(sharedLink.getExpiresAt());
                }
            });
        });
    }

    private void saveSharedLink() {
        String name = nameEditText.getText().toString().trim();
        String url = urlEditText.getText().toString().trim();
        String expiresAt = expiresAtEditText.getText().toString().trim();
        String companyId = sessionManager.getCurrentCompanyId();

        if (companyId == null) {
            Toast.makeText(this, "خطأ: لم يتم العثور على معرف الشركة.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (name.isEmpty() || url.isEmpty() || expiresAt.isEmpty()) {
            Toast.makeText(this, "الرجاء تعبئة جميع الحقول المطلوبة.", Toast.LENGTH_SHORT).show();
            return;
        }

        AppDatabase.databaseWriteExecutor.execute(() -> {
            if (sharedLinkId == null) {
                SharedLink sharedLink = new SharedLink(UUID.randomUUID().toString(), companyId, url, name, "{}", null, "", expiresAt);
                sharedLinkDao.insert(sharedLink);
            } else {
                SharedLink sharedLink = sharedLinkDao.getById(sharedLinkId);
                if (sharedLink != null) {
                    sharedLink.setToken(url);
                    sharedLink.setType(name);
                    sharedLink.setExpiresAt(expiresAt);
                    sharedLinkDao.update(sharedLink);
                }
            }
            runOnUiThread(() -> {
                Toast.makeText(this, "تم الحفظ بنجاح.", Toast.LENGTH_SHORT).show();
                finish();
            });
        });
    }

    private void deleteSharedLink() {
        if (sharedLinkId != null) {
            AppDatabase.databaseWriteExecutor.execute(() -> {
                SharedLink sharedLink = sharedLinkDao.getById(sharedLinkId);
                if (sharedLink != null) {
                    sharedLinkDao.delete(sharedLink);
                }
                runOnUiThread(() -> {
                    Toast.makeText(this, "تم الحذف بنجاح.", Toast.LENGTH_SHORT).show();
                    finish();
                });
            });
        }
    }
}
