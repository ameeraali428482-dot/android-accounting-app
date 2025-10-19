package com.example.androidapp.data.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;
import androidx.room.Index;

@Entity(tableName = "warehouses",
        indices = {
            @Index(value = {"name"}, unique = true),
            @Index(value = {"company_id"})
        })
public class Warehouse {
    @PrimaryKey(autoGenerate = true)
    public long id;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "address")
    public String address;

    @ColumnInfo(name = "phone")
    public String phone;

    @ColumnInfo(name = "email")
    public String email;

    @ColumnInfo(name = "manager_name")
    public String managerName;

    @ColumnInfo(name = "capacity")
    public double capacity;

    @ColumnInfo(name = "current_usage")
    public double currentUsage;

    @ColumnInfo(name = "is_active")
    public boolean isActive = true;

    @ColumnInfo(name = "company_id")
    public String companyId;

    @ColumnInfo(name = "created_at")
    public long createdAt;

    @ColumnInfo(name = "updated_at")
    public long updatedAt;

    public Warehouse() {
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
    }
}
