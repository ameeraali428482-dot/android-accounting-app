package com.example.androidapp.ui.reports;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidapp.R;
import com.google.android.material.button.MaterialButton;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ReportCardAdapter extends RecyclerView.Adapter<ReportCardAdapter.ReportCardViewHolder> {

    private List<ReportItem> reportItems;
    private OnReportItemClickListener clickListener;
    private OnShareItemClickListener shareClickListener;
    private NumberFormat currencyFormatter;
    private SimpleDateFormat dateFormatter;

    public interface OnReportItemClickListener {
        void onReportItemClicked(ReportItem item);
    }

    public interface OnShareItemClickListener {
        void onShareItemClicked(ReportItem item);
    }

    public ReportCardAdapter(List<ReportItem> reportItems, 
                           OnReportItemClickListener clickListener,
                           OnShareItemClickListener shareClickListener) {
        this.reportItems = new ArrayList<>(reportItems);
        this.clickListener = clickListener;
        this.shareClickListener = shareClickListener;
        this.currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("ar", "SA"));
        this.dateFormatter = new SimpleDateFormat("dd MMMM yyyy", new Locale("ar", "SA"));
    }

    @NonNull
    @Override
    public ReportCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_report_card, parent, false);
        return new ReportCardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportCardViewHolder holder, int position) {
        ReportItem item = reportItems.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return reportItems.size();
    }

    public void updateData(List<ReportItem> newData) {
        this.reportItems.clear();
        this.reportItems.addAll(newData);
        notifyDataSetChanged();
    }

    public List<ReportItem> getCurrentData() {
        return new ArrayList<>(reportItems);
    }

    public class ReportCardViewHolder extends RecyclerView.ViewHolder {
        private ImageView reportItemIcon;
        private TextView reportItemTitle;
        private TextView reportItemDate;
        private TextView reportItemStatus;
        private TextView reportItemCustomer;
        private TextView reportItemAmount;
        private MaterialButton viewDetailsButton;
        private MaterialButton shareItemButton;

        public ReportCardViewHolder(@NonNull View itemView) {
            super(itemView);
            
            reportItemIcon = itemView.findViewById(R.id.reportItemIcon);
            reportItemTitle = itemView.findViewById(R.id.reportItemTitle);
            reportItemDate = itemView.findViewById(R.id.reportItemDate);
            reportItemStatus = itemView.findViewById(R.id.reportItemStatus);
            reportItemCustomer = itemView.findViewById(R.id.reportItemCustomer);
            reportItemAmount = itemView.findViewById(R.id.reportItemAmount);
            viewDetailsButton = itemView.findViewById(R.id.viewDetailsButton);
            shareItemButton = itemView.findViewById(R.id.shareItemButton);

            viewDetailsButton.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && clickListener != null) {
                    clickListener.onReportItemClicked(reportItems.get(position));
                }
            });

            shareItemButton.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && shareClickListener != null) {
                    shareClickListener.onShareItemClicked(reportItems.get(position));
                }
            });
        }

        public void bind(ReportItem item) {
            // Set title
            reportItemTitle.setText(item.getType() + " #" + item.getId());
            
            // Set date
            reportItemDate.setText(dateFormatter.format(item.getDate()));
            
            // Set customer
            reportItemCustomer.setText(item.getCustomer());
            
            // Set amount
            reportItemAmount.setText(currencyFormatter.format(item.getAmount()));
            
            // Set status
            reportItemStatus.setText(item.getStatus());
            
            // Set icon based on type
            if (item.getType().contains("فاتورة")) {
                reportItemIcon.setImageResource(R.drawable.ic_receipt_24);
            } else if (item.getType().contains("دفع")) {
                reportItemIcon.setImageResource(R.drawable.ic_payment_24);
            } else {
                reportItemIcon.setImageResource(R.drawable.ic_analytics_24);
            }

            // Set status colors
            switch (item.getStatus()) {
                case "مكتملة":
                    reportItemStatus.setBackgroundResource(R.drawable.bg_status_success);
                    break;
                case "معلقة":
                    reportItemStatus.setBackgroundResource(R.drawable.bg_status_pending);
                    break;
                case "ملغية":
                    reportItemStatus.setBackgroundResource(R.drawable.bg_status_error);
                    break;
                case "قيد المعالجة":
                    reportItemStatus.setBackgroundResource(R.drawable.bg_status_warning);
                    break;
                default:
                    reportItemStatus.setBackgroundResource(R.drawable.bg_status_pill);
                    break;
            }
        }
    }
}