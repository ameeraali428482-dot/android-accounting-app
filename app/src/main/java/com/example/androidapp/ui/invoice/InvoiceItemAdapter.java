package com.example.androidapp.ui.invoice;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.androidapp.R;
import com.example.androidapp.data.entities.InvoiceItem;
import java.util.List;






public class InvoiceItemAdapter extends RecyclerView.Adapter<InvoiceItemAdapter.InvoiceItemViewHolder> {

    private List<InvoiceItem> invoiceItems;
    private OnItemActionListener listener;

    public InvoiceItemAdapter(List<InvoiceItem> invoiceItems, OnItemActionListener listener) {
        this.invoiceItems = invoiceItems;
        this.listener = listener;
    }

    public void setInvoiceItems(List<InvoiceItem> invoiceItems) {
        this.invoiceItems = invoiceItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public InvoiceItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.invoice_item_row, parent, false);
        return new InvoiceItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InvoiceItemViewHolder holder, int position) {
        InvoiceItem item = invoiceItems.get(position);
        holder.itemName.setText(item.getItemName());
        holder.quantity.setText(String.format("الكمية: %d", item.getQuantity()));
        holder.price.setText(String.format("السعر: %.2f", item.getPrice()));
        holder.total.setText(String.format("الإجمالي: %.2f", item.getTotal()));

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditItem(item);
            }
        });

        holder.itemView.findViewById(R.id.delete_invoice_item_button).setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteItem(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return invoiceItems.size();
    }

    public static class InvoiceItemViewHolder extends RecyclerView.ViewHolder {
        TextView itemName, quantity, price, total;

        public InvoiceItemViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.invoice_item_name);
            quantity = itemView.findViewById(R.id.invoice_item_quantity);
            price = itemView.findViewById(R.id.invoice_item_price);
            total = itemView.findViewById(R.id.invoice_item_total);
        }
    }

    public interface OnItemActionListener {
        void onEditItem(InvoiceItem item);
        void onDeleteItem(InvoiceItem item);
    }
}

