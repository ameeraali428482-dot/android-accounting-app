package com.example.androidapp.ui.common;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.androidapp.R;
import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.models.Account;
import com.example.androidapp.models.Item;
import com.example.androidapp.utils.ExcelUtil;
import com.example.androidapp.utils.SessionManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ImportExportActivity extends AppCompatActivity {

    private static final String TAG = "ImportExportActivity";
    public static final String EXTRA_DATA_TYPE = "data_type";

    private String dataType;
    private AppDatabase database;
    private SessionManager sessionManager;

    private ActivityResultLauncher<Intent> importLauncher;
    private ActivityResultLauncher<Intent> exportLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_export);

        database = AppDatabase.getDatabase(this);
        sessionManager = new SessionManager(this);

        dataType = getIntent().getStringExtra(EXTRA_DATA_TYPE);
        if (dataType == null) {
            Toast.makeText(this, "نوع البيانات غير محدد", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setTitle("استيراد/تصدير " + getDataTypeTitle(dataType));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initLaunchers();
        initViews();
    }

    private String getDataTypeTitle(String type) {
        switch (type) {
            case "items":
                return "الأصناف";
            case "accounts":
                return "الحسابات";
            // Add other data types as needed
            default:
                return "البيانات";
        }
    }

    private void initViews() {
        Button btnImport = findViewById(R.id.btn_import_excel);
        Button btnExport = findViewById(R.id.btn_export_excel);

        btnImport.setOnClickListener(v -> openFilePickerForImport());
        btnExport.setOnClickListener(v -> createExcelFileForExport());
    }

    private void initLaunchers() {
        importLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri uri = result.getData().getData();
                        if (uri != null) {
                            importData(uri);
                        }
                    }
                });

        exportLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri uri = result.getData().getData();
                        if (uri != null) {
                            exportData(uri);
                        }
                    }
                });
    }

    private void openFilePickerForImport() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"); // .xlsx
        importLauncher.launch(intent);
    }

    private void createExcelFileForExport() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"); // .xlsx
        intent.putExtra(Intent.EXTRA_TITLE, getDataTypeTitle(dataType) + ".xlsx");
        exportLauncher.launch(intent);
    }

    private void importData(Uri uri) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            List<List<String>> importedData = ExcelUtil.importDataFromExcel(this, uri);
            if (importedData != null && !importedData.isEmpty()) {
                // Assuming the first row is headers, skip it for data insertion
                List<List<String>> dataRows = importedData.subList(1, importedData.size());
                boolean success = false;
                switch (dataType) {
                    case "items":
                        success = importItems(dataRows);
                        break;
                    case "accounts":
                        success = importAccounts(dataRows);
                        break;
                }
                boolean finalSuccess = success;
                runOnUiThread(() -> {
                    if (finalSuccess) {
                        Toast.makeText(this, "تم استيراد " + getDataTypeTitle(dataType) + " بنجاح", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "فشل استيراد " + getDataTypeTitle(dataType), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                runOnUiThread(() -> Toast.makeText(this, "لا توجد بيانات للاستيراد", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private boolean importItems(List<List<String>> dataRows) {
        try {
            for (List<String> row : dataRows) {
                if (row.size() >= 4) { // Assuming name, description, price, quantity
                    String name = row.get(0);
                    String description = row.get(1);
                    double price = Double.parseDouble(row.get(2));
                    int quantity = Integer.parseInt(row.get(3));
                    // Assuming companyId is current company
                    Item item = new Item(sessionManager.getCurrentCompanyId(), name, description, price, quantity);
                    database.itemDao().insert(item);
                }
            }
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error importing items", e);
            return false;
        }
    }

    private boolean importAccounts(List<List<String>> dataRows) {
        try {
            for (List<String> row : dataRows) {
                if (row.size() >= 3) { // Assuming name, type, balance
                    String name = row.get(0);
                    String type = row.get(1);
                    double balance = Double.parseDouble(row.get(2));
                    // Assuming companyId is current company
                    Account account = new Account(sessionManager.getCurrentCompanyId(), name, type, balance);
                    database.accountDao().insert(account);
                }
            }
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error importing accounts", e);
            return false;
        }
    }

    private void exportData(Uri uri) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            List<List<String>> dataToExport = new ArrayList<>();
            // Add headers
            switch (dataType) {
                case "items":
                    dataToExport.add(List.of("الاسم", "الوصف", "السعر", "الكمية"));
                    List<Item> items = database.itemDao().getAllItems(sessionManager.getCurrentCompanyId()).getValue();
                    if (items != null) {
                        for (Item item : items) {
                            dataToExport.add(List.of(item.getName(), item.getDescription(), String.valueOf(item.getPrice()), String.valueOf(item.getQuantity())));
                        }
                    }
                    break;
                case "accounts":
                    dataToExport.add(List.of("الاسم", "النوع", "الرصيد"));
                    List<Account> accounts = database.accountDao().getAllAccounts(sessionManager.getCurrentCompanyId()).getValue();
                    if (accounts != null) {
                        for (Account account : accounts) {
                            dataToExport.add(List.of(account.getName(), account.getType(), String.valueOf(account.getBalance())));
                        }
                    }
                    break;
            }

            boolean success = ExcelUtil.exportDataToExcel(this, uri, dataToExport, getDataTypeTitle(dataType));
            runOnUiThread(() -> {
                if (success) {
                    Toast.makeText(this, "تم تصدير " + getDataTypeTitle(dataType) + " بنجاح", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "فشل تصدير " + getDataTypeTitle(dataType), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
