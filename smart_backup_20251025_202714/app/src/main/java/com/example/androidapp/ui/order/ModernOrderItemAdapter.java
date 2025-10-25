package com.example.androidapp.ui.order;

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
import com.example.androidapp.data.entities.OrderItem;
import com.example.androidapp.ui.common.VoiceInputEditText;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.textfield.TextInputEditText;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

/**
 * محول عناصر الطلب المتطور - Material 3 Design
 */
public class ModernOrderItemAdapter extends RecyclerView.Adapter<ModernOrderItemAdapter.OrderItemViewHolder> {

    private List<OrderItem> items;
    private OnItemChangedListener onItemChangedListener;
    private OnItemRemovedListener onItemRemovedListener;
    private NumberFormat currencyFormatter;
    
    // Product suggestions for autocomplete
    private String[] productSuggestions = {
            "لابتوب Dell XPS 13",
            "آيفون 14 برو ماكس",
            "سامسونج جالاكسي S23",
            "شاشة LG 27 بوصة",
            "كيبورد ميكانيكي",
            "فأرة لوجيتك MX Master",
            "سماعات Sony WH-1000XM5",
            "تابلت iPad Pro",
            "كاميرا Canon EOS R5",
            "طابعة HP LaserJet"
    };

    public interface OnItemChangedListener {
        void onItemChanged(int position);
    }

    public interface OnItemRemovedListener {
        void onItemRemoved(int position);
    }

    public ModernOrderItemAdapter(List<OrderItem> items, 
                                 OnItemChangedListener onItemChangedListener,
                                 OnItemRemovedListener onItemRemovedListener) {
        this.items = items;
        this.onItemChangedListener = onItemChangedListener;
        this.onItemRemovedListener = onItemRemovedListener;
        this.currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("ar", "SA"));
    }

    @NonNull
    @Override
    public OrderItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order_item_modern, parent, false);
        return new OrderItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderItemViewHolder holder, int position) {
        OrderItem item = items.get(position);
        holder.bind(item, position);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class OrderItemViewHolder extends RecyclerView.ViewHolder {
        
        private MaterialCardView cardView;
        private TextView itemNumberText;
        private AutoCompleteTextView productNameDropdown;
        private VoiceInputEditText productDescriptionEditText;
        private TextInputEditText quantityEditText;
        private TextInputEditText unitPriceEditText;
        private TextView itemTotalText;
        private ImageButton removeButton;
        private ImageButton duplicateButton;
        private Chip availabilityChip;
        private Chip priorityChip;
        
        private boolean isUpdating = false;

        public OrderItemViewHolder(@NonNull View itemView) {
            super(itemView);
            
            cardView = itemView.findViewById(R.id.cardView);
            itemNumberText = itemView.findViewById(R.id.itemNumberText);
            productNameDropdown = itemView.findViewById(R.id.productNameDropdown);
            productDescriptionEditText = itemView.findViewById(R.id.productDescriptionEditText);
            quantityEditText = itemView.findViewById(R.id.quantityEditText);
            unitPriceEditText = itemView.findViewById(R.id.unitPriceEditText);
            itemTotalText = itemView.findViewById(R.id.itemTotalText);
            removeButton = itemView.findViewById(R.id.removeButton);
            duplicateButton = itemView.findViewById(R.id.duplicateButton);
            availabilityChip = itemView.findViewById(R.id.availabilityChip);
            priorityChip = itemView.findViewById(R.id.priorityChip);
            
            // Setup autocomplete for product names
            setupProductNameSuggestions();
            
            // Setup text watchers for automatic calculations
            setupTextWatchers();
            
            // Setup button listeners
            setupButtonListeners();
            
            // Setup chip listeners
            setupChipListeners();
        }

        private void setupProductNameSuggestions() {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    itemView.getContext(),
                    android.R.layout.simple_dropdown_item_1line,
                    productSuggestions
            );
            productNameDropdown.setAdapter(adapter);
            productNameDropdown.setThreshold(1);
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
            
            // Product name change listener
            productNameDropdown.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable s) {
                    if (!isUpdating) {
                        updateItemData();
                        checkProductAvailability(s.toString());
                    }
                }
            });
            
            // Description change listener
            productDescriptionEditText.addTextChangedListener(new TextWatcher() {
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

        private void setupChipListeners() {
            availabilityChip.setOnClickListener(v -> toggleAvailability());
            priorityChip.setOnClickListener(v -> togglePriority());
        }

        public void bind(OrderItem item, int position) {
            isUpdating = true;
            
            // Set item number
            itemNumberText.setText(String.valueOf(position + 1));
            
            // Set item data
            productNameDropdown.setText(item.getProductName(), false);
            productDescriptionEditText.setText(item.getDescription());
            quantityEditText.setText(item.getQuantity() > 0 ? String.valueOf(item.getQuantity()) : "");
            unitPriceEditText.setText(item.getUnitPrice() > 0 ? String.format("%.2f", item.getUnitPrice()) : "");
            
            // Calculate and display total
            calculateItemTotal();
            
            // Update availability chip
            updateAvailabilityChip(item.getProductName());
            
            // Update priority chip
            updatePriorityChip(item.getQuantity());
            
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
                    OrderItem item = items.get(position);
                    item.setQuantity(quantity);
                    item.setUnitPrice(price);
                    item.setTotalPrice(total);
                }
                
            } catch (NumberFormatException e) {
                itemTotalText.setText(currencyFormatter.format(0));
            }
        }

        private void updateItemData() {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                OrderItem item = items.get(position);
                item.setProductName(productNameDropdown.getText().toString().trim());
                item.setDescription(productDescriptionEditText.getText().toString().trim());
                
                // Trigger callback to parent activity
                if (onItemChangedListener != null) {
                    onItemChangedListener.onItemChanged(position);
                }
                
                // Update card appearance
                updateCardAppearance(item);
            }
        }

        private void updateCardAppearance(OrderItem item) {
            // Change card appearance based on completion status
            boolean isComplete = !item.getProductName().isEmpty() && 
                               item.getQuantity() > 0 && 
                               item.getUnitPrice() > 0;
            
            if (isComplete) {
                cardView.setCardBackgroundColor(itemView.getContext().getColor(R.color.success_container));
                cardView.setStrokeColor(itemView.getContext().getColor(R.color.success));
                cardView.setStrokeWidth(2);
            } else {
                cardView.setCardBackgroundColor(itemView.getContext().getColor(android.R.color.white));
                cardView.setStrokeColor(itemView.getContext().getColor(R.color.outline));
                cardView.setStrokeWidth(1);
            }
        }

        private void checkProductAvailability(String productName) {
            // Simulate product availability check
            boolean isAvailable = !productName.toLowerCase().contains("iphone") || 
                                 Math.random() > 0.3; // 70% available
            
            updateAvailabilityChip(productName);
        }

        private void updateAvailabilityChip(String productName) {
            if (productName.isEmpty()) {
                availabilityChip.setVisibility(View.GONE);
                return;
            }
            
            availabilityChip.setVisibility(View.VISIBLE);
            
            // Simulate availability check
            boolean isAvailable = Math.random() > 0.2; // 80% available
            
            if (isAvailable) {
                availabilityChip.setText("متوفر");
                availabilityChip.setChipBackgroundColorResource(R.color.success_container);
                availabilityChip.setTextColor(itemView.getContext().getColor(R.color.on_success_container));
            } else {
                availabilityChip.setText("غير متوفر");
                availabilityChip.setChipBackgroundColorResource(R.color.error_container);
                availabilityChip.setTextColor(itemView.getContext().getColor(R.color.on_error_container));
            }
        }

        private void updatePriorityChip(double quantity) {
            if (quantity <= 0) {
                priorityChip.setVisibility(View.GONE);
                return;
            }
            
            priorityChip.setVisibility(View.VISIBLE);
            
            if (quantity >= 100) {
                priorityChip.setText("طلب كبير");
                priorityChip.setChipBackgroundColorResource(R.color.warning_container);
                priorityChip.setTextColor(itemView.getContext().getColor(R.color.on_warning_container));
            } else if (quantity >= 10) {
                priorityChip.setText("طلب عادي");
                priorityChip.setChipBackgroundColorResource(R.color.info_container);
                priorityChip.setTextColor(itemView.getContext().getColor(R.color.on_info_container));
            } else {
                priorityChip.setText("طلب صغير");
                priorityChip.setChipBackgroundColorResource(R.color.surface_variant);
                priorityChip.setTextColor(itemView.getContext().getColor(R.color.on_surface_variant));
            }
        }

        private void toggleAvailability() {
            // Toggle between available and out of stock
            if (availabilityChip.getText().equals("متوفر")) {
                availabilityChip.setText("غير متوفر");
                availabilityChip.setChipBackgroundColorResource(R.color.error_container);
                availabilityChip.setTextColor(itemView.getContext().getColor(R.color.on_error_container));
            } else {
                availabilityChip.setText("متوفر");
                availabilityChip.setChipBackgroundColorResource(R.color.success_container);
                availabilityChip.setTextColor(itemView.getContext().getColor(R.color.on_success_container));
            }
        }

        private void togglePriority() {
            // Cycle through priority levels
            String currentText = priorityChip.getText().toString();
            switch (currentText) {
                case "طلب صغير":
                    priorityChip.setText("طلب عادي");
                    priorityChip.setChipBackgroundColorResource(R.color.info_container);
                    priorityChip.setTextColor(itemView.getContext().getColor(R.color.on_info_container));
                    break;
                case "طلب عادي":
                    priorityChip.setText("طلب كبير");
                    priorityChip.setChipBackgroundColorResource(R.color.warning_container);
                    priorityChip.setTextColor(itemView.getContext().getColor(R.color.on_warning_container));
                    break;
                case "طلب كبير":
                    priorityChip.setText("طلب صغير");
                    priorityChip.setChipBackgroundColorResource(R.color.surface_variant);
                    priorityChip.setTextColor(itemView.getContext().getColor(R.color.on_surface_variant));
                    break;
            }
        }

        private void duplicateItem(int position) {
            if (position >= 0 && position < items.size()) {
                OrderItem originalItem = items.get(position);
                
                // Create duplicate item
                OrderItem duplicateItem = new OrderItem();
                duplicateItem.setId(java.util.UUID.randomUUID().toString());
                duplicateItem.setProductName(originalItem.getProductName());
                duplicateItem.setDescription(originalItem.getDescription());
                duplicateItem.setQuantity(originalItem.getQuantity());
                duplicateItem.setUnitPrice(originalItem.getUnitPrice());
                duplicateItem.setTotalPrice(originalItem.getTotalPrice());
                
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