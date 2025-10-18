package com.example.androidapp.ui.reports;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.androidapp.R;
import com.example.androidapp.data.AppDatabase;
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

        totalIncomeTextView = findViewById(R.id.total_income_text_view);
        totalExpensesTextView = findViewById(R.id.total_expenses_text_view);
        netProfitLossTextView = findViewById(R.id.net_profit_loss_text_view);

        AppDatabase db = AppDatabase.getDatabase(this);
        invoiceDao = db.invoiceDao();
        journalEntryDao = db.journalEntryDao();
        sessionManager = new SessionManager(this);

        loadProfitLossReport();
    }

    private void loadProfitLossReport() {
        String companyId = sessionManager.getCurrentCompanyId();
        if (companyId == null) {
            totalIncomeTextView.setText("إجمالي الإيرادات: 0.00");
            totalExpensesTextView.setText("إجمالي المصروفات: 0.00");
            netProfitLossTextView.setText("صافي الربح/الخسارة: 0.00");
            return;
        }

        AppDatabase.databaseWriteExecutor.execute(() -> {
            double totalIncome = 0;
            double totalExpenses = 0;

            List<Invoice> salesInvoices = invoiceDao.getInvoicesByCompanyIdAndType(companyId, "sales");
            for (Invoice invoice : salesInvoices) {
                totalIncome += invoice.getGrandTotal();
            }

            List<JournalEntry> incomeEntries = journalEntryDao.getJournalEntriesByCompanyIdAndType(companyId, "income");
            for (JournalEntry entry : incomeEntries) {
                totalIncome += entry.getAmount();
            }

            List<Invoice> purchaseInvoices = invoiceDao.getInvoicesByCompanyIdAndType(companyId, "purchase");
            for (Invoice invoice : purchaseInvoices) {
                totalExpenses += invoice.getGrandTotal();
            }

            List<JournalEntry> expenseEntries = journalEntryDao.getJournalEntriesByCompanyIdAndType(companyId, "expense");
            for (JournalEntry entry : expenseEntries) {
                totalExpenses += entry.getAmount();
            }

            double finalTotalIncome = totalIncome;
            double finalTotalExpenses = totalExpenses;
            double netProfitLoss = finalTotalIncome - finalTotalExpenses;

            runOnUiThread(() -> {
                totalIncomeTextView.setText(String.format("إجمالي الإيرادات: %.2f", finalTotalIncome));
                totalExpensesTextView.setText(String.format("إجمالي المصروفات: %.2f", finalTotalExpenses));
                netProfitLossTextView.setText(String.format("صافي الربح/الخسارة: %.2f", netProfitLoss));
            });
        });
    }
}
