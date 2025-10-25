package com.example.androidapp.ui.reports;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidapp.R;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * محول جدول التقارير المتطور مع رؤوس أعمدة قابلة للنقر
 */
public class AdvancedReportTableAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    
    private List<ReportItem> reportItems;
    private OnColumnHeaderClickListener headerClickListener;
    private NumberFormat currencyFormatter;
    private SimpleDateFormat dateFormatter;
    
    // Sort state for each column
    private Map<String, Boolean> columnSortState; // true = ascending, false = descending
    private String currentSortColumn = "التاريخ";

    public interface OnColumnHeaderClickListener {
        void onColumnHeaderClick(String columnName, boolean isAscending);
    }

    public AdvancedReportTableAdapter(List<ReportItem> reportItems, OnColumnHeaderClickListener listener) {
        this.reportItems = reportItems;
        this.headerClickListener = listener;
        this.currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("ar", "SA"));
        this.dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        this.columnSortState = new HashMap<>();
        
        // Initialize all columns as descending by default
        columnSortState.put("التاريخ", false);
        columnSortState.put("العميل", false);
        columnSortState.put("النوع", false);
        columnSortState.put("الفئة", false);
        columnSortState.put("المبلغ", false);
        columnSortState.put("الحالة", false);
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? TYPE_HEADER : TYPE_ITEM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_report_table_header, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_report_table_row, parent, false);
            return new ItemViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderViewHolder) {
            ((HeaderViewHolder) holder).bind();
        } else if (holder instanceof ItemViewHolder) {
            ReportItem item = reportItems.get(position - 1); // -1 because of header
            ((ItemViewHolder) holder).bind(item, position - 1);
        }
    }

    @Override
    public int getItemCount() {
        return reportItems.size() + 1; // +1 for header
    }

    public void updateCurrentSortColumn(String columnName, boolean isAscending) {
        currentSortColumn = columnName;
        columnSortState.put(columnName, isAscending);
        notifyItemChanged(0); // Update header to show new sort indicator
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder {
        
        private LinearLayout dateHeaderLayout;
        private LinearLayout customerHeaderLayout;
        private LinearLayout typeHeaderLayout;
        private LinearLayout categoryHeaderLayout;
        private LinearLayout amountHeaderLayout;
        private LinearLayout statusHeaderLayout;
        
        private TextView dateHeaderText;
        private TextView customerHeaderText;
        private TextView typeHeaderText;
        private TextView categoryHeaderText;
        private TextView amountHeaderText;
        private TextView statusHeaderText;
        
        private ImageView dateHeaderSort;
        private ImageView customerHeaderSort;
        private ImageView typeHeaderSort;
        private ImageView categoryHeaderSort;
        private ImageView amountHeaderSort;
        private ImageView statusHeaderSort;

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            
            // Header layouts (clickable)
            dateHeaderLayout = itemView.findViewById(R.id.dateHeaderLayout);
            customerHeaderLayout = itemView.findViewById(R.id.customerHeaderLayout);
            typeHeaderLayout = itemView.findViewById(R.id.typeHeaderLayout);
            categoryHeaderLayout = itemView.findViewById(R.id.categoryHeaderLayout);
            amountHeaderLayout = itemView.findViewById(R.id.amountHeaderLayout);
            statusHeaderLayout = itemView.findViewById(R.id.statusHeaderLayout);
            
            // Header texts
            dateHeaderText = itemView.findViewById(R.id.dateHeaderText);
            customerHeaderText = itemView.findViewById(R.id.customerHeaderText);
            typeHeaderText = itemView.findViewById(R.id.typeHeaderText);
            categoryHeaderText = itemView.findViewById(R.id.categoryHeaderText);
            amountHeaderText = itemView.findViewById(R.id.amountHeaderText);
            statusHeaderText = itemView.findViewById(R.id.statusHeaderText);
            
            // Sort indicators
            dateHeaderSort = itemView.findViewById(R.id.dateHeaderSort);
            customerHeaderSort = itemView.findViewById(R.id.customerHeaderSort);
            typeHeaderSort = itemView.findViewById(R.id.typeHeaderSort);
            categoryHeaderSort = itemView.findViewById(R.id.categoryHeaderSort);
            amountHeaderSort = itemView.findViewById(R.id.amountHeaderSort);
            statusHeaderSort = itemView.findViewById(R.id.statusHeaderSort);
            
            setupClickListeners();
        }

        private void setupClickListeners() {
            dateHeaderLayout.setOnClickListener(v -> onHeaderClick("التاريخ", dateHeaderSort));
            customerHeaderLayout.setOnClickListener(v -> onHeaderClick("العميل", customerHeaderSort));
            typeHeaderLayout.setOnClickListener(v -> onHeaderClick("النوع", typeHeaderSort));
            categoryHeaderLayout.setOnClickListener(v -> onHeaderClick("الفئة", categoryHeaderSort));
            amountHeaderLayout.setOnClickListener(v -> onHeaderClick("المبلغ", amountHeaderSort));
            statusHeaderLayout.setOnClickListener(v -> onHeaderClick("الحالة", statusHeaderSort));
        }

        private void onHeaderClick(String columnName, ImageView sortIcon) {
            // Toggle sort direction
            boolean currentDirection = columnSortState.get(columnName);
            boolean newDirection = !currentDirection;
            
            // Update state
            columnSortState.put(columnName, newDirection);
            currentSortColumn = columnName;
            
            // Update UI
            updateSortIndicators();
            
            // Notify listener
            if (headerClickListener != null) {
                headerClickListener.onColumnHeaderClick(columnName, newDirection);
            }
        }

        public void bind() {
            updateSortIndicators();
        }

        private void updateSortIndicators() {
            // Reset all indicators
            resetSortIndicator(dateHeaderSort, dateHeaderText);
            resetSortIndicator(customerHeaderSort, customerHeaderText);
            resetSortIndicator(typeHeaderSort, typeHeaderText);
            resetSortIndicator(categoryHeaderSort, categoryHeaderText);
            resetSortIndicator(amountHeaderSort, amountHeaderText);
            resetSortIndicator(statusHeaderSort, statusHeaderText);
            
            // Set active indicator for current sort column
            ImageView activeIcon = null;
            TextView activeText = null;
            
            switch (currentSortColumn) {
                case "التاريخ":
                    activeIcon = dateHeaderSort;
                    activeText = dateHeaderText;
                    break;
                case "العميل":
                    activeIcon = customerHeaderSort;
                    activeText = customerHeaderText;
                    break;
                case "النوع":
                    activeIcon = typeHeaderSort;
                    activeText = typeHeaderText;
                    break;
                case "الفئة":
                    activeIcon = categoryHeaderSort;
                    activeText = categoryHeaderText;
                    break;
                case "المبلغ":
                    activeIcon = amountHeaderSort;
                    activeText = amountHeaderText;
                    break;
                case "الحالة":
                    activeIcon = statusHeaderSort;
                    activeText = statusHeaderText;
                    break;
            }
            
            if (activeIcon != null && activeText != null) {
                boolean isAscending = columnSortState.get(currentSortColumn);
                setSortIndicator(activeIcon, activeText, isAscending);
            }
        }

        private void resetSortIndicator(ImageView icon, TextView text) {
            icon.setImageResource(R.drawable.ic_sort_24);
            icon.setColorFilter(itemView.getContext().getColor(R.color.outline));
            text.setTextColor(itemView.getContext().getColor(R.color.on_surface_variant));
        }

        private void setSortIndicator(ImageView icon, TextView text, boolean isAscending) {
            icon.setImageResource(isAscending ? R.drawable.ic_arrow_upward_24 : R.drawable.ic_arrow_downward_24);
            icon.setColorFilter(itemView.getContext().getColor(R.color.primary));
            text.setTextColor(itemView.getContext().getColor(R.color.primary));
        }
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        
        private MaterialCardView cardView;
        private TextView rowNumberText;
        private TextView dateText;
        private TextView customerText;
        private TextView typeText;
        private TextView categoryText;
        private TextView amountText;
        private Chip statusChip;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            
            cardView = itemView.findViewById(R.id.cardView);
            rowNumberText = itemView.findViewById(R.id.rowNumberText);
            dateText = itemView.findViewById(R.id.dateText);
            customerText = itemView.findViewById(R.id.customerText);
            typeText = itemView.findViewById(R.id.typeText);
            categoryText = itemView.findViewById(R.id.categoryText);
            amountText = itemView.findViewById(R.id.amountText);
            statusChip = itemView.findViewById(R.id.statusChip);
        }

        public void bind(ReportItem item, int position) {
            rowNumberText.setText(String.valueOf(position + 1));
            dateText.setText(dateFormatter.format(item.getDate()));
            customerText.setText(item.getCustomerName());
            typeText.setText(item.getType());
            categoryText.setText(item.getCategory());
            amountText.setText(currencyFormatter.format(item.getAmount()));
            
            // Set status chip
            statusChip.setText(item.getStatus());
            updateStatusChipAppearance(item.getStatus());
            
            // Set alternating row colors for better readability
            if (position % 2 == 0) {
                cardView.setCardBackgroundColor(itemView.getContext().getColor(R.color.surface));
            } else {
                cardView.setCardBackgroundColor(itemView.getContext().getColor(R.color.surface_variant));
            }
            
            // Set click listener for row
            itemView.setOnClickListener(v -> {
                // Handle row click - could navigate to details
            });
        }

        private void updateStatusChipAppearance(String status) {
            switch (status) {
                case "مكتمل":
                    statusChip.setChipBackgroundColorResource(R.color.success_container);
                    statusChip.setTextColor(itemView.getContext().getColor(R.color.on_success_container));
                    break;
                case "معلق":
                    statusChip.setChipBackgroundColorResource(R.color.warning_container);
                    statusChip.setTextColor(itemView.getContext().getColor(R.color.on_warning_container));
                    break;
                case "ملغي":
                    statusChip.setChipBackgroundColorResource(R.color.error_container);
                    statusChip.setTextColor(itemView.getContext().getColor(R.color.on_error_container));
                    break;
                case "قيد المعالجة":
                    statusChip.setChipBackgroundColorResource(R.color.info_container);
                    statusChip.setTextColor(itemView.getContext().getColor(R.color.on_info_container));
                    break;
                default:
                    statusChip.setChipBackgroundColorResource(R.color.surface_variant);
                    statusChip.setTextColor(itemView.getContext().getColor(R.color.on_surface_variant));
                    break;
            }
        }
    }
}