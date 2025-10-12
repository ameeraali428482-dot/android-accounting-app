package com.example.androidapp.ui.journalentry;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.androidapp.R;
import com.example.androidapp.data.entities.JournalEntry;
import com.example.androidapp.ui.journalentry.viewmodel.JournalEntryViewModel;
import com.example.androidapp.utils.SessionManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;






public class JournalEntryListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private JournalEntryAdapter adapter;
    private JournalEntryViewModel viewModel;
    private SessionManager sessionManager;
    private String companyId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal_entry_list);

        sessionManager = new SessionManager(this);
        companyId = sessionManager.getCompanyId();

        if (companyId == null) {
            Toast.makeText(this, "معرف الشركة غير صالح", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        viewModel = new ViewModelProvider(this).get(JournalEntryViewModel.class);

        initViews();
        setupRecyclerView();
        loadJournalEntries();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initViews() {

        setTitle("القيود اليومية");

        fab.setOnClickListener(v -> {
            Intent intent = new Intent(this, JournalEntryDetailActivity.class);
            startActivity(intent);
        });
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new JournalEntryAdapter(new ArrayList<>(), journalEntry -> {
            // Handle journal entry click - navigate to JournalEntryDetailActivity
            Intent intent = new Intent(JournalEntryListActivity.this, JournalEntryDetailActivity.class);
            intent.putExtra("journal_entry_id", journalEntry.getId());
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);
    }

    private void loadJournalEntries() {
        viewModel.getAllJournalEntries(companyId).observe(this, journalEntries -> {
            if (journalEntries != null) {
                adapter.updateData(journalEntries);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadJournalEntries();
    }
}

