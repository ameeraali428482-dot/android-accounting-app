package com.example.androidapp.ui.receipt;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.androidapp.R;
import com.example.androidapp.data.entities.Receipt;
import java.util.List;

public class ReceiptAdapter extends RecyclerView.Adapter<ReceiptAdapter.Holder> {

    public interface OnClickListener { void onClick(Receipt r); }

    private List<Receipt> list;
    private OnClickListener listener;

    public ReceiptAdapter(List<Receipt> list, OnClickListener listener) {
        this.list = list;
        this.listener = listener;
    }

    public void updateData(List<Receipt> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup p, int viewType) {
        View v = LayoutInflater.from(p.getContext()).inflate(R.layout.receipt_list_row, p, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder h, int position) {
        Receipt r = list.get(position);
        h.tvNum .setText(r.getReceiptNumber());
        h.tvAmt .setText(String.valueOf(r.getTotalAmount()));
        h.tvDate.setText(r.getReceiptDate());
        h.itemView.setOnClickListener(v -> listener.onClick(r));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class Holder extends RecyclerView.ViewHolder {
        TextView tvNum, tvAmt, tvDate;
        Holder(@NonNull View itemView) {
            super(itemView);
            tvNum  = itemView.findViewById(R.id.tvReceiptNumber);
            tvAmt  = itemView.findViewById(R.id.tvReceiptAmount);
            tvDate = itemView.findViewById(R.id.tvReceiptDate);
        }
    }
}
