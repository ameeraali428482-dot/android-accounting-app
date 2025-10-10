package com.example.androidapp.ui.receipt;

import java.util.Date;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.androidapp.R;
import com.example.androidapp.data.entities.Receipt;
import java.util.ArrayList;
import java.util.List;






public class ReceiptAdapter extends RecyclerView.Adapter<ReceiptAdapter.ReceiptViewHolder> {

    private List<Receipt> receipts;
    private LayoutInflater inflater;
    private OnReceiptClickListener listener;

    public interface OnReceiptClickListener {
        void onReceiptClick(Receipt receipt);
    }

    public ReceiptAdapter(Context context, OnReceiptClickListener listener) {
        this.inflater = LayoutInflater.from(context);
        this.receipts = new ArrayList<>();
        this.listener = listener;
    }

    public void setReceipts(List<Receipt> receipts) {
        this.receipts = receipts;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ReceiptViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.item_receipt, parent, false);
        return new ReceiptViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ReceiptViewHolder holder, int position) {
        Receipt currentReceipt = receipts.get(position);
        holder.bind(currentReceipt, listener);
    }

    @Override
    public int getItemCount() {
        return receipts.size();
    }

    static class ReceiptViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvReceiptId;
        private final TextView tvAmount;
        private final TextView tvDate;

        public ReceiptViewHolder(@NonNull View itemView) {
            super(itemView);
            tvReceiptId = itemView.// TODO: Fix findViewById;
            tvAmount = itemView.// TODO: Fix findViewById;
            tvDate = itemView.// TODO: Fix findViewById;
        }

        public void bind(final Receipt receipt, final OnReceiptClickListener listener) {
            tvReceiptId.setText("ID: " + receipt.getId());
            tvAmount.setText("Amount: " + receipt.getAmount());
            tvDate.setText("Date: " + receipt.getDate());
            itemView.setOnClickListener(v -> listener.onReceiptClick(receipt));
        }
    }
}
