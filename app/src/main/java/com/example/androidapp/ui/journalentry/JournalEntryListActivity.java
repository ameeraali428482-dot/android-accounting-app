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
    private FloatingActionButton fabAddJournalEntry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal_entry_list);

        database = AppDatabase.getInstance(this);
        sessionManager = new SessionManager(this);

        recyclerView = findViewById(R.id.recyclerView);
        fabAddJournalEntry = findViewById(R.id.fabAddJournalEntry);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        fabAddJournalEntry.setOnClickListener(v -> {
            Intent intent = new Intent(JournalEntryListActivity.this, JournalEntryDetailActivity.class);
            startActivity(intent);
        });

        loadJournalEntries();
    }

    private void loadJournalEntries() {
        String companyId = sessionManager.getUserDetails().get(SessionManager.KEY_COMPANY_ID);

        adapter = new GenericAdapter<>(new ArrayList<>(), new GenericAdapter.OnItemClickListener<JournalEntry>() {
            @Override
            public void onItemClick(JournalEntry item) {
                Intent intent = new Intent(JournalEntryListActivity.this, JournalEntryDetailActivity.class);
                intent.putExtra("journalentry_id", item.getId());
                startActivity(intent);
            }
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

                tvEntryNumber.setText(journalEntry.getEntryNumber());
                tvEntryDescription.setText(journalEntry.getDescription());
                tvTotalDebit.setText(String.valueOf(journalEntry.getTotalDebit()));
                tvTotalCredit.setText(String.valueOf(journalEntry.getTotalCredit()));
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
