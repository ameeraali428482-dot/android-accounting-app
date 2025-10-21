package com.example.androidapp.ui.reports;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidapp.R;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ReportTableAdapter extends RecyclerView.Adapter<ReportTableAdapter.ReportTableViewHolder> {

    private List<ReportItem> reportItems;
    private OnReportItemClickListener clickListener;
    private NumberFormat currencyFormatter;
    private SimpleDateFormat dateFormatter;

    public interface OnReportItemClickListener {
        void onReportItemClicked(ReportItem item);
    }

    public ReportTableAdapter(List<ReportItem> reportItems, OnReportItemClickListener clickListener) {
        this.reportItems = new ArrayList<>(reportItems);
        this.clickListener = clickListener;
        this.currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("ar", "SA"));
        this.dateFormatter = new SimpleDateFormat("yyyy-MM-dd", new Locale("ar", "SA"));
    }

    @NonNull
    @Override
    public ReportTableViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_report_row, parent, false);
        return new ReportTableViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportTableViewHolder holder, int position) {
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

    public class ReportTableViewHolder extends RecyclerView.ViewHolder {
        private TextView dateText;
        private TextView customerText;
        private TextView amountText;
        private TextView statusText;

        public ReportTableViewHolder(@NonNull View itemView) {
            super(itemView);
            dateText = itemView.findViewById(R.id.dateText);
            customerText = itemView.findViewById(R.id.customerText);
            amountText = itemView.findViewById(R.id.amountText);
            statusText = itemView.findViewById(R.id.statusText);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && clickListener != null) {
                    clickListener.onReportItemClicked(reportItems.get(position));
                }
            });
        }

        public void bind(ReportItem item) {
            dateText.setText(dateFormatter.format(item.getDate()));
            customerText.setText(item.getCustomer());
            amountText.setText(currencyFormatter.format(item.getAmount()));
            statusText.setText(item.getStatus());

            // Set status colors
            switch (item.getStatus()) {
                case "مكتملة":
                    statusText.setBackgroundResource(R.drawable.bg_status_success);
                    break;
                case "معلقة":
                    statusText.setBackgroundResource(R.drawable.bg_status_pending);
                    break;
                case "ملغية":
                    statusText.setBackgroundResource(R.drawable.bg_status_error);
                    break;
                case "قيد المعالجة":
                    statusText.setBackgroundResource(R.drawable.bg_status_warning);
                    break;
                default:
                    statusText.setBackgroundResource(R.drawable.bg_status_pill);
                    break;
            }
        }
    }
}