package com.example.androidapp.ui.receipt;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidapp.R;
import com.example.androidapp.data.entities.Receipt;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ReceiptAdapter extends RecyclerView.Adapter<ReceiptAdapter.ReceiptViewHolder> {
    private List<Receipt> receipts = new ArrayList<>();
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Receipt receipt);
    }

    public ReceiptAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ReceiptViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.receipt_list_row, parent, false);
        return new ReceiptViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ReceiptViewHolder holder, int position) {
        Receipt receipt = receipts.get(position);
        holder.bind(receipt, listener);
    }

    @Override
    public int getItemCount() {
        return receipts.size();
    }

    public void setReceipts(List<Receipt> receipts) {
        this.receipts = receipts;
        notifyDataSetChanged();
    }

    static class ReceiptViewHolder extends RecyclerView.ViewHolder {
        private TextView tvReceiptNumber;
        private TextView tvAmount;
        private TextView tvDate;

        public ReceiptViewHolder(@NonNull View itemView) {
            super(itemView);
            tvReceiptNumber = itemView.findViewById(R.id.tvReceiptNumber);
            tvAmount = itemView.findViewById(R.id.tvReceiptAmount);
            tvDate = itemView.findViewById(R.id.tvReceiptDate);
        }

        public void bind(Receipt receipt, OnItemClickListener listener) {
            tvReceiptNumber.setText("Receipt: " + receipt.getReceiptNumber());
            tvAmount.setText("Amount: " + receipt.getTotalAmount());
            
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            tvDate.setText("Date: " + receipt.getReceiptDate());

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(receipt);
                }
            });
        }
    }
}
