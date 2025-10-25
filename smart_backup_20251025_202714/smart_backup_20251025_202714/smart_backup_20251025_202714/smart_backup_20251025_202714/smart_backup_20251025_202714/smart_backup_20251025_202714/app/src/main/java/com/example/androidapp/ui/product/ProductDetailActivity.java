package com.example.androidapp.ui.product;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.androidapp.R;
import com.example.androidapp.data.entities.Item;
import com.example.androidapp.data.database.AppDatabase;
import com.example.androidapp.data.dao.ItemDao;

import java.text.DecimalFormat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProductDetailActivity extends AppCompatActivity {

    public static final String EXTRA_PRODUCT_ID = "extra_product_id";
    public static final String EXTRA_PRODUCT_ITEM = "extra_product_item";

    private TextView tvProductName;
    private TextView tvProductCode;
    private TextView tvProductDescription;
    private TextView tvProductPrice;
    private TextView tvProductQuantity;
    private TextView tvProductCategory;
    private TextView tvProductUnit;

    private Item currentItem;
    private ItemDao itemDao;
    private ExecutorService executor;
    private DecimalFormat decimalFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        initializeViews();
        setupToolbar();
        initializeDatabase();
        loadProductData();
    }

    private void initializeViews() {
        tvProductName = findViewById(R.id.tv_product_name);
        tvProductCode = findViewById(R.id.tv_product_code);
        tvProductDescription = findViewById(R.id.tv_product_description);
        tvProductPrice = findViewById(R.id.tv_product_price);
        tvProductQuantity = findViewById(R.id.tv_product_quantity);
        tvProductCategory = findViewById(R.id.tv_product_category);
        tvProductUnit = findViewById(R.id.tv_product_unit);

        decimalFormat = new DecimalFormat("#,##0.00");
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("تفاصيل المنتج");
        }
    }

    private void initializeDatabase() {
        AppDatabase database = AppDatabase.getDatabase(this);
        itemDao = database.itemDao();
        executor = Executors.newFixedThreadPool(2);
    }

    private void loadProductData() {
        Intent intent = getIntent();
        
        // Check if Item object was passed directly
        if (intent.hasExtra(EXTRA_PRODUCT_ITEM)) {
            currentItem = (Item) intent.getSerializableExtra(EXTRA_PRODUCT_ITEM);
            if (currentItem != null) {
                displayProductData(currentItem);
                return;
            }
        }

        // Check if product ID was passed
        if (intent.hasExtra(EXTRA_PRODUCT_ID)) {
            String productId = intent.getStringExtra(EXTRA_PRODUCT_ID);
            if (productId != null && !productId.trim().isEmpty()) {
                loadProductFromDatabase(productId);
                return;
            }
        }

        // No valid data provided
        Toast.makeText(this, "لم يتم العثور على بيانات المنتج", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void loadProductFromDatabase(String productId) {
        executor.execute(() -> {
            try {
                Item item = itemDao.getById(productId);
                runOnUiThread(() -> {
                    if (item != null) {
                        currentItem = item;
                        displayProductData(item);
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

    private void displayProductData(Item item) {
        if (item == null) return;

        // Product name
        tvProductName.setText(item.getName() != null ? item.getName() : "غير محدد");

        // Product code
        tvProductCode.setText(item.getCode() != null ? item.getCode() : "غير محدد");

        // Product description
        tvProductDescription.setText(item.getDescription() != null ? item.getDescription() : "لا يوجد وصف");

        // Product price
        tvProductPrice.setText(getString(R.string.price_format, decimalFormat.format(item.getPrice())));

        // Product quantity
        tvProductQuantity.setText(getString(R.string.quantity_format, item.getQuantity()));

        // Product category (you might need to fetch this from Category entity)
        tvProductCategory.setText(item.getCategoryId() != null ? item.getCategoryId() : "غير محدد");

        // Product unit
        tvProductUnit.setText(item.getUnitId() != null ? item.getUnitId() : "غير محدد");

        // Update toolbar title
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(item.getName() != null ? item.getName() : "تفاصيل المنتج");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_product_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_edit:
                editProduct();
                return true;
            case R.id.action_share:
                shareProduct();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void editProduct() {
        if (currentItem != null) {
            // Launch edit activity
            Intent intent = new Intent(this, ProductEditActivity.class);
            intent.putExtra("ITEM_ID", currentItem.getId());
            startActivity(intent);
        }
    }

    private void shareProduct() {
        if (currentItem != null) {
            String shareText = createShareText(currentItem);
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "تفاصيل المنتج");
            startActivity(Intent.createChooser(shareIntent, "مشاركة المنتج"));
        }
    }

    private String createShareText(Item item) {
        StringBuilder shareText = new StringBuilder();
        shareText.append("🛍️ تفاصيل المنتج:\n\n");
        shareText.append("📦 اسم المنتج: ").append(item.getName()).append("\n");
        shareText.append("🔢 كود المنتج: ").append(item.getCode()).append("\n");
        shareText.append("💰 السعر: ").append(decimalFormat.format(item.getPrice())).append("\n");
        shareText.append("📊 الكمية: ").append(item.getQuantity()).append("\n");
        if (item.getDescription() != null && !item.getDescription().trim().isEmpty()) {
            shareText.append("📝 الوصف: ").append(item.getDescription()).append("\n");
        }
        return shareText.toString();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
        }
    }
}
