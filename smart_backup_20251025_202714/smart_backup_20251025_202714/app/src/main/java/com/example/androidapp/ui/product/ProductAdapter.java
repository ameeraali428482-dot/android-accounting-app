package com.example.androidapp.ui.product;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidapp.R;
import com.example.androidapp.models.Product;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

/**
 * محول قائمة المنتجات
 */
public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    public interface OnProductClickListener {
        void onProductClick(Product product);
        void onProductEdit(Product product);
        void onProductDelete(Product product);
        void onProductShare(Product product);
    }

    private Context context;
    private List<Product> products;
    private OnProductClickListener listener;
    private NumberFormat currencyFormatter;

    public ProductAdapter(Context context, List<Product> products) {
        this.context = context;
        this.products = products;
        this.currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("ar", "SA"));
    }

    public void setOnProductClickListener(OnProductClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product_modern, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = products.get(position);
        
        holder.productNameText.setText(product.getName());
        holder.productCodeText.setText(product.getCode());
        holder.priceText.setText(currencyFormatter.format(product.getSellingPrice()));
        holder.stockText.setText(String.valueOf(product.getCurrentStock()) + " قطعة");
        holder.categoryText.setText(product.getCategory());

        // Set stock progress
        int stockPercentage = calculateStockPercentage(product);
        holder.stockProgress.setProgress(stockPercentage);

        // Set status chip
        switch (product.getStatus()) {
            case ACTIVE:
                holder.statusChip.setText("نشط");
                holder.statusChip.setChipBackgroundColorResource(android.R.color.holo_green_light);
                break;
            case INACTIVE:
                holder.statusChip.setText("غير نشط");
                holder.statusChip.setChipBackgroundColorResource(android.R.color.darker_gray);
                break;
            case DISCONTINUED:
                holder.statusChip.setText("متوقف");
                holder.statusChip.setChipBackgroundColorResource(android.R.color.holo_red_light);
                break;
        }

        // Set type chip
        switch (product.getType()) {
            case PRODUCT:
                holder.typeChip.setText("منتج");
                holder.typeChip.setChipBackgroundColorResource(android.R.color.holo_blue_light);
                break;
            case SERVICE:
                holder.typeChip.setText("خدمة");
                holder.typeChip.setChipBackgroundColorResource(android.R.color.holo_orange_light);
                break;
            case DIGITAL:
                holder.typeChip.setText("رقمي");
                holder.typeChip.setChipBackgroundColorResource(android.R.color.holo_purple);
                break;
        }

        // Set stock status color
        if (product.getCurrentStock() <= 0) {
            holder.stockText.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark, context.getTheme()));
        } else if (product.getCurrentStock() <= product.getMinStockLevel()) {
            holder.stockText.setTextColor(context.getResources().getColor(android.R.color.holo_orange_dark, context.getTheme()));
        } else {
            holder.stockText.setTextColor(context.getResources().getColor(android.R.color.holo_green_dark, context.getTheme()));
        }

        // Set click listeners
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onProductClick(product);
            }
        });

        holder.editButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onProductEdit(product);
            }
        });

        holder.deleteButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onProductDelete(product);
            }
        });

        holder.shareButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onProductShare(product);
            }
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    private int calculateStockPercentage(Product product) {
        if (product.getMaxStockLevel() > 0) {
            return (int) ((double) product.getCurrentStock() / product.getMaxStockLevel() * 100);
        }
        return 0;
    }

    public void updateProducts(List<Product> newProducts) {
        this.products.clear();
        this.products.addAll(newProducts);
        notifyDataSetChanged();
    }

    public void addProduct(Product product) {
        this.products.add(product);
        notifyItemInserted(products.size() - 1);
    }

    public void removeProduct(int position) {
        if (position >= 0 && position < products.size()) {
            this.products.remove(position);
            notifyItemRemoved(position);
        }
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView itemCard;
        ImageView productImageView;
        TextView productNameText;
        TextView productCodeText;
        TextView priceText;
        TextView stockText;
        TextView categoryText;
        LinearProgressIndicator stockProgress;
        Chip statusChip;
        Chip typeChip;
        MaterialButton editButton;
        MaterialButton deleteButton;
        MaterialButton shareButton;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            itemCard = itemView.findViewById(R.id.itemCard);
            productImageView = itemView.findViewById(R.id.productImageView);
            productNameText = itemView.findViewById(R.id.productNameText);
            productCodeText = itemView.findViewById(R.id.productCodeText);
            priceText = itemView.findViewById(R.id.priceText);
            stockText = itemView.findViewById(R.id.stockText);
            categoryText = itemView.findViewById(R.id.categoryText);
            stockProgress = itemView.findViewById(R.id.stockProgress);
            statusChip = itemView.findViewById(R.id.statusChip);
            typeChip = itemView.findViewById(R.id.typeChip);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            shareButton = itemView.findViewById(R.id.shareButton);
        }
    }
}