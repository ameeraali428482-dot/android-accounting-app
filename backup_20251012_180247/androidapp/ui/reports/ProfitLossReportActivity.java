package com.example.androidapp.ui.reports;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.androidapp.App;
import com.example.androidapp.R;
import com.example.androidapp.data.dao.InvoiceDao;
import com.example.androidapp.data.dao.JournalEntryDao;
import com.example.androidapp.data.entities.Invoice;
import com.example.androidapp.data.entities.JournalEntry;
import com.example.androidapp.utils.SessionManager;
import java.util.List;






public class ProfitLossReportActivity extends AppCompatActivity {

    private TextView totalIncomeTextView;
    private TextView totalExpensesTextView;
    private TextView netProfitLossTextView;

    private InvoiceDao invoiceDao;
    private JournalEntryDao journalEntryDao;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profit_loss_report);


        invoiceDao = new InvoiceDao(App.getDatabaseHelper());
        journalEntryDao = new JournalEntryDao(App.getDatabaseHelper());
        sessionManager = new SessionManager(this);

        loadProfitLossReport();
    }

    private void loadProfitLossReport() {
        String companyId = sessionManager.getUserDetails().get(SessionManager.KEY_COMPANY_ID);
        if (companyId == null) {
            totalIncomeTextView.setText("إجمالي الإيرادات: 0.00");
            totalExpensesTextView.setText("إجمالي المصروفات: 0.00");
            netProfitLossTextView.setText("صافي الربح/الخسارة: 0.00");
            return;
        }

        double totalIncome = 0;
        double totalExpenses = 0;

        // Calculate income from sales invoices
        List<Invoice> salesInvoices = invoiceDao.getInvoicesByCompanyIdAndType(companyId, "sales");
        for (Invoice invoice : salesInvoices) {
            totalIncome += invoice.getGrandTotal();
        }

        // Calculate income from other journal entries (assuming type "income")
        List<JournalEntry> incomeEntries = journalEntryDao.getJournalEntriesByCompanyIdAndType(companyId, "income");
        for (JournalEntry entry : incomeEntries) {
            totalIncome += entry.getAmount();
        }

        // Calculate expenses from purchase invoices
        List<Invoice> purchaseInvoices = invoiceDao.getInvoicesByCompanyIdAndType(companyId, "purchase");
        for (Invoice invoice : purchaseInvoices) {
            totalExpenses += invoice.getGrandTotal();
        }

        // Calculate expenses from other journal entries (assuming type "expense")
        List<JournalEntry> expenseEntries = journalEntryDao.getJournalEntriesByCompanyIdAndType(companyId, "expense");
        for (JournalEntry entry : expenseEntries) {
            totalExpenses += entry.getAmount();
        }

        double netProfitLoss = totalIncome - totalExpenses;

        totalIncomeTextView.setText(String.format("إجمالي الإيرادات: %.2f", totalIncome));
        totalExpensesTextView.setText(String.format("إجمالي المصروفات: %.2f", totalExpenses));
        netProfitLossTextView.setText(String.format("صافي الربح/الخسارة: %.2f", netProfitLoss));
    }
}
