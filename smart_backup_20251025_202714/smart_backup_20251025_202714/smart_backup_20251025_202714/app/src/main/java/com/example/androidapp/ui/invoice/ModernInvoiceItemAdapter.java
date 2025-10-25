package com.example.androidapp.ui.invoice;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidapp.R;
import com.example.androidapp.data.entities.InvoiceItem;
import com.example.androidapp.ui.common.VoiceInputEditText;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

/**
 * محول عناصر الفاتورة المتطور - Material 3 Design
 */
public class ModernInvoiceItemAdapter extends RecyclerView.Adapter<ModernInvoiceItemAdapter.InvoiceItemViewHolder> {

    private List<InvoiceItem> items;
    private OnItemChangedListener onItemChangedListener;
    private OnItemRemovedListener onItemRemovedListener;
    private NumberFormat currencyFormatter;
    
    // Product suggestions for autocomplete
    private String[] productSuggestions = {
            "شاشة كمبيوتر 24 بوصة",
            "لوحة مفاتيح لاسلكية",
            "فأرة لاسلكية",
            "طابعة ليزر",
            "كاميرا ويب HD",
            "سماعات بلوتوث",
            "كابل HDMI",
            "قرص صلب خارجي 1TB",
            "ذاكرة USB 64GB",
            "مكبر صوت محمول"
    };

    public interface OnItemChangedListener {
        void onItemChanged(int position);
    }

    public interface OnItemRemovedListener {
        void onItemRemoved(int position);
    }

    public ModernInvoiceItemAdapter(List<InvoiceItem> items, 
                                   OnItemChangedListener onItemChangedListener,
                                   OnItemRemovedListener onItemRemovedListener) {
        this.items = items;
        this.onItemChangedListener = onItemChangedListener;
        this.onItemRemovedListener = onItemRemovedListener;
        this.currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("ar", "SA"));
    }

    @NonNull
    @Override
    public InvoiceItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_invoice_item_modern, parent, false);
        return new InvoiceItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InvoiceItemViewHolder holder, int position) {
        InvoiceItem item = items.get(position);
        holder.bind(item, position);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class InvoiceItemViewHolder extends RecyclerView.ViewHolder {
        
        private MaterialCardView cardView;
        private TextView itemNumberText;
        private AutoCompleteTextView itemNameDropdown;
        private VoiceInputEditText itemDescriptionEditText;
        private TextInputEditText quantityEditText;
        private TextInputEditText unitPriceEditText;
        private TextView itemTotalText;
        private ImageButton removeButton;
        private ImageButton duplicateButton;
        
        private boolean isUpdating = false;

        public InvoiceItemViewHolder(@NonNull View itemView) {
            super(itemView);
            
            cardView = itemView.findViewById(R.id.cardView);
            itemNumberText = itemView.findViewById(R.id.itemNumberText);
            itemNameDropdown = itemView.findViewById(R.id.itemNameDropdown);
            itemDescriptionEditText = itemView.findViewById(R.id.itemDescriptionEditText);
            quantityEditText = itemView.findViewById(R.id.quantityEditText);
            unitPriceEditText = itemView.findViewById(R.id.unitPriceEditText);
            itemTotalText = itemView.findViewById(R.id.itemTotalText);
            removeButton = itemView.findViewById(R.id.removeButton);
            duplicateButton = itemView.findViewById(R.id.duplicateButton);
            
            // Setup autocomplete for item names
            setupItemNameSuggestions();
            
            // Setup text watchers for automatic calculations
            setupTextWatchers();
            
            // Setup button listeners
            setupButtonListeners();
        }

        private void setupItemNameSuggestions() {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    itemView.getContext(),
                    android.R.layout.simple_dropdown_item_1line,
                    productSuggestions
            );
            itemNameDropdown.setAdapter(adapter);
            itemNameDropdown.setThreshold(1);
        }

        private void setupTextWatchers() {
            TextWatcher calculationWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable s) {
                    if (!isUpdating) {
                        calculateItemTotal();
                        updateItemData();
                    }
                }
            };
            
            quantityEditText.addTextChangedListener(calculationWatcher);
            unitPriceEditText.addTextChangedListener(calculationWatcher);
            
            // Item name change listener
            itemNameDropdown.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable s) {
                    if (!isUpdating) {
                        updateItemData();
                    }
                }
            });
            
            // Description change listener
            itemDescriptionEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable s) {
                    if (!isUpdating) {
                        updateItemData();
                    }
                }
            });
        }

        private void setupButtonListeners() {
            removeButton.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && onItemRemovedListener != null) {
                    onItemRemovedListener.onItemRemoved(position);
                }
            });
            
            duplicateButton.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    duplicateItem(position);
                }
            });
        }

        public void bind(InvoiceItem item, int position) {
            isUpdating = true;
            
            // Set item number
            itemNumberText.setText(String.valueOf(position + 1));
            
            // Set item data
            itemNameDropdown.setText(item.getItemName(), false);
            itemDescriptionEditText.setText(item.getDescription());
            quantityEditText.setText(item.getQuantity() > 0 ? String.valueOf(item.getQuantity()) : "");
            unitPriceEditText.setText(item.getUnitPrice() > 0 ? String.format("%.2f", item.getUnitPrice()) : "");
            
            // Calculate and display total
            calculateItemTotal();
            
            // Update card appearance based on data completeness
            updateCardAppearance(item);
            
            isUpdating = false;
        }

        private void calculateItemTotal() {
            try {
                String quantityStr = quantityEditText.getText().toString().trim();
                String priceStr = unitPriceEditText.getText().toString().trim();
                
                double quantity = quantityStr.isEmpty() ? 0 : Double.parseDouble(quantityStr);
                double price = priceStr.isEmpty() ? 0 : Double.parseDouble(priceStr);
                double total = quantity * price;
                
                itemTotalText.setText(currencyFormatter.format(total));
                
                // Update item in the list
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    InvoiceItem item = items.get(position);
                    item.setQuantity(quantity);
                    item.setUnitPrice(price);
                    item.setTotal(total);
                }
                
            } catch (NumberFormatException e) {
                itemTotalText.setText(currencyFormatter.format(0));
            }
        }

        private void updateItemData() {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                InvoiceItem item = items.get(position);
                item.setItemName(itemNameDropdown.getText().toString().trim());
                item.setDescription(itemDescriptionEditText.getText().toString().trim());
                
                // Trigger callback to parent activity
                if (onItemChangedListener != null) {
                    onItemChangedListener.onItemChanged(position);
                }
                
                // Update card appearance
                updateCardAppearance(item);
            }
        }

        private void updateCardAppearance(InvoiceItem item) {
            // Change card appearance based on completion status
            boolean isComplete = !item.getItemName().isEmpty() && 
                               item.getQuantity() > 0 && 
                               item.getUnitPrice() > 0;
            
            if (isComplete) {
                cardView.setCardBackgroundColor(itemView.getContext().getColor(R.color.success_light));
                cardView.setStrokeColor(itemView.getContext().getColor(R.color.success));
                cardView.setStrokeWidth(2);
            } else {
                cardView.setCardBackgroundColor(itemView.getContext().getColor(android.R.color.white));
                cardView.setStrokeColor(itemView.getContext().getColor(R.color.outline));
                cardView.setStrokeWidth(1);
            }
        }

        private void duplicateItem(int position) {
            if (position >= 0 && position < items.size()) {
                InvoiceItem originalItem = items.get(position);
                
                // Create duplicate item
                InvoiceItem duplicateItem = new InvoiceItem();
                duplicateItem.setId(java.util.UUID.randomUUID().toString());
                duplicateItem.setItemName(originalItem.getItemName());
                duplicateItem.setDescription(originalItem.getDescription());
                duplicateItem.setQuantity(originalItem.getQuantity());
                duplicateItem.setUnitPrice(originalItem.getUnitPrice());
                duplicateItem.setTotal(originalItem.getTotal());
                
                // Add to list and notify adapter
                items.add(position + 1, duplicateItem);
                notifyItemInserted(position + 1);
                
                // Trigger callback
                if (onItemChangedListener != null) {
                    onItemChangedListener.onItemChanged(position + 1);
                }
            }
        }
    }
}