import java.util.Date;
package com.example.androidapp.ui.reports;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidapp.App;
import com.example.androidapp.R;
import com.example.androidapp.data.dao.InvoiceDao;
import com.example.androidapp.data.dao.InvoiceItemDao;
import com.example.androidapp.data.entities.Invoice;
import com.example.androidapp.data.entities.InvoiceItem;
import com.example.androidapp.ui.common.GenericAdapter;
import com.example.androidapp.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class SalesReportActivity extends AppCompatActivity {

    private TextView totalSalesTextView;
    private TextView totalInvoicesTextView;
    private RecyclerView salesDetailRecyclerView;
    private InvoiceDao invoiceDao;
    private InvoiceItemDao invoiceItemDao;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_report);

        totalSalesTextView = findViewById(R.id.total_sales_text_view);
        totalInvoicesTextView = findViewById(R.id.total_invoices_text_view);
        salesDetailRecyclerView = findViewById(R.id.sales_detail_recycler_view);

        invoiceDao = new InvoiceDao(App.getDatabaseHelper());
        invoiceItemDao = new InvoiceItemDao(App.getDatabaseHelper());
        sessionManager = new SessionManager(this);

        salesDetailRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        loadSalesReport();
    }

    private void loadSalesReport() {
        String companyId = sessionManager.getUserDetails().get(SessionManager.KEY_COMPANY_ID);
        if (companyId == null) {
            totalSalesTextView.setText("إجمالي المبيعات: 0.00");
            totalInvoicesTextView.setText("إجمالي الفواتير: 0");
            return;
        }

        // Fetch only sales invoices
        List<Invoice> salesInvoices = invoiceDao.getInvoicesByCompanyIdAndType(companyId, "sales");
        double totalSales = 0;
        int totalInvoices = salesInvoices.size();

        List<SalesReportItem> reportItems = new ArrayList<>();

        for (Invoice invoice : salesInvoices) {
            totalSales += invoice.getGrandTotal();
            List<InvoiceItem> items = invoiceItemDao.getInvoiceItemsByInvoiceId(invoice.getId());
            for (InvoiceItem item : items) {
                reportItems.add(new SalesReportItem(invoice.getDate(), invoice.getInvoiceType(), item.getItemName(), item.getQuantity(), item.getPrice(), item.getTotal()));
            }
        }

        totalSalesTextView.setText(String.format("إجمالي المبيعات: %.2f", totalSales));
        totalInvoicesTextView.setText(String.format("إجمالي الفواتير: %d", totalInvoices));

        GenericAdapter<SalesReportItem> adapter = new GenericAdapter<SalesReportItem>(reportItems) {
            @Override
            protected int getLayoutResId() {
                return R.layout.sales_report_item_row;
            }

            @Override
            protected void bindView(View itemView, SalesReportItem item) {
                TextView date = itemView.findViewById(R.id.report_item_date);
                TextView invoiceType = itemView.findViewById(R.id.report_item_invoice_type);
                TextView itemName = itemView.findViewById(R.id.report_item_name);
                TextView quantity = itemView.findViewById(R.id.report_item_quantity);
                TextView price = itemView.findViewById(R.id.report_item_price);
                TextView total = itemView.findViewById(R.id.report_item_total);

                date.setText(item.getDate());
                invoiceType.setText(item.getInvoiceType());
                itemName.setText(item.getItemName());
                quantity.setText(String.format("الكمية: %d", item.getQuantity()));
                price.setText(String.format("السعر: %.2f", item.getPrice()));
                total.setText(String.format("الإجمالي: %.2f", item.getTotal()));
            }
        };
        salesDetailRecyclerView.setAdapter(adapter);
    }

    // Helper class to represent a single row in the sales report detail
    private static class SalesReportItem {
        String date;
        String invoiceType;
        String itemName;
        int quantity;
        float price;
        float total;

        public SalesReportItem(String date, String invoiceType, String itemName, int quantity, float price, float total) {
            this.date = date;
            this.invoiceType = invoiceType;
            this.itemName = itemName;
            this.quantity = quantity;
            this.price = price;
            this.total = total;
        }

        public String getDate() {
            return date;
        }

        public String getInvoiceType() {
            return invoiceType;
        }

        public String getItemName() {
            return itemName;
        }

        public int getQuantity() {
            return quantity;
        }

        public float getPrice() {
            return price;
        }

        public float getTotal() {
            return total;
        }
    }
}

