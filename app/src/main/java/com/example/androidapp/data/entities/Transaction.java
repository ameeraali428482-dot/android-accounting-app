package com.example.androidapp.data.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.TypeConverters;

import com.example.androidapp.data.DateConverter;

@Entity(tableName = "transactions",
        foreignKeys = {
            @ForeignKey(entity = Account.class,
                        parentColumns = "id",
                        childColumns = "from_account_id",
                        onDelete = ForeignKey.RESTRICT),
            @ForeignKey(entity = Account.class,
                        parentColumns = "id",
                        childColumns = "to_account_id",
                        onDelete = ForeignKey.RESTRICT),
            @ForeignKey(entity = Category.class,
                        parentColumns = "id",
                        childColumns = "category_id",
                        onDelete = ForeignKey.SET_NULL)
        },
        indices = {
            @Index(value = {"from_account_id"}),
            @Index(value = {"to_account_id"}),
            @Index(value = {"category_id"}),
            @Index(value = {"date"}),
            @Index(value = {"user_id"}),
            @Index(value = {"company_id"}),
            @Index(value = {"reference_number"}, unique = true)
        })
@TypeConverters(DateConverter.class)
public class Transaction {
    @PrimaryKey(autoGenerate = true)
    public long id;

    @ColumnInfo(name = "amount")
    public double amount;

    @ColumnInfo(name = "date")
    public long date;

    @ColumnInfo(name = "description")
    public String description;

    @ColumnInfo(name = "from_account_id")
    public long fromAccountId;

    @ColumnInfo(name = "to_account_id")
    public long toAccountId;

    @ColumnInfo(name = "category_id")
    public long categoryId;

    @ColumnInfo(name = "transaction_type")
    public String type;

    @ColumnInfo(name = "status")
    public String status = "COMPLETED";

    @ColumnInfo(name = "reference_number")
    public String referenceNumber;

    @ColumnInfo(name = "user_id")
    public String userId;

    @ColumnInfo(name = "company_id")
    public String companyId;

    @ColumnInfo(name = "last_modified")
    public long lastModified;

    @ColumnInfo(name = "created_at")
    public long createdAt;

    public Transaction() {
        this.date = System.currentTimeMillis();
        this.createdAt = System.currentTimeMillis();
        this.lastModified = System.currentTimeMillis();
    }
}
