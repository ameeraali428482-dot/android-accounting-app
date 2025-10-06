package com.example.androidapp.logic;

import android.content.Context;
import android.util.Log;

import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.dao.*;
import com.example.androidapp.data.entities.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class AccountingManager {
    private static final String TAG = "AccountingManager";
    private final AppDatabase database;

    public AccountingManager(Context context) {
        this.database = AppDatabase.getDatabase(context);
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
                database.journalEntryDao().insert(journalEntry);

                // Debit Accounts Receivable or Cash
                Account debitAccount = null;
                if ("CASH".equals(invoice.getInvoiceType())) {
                    debitAccount = database.accountDao().getAccountByNameAndCompanyId("Cash", invoice.getCompanyId());
                } else { // CREDIT or CASH_CREDIT
                    debitAccount = database.accountDao().getAccountByNameAndCompanyId("Accounts Receivable", invoice.getCompanyId());
                }
                if (debitAccount != null) {
                    database.journalEntryItemDao().insert(new JournalEntryItem(
                        journalEntryId, 
                        debitAccount.getId(), 
                        totalAmount, 
                        0.0f, 
                        "Accounts Receivable/Cash for Invoice " + invoice.getInvoiceNumber()
                    ));
                }

                // Credit Sales Revenue
                Account salesRevenueAccount = database.accountDao().getAccountByNameAndCompanyId("Sales Revenue", invoice.getCompanyId());
                if (salesRevenueAccount != null) {
                    database.journalEntryItemDao().insert(new JournalEntryItem(
                        journalEntryId, 
                        salesRevenueAccount.getId(), 
                        0.0f, 
                        totalAmount, 
                        "Sales Revenue for Invoice " + invoice.getInvoiceNumber()
                    ));
                }

                // Create Cost of Goods Sold entries and update inventory
                createCostOfGoodsSoldEntries(journalEntryId, invoiceItems, invoice.getInvoiceNumber(), invoice.getCompanyId());

                // Update the main JournalEntry with final calculated totals (if necessary, though for simple sales, totalAmount should balance)
                // For more complex scenarios (e.g., taxes, discounts), these totals would need recalculation
                journalEntry.setTotalDebit(totalAmount);
                journalEntry.setTotalCredit(totalAmount);
                database.journalEntryDao().update(journalEntry);

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
                Item item = database.itemDao().getItemById(invoiceItem.getItemId(), companyId);
                if (item != null) {
                    float itemCost = item.getCostPrice() * invoiceItem.getQuantity();
                    totalCost += itemCost;
                    updateInventoryForSale(invoiceItem, companyId);
                }
            }

            if (totalCost > 0) {
                // Debit Cost of Goods Sold
                Account cogsAccount = database.accountDao().getAccountByNameAndCompanyId("Cost of Goods Sold", companyId);
                if (cogsAccount != null) {
                    database.journalEntryItemDao().insert(new JournalEntryItem(
                        journalEntryId, 
                        cogsAccount.getId(), 
                        totalCost, 
                        0.0f, 
                        "COGS for Invoice " + invoiceNumber
                    ));
                }

                // Credit Inventory
                Account inventoryAccount = database.accountDao().getAccountByNameAndCompanyId("Inventory", companyId);
                if (inventoryAccount != null) {
                    database.journalEntryItemDao().insert(new JournalEntryItem(
                        journalEntryId, 
                        inventoryAccount.getId(), 
                        0.0f, 
                        totalCost, 
                        "Inventory for Invoice " + invoiceNumber
                    ));
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
            List<Inventory> inventoryList = database.inventoryDao().getInventoryForItem(invoiceItem.getItemId(), companyId);
            float remainingQty = invoiceItem.getQuantity();

            for (Inventory inventory : inventoryList) {
                if (remainingQty <= 0) break;

                float availableQty = inventory.getQuantity();
                if (availableQty > 0) {
                    float qtyToReduce = Math.min(remainingQty, availableQty);
                    inventory.setQuantity(availableQty - qtyToReduce);
                    database.inventoryDao().update(inventory);
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
                database.journalEntryDao().insert(journalEntry);

                // Debit Cash/Bank account
                Account cashAccount = database.accountDao().getAccountByNameAndCompanyId(getAccountNameForPaymentMethod(payment.getPaymentMethod()), payment.getCompanyId());
                if (cashAccount != null) {
                    database.journalEntryItemDao().insert(new JournalEntryItem(
                        journalEntryId, 
                        cashAccount.getId(), 
                        payment.getAmount(), 
                        0.0f, 
                        "Cash/Bank received for payment " + payment.getReferenceNumber()
                    ));
                }

                // Credit Accounts Receivable (assuming payment is against an invoice)
                Account accountsReceivableAccount = database.accountDao().getAccountByNameAndCompanyId("Accounts Receivable", payment.getCompanyId());
                if (accountsReceivableAccount != null) {
                    database.journalEntryItemDao().insert(new JournalEntryItem(
                        journalEntryId, 
                        accountsReceivableAccount.getId(), 
                        0.0f, 
                        payment.getAmount(), 
                        "Payment received against Accounts Receivable for " + payment.getReferenceNumber()
                    ));
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
                database.journalEntryDao().insert(journalEntry);

                // Debit Cash/Bank account
                Account cashAccount = database.accountDao().getAccountByNameAndCompanyId(getAccountNameForPaymentMethod(receipt.getPaymentMethod()), receipt.getCompanyId());
                if (cashAccount != null) {
                    database.journalEntryItemDao().insert(new JournalEntryItem(
                        journalEntryId, 
                        cashAccount.getId(), 
                        receipt.getAmount(), 
                        0.0f, 
                        "Cash/Bank received for receipt " + receipt.getReferenceNumber()
                    ));
                }

                // Credit a specific revenue account (e.g., Other Income, Service Revenue)
                Account revenueAccount = database.accountDao().getAccountByNameAndCompanyId("Other Income", receipt.getCompanyId()); // Example
                if (revenueAccount != null) {
                    database.journalEntryItemDao().insert(new JournalEntryItem(
                        journalEntryId, 
                        revenueAccount.getId(), 
                        0.0f, 
                        receipt.getAmount(), 
                        "Revenue from receipt " + receipt.getReferenceNumber()
                    ));
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
                return "Bank";
            case "Credit Card":
            case "بطاقة ائتمان":
                return "Credit Card Payable"; // Or a specific bank account for credit card settlements
            case "Check":
            case "شيك":
                return "Bank";
            default:
                return "Cash"; // Default to Cash if method is unknown
        }
    }

    /**
     * Validates if a journal entry is balanced (total debits equal total credits).
     *
     * @param journalEntryId The ID of the journal entry to validate.
     * @return True if balanced, false otherwise.
     */
    public boolean validateJournalEntryBalance(String journalEntryId) {
        try {
            List<JournalEntryItem> items = database.journalEntryItemDao().getJournalEntryItemsForJournalEntry(journalEntryId);
            float totalDebit = 0;
            float totalCredit = 0;

            for (JournalEntryItem item : items) {
                totalDebit += item.getDebit();
                totalCredit += item.getCredit();
            }
            return Math.abs(totalDebit - totalCredit) < 0.01; // Allow for small rounding differences
        } catch (Exception e) {
            Log.e(TAG, "Error validating journal entry balance: " + e.getMessage());
            return false;
        }
    }

    /**
     * Checks for duplicate invoice numbers within a company.
     *
     * @param invoiceNumber The invoice number to check.
     * @param companyId The ID of the company.
     * @return True if unique, false otherwise.
     */
    public boolean isInvoiceNumberUnique(String invoiceNumber, String companyId) {
        try {
            return database.invoiceDao().countInvoiceByNumber(invoiceNumber, companyId) == 0;
        } catch (Exception e) {
            Log.e(TAG, "Error checking invoice number uniqueness: " + e.getMessage());
            return false;
        }
    }

    /**
     * Checks for duplicate reference numbers for payments, receipts, or journal entries within a company.
     *
     * @param referenceNumber The reference number to check.
     * @param companyId The ID of the company.
     * @param type The type of document (e.g., "PAYMENT", "RECEIPT", "JOURNAL").
     * @return True if unique, false otherwise.
     */
    public void calculateAndSaveAccountStatement(AccountStatement newStatement) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            try {
                // Get all existing statements for the account, ordered by date
                List<AccountStatement> existingStatements = database.accountStatementDao().getAccountStatementsForBalanceCalculation(
                        newStatement.getCompanyId(), newStatement.getAccountId());

                float currentRunningBalance = 0.0f;
                // If there are existing statements, find the running balance just before the new statement's date
                if (!existingStatements.isEmpty()) {
                    for (AccountStatement statement : existingStatements) {
                        if (statement.getTransactionDate().compareTo(newStatement.getTransactionDate()) < 0) {
                            currentRunningBalance = statement.getRunningBalance();
                        } else {
                            break; // Statements are ordered, so we found the point
                        }
                    }
                }

                // Calculate the running balance for the new statement
                newStatement.setRunningBalance(currentRunningBalance + newStatement.getDebit() - newStatement.getCredit());

                // Insert the new statement
                database.accountStatementDao().insert(newStatement);

                // Recalculate and update running balances for all subsequent statements
                recalculateRunningBalances(newStatement.getCompanyId(), newStatement.getAccountId(), newStatement.getTransactionDate());

                Log.d(TAG, "Account statement added/updated and balances recalculated for account: " + newStatement.getAccountId());
            } catch (Exception e) {
                Log.e(TAG, "Error calculating and saving account statement: " + e.getMessage());
            }
        });
    }

    public void recalculateRunningBalances(String companyId, String accountId, String startDate) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            try {
                List<AccountStatement> statementsToRecalculate = database.accountStatementDao().getAccountStatementsForBalanceCalculation(companyId, accountId);
                float runningBalance = 0.0f;

                // Find the starting balance if recalculating from a specific date
                for (AccountStatement statement : statementsToRecalculate) {
                    if (statement.getTransactionDate().compareTo(startDate) < 0) {
                        runningBalance = statement.getRunningBalance();
                    } else {
                        break;
                    }
                }

                for (AccountStatement statement : statementsToRecalculate) {
                    if (statement.getTransactionDate().compareTo(startDate) >= 0) {
                        runningBalance += statement.getDebit() - statement.getCredit();
                        if (statement.getRunningBalance() != runningBalance) { // Only update if changed
                            statement.setRunningBalance(runningBalance);
                            database.accountStatementDao().update(statement);
                        }
                    }
                }
                Log.d(TAG, "Running balances recalculated for account: " + accountId + " from date: " + startDate);
            } catch (Exception e) {
                Log.e(TAG, "Error recalculating running balances: " + e.getMessage());
            }
        });
    }

    public boolean isReferenceNumberUnique(String referenceNumber, String companyId, String type) {
        try {
            switch (type) {
                case "PAYMENT":
                    return database.paymentDao().countPaymentByReferenceNumber(referenceNumber, companyId) == 0;
                case "RECEIPT":
                    return database.receiptDao().countReceiptByReferenceNumber(referenceNumber, companyId) == 0;
                case "JOURNAL":
                    return database.journalEntryDao().countJournalEntriesByReferenceNumber(referenceNumber, companyId) == 0;
                default:
                    return false;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error checking reference number uniqueness: " + e.getMessage());
            return false;
        }
    }

    /**
     * Gets the last purchase price for an item from a specific supplier.
     * This would typically involve querying purchase orders or supplier invoices.
     *
     * @param itemId The ID of the item.
     * @param supplierId The ID of the supplier.
     * @param companyId The ID of the company.
     * @return The last purchase price, or 0 if not found.
     */
    public float getLastPurchasePrice(String itemId, String supplierId, String companyId) {
        // This would require a purchase invoice/order system similar to sales invoices
        // For now, return the item's cost price as a placeholder
        try {
            Item item = database.itemDao().getItemById(itemId, companyId);
            return item != null ? item.getCostPrice() : 0;
        } catch (Exception e) {
            Log.e(TAG, "Error getting last purchase price: " + e.getMessage());
            return 0;
        }
    }
}

