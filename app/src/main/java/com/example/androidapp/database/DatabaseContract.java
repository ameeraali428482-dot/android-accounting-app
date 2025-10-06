package com.example.androidapp.data;

import android.provider.BaseColumns;

public final class DatabaseContract {
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private DatabaseContract() {}

    /* Inner class that defines the table contents */

    public static abstract class UserEntry implements BaseColumns {
        public static final String TABLE_NAME = "user";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_EMAIL = "email";
        public static final String COLUMN_NAME_PASSWORD = "password";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_PHONE = "phone";
        public static final String COLUMN_NAME_PHONE_HASH = "phoneHash";
        public static final String COLUMN_NAME_POINTS = "points";
        public static final String COLUMN_NAME_CREATED_AT = "createdAt";
        public static final String COLUMN_NAME_UPDATED_AT = "updatedAt";
        public static final String COLUMN_NAME_PERSONAL_COMPANY_ID = "personalCompanyId";

        public static final String CREATE_TABLE = "CREATE TABLE " +
                TABLE_NAME + " (" +
                COLUMN_NAME_ID + " TEXT PRIMARY KEY," +
                COLUMN_NAME_EMAIL + " TEXT UNIQUE NOT NULL," +
                COLUMN_NAME_PASSWORD + " TEXT NOT NULL," +
                COLUMN_NAME_NAME + " TEXT NOT NULL," +
                COLUMN_NAME_PHONE + " TEXT UNIQUE," +
                COLUMN_NAME_PHONE_HASH + " TEXT UNIQUE," +
                COLUMN_NAME_POINTS + " INTEGER DEFAULT 0," +
                COLUMN_NAME_CREATED_AT + " TEXT NOT NULL," +
                COLUMN_NAME_UPDATED_AT + " TEXT NOT NULL," +
                COLUMN_NAME_PERSONAL_COMPANY_ID + " TEXT)";

        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    public static abstract class CompanyEntry implements BaseColumns {
        public static final String TABLE_NAME = "company";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_ADDRESS = "address";
        public static final String COLUMN_NAME_PHONE = "phone";
        public static final String COLUMN_NAME_CREATED_AT = "createdAt";
        public static final String COLUMN_NAME_UPDATED_AT = "updatedAt";
        public static final String COLUMN_NAME_DEFAULT_CASH_ACCOUNT_ID = "defaultCashAccountId";
        public static final String COLUMN_NAME_DEFAULT_EXCHANGE_DIFF_ACCOUNT_ID = "defaultExchangeDiffAccountId";
        public static final String COLUMN_NAME_DEFAULT_PAYROLL_EXPENSE_ACCOUNT_ID = "defaultPayrollExpenseAccountId";
        public static final String COLUMN_NAME_DEFAULT_SALARIES_PAYABLE_ACCOUNT_ID = "defaultSalariesPayableAccountId";

        public static final String CREATE_TABLE = "CREATE TABLE " +
                TABLE_NAME + " (" +
                COLUMN_NAME_ID + " TEXT PRIMARY KEY," +
                COLUMN_NAME_NAME + " TEXT NOT NULL," +
                COLUMN_NAME_ADDRESS + " TEXT," +
                COLUMN_NAME_PHONE + " TEXT," +
                COLUMN_NAME_CREATED_AT + " TEXT NOT NULL," +
                COLUMN_NAME_UPDATED_AT + " TEXT NOT NULL," +
                COLUMN_NAME_DEFAULT_CASH_ACCOUNT_ID + " TEXT," +
                COLUMN_NAME_DEFAULT_EXCHANGE_DIFF_ACCOUNT_ID + " TEXT," +
                COLUMN_NAME_DEFAULT_PAYROLL_EXPENSE_ACCOUNT_ID + " TEXT," +
                COLUMN_NAME_DEFAULT_SALARIES_PAYABLE_ACCOUNT_ID + " TEXT)";

        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    public static abstract class MembershipEntry implements BaseColumns {
        public static final String TABLE_NAME = "membership";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_USER_ID = "userId";
        public static final String COLUMN_NAME_COMPANY_ID = "companyId";
        public static final String COLUMN_NAME_ROLE_ID = "roleId";
        public static final String COLUMN_NAME_CREATED_AT = "createdAt";

        public static final String CREATE_TABLE = "CREATE TABLE " +
                TABLE_NAME + " (" +
                COLUMN_NAME_ID + " TEXT PRIMARY KEY," +
                COLUMN_NAME_USER_ID + " TEXT NOT NULL," +
                COLUMN_NAME_COMPANY_ID + " TEXT NOT NULL," +
                COLUMN_NAME_ROLE_ID + " TEXT NOT NULL," +
                COLUMN_NAME_CREATED_AT + " TEXT NOT NULL," +
                "FOREIGN KEY(" + COLUMN_NAME_USER_ID + ") REFERENCES " + UserEntry.TABLE_NAME + "(" + UserEntry.COLUMN_NAME_ID + ") ON DELETE CASCADE," +
                "FOREIGN KEY(" + COLUMN_NAME_COMPANY_ID + ") REFERENCES " + CompanyEntry.TABLE_NAME + "(" + CompanyEntry.COLUMN_NAME_ID + ") ON DELETE CASCADE," +
                "FOREIGN KEY(" + COLUMN_NAME_ROLE_ID + ") REFERENCES " + RoleEntry.TABLE_NAME + "(" + RoleEntry.COLUMN_NAME_ID + ") ON DELETE CASCADE," +
                "UNIQUE (" + COLUMN_NAME_USER_ID + ", " + COLUMN_NAME_COMPANY_ID + "))";

        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    public static abstract class RoleEntry implements BaseColumns {
        public static final String TABLE_NAME = "role";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_DESCRIPTION = "description";
        public static final String COLUMN_NAME_COMPANY_ID = "companyId";
        public static final String COLUMN_NAME_IS_DEFAULT = "isDefault";

        public static final String CREATE_TABLE = "CREATE TABLE " +
                TABLE_NAME + " (" +
                COLUMN_NAME_ID + " TEXT PRIMARY KEY," +
                COLUMN_NAME_NAME + " TEXT NOT NULL," +
                COLUMN_NAME_DESCRIPTION + " TEXT," +
                COLUMN_NAME_COMPANY_ID + " TEXT NOT NULL," +
                COLUMN_NAME_IS_DEFAULT + " INTEGER DEFAULT 0," +
                "FOREIGN KEY(" + COLUMN_NAME_COMPANY_ID + ") REFERENCES " + CompanyEntry.TABLE_NAME + "(" + CompanyEntry.COLUMN_NAME_ID + ") ON DELETE CASCADE," +
                "UNIQUE (" + COLUMN_NAME_COMPANY_ID + ", " + COLUMN_NAME_NAME + "))";

        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    public static abstract class PermissionEntry implements BaseColumns {
        public static final String TABLE_NAME = "permission";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_ACTION = "action";
        public static final String COLUMN_NAME_DESCRIPTION = "description";
        public static final String COLUMN_NAME_GROUP = "group";

        public static final String CREATE_TABLE = "CREATE TABLE " +
                TABLE_NAME + " (" +
                COLUMN_NAME_ID + " TEXT PRIMARY KEY," +
                COLUMN_NAME_ACTION + " TEXT UNIQUE NOT NULL," +
                COLUMN_NAME_DESCRIPTION + " TEXT NOT NULL," +
                COLUMN_NAME_GROUP + " TEXT NOT NULL)";

        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    public static abstract class AccountEntry implements BaseColumns {
        public static final String TABLE_NAME = "account";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_CODE = "code";
        public static final String COLUMN_NAME_TYPE = "type";
        public static final String COLUMN_NAME_IS_DEBIT = "isDebit";
        public static final String COLUMN_NAME_PARENT_CODE = "parentCode";
        public static final String COLUMN_NAME_DESCRIPTION = "description";
        public static final String COLUMN_NAME_COMPANY_ID = "companyId";
        public static final String COLUMN_NAME_CREATED_AT = "createdAt";
        public static final String COLUMN_NAME_UPDATED_AT = "updatedAt";

        public static final String CREATE_TABLE = "CREATE TABLE " +
                TABLE_NAME + " (" +
                COLUMN_NAME_ID + " TEXT PRIMARY KEY," +
                COLUMN_NAME_NAME + " TEXT NOT NULL," +
                COLUMN_NAME_CODE + " TEXT NOT NULL," +
                COLUMN_NAME_TYPE + " TEXT NOT NULL," +
                COLUMN_NAME_IS_DEBIT + " INTEGER NOT NULL," +
                COLUMN_NAME_PARENT_CODE + " TEXT," +
                COLUMN_NAME_DESCRIPTION + " TEXT," +
                COLUMN_NAME_COMPANY_ID + " TEXT NOT NULL," +
                COLUMN_NAME_CREATED_AT + " TEXT NOT NULL," +
                COLUMN_NAME_UPDATED_AT + " TEXT NOT NULL," +
                "FOREIGN KEY(" + COLUMN_NAME_COMPANY_ID + ") REFERENCES " + CompanyEntry.TABLE_NAME + "(" + CompanyEntry.COLUMN_NAME_ID + ") ON DELETE CASCADE," +
                "UNIQUE (" + COLUMN_NAME_COMPANY_ID + ", " + COLUMN_NAME_CODE + "))";

        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    public static abstract class ItemEntry implements BaseColumns {
        public static final String TABLE_NAME = "item";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_SCIENTIFIC_NAME = "scientificName";
        public static final String COLUMN_NAME_DESCRIPTION = "description";
        public static final String COLUMN_NAME_BRAND = "brand";
        public static final String COLUMN_NAME_AGENT = "agent";
        public static final String COLUMN_NAME_BARCODE = "barcode";
        public static final String COLUMN_NAME_COMPANY_ID = "companyId";
        public static final String COLUMN_NAME_REORDER_LEVEL = "reorderLevel";

        public static final String CREATE_TABLE = "CREATE TABLE " +
                TABLE_NAME + " (" +
                COLUMN_NAME_ID + " TEXT PRIMARY KEY," +
                COLUMN_NAME_NAME + " TEXT NOT NULL," +
                COLUMN_NAME_SCIENTIFIC_NAME + " TEXT," +
                COLUMN_NAME_DESCRIPTION + " TEXT," +
                COLUMN_NAME_BRAND + " TEXT," +
                COLUMN_NAME_AGENT + " TEXT," +
                COLUMN_NAME_BARCODE + " TEXT UNIQUE," +
                COLUMN_NAME_COMPANY_ID + " TEXT NOT NULL," +
                COLUMN_NAME_REORDER_LEVEL + " INTEGER," +
                "FOREIGN KEY(" + COLUMN_NAME_COMPANY_ID + ") REFERENCES " + CompanyEntry.TABLE_NAME + "(" + CompanyEntry.COLUMN_NAME_ID + ") ON DELETE CASCADE)";

        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    public static abstract class ItemUnitEntry implements BaseColumns {
        public static final String TABLE_NAME = "item_unit";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_ITEM_ID = "itemId";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_CONVERSION_FACTOR = "conversionFactor";
        public static final String COLUMN_NAME_PRICE = "price";
        public static final String COLUMN_NAME_COST = "cost";
        public static final String COLUMN_NAME_IS_BASE_UNIT = "isBaseUnit";

        public static final String CREATE_TABLE = "CREATE TABLE " +
                TABLE_NAME + " (" +
                COLUMN_NAME_ID + " TEXT PRIMARY KEY," +
                COLUMN_NAME_ITEM_ID + " TEXT NOT NULL," +
                COLUMN_NAME_NAME + " TEXT NOT NULL," +
                COLUMN_NAME_CONVERSION_FACTOR + " REAL NOT NULL," +
                COLUMN_NAME_PRICE + " REAL NOT NULL," +
                COLUMN_NAME_COST + " REAL NOT NULL," +
                COLUMN_NAME_IS_BASE_UNIT + " INTEGER DEFAULT 0," +
                "FOREIGN KEY(" + COLUMN_NAME_ITEM_ID + ") REFERENCES " + ItemEntry.TABLE_NAME + "(" + ItemEntry.COLUMN_NAME_ID + ") ON DELETE CASCADE," +
                "UNIQUE (" + COLUMN_NAME_ITEM_ID + ", " + COLUMN_NAME_NAME + "))";

        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    public static abstract class CustomerEntry implements BaseColumns {
        public static final String TABLE_NAME = "customer";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_EMAIL = "email";
        public static final String COLUMN_NAME_PHONE = "phone";
        public static final String COLUMN_NAME_ADDRESS = "address";
        public static final String COLUMN_NAME_REGION = "region";
        public static final String COLUMN_NAME_CATEGORY = "category";
        public static final String COLUMN_NAME_CREDIT_LIMIT = "creditLimit";
        public static final String COLUMN_NAME_OPENING_BALANCE = "openingBalance";
        public static final String COLUMN_NAME_COMPANY_ID = "companyId";

        public static final String CREATE_TABLE = "CREATE TABLE " +
                TABLE_NAME + " (" +
                COLUMN_NAME_ID + " TEXT PRIMARY KEY," +
                COLUMN_NAME_NAME + " TEXT NOT NULL," +
                COLUMN_NAME_EMAIL + " TEXT," +
                COLUMN_NAME_PHONE + " TEXT," +
                COLUMN_NAME_ADDRESS + " TEXT," +
                COLUMN_NAME_REGION + " TEXT," +
                COLUMN_NAME_CATEGORY + " TEXT," +
                COLUMN_NAME_CREDIT_LIMIT + " REAL," +
                COLUMN_NAME_OPENING_BALANCE + " REAL DEFAULT 0," +
                COLUMN_NAME_COMPANY_ID + " TEXT NOT NULL," +
                "FOREIGN KEY(" + COLUMN_NAME_COMPANY_ID + ") REFERENCES " + CompanyEntry.TABLE_NAME + "(" + CompanyEntry.COLUMN_NAME_ID + ") ON DELETE CASCADE)";

        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    public static abstract class SupplierEntry implements BaseColumns {
        public static final String TABLE_NAME = "supplier";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_EMAIL = "email";
        public static final String COLUMN_NAME_PHONE = "phone";
        public static final String COLUMN_NAME_ADDRESS = "address";
        public static final String COLUMN_NAME_OPENING_BALANCE = "openingBalance";
        public static final String COLUMN_NAME_COMPANY_ID = "companyId";

        public static final String CREATE_TABLE = "CREATE TABLE " +
                TABLE_NAME + " (" +
                COLUMN_NAME_ID + " TEXT PRIMARY KEY," +
                COLUMN_NAME_NAME + " TEXT NOT NULL," +
                COLUMN_NAME_EMAIL + " TEXT," +
                COLUMN_NAME_PHONE + " TEXT," +
                COLUMN_NAME_ADDRESS + " TEXT," +
                COLUMN_NAME_OPENING_BALANCE + " REAL DEFAULT 0," +
                COLUMN_NAME_COMPANY_ID + " TEXT NOT NULL," +
                "FOREIGN KEY(" + COLUMN_NAME_COMPANY_ID + ") REFERENCES " + CompanyEntry.TABLE_NAME + "(" + CompanyEntry.COLUMN_NAME_ID + ") ON DELETE CASCADE)";

        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    public static abstract class InvoiceEntry implements BaseColumns {
        public static final String TABLE_NAME = "invoice";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_COMPANY_ID = "companyId";
        public static final String COLUMN_NAME_INVOICE_TYPE = "invoiceType";
        public static final String COLUMN_NAME_PAYMENT_TYPE = "paymentType";
        public static final String COLUMN_NAME_DATE = "date";
        public static final String COLUMN_NAME_DUE_DATE = "dueDate";
        public static final String COLUMN_NAME_CUSTOMER_ID = "customerId";
        public static final String COLUMN_NAME_SUPPLIER_ID = "supplierId";
        public static final String COLUMN_NAME_SUB_TOTAL = "subTotal";
        public static final String COLUMN_NAME_DISCOUNT = "discount";
        public static final String COLUMN_NAME_TAX = "tax";
        public static final String COLUMN_NAME_GRAND_TOTAL = "grandTotal";
        public static final String COLUMN_NAME_PAID_AMOUNT = "paidAmount";
        public static final String COLUMN_NAME_REMAINING_AMOUNT = "remainingAmount";
        public static final String COLUMN_NAME_STATUS = "status";
        public static final String COLUMN_NAME_NOTES = "notes";

        pub        public static final String CREATE_TABLE = "CREATE TABLE " +
                TABLE_NAME + " (" +
                COLUMN_NAME_ID + " TEXT PRIMARY KEY," +
                COLUMN_NAME_COMPANY_ID + " TEXT NOT NULL," +
                COLUMN_NAME_INVOICE_TYPE + " TEXT NOT NULL," +
                COLUMN_NAME_PAYMENT_TYPE + " TEXT NOT NULL," +
                COLUMN_NAME_DATE + " TEXT NOT NULL," +
                COLUMN_NAME_DUE_DATE + " TEXT," +
                COLUMN_NAME_CUSTOMER_ID + " TEXT," +
                COLUMN_NAME_SUPPLIER_ID + " TEXT," +
                COLUMN_NAME_SUB_TOTAL + " REAL NOT NULL," +
                COLUMN_NAME_DISCOUNT + " REAL DEFAULT 0," +
                COLUMN_NAME_TAX + " REAL DEFAULT 0," +
                COLUMN_NAME_GRAND_TOTAL + " REAL NOT NULL," +
                COLUMN_NAME_PAID_AMOUNT + " REAL NOT NULL," +
                COLUMN_NAME_REMAINING_AMOUNT + " REAL NOT NULL," +
                COLUMN_NAME_STATUS + " TEXT NOT NULL," +
                COLUMN_NAME_NOTES + " TEXT," +
                "FOREIGN KEY(" + COLUMN_NAME_COMPANY_ID + ") REFERENCES " + CompanyEntry.TABLE_NAME + "(" + CompanyEntry.COLUMN_NAME_ID + ") ON DELETE CASCADE," +
                "FOREIGN KEY(" + COLUMN_NAME_CUSTOMER_ID + ") REFERENCES " + CustomerEntry.TABLE_NAME + "(" + CustomerEntry.COLUMN_NAME_ID + ")," +
                "FOREIGN KEY(" + COLUMN_NAME_SUPPLIER_ID + ") REFERENCES " + SupplierEntry.TABLE_NAME + "(" + SupplierEntry.COLUMN_NAME_ID + "))";

        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    public static abstract class InvoiceItemEntry implements BaseColumns {
        public static final String TABLE_NAME = "invoice_item";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_INVOICE_ID = "invoiceId";
        public static final String COLUMN_NAME_ITEM_ID = "itemId";
        public static final String COLUMN_NAME_QUANTITY = "quantity";
        public static final String COLUMN_NAME_UNIT = "unit";
        public static final String COLUMN_NAME_PRICE = "price";
        public static final String COLUMN_NAME_COST = "cost";
        public static final String COLUMN_NAME_DISCOUNT = "discount";
        public static final String COLUMN_NAME_TAX = "tax";

        public static final String CREATE_TABLE = "CREATE TABLE " +
                TABLE_NAME + " (" +
                COLUMN_NAME_ID + " TEXT PRIMARY KEY," +
                COLUMN_NAME_INVOICE_ID + " TEXT NOT NULL," +
                COLUMN_NAME_ITEM_ID + " TEXT NOT NULL," +
                COLUMN_NAME_QUANTITY + " REAL NOT NULL," +
                COLUMN_NAME_UNIT + " TEXT NOT NULL," +
                COLUMN_NAME_PRICE + " REAL NOT NULL," +
                COLUMN_NAME_COST + " REAL NOT NULL," +
                COLUMN_NAME_DISCOUNT + " REAL DEFAULT 0," +
                COLUMN_NAME_TAX + " REAL DEFAULT 0," +
                "FOREIGN KEY(" + COLUMN_NAME_INVOICE_ID + ") REFERENCES " + InvoiceEntry.TABLE_NAME + "(" + InvoiceEntry.COLUMN_NAME_ID + ") ON DELETE CASCADE," +
                "FOREIGN KEY(" + COLUMN_NAME_ITEM_ID + ") REFERENCES " + ItemEntry.TABLE_NAME + "(" + ItemEntry.COLUMN_NAME_ID + ") ON DELETE CASCADE)";

        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    public static abstract class JournalEntryEntry implements BaseColumns {
        public static final String TABLE_NAME = "journal_entry";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_COMPANY_ID = "companyId";
        public static final String COLUMN_NAME_DATE = "date";
        public static final String COLUMN_NAME_DESCRIPTION = "description";
        public static final String COLUMN_NAME_INVOICE_ID = "invoiceId";
        public static final String COLUMN_NAME_AMOUNT = "amount";

        public static final String CREATE_TABLE = "CREATE TABLE " +
                TABLE_NAME + " (" +
                COLUMN_NAME_ID + " TEXT PRIMARY KEY," +
                COLUMN_NAME_COMPANY_ID + " TEXT NOT NULL," +
                COLUMN_NAME_DATE + " TEXT NOT NULL," +
                COLUMN_NAME_DESCRIPTION + " TEXT NOT NULL," +
                COLUMN_NAME_INVOICE_ID + " TEXT UNIQUE," +
                COLUMN_NAME_AMOUNT + " REAL," +
                "FOREIGN KEY(" + COLUMN_NAME_COMPANY_ID + ") REFERENCES " + CompanyEntry.TABLE_NAME + "(" + CompanyEntry.COLUMN_NAME_ID + ") ON DELETE CASCADE," +
                "FOREIGN KEY(" + COLUMN_NAME_INVOICE_ID + ") REFERENCES " + InvoiceEntry.TABLE_NAME + "(" + InvoiceEntry.COLUMN_NAME_ID + ") ON DELETE SET NULL)";

        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    public static abstract class JournalEntryItemEntry implements BaseColumns {
        public static final String TABLE_NAME = "journal_entry_item";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_JOURNAL_ENTRY_ID = "journalEntryId";
        public static final String COLUMN_NAME_ACCOUNT_ID = "accountId";
        public static final String COLUMN_NAME_TYPE = "type";
        public static final String COLUMN_NAME_AMOUNT = "amount";

        public static final String CREATE_TABLE = "CREATE TABLE " +
                TABLE_NAME + " (" +
                COLUMN_NAME_ID + " TEXT PRIMARY KEY," +
                COLUMN_NAME_JOURNAL_ENTRY_ID + " TEXT NOT NULL," +
                COLUMN_NAME_ACCOUNT_ID + " TEXT NOT NULL," +
                COLUMN_NAME_TYPE + " TEXT NOT NULL," +
                COLUMN_NAME_AMOUNT + " REAL NOT NULL," +
                "FOREIGN KEY(" + COLUMN_NAME_JOURNAL_ENTRY_ID + ") REFERENCES " + JournalEntryEntry.TABLE_NAME + "(" + JournalEntryEntry.COLUMN_NAME_ID + ") ON DELETE CASCADE," +
                "FOREIGN KEY(" + COLUMN_NAME_ACCOUNT_ID + ") REFERENCES " + AccountEntry.TABLE_NAME + "(" + AccountEntry.COLUMN_NAME_ID + ") ON DELETE CASCADE)";

        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    public static abstract class ReminderEntry implements BaseColumns {
        public static final String TABLE_NAME = "reminder";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_COMPANY_ID = "companyId";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_DESCRIPTION = "description";
        public static final String COLUMN_NAME_DUE_DATE = "dueDate";
        public static final String COLUMN_NAME_PRIORITY = "priority";
        public static final String COLUMN_NAME_IS_COMPLETED = "isCompleted";
        public static final String COLUMN_NAME_IS_EXECUTED = "isExecuted";
        public static final String COLUMN_NAME_ACTION_TYPE = "actionType";
        public static final String COLUMN_NAME_ACTION_PAYLOAD = "actionPayload"; // JSON string
        public static final String COLUMN_NAME_ASSIGNED_TO_ID = "assignedToId";
        public static final String COLUMN_NAME_CREATED_AT = "createdAt";

        public static final String CREATE_TABLE = "CREATE TABLE " +
                TABLE_NAME + " (" +
                COLUMN_NAME_ID + " TEXT PRIMARY KEY," +
                COLUMN_NAME_COMPANY_ID + " TEXT NOT NULL," +
                COLUMN_NAME_TITLE + " TEXT NOT NULL," +
                COLUMN_NAME_DESCRIPTION + " TEXT," +
                COLUMN_NAME_DUE_DATE + " TEXT NOT NULL," +
                COLUMN_NAME_PRIORITY + " TEXT DEFAULT 'MEDIUM'," +
                COLUMN_NAME_IS_COMPLETED + " INTEGER DEFAULT 0," +
                COLUMN_NAME_IS_EXECUTED + " INTEGER DEFAULT 0," +
                COLUMN_NAME_ACTION_TYPE + " TEXT DEFAULT 'NOTIFICATION'," +
                COLUMN_NAME_ACTION_PAYLOAD + " TEXT," +
                COLUMN_NAME_ASSIGNED_TO_ID + " TEXT NOT NULL," +
                COLUMN_NAME_CREATED_AT + " TEXT NOT NULL," +
                "FOREIGN KEY(" + COLUMN_NAME_COMPANY_ID + ") REFERENCES " + CompanyEntry.TABLE_NAME + "(" + CompanyEntry.COLUMN_NAME_ID + ") ON DELETE CASCADE)";

        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    public static abstract class NotificationEntry implements BaseColumns {
        public static final String TABLE_NAME = "notification";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_USER_ID = "userId";
        public static final String COLUMN_NAME_TYPE = "type";
        public static final String COLUMN_NAME_MESSAGE = "message";
        public static final String COLUMN_NAME_IS_READ = "isRead";
        public static final String COLUMN_NAME_ENTITY_ID = "entityId";
        public static final String COLUMN_NAME_CREATED_AT = "createdAt";

        public static final String CREATE_TABLE = "CREATE TABLE " +
                TABLE_NAME + " (" +
                COLUMN_NAME_ID + " TEXT PRIMARY KEY," +
                COLUMN_NAME_USER_ID + " TEXT NOT NULL," +
                COLUMN_NAME_TYPE + " TEXT NOT NULL," +
                COLUMN_NAME_MESSAGE + " TEXT NOT NULL," +
                COLUMN_NAME_IS_READ + " INTEGER DEFAULT 0," +
                COLUMN_NAME_ENTITY_ID + " TEXT," +
                COLUMN_NAME_CREATED_AT + " TEXT NOT NULL," +
                "FOREIGN KEY(" + COLUMN_NAME_USER_ID + ") REFERENCES " + UserEntry.TABLE_NAME + "(" + UserEntry.COLUMN_NAME_ID + ") ON DELETE CASCADE)";

        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    public static abstract class AuditLogEntry implements BaseColumns {
        public static final String TABLE_NAME = "audit_log";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_TIMESTAMP = "timestamp";
        public static final String COLUMN_NAME_USER_ID = "userId";
        public static final String COLUMN_NAME_COMPANY_ID = "companyId";
        public static final String COLUMN_NAME_ACTION = "action";
        public static final String COLUMN_NAME_ENTITY = "entity";
        public static final String COLUMN_NAME_ENTITY_ID = "entityId";
        public static final String COLUMN_NAME_DETAILS = "details"; // JSON string

        public static final String CREATE_TABLE = "CREATE TABLE " +
                TABLE_NAME + " (" +
                COLUMN_NAME_ID + " TEXT PRIMARY KEY," +
                COLUMN_NAME_TIMESTAMP + " TEXT NOT NULL," +
                COLUMN_NAME_USER_ID + " TEXT NOT NULL," +
                COLUMN_NAME_COMPANY_ID + " TEXT NOT NULL," +
                COLUMN_NAME_ACTION + " TEXT NOT NULL," +
                COLUMN_NAME_ENTITY + " TEXT NOT NULL," +
                COLUMN_NAME_ENTITY_ID + " TEXT NOT NULL," +
                COLUMN_NAME_DETAILS + " TEXT," +
                "FOREIGN KEY(" + COLUMN_NAME_USER_ID + ") REFERENCES " + UserEntry.TABLE_NAME + "(" + UserEntry.COLUMN_NAME_ID + ") ON DELETE CASCADE," +
                "FOREIGN KEY(" + COLUMN_NAME_COMPANY_ID + ") REFERENCES " + CompanyEntry.TABLE_NAME + "(" + CompanyEntry.COLUMN_NAME_ID + ") ON DELETE CASCADE)";

        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    public static abstract class CampaignEntry implements BaseColumns {
        public static final String TABLE_NAME = "campaign";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_COMPANY_ID = "companyId";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_CONTENT = "content";
        public static final String COLUMN_NAME_TYPE = "type";
        public static final String COLUMN_NAME_STATUS = "status";
        public static final String COLUMN_NAME_TARGET_AUDIENCE = "targetAudience";
        public static final String COLUMN_NAME_SENT_AT = "sentAt";
        public static final String COLUMN_NAME_CREATED_AT = "createdAt";

        public static final String CREATE_TABLE = "CREATE TABLE " +
                TABLE_NAME + " (" +
                COLUMN_NAME_ID + " TEXT PRIMARY KEY," +
                COLUMN_NAME_COMPANY_ID + " TEXT NOT NULL," +
                COLUMN_NAME_TITLE + " TEXT NOT NULL," +
                COLUMN_NAME_CONTENT + " TEXT NOT NULL," +
                COLUMN_NAME_TYPE + " TEXT NOT NULL," +
                COLUMN_NAME_STATUS + " TEXT NOT NULL," +
                COLUMN_NAME_TARGET_AUDIENCE + " TEXT NOT NULL," +
                COLUMN_NAME_SENT_AT + " TEXT," +
                COLUMN_NAME_CREATED_AT + " TEXT NOT NULL," +
                "FOREIGN KEY(" + COLUMN_NAME_COMPANY_ID + ") REFERENCES " + CompanyEntry.TABLE_NAME + "(" + CompanyEntry.COLUMN_NAME_ID + ") ON DELETE CASCADE)";

        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    public static abstract class DeliveryReceiptEntry implements BaseColumns {
        public static final String TABLE_NAME = "delivery_receipt";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_CAMPAIGN_ID = "campaignId";
        public static final String COLUMN_NAME_COMPANY_ID = "companyId";
        public static final String COLUMN_NAME_CUSTOMER_ID = "customerId";
        public static final String COLUMN_NAME_STATUS = "status";
        public static final String COLUMN_NAME_SENT_AT = "sentAt";

        public static final String CREATE_TABLE = "CREATE TABLE " +
                TABLE_NAME + " (" +
                COLUMN_NAME_ID + " TEXT PRIMARY KEY," +
                COLUMN_NAME_CAMPAIGN_ID + " TEXT NOT NULL," +
                COLUMN_NAME_COMPANY_ID + " TEXT NOT NULL," +
                COLUMN_NAME_CUSTOMER_ID + " TEXT NOT NULL," +
                COLUMN_NAME_STATUS + " TEXT NOT NULL," +
                COLUMN_NAME_SENT_AT + " TEXT NOT NULL," +
                "FOREIGN KEY(" + COLUMN_NAME_CAMPAIGN_ID + ") REFERENCES " + CampaignEntry.TABLE_NAME + "(" + CampaignEntry.COLUMN_NAME_ID + ") ON DELETE CASCADE," +
                "FOREIGN KEY(" + COLUMN_NAME_COMPANY_ID + ") REFERENCES " + CompanyEntry.TABLE_NAME + "(" + CompanyEntry.COLUMN_NAME_ID + ") ON DELETE CASCADE," +
                "FOREIGN KEY(" + COLUMN_NAME_CUSTOMER_ID + ") REFERENCES " + CustomerEntry.TABLE_NAME + "(" + CustomerEntry.COLUMN_NAME_ID + ") ON DELETE CASCADE)";

        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    public static abstract class ConnectionEntry implements BaseColumns {
        public static final String TABLE_NAME = "connection";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_FROM_ID = "fromId";
        public static final String COLUMN_NAME_TO_ID = "toId";
        public static final String COLUMN_NAME_STATUS = "status";
        public static final String COLUMN_NAME_CREATED_AT = "createdAt";

        public static final String CREATE_TABLE = "CREATE TABLE " +
                TABLE_NAME + " (" +
                COLUMN_NAME_ID + " TEXT PRIMARY KEY," +
                COLUMN_NAME_FROM_ID + " TEXT NOT NULL," +
                COLUMN_NAME_TO_ID + " TEXT NOT NULL," +
                COLUMN_NAME_STATUS + " TEXT DEFAULT 'PENDING'," +
                COLUMN_NAME_CREATED_AT + " TEXT NOT NULL," +
                "FOREIGN KEY(" + COLUMN_NAME_FROM_ID + ") REFERENCES " + UserEntry.TABLE_NAME + "(" + UserEntry.COLUMN_NAME_ID + ") ON DELETE CASCADE," +
                "FOREIGN KEY(" + COLUMN_NAME_TO_ID + ") REFERENCES " + UserEntry.TABLE_NAME + "(" + UserEntry.COLUMN_NAME_ID + ") ON DELETE CASCADE," +
                "UNIQUE (" + COLUMN_NAME_FROM_ID + ", " + COLUMN_NAME_TO_ID + "))";

        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    public static abstract class SharedLinkEntry implements BaseColumns {
        public static final String TABLE_NAME = "shared_link";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_COMPANY_ID = "companyId";
        public static final String COLUMN_NAME_TOKEN = "token";
        public static final String COLUMN_NAME_TYPE = "type";
        public static final String COLUMN_NAME_FILTERS = "filters"; // JSON string
        public static final String COLUMN_NAME_PASSWORD = "password";
        public static final String COLUMN_NAME_CREATED_AT = "createdAt";
        public static final String COLUMN_NAME_EXPIRES_AT = "expiresAt";

        public static final String CREATE_TABLE = "CREATE TABLE " +
                TABLE_NAME + " (" +
                COLUMN_NAME_ID + " TEXT PRIMARY KEY," +
                COLUMN_NAME_COMPANY_ID + " TEXT NOT NULL," +
                COLUMN_NAME_TOKEN + " TEXT UNIQUE NOT NULL," +
                COLUMN_NAME_TYPE + " TEXT NOT NULL," +
                COLUMN_NAME_FILTERS + " TEXT," +
                COLUMN_NAME_PASSWORD + " TEXT," +
                COLUMN_NAME_CREATED_AT + " TEXT NOT NULL," +
                COLUMN_NAME_EXPIRES_AT + " TEXT," +
                "FOREIGN KEY(" + COLUMN_NAME_COMPANY_ID + ") REFERENCES " + CompanyEntry.TABLE_NAME + "(" + CompanyEntry.COLUMN_NAME_ID + ") ON DELETE CASCADE)";

        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    public static abstract class CompanySettingsEntry implements BaseColumns {
        public static final String TABLE_NAME = "company_settings";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_COMPANY_ID = "companyId";

        public static final String CREATE_TABLE = "CREATE TABLE " +
                TABLE_NAME + " (" +
                COLUMN_NAME_ID + " TEXT PRIMARY KEY," +
                COLUMN_NAME_COMPANY_ID + " TEXT UNIQUE NOT NULL," +
                "FOREIGN KEY(" + COLUMN_NAME_COMPANY_ID + ") REFERENCES " + CompanyEntry.TABLE_NAME + "(" + CompanyEntry.COLUMN_NAME_ID + ") ON DELETE CASCADE)";

        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    public static abstract class PointTransactionEntry implements BaseColumns {
        public static final String TABLE_NAME = "point_transaction";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_USER_ID = "userId";
        public static final String COLUMN_NAME_ACTION = "action";
        public static final String COLUMN_NAME_POINTS = "points";
        public static final String COLUMN_NAME_DETAILS = "details"; // JSON string
        public static final String COLUMN_NAME_CREATED_AT = "createdAt";

        public static final String CREATE_TABLE = "CREATE TABLE " +
                TABLE_NAME + " (" +
                COLUMN_NAME_ID + " TEXT PRIMARY KEY," +
                COLUMN_NAME_USER_ID + " TEXT NOT NULL," +
                COLUMN_NAME_ACTION + " TEXT NOT NULL," +
                COLUMN_NAME_POINTS + " INTEGER NOT NULL," +
                COLUMN_NAME_DETAILS + " TEXT," +
                COLUMN_NAME_CREATED_AT + " TEXT NOT NULL," +
                "FOREIGN KEY(" + COLUMN_NAME_USER_ID + ") REFERENCES " + UserEntry.TABLE_NAME + "(" + UserEntry.COLUMN_NAME_ID + ") ON DELETE CASCADE)";

        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    public static abstract class RewardEntry implements BaseColumns {
        public static final String TABLE_NAME = "reward";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_DESCRIPTION = "description";
        public static final String COLUMN_NAME_POINTS_COST = "pointsCost";
        public static final String COLUMN_NAME_IS_ACTIVE = "isActive";

        public static final String CREATE_TABLE = "CREATE TABLE " +
                TABLE_NAME + " (" +
                COLUMN_NAME_ID + " TEXT PRIMARY KEY," +
                COLUMN_NAME_NAME + " TEXT NOT NULL," +
                COLUMN_NAME_DESCRIPTION + " TEXT NOT NULL," +
                COLUMN_NAME_POINTS_COST + " INTEGER NOT NULL," +
                COLUMN_NAME_IS_ACTIVE + " INTEGER DEFAULT 1)";

        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    public static abstract class UserRewardEntry implements BaseColumns {
        public static final String TABLE_NAME = "user_reward";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_USER_ID = "userId";
        public static final String COLUMN_NAME_REWARD_ID = "rewardId";
        public static final String COLUMN_NAME_REDEEMED_AT = "redeemedAt";

        public static final String CREATE_TABLE = "CREATE TABLE " +
                TABLE_NAME + " (" +
                COLUMN_NAME_ID + " TEXT PRIMARY KEY," +
                COLUMN_NAME_USER_ID + " TEXT NOT NULL," +
                COLUMN_NAME_REWARD_ID + " TEXT NOT NULL," +
                COLUMN_NAME_REDEEMED_AT + " TEXT NOT NULL," +
                "FOREIGN KEY(" + COLUMN_NAME_USER_ID + ") REFERENCES " + UserEntry.TABLE_NAME + "(" + UserEntry.COLUMN_NAME_ID + ") ON DELETE CASCADE," +
                "FOREIGN KEY(" + COLUMN_NAME_REWARD_ID + ") REFERENCES " + RewardEntry.TABLE_NAME + "(" + RewardEntry.COLUMN_NAME_ID + ") ON DELETE CASCADE)";

        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    public static abstract class EmployeeEntry implements BaseColumns {
        public static final String TABLE_NAME = "employee";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_COMPANY_ID = "companyId";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_POSITION = "position";
        public static final String COLUMN_NAME_HIRE_DATE = "hireDate";
        public static final String COLUMN_NAME_SALARY = "salary";
        public static final String COLUMN_NAME_CONTRACT_TYPE = "contractType";

        public static final String CREATE_TABLE = "CREATE TABLE " +
                TABLE_NAME + " (" +
                COLUMN_NAME_ID + " TEXT PRIMARY KEY," +
                COLUMN_NAME_COMPANY_ID + " TEXT NOT NULL," +
                COLUMN_NAME_NAME + " TEXT NOT NULL," +
                COLUMN_NAME_POSITION + " TEXT NOT NULL," +
                COLUMN_NAME_HIRE_DATE + " TEXT NOT NULL," +
                COLUMN_NAME_SALARY + " REAL NOT NULL," +
                COLUMN_NAME_CONTRACT_TYPE + " TEXT NOT NULL," +
                "FOREIGN KEY(" + COLUMN_NAME_COMPANY_ID + ") REFERENCES " + CompanyEntry.TABLE_NAME + "(" + CompanyEntry.COLUMN_NAME_ID + ") ON DELETE CASCADE)";

        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    public static abstract class PayrollEntry implements BaseColumns {
        public static final String TABLE_NAME = "payroll";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_COMPANY_ID = "companyId";
        public static final String COLUMN_NAME_YEAR = "year";
        public static final String COLUMN_NAME_MONTH = "month";
        public static final String COLUMN_NAME_STATUS = "status";
        public static final String COLUMN_NAME_TOTAL_SALARY = "totalSalary";
        public static final String COLUMN_NAME_TOTAL_DEDUCTIONS = "totalDeductions";
        public static final String COLUMN_NAME_TOTAL_BONUSES = "totalBonuses";
        public static final String COLUMN_NAME_NET_PAYABLE = "netPayable";
        public static final String COLUMN_NAME_JOURNAL_ENTRY_ID = "journalEntryId";

        public static final String CREATE_TABLE = "CREATE TABLE " +
                TABLE_NAME + " (" +
                COLUMN_NAME_ID + " TEXT PRIMARY KEY," +
                COLUMN_NAME_COMPANY_ID + " TEXT NOT NULL," +
                COLUMN_NAME_YEAR + " INTEGER NOT NULL," +
                COLUMN_NAME_MONTH + " INTEGER NOT NULL," +
                COLUMN_NAME_STATUS + " TEXT NOT NULL," +
                COLUMN_NAME_TOTAL_SALARY + " REAL NOT NULL," +
                COLUMN_NAME_TOTAL_DEDUCTIONS + " REAL NOT NULL," +
                COLUMN_NAME_TOTAL_BONUSES + " REAL NOT NULL," +
                COLUMN_NAME_NET_PAYABLE + " REAL NOT NULL," +
                COLUMN_NAME_JOURNAL_ENTRY_ID + " TEXT UNIQUE," +
                "FOREIGN KEY(" + COLUMN_NAME_COMPANY_ID + ") REFERENCES " + CompanyEntry.TABLE_NAME + "(" + CompanyEntry.COLUMN_NAME_ID + ") ON DELETE CASCADE," +
                "FOREIGN KEY(" + COLUMN_NAME_JOURNAL_ENTRY_ID + ") REFERENCES " + JournalEntryEntry.TABLE_NAME + "(" + JournalEntryEntry.COLUMN_NAME_ID + ") ON DELETE SET NULL)";

        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    public static abstract class PayrollItemEntry implements BaseColumns {
        public static final String TABLE_NAME = "payroll_item";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_PAYROLL_ID = "payrollId";
        public static final String COLUMN_NAME_EMPLOYEE_ID = "employeeId";
        public static final String COLUMN_NAME_BASE_SALARY = "baseSalary";
        public static final String COLUMN_NAME_NET_SALARY = "netSalary";

        public static final String CREATE_TABLE = "CREATE TABLE " +
                TABLE_NAME + " (" +
                COLUMN_NAME_ID + " TEXT PRIMARY KEY," +
                COLUMN_NAME_PAYROLL_ID + " TEXT NOT NULL," +
                COLUMN_NAME_EMPLOYEE_ID + " TEXT NOT NULL," +
                COLUMN_NAME_BASE_SALARY + " REAL NOT NULL," +
                COLUMN_NAME_NET_SALARY + " REAL NOT NULL," +
                "FOREIGN KEY(" + COLUMN_NAME_PAYROLL_ID + ") REFERENCES " + PayrollEntry.TABLE_NAME + "(" + PayrollEntry.COLUMN_NAME_ID + ") ON DELETE CASCADE," +
                "FOREIGN KEY(" + COLUMN_NAME_EMPLOYEE_ID + ") REFERENCES " + EmployeeEntry.TABLE_NAME + "(" + EmployeeEntry.COLUMN_NAME_ID + ") ON DELETE CASCADE)";

        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    public static abstract class ServiceEntry implements BaseColumns {
        public static final String TABLE_NAME = "service";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_COMPANY_ID = "companyId";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_CATEGORY = "category";
        public static final String COLUMN_NAME_PRICE = "price";
        public static final String COLUMN_NAME_DESCRIPTION = "description";

        public static final String CREATE_TABLE = "CREATE TABLE " +
                TABLE_NAME + " (" +
                COLUMN_NAME_ID + " TEXT PRIMARY KEY," +
                COLUMN_NAME_COMPANY_ID + " TEXT NOT NULL," +
                COLUMN_NAME_NAME + " TEXT NOT NULL," +
                COLUMN_NAME_CATEGORY + " TEXT NOT NULL," +
                COLUMN_NAME_PRICE + " REAL NOT NULL," +
                COLUMN_NAME_DESCRIPTION + " TEXT," +
                "FOREIGN KEY(" + COLUMN_NAME_COMPANY_ID + ") REFERENCES " + CompanyEntry.TABLE_NAME + "(" + CompanyEntry.COLUMN_NAME_ID + ") ON DELETE CASCADE)";

        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    public static abstract class DoctorEntry implements BaseColumns {
        public static final String TABLE_NAME = "doctor";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_COMPANY_ID = "companyId";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_SPECIALTY = "specialty";

        public static final String CREATE_TABLE = "CREATE TABLE " +
                TABLE_NAME + " (" +
                COLUMN_NAME_ID + " TEXT PRIMARY KEY," +
                COLUMN_NAME_COMPANY_ID + " TEXT NOT NULL," +
                COLUMN_NAME_NAME + " TEXT NOT NULL," +
                COLUMN_NAME_SPECIALTY + " TEXT NOT NULL," +
                "FOREIGN KEY(" + COLUMN_NAME_COMPANY_ID + ") REFERENCES " + CompanyEntry.TABLE_NAME + "(" + CompanyEntry.COLUMN_NAME_ID + ") ON DELETE CASCADE)";

        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    public static abstract class VoucherEntry implements BaseColumns {
        public static final String TABLE_NAME = "voucher";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_COMPANY_ID = "companyId";
        public static final String COLUMN_NAME_TYPE = "type";
        public static final String COLUMN_NAME_DATE = "date";
        public static final String COLUMN_NAME_AMOUNT = "amount";
        public static final String COLUMN_NAME_DESCRIPTION = "description";
        public static final String COLUMN_NAME_JOURNAL_ENTRY_ID = "journalEntryId";

        public static final String CREATE_TABLE = "CREATE TABLE " +
                TABLE_NAME + " (" +
                COLUMN_NAME_ID + " TEXT PRIMARY KEY," +
                COLUMN_NAME_COMPANY_ID + " TEXT NOT NULL," +
                COLUMN_NAME_TYPE + " TEXT NOT NULL," +
                COLUMN_NAME_DATE + " TEXT NOT NULL," +
                COLUMN_NAME_AMOUNT + " REAL NOT NULL," +
                COLUMN_NAME_DESCRIPTION + " TEXT NOT NULL," +
                COLUMN_NAME_JOURNAL_ENTRY_ID + " TEXT UNIQUE," +
                "FOREIGN KEY(" + COLUMN_NAME_COMPANY_ID + ") REFERENCES " + CompanyEntry.TABLE_NAME + "(" + CompanyEntry.COLUMN_NAME_ID + ") ON DELETE CASCADE," +
                "FOREIGN KEY(" + COLUMN_NAME_JOURNAL_ENTRY_ID + ") REFERENCES " + JournalEntryEntry.TABLE_NAME + "(" + JournalEntryEntry.COLUMN_NAME_ID + ") ON DELETE SET NULL)";

        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    public static abstract class FinancialTransferEntry implements BaseColumns {
        public static final String TABLE_NAME = "financial_transfer";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_COMPANY_ID = "companyId";
        public static final String COLUMN_NAME_FROM_CASH_BOX_ID = "fromCashBoxId";
        public static final String COLUMN_NAME_TO_CASH_BOX_ID = "toCashBoxId";
        public static final String COLUMN_NAME_AMOUNT = "amount";
        public static final String COLUMN_NAME_COMMISSION = "commission";
        public static final String COLUMN_NAME_DATE = "date";
        public static final String COLUMN_NAME_JOURNAL_ENTRY_ID = "journalEntryId";

        public static final String CREATE_TABLE = "CREATE TABLE " +
                TABLE_NAME + " (" +
                COLUMN_NAME_ID + " TEXT PRIMARY KEY," +
                COLUMN_NAME_COMPANY_ID + " TEXT NOT NULL," +
                COLUMN_NAME_FROM_CASH_BOX_ID + " TEXT NOT NULL," +
                COLUMN_NAME_TO_CASH_BOX_ID + " TEXT NOT NULL," +
                COLUMN_NAME_AMOUNT + " REAL NOT NULL," +
                COLUMN_NAME_COMMISSION + " REAL," +
                COLUMN_NAME_DATE + " TEXT NOT NULL," +
                COLUMN_NAME_JOURNAL_ENTRY_ID + " TEXT UNIQUE," +
                "FOREIGN KEY(" + COLUMN_NAME_COMPANY_ID + ") REFERENCES " + CompanyEntry.TABLE_NAME + "(" + CompanyEntry.COLUMN_NAME_ID + ") ON DELETE CASCADE," +
                "FOREIGN KEY(" + COLUMN_NAME_JOURNAL_ENTRY_ID + ") REFERENCES " + JournalEntryEntry.TABLE_NAME + "(" + JournalEntryEntry.COLUMN_NAME_ID + ") ON DELETE SET NULL)";

        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    public static abstract class CurrencyExchangeEntry implements BaseColumns {
        public static final String TABLE_NAME = "currency_exchange";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_COMPANY_ID = "companyId";
        public static final String COLUMN_NAME_FROM_CURRENCY_ID = "fromCurrencyId";
        public static final String COLUMN_NAME_FROM_AMOUNT = "fromAmount";
        public static final String COLUMN_NAME_TO_CURRENCY_ID = "toCurrencyId";
        public static final String COLUMN_NAME_TO_AMOUNT = "toAmount";
        public static final String COLUMN_NAME_RATE = "rate";
        public static final String COLUMN_NAME_DATE = "date";
        public static final String COLUMN_NAME_JOURNAL_ENTRY_ID = "journalEntryId";

        public static final String CREATE_TABLE = "CREATE TABLE " +
                TABLE_NAME + " (" +
                COLUMN_NAME_ID + " TEXT PRIMARY KEY," +
                COLUMN_NAME_COMPANY_ID + " TEXT NOT NULL," +
                COLUMN_NAME_FROM_CURRENCY_ID + " TEXT NOT NULL," +
                COLUMN_NAME_FROM_AMOUNT + " REAL NOT NULL," +
                COLUMN_NAME_TO_CURRENCY_ID + " TEXT NOT NULL," +
                COLUMN_NAME_TO_AMOUNT + " REAL NOT NULL," +
                COLUMN_NAME_RATE + " REAL NOT NULL," +
                COLUMN_NAME_DATE + " TEXT NOT NULL," +
                COLUMN_NAME_JOURNAL_ENTRY_ID + " TEXT UNIQUE," +
                "FOREIGN KEY(" + COLUMN_NAME_COMPANY_ID + ") REFERENCES " + CompanyEntry.TABLE_NAME + "(" + CompanyEntry.COLUMN_NAME_ID + ") ON DELETE CASCADE," +
                "FOREIGN KEY(" + COLUMN_NAME_JOURNAL_ENTRY_ID + ") REFERENCES " + JournalEntryEntry.TABLE_NAME + "(" + JournalEntryEntry.COLUMN_NAME_ID + ") ON DELETE SET NULL)";

        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    public static abstract class JoinRequestEntry implements BaseColumns {
        public static final String TABLE_NAME = "join_request";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_USER_ID = "userId";
        public static final String COLUMN_NAME_COMPANY_ID = "companyId";
        public static final String COLUMN_NAME_STATUS = "status";
        public static final String COLUMN_NAME_CREATED_AT = "createdAt";

        public static final String CREATE_TABLE = "CREATE TABLE " +
                TABLE_NAME + " (" +
                COLUMN_NAME_ID + " TEXT PRIMARY KEY," +
                COLUMN_NAME_USER_ID + " TEXT NOT NULL," +
                COLUMN_NAME_COMPANY_ID + " TEXT NOT NULL," +
                COLUMN_NAME_STATUS + " TEXT DEFAULT 'pending'," +
                COLUMN_NAME_CREATED_AT + " TEXT NOT NULL," +
                "FOREIGN KEY(" + COLUMN_NAME_USER_ID + ") REFERENCES " + UserEntry.TABLE_NAME + "(" + UserEntry.COLUMN_NAME_ID + ") ON DELETE CASCADE," +
                "FOREIGN KEY(" + COLUMN_NAME_COMPANY_ID + ") REFERENCES " + CompanyEntry.TABLE_NAME + "(" + CompanyEntry.COLUMN_NAME_ID + ") ON DELETE CASCADE)";

        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    // Enums
    public enum PointAction {
        USER_REGISTRATION,
        REFERRAL_SUCCESS,
        FIRST_INVOICE,
        DAILY_LOGIN,
        REDEEM_REWARD
    }

    public enum VoucherType {
        RECEIPT,
        PAYMENT
    }
}

