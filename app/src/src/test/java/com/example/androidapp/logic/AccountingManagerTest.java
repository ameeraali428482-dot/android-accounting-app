package com.example.androidapp.logic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.content.Context;

import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.dao.AccountDao;
import com.example.androidapp.data.dao.AccountStatementDao;
import com.example.androidapp.data.dao.InvoiceDao;
import com.example.androidapp.data.dao.InvoiceItemDao;
import com.example.androidapp.data.dao.ItemDao;
import com.example.androidapp.data.dao.JournalEntryDao;
import com.example.androidapp.data.dao.JournalEntryItemDao;
import com.example.androidapp.data.dao.InventoryDao;
import com.example.androidapp.data.dao.PaymentDao;
import com.example.androidapp.data.dao.ReceiptDao;
import com.example.androidapp.data.entities.Account;
import com.example.androidapp.data.entities.AccountStatement;
import com.example.androidapp.data.entities.Invoice;
import com.example.androidapp.data.entities.InvoiceItem;
import com.example.androidapp.data.entities.Item;
import com.example.androidapp.data.entities.JournalEntry;
import com.example.androidapp.data.entities.JournalEntryItem;
import com.example.androidapp.data.entities.Inventory;
import com.example.androidapp.data.entities.Payment;
import com.example.androidapp.data.entities.Receipt;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class AccountingManagerTest {

    private AccountingManager accountingManager;
    private AppDatabase mockAppDatabase;
    private AccountDao mockAccountDao;
    private JournalEntryDao mockJournalEntryDao;
    private JournalEntryItemDao mockJournalEntryItemDao;
    private AccountStatementDao mockAccountStatementDao;
    private InvoiceDao mockInvoiceDao;
    private InvoiceItemDao mockInvoiceItemDao;
    private ItemDao mockItemDao;
    private InventoryDao mockInventoryDao;
    private PaymentDao mockPaymentDao;
    private ReceiptDao mockReceiptDao;
    private ExecutorService mockExecutorService;
    private Context mockContext;

    @Before
    public void setUp() {
        mockContext = mock(Context.class);
        mockAppDatabase = mock(AppDatabase.class);
        mockAccountDao = mock(AccountDao.class);
        mockJournalEntryDao = mock(JournalEntryDao.class);
        mockJournalEntryItemDao = mock(JournalEntryItemDao.class);
        mockAccountStatementDao = mock(AccountStatementDao.class);
        mockInvoiceDao = mock(InvoiceDao.class);
        mockInvoiceItemDao = mock(InvoiceItemDao.class);
        mockItemDao = mock(ItemDao.class);
        mockInventoryDao = mock(InventoryDao.class);
        mockPaymentDao = mock(PaymentDao.class);
        mockReceiptDao = mock(ReceiptDao.class);

        // Mock the executor to run tasks synchronously for testing
        mockExecutorService = mock(ExecutorService.class);
        when(mockExecutorService.execute(any(Runnable.class))).thenAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                ((Runnable) invocation.getArguments()[0]).run();
                return null;
            }
        });

        when(mockAppDatabase.accountDao()).thenReturn(mockAccountDao);
        when(mockAppDatabase.journalEntryDao()).thenReturn(mockJournalEntryDao);
        when(mockAppDatabase.journalEntryItemDao()).thenReturn(mockJournalEntryItemDao);
        when(mockAppDatabase.accountStatementDao()).thenReturn(mockAccountStatementDao);
        when(mockAppDatabase.invoiceDao()).thenReturn(mockInvoiceDao);
        when(mockAppDatabase.invoiceItemDao()).thenReturn(mockInvoiceItemDao);
        when(mockAppDatabase.itemDao()).thenReturn(mockItemDao);
        when(mockAppDatabase.inventoryDao()).thenReturn(mockInventoryDao);
        when(mockAppDatabase.paymentDao()).thenReturn(mockPaymentDao);
        when(mockAppDatabase.receiptDao()).thenReturn(mockReceiptDao);

        accountingManager = new AccountingManager(mockContext);
        // Manually set the database and executor for the test instance
        // This is a workaround since the constructor calls AppDatabase.getDatabase(context)
        // which cannot be mocked directly without PowerMock or similar.
        // A better approach would be to inject AppDatabase into AccountingManager.
        try {
            java.lang.reflect.Field databaseField = AccountingManager.class.getDeclaredField("database");
            databaseField.setAccessible(true);
            databaseField.set(accountingManager, mockAppDatabase);

            java.lang.reflect.Field executorField = AppDatabase.class.getDeclaredField("databaseWriteExecutor");
            executorField.setAccessible(true);
            executorField.set(null, mockExecutorService); // It's a static field
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testCreateJournalEntriesForInvoice_cash() {
        String companyId = "comp1";
        String invoiceId = "inv1";
        String customerId = "cust1";
        String invoiceNumber = "INV001";
        float totalAmount = 100.0f;

        Invoice invoice = new Invoice(invoiceId, companyId, customerId, invoiceNumber, "2023-01-01", "2023-01-31", totalAmount, "Paid", "CASH", totalAmount);
        List<InvoiceItem> invoiceItems = new ArrayList<>();
        invoiceItems.add(new InvoiceItem("ii1", invoiceId, "item1", 1, 100.0f, 0.0f));

        Item item = new Item("item1", companyId, "Product A", "Description", 50.0f, 100.0f);
        when(mockItemDao.getItemById("item1", companyId)).thenReturn(item);
        when(mockInventoryDao.getInventoryForItem("item1", companyId)).thenReturn(Collections.singletonList(new Inventory("inv1", companyId, "item1", 10, 50.0f)));

        Account cashAccount = new Account("acc1", companyId, "Cash", "Asset", 0.0f);
        Account salesRevenueAccount = new Account("acc2", companyId, "Sales Revenue", "Revenue", 0.0f);
        Account cogsAccount = new Account("acc3", companyId, "Cost of Goods Sold", "Expense", 0.0f);
        Account inventoryAccount = new Account("acc4", companyId, "Inventory", "Asset", 0.0f);

        when(mockAccountDao.getAccountByNameAndCompanyId("Cash", companyId)).thenReturn(cashAccount);
        when(mockAccountDao.getAccountByNameAndCompanyId("Sales Revenue", companyId)).thenReturn(salesRevenueAccount);
        when(mockAccountDao.getAccountByNameAndCompanyId("Cost of Goods Sold", companyId)).thenReturn(cogsAccount);
        when(mockAccountDao.getAccountByNameAndCompanyId("Inventory", companyId)).thenReturn(inventoryAccount);

        accountingManager.createJournalEntriesForInvoice(invoice, invoiceItems);

        ArgumentCaptor<JournalEntry> journalEntryCaptor = ArgumentCaptor.forClass(JournalEntry.class);
        verify(mockJournalEntryDao, times(1)).insert(journalEntryCaptor.capture());
        JournalEntry capturedJournalEntry = journalEntryCaptor.getValue();

        assertNotNull(capturedJournalEntry);
        assertEquals(companyId, capturedJournalEntry.getCompanyId());
        assertEquals(invoiceId, capturedJournalEntry.getReferenceNumber());
        assertEquals("AUTO_INVOICE", capturedJournalEntry.getEntryType());
        assertEquals(totalAmount, capturedJournalEntry.getTotalDebit(), 0.001);
        assertEquals(totalAmount, capturedJournalEntry.getTotalCredit(), 0.001);

        ArgumentCaptor<JournalEntryItem> journalEntryItemCaptor = ArgumentCaptor.forClass(JournalEntryItem.class);
        verify(mockJournalEntryItemDao, times(4)).insert(journalEntryItemCaptor.capture());
        List<JournalEntryItem> capturedItems = journalEntryItemCaptor.getAllValues();

        // Cash (Debit)
        assertTrue(capturedItems.stream().anyMatch(item -> item.getAccountId().equals(cashAccount.getId()) && item.getDebit() == totalAmount && item.getCredit() == 0.0f));
        // Sales Revenue (Credit)
        assertTrue(capturedItems.stream().anyMatch(item -> item.getAccountId().equals(salesRevenueAccount.getId()) && item.getDebit() == 0.0f && item.getCredit() == totalAmount));
        // COGS (Debit)
        assertTrue(capturedItems.stream().anyMatch(item -> item.getAccountId().equals(cogsAccount.getId()) && item.getDebit() == item.getCostPrice() * invoiceItems.get(0).getQuantity() && item.getCredit() == 0.0f));
        // Inventory (Credit)
        assertTrue(capturedItems.stream().anyMatch(item -> item.getAccountId().equals(inventoryAccount.getId()) && item.getDebit() == 0.0f && item.getCredit() == item.getCostPrice() * invoiceItems.get(0).getQuantity()));

        verify(mockInventoryDao, times(1)).update(any(Inventory.class));
    }

    @Test
    public void testCreateJournalEntriesForInvoice_credit() {
        String companyId = "comp1";
        String invoiceId = "inv2";
        String customerId = "cust2";
        String invoiceNumber = "INV002";
        float totalAmount = 200.0f;

        Invoice invoice = new Invoice(invoiceId, companyId, customerId, invoiceNumber, "2023-01-01", "2023-01-31", totalAmount, "Pending", "CREDIT", 0.0f);
        List<InvoiceItem> invoiceItems = new ArrayList<>();
        invoiceItems.add(new InvoiceItem("ii2", invoiceId, "item2", 2, 100.0f, 0.0f));

        Item item = new Item("item2", companyId, "Product B", "Description", 75.0f, 100.0f);
        when(mockItemDao.getItemById("item2", companyId)).thenReturn(item);
        when(mockInventoryDao.getInventoryForItem("item2", companyId)).thenReturn(Collections.singletonList(new Inventory("inv2", companyId, "item2", 10, 75.0f)));

        Account accountsReceivableAccount = new Account("acc5", companyId, "Accounts Receivable", "Asset", 0.0f);
        Account salesRevenueAccount = new Account("acc6", companyId, "Sales Revenue", "Revenue", 0.0f);
        Account cogsAccount = new Account("acc7", companyId, "Cost of Goods Sold", "Expense", 0.0f);
        Account inventoryAccount = new Account("acc8", companyId, "Inventory", "Asset", 0.0f);

        when(mockAccountDao.getAccountByNameAndCompanyId("Accounts Receivable", companyId)).thenReturn(accountsReceivableAccount);
        when(mockAccountDao.getAccountByNameAndCompanyId("Sales Revenue", companyId)).thenReturn(salesRevenueAccount);
        when(mockAccountDao.getAccountByNameAndCompanyId("Cost of Goods Sold", companyId)).thenReturn(cogsAccount);
        when(mockAccountDao.getAccountByNameAndCompanyId("Inventory", companyId)).thenReturn(inventoryAccount);

        accountingManager.createJournalEntriesForInvoice(invoice, invoiceItems);

        ArgumentCaptor<JournalEntry> journalEntryCaptor = ArgumentCaptor.forClass(JournalEntry.class);
        verify(mockJournalEntryDao, times(1)).insert(journalEntryCaptor.capture());
        JournalEntry capturedJournalEntry = journalEntryCaptor.getValue();

        assertNotNull(capturedJournalEntry);
        assertEquals(companyId, capturedJournalEntry.getCompanyId());
        assertEquals(invoiceId, capturedJournalEntry.getReferenceNumber());
        assertEquals("AUTO_INVOICE", capturedJournalEntry.getEntryType());
        assertEquals(totalAmount, capturedJournalEntry.getTotalDebit(), 0.001);
        assertEquals(totalAmount, capturedJournalEntry.getTotalCredit(), 0.001);

        ArgumentCaptor<JournalEntryItem> journalEntryItemCaptor = ArgumentCaptor.forClass(JournalEntryItem.class);
        verify(mockJournalEntryItemDao, times(4)).insert(journalEntryItemCaptor.capture());
        List<JournalEntryItem> capturedItems = journalEntryItemCaptor.getAllValues();

        // Accounts Receivable (Debit)
        assertTrue(capturedItems.stream().anyMatch(item -> item.getAccountId().equals(accountsReceivableAccount.getId()) && item.getDebit() == totalAmount && item.getCredit() == 0.0f));
        // Sales Revenue (Credit)
        assertTrue(capturedItems.stream().anyMatch(item -> item.getAccountId().equals(salesRevenueAccount.getId()) && item.getDebit() == 0.0f && item.getCredit() == totalAmount));
        // COGS (Debit)
        assertTrue(capturedItems.stream().anyMatch(item -> item.getAccountId().equals(cogsAccount.getId()) && item.getDebit() == item.getCostPrice() * invoiceItems.get(0).getQuantity() && item.getCredit() == 0.0f));
        // Inventory (Credit)
        assertTrue(capturedItems.stream().anyMatch(item -> item.getAccountId().equals(inventoryAccount.getId()) && item.getDebit() == 0.0f && item.getCredit() == item.getCostPrice() * invoiceItems.get(0).getQuantity()));

        verify(mockInventoryDao, times(1)).update(any(Inventory.class));
    }

    @Test
    public void testValidateJournalEntryBalance_balanced() {
        String journalEntryId = "je1";
        List<JournalEntryItem> items = Arrays.asList(
                new JournalEntryItem(journalEntryId, "acc1", 100.0f, 0.0f, ""),
                new JournalEntryItem(journalEntryId, "acc2", 0.0f, 100.0f, "")
        );
        when(mockJournalEntryItemDao.getJournalEntryItemsForJournalEntry(journalEntryId)).thenReturn(items);

        assertTrue(accountingManager.validateJournalEntryBalance(journalEntryId));
    }

    @Test
    public void testValidateJournalEntryBalance_unbalanced() {
        String journalEntryId = "je2";
        List<JournalEntryItem> items = Arrays.asList(
                new JournalEntryItem(journalEntryId, "acc1", 100.0f, 0.0f, ""),
                new JournalEntryItem(journalEntryId, "acc2", 0.0f, 90.0f, "")
        );
        when(mockJournalEntryItemDao.getJournalEntryItemsForJournalEntry(journalEntryId)).thenReturn(items);

        assertFalse(accountingManager.validateJournalEntryBalance(journalEntryId));
    }

    @Test
    public void testIsInvoiceNumberUnique_unique() {
        String invoiceNumber = "INV003";
        String companyId = "comp1";
        when(mockInvoiceDao.countInvoiceByNumber(invoiceNumber, companyId)).thenReturn(0);

        assertTrue(accountingManager.isInvoiceNumberUnique(invoiceNumber, companyId));
    }

    @Test
    public void testIsInvoiceNumberUnique_duplicate() {
        String invoiceNumber = "INV001";
        String companyId = "comp1";
        when(mockInvoiceDao.countInvoiceByNumber(invoiceNumber, companyId)).thenReturn(1);

        assertFalse(accountingManager.isInvoiceNumberUnique(invoiceNumber, companyId));
    }

    @Test
    public void testIsReferenceNumberUnique_payment_unique() {
        String refNumber = "PAY001";
        String companyId = "comp1";
        when(mockPaymentDao.countPaymentByReferenceNumber(refNumber, companyId)).thenReturn(0);

        assertTrue(accountingManager.isReferenceNumberUnique(refNumber, companyId, "PAYMENT"));
    }

    @Test
    public void testIsReferenceNumberUnique_payment_duplicate() {
        String refNumber = "PAY001";
        String companyId = "comp1";
        when(mockPaymentDao.countPaymentByReferenceNumber(refNumber, companyId)).thenReturn(1);

        assertFalse(accountingManager.isReferenceNumberUnique(refNumber, companyId, "PAYMENT"));
    }

    @Test
    public void testIsReferenceNumberUnique_receipt_unique() {
        String refNumber = "REC001";
        String companyId = "comp1";
        when(mockReceiptDao.countReceiptByReferenceNumber(refNumber, companyId)).thenReturn(0);

        assertTrue(accountingManager.isReferenceNumberUnique(refNumber, companyId, "RECEIPT"));
    }

    @Test
    public void testIsReferenceNumberUnique_receipt_duplicate() {
        String refNumber = "REC001";
        String companyId = "comp1";
        when(mockReceiptDao.countReceiptByReferenceNumber(refNumber, companyId)).thenReturn(1);

        assertFalse(accountingManager.isReferenceNumberUnique(refNumber, companyId, "RECEIPT"));
    }

    @Test
    public void testIsReferenceNumberUnique_journal_unique() {
        String refNumber = "JE001";
        String companyId = "comp1";
        when(mockJournalEntryDao.countJournalEntryByReferenceNumber(refNumber, companyId)).thenReturn(0);

        assertTrue(accountingManager.isReferenceNumberUnique(refNumber, companyId, "JOURNAL"));
    }

    @Test
    public void testIsReferenceNumberUnique_journal_duplicate() {
        String refNumber = "JE001";
        String companyId = "comp1";
        when(mockJournalEntryDao.countJournalEntryByReferenceNumber(refNumber, companyId)).thenReturn(1);

        assertFalse(accountingManager.isReferenceNumberUnique(refNumber, companyId, "JOURNAL"));
    }

    @Test
    public void testCalculateAndSaveAccountStatement() {
        String companyId = "comp1";
        String accountId = "acc1";

        // Mock journal entry items for the account
        List<JournalEntryItem> journalEntryItems = Arrays.asList(
                new JournalEntryItem("jei1", accountId, 100.0f, 0.0f, ""), // Debit 100
                new JournalEntryItem("jei2", accountId, 0.0f, 50.0f, ""),  // Credit 50
                new JournalEntryItem("jei3", accountId, 200.0f, 0.0f, "")  // Debit 200
        );
        when(mockJournalEntryItemDao.getJournalEntryItemsForAccount(accountId)).thenReturn(journalEntryItems);

        accountingManager.calculateAndSaveAccountStatement(companyId, accountId);

        ArgumentCaptor<AccountStatement> accountStatementCaptor = ArgumentCaptor.forClass(AccountStatement.class);
        verify(mockAccountStatementDao, times(1)).insert(accountStatementCaptor.capture());
        AccountStatement capturedStatement = accountStatementCaptor.getValue();

        assertNotNull(capturedStatement);
        assertEquals(accountId, capturedStatement.getAccountId());
        assertEquals(companyId, capturedStatement.getCompanyId());
        assertEquals(250.0f, capturedStatement.getRunningBalance(), 0.001f); // 100 - 50 + 200 = 250
    }

    // Helper method to create a mock Account
    private Account createMockAccount(String id, String companyId, String name, String type, float balance) {
        Account account = new Account(id, companyId, name, type, balance);
        account.setId(id);
        return account;
    }
}

