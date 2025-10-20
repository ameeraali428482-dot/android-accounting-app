package com.example.androidapp.data.entities;

import androidx.annotation.NonNull;
import androidx.room.*;

@Entity(tableName = "companies",
        foreignKeys = {
                @ForeignKey(entity = Account.class,
                           parentColumns = "id",
                           childColumns = "default_cash_account_id",
                           onDelete = ForeignKey.SET_NULL),
                @ForeignKey(entity = Account.class,
                           parentColumns = "id",
                           childColumns = "default_exchange_diff_account_id",
                           onDelete = ForeignKey.SET_NULL),
                @ForeignKey(entity = Account.class,
                           parentColumns = "id",
                           childColumns = "default_payroll_expense_account_id",
                           onDelete = ForeignKey.SET_NULL),
                @ForeignKey(entity = Account.class,
                           parentColumns = "id",
                           childColumns = "default_salaries_payable_account_id",
                           onDelete = ForeignKey.SET_NULL)
        },
        indices = {
                @Index(value = "default_cash_account_id"),
                @Index(value = "default_exchange_diff_account_id"),
                @Index(value = "default_payroll_expense_account_id"),
                @Index(value = "default_salaries_payable_account_id")
        })
public class Company {
    @PrimaryKey
    @NonNull
    private String id;
    
    private String name;
    private String address;
    private String phone;
    private String email;
    
    @ColumnInfo(name = "created_at")
    private long createdAt;
    
    @ColumnInfo(name = "updated_at")
    private long updatedAt;
    
    @ColumnInfo(name = "default_cash_account_id")
    private Long defaultCashAccountId; // تغيير إلى Long للسماح بـ null
    
    @ColumnInfo(name = "default_exchange_diff_account_id")
    private Long defaultExchangeDiffAccountId;
    
    @ColumnInfo(name = "default_payroll_expense_account_id")
    private Long defaultPayrollExpenseAccountId;
    
    @ColumnInfo(name = "default_salaries_payable_account_id")
    private Long defaultSalariesPayableAccountId;
    
    @ColumnInfo(name = "is_active", defaultValue = "1")
    private boolean isActive;

    // Constructor الرئيسي لـ Room
    public Company(@NonNull String id, String name, String address, String phone, 
                   String email, long createdAt, long updatedAt, 
                   Long defaultCashAccountId, Long defaultExchangeDiffAccountId,
                   Long defaultPayrollExpenseAccountId, Long defaultSalariesPayableAccountId,
                   boolean isActive) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.defaultCashAccountId = defaultCashAccountId;
        this.defaultExchangeDiffAccountId = defaultExchangeDiffAccountId;
        this.defaultPayrollExpenseAccountId = defaultPayrollExpenseAccountId;
        this.defaultSalariesPayableAccountId = defaultSalariesPayableAccountId;
        this.isActive = isActive;
    }

    // Constructor مبسط
    @Ignore
    public Company(@NonNull String id, String name) {
        this.id = id;
        this.name = name;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
        this.isActive = true;
    }

    // Constructor فارغ
    @Ignore
    public Company() {
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
        this.isActive = true;
    }

    // Getters
    @NonNull
    public String getId() { return id; }
    public String getName() { return name; }
    public String getAddress() { return address; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public long getCreatedAt() { return createdAt; }
    public long getUpdatedAt() { return updatedAt; }
    public Long getDefaultCashAccountId() { return defaultCashAccountId; }
    public Long getDefaultExchangeDiffAccountId() { return defaultExchangeDiffAccountId; }
    public Long getDefaultPayrollExpenseAccountId() { return defaultPayrollExpenseAccountId; }
    public Long getDefaultSalariesPayableAccountId() { return defaultSalariesPayableAccountId; }
    public boolean isActive() { return isActive; }

    // Setters
    public void setId(@NonNull String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setAddress(String address) { this.address = address; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setEmail(String email) { this.email = email; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }
    public void setDefaultCashAccountId(Long defaultCashAccountId) { 
        this.defaultCashAccountId = defaultCashAccountId; 
    }
    public void setDefaultExchangeDiffAccountId(Long defaultExchangeDiffAccountId) { 
        this.defaultExchangeDiffAccountId = defaultExchangeDiffAccountId; 
    }
    public void setDefaultPayrollExpenseAccountId(Long defaultPayrollExpenseAccountId) { 
        this.defaultPayrollExpenseAccountId = defaultPayrollExpenseAccountId; 
    }
    public void setDefaultSalariesPayableAccountId(Long defaultSalariesPayableAccountId) { 
        this.defaultSalariesPayableAccountId = defaultSalariesPayableAccountId; 
    }
    public void setActive(boolean active) { isActive = active; }
}
