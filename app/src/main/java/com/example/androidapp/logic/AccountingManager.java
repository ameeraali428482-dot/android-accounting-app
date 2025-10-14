package com.example.androidapp.logic;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.dao.AccountStatementDao;
import com.example.androidapp.data.entities.Account;
import com.example.androidapp.data.entities.AccountStatement;
import com.example.androidapp.data.entities.BalanceSheet;
import com.example.androidapp.data.entities.Inventory;
import com.example.androidapp.data.entities.Invoice;
import com.example.androidapp.data.entities.InvoiceItem;
import com.example.androidapp.data.entities.Item;
import com.example.androidapp.data.entities.JournalEntry;
import com.example.androidapp.data.entities.JournalEntryItem;
import com.example.androidapp.data.entities.Payment;
import com.example.androidapp.data.entities.ProfitLossStatement;
import com.example.androidapp.data.entities.Purchase;
import com.example.androidapp.data.entities.Receipt;
import com.example.androidapp.data.repositories.AccountRepository;
import com.example.androidapp.data.repositories.InventoryRepository;
import com.example.androidapp.data.repositories.InvoiceRepository;
import com.example.androidapp.data.repositories.ItemRepository;
import com.example.androidapp.data.repositories.JournalEntryItemRepository;
import com.example.androidapp.data.repositories.JournalEntryRepository;
import com.example.androidapp.data.repositories.PaymentRepository;
import com.example.androidapp.data.repositories.PurchaseRepository;
import com.example.androidapp.data.repositories.ReceiptRepository;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class AccountingManager {
    private static final String TAG = "AccountingManager";
    private final AccountRepository accountRepository;
    private final InventoryRepository inventoryRepository;
    private final InvoiceRepository invoiceRepository;
    private final ItemRepository itemRepository;
    private final JournalEntryRepository journalEntryRepository;
    private final JournalEntryItemRepository journalEntryItemRepository;
    private final PaymentRepository paymentRepository;
    private final ReceiptRepository receiptRepository;
    private final PurchaseRepository purchaseRepository;
    private final AppDatabase database;

    public AccountingManager(Context context) {
        Application application = (Application) context.getApplicationContext();
        this.database = AppDatabase.getDatabase(application);
        this.accountRepository = new AccountRepository(application);
        this.inventoryRepository = new InventoryRepository(application);
        this.invoiceRepository = new InvoiceRepository(application);
        this.itemRepository = new ItemRepository(application);
        this.journalEntryRepository = new JournalEntryRepository(application);
        this.journalEntryItemRepository = new JournalEntryItemRepository(application);
        this.paymentRepository = new PaymentRepository(application);
        this.receiptRepository = new ReceiptRepository(application);
        this.purchaseRepository = new PurchaseRepository(application);
    }

    public void createJournalEntriesForInvoice(Invoice invoice, List<InvoiceItem> invoiceItems) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            try {
                String journalEntryId = UUID.randomUUID().toString();
                String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

                float totalAmount = 0;
                for (InvoiceItem item : invoiceItems) {
                    totalAmount += item.getQuantity() * item.getUnitPrice();
                }

                # ====================================================================================
# =========================== أكمل اللصق من هنا ======================================
# ====================================================================================
                JournalEntry journalEntry = new JournalEntry(
                    journalEntryId,
                    invoice.getCompanyId(),
                    currentDate,
                    "Invoice No: " + invoice.getInvoiceNumber(),
                    invoice.getId(), // referenceNumber
                    "AUTO_INVOICE", // entryType
                    totalAmount, // totalDebit (will be updated after all items are processed)
                    totalAmount  // totalCredit (will be updated after all items are processed)
                );
                journalEntryRepository.insert(journalEntry).get();

                // Debit Accounts Receivable or Cash
                Account debitAccount = null;
                if ("CASH".equals(invoice.getInvoiceType())) {
                    debitAccount = accountRepository.getAccountByNameAndCompanyId("Cash", invoice.getCompanyId()).get();
                } else { // CREDIT or CASH_CREDIT
                    debitAccount = accountRepository.getAccountByNameAndCompanyId("Accounts Receivable", invoice.getCompanyId()).get();
                }
                if (debitAccount != null) {
                    journalEntryItemRepository.insert(new JournalEntryItem(
                        journalEntryId,
                        debitAccount.getId(),
                        totalAmount,
                        0.0f,
                        "Accounts Receivable/Cash for Invoice " + invoice.getInvoiceNumber()
                    )).get();
                }

                // Credit Sales Revenue
                Account salesRevenueAccount = accountRepository.getAccountByNameAndCompanyId("Sales Revenue", invoice.getCompanyId()).get();
                if (salesRevenueAccount != null) {
                    journalEntryItemRepository.insert(new JournalEntryItem(
                        journalEntryId,
                        salesRevenueAccount.getId(),
                        0.0f,
                        totalAmount,
                        "Sales Revenue for Invoice " + invoice.getInvoiceNumber()
                    )).get();
                }

                // Create Cost of Goods Sold entries and update inventory
                createCostOfGoodsSoldEntries(journalEntryId, invoiceItems, invoice.getInvoiceNumber(), invoice.getCompanyId());

                journalEntry.setTotalDebit(totalAmount);
                journalEntry.setTotalCredit(totalAmount);
                journalEntryRepository.update(journalEntry).get();

                Log.d(TAG, "Journal entries created for invoice: " + invoice.getInvoiceNumber());
            } catch (Exception e) {
                Log.e(TAG, "Error creating journal entries for invoice: " + e.getMessage());
            }
        });
    }

    private void createCostOfGoodsSoldEntries(String journalEntryId, List<InvoiceItem> invoiceItems, String invoiceNumber, String companyId) {
        try {
            float totalCost = 0;
            for (InvoiceItem invoiceItem : invoiceItems) {
                Item item = itemRepository.getItemById(invoiceItem.getItemId(), companyId).get();
                if (item != null) {
                    float itemCost = item.getCost() * invoiceItem.getQuantity();
                    totalCost += itemCost;
                    updateInventoryForSale(invoiceItem, companyId);
                }
            }

            if (totalCost > 0) {
                // Debit Cost of Goods Sold
                Account cogsAccount = accountRepository.getAccountByNameAndCompanyId("Cost of Goods Sold", companyId).get();
                if (cogsAccount != null) {
                    journalEntryItemRepository.insert(new JournalEntryItem(
                        journalEntryId,
                        cogsAccount.getId(),
                        totalCost,
                        0.0f,
                        "COGS for Invoice " + invoiceNumber
                    )).get();
                }

                // Credit Inventory
                Account inventoryAccount = accountRepository.getAccountByNameAndCompanyId("Inventory", companyId).get();
                if (inventoryAccount != null) {
                    journalEntryItemRepository.insert(new JournalEntryItem(
                        journalEntryId,
                        inventoryAccount.getId(),
                        0.0f,
                        totalCost,
                        "Inventory for Invoice " + invoiceNumber
                    )).get();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error creating COGS entries: " + e.getMessage());
        }
    }

    private void updateInventoryForSale(InvoiceItem invoiceItem, String companyId) {
        try {
            List<Inventory> inventoryList = inventoryRepository.getInventoryForItem(invoiceItem.getItemId(), companyId).get();
            float remainingQty = invoiceItem.getQuantity();

            for (Inventory inventory : inventoryList) {
                if (remainingQty <= 0) break;

                float availableQty = inventory.getQuantity();
                if (availableQty > 0) {
                    float qtyToReduce = Math.min(remainingQty, availableQty);
                    inventory.setQuantity(availableQty - qtyToReduce);
                    inventoryRepository.update(inventory).get();
                    remainingQty -= qtyToReduce;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error updating inventory: " + e.getMessage());
        }
    }

    public void createJournalEntriesForPayment(Payment payment) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            try {
                String journalEntryId = UUID.randomUUID().toString();
                String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

                JournalEntry journalEntry = new JournalEntry(
                    journalEntryId,
                    payment.getCompanyId(),
                    currentDate,
                    "Payment Ref: " + payment.getReferenceNumber(),
                    payment.getId(),
                    "AUTO_PAYMENT",
                    payment.getAmount(),
                    payment.getAmount()
                );
                journalEntryRepository.insert(journalEntry).get();

                Account cashAccount = accountRepository.getAccountByNameAndCompanyId(getAccountNameForPaymentMethod(payment.getPaymentMethod()), payment.getCompanyId()).get();
                if (cashAccount != null) {
                    journalEntryItemRepository.insert(new JournalEntryItem(
                        journalEntryId,
                        cashAccount.getId(),
                        payment.getAmount(),
                        0.0f,
                        "Cash/Bank received for payment " + payment.getReferenceNumber()
                    )).get();
                }

                Account accountsReceivableAccount = accountRepository.getAccountByNameAndCompanyId("Accounts Receivable", payment.getCompanyId()).get();
                if (accountsReceivableAccount != null) {
                    journalEntryItemRepository.insert(new JournalEntryItem(
                        journalEntryId,
                        accountsReceivableAccount.getId(),
                        0.0f,
                        payment.getAmount(),
                        "Payment received against Accounts Receivable for " + payment.getReferenceNumber()
                    )).get();
                }

                Log.d(TAG, "Journal entries created for payment: " + payment.getReferenceNumber());
            } catch (Exception e) {
                Log.e(TAG, "Error creating journal entries for payment: " + e.getMessage());
            }
        });
    }

    public void createJournalEntriesForReceipt(Receipt receipt) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            try {
                String journalEntryId = UUID.randomUUID().toString();
                String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

                JournalEntry journalEntry = new JournalEntry(
                    journalEntryId,
                    receipt.getCompanyId(),
                    currentDate,
                    "Receipt Ref: " + receipt.getReferenceNumber(),
                    receipt.getId(),
                    "AUTO_RECEIPT",
                    receipt.getAmount(),
                    receipt.getAmount()
                );
                journalEntryRepository.insert(journalEntry).get();

                Account cashAccount = accountRepository.getAccountByNameAndCompanyId(getAccountNameForPaymentMethod(receipt.getPaymentMethod()), receipt.getCompanyId()).get();
                if (cashAccount != null) {
                    journalEntryItemRepository.insert(new JournalEntryItem(
                        journalEntryId,
                        cashAccount.getId(),
                        receipt.getAmount(),
                        0.0f,
                        "Cash/Bank received for receipt " + receipt.getReferenceNumber()
                    )).get();
                }

                Account revenueAccount = accountRepository.getAccountByNameAndCompanyId("Other Income", receipt.getCompanyId()).get();
                if (revenueAccount != null) {
                    journalEntryItemRepository.insert(new JournalEntryItem(
                        journalEntryId,
                        revenueAccount.getId(),
                        0.0f,
                        receipt.getAmount(),
                        "Revenue from receipt " + receipt.getReferenceNumber()
                    )).get();
                }

                Log.d(TAG, "Journal entries created for receipt: " + receipt.getReferenceNumber());
            } catch (Exception e) {
                Log.e(TAG, "Error creating journal entries for receipt: " + e.getMessage());
            }
        });
    }

    private String getAccountNameForPaymentMethod(String paymentMethod) {
        switch (paymentMethod) {
            case "Cash":
            case "نقد":
                return "Cash";
            case "Bank Transfer":
            case "تحويل بنكي":
            case "Credit Card":
            case "بطاقة ائتمان":
                return "Bank";
            default:
                return "Cash";
        }
    }

    public boolean validateJournalEntryBalance(String journalEntryId) {
        try {
            List<JournalEntryItem> items = journalEntryItemRepository.getJournalEntryItemsSync(journalEntryId).get();
            float totalDebit = 0;
            float totalCredit = 0;
            for (JournalEntryItem item : items) {
                totalDebit += item.getDebit();
                totalCredit += item.getCredit();
            }
            return Math.abs(totalDebit - totalCredit) < 0.001;
        } catch (Exception e) {
            Log.e(TAG, "Error validating journal entry balance: " + e.getMessage());
            return false;
        }
    }

    public boolean isInvoiceNumberUnique(String invoiceNumber, String companyId) {
        try {
            return invoiceRepository.countInvoicesByNumber(invoiceNumber, companyId).get() == 0;
        } catch (Exception e) {
            Log.e(TAG, "Error checking invoice number uniqueness: " + e.getMessage());
            return false;
        }
    }

    public boolean isReferenceNumberUnique(String referenceNumber, String companyId, String type) {
        try {
            switch (type) {
                case "PAYMENT":
                    return paymentRepository.countPaymentByReferenceNumber(referenceNumber, companyId).get() == 0;
                case "RECEIPT":
                    return receiptRepository.countReceiptByReferenceNumber(referenceNumber, companyId).get() == 0;
                case "JOURNAL":
                    return journalEntryRepository.countJournalEntryByReferenceNumber(referenceNumber, companyId).get() == 0;
                default:
                    return true;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error checking reference number uniqueness: " + e.getMessage());
            return false;
        }
    }

    public float getTotalSales(String companyId, String startDate, String endDate) {
        try {
            // This needs a proper implementation in InvoiceDao
            // return invoiceRepository.getTotalSalesByDateRange(companyId, startDate, endDate).get();
            return 0f;
        } catch (Exception e) {
            Log.e(TAG, "Error getting total sales: " + e.getMessage());
            return 0;
        }
    }

    public float getTotalPurchases(String companyId, String startDate, String endDate) {
        try {
            return purchaseRepository.getTotalPurchasesByDateRange(companyId, startDate, endDate).get();
        } catch (Exception e) {
            Log.e(TAG, "Error getting total purchases: " + e.getMessage());
            return 0;
        }
    }

    public ProfitLossStatement generateProfitAndLoss(String companyId, String startDate, String endDate) {
        try {
            float totalRevenue = getTotalSales(companyId, startDate, endDate);
            float totalCostOfGoodsSold = journalEntryItemRepository.getTotalAmountForAccountTypeAndDateRange("Cost of Goods Sold", companyId, startDate, endDate).get();
            float grossProfit = totalRevenue - totalCostOfGoodsSold;

            float operatingExpenses = journalEntryItemRepository.getTotalAmountForAccountTypeAndDateRange("Operating Expenses", companyId, startDate, endDate).get();
            double netProfit = grossProfit - operatingExpenses;

            return new ProfitLossStatement(UUID.randomUUID().toString(), companyId, startDate + " - " + endDate, totalRevenue, totalCostOfGoodsSold, netProfit, new Date().toString());
        } catch (Exception e) {
            Log.e(TAG, "Error generating P&L: " + e.getMessage());
            return new ProfitLossStatement(UUID.randomUUID().toString(), companyId, "", 0, 0, 0, new Date().toString());
        }
    }

    public BalanceSheet generateBalanceSheet(String companyId, String asOfDate) {
        Log.w(TAG, "generateBalanceSheet: Full implementation requires extensive querying of account balances.");
        return new BalanceSheet();
    }

    public float getLastPurchasePrice(String itemId, String supplierId, String companyId) {
        try {
            Item item = itemRepository.getItemById(itemId, companyId).get();
            return item != null ? item.getCost() : 0;
        } catch (Exception e) {
            Log.e(TAG, "Error getting last purchase price: " + e.getMessage());
            return 0;
        }
    }

    public void calculateAndSaveAccountStatement(AccountStatement newStatement) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            AccountStatementDao dao = database.accountStatementDao();
            AccountStatement lastStatement = dao.getLastStatementBeforeDate(
                newStatement.getCompanyId(),
                newStatement.getAccountId(),
                newStatement.getDate()
            );

            float previousBalance = (lastStatement != null) ? lastStatement.getRunningBalance() : 0.0f;
            float newBalance = previousBalance + newStatement.getCredit() - newStatement.getDebit();
            newStatement.setRunningBalance(newBalance);
            dao.insert(newStatement);

            recalculateRunningBalances(newStatement.getCompanyId(), newStatement.getAccountId(), newStatement.getDate());
        });
    }

    public void recalculateRunningBalances(String companyId, String accountId, String startDate) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            AccountStatementDao dao = database.accountStatementDao();
            AccountStatement lastStatementBefore = dao.getLastStatementBeforeDate(companyId, accountId, startDate);
            float runningBalance = (lastStatementBefore != null) ? lastStatementBefore.getRunningBalance() : 0.0f;

            List<AccountStatement> statementsToUpdate = dao.getStatementsForRecalculation(companyId, accountId, startDate);
            for (AccountStatement statement : statementsToUpdate) {
                runningBalance += statement.getCredit() - statement.getDebit();
                if (statement.getRunningBalance() != runningBalance) {
                    statement.setRunningBalance(runningBalance);
                    dao.update(statement);
                }
            }
        });
    }
}
