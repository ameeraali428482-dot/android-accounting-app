package com.example.androidapp.ui.item;

import com.example.androidapp.data.entities.Item;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.example.androidapp.App;
import com.example.androidapp.R;
import com.example.androidapp.data.dao.ItemDao;
import com.example.androidapp.data.entities.Item;
import com.example.androidapp.ui.common.BaseListActivity;
import com.example.androidapp.ui.common.GenericAdapter;
import com.example.androidapp.utils.SessionManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;





public class ItemListActivity extends BaseListActivity<Item> {

    private ItemDao itemDao;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);

        itemDao = new ItemDao(App.getDatabaseHelper());
        sessionManager = new SessionManager(this);

        FloatingActionButton fabAddItem = // TODO: Fix findViewById;
        fabAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ItemListActivity.this, ItemDetailActivity.class);
                startActivity(intent);
            }
        });

        // Initialize RecyclerView and other common elements from BaseListActivity
        recyclerView = // TODO: Fix findViewById;
        loadingProgressBar = // TODO: Fix findViewById;
        emptyStateTextView = // TODO: Fix findViewById;

        recyclerView.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this));
        adapter = createAdapter();
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new GenericAdapter.OnItemClickListener<Item>() {
            @Override
            public void onItemClick(Item item) {
                Intent intent = new Intent(ItemListActivity.this, ItemDetailActivity.class);
                intent.putExtra("itemId", item.getId());
                startActivity(intent);
            }
        });

        loadData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData(); // Refresh data when returning to this activity
    }

    @Override
    protected GenericAdapter<Item> createAdapter() {
        return new GenericAdapter<Item>(new ArrayList<>()) {
            @Override
            protected int getLayoutResId() {
                return R.layout.item_list_row;
            }

            @Override
            protected void bindView(View itemView, Item item) {
                TextView itemName = itemView.// TODO: Fix findViewById;
                TextView itemPrice = itemView.// TODO: Fix findViewById;
                TextView itemCategory = itemView.// TODO: Fix findViewById;

                itemName.setText(item.getName());
                itemPrice.setText(String.format("السعر: %.2f", item.getPrice()));
                itemCategory.setText(String.format("الفئة: %s", item.getCategory()));
            }
        };
    }

    @Override
    protected void loadData() {
        showLoading();
        // In a real app, this would be an async operation
        String companyId = sessionManager.getUserDetails().get(SessionManager.KEY_CURRENT_ORG_ID);
        if (companyId != null) {
            List<Item> items = itemDao.getItemsByCompanyId(companyId);
            showData(items);
        } else {
            showData(new ArrayList<>()); // No company selected or logged in
        }
    }

    @Override
    protected String getEmptyStateMessage() {
        return "لا توجد أصناف لعرضها. اضغط على زر الإضافة لإنشاء صنف جديد.";
    }
}

