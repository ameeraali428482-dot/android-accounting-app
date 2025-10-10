package com.example.androidapp.ui.item;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.androidapp.App;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);

        itemDao = new ItemDao(App.getDatabaseHelper());
        sessionManager = new SessionManager(this);

        itemNameEditText = findViewById(R.id.item_name_edit_text);
        itemDescriptionEditText = findViewById(R.id.item_description_edit_text);
        itemPriceEditText = findViewById(R.id.item_price_edit_text);
        itemCostEditText = findViewById(R.id.item_cost_edit_text);
        itemCategoryEditText = findViewById(R.id.item_category_edit_text);
        itemBarcodeEditText = findViewById(R.id.item_barcode_edit_text);
        saveItemButton = findViewById(R.id.save_item_button);
        deleteItemButton = findViewById(R.id.delete_item_button);

        // Check if we are editing an existing item
        if (getIntent().hasExtra("itemId")) {
            currentItemId = getIntent().getStringExtra("itemId");
            loadItemDetails(currentItemId);
            deleteItemButton.setVisibility(View.VISIBLE);
        } else {
            deleteItemButton.setVisibility(View.GONE);
        }

        saveItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveItem();
            }
        });

        deleteItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteItem();
            }
        });
    }

    private void loadItemDetails(String itemId) {
        Item item = itemDao.getById(itemId);
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
    }

    private void saveItem() {
        String name = itemNameEditText.getText().toString().trim();
        String description = itemDescriptionEditText.getText().toString().trim();
        String priceStr = itemPriceEditText.getText().toString().trim();
        String costStr = itemCostEditText.getText().toString().trim();
        String category = itemCategoryEditText.getText().toString().trim();
        String barcode = itemBarcodeEditText.getText().toString().trim();

        if (name.isEmpty()) {
            itemNameEditText.setError("اسم الصنف مطلوب.");
            itemNameEditText.requestFocus();
            return;
        }
        if (priceStr.isEmpty()) {
            itemPriceEditText.setError("السعر مطلوب.");
            itemPriceEditText.requestFocus();
            return;
        }
        if (costStr.isEmpty()) {
            itemCostEditText.setError("التكلفة مطلوبة.");
            itemCostEditText.requestFocus();
            return;
        }

        float price = Float.parseFloat(priceStr);
        float cost = Float.parseFloat(costStr);

        String companyId = sessionManager.getUserDetails().get(SessionManager.KEY_CURRENT_ORG_ID);
        if (companyId == null) {
            Toast.makeText(this, "لا توجد شركة محددة. يرجى تسجيل الدخول واختيار شركة.", Toast.LENGTH_LONG).show();
            return;
        }

        Item item;
        if (currentItemId == null) {
            // New item
            item = new Item(UUID.randomUUID().toString(), companyId, name, description, price, cost, category, barcode);
            long result = itemDao.insert(item);
            if (result != -1) {
                Toast.makeText(this, "تم إضافة الصنف بنجاح.", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "فشل إضافة الصنف.", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Existing item
            item = new Item(currentItemId, companyId, name, description, price, cost, category, barcode);
            int result = itemDao.update(item);
            if (result > 0) {
                Toast.makeText(this, "تم تحديث الصنف بنجاح.", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "فشل تحديث الصنف.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void deleteItem() {
        if (currentItemId != null) {
            int result = itemDao.delete(currentItemId);
            if (result > 0) {
                Toast.makeText(this, "تم حذف الصنف بنجاح.", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "فشل حذف الصنف.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

