package com.example.androidapp.ui.common;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.androidapp.R;
import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.entities.Account;
import com.example.androidapp.data.entities.Item;
import com.example.androidapp.utils.SessionManager;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;

public class ImportExportActivity extends AppCompatActivity {
    private static final int PICK_EXCEL_FILE = 1;
    private static final int CREATE_EXCEL_FILE = 2;

    private Button btnImport;
    private Button btnExport;
    private AppDatabase database;
    private SessionManager sessionManager;
    private String currentImportType = "items";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_export);

        database = AppDatabase.getInstance(this);
        sessionManager = new SessionManager(this);

        btnImport = findViewById(R.id.btnImport);
        btnExport = findViewById(R.id.btnExport);

        btnImport.setOnClickListener(v -> openFilePickerForImport());
        btnExport.setOnClickListener(v -> createExcelFileForExport());
    }

    private void openFilePickerForImport() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        startActivityForResult(intent, PICK_EXCEL_FILE);
    }

    private void createExcelFileForExport() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        intent.putExtra(Intent.EXTRA_TITLE, "export.xlsx");
        startActivityForResult(intent, CREATE_EXCEL_FILE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                if (requestCode == PICK_EXCEL_FILE) {
                    importFromExcel(uri);
                } else if (requestCode == CREATE_EXCEL_FILE) {
                    exportToExcel(uri);
                }
            }
        }
    }

    private void importFromExcel(Uri uri) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                InputStream inputStream = getContentResolver().openInputStream(uri);
                Workbook workbook = new XSSFWorkbook(inputStream);
                Sheet sheet = workbook.getSheetAt(0);

                String companyId = sessionManager.getUserDetails().get(SessionManager.KEY_COMPANY_ID);

                if (currentImportType.equals("items")) {
                    for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
                        Row row = sheet.getRow(i);
                        if (row != null) {
                            String name = row.getCell(0).getStringCellValue();
                            String description = row.getCell(1).getStringCellValue();
                            double price = row.getCell(2).getNumericCellValue();
                            String category = row.getCell(3).getStringCellValue();

                            Item item = new Item(
                                UUID.randomUUID().toString(),
                                companyId,
                                name,
                                description,
                                price,
                                category,
                                ""
                            );
                            database.itemDao().insert(item);
                        }
                    }
                } else if (currentImportType.equals("accounts")) {
                    for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
                        Row row = sheet.getRow(i);
                        if (row != null) {
                            String name = row.getCell(0).getStringCellValue();
                            String type = row.getCell(1).getStringCellValue();
                            String code = row.getCell(2).getStringCellValue();

                            Account account = new Account(
                                UUID.randomUUID().toString(),
                                companyId,
                                name,
                                code,
                                false,
                                type,
                                "",
                                "0",
                                "0",
                                ""
                            );
                            database.accountDao().insert(account);
                        }
                    }
                }

                workbook.close();
                inputStream.close();

                runOnUiThread(() -> Toast.makeText(this, "تم الاستيراد بنجاح", Toast.LENGTH_SHORT).show());
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "فشل الاستيراد: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void exportToExcel(Uri uri) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                Workbook workbook = new XSSFWorkbook();
                Sheet sheet = workbook.createSheet("Export");

                String companyId = sessionManager.getUserDetails().get(SessionManager.KEY_COMPANY_ID);
                List<List<String>> dataToExport = new ArrayList<>();

                if (currentImportType.equals("items")) {
                    database.itemDao().getAllItems(companyId).observeForever(items -> {
                        for (Item item : items) {
                            List<String> row = new ArrayList<>();
                            row.add(item.getItemName());
                            row.add(item.getDescription());
                            row.add(String.valueOf(item.getPrice()));
                            row.add(item.getCategory());
                            dataToExport.add(row);
                        }
                    });
                } else if (currentImportType.equals("accounts")) {
                    database.accountDao().getAllAccounts(companyId).observeForever(accounts -> {
                        for (Account account : accounts) {
                            List<String> row = new ArrayList<>();
                            row.add(account.getAccountName());
                            row.add(account.getAccountType());
                            row.add(account.getAccountCode());
                            dataToExport.add(row);
                        }
                    });
                }

                for (int i = 0; i < dataToExport.size(); i++) {
                    Row row = sheet.createRow(i);
                    List<String> rowData = dataToExport.get(i);
                    for (int j = 0; j < rowData.size(); j++) {
                        row.createCell(j).setCellValue(rowData.get(j));
                    }
                }

                OutputStream outputStream = getContentResolver().openOutputStream(uri);
                workbook.write(outputStream);
                workbook.close();
                outputStream.close();

                runOnUiThread(() -> Toast.makeText(this, "تم التصدير بنجاح", Toast.LENGTH_SHORT).show());
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "فشل التصدير: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });
    }
}
