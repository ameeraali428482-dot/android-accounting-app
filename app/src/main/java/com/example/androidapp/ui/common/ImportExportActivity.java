package com.example.androidapp.ui.common;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.androidapp.R;
import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.entities.Item;
import com.example.androidapp.utils.ExcelUtil;
import com.example.androidapp.utils.SessionManager;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;

public class ImportExportActivity extends AppCompatActivity {
    private static final int PICK = 1, CREATE = 2;
    private Button btnImport, btnExport;
    private AppDatabase db;
    private SessionManager sm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_export);

        db  = AppDatabase.getInstance(this);
        sm  = new SessionManager(this);

        btnImport = findViewById(R.id.btnImport);
        btnExport = findViewById(R.id.btnExport);

        btnImport.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            i.setType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            startActivityForResult(i, PICK);
        });

        btnExport.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_CREATE_DOCUMENT);
            i.setType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            i.putExtra(Intent.EXTRA_TITLE, "export.xlsx");
            startActivityForResult(i, CREATE);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || data == null || data.getData() == null) return;
        Uri uri = data.getData();
        if (requestCode == PICK) importExcel(uri);
        if (requestCode == CREATE) exportExcel(uri);
    }

    private void importExcel(Uri uri) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                List<List<String>> rows = ExcelUtil.importDataFromExcel(this, uri);
                String companyId = sm.getUserDetails().get(SessionManager.KEY_COMPANY_ID);
                for (int i = 1; i < rows.size(); i++) {
                    List<String> row = rows.get(i);
                    if (row.size() < 4) continue;
                    Item item = new Item();
                    item.setId(UUID.randomUUID().toString());
                    item.setCompanyId(companyId);
                    item.setItemName(row.get(0));
                    item.setDescription(row.get(1));
                    item.setPrice(Double.parseDouble(row.get(2)));
                    item.setCategory(row.get(3));
                    db.itemDao().insert(item);
                }
                runOnUiThread(() -> Toast.makeText(this, "تم الاستيراد", Toast.LENGTH_SHORT).show());
            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(this, "خطأ في الاستيراد", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void exportExcel(Uri uri) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                String companyId = sm.getUserDetails().get(SessionManager.KEY_COMPANY_ID);
                List<Item> list = db.itemDao().getAllItemsSync(companyId);
                List<List<String>> data = new java.util.ArrayList<>();
                List<String> header = new java.util.ArrayList<>();
                header.add("Name"); header.add("Description"); header.add("Price"); header.add("Category");
                data.add(header);
                for (Item i : list) {
                    List<String> row = new java.util.ArrayList<>();
                    row.add(i.getItemName());
                    row.add(i.getDescription());
                    row.add(String.valueOf(i.getPrice()));
                    row.add(i.getCategory());
                    data.add(row);
                }
                ExcelUtil.exportDataToExcel(this, uri, data, "Items");
                runOnUiThread(() -> Toast.makeText(this, "تم التصدير", Toast.LENGTH_SHORT).show());
            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(this, "خطأ في التصدير", Toast.LENGTH_SHORT).show());
            }
        });
    }
}
