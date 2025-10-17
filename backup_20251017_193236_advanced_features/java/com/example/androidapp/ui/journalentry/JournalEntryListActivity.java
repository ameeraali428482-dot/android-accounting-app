package com.example.androidapp.ui.journalentry;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.androidapp.R;
import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.entities.JournalEntry;
import com.example.androidapp.ui.common.GenericAdapter;
import com.example.androidapp.utils.SessionManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;

public class JournalEntryListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private GenericAdapter<JournalEntry> adapter;
    private AppDatabase database;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal_entry_list);

        database = AppDatabase.getInstance(this);
        sessionManager = new SessionManager(this);

        recyclerView = findViewById(R.id.recycler_view_journal_entries);
        FloatingActionButton fabAddJournalEntry = findViewById(R.id.fab_add_journal_entry);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        fabAddJournalEntry.setOnClickListener(v -> {
            Intent intent = new Intent(JournalEntryListActivity.this, JournalEntryDetailActivity.class);
            startActivity(intent);
        });

        loadJournalEntries();
    }

    private void loadJournalEntries() {
        String companyId = sessionManager.getCurrentCompanyId();
        if (companyId == null) return;

        adapter = new GenericAdapter<JournalEntry>(new ArrayList<>(), item -> {
            Intent i = new Intent(JournalEntryListActivity.this, JournalEntryDetailActivity.class);
            i.putExtra("journal_entry_id", item.getId());
            startActivity(i);
        }) {
            @Override
            protected int getLayoutResId() {
                return R.layout.journal_entry_list_row;
            }

            @Override
            protected void bindView(View itemView, JournalEntry journalEntry) {
                // Bind data to views
            }
        };
        recyclerView.setAdapter(adapter);

        database.journalEntryDao().getAllJournalEntries(companyId).observe(this, journalEntries -> {
            if (journalEntries != null) {
                adapter.updateData(journalEntries);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadJournalEntries();
    }
}
