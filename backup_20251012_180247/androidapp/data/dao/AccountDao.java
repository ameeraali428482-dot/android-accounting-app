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
import java.util.List;





@Dao
public interface AccountDao {
    @Insert
    void insert(Account account);

    @Update
    void update(Account account);

    @Delete
    void delete(Account account);

    @Query("SELECT * FROM accounts WHERE companyId = :companyId")
    List<Account> getAllAccounts(String companyId);

    @Query("SELECT * FROM accounts WHERE id = :id AND companyId = :companyId LIMIT 1")
    Account getAccountById(String id, String companyId);

    @Query("SELECT * FROM accounts WHERE name = :accountName AND companyId = :companyId LIMIT 1")
    Account getAccountByNameAndCompanyId(String accountName, String companyId);

    @Query("SELECT COUNT(*) FROM accounts WHERE name = :accountName AND companyId = :companyId")
    int countAccountByNameAndCompanyId(String accountName, String companyId);
}
