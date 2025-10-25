package com.example.androidapp.ui.bankreconciliation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidapp.R;
import com.example.androidapp.models.BankReconciliationItem;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.textfield.TextInputEditText;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * محول مطابقة كشف الحساب المصرفي
 * يدعم أنواع مختلفة من عناصر المطابقة
 */
public class BankReconciliationAdapter extends RecyclerView.Adapter<BankReconciliationAdapter.ReconciliationViewHolder> {

    public enum ItemType {
        DEPOSIT_IN_TRANSIT,     // الودائع المعلقة
        OUTSTANDING_CHECK,      // الشيكات المعلقة
        BANK_ERROR,            // أخطاء البنك
        BOOK_ERROR             // أخطاء الدفاتر
    }

    public interface OnItemChangeListener {
        void onItemAdded(BankReconciliationItem item);
        void onItemUpdated(BankReconciliationItem item);
        void onItemDeleted(BankReconciliationItem item);
    }

    private Context context;
    private List<BankReconciliationItem> items;
    private ItemType itemType;
    private OnItemChangeListener listener;
    private NumberFormat currencyFormatter;
    private SimpleDateFormat dateFormatter;

    public BankReconciliationAdapter(Context context, List<BankReconciliationItem> items, ItemType itemType) {
        this.context = context;
        this.items = items;
        this.itemType = itemType;
        this.currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("ar", "SA"));
        this.dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    }

    public void setOnItemChangeListener(OnItemChangeListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ReconciliationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_bank_reconciliation, parent, false);
        return new ReconciliationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReconciliationViewHolder holder, int position) {
        BankReconciliationItem item = items.get(position);
        
        // Bind data
        holder.descriptionText.setText(item.getDescription());
        holder.amountText.setText(currencyFormatter.format(Math.abs(item.getAmount())));
        holder.dateText.setText(dateFormatter.format(item.getDate()));
        holder.notesText.setText(item.getNotes());
        holder.reconciledCheckbox.setChecked(item.isReconciled());

        // Set amount color based on type and value
        if (item.getAmount() >= 0) {
            holder.amountText.setTextColor(context.getResources().getColor(android.R.color.holo_green_dark, context.getTheme()));
            holder.amountTypeChip.setText("إضافة");
            holder.amountTypeChip.setChipBackgroundColorResource(android.R.color.holo_green_light);
        } else {
            holder.amountText.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark, context.getTheme()));
            holder.amountTypeChip.setText("خصم");
            holder.amountTypeChip.setChipBackgroundColorResource(android.R.color.holo_red_light);
        }

        // Set item type chip
        switch (itemType) {
            case DEPOSIT_IN_TRANSIT:
                holder.itemTypeChip.setText("وديعة معلقة");
                holder.itemTypeChip.setChipBackgroundColorResource(android.R.color.holo_blue_light);
                break;
            case OUTSTANDING_CHECK:
                holder.itemTypeChip.setText("شيك معلق");
                holder.itemTypeChip.setChipBackgroundColorResource(android.R.color.holo_orange_light);
                break;
            case BANK_ERROR:
                holder.itemTypeChip.setText("خطأ بنكي");
                holder.itemTypeChip.setChipBackgroundColorResource(android.R.color.holo_red_light);
                break;
            case BOOK_ERROR:
                holder.itemTypeChip.setText("خطأ دفتري");
                holder.itemTypeChip.setChipBackgroundColorResource(android.R.color.holo_purple);
                break;
        }

        // Setup listeners
        holder.reconciledCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            item.setReconciled(isChecked);
            if (listener != null) {
                listener.onItemUpdated(item);
            }
        });

        holder.editButton.setOnClickListener(v -> editItem(holder, item));
        holder.deleteButton.setOnClickListener(v -> deleteItem(position));

        // Highlight reconciled items
        if (item.isReconciled()) {
            holder.itemCard.setCardBackgroundColor(context.getResources().getColor(android.R.color.holo_green_light, context.getTheme()));
            holder.itemCard.setAlpha(0.8f);
        } else {
            holder.itemCard.setCardBackgroundColor(context.getResources().getColor(android.R.color.white, context.getTheme()));
            holder.itemCard.setAlpha(1.0f);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private void editItem(ReconciliationViewHolder holder, BankReconciliationItem item) {
        // Toggle edit mode
        if (holder.descriptionEditText.getVisibility() == View.GONE) {
            // Switch to edit mode
            holder.descriptionText.setVisibility(View.GONE);
            holder.amountText.setVisibility(View.GONE);
            holder.notesText.setVisibility(View.GONE);
            
            holder.descriptionEditText.setVisibility(View.VISIBLE);
            holder.amountEditText.setVisibility(View.VISIBLE);
            holder.notesEditText.setVisibility(View.VISIBLE);
            holder.saveButton.setVisibility(View.VISIBLE);
            
            // Fill edit fields
            holder.descriptionEditText.setText(item.getDescription());
            holder.amountEditText.setText(String.valueOf(Math.abs(item.getAmount())));
            holder.notesEditText.setText(item.getNotes());
            
            holder.editButton.setText("إلغاء");
        } else {
            // Cancel edit mode
            switchToViewMode(holder);
            holder.editButton.setText("تعديل");
        }
    }

    private void saveItem(ReconciliationViewHolder holder, BankReconciliationItem item) {
        // Get edited values
        String description = holder.descriptionEditText.getText().toString().trim();
        String amountStr = holder.amountEditText.getText().toString().trim();
        String notes = holder.notesEditText.getText().toString().trim();

        if (description.isEmpty() || amountStr.isEmpty()) {
            return; // Validation error
        }

        try {
            double amount = Double.parseDouble(amountStr);
            
            // For outstanding checks and some errors, amount should be negative
            if (itemType == ItemType.OUTSTANDING_CHECK) {
                amount = -Math.abs(amount);
            }

            // Update item
            item.setDescription(description);
            item.setAmount(amount);
            item.setNotes(notes);

            // Switch back to view mode
            switchToViewMode(holder);
            
            // Notify listener
            if (listener != null) {
                listener.onItemUpdated(item);
            }
            
            // Refresh this item
            notifyItemChanged(holder.getAdapterPosition());
            
        } catch (NumberFormatException e) {
            holder.amountEditText.setError("يرجى إدخال رقم صحيح");
        }
    }

    private void switchToViewMode(ReconciliationViewHolder holder) {
        holder.descriptionText.setVisibility(View.VISIBLE);
        holder.amountText.setVisibility(View.VISIBLE);
        holder.notesText.setVisibility(View.VISIBLE);
        
        holder.descriptionEditText.setVisibility(View.GONE);
        holder.amountEditText.setVisibility(View.GONE);
        holder.notesEditText.setVisibility(View.GONE);
        holder.saveButton.setVisibility(View.GONE);
        
        holder.editButton.setText("تعديل");
    }

    private void deleteItem(int position) {
        BankReconciliationItem item = items.get(position);
        items.remove(position);
        notifyItemRemoved(position);
        
        if (listener != null) {
            listener.onItemDeleted(item);
        }
    }

    public void addItem(BankReconciliationItem item) {
        items.add(item);
        notifyItemInserted(items.size() - 1);
        
        if (listener != null) {
            listener.onItemAdded(item);
        }
    }

    static class ReconciliationViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView itemCard;
        
        // View mode components
        TextView descriptionText;
        TextView amountText;
        TextView dateText;
        TextView notesText;
        
        // Edit mode components
        TextInputEditText descriptionEditText;
        TextInputEditText amountEditText;
        TextInputEditText notesEditText;
        
        // Controls
        CheckBox reconciledCheckbox;
        Chip itemTypeChip;
        Chip amountTypeChip;
        MaterialButton editButton;
        MaterialButton saveButton;
        MaterialButton deleteButton;

        public ReconciliationViewHolder(@NonNull View itemView) {
            super(itemView);
            
            itemCard = itemView.findViewById(R.id.itemCard);
            
            // View mode
            descriptionText = itemView.findViewById(R.id.descriptionText);
            amountText = itemView.findViewById(R.id.amountText);
            dateText = itemView.findViewById(R.id.dateText);
            notesText = itemView.findViewById(R.id.notesText);
            
            // Edit mode
            descriptionEditText = itemView.findViewById(R.id.descriptionEditText);
            amountEditText = itemView.findViewById(R.id.amountEditText);
            notesEditText = itemView.findViewById(R.id.notesEditText);
            
            // Controls
            reconciledCheckbox = itemView.findViewById(R.id.reconciledCheckbox);
            itemTypeChip = itemView.findViewById(R.id.itemTypeChip);
            amountTypeChip = itemView.findViewById(R.id.amountTypeChip);
            editButton = itemView.findViewById(R.id.editButton);
            saveButton = itemView.findViewById(R.id.saveButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            
            // Setup save button listener
            saveButton.setOnClickListener(v -> {
                // This will be handled by the adapter
            });
        }
    }
}