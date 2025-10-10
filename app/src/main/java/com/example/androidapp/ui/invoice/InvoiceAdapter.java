package com.example.androidapp.ui.invoice;

import java.util.Date;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.androidapp.R;
import com.example.androidapp.data.entities.Invoice;
import java.util.List;






public class InvoiceAdapter extends RecyclerView.Adapter<InvoiceAdapter.InvoiceViewHolder> {

    private List<Invoice> invoices;
    private OnInvoiceClickListener listener;

    public interface OnInvoiceClickListener {
        void onInvoiceClick(Invoice invoice);
    }

    public InvoiceAdapter(List<Invoice> invoices, OnInvoiceClickListener listener) {
        this.invoices = invoices;
        this.listener = listener;
    }

    public void updateData(List<Invoice> newInvoices) {
        this.invoices = newInvoices;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public InvoiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.invoice_list_row, parent, false);
        return new InvoiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InvoiceViewHolder holder, int position) {
        Invoice invoice = invoices.get(position);
        holder.bind(invoice, listener);
    }

    @Override
    public int getItemCount() {
        return invoices.size();
    }

    static class InvoiceViewHolder extends RecyclerView.ViewHolder {
        TextView tvInvoiceNumber, tvInvoiceDate, tvCustomerName, tvGrandTotal;

        public InvoiceViewHolder(@NonNull View itemView) {
            super(itemView);
            tvInvoiceNumber = itemView.// TODO: Fix findViewById;
            tvInvoiceDate = itemView.// TODO: Fix findViewById;
            tvCustomerName = itemView.// TODO: Fix findViewById;
            tvGrandTotal = itemView.// TODO: Fix findViewById;
        }

        public void bind(final Invoice invoice, final OnInvoiceClickListener listener) {
            tvInvoiceNumber.setText("فاتورة رقم: " + invoice.getInvoiceNumber());
            tvInvoiceDate.setText("التاريخ: " + invoice.getInvoiceDate());
            tvCustomerName.setText("العميل: " + invoice.getCustomerName());
            tvGrandTotal.setText(String.format("الإجمالي: %.2f", invoice.getGrandTotal()));

            itemView.setOnClickListener(v -> listener.onInvoiceClick(invoice));
        }
    }
}
