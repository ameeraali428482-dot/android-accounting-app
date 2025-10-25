package com.example.androidapp.ui.product;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.androidapp.R;
import com.example.androidapp.data.entities.Item;
import com.example.androidapp.data.database.AppDatabase;
import com.example.androidapp.data.dao.ItemDao;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProductEditActivity extends AppCompatActivity {

    public static final String EXTRA_ITEM_ID = "ITEM_ID";

    private EditText etProductName;
    private EditText etProductCode;
    private EditText etProductDescription;
    private EditText etProductPrice;
    private EditText etProductQuantity;

    private Item currentItem;
    private ItemDao itemDao;
    private ExecutorService executor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_edit);

        initializeViews();
        setupToolbar();
        initializeDatabase();
        loadItemData();
    }

    private void initializeViews() {
        etProductName = findViewById(R.id.et_product_name);
        etProductCode = findViewById(R.id.et_product_code);
        etProductDescription = findViewById(R.id.et_product_description);
        etProductPrice = findViewById(R.id.et_product_price);
        etProductQuantity = findViewById(R.id.et_product_quantity);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("تعديل المنتج");
        }
    }

    private void initializeDatabase() {
        AppDatabase database = AppDatabase.getDatabase(this);
        itemDao = database.itemDao();
        executor = Executors.newFixedThreadPool(2);
    }

    private void loadItemData() {
        String itemId = getIntent().getStringExtra(EXTRA_ITEM_ID);
        if (itemId == null || itemId.trim().isEmpty()) {
            Toast.makeText(this, "معرف المنتج غير صحيح", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        executor.execute(() -> {
            try {
                Item item = itemDao.getById(itemId);
                runOnUiThread(() -> {
                    if (item != null) {
                        currentItem = item;
                        populateFields(item);
                    } else {
                        Toast.makeText(this, "المنتج غير موجود", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "خطأ في تحميل بيانات المنتج: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    finish();
                });
            }
        });
    }

    private void populateFields(Item item) {
        etProductName.setText(item.getName());
        etProductCode.setText(item.getCode());
        etProductDescription.setText(item.getDescription());
        etProductPrice.setText(String.valueOf(item.getPrice()));
        etProductQuantity.setText(String.valueOf(item.getQuantity()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_save:
                saveProduct();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void saveProduct() {
        if (!validateInputs()) {
            return;
        }

        // Update current item with new values
        currentItem.setName(etProductName.getText().toString().trim());
        currentItem.setCode(etProductCode.getText().toString().trim());
        currentItem.setDescription(etProductDescription.getText().toString().trim());
        
        try {
            currentItem.setPrice(Double.parseDouble(etProductPrice.getText().toString().trim()));
            currentItem.setQuantity(Double.parseDouble(etProductQuantity.getText().toString().trim()));
        } catch (NumberFormatException e) {
            Toast.makeText(this, "يرجى إدخال أرقام صحيحة للسعر والكمية", Toast.LENGTH_SHORT).show();
            return;
        }

        currentItem.setUpdatedDate(new Date());

        executor.execute(() -> {
            try {
                itemDao.update(currentItem);
                runOnUiThread(() -> {
                    Toast.makeText(this, "تم حفظ التعديلات بنجاح", Toast.LENGTH_SHORT).show();
                    finish();
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "خطأ في حفظ التعديلات: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private boolean validateInputs() {
        if (etProductName.getText().toString().trim().isEmpty()) {
            etProductName.setError("اسم المنتج مطلوب");
            etProductName.requestFocus();
            return false;
        }

        if (etProductCode.getText().toString().trim().isEmpty()) {
            etProductCode.setError("كود المنتج مطلوب");
            etProductCode.requestFocus();
            return false;
        }

        if (etProductPrice.getText().toString().trim().isEmpty()) {
            etProductPrice.setError("السعر مطلوب");
            etProductPrice.requestFocus();
            return false;
        }

        if (etProductQuantity.getText().toString().trim().isEmpty()) {
            etProductQuantity.setError("الكمية مطلوبة");
            etProductQuantity.requestFocus();
            return false;
        }

        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
        }
    }
}