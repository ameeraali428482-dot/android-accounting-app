package com.example.androidapp.ui.item;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.R;
import com.example.androidapp.data.dao.ItemDao;
import com.example.androidapp.data.entities.Item;
import com.example.androidapp.utils.SessionManager;
import java.util.UUID;

public class ItemDetailActivity extends AppCompatActivity {

    private EditText itemNameEditText, itemDescriptionEditText, itemPriceEditText, itemCostEditText, itemCategoryEditText, itemBarcodeEditText;
    private Button saveItemButton, deleteItemButton;
    private ItemDao itemDao;
    private SessionManager sessionManager;
    private String currentItemId = null;
    private String companyId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);

        itemDao = AppDatabase.getDatabase(this).itemDao();
        sessionManager = new SessionManager(this);
        companyId = sessionManager.getCurrentCompanyId();

        itemNameEditText = findViewById(R.id.item_name_edit_text);
        itemDescriptionEditText = findViewById(R.id.item_description_edit_text);
        itemPriceEditText = findViewById(R.id.item_price_edit_text);
        itemCostEditText = findViewById(R.id.item_cost_edit_text);
        itemCategoryEditText = findViewById(R.id.item_category_edit_text);
        itemBarcodeEditText = findViewById(R.id.item_barcode_edit_text);
        saveItemButton = findViewById(R.id.save_item_button);
        deleteItemButton = findViewById(R.id.delete_item_button);

        if (getIntent().hasExtra("itemId")) {
            currentItemId = getIntent().getStringExtra("itemId");
            loadItemDetails(currentItemId);
            deleteItemButton.setVisibility(View.VISIBLE);
        } else {
            deleteItemButton.setVisibility(View.GONE);
        }

        saveItemButton.setOnClickListener(v -> saveItem());
        deleteItemButton.setOnClickListener(v -> deleteItem());
    }

    private void loadItemDetails(String itemId) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            Item item = itemDao.getItemById(itemId, companyId);
            runOnUiThread(() -> {
                if (item != null) {
                    itemNameEditText.setText(item.getName());
                    itemDescriptionEditText.setText(item.getDescription());
                    itemPriceEditText.setText(String.valueOf(item.getPrice()));
                    itemCostEditText.setText(String.valueOf(item.getCost()));
                    itemCategoryEditText.setText(item.getCategory());
                    itemBarcodeEditText.setText(item.getBarcode());
                } else {
                    Toast.makeText(this, "الصنف غير موجود.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        });
    }

    private void saveItem() {
        String name = itemNameEditText.getText().toString().trim();
        String description = itemDescriptionEditText.getText().toString().trim();
        String priceStr = itemPriceEditText.getText().toString().trim();
        String costStr = itemCostEditText.getText().toString().trim();
        String category = itemCategoryEditText.getText().toString().trim();
        String barcode = itemBarcodeEditText.getText().toString().trim();

        if (name.isEmpty() || priceStr.isEmpty() || costStr.isEmpty()) {
            Toast.makeText(this, "الرجاء تعبئة الحقول المطلوبة.", Toast.LENGTH_SHORT).show();
            return;
        }

        double price = Double.parseDouble(priceStr);
        float cost = Float.parseFloat(costStr);

        if (companyId == null) {
            Toast.makeText(this, "لا توجد شركة محددة.", Toast.LENGTH_LONG).show();
            return;
        }

        AppDatabase.databaseWriteExecutor.execute(() -> {
            if (currentItemId == null) {
                Item item = new Item(UUID.randomUUID().toString(), companyId, name, description, price, category, barcode, 0, 0f, cost);
                itemDao.insert(item);
            } else {
                Item item = itemDao.getItemById(currentItemId, companyId);
                if (item != null) {
                    item.setName(name);
                    item.setDescription(description);
                    item.setPrice(price);
                    item.setCost(cost);
                    item.setCategory(category);
                    item.setBarcode(barcode);
                    itemDao.update(item);
                }
            }
            runOnUiThread(() -> {
                Toast.makeText(this, "تم الحفظ بنجاح.", Toast.LENGTH_SHORT).show();
                finish();
            });
        });
    }

    private void deleteItem() {
        if (currentItemId != null) {
            AppDatabase.databaseWriteExecutor.execute(() -> {
                Item item = itemDao.getItemById(currentItemId, companyId);
                if (item != null) {
                    itemDao.delete(item);
                    runOnUiThread(() -> {
                        Toast.makeText(this, "تم حذف الصنف بنجاح.", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                }
            });
        }
    }
}
