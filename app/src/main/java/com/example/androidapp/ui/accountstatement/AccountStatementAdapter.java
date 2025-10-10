import java.util.Date;
package com.example.androidapp.ui.accountstatement;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidapp.R;
import com.example.androidapp.data.entities.AccountStatement;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class AccountStatementAdapter extends RecyclerView.Adapter<AccountStatementAdapter.AccountStatementViewHolder> {

    private List<AccountStatement> statements;
    private List<AccountStatement> selectedStatements;
    private OnItemClickListener clickListener;
    private OnItemLongClickListener longClickListener;
    private boolean isSelectionMode = false;

    public interface OnItemClickListener {
        void onItemClick(AccountStatement statement);
    }

    public interface OnItemLongClickListener {
        boolean onItemLongClick(AccountStatement statement);
    }

    public AccountStatementAdapter(List<AccountStatement> statements, OnItemClickListener clickListener, OnItemLongClickListener longClickListener) {
        this.statements = statements;
        this.selectedStatements = new ArrayList<>();
        this.clickListener = clickListener;
        this.longClickListener = longClickListener;
    }

    public void updateData(List<AccountStatement> newStatements) {
        this.statements = newStatements;
        notifyDataSetChanged();
    }

    public void toggleSelection(AccountStatement statement) {
        if (selectedStatements.contains(statement)) {
            selectedStatements.remove(statement);
        } else {
            selectedStatements.add(statement);
        }
        notifyDataSetChanged();
    }

    public void clearSelection() {
        selectedStatements.clear();
        isSelectionMode = false;
        notifyDataSetChanged();
    }

    public int getSelectedItemCount() {
        return selectedStatements.size();
    }

    public List<AccountStatement> getSelectedStatements() {
        return new ArrayList<>(selectedStatements);
    }

    public void setSelectionMode(boolean selectionMode) {
        isSelectionMode = selectionMode;
        if (!isSelectionMode) {
            selectedStatements.clear();
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AccountStatementViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.account_statement_list_row, parent, false);
        return new AccountStatementViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AccountStatementViewHolder holder, int position) {
        AccountStatement statement = statements.get(position);
        holder.bind(statement, clickListener, longClickListener, selectedStatements.contains(statement), isSelectionMode, position);
    }

    @Override
    public int getItemCount() {
        return statements.size();
    }

    static class AccountStatementViewHolder extends RecyclerView.ViewHolder {
        TextView tvSerial, tvDate, tvDescription, tvDebit, tvCredit, tvBalance;
        CheckBox cbSelect;
        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");

        public AccountStatementViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSerial = itemView.findViewById(R.id.tv_serial);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvDescription = itemView.findViewById(R.id.tv_description);
            tvDebit = itemView.findViewById(R.id.tv_debit);
            tvCredit = itemView.findViewById(R.id.tv_credit);
            tvBalance = itemView.findViewById(R.id.tv_balance);
            cbSelect = itemView.findViewById(R.id.cb_select);
        }

        public void bind(final AccountStatement statement, final OnItemClickListener clickListener, final OnItemLongClickListener longClickListener, boolean isSelected, boolean isSelectionMode, int position) {
            tvSerial.setText(String.valueOf(position + 1) + ".");
            tvDate.setText(statement.getTransactionDate());
            tvDescription.setText(statement.getDescription());
            tvDebit.setText(decimalFormat.format(statement.getDebit()));
            tvCredit.setText(decimalFormat.format(statement.getCredit()));
            tvBalance.setText(decimalFormat.format(statement.getRunningBalance()));

            if (statement.getRunningBalance() < 0) {
                tvBalance.setTextColor(Color.RED);
            } else {
                tvBalance.setTextColor(Color.BLACK);
            }

            cbSelect.setChecked(isSelected);
            cbSelect.setVisibility(isSelectionMode ? View.VISIBLE : View.GONE);

            itemView.setOnClickListener(v -> clickListener.onItemClick(statement));
            itemView.setOnLongClickListener(v -> longClickListener.onItemLongClick(statement));

            itemView.setBackgroundColor(isSelected ? itemView.getContext().getResources().getColor(R.color.selected_item_background, null) : Color.TRANSPARENT);
        }
    }
}
