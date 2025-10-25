package com.example.androidapp.ui.product;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidapp.R;
import com.example.androidapp.models.ProductVariant;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

/**
 * محول متغيرات المنتج
 */
public class ProductVariantAdapter extends RecyclerView.Adapter<ProductVariantAdapter.VariantViewHolder> {

    public interface OnVariantClickListener {
        void onVariantClick(ProductVariant variant);
        void onVariantEdit(ProductVariant variant);
        void onVariantDelete(ProductVariant variant);
    }

    private Context context;
    private List<ProductVariant> variants;
    private OnVariantClickListener listener;
    private NumberFormat currencyFormatter;

    public ProductVariantAdapter(Context context, List<ProductVariant> variants) {
        this.context = context;
        this.variants = variants;
        this.currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("ar", "SA"));
    }

    public void setOnVariantClickListener(OnVariantClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public VariantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product_variant, parent, false);
        return new VariantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VariantViewHolder holder, int position) {
        ProductVariant variant = variants.get(position);
        
        holder.variantNameText.setText(variant.getName());
        holder.variantCodeText.setText(variant.getCode());
        holder.priceText.setText(currencyFormatter.format(variant.getPrice()));
        holder.stockText.setText(String.valueOf(variant.getStock()));

        // Set status chip
        if (variant.isActive()) {
            holder.statusChip.setText("نشط");
            holder.statusChip.setChipBackgroundColorResource(android.R.color.holo_green_light);
        } else {
            holder.statusChip.setText("غير نشط");
            holder.statusChip.setChipBackgroundColorResource(android.R.color.darker_gray);
        }

        // Set click listeners
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onVariantClick(variant);
            }
        });

        holder.editButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onVariantEdit(variant);
            }
        });

        holder.deleteButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onVariantDelete(variant);
            }
        });
    }

    @Override
    public int getItemCount() {
        return variants.size();
    }

    static class VariantViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView itemCard;
        TextView variantNameText;
        TextView variantCodeText;
        TextView priceText;
        TextView stockText;
        Chip statusChip;
        MaterialButton editButton;
        MaterialButton deleteButton;

        public VariantViewHolder(@NonNull View itemView) {
            super(itemView);
            itemCard = itemView.findViewById(R.id.itemCard);
            variantNameText = itemView.findViewById(R.id.variantNameText);
            variantCodeText = itemView.findViewById(R.id.variantCodeText);
            priceText = itemView.findViewById(R.id.priceText);
            stockText = itemView.findViewById(R.id.stockText);
            statusChip = itemView.findViewById(R.id.statusChip);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}