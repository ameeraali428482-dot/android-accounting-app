package com.example.androidapp.ui.invoice;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.androidapp.R;
import com.example.androidapp.data.entities.Invoice;
import com.example.androidapp.data.entities.InvoiceItem;
import com.example.androidapp.ui.invoice.viewmodel.InvoiceViewModel;
import com.example.androidapp.utils.SessionManager;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;






public class InvoiceDetailActivity extends AppCompatActivity {

    private EditText etInvoiceNumber, etInvoiceDate, etCustomerName, etInvoiceType, etSubTotal, etTax, etDiscount, etGrandTotal;
    private LinearLayout invoiceItemsContainer;
    private Button btnAddItem, btnSave, btnDelete;
    private InvoiceViewModel viewModel;
    private SessionManager sessionManager;
    private String companyId;
    private String invoiceId = null;
    private List<InvoiceItem> currentItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice_detail);

        sessionManager = new SessionManager(this);
        companyId = sessionManager.getCompanyId();

        if (companyId == null) {
            Toast.makeText(this, "معرف الشركة غير صالح", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        viewModel = new ViewModelProvider(this).get(InvoiceViewModel.class);

        initViews();
        setupListeners();

        invoiceId = getIntent().getStringExtra("invoice_id");

        if (invoiceId != null) {
            setTitle("تعديل فاتورة");
            loadInvoiceDetails(invoiceId);
            btnDelete.setVisibility(View.VISIBLE);
        } else {
            setTitle("إضافة فاتورة جديدة");
            etInvoiceDate.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date()));
            btnDelete.setVisibility(View.GONE);
            addItemView(null); // Add one empty item for new invoice
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initViews() {
        etInvoiceNumber = // TODO: Fix findViewById;
        etInvoiceDate = // TODO: Fix findViewById;
        etCustomerName = // TODO: Fix findViewById;
        etInvoiceType = // TODO: Fix findViewById;
        etSubTotal = // TODO: Fix findViewById;
        etTax = // TODO: Fix findViewById;
        etDiscount = // TODO: Fix findViewById;
        etGrandTotal = // TODO: Fix findViewById;
        invoiceItemsContainer = // TODO: Fix findViewById;
        btnAddItem = // TODO: Fix findViewById;
        btnSave = // TODO: Fix findViewById;
        btnDelete = // TODO: Fix findViewById;

        etSubTotal.setEnabled(false);
        etGrandTotal.setEnabled(false);
    }

    private void setupListeners() {
        btnAddItem.setOnClickListener(v -> addItemView(null));
        btnSave.setOnClickListener(v -> saveInvoice());
        btnDelete.setOnClickListener(v -> deleteInvoice());
    }

    private void loadInvoiceDetails(String id) {
        viewModel.getInvoiceById(id, companyId).observe(this, invoice -> {
            if (invoice != null) {
                etInvoiceNumber.setText(invoice.getInvoiceNumber());
                etInvoiceDate.setText(invoice.getInvoiceDate());
                etCustomerName.setText(invoice.getCustomerName());
                etInvoiceType.setText(invoice.getInvoiceType());
                etSubTotal.setText(String.valueOf(invoice.getSubTotal()));
                etTax.setText(String.valueOf(invoice.getTaxAmount()));
                etDiscount.setText(String.valueOf(invoice.getDiscountAmount()));
                etGrandTotal.setText(String.valueOf(invoice.getGrandTotal()));
                // TODO: Load invoice items (requires InvoiceItemDao and ViewModel support)
            } else {
                Toast.makeText(this, "لم يتم العثور على الفاتورة", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void addItemView(InvoiceItem item) {
        View itemView = getLayoutInflater().inflate(R.layout.invoice_item_row, invoiceItemsContainer, false);
        EditText etItemName = itemView.// TODO: Fix findViewById;
        EditText etQuantity = itemView.// TODO: Fix findViewById;
        EditText etUnitPrice = itemView.// TODO: Fix findViewById;
        EditText etItemTotal = itemView.// TODO: Fix findViewById;
        Button btnRemove = itemView.// TODO: Fix findViewById;

        etItemTotal.setEnabled(false);

        if (item != null) {
            etItemName.setText(item.getItemName());
            etQuantity.setText(String.valueOf(item.getQuantity()));
            etUnitPrice.setText(String.valueOf(item.getUnitPrice()));
            etItemTotal.setText(String.valueOf(item.getTotal()));
        }

        btnRemove.setOnClickListener(v -> {
            invoiceItemsContainer.removeView(itemView);
            calculateTotals();
        });

        // Add listeners to quantity and unit price to update item total
        etQuantity.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) calculateItemTotal(itemView);
        });
        etUnitPrice.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) calculateItemTotal(itemView);
        });

        invoiceItemsContainer.addView(itemView);
        calculateTotals();
    }

    private void calculateItemTotal(View itemView) {
        EditText etQuantity = itemView.// TODO: Fix findViewById;
        EditText etUnitPrice = itemView.// TODO: Fix findViewById;
        EditText etItemTotal = itemView.// TODO: Fix findViewById;

        float quantity = Float.parseFloat(etQuantity.getText().toString().trim().isEmpty() ? "0" : etQuantity.getText().toString().trim());
        float unitPrice = Float.parseFloat(etUnitPrice.getText().toString().trim().isEmpty() ? "0" : etUnitPrice.getText().toString().trim());
        float itemTotal = quantity * unitPrice;
        etItemTotal.setText(String.valueOf(itemTotal));
        calculateTotals();
    }

    private void calculateTotals() {
        float subTotal = 0.0f;
        for (int i = 0; i < invoiceItemsContainer.getChildCount(); i++) {
            View itemView = invoiceItemsContainer.getChildAt(i);
            EditText etItemTotal = itemView.// TODO: Fix findViewById;
            subTotal += Float.parseFloat(etItemTotal.getText().toString().trim().isEmpty() ? "0" : etItemTotal.getText().toString().trim());
        }
        etSubTotal.setText(String.valueOf(subTotal));

        float taxRate = Float.parseFloat(etTax.getText().toString().trim().isEmpty() ? "0" : etTax.getText().toString().trim());
        float discountRate = Float.parseFloat(etDiscount.getText().toString().trim().isEmpty() ? "0" : etDiscount.getText().toString().trim());

        float taxAmount = subTotal * (taxRate / 100);
        float discountAmount = subTotal * (discountRate / 100);
        float grandTotal = subTotal + taxAmount - discountAmount;

        etTax.setText(String.valueOf(taxAmount)); // Display calculated tax amount
        etDiscount.setText(String.valueOf(discountAmount)); // Display calculated discount amount
        etGrandTotal.setText(String.valueOf(grandTotal));
    }

    private void saveInvoice() {
        String invoiceNumber = etInvoiceNumber.getText().toString().trim();
        String invoiceDate = etInvoiceDate.getText().toString().trim();
        String customerName = etCustomerName.getText().toString().trim();
        String invoiceType = etInvoiceType.getText().toString().trim();
        float subTotal = Float.parseFloat(etSubTotal.getText().toString().trim().isEmpty() ? "0" : etSubTotal.getText().toString().trim());
        float taxAmount = Float.parseFloat(etTax.getText().toString().trim().isEmpty() ? "0" : etTax.getText().toString().trim());
        float discountAmount = Float.parseFloat(etDiscount.getText().toString().trim().isEmpty() ? "0" : etDiscount.getText().toString().trim());
        float grandTotal = Float.parseFloat(etGrandTotal.getText().toString().trim().isEmpty() ? "0" : etGrandTotal.getText().toString().trim());

        if (invoiceNumber.isEmpty() || invoiceDate.isEmpty() || customerName.isEmpty() || invoiceType.isEmpty()) {
            Toast.makeText(this, "الرجاء تعبئة جميع الحقول الرئيسية.", Toast.LENGTH_SHORT).show();
            return;
        }

        Invoice invoice;
        if (invoiceId == null) {
            // New invoice
            invoiceId = UUID.randomUUID().toString();
            invoice = new Invoice(invoiceId, companyId, invoiceNumber, invoiceDate, customerName, invoiceType, subTotal, taxAmount, discountAmount, grandTotal);
            viewModel.insert(invoice);
            // TODO: Insert invoice items
            Toast.makeText(this, "تم إضافة الفاتورة بنجاح.", Toast.LENGTH_SHORT).show();
        } else {
            // Existing invoice
            invoice = new Invoice(invoiceId, companyId, invoiceNumber, invoiceDate, customerName, invoiceType, subTotal, taxAmount, discountAmount, grandTotal);
            viewModel.update(invoice);
            // TODO: Update/delete existing invoice items and insert new ones
            Toast.makeText(this, "تم تحديث الفاتورة بنجاح.", Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    private void deleteInvoice() {
        if (invoiceId != null) {
            viewModel.getInvoiceById(invoiceId, companyId).observe(this, invoice -> {
                if (invoice != null) {
                    viewModel.delete(invoice);
                    Toast.makeText(this, "تم حذف الفاتورة بنجاح.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
