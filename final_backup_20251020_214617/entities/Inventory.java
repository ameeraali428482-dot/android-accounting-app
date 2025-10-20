package com.example.androidapp.data.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;
import androidx.room.ForeignKey;
import androidx.room.Index;

@Entity(tableName = "inventory",
        foreignKeys = {
            @ForeignKey(entity = Item.class,
                        parentColumns = "id",
                        childColumns = "item_id",
                        onDelete = ForeignKey.CASCADE),
            @ForeignKey(entity = Warehouse.class,
                        parentColumns = "id",
                        childColumns = "warehouse_id",
                        onDelete = ForeignKey.CASCADE)
        },
        indices = {
            @Index(value = {"item_id"}),
            @Index(value = {"warehouse_id"}),
            @Index(value = {"item_id", "warehouse_id"}, unique = true)
        })
public class Inventory {
    @PrimaryKey(autoGenerate = true)
    public long id;

    @ColumnInfo(name = "item_id")
    public long itemId;

    @ColumnInfo(name = "warehouse_id")
    public long warehouseId;

    @ColumnInfo(name = "quantity")
    public double quantity;

    @ColumnInfo(name = "minimum_stock")
    public double minimumStock;

    @ColumnInfo(name = "maximum_stock")
    public double maximumStock;

    @ColumnInfo(name = "reorder_point")
    public double reorderPoint;

    @ColumnInfo(name = "last_restocked")
    public long lastRestocked;

    @ColumnInfo(name = "unit_cost")
    public double unitCost;

    @ColumnInfo(name = "total_value")
    public double totalValue;

    @ColumnInfo(name = "location")
    public String location;

    @ColumnInfo(name = "notes")
    public String notes;

    @ColumnInfo(name = "created_at")
    public long createdAt;

    @ColumnInfo(name = "updated_at")
    public long updatedAt;

    public Inventory() {
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
    }
}
