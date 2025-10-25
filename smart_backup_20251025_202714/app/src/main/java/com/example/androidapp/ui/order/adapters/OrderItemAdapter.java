package com.example.androidapp.ui.order.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidapp.R;
import com.example.androidapp.data.entities.OrderItem;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class OrderItemAdapter extends RecyclerView.Adapter<OrderItemAdapter.OrderItemViewHolder> {

    private Context context;
    private List<OrderItem> orderItems;
    private OnOrderItemClickListener onOrderItemClickListener;
    private DecimalFormat decimalFormat;

    public interface OnOrderItemClickListener {
        void onOrderItemClick(OrderItem orderItem);
        void onOrderItemLongClick(OrderItem orderItem);
    }

    public OrderItemAdapter(Context context) {
        this.context = context;
        this.orderItems = new ArrayList<>();
        this.decimalFormat = new DecimalFormat("#,##0.00");
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems != null ? orderItems : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void setOnOrderItemClickListener(OnOrderItemClickListener listener) {
        this.onOrderItemClickListener = listener;
    }

    @NonNull
    @Override
    public OrderItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.order_item_row, parent, false);
        return new OrderItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderItemViewHolder holder, int position) {
        OrderItem orderItem = orderItems.get(position);
        holder.bind(orderItem);
    }

    @Override
    public int getItemCount() {
        return orderItems.size();
    }

    public class OrderItemViewHolder extends RecyclerView.ViewHolder {
        private TextView tvItemName;
        private TextView tvQuantity;
        private TextView tvUnitPrice;
        private TextView tvTotalPrice;
        private TextView tvDescription;

        public OrderItemViewHolder(@NonNull View itemView) {
            super(itemView);
            initViews(itemView);
            setupClickListeners();
        }

        private void initViews(View itemView) {
            tvItemName = itemView.findViewById(R.id.tv_item_name);
            tvQuantity = itemView.findViewById(R.id.tv_quantity);
            tvUnitPrice = itemView.findViewById(R.id.tv_unit_price);
            tvTotalPrice = itemView.findViewById(R.id.tv_total_price);
            tvDescription = itemView.findViewById(R.id.tv_description);
        }

        private void setupClickListeners() {
            itemView.setOnClickListener(v -> {
                if (onOrderItemClickListener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    onOrderItemClickListener.onOrderItemClick(orderItems.get(getAdapterPosition()));
                }
            });

            itemView.setOnLongClickListener(v -> {
                if (onOrderItemClickListener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    onOrderItemClickListener.onOrderItemLongClick(orderItems.get(getAdapterPosition()));
                    return true;
                }
                return false;
            });
        }

        public void bind(OrderItem orderItem) {
            if (orderItem == null) return;

            // Set item name (you might need to fetch this from Item entity)
            tvItemName.setText(orderItem.getItemId());

            // Set quantity
            tvQuantity.setText(context.getString(R.string.quantity_format, orderItem.getQuantity()));

            // Set unit price
            tvUnitPrice.setText(context.getString(R.string.price_format, decimalFormat.format(orderItem.getUnitPrice())));

            // Set total price
            tvTotalPrice.setText(context.getString(R.string.price_format, decimalFormat.format(orderItem.getTotalPrice())));

            // Set description
            if (orderItem.getDescription() != null && !orderItem.getDescription().trim().isEmpty()) {
                tvDescription.setVisibility(View.VISIBLE);
                tvDescription.setText(orderItem.getDescription());
            } else {
                tvDescription.setVisibility(View.GONE);
            }
        }
    }
}
