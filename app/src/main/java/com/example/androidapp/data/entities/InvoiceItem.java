package com.example.androidapp.data.entities;
import androidx.room.Ignore;
import androidx.room.Embedded;
import androidx.room.PrimaryKey;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

@Entity(tableName = "invoice_items")
public class InvoiceItem {
    private String id;
    private String invoiceId;
    private String itemId;
    private float quantity;
    private String unit;
    private float price;
    private float cost;
    private float discount;
    private float tax;

    public InvoiceItem(String id, String invoiceId, String itemId, float quantity, String unit, float price, float cost, float discount, float tax) {
        this.id = id;
        this.invoiceId = invoiceId;
        this.itemId = itemId;
        this.quantity = quantity;
        this.unit = unit;
        this.price = price;
        this.cost = cost;
        this.discount = discount;
        this.tax = tax;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getInvoiceId() {
        return invoiceId;
    }

    public String getItemId() {
        return itemId;
    }

    public float getQuantity() {
        return quantity;
    }

    public String getUnit() {
        return unit;
    }

    public float getPrice() {
        return price;
    }

    public float getCost() {
        return cost;
    }

    public float getDiscount() {
        return discount;
    }

    public float getTax() {
        return tax;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public void setQuantity(float quantity) {
        this.quantity = quantity;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public void setCost(float cost) {
        this.cost = cost;
    }

    public void setDiscount(float discount) {
        this.discount = discount;
    }

    public void setTax(float tax) {
        this.tax = tax;
    }
}

