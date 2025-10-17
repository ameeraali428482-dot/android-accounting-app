package com.example.androidapp.data.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "accounts",
        foreignKeys = @ForeignKey(entity = Company.class,
                                  parentColumns = "id",
                                  childColumns = "companyId",
                                  onDelete = ForeignKey.CASCADE),
        indices = {@Index("companyId")})
public class Account {
    @PrimaryKey
    @NonNull
    private String id;
    private String companyId;
    private String name;
    private String type;
    private String number;
    private float balance;

    public Account(@NonNull String id, String companyId, String name, String type, String number, float balance) {
        this.id = id;
        this.companyId = companyId;
        this.name = name;
        this.type = type;
        this.number = number;
        this.balance = balance;
    }

    // Getters
    @NonNull
    public String getId() { return id; }
    public String getCompanyId() { return companyId; }
    public String getName() { return name; }
    public String getType() { return type; }
    public String getNumber() { return number; }
    public float getBalance() { return balance; }
    public String getAccountName() { return name; }
    public String getAccountNumber() { return number; }
    public String getAccountType() { return type; }
    public float getCurrentBalance() { return balance; }
}
