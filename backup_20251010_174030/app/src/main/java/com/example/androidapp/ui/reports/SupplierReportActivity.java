package com.example.androidapp.ui.reports;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidapp.App;
import com.example.androidapp.R;
import com.example.androidapp.data.dao.InvoiceDao;
import com.example.androidapp.data.dao.SupplierDao;
import com.example.androidapp.data.entities.Invoice;
import com.example.androidapp.data.entities.Supplier;
import com.example.androidapp.ui.common.GenericAdapter;
import com.example.androidapp.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class SupplierReportActivity extends AppCompatActivity {

    private RecyclerView supplierReportRecyclerView;
    private SupplierDao supplierDao;
    private InvoiceDao invoiceDao;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supplier_report);

        supplierReportRecyclerView = findViewById(R.id.supplier_report_recycler_view);

        supplierDao = new SupplierDao(App.getDatabaseHelper());
        invoiceDao = new InvoiceDao(App.getDatabaseHelper());
        sessionManager = new SessionManager(this);

        supplierReportRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        loadSupplierReport();
    }

    private void loadSupplierReport() {
        String companyId = sessionManager.getUserDetails().get(SessionManager.KEY_COMPANY_ID);
        if (companyId == null) {
            return;
        }

        List<Supplier> suppliers = supplierDao.getSuppliersByCompanyId(companyId);
        List<SupplierReportItem> reportItems = new ArrayList<>();

        for (Supplier supplier : suppliers) {
            double totalPurchases = 0;
            List<Invoice> supplierInvoices = invoiceDao.getInvoicesBySupplierId(supplier.getId());
            for (Invoice invoice : supplierInvoices) {
                if ("purchase".equals(invoice.getInvoiceType())) { // Only count purchase invoices
                    totalPurchases += invoice.getGrandTotal();
                }
            }
            reportItems.add(new SupplierReportItem(supplier.getName(), supplier.getEmail(), supplier.getPhone(), totalPurchases));
        }

        GenericAdapter<SupplierReportItem> adapter = new GenericAdapter<SupplierReportItem>(reportItems) {
            @Override
            protected int getLayoutResId() {
                return R.layout.supplier_report_item_row;
            }

            @Override
            protected void bindView(View itemView, SupplierReportItem item) {
                TextView supplierName = itemView.findViewById(R.id.report_supplier_name);
                TextView supplierEmail = itemView.findViewById(R.id.report_supplier_email);
                TextView supplierPhone = itemView.findViewById(R.id.report_supplier_phone);
                TextView supplierTotalPurchases = itemView.findViewById(R.id.report_supplier_total_purchases);

                supplierName.setText(item.getName());
                supplierEmail.setText("البريد الإلكتروني: " + item.getEmail());
                supplierPhone.setText("الهاتف: " + item.getPhone());
                supplierTotalPurchases.setText(String.format("إجمالي المشتريات: %.2f", item.getTotalPurchases()));
            }
        };
        supplierReportRecyclerView.setAdapter(adapter);
    }

    private static class SupplierReportItem {
        String name;
        String email;
        String phone;
        double totalPurchases;

        public SupplierReportItem(String name, String email, String phone, double totalPurchases) {
            this.name = name;
            this.email = email;
            this.phone = phone;
            this.totalPurchases = totalPurchases;
        }

        public String getName() {
            return name;
        }

        public String getEmail() {
            return email;
        }

        public String getPhone() {
            return phone;
        }

        public double getTotalPurchases() {
            return totalPurchases;
        }
    }
}
