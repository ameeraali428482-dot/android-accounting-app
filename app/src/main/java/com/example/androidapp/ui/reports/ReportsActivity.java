package com.example.androidapp.ui.reports;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.androidapp.R;

public class ReportsActivity extends AppCompatActivity {

    private Button salesReportButton;
    private Button profitLossReportButton;
    private Button customerReportButton;
    private Button supplierReportButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);

        salesReportButton = findViewById(R.id.sales_report_button);
        profitLossReportButton = findViewById(R.id.profit_loss_report_button);
        customerReportButton = findViewById(R.id.customer_report_button);
        supplierReportButton = findViewById(R.id.supplier_report_button);

        salesReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ReportsActivity.this, SalesReportActivity.class));
            }
        });

        profitLossReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ReportsActivity.this, ProfitLossReportActivity.class));
            }
        });

        customerReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ReportsActivity.this, CustomerReportActivity.class));
            }
        });

        supplierReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ReportsActivity.this, SupplierReportActivity.class));
            }
        });
    }
}

