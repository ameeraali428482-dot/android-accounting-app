package com.example.androidapp.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * نموذج طلب الشراء
 */
public class PurchaseOrder implements Serializable {
    private int id;
    private String orderNumber;
    private Date orderDate;
    private Date expectedDeliveryDate;
    private Date actualDeliveryDate;
    private int supplierId;
    private String supplierName;
    private double subtotal;
    private double taxAmount;
    private double discountAmount;
    private double totalAmount;
    private OrderStatus status;
    private boolean urgent;
    private String notes;
    private String createdBy;
    private Date createdDate;
    private Date updatedDate;
    private List<PurchaseOrderItem> items;

    public enum OrderStatus {
        PENDING,    // معلق
        APPROVED,   // موافق عليه
        RECEIVED,   // مستلم
        CANCELLED   // ملغي
    }

    // Constructors
    public PurchaseOrder() {
        this.orderDate = new Date();
        this.createdDate = new Date();
        this.updatedDate = new Date();
        this.status = OrderStatus.PENDING;
        this.urgent = false;
        this.items = new ArrayList<>();
    }

    public PurchaseOrder(String orderNumber, int supplierId) {
        this();
        this.orderNumber = orderNumber;
        this.supplierId = supplierId;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOrderNumber() {
        return orderNumber != null ? orderNumber : "";
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public Date getOrderDate() {
        return orderDate != null ? orderDate : new Date();
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public Date getExpectedDeliveryDate() {
        return expectedDeliveryDate;
    }

    public void setExpectedDeliveryDate(Date expectedDeliveryDate) {
        this.expectedDeliveryDate = expectedDeliveryDate;
    }

    public Date getActualDeliveryDate() {
        return actualDeliveryDate;
    }

    public void setActualDeliveryDate(Date actualDeliveryDate) {
        this.actualDeliveryDate = actualDeliveryDate;
    }

    public int getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(int supplierId) {
        this.supplierId = supplierId;
    }

    public String getSupplierName() {
        return supplierName != null ? supplierName : "";
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public double getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(double taxAmount) {
        this.taxAmount = taxAmount;
    }

    public double getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(double discountAmount) {
        this.discountAmount = discountAmount;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public OrderStatus getStatus() {
        return status != null ? status : OrderStatus.PENDING;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public boolean isUrgent() {
        return urgent;
    }

    public void setUrgent(boolean urgent) {
        this.urgent = urgent;
    }

    public String getNotes() {
        return notes != null ? notes : "";
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getCreatedBy() {
        return createdBy != null ? createdBy : "";
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
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

    public List<PurchaseOrderItem> getItems() {
        return items != null ? items : new ArrayList<>();
    }

    public void setItems(List<PurchaseOrderItem> items) {
        this.items = items;
    }

    // Helper methods
    public void addItem(PurchaseOrderItem item) {
        if (items == null) {
            items = new ArrayList<>();
        }
        items.add(item);
    }

    public void removeItem(PurchaseOrderItem item) {
        if (items != null) {
            items.remove(item);
        }
    }

    public int getItemsCount() {
        return items != null ? items.size() : 0;
    }

    public void calculateTotals() {
        subtotal = 0.0;
        if (items != null) {
            for (PurchaseOrderItem item : items) {
                subtotal += item.getTotalPrice();
            }
        }
        
        // Calculate tax (15% VAT in Saudi Arabia)
        taxAmount = subtotal * 0.15;
        
        // Calculate total
        totalAmount = subtotal + taxAmount - discountAmount;
    }

    public boolean isOverdue() {
        if (expectedDeliveryDate != null && status != OrderStatus.RECEIVED && status != OrderStatus.CANCELLED) {
            return new Date().after(expectedDeliveryDate);
        }
        return false;
    }

    public int getDaysUntilDelivery() {
        if (expectedDeliveryDate != null) {
            long diffInMs = expectedDeliveryDate.getTime() - new Date().getTime();
            return (int) (diffInMs / (1000 * 60 * 60 * 24));
        }
        return 0;
    }

    public String getStatusDisplayName() {
        switch (status) {
            case PENDING:
                return "معلق";
            case APPROVED:
                return "موافق عليه";
            case RECEIVED:
                return "مستلم";
            case CANCELLED:
                return "ملغي";
            default:
                return "غير محدد";
        }
    }

    @Override
    public String toString() {
        return "PurchaseOrder{" +
                "id=" + id +
                ", orderNumber='" + orderNumber + '\'' +
                ", orderDate=" + orderDate +
                ", supplierName='" + supplierName + '\'' +
                ", totalAmount=" + totalAmount +
                ", status=" + status +
                ", itemsCount=" + getItemsCount() +
                '}';
    }

    /**
     * نموذج عنصر طلب الشراء
     */
    public static class PurchaseOrderItem implements Serializable {
        private int id;
        private int productId;
        private String productName;
        private String productCode;
        private int quantity;
        private double unitPrice;
        private double totalPrice;
        private String notes;

        // Constructors
        public PurchaseOrderItem() {}

        public PurchaseOrderItem(int productId, String productName, int quantity, double unitPrice) {
            this.productId = productId;
            this.productName = productName;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
            this.totalPrice = quantity * unitPrice;
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

        public String getProductName() {
            return productName != null ? productName : "";
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }

        public String getProductCode() {
            return productCode != null ? productCode : "";
        }

        public void setProductCode(String productCode) {
            this.productCode = productCode;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
            this.totalPrice = quantity * unitPrice;
        }

        public double getUnitPrice() {
            return unitPrice;
        }

        public void setUnitPrice(double unitPrice) {
            this.unitPrice = unitPrice;
            this.totalPrice = quantity * unitPrice;
        }

        public double getTotalPrice() {
            return totalPrice;
        }

        public void setTotalPrice(double totalPrice) {
            this.totalPrice = totalPrice;
        }

        public String getNotes() {
            return notes != null ? notes : "";
        }

        public void setNotes(String notes) {
            this.notes = notes;
        }

        @Override
        public String toString() {
            return "PurchaseOrderItem{" +
                    "id=" + id +
                    ", productName='" + productName + '\'' +
                    ", quantity=" + quantity +
                    ", unitPrice=" + unitPrice +
                    ", totalPrice=" + totalPrice +
                    '}';
        }
    }
}