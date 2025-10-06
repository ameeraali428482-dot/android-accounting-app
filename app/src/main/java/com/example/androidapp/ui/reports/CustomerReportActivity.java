package com.example.androidapp.ui.reports;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidapp.App;
import com.example.androidapp.R;
import com.example.androidapp.data.dao.CustomerDao;
import com.example.androidapp.data.dao.InvoiceDao;
import com.example.androidapp.models.Customer;
import com.example.androidapp.models.Invoice;
import com.example.androidapp.ui.common.GenericAdapter;
import com.example.androidapp.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class CustomerReportActivity extends AppCompatActivity {

    private RecyclerView customerReportRecyclerView;
    private CustomerDao customerDao;
    private InvoiceDao invoiceDao;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_report);

        customerReportRecyclerView = findViewById(R.id.customer_report_recycler_view);

        customerDao = new CustomerDao(App.getDatabaseHelper());
        invoiceDao = new InvoiceDao(App.getDatabaseHelper());
        sessionManager = new SessionManager(this);

        customerReportRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        loadCustomerReport();
    }

    private void loadCustomerReport() {
        String companyId = sessionManager.getUserDetails().get(SessionManager.KEY_COMPANY_ID);
        if (companyId == null) {
            return;
        }

        List<Customer> customers = customerDao.getCustomersByCompanyId(companyId);
        List<CustomerReportItem> reportItems = new ArrayList<>();

        for (Customer customer : customers) {
            double totalSales = 0;
            List<Invoice> customerInvoices = invoiceDao.getInvoicesByCustomerId(customer.getId());
            for (Invoice invoice : customerInvoices) {
                if ("sales".equals(invoice.getInvoiceType())) { // Only count sales invoices
                    totalSales += invoice.getGrandTotal();
                }
            }
            reportItems.add(new CustomerReportItem(customer.getName(), customer.getEmail(), customer.getPhone(), totalSales));
        }

        GenericAdapter<CustomerReportItem> adapter = new GenericAdapter<CustomerReportItem>(reportItems) {
            @Override
            protected int getLayoutResId() {
                return R.layout.customer_report_item_row;
            }

            @Override
            protected void bindView(View itemView, CustomerReportItem item) {
                TextView customerName = itemView.findViewById(R.id.report_customer_name);
                TextView customerEmail = itemView.findViewById(R.id.report_customer_email);
                TextView customerPhone = itemView.findViewById(R.id.report_customer_phone);
                TextView customerTotalSales = itemView.findViewById(R.id.report_customer_total_sales);

                customerName.setText(item.getName());
                customerEmail.setText("البريد الإلكتروني: " + item.getEmail());
                customerPhone.setText("الهاتف: " + item.getPhone());
                customerTotalSales.setText(String.format("إجمالي المبيعات: %.2f", item.getTotalSales()));
            }
        };
        customerReportRecyclerView.setAdapter(adapter);
    }

    private static class CustomerReportItem {
        String name;
        String email;
        String phone;
        double totalSales;

        public CustomerReportItem(String name, String email, String phone, double totalSales) {
            this.name = name;
            this.email = email;
            this.phone = phone;
            this.totalSales = totalSales;
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

        public double getTotalSales() {
            return totalSales;
        }
    }
}
