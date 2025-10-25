package com.example.androidapp.models;

import java.io.Serializable;
import java.util.Date;

/**
 * نموذج عنصر مطابقة كشف الحساب المصرفي
 */
public class BankReconciliationItem implements Serializable {
    private int id;
    private String description;
    private double amount;
    private Date date;
    private String notes;
    private boolean reconciled;
    private String referenceNumber;
    private ReconciliationType type;

    public enum ReconciliationType {
        DEPOSIT_IN_TRANSIT,     // وديعة معلقة
        OUTSTANDING_CHECK,      // شيك معلق
        BANK_ERROR,            // خطأ بنكي
        BOOK_ERROR,            // خطأ دفتري
        BANK_CHARGE,           // رسوم بنكية
        INTEREST_EARNED,       // فوائد مكتسبة
        NSF_CHECK,             // شيك مرتد
        ELECTRONIC_TRANSFER    // تحويل إلكتروني
    }

    // Constructors
    public BankReconciliationItem() {
        this.date = new Date();
        this.reconciled = false;
    }

    public BankReconciliationItem(int id, String description, double amount, Date date, String notes, boolean reconciled) {
        this.id = id;
        this.description = description;
        this.amount = amount;
        this.date = date != null ? date : new Date();
        this.notes = notes;
        this.reconciled = reconciled;
    }

    public BankReconciliationItem(String description, double amount, ReconciliationType type) {
        this();
        this.description = description;
        this.amount = amount;
        this.type = type;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description != null ? description : "";
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Date getDate() {
        return date != null ? date : new Date();
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getNotes() {
        return notes != null ? notes : "";
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public boolean isReconciled() {
        return reconciled;
    }

    public void setReconciled(boolean reconciled) {
        this.reconciled = reconciled;
    }

    public String getReferenceNumber() {
        return referenceNumber != null ? referenceNumber : "";
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public ReconciliationType getType() {
        return type != null ? type : ReconciliationType.DEPOSIT_IN_TRANSIT;
    }

    public void setType(ReconciliationType type) {
        this.type = type;
    }

    // Helper methods
    public boolean isDebit() {
        return amount < 0;
    }

    public boolean isCredit() {
        return amount >= 0;
    }

    public double getAbsoluteAmount() {
        return Math.abs(amount);
    }

    public String getFormattedAmount() {
        return String.format("%.2f", Math.abs(amount));
    }

    public String getTypeDisplayName() {
        switch (type) {
            case DEPOSIT_IN_TRANSIT:
                return "وديعة معلقة";
            case OUTSTANDING_CHECK:
                return "شيك معلق";
            case BANK_ERROR:
                return "خطأ بنكي";
            case BOOK_ERROR:
                return "خطأ دفتري";
            case BANK_CHARGE:
                return "رسوم بنكية";
            case INTEREST_EARNED:
                return "فوائد مكتسبة";
            case NSF_CHECK:
                return "شيك مرتد";
            case ELECTRONIC_TRANSFER:
                return "تحويل إلكتروني";
            default:
                return "غير محدد";
        }
    }

    @Override
    public String toString() {
        return "BankReconciliationItem{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", amount=" + amount +
                ", date=" + date +
                ", reconciled=" + reconciled +
                ", type=" + type +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BankReconciliationItem that = (BankReconciliationItem) o;

        if (id != that.id) return false;
        if (Double.compare(that.amount, amount) != 0) return false;
        if (reconciled != that.reconciled) return false;
        if (description != null ? !description.equals(that.description) : that.description != null)
            return false;
        if (date != null ? !date.equals(that.date) : that.date != null) return false;
        return type == that.type;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = id;
        result = 31 * result + (description != null ? description.hashCode() : 0);
        temp = Double.doubleToLongBits(amount);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (date != null ? date.hashCode() : 0);
        result = 31 * result + (reconciled ? 1 : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }
}