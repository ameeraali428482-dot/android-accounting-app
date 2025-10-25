package com.example.androidapp.models;

import java.io.Serializable;
import java.util.Date;

/**
 * نموذج متغير المنتج
 */
public class ProductVariant implements Serializable {
    private int id;
    private int productId;
    private String name;
    private String code;
    private String description;
    private double price;
    private double costPrice;
    private int stock;
    private String size;
    private String color;
    private String material;
    private String sku;
    private boolean active;
    private Date createdDate;
    private Date updatedDate;

    // Constructors
    public ProductVariant() {
        this.active = true;
        this.createdDate = new Date();
        this.updatedDate = new Date();
    }

    public ProductVariant(String name, String code, double price) {
        this();
        this.name = name;
        this.code = code;
        this.price = price;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getName() {
        return name != null ? name : "";
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code != null ? code : "";
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description != null ? description : "";
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(double costPrice) {
        this.costPrice = costPrice;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getSize() {
        return size != null ? size : "";
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getColor() {
        return color != null ? color : "";
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getMaterial() {
        return material != null ? material : "";
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public String getSku() {
        return sku != null ? sku : "";
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Date getCreatedDate() {
        return createdDate != null ? createdDate : new Date();
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getUpdatedDate() {
        return updatedDate != null ? updatedDate : new Date();
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }

    // Helper methods
    public double getProfitMargin() {
        if (costPrice > 0) {
            return ((price - costPrice) / costPrice) * 100;
        }
        return 0;
    }

    public double getProfitAmount() {
        return price - costPrice;
    }

    public boolean isInStock() {
        return stock > 0;
    }

    public String getDisplayName() {
        StringBuilder displayName = new StringBuilder(getName());
        
        if (!getSize().isEmpty()) {
            displayName.append(" - ").append(getSize());
        }
        
        if (!getColor().isEmpty()) {
            displayName.append(" - ").append(getColor());
        }
        
        return displayName.toString();
    }

    @Override
    public String toString() {
        return "ProductVariant{" +
                "id=" + id +
                ", productId=" + productId +
                ", name='" + name + '\'' +
                ", code='" + code + '\'' +
                ", price=" + price +
                ", stock=" + stock +
                ", active=" + active +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProductVariant that = (ProductVariant) o;

        if (id != that.id) return false;
        if (productId != that.productId) return false;
        if (Double.compare(that.price, price) != 0) return false;
        if (stock != that.stock) return false;
        if (active != that.active) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        return code != null ? code.equals(that.code) : that.code == null;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = id;
        result = 31 * result + productId;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (code != null ? code.hashCode() : 0);
        temp = Double.doubleToLongBits(price);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + stock;
        result = 31 * result + (active ? 1 : 0);
        return result;
    }
}