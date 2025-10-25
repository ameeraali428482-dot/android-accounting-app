package com.example.androidapp.ui.item;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.androidapp.R;
import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.dao.ItemDao;
import com.example.androidapp.data.entities.Item;
import com.example.androidapp.ui.common.BaseListActivity;
import com.example.androidapp.ui.common.GenericAdapter;
import com.example.androidapp.utils.SessionManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;

public class ItemListActivity extends BaseListActivity<Item> {

    private ItemDao itemDao;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);

        itemDao = AppDatabase.getDatabase(this).itemDao();
        sessionManager = new SessionManager(this);

        FloatingActionButton fabAddItem = findViewById(R.id.fab_add_item);
        fabAddItem.setOnClickListener(view -> {
            Intent intent = new Intent(ItemListActivity.this, ItemDetailActivity.class);
            startActivity(intent);
        });

        recyclerView = findViewById(R.id.recyclerView);
        loadingProgressBar = findViewById(R.id.loadingProgressBar);
        emptyStateTextView = findViewById(R.id.emptyStateTextView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = createAdapter();
        recyclerView.setAdapter(adapter);

        loadData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    @Override
    protected GenericAdapter<Item> createAdapter() {
        return new GenericAdapter<Item>(new ArrayList<>(), item -> {
            Intent intent = new Intent(ItemListActivity.this, ItemDetailActivity.class);
            intent.putExtra("itemId", item.getId());
            startActivity(intent);
        }) {
            @Override
            protected int getLayoutResId() {
                return R.layout.item_list_row;
            }

            @Override
            protected void bindView(View itemView, Item item) {
                TextView itemName = itemView.findViewById(R.id.item_name);
                TextView itemPrice = itemView.findViewById(R.id.item_price);
                TextView itemCategory = itemView.findViewById(R.id.item_category);

                itemName.setText(item.getName());
                itemPrice.setText(String.format("السعر: %.2f", item.getPrice()));
                itemCategory.setText(String.format("الفئة: %s", item.getCategory()));
            }
        };
    }

    @Override
    protected void loadData() {
        showLoading();
        String companyId = sessionManager.getCurrentCompanyId();
        if (companyId != null) {
            itemDao.getAllItems(companyId).observe(this, this::showData);
        } else {
            showData(new ArrayList<>());
        }
    }

    @Override
    protected String getEmptyStateMessage() {
        return "لا توجد أصناف لعرضها. اضغط على زر الإضافة لإنشاء صنف جديد.";
    }
}
