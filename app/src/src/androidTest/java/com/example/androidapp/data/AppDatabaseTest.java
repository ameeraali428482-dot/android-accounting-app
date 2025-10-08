package com.example.androidapp.data;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.androidapp.data.dao.AccountDao;
import com.example.androidapp.data.dao.AccountStatementDao;
import com.example.androidapp.data.dao.InvoiceDao;
import com.example.androidapp.data.dao.InvoiceItemDao;
import com.example.androidapp.data.dao.ItemDao;
import com.example.androidapp.data.dao.JournalEntryDao;
import com.example.androidapp.data.dao.JournalEntryItemDao;
import com.example.androidapp.data.dao.PaymentDao;
import com.example.androidapp.data.dao.ReceiptDao;
import com.example.androidapp.data.entities.Account;
import com.example.androidapp.data.entities.AccountStatement;
import com.example.androidapp.data.entities.Invoice;
import com.example.androidapp.data.entities.InvoiceItem;
import com.example.androidapp.data.entities.Item;
import com.example.androidapp.data.entities.JournalEntry;
import com.example.androidapp.data.entities.JournalEntryItem;
import com.example.androidapp.data.entities.Payment;
import com.example.androidapp.data.entities.Receipt;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class AppDatabaseTest {
    private AppDatabase db;
    private AccountDao accountDao;
    private AccountStatementDao accountStatementDao;
    private JournalEntryDao journalEntryDao;
    private JournalEntryItemDao journalEntryItemDao;
    private InvoiceDao invoiceDao;
    private InvoiceItemDao invoiceItemDao;
    private ItemDao itemDao;
    private PaymentDao paymentDao;
    private ReceiptDao receiptDao;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                .allowMainThreadQueries()
                .build();
        accountDao = db.accountDao();
        accountStatementDao = db.accountStatementDao();
        journalEntryDao = db.journalEntryDao();
        journalEntryItemDao = db.journalEntryItemDao();
        invoiceDao = db.invoiceDao();
        invoiceItemDao = db.invoiceItemDao();
        itemDao = db.itemDao();
        paymentDao = db.paymentDao();
        receiptDao = db.receiptDao();
    }

    @After
    public void closeDb() throws IOException {
        db.close();
    }

    @Test
    public void writeAccountAndReadInList() throws Exception {
        Account account = new Account("company1", "Cash", "Asset", 100.0);
        accountDao.insert(account);
        List<Account> byCompany = accountDao.getAccountsByCompanyId("company1");
        assertEquals(1, byCompany.size());
        assertEquals("Cash", byCompany.get(0).name);
    }

    @Test
    public void writeJournalEntryAndItems() throws Exception {
        JournalEntry journalEntry = new JournalEntry("company1", "Test Entry", new Date(), "Type", "cust1", null);
        long journalEntryId = journalEntryDao.insert(journalEntry);

        JournalEntryItem item1 = new JournalEntryItem(journalEntryId, 1L, 50.0, 0.0);
        JournalEntryItem item2 = new JournalEntryItem(journalEntryId, 2L, 0.0, 50.0);
        journalEntryItemDao.insertAll(Arrays.asList(item1, item2));

        List<JournalEntryItem> items = journalEntryItemDao.getJournalEntryItemsForJournalEntry(journalEntryId);
        assertEquals(2, items.size());
        assertEquals(50.0, items.get(0).debit, 0.001);
    }

    @Test
    public void writeAccountStatement() throws Exception {
        AccountStatement statement = new AccountStatement("company1", 1L, new Date(), 100.0);
        accountStatementDao.insert(statement);
        List<AccountStatement> statements = accountStatementDao.getAccountStatementsByAccountId(1L);
        assertEquals(1, statements.size());
        assertEquals(100.0, statements.get(0).runningBalance, 0.001);
    }

    @Test
    public void writeInvoiceAndItems() throws Exception {
        Invoice invoice = new Invoice("company1", "cust1", new Date(), 150.0, 10.0, 160.0, false);
        long invoiceId = invoiceDao.insert(invoice);

        InvoiceItem invoiceItem = new InvoiceItem(invoiceId, 1L, 2, 75.0, 150.0);
        invoiceItemDao.insert(invoiceItem);

        List<InvoiceItem> items = invoiceItemDao.getInvoiceItemsForInvoice(invoiceId);
        assertEquals(1, items.size());
        assertEquals(150.0, items.get(0).total, 0.001);
    }

    @Test
    public void writeItem() throws Exception {
        Item item = new Item("company1", "Product A", "Description A", 10.0, 15.0);
        itemDao.insert(item);
        List<Item> items = itemDao.getAllItems("company1");
        assertEquals(1, items.size());
        assertEquals("Product A", items.get(0).name);
    }

    @Test
    public void writePayment() throws Exception {
        Payment payment = new Payment("company1", "cust1", new Date(), 100.0, "Cash");
        paymentDao.insert(payment);
        List<Payment> payments = paymentDao.getPaymentsByCompanyId("company1");
        assertEquals(1, payments.size());
        assertEquals(100.0, payments.get(0).amount, 0.001);
    }

    @Test
    public void writeReceipt() throws Exception {
        Receipt receipt = new Receipt("company1", "cust1", new Date(), 200.0, "Bank");
        receiptDao.insert(receipt);
        List<Receipt> receipts = receiptDao.getReceiptsByCompanyId("company1");
        assertEquals(1, receipts.size());
        assertEquals(200.0, receipts.get(0).amount, 0.001);
    }
}

