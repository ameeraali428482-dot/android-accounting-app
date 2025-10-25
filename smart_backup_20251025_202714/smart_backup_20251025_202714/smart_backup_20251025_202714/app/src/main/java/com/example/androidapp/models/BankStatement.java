package com.example.androidapp.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * نموذج كشف الحساب المصرفي
 */
public class BankStatement implements Serializable {
    private int id;
    private String bankName;
    private String accountNumber;
    private String accountHolderName;
    private Date statementDate;
    private Date statementPeriodStart;
    private Date statementPeriodEnd;
    private double openingBalance;
    private double closingBalance;
    private List<BankTransaction> transactions;
    private String statementReference;
    private boolean isImported;
    private Date importDate;

    // Constructors
    public BankStatement() {
        this.statementDate = new Date();
        this.transactions = new ArrayList<>();
        this.isImported = false;
    }

    public BankStatement(String bankName, String accountNumber, Date statementDate) {
        this();
        this.bankName = bankName;
        this.accountNumber = accountNumber;
        this.statementDate = statementDate;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBankName() {
        return bankName != null ? bankName : "";
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getAccountNumber() {
        return accountNumber != null ? accountNumber : "";
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getAccountHolderName() {
        return accountHolderName != null ? accountHolderName : "";
    }

    public void setAccountHolderName(String accountHolderName) {
        this.accountHolderName = accountHolderName;
    }

    public Date getStatementDate() {
        return statementDate != null ? statementDate : new Date();
    }

    public void setStatementDate(Date statementDate) {
        this.statementDate = statementDate;
    }

    public Date getStatementPeriodStart() {
        return statementPeriodStart;
    }

    public void setStatementPeriodStart(Date statementPeriodStart) {
        this.statementPeriodStart = statementPeriodStart;
    }

    public Date getStatementPeriodEnd() {
        return statementPeriodEnd;
    }

    public void setStatementPeriodEnd(Date statementPeriodEnd) {
        this.statementPeriodEnd = statementPeriodEnd;
    }

    public double getOpeningBalance() {
        return openingBalance;
    }

    public void setOpeningBalance(double openingBalance) {
        this.openingBalance = openingBalance;
    }

    public double getClosingBalance() {
        return closingBalance;
    }

    public void setClosingBalance(double closingBalance) {
        this.closingBalance = closingBalance;
    }

    public List<BankTransaction> getTransactions() {
        return transactions != null ? transactions : new ArrayList<>();
    }

    public void setTransactions(List<BankTransaction> transactions) {
        this.transactions = transactions;
    }

    public String getStatementReference() {
        return statementReference != null ? statementReference : "";
    }

    public void setStatementReference(String statementReference) {
        this.statementReference = statementReference;
    }

    public boolean isImported() {
        return isImported;
    }

    public void setImported(boolean imported) {
        isImported = imported;
    }

    public Date getImportDate() {
        return importDate;
    }

    public void setImportDate(Date importDate) {
        this.importDate = importDate;
    }

    // Helper methods
    public void addTransaction(BankTransaction transaction) {
        if (transactions == null) {
            transactions = new ArrayList<>();
        }
        transactions.add(transaction);
    }

    public void removeTransaction(BankTransaction transaction) {
        if (transactions != null) {
            transactions.remove(transaction);
        }
    }

    public int getTransactionCount() {
        return transactions != null ? transactions.size() : 0;
    }

    public double getTotalDebits() {
        double total = 0.0;
        if (transactions != null) {
            for (BankTransaction transaction : transactions) {
                if (transaction.getAmount() < 0) {
                    total += Math.abs(transaction.getAmount());
                }
            }
        }
        return total;
    }

    public double getTotalCredits() {
        double total = 0.0;
        if (transactions != null) {
            for (BankTransaction transaction : transactions) {
                if (transaction.getAmount() > 0) {
                    total += transaction.getAmount();
                }
            }
        }
        return total;
    }

    public double getNetMovement() {
        return getTotalCredits() - getTotalDebits();
    }

    public boolean isBalanceValid() {
        double calculatedBalance = openingBalance + getNetMovement();
        return Math.abs(calculatedBalance - closingBalance) < 0.01; // Tolerance for rounding
    }

    @Override
    public String toString() {
        return "BankStatement{" +
                "id=" + id +
                ", bankName='" + bankName + '\'' +
                ", accountNumber='" + accountNumber + '\'' +
                ", statementDate=" + statementDate +
                ", openingBalance=" + openingBalance +
                ", closingBalance=" + closingBalance +
                ", transactionCount=" + getTransactionCount() +
                '}';
    }

    /**
     * نموذج معاملة بنكية ضمن كشف الحساب
     */
    public static class BankTransaction implements Serializable {
        private int id;
        private Date transactionDate;
        private String description;
        private String reference;
        private double amount;
        private double runningBalance;
        private String transactionType;
        private boolean isMatched;

        // Constructors
        public BankTransaction() {
            this.transactionDate = new Date();
            this.isMatched = false;
        }

        public BankTransaction(Date transactionDate, String description, double amount) {
            this();
            this.transactionDate = transactionDate;
            this.description = description;
            this.amount = amount;
        }

        // Getters and Setters
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public Date getTransactionDate() {
            return transactionDate != null ? transactionDate : new Date();
        }

        public void setTransactionDate(Date transactionDate) {
            this.transactionDate = transactionDate;
        }

        public String getDescription() {
            return description != null ? description : "";
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getReference() {
            return reference != null ? reference : "";
        }

        public void setReference(String reference) {
            this.reference = reference;
        }

        public double getAmount() {
            return amount;
        }

        public void setAmount(double amount) {
            this.amount = amount;
        }

        public double getRunningBalance() {
            return runningBalance;
        }

        public void setRunningBalance(double runningBalance) {
            this.runningBalance = runningBalance;
        }

        public String getTransactionType() {
            return transactionType != null ? transactionType : "";
        }

        public void setTransactionType(String transactionType) {
            this.transactionType = transactionType;
        }

        public boolean isMatched() {
            return isMatched;
        }

        public void setMatched(boolean matched) {
            isMatched = matched;
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

        @Override
        public String toString() {
            return "BankTransaction{" +
                    "id=" + id +
                    ", transactionDate=" + transactionDate +
                    ", description='" + description + '\'' +
                    ", amount=" + amount +
                    ", isMatched=" + isMatched +
                    '}';
        }
    }
}