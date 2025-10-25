package com.example.androidapp.ui.import_export.models;

/**
 * تعداد أنواع البيانات المتاحة للاستيراد
 * يحدد نوع البيانات والأعمدة المطلوبة لكل نوع
 */
public enum ImportDataType {
    
    CUSTOMERS("العملاء", new String[]{
        "المسلسل", "اسم العميل", "رقم الهاتف", "المنطقة", 
        "له (مديون)", "عليه (دائن)", "الإجمالي", "اللقب", "العنوان", "البريد الإلكتروني"
    }),
    
    SUPPLIERS("الموردين", new String[]{
        "المسلسل", "اسم المورد", "رقم الهاتف", "المنطقة", 
        "له (مديون)", "عليه (دائن)", "الإجمالي", "اللقب", "العنوان", "البريد الإلكتروني"
    }),
    
    ITEMS("الأصناف", new String[]{
        "المسلسل", "اسم الصنف", "الوحدة", "العدد", "سعر الشراء", 
        "سعر البيع", "الكمية", "التاريخ", "الوكيل", "الفئة", "الحد الأدنى"
    }),
    
    ACCOUNTS("الحسابات", new String[]{
        "المسلسل", "اسم الحساب", "نوع الحساب", "الرصيد الافتتاحي", 
        "العملة", "الوصف", "الحالة"
    }),
    
    INVOICES("الفواتير", new String[]{
        "رقم الفاتورة", "تاريخ الفاتورة", "العميل", "نوع الفاتورة", 
        "المبلغ الإجمالي", "المبلغ المدفوع", "المبلغ المتبقي", "الحالة"
    }),
    
    PAYMENTS("المدفوعات", new String[]{
        "رقم المدفوع", "التاريخ", "العميل", "المبلغ", "طريقة الدفع", 
        "الوصف", "رقم الإيصال"
    }),
    
    EMPLOYEES("الموظفين", new String[]{
        "المسلسل", "اسم الموظف", "المنصب", "الراتب الأساسي", 
        "تاريخ التوظيف", "رقم الهاتف", "العنوان", "الحالة"
    });
    
    private final String arabicName;
    private final String[] requiredColumns;
    
    ImportDataType(String arabicName, String[] requiredColumns) {
        this.arabicName = arabicName;
        this.requiredColumns = requiredColumns;
    }
    
    public String getArabicName() {
        return arabicName;
    }
    
    public String[] getRequiredColumns() {
        return requiredColumns;
    }
    
    /**
     * البحث عن نوع البيانات بالاسم العربي
     */
    public static ImportDataType fromArabicName(String arabicName) {
        for (ImportDataType type : values()) {
            if (type.getArabicName().equals(arabicName)) {
                return type;
            }
        }
        return null;
    }
    
    /**
     * الحصول على الحقول المطلوبة في النظام
     */
    public String[] getSystemFields() {
        switch (this) {
            case CUSTOMERS:
            case SUPPLIERS:
                return new String[]{
                    "name", "phone", "area", "debit_balance", "credit_balance", 
                    "total_balance", "nickname", "address", "email"
                };
                
            case ITEMS:
                return new String[]{
                    "name", "unit", "quantity", "purchase_price", "sale_price", 
                    "stock_quantity", "date", "agent", "category", "minimum_stock"
                };
                
            case ACCOUNTS:
                return new String[]{
                    "account_name", "account_type", "opening_balance", 
                    "currency", "description", "status"
                };
                
            case INVOICES:
                return new String[]{
                    "invoice_number", "invoice_date", "customer", "invoice_type", 
                    "total_amount", "paid_amount", "remaining_amount", "status"
                };
                
            case PAYMENTS:
                return new String[]{
                    "payment_number", "payment_date", "customer", "amount", 
                    "payment_method", "description", "receipt_number"
                };
                
            case EMPLOYEES:
                return new String[]{
                    "employee_name", "position", "basic_salary", "hire_date", 
                    "phone", "address", "status"
                };
                
            default:
                return new String[0];
        }
    }
    
    /**
     * الحصول على الحقول الإجبارية
     */
    public String[] getMandatoryFields() {
        switch (this) {
            case CUSTOMERS:
            case SUPPLIERS:
                return new String[]{"name"};
                
            case ITEMS:
                return new String[]{"name", "unit"};
                
            case ACCOUNTS:
                return new String[]{"account_name", "account_type"};
                
            case INVOICES:
                return new String[]{"invoice_number", "customer", "total_amount"};
                
            case PAYMENTS:
                return new String[]{"customer", "amount"};
                
            case EMPLOYEES:
                return new String[]{"employee_name", "position"};
                
            default:
                return new String[0];
        }
    }
}