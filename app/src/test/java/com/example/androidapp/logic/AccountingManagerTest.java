package com.example.androidapp.logic;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.content.Context;

import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.dao.AccountDao;
import com.example.androidapp.data.dao.AccountStatementDao;
import com.example.androidapp.data.dao.JournalEntryDao;
import com.example.androidapp.data.dao.JournalEntryItemDao;
import com.example.androidapp.data.entities.Account;
import com.example.androidapp.data.entities.AccountStatement;
import com.example.androidapp.data.entities.JournalEntry;
import com.example.androidapp.data.entities.JournalEntryItem;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.Arrays;
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
        mockExecutorService = mock(ExecutorService.class);

        when(mockAppDatabase.accountDao()).thenReturn(mockAccountDao);
        when(mockAppDatabase.journalEntryDao()).thenReturn(mockJournalEntryDao);
        when(mockAppDatabase.journalEntryItemDao()).thenReturn(mockJournalEntryItemDao);
        when(mockAppDatabase.accountStatementDao()).thenReturn(mockAccountStatementDao);

        accountingManager = new AccountingManager(mockContext, mockAppDatabase, mockExecutorService);
    }

    @Test
    public void testCreateJournalEntry_receipt() {
        String companyId = "comp1";
        String description = "Cash Receipt";
        Date date = new Date();
        double amount = 100.0;
        String customerId = "cust1";

        Account cashAccount = new Account(companyId, "Cash", "Asset", 0.0);
        cashAccount.id = 1L;
        Account salesAccount = new Account(companyId, "Sales Revenue", "Revenue", 0.0);
        salesAccount.id = 2L;

        when(mockAccountDao.getAccountByName(companyId, "Cash")).thenReturn(cashAccount);
        when(mockAccountDao.getAccountByName(companyId, "Sales Revenue")).thenReturn(salesAccount);

        accountingManager.createJournalEntry(companyId, description, date, amount, "Receipt", customerId, null);

        ArgumentCaptor<JournalEntry> journalEntryCaptor = ArgumentCaptor.forClass(JournalEntry.class);
        verify(mockJournalEntryDao).insert(journalEntryCaptor.capture());
        JournalEntry capturedJournalEntry = journalEntryCaptor.getValue();

        assertEquals(companyId, capturedJournalEntry.companyId);
        assertEquals(description, capturedJournalEntry.description);
        assertEquals(date, capturedJournalEntry.date);
        assertEquals("Receipt", capturedJournalEntry.type);
        assertEquals(customerId, capturedJournalEntry.customerId);

        ArgumentCaptor<List<JournalEntryItem>> journalEntryItemsCaptor = ArgumentCaptor.forClass(List.class);
        verify(mockJournalEntryItemDao).insertAll(journalEntryItemsCaptor.capture());
        List<JournalEntryItem> capturedItems = journalEntryItemsCaptor.getValue();

        assertEquals(2, capturedItems.size());

        JournalEntryItem item1 = capturedItems.get(0);
        assertEquals(capturedJournalEntry.id, item1.journalEntryId);
        assertEquals(cashAccount.id, item1.accountId);
        assertEquals(amount, item1.debit, 0.001);
        assertEquals(0.0, item1.credit, 0.001);

        JournalEntryItem item2 = capturedItems.get(1);
        assertEquals(capturedJournalEntry.id, item2.journalEntryId);
        assertEquals(salesAccount.id, item2.accountId);
        assertEquals(0.0, item2.debit, 0.001);
        assertEquals(amount, item2.credit, 0.001);
    }

    @Test
    public void testCreateJournalEntry_payment() {
        String companyId = "comp1";
        String description = "Cash Payment";
        Date date = new Date();
        double amount = 50.0;
        String supplierId = "supp1";

        Account cashAccount = new Account(companyId, "Cash", "Asset", 0.0);
        cashAccount.id = 1L;
        Account expensesAccount = new Account(companyId, "Operating Expenses", "Expense", 0.0);
        expensesAccount.id = 3L;

        when(mockAccountDao.getAccountByName(companyId, "Cash")).thenReturn(cashAccount);
        when(mockAccountDao.getAccountByName(companyId, "Operating Expenses")).thenReturn(expensesAccount);

        accountingManager.createJournalEntry(companyId, description, date, amount, "Payment", null, supplierId);

        ArgumentCaptor<JournalEntry> journalEntryCaptor = ArgumentCaptor.forClass(JournalEntry.class);
        verify(mockJournalEntryDao).insert(journalEntryCaptor.capture());
        JournalEntry capturedJournalEntry = journalEntryCaptor.getValue();

        assertEquals(companyId, capturedJournalEntry.companyId);
        assertEquals(description, capturedJournalEntry.description);
        assertEquals(date, capturedJournalEntry.date);
        assertEquals("Payment", capturedJournalEntry.type);
        assertEquals(supplierId, capturedJournalEntry.supplierId);

        ArgumentCaptor<List<JournalEntryItem>> journalEntryItemsCaptor = ArgumentCaptor.forClass(List.class);
        verify(mockJournalEntryItemDao).insertAll(journalEntryItemsCaptor.capture());
        List<JournalEntryItem> capturedItems = journalEntryItemsCaptor.getValue();

        assertEquals(2, capturedItems.size());

        JournalEntryItem item1 = capturedItems.get(0);
        assertEquals(capturedJournalEntry.id, item1.journalEntryId);
        assertEquals(expensesAccount.id, item1.accountId);
        assertEquals(amount, item1.debit, 0.001);
        assertEquals(0.0, item1.credit, 0.001);

        JournalEntryItem item2 = capturedItems.get(1);
        assertEquals(capturedJournalEntry.id, item2.journalEntryId);
        assertEquals(cashAccount.id, item2.accountId);
        assertEquals(0.0, item2.debit, 0.001);
        assertEquals(amount, item2.credit, 0.001);
    }

    @Test
    public void testCalculateAndSaveAccountStatement() {
        String companyId = "comp1";
        long accountId = 1L;
        Account account = new Account(companyId, "Cash", "Asset", 0.0);
        account.id = accountId;

        JournalEntry journalEntry1 = new JournalEntry(companyId, "Entry 1", new Date(), "Receipt", null, null);
        journalEntry1.id = 10L;
        JournalEntry journalEntry2 = new JournalEntry(companyId, "Entry 2", new Date(), "Payment", null, null);
        journalEntry2.id = 11L;

        JournalEntryItem item1 = new JournalEntryItem(journalEntry1.id, accountId, 100.0, 0.0);
        JournalEntryItem item2 = new JournalEntryItem(journalEntry2.id, accountId, 0.0, 50.0);

        when(mockAccountDao.getAccountById(accountId)).thenReturn(account);
        when(mockJournalEntryItemDao.getJournalEntryItemsForAccount(accountId)).thenReturn(Arrays.asList(item1, item2));

        accountingManager.calculateAndSaveAccountStatement(companyId, accountId);

        ArgumentCaptor<AccountStatement> accountStatementCaptor = ArgumentCaptor.forClass(AccountStatement.class);
        verify(mockAccountStatementDao).insert(accountStatementCaptor.capture());
        AccountStatement capturedStatement = accountStatementCaptor.getValue();

        assertEquals(companyId, capturedStatement.companyId);
        assertEquals(accountId, capturedStatement.accountId);
        assertEquals(50.0, capturedStatement.runningBalance, 0.001);
    }
}

