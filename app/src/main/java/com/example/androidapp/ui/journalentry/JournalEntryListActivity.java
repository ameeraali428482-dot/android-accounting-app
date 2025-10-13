package com.example.androidapp.ui.journalentry;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
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

        database = AppDatabase.getDatabase(this);
        sessionManager = new SessionManager(this);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FloatingActionButton fab = findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(v -> {
                Intent intent = new Intent(this, JournalEntryDetailActivity.class);
                startActivity(intent);
            });
        }

        setupRecyclerView();
        loadJournalEntries();
    }

    private void setupRecyclerView() {
        adapter = new GenericAdapter<JournalEntry>(new ArrayList<>(), journalEntry -> {
            Intent intent = new Intent(JournalEntryListActivity.this, JournalEntryDetailActivity.class);
            intent.putExtra("journal_entry_id", journalEntry.getId());
            startActivity(intent);
        }) {
            @Override
            protected int getLayoutResId() {
                return R.layout.journal_entry_list_row;
            }

            @Override
            protected void bindView(View itemView, JournalEntry journalEntry) {
                TextView tvEntryId = itemView.findViewById(R.id.tvEntryId);
                TextView tvEntryDescription = itemView.findViewById(R.id.tvEntryDescription);
                TextView tvEntryDebit = itemView.findViewById(R.id.tvEntryDebit);
                TextView tvEntryCredit = itemView.findViewById(R.id.tvEntryCredit);

                if (tvEntryId != null) tvEntryId.setText("قيد #" + journalEntry.getId());
                if (tvEntryDescription != null) tvEntryDescription.setText(journalEntry.getDescription());
                if (tvEntryDebit != null) tvEntryDebit.setText(String.valueOf(journalEntry.getTotalDebit()));
                if (tvEntryCredit != null) tvEntryCredit.setText(String.valueOf(journalEntry.getTotalCredit()));
            }
        };
        recyclerView.setAdapter(adapter);
    }

    private void loadJournalEntries() {
        String companyId = sessionManager.getCurrentCompanyId();
        if (companyId != null) {
            database.journalEntryDao().getAllJournalEntries(companyId).observe(this, journalEntries -> {
                if (journalEntries != null) {
                    adapter.updateData(journalEntries);
                }
            });
        }
    }
}
