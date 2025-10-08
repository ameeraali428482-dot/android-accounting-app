package com.example.androidapp.data.dao;
import com.example.androidapp.data.entities.Account;
import com.example.androidapp.data.entities.Item;
import com.example.androidapp.data.entities.InvoiceItem;
import com.example.androidapp.data.entities.Employee;
import com.example.androidapp.data.entities.Voucher;
import com.example.androidapp.data.entities.Company;
import com.example.androidapp.data.entities.Doctor;
import com.example.androidapp.data.entities.User;
import com.example.androidapp.data.entities.Supplier;
import com.example.androidapp.data.entities.Customer;
import com.example.androidapp.data.entities.Trophy;
import com.example.androidapp.data.entities.Order;
import com.example.androidapp.data.entities.Repair;
import com.example.androidapp.data.entities.Chat;
import com.example.androidapp.data.entities.UserReward;
import com.example.androidapp.data.entities.Reward;
import com.example.androidapp.data.entities.PointTransaction;
import com.example.androidapp.data.entities.Campaign;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;

import com.example.androidapp.data.entities.JournalEntryItem;

import java.util.List;

@Dao
public interface JournalEntryItemDao {
    @Insert
    void insert(JournalEntryItem journalEntryItem);

    @Update
    void update(JournalEntryItem journalEntryItem);

    @Delete
    void delete(JournalEntryItem journalEntryItem);

    @Query("SELECT * FROM journal_entry_items WHERE journalEntryId = :journalEntryId")
    List<JournalEntryItem> getJournalEntryItemsForJournalEntry(String journalEntryId);

    @Query("SELECT * FROM journal_entry_items WHERE id = :id LIMIT 1")
    JournalEntryItem getJournalEntryItemById(int id);

    @Query("SELECT COUNT(*) FROM journal_entry_items WHERE journalEntryId = :journalEntryId")
    int countJournalEntryItemsForJournalEntry(String journalEntryId);
}


    @Query("SELECT SUM(CASE WHEN A.isDebit = 1 THEN JEI.debit ELSE JEI.credit END) FROM journal_entry_items JEI INNER JOIN accounts A ON JEI.accountId = A.id INNER JOIN journal_entries JE ON JEI.journalEntryId = JE.id WHERE A.name = :accountName AND JE.companyId = :companyId AND JE.entryDate BETWEEN :startDate AND :endDate")
    float getTotalAmountForAccountTypeAndDateRange(String accountName, String companyId, String startDate, String endDate);

