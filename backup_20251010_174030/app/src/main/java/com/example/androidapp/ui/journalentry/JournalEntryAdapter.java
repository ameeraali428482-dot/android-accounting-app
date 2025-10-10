package com.example.androidapp.ui.journalentry;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidapp.R;
import com.example.androidapp.data.entities.JournalEntry;

import java.util.List;

public class JournalEntryAdapter extends RecyclerView.Adapter<JournalEntryAdapter.JournalEntryViewHolder> {

    private List<JournalEntry> journalEntries;
    private OnJournalEntryClickListener listener;

    public interface OnJournalEntryClickListener {
        void onJournalEntryClick(JournalEntry journalEntry);
    }

    public JournalEntryAdapter(List<JournalEntry> journalEntries, OnJournalEntryClickListener listener) {
        this.journalEntries = journalEntries;
        this.listener = listener;
    }

    public void updateData(List<JournalEntry> newJournalEntries) {
        this.journalEntries = newJournalEntries;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public JournalEntryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.journal_entry_list_row, parent, false);
        return new JournalEntryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull JournalEntryViewHolder holder, int position) {
        JournalEntry journalEntry = journalEntries.get(position);
        holder.bind(journalEntry, listener);
    }

    @Override
    public int getItemCount() {
        return journalEntries.size();
    }

    static class JournalEntryViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvDescription, tvTotalDebit, tvTotalCredit;

        public JournalEntryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.journal_entry_date);
            tvDescription = itemView.findViewById(R.id.journal_entry_description);
            tvTotalDebit = itemView.findViewById(R.id.journal_entry_total_debit);
            tvTotalCredit = itemView.findViewById(R.id.journal_entry_total_credit);
        }

        public void bind(final JournalEntry journalEntry, final OnJournalEntryClickListener listener) {
            tvDate.setText(journalEntry.getEntryDate());
            tvDescription.setText(journalEntry.getDescription());
            tvTotalDebit.setText(String.format("مدين: %.2f", journalEntry.getTotalDebit()));
            tvTotalCredit.setText(String.format("دائن: %.2f", journalEntry.getTotalCredit()));

            itemView.setOnClickListener(v -> listener.onJournalEntryClick(journalEntry));
        }
    }
}
