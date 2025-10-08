package com.example.androidapp.ui.account;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidapp.R;
import com.example.androidapp.data.entities.Account;

import java.util.List;

public class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.AccountViewHolder> {

    private List<Account> accounts;
    private OnAccountClickListener listener;

    public interface OnAccountClickListener {
        void onAccountClick(Account account);
    }

    public AccountAdapter(List<Account> accounts, OnAccountClickListener listener) {
        this.accounts = accounts;
        this.listener = listener;
    }

    public void updateData(List<Account> newAccounts) {
        this.accounts = newAccounts;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AccountViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.account_list_row, parent, false);
        return new AccountViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AccountViewHolder holder, int position) {
        Account account = accounts.get(position);
        holder.bind(account, listener);
    }

    @Override
    public int getItemCount() {
        return accounts.size();
    }

    static class AccountViewHolder extends RecyclerView.ViewHolder {
        TextView accountName;
        TextView accountNumber;
        TextView accountBalance;
        TextView accountType;

        public AccountViewHolder(@NonNull View itemView) {
            super(itemView);
            accountName = itemView.findViewById(R.id.account_name);
            accountNumber = itemView.findViewById(R.id.account_number);
            accountBalance = itemView.findViewById(R.id.account_balance);
            accountType = itemView.findViewById(R.id.account_type);
        }

        public void bind(final Account account, final OnAccountClickListener listener) {
            accountName.setText(account.getAccountName());
            accountNumber.setText("الرقم: " + account.getAccountNumber());
            accountBalance.setText(String.format("الرصيد: %.2f", account.getCurrentBalance()));
            accountType.setText("النوع: " + account.getAccountType());

            itemView.setOnClickListener(v -> listener.onAccountClick(account));
        }
    }
}
