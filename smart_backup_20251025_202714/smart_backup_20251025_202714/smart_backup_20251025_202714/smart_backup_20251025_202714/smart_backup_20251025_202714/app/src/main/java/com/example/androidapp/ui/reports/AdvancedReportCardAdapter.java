package com.example.androidapp.ui.reports;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidapp.R;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * محول بطاقات التقارير المتطور
 */
public class AdvancedReportCardAdapter extends RecyclerView.Adapter<AdvancedReportCardAdapter.CardViewHolder> {

    private List<ReportItem> reportItems;
    private OnReportItemClickListener clickListener;
    private NumberFormat currencyFormatter;
    private SimpleDateFormat dateFormatter;

    public interface OnReportItemClickListener {
        void onReportItemClick(ReportItem item);
    }

    public AdvancedReportCardAdapter(List<ReportItem> reportItems, OnReportItemClickListener listener) {
        this.reportItems = reportItems;
        this.clickListener = listener;
        this.currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("ar", "SA"));
        this.dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_report_card, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        ReportItem item = reportItems.get(position);
        holder.bind(item, position);
    }

    @Override
    public int getItemCount() {
        return reportItems.size();
    }

    class CardViewHolder extends RecyclerView.ViewHolder {
        
        private MaterialCardView cardView;
        private TextView cardNumberText;
        private TextView customerNameText;
        private TextView typeText;
        private TextView categoryText;
        private TextView dateText;
        private TextView amountText;
        private Chip statusChip;
        private View statusIndicator;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            
            cardView = itemView.findViewById(R.id.cardView);
            cardNumberText = itemView.findViewById(R.id.cardNumberText);
            customerNameText = itemView.findViewById(R.id.customerNameText);
            typeText = itemView.findViewById(R.id.typeText);
            categoryText = itemView.findViewById(R.id.categoryText);
            dateText = itemView.findViewById(R.id.dateText);
            amountText = itemView.findViewById(R.id.amountText);
            statusChip = itemView.findViewById(R.id.statusChip);
            statusIndicator = itemView.findViewById(R.id.statusIndicator);
        }

        public void bind(ReportItem item, int position) {
            cardNumberText.setText("#" + (position + 1));
            customerNameText.setText(item.getCustomerName());
            typeText.setText(item.getType());
            categoryText.setText(item.getCategory());
            dateText.setText(dateFormatter.format(item.getDate()));
            amountText.setText(currencyFormatter.format(item.getAmount()));
            
            // Set status chip
            statusChip.setText(item.getStatus());
            updateStatusAppearance(item.getStatus());
            
            // Set click listener
            cardView.setOnClickListener(v -> {
                if (clickListener != null) {
                    clickListener.onReportItemClick(item);
                }
            });
            
            // Add subtle animation
            cardView.setOnClickListener(v -> {
                cardView.animate()
                    .scaleX(0.95f)
                    .scaleY(0.95f)
                    .setDuration(100)
                    .withEndAction(() -> {
                        cardView.animate()
                            .scaleX(1.0f)
                            .scaleY(1.0f)
                            .setDuration(100)
                            .start();
                        
                        if (clickListener != null) {
                            clickListener.onReportItemClick(item);
                        }
                    })
                    .start();
            });
        }

        private void updateStatusAppearance(String status) {
            int backgroundColor, textColor, indicatorColor;
            
            switch (status) {
                case "مكتمل":
                    backgroundColor = R.color.success_container;
                    textColor = R.color.on_success_container;
                    indicatorColor = R.color.success;
                    break;
                case "معلق":
                    backgroundColor = R.color.warning_container;
                    textColor = R.color.on_warning_container;
                    indicatorColor = R.color.warning;
                    break;
                case "ملغي":
                    backgroundColor = R.color.error_container;
                    textColor = R.color.on_error_container;
                    indicatorColor = R.color.error;
                    break;
                case "قيد المعالجة":
                    backgroundColor = R.color.info_container;
                    textColor = R.color.on_info_container;
                    indicatorColor = R.color.info;
                    break;
                default:
                    backgroundColor = R.color.surface_variant;
                    textColor = R.color.on_surface_variant;
                    indicatorColor = R.color.outline;
                    break;
            }
            
            statusChip.setChipBackgroundColorResource(backgroundColor);
            statusChip.setTextColor(itemView.getContext().getColor(textColor));
            statusIndicator.setBackgroundColor(itemView.getContext().getColor(indicatorColor));
        }
    }
}