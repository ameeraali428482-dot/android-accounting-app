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
                TextView tvEntryNumber = itemView.findViewById(R.id.tvEntryNumber);
                TextView tvEntryDescription = itemView.findViewById(R.id.tvEntryDescription);
                TextView tvTotalDebit = itemView.findViewById(R.id.tvTotalDebit);
                TextView tvTotalCredit = itemView.findViewById(R.id.tvTotalCredit);

                if (tvEntryNumber != null) tvEntryNumber.setText("#" + journalEntry.getId());
                if (tvEntryDescription != null) tvEntryDescription.setText(journalEntry.getDescription());
                if (tvTotalDebit != null) tvTotalDebit.setText(String.valueOf(journalEntry.getTotalDebit()));
                if (tvTotalCredit != null) tvTotalCredit.setText(String.valueOf(journalEntry.getTotalCredit()));
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
