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

    /**
     * Creates automatic journal entries for an invoice.
     * This method assumes a double-entry accounting system.
     * For a sales invoice, typically: Accounts Receivable (Debit), Sales Revenue (Credit), Sales Tax Payable (Credit)
     * For a purchase invoice, typically: Purchases/Expenses (Debit), Accounts Payable (Credit), Input Tax Credit (Debit)
     *
     * @param invoice The invoice for which to create the journal entry.
     * @param invoiceItems The list of items in the invoice.
     */
    public void createJournalEntriesForInvoice(Invoice invoice, List<InvoiceItem> invoiceItems) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            try {
                String journalEntryId = UUID.randomUUID().toString();
                String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

                float totalAmount = 0;
                for (InvoiceItem item : invoiceItems) {
                    totalAmount += item.getQuantity() * item.getUnitPrice();
                }

                // Create main journal entry with initial total debit/credit as totalAmount
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

                // Update the main JournalEntry with final calculated totals (if necessary, though for simple sales, totalAmount should balance)
                // For more complex scenarios (e.g., taxes, discounts), these totals would need recalculation
                journalEntry.setTotalDebit(totalAmount);
                journalEntry.setTotalCredit(totalAmount);
                journalEntryRepository.update(journalEntry).get();

                Log.d(TAG, "Journal entries created for invoice: " + invoice.getInvoiceNumber());
            } catch (Exception e) {
                Log.e(TAG, "Error creating journal entries for invoice: " + e.getMessage());
            }
        });
    }

    /**
     * Creates cost of goods sold journal entries and updates inventory.
     *
     * @param journalEntryId The ID of the main journal entry.
     * @param invoiceItems The list of items in the invoice.
     * @param invoiceNumber The invoice number.
     * @param companyId The ID of the company.
     */
    private void createCostOfGoodsSoldEntries(String journalEntryId, List<InvoiceItem> invoiceItems, String invoiceNumber, String companyId) {
        try {
            float totalCost = 0;
            for (InvoiceItem invoiceItem : invoiceItems) {
                Item item = itemRepository.getItemById(invoiceItem.getItemId(), companyId).get();
                if (item != null) {
                    float itemCost = item.getCostPrice() * invoiceItem.getQuantity();
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
                    journalEntryItemRepository.insert(new JournalEntryItem(journalEntryId, 
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

    /**
     * Updates inventory quantities for sold items.
     *
     * @param invoiceItem The invoice item being sold.
     * @param companyId The ID of the company.
     */
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

    /**
     * Creates journal entries for payments.
     * Typically: Cash/Bank (Debit), Accounts Receivable (Credit)
     *
     * @param payment The payment object.
     */
    public void createJournalEntriesForPayment(Payment payment) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            try {
                String journalEntryId = UUID.randomUUID().toString();
                String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

                // Create main journal entry
                JournalEntry journalEntry = new JournalEntry(
                    journalEntryId,
                    payment.getCompanyId(),
                    currentDate,
                    "Payment Ref: " + payment.getReferenceNumber(),
                    payment.getId(), // referenceNumber
                    "AUTO_PAYMENT", // entryType
                    payment.getAmount(), // totalDebit
                    payment.getAmount()  // totalCredit
                );
                journalEntryRepository.insert(journalEntry).get();

                // Debit Cash/Bank account
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

                // Credit Accounts Receivable (assuming payment is against an invoice)
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

    /**
     * Creates journal entries for receipts.
     * Typically: Cash/Bank (Debit), Specific Revenue Account (Credit)
     *
     * @param receipt The receipt object.
     */
    public void createJournalEntriesForReceipt(Receipt receipt) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            try {
                String journalEntryId = UUID.randomUUID().toString();
                String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

                // Create main journal entry
                JournalEntry journalEntry = new JournalEntry(
                    journalEntryId,
                    receipt.getCompanyId(),
                    currentDate,
                    "Receipt Ref: " + receipt.getReferenceNumber(),
                    receipt.getId(), // referenceNumber
                    "AUTO_RECEIPT", // entryType
                    receipt.getAmount(), // totalDebit
                    receipt.getAmount()  // totalCredit
                );
                journalEntryRepository.insert(journalEntry).get();

                // Debit Cash/Bank account
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

                // Credit a specific revenue account (e.g., Other Income, Service Revenue)
                Account revenueAccount = accountRepository.getAccountByNameAndCompanyId("Other Income", receipt.getCompanyId()).get(); // Example
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

    /**
     * Gets the appropriate account name based on the payment method.
     *
     * @param paymentMethod The payment method string.
     * @return The corresponding account name.
     */
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
                return "Cash"; // Default to Cash
        }
    }

    /**
     * Validates if the total debits equal total credits for a given journal entry.
     *
     * @param journalEntryId The ID of the journal entry to validate.
     * @return True if balanced, false otherwise.
     */
    public boolean validateJournalEntryBalance(String journalEntryId) {
        try {
            List<JournalEntryItem> items = journalEntryItemRepository.getJournalEntryItems(journalEntryId).get();
            float totalDebit = 0;
            float totalCredit = 0;
            for (JournalEntryItem item : items) {
                totalDebit += item.getDebit();
                totalCredit += item.getCredit();
            }
            return Math.abs(totalDebit - totalCredit) < 0.001; // Using a tolerance for float comparison
        } catch (Exception e) {
            Log.e(TAG, "Error validating journal entry balance: " + e.getMessage());
            return false;
        }
    }

    /**
     * Checks if an invoice number is unique for a given company.
     *
     * @param invoiceNumber The invoice number to check.
     * @param companyId The ID of the company.
     * @return True if unique, false otherwise.
     */
    public boolean isInvoiceNumberUnique(String invoiceNumber, String companyId) {
        try {
            return invoiceRepository.countInvoicesByNumber(invoiceNumber, companyId).get() == 0;
        } catch (Exception e) {
            Log.e(TAG, "Error checking invoice number uniqueness: " + e.getMessage());
            return false;
        }
    }

    /**
     * Checks if a reference number is unique for a given company and type.
     *
     * @param referenceNumber The reference number to check.
     * @param companyId The ID of the company.
     * @param type The type of reference (e.g., "PAYMENT", "RECEIPT", "JOURNAL").
     * @return True if unique, false otherwise.
     */
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

    /**
     * Calculates the total sales for a given period.
     *
     * @param companyId The ID of the company.
     * @param startDate The start date of the period.
     * @param endDate The end date of the period.
     * @return The total sales amount.
     */
    public float getTotalSales(String companyId, String startDate, String endDate) {
        try {
            return invoiceRepository.getTotalSalesByDateRange(companyId, startDate, endDate).get();
        } catch (Exception e) {
            Log.e(TAG, "Error getting total sales: " + e.getMessage());
            return 0;
        }
    }

    /**
     * Calculates the total purchases for a given period.
     *
     * @param companyId The ID of the company.
     * @param startDate The start date of the period.
     * @param endDate The end date of the period.
     * @return The total purchases amount.
     */
    public float getTotalPurchases(String companyId, String startDate, String endDate) {
        try {
            return purchaseRepository.getTotalPurchasesByDateRange(companyId, startDate, endDate).get();
        } catch (Exception e) {
            Log.e(TAG, "Error getting total purchases: " + e.getMessage());
            return 0;
        }
    }

    /**
     * Generates a profit and loss statement.
     *
     * @param companyId The ID of the company.
     * @param startDate The start date of the period.
     * @param endDate The end date of the period.
     * @return A map or a custom object representing the P&L statement.
     */
    public ProfitLossStatement generateProfitAndLoss(String companyId, String startDate, String endDate) {
        try {
            float totalRevenue = getTotalSales(companyId, startDate, endDate);
            float totalCostOfGoodsSold = journalEntryItemRepository.getTotalAmountForAccountTypeAndDateRange("Cost of Goods Sold", companyId, startDate, endDate).get();
            float grossProfit = totalRevenue - totalCostOfGoodsSold;

            float operatingExpenses = journalEntryItemRepository.getTotalAmountForAccountTypeAndDateRange("Operating Expenses", companyId, startDate, endDate).get();
            float netProfit = grossProfit - operatingExpenses;

            return new ProfitLossStatement(totalRevenue, totalCostOfGoodsSold, grossProfit, operatingExpenses, netProfit);
        } catch (Exception e) {
            Log.e(TAG, "Error generating P&L: " + e.getMessage());
            return new ProfitLossStatement(0, 0, 0, 0, 0);
        }
    }

    /**
     * Generates a balance sheet.
     *
     * @param companyId The ID of the company.
     * @param asOfDate The date for which to generate the balance sheet.
     * @return A map or a custom object representing the balance sheet.
     */
    public BalanceSheet generateBalanceSheet(String companyId, String asOfDate) {
        // This is a highly complex function requiring aggregation of all asset, liability, and equity accounts.
        // A full implementation would involve:
        // 1. Getting all asset accounts and summing their balances up to asOfDate.
        // 2. Getting all liability accounts and summing their balances up to asOfDate.
        // 3. Calculating equity (Owner's Equity + Retained Earnings + Net Profit/Loss).
        // This placeholder returns an empty BalanceSheet object.
        Log.w(TAG, "generateBalanceSheet: Full implementation requires extensive querying of account balances.");
        return new BalanceSheet();
    }

    /**
     * Gets the last purchase price for a given item.
     * This is a placeholder as we don't have purchase records.
     * In a real scenario, you'd look at purchase invoices or bills.
     *
     * @param itemId The ID of the item.
     * @param supplierId The ID of the supplier.
     * @param companyId The ID of the company.
     * @return The last purchase price.
     */
    public float getLastPurchasePrice(String itemId, String supplierId, String companyId) {
        // Placeholder: Returning the cost price from the Item table for now.
        try {
            Item item = itemRepository.getItemById(itemId, companyId).get();
            return item != null ? item.getCostPrice() : 0;
        } catch (Exception e) {
            Log.e(TAG, "Error getting last purchase price: " + e.getMessage());
            return 0;
        }
    }

    /**
     * Adds or updates an account statement and recalculates running balances.
     *
     * @param newStatement The AccountStatement object to add or update.
     */
    public void addOrUpdateAccountStatement(AccountStatement newStatement) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            try {
                AccountStatementDao dao = database.accountStatementDao();
                List<AccountStatement> existingStatements = dao.getAccountStatementsForBalanceCalculation(
                    newStatement.getCompanyId(),
                    newStatement.getAccountId(),
                    newStatement.getTransactionDate()
                );

                float currentRunningBalance = 0.0f;
                if (!existingStatements.isEmpty()) {
                    // Assuming the list is ordered by date descending, the first one is the latest before or on transactionDate
                    currentRunningBalance = existingStatements.get(0).getRunningBalance();
                }

                newStatement.setRunningBalance(currentRunningBalance + newStatement.getDebit() - newStatement.getCredit());
                dao.insert(newStatement);

                recalculateRunningBalances(newStatement.getCompanyId(), newStatement.getAccountId(), newStatement.getTransactionDate());

                Log.d(TAG, "Account statement added/updated and balances recalculated for account: " + newStatement.getAccountId());
            } catch (Exception e) {
                Log.e(TAG, "Error adding or updating account statement: " + e.getMessage());
            }
        });
    }

    /**
     * Recalculates running balances for account statements from a given start date.
     *
     * @param companyId The ID of the company.
     * @param accountId The ID of the account.
     * @param startDate The date from which to start recalculation.
     */
    public void recalculateRunningBalances(String companyId, String accountId, String startDate) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            try {
                AccountStatementDao dao = database.accountStatementDao();
                List<AccountStatement> statementsToRecalculate = dao.getStatementsForRecalculation(companyId, accountId, startDate);

                float runningBalance = 0.0f;
                AccountStatement lastStatementBeforeStartDate = dao.getLastStatementBeforeDate(companyId, accountId, startDate);
                if (lastStatementBeforeStartDate != null) {
                    runningBalance = lastStatementBeforeStartDate.getRunningBalance();
                }

                for (AccountStatement statement : statementsToRecalculate) {
                    runningBalance += statement.getDebit() - statement.getCredit();
                    if (statement.getRunningBalance() != runningBalance) { // Only update if changed
                        statement.setRunningBalance(runningBalance);
                        dao.update(statement);
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error recalculating running balances: " + e.getMessage());
            }
        });
    }
}
