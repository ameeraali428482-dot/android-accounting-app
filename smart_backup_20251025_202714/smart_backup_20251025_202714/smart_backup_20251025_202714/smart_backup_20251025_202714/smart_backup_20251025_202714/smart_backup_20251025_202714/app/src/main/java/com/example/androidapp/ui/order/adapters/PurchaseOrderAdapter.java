package com.example.androidapp.ui.order.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidapp.R;
import com.example.androidapp.models.PurchaseOrder;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * محول طلبات الشراء
 */
public class PurchaseOrderAdapter extends RecyclerView.Adapter<PurchaseOrderAdapter.PurchaseOrderViewHolder> {

    public interface OnPurchaseOrderClickListener {
        void onOrderClick(PurchaseOrder order);
        void onOrderEdit(PurchaseOrder order);
        void onOrderDelete(PurchaseOrder order);
        void onOrderView(PurchaseOrder order);
    }

    private Context context;
    private List<PurchaseOrder> orders;
    private OnPurchaseOrderClickListener listener;
    private NumberFormat currencyFormatter;
    private SimpleDateFormat dateFormatter;

    public PurchaseOrderAdapter(Context context, List<PurchaseOrder> orders) {
        this.context = context;
        this.orders = orders;
        this.currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("ar", "SA"));
        this.dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    }

    public void setOnPurchaseOrderClickListener(OnPurchaseOrderClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public PurchaseOrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_purchase_order, parent, false);
        return new PurchaseOrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PurchaseOrderViewHolder holder, int position) {
        PurchaseOrder order = orders.get(position);
        
        holder.orderNumberText.setText(order.getOrderNumber());
        holder.supplierNameText.setText(order.getSupplierName());
        holder.orderDateText.setText(dateFormatter.format(order.getOrderDate()));
        holder.totalAmountText.setText(currencyFormatter.format(order.getTotalAmount()));
        holder.itemsCountText.setText(order.getItemsCount() + " عنصر");

        // Set status chip
        switch (order.getStatus()) {
            case PENDING:
                holder.statusChip.setText("معلق");
                holder.statusChip.setChipBackgroundColorResource(android.R.color.holo_orange_light);
                break;
            case APPROVED:
                holder.statusChip.setText("موافق عليه");
                holder.statusChip.setChipBackgroundColorResource(android.R.color.holo_blue_light);
                break;
            case RECEIVED:
                holder.statusChip.setText("مستلم");
                holder.statusChip.setChipBackgroundColorResource(android.R.color.holo_green_light);
                break;
            case CANCELLED:
                holder.statusChip.setText("ملغي");
                holder.statusChip.setChipBackgroundColorResource(android.R.color.holo_red_light);
                break;
        }

        // Set priority chip if urgent
        if (order.isUrgent()) {
            holder.priorityChip.setVisibility(View.VISIBLE);
            holder.priorityChip.setText("عاجل");
            holder.priorityChip.setChipBackgroundColorResource(android.R.color.holo_red_light);
        } else {
            holder.priorityChip.setVisibility(View.GONE);
        }

        // Set delivery date if available
        if (order.getExpectedDeliveryDate() != null) {
            holder.deliveryDateText.setText("التسليم المتوقع: " + dateFormatter.format(order.getExpectedDeliveryDate()));
            holder.deliveryDateText.setVisibility(View.VISIBLE);
        } else {
            holder.deliveryDateText.setVisibility(View.GONE);
        }

        // Set click listeners
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onOrderClick(order);
            }
        });

        holder.viewButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onOrderView(order);
            }
        });

        holder.editButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onOrderEdit(order);
            }
        });

        holder.deleteButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onOrderDelete(order);
            }
        });
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public void updateOrders(List<PurchaseOrder> newOrders) {
        this.orders.clear();
        this.orders.addAll(newOrders);
        notifyDataSetChanged();
    }

    public void addOrder(PurchaseOrder order) {
        this.orders.add(0, order); // Add to beginning
        notifyItemInserted(0);
    }

    public void removeOrder(int position) {
        if (position >= 0 && position < orders.size()) {
            this.orders.remove(position);
            notifyItemRemoved(position);
        }
    }

    static class PurchaseOrderViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView itemCard;
        TextView orderNumberText;
        TextView supplierNameText;
        TextView orderDateText;
        TextView totalAmountText;
        TextView itemsCountText;
        TextView deliveryDateText;
        Chip statusChip;
        Chip priorityChip;
        MaterialButton viewButton;
        MaterialButton editButton;
        MaterialButton deleteButton;

        public PurchaseOrderViewHolder(@NonNull View itemView) {
            super(itemView);
            itemCard = itemView.findViewById(R.id.itemCard);
            orderNumberText = itemView.findViewById(R.id.orderNumberText);
            supplierNameText = itemView.findViewById(R.id.supplierNameText);
            orderDateText = itemView.findViewById(R.id.orderDateText);
            totalAmountText = itemView.findViewById(R.id.totalAmountText);
            itemsCountText = itemView.findViewById(R.id.itemsCountText);
            deliveryDateText = itemView.findViewById(R.id.deliveryDateText);
            statusChip = itemView.findViewById(R.id.statusChip);
            priorityChip = itemView.findViewById(R.id.priorityChip);
            viewButton = itemView.findViewById(R.id.viewButton);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}