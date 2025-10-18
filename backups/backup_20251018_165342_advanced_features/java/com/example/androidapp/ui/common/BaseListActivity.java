package com.example.androidapp.ui.common;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.androidapp.R;
import java.util.List;






public abstract class BaseListActivity<T> extends AppCompatActivity {

    protected RecyclerView recyclerView;
    protected GenericAdapter<T> adapter;
    protected ProgressBar loadingProgressBar;
    protected TextView emptyStateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_list);


        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = createAdapter();
        recyclerView.setAdapter(adapter);

        loadData();
    }

    protected abstract GenericAdapter<T> createAdapter();
    protected abstract void loadData();

    protected void showLoading() {
        loadingProgressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        emptyStateTextView.setVisibility(View.GONE);
    }

    protected void hideLoading() {
        loadingProgressBar.setVisibility(View.GONE);
    }

    protected void showData(List<T> data) {
        hideLoading();
        if (data != null && !data.isEmpty()) {
            adapter.setData(data);
            recyclerView.setVisibility(View.VISIBLE);
            emptyStateTextView.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.GONE);
            emptyStateTextView.setVisibility(View.VISIBLE);
            emptyStateTextView.setText(getEmptyStateMessage());
        }
    }

    protected abstract String getEmptyStateMessage();
}

