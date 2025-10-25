package com.example.androidapp.services;

import android.util.Log;

import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.entities.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * مدير سياق قاعدة البيانات للذكاء الاصطناعي
 * Database Context Manager for AI services
 */
public class DatabaseContextManager {
    
    private static final String TAG = "DatabaseContextManager";
    private AppDatabase database;
    private ExecutorService executorService;
    
    public DatabaseContextManager(AppDatabase database) {
        this.database = database;
        this.executorService = Executors.newSingleThreadExecutor();
    }
    
    /**
     * بناء السياق الكامل لقاعدة البيانات
     */
    public String buildFullDatabaseContext(String userId) {
        try {
            JSONObject context = new JSONObject();
            
            // معلومات المستخدم والشركة
            context.put("user_info", buildUserContext(userId));
            
            // الإحصائيات العامة
            context.put("statistics", buildStatisticsContext(userId));
            
            // العملاء والموردين
            context.put("customers", buildCustomersContext(userId));
            context.put("suppliers", buildSuppliersContext(userId));
            
            // المنتجات والمخزون
            context.put("products", buildProductsContext(userId));
            context.put("inventory", buildInventoryContext(userId));
            
            // الفواتير والمدفوعات
            context.put("invoices", buildInvoicesContext(userId));
            context.put("payments", buildPaymentsContext(userId));
            
            // الحسابات والتحليل المالي
            context.put("accounts", buildAccountsContext(userId));
            context.put("financial_analysis", buildFinancialAnalysisContext(userId));
            
            // الأنشطة الأخيرة
            context.put("recent_activities", buildRecentActivitiesContext(userId));
            
            return context.toString(2);
            
        } catch (JSONException e) {
            Log.e(TAG, "Error building database context", e);
            return "{}";
        }
    }
    
    /**
     * جمع البيانات المالية للتحليل
     */
    public FinancialData gatherFinancialData(String userId) {
        try {
            // جمع بيانات الإيرادات
            List<Invoice> invoices = database.invoiceDao().getInvoicesByUser(userId).getValue();
            double totalRevenue = calculateTotalRevenue(invoices);
            
            // جمع بيانات المصروفات
            List<Payment> payments = database.paymentDao().getPaymentsByUser(userId).getValue();
            double totalExpenses = calculateTotalExpenses(payments);
            
            // جمع بيانات الحسابات
            List<Account> accounts = database.accountDao().getAccountsByUser(userId).getValue();
            
            // جمع بيانات المخزون
            List<Item> items = database.itemDao().getItemsByUser(userId).getValue();
            double inventoryValue = calculateInventoryValue(items);
            
            return new FinancialData(
                totalRevenue,
                totalExpenses,
                inventoryValue,
                invoices,
                payments,
                accounts,
                items
            );
            
        } catch (Exception e) {
            Log.e(TAG, "Error gathering financial data", e);
            return new FinancialData(0, 0, 0, null, null, null, null);
        }
    }
    
    /**
     * تحليل أداء الأعمال
     */
    public BusinessPerformanceData analyzeBusinessPerformance(String userId) {
        try {
            // حساب معدلات النمو
            double monthlyGrowth = calculateMonthlyGrowth(userId);
            double yearlyGrowth = calculateYearlyGrowth(userId);
            
            // تحليل أفضل العملاء
            List<Customer> topCustomers = getTopCustomers(userId, 10);
            
            // تحليل أفضل المنتجات
            List<Item> topProducts = getTopSellingProducts(userId, 10);
            
            // تحليل الاتجاهات الموسمية
            SeasonalTrends seasonalTrends = analyzeSeasonalTrends(userId);
            
            return new BusinessPerformanceData(
                monthlyGrowth,
                yearlyGrowth,
                topCustomers,
                topProducts,
                seasonalTrends
            );
            
        } catch (Exception e) {
            Log.e(TAG, "Error analyzing business performance", e);
            return new BusinessPerformanceData(0, 0, null, null, null);
        }
    }
    
    /**
     * تحليل أنماط المعاملات
     */
    public TransactionPattern analyzeTransactionPatterns(String userId) {
        try {
            // تحليل أنماط الفواتير
            InvoicePattern invoicePattern = analyzeInvoicePatterns(userId);
            
            // تحليل أنماط المدفوعات
            PaymentPattern paymentPattern = analyzePaymentPatterns(userId);
            
            // كشف التغيرات المفاجئة
            List<String> suddenChanges = detectSuddenChanges(userId);
            
            return new TransactionPattern(
                invoicePattern,
                paymentPattern,
                suddenChanges
            );
            
        } catch (Exception e) {
            Log.e(TAG, "Error analyzing transaction patterns", e);
            return new TransactionPattern(null, null, null);
        }
    }
    
    private JSONObject buildUserContext(String userId) throws JSONException {
        JSONObject userContext = new JSONObject();
        
        // معلومات المستخدم الأساسية
        userContext.put("user_id", userId);
        userContext.put("registration_date", "2024-01-01"); // يجب جلب التاريخ الفعلي
        userContext.put("last_login", System.currentTimeMillis());
        
        // إعدادات الشركة
        List<Company> companies = database.companyDao().getCompaniesByUser(userId).getValue();
        if (companies != null && !companies.isEmpty()) {
            Company company = companies.get(0);
            userContext.put("company_name", company.getName());
            userContext.put("company_type", company.getType());
        }
        
        return userContext;
    }
    
    private JSONObject buildStatisticsContext(String userId) throws JSONException {
        JSONObject stats = new JSONObject();
        
        // إحصائيات أساسية
        int totalCustomers = database.customerDao().getCustomersByUser(userId).getValue().size();
        int totalSuppliers = database.supplierDao().getSuppliersByUser(userId).getValue().size();
        int totalProducts = database.itemDao().getItemsByUser(userId).getValue().size();
        int totalInvoices = database.invoiceDao().getInvoicesByUser(userId).getValue().size();
        
        stats.put("total_customers", totalCustomers);
        stats.put("total_suppliers", totalSuppliers);
        stats.put("total_products", totalProducts);
        stats.put("total_invoices", totalInvoices);
        
        // إحصائيات هذا الشهر
        long monthStart = getMonthStartTimestamp();
        int monthlyInvoices = getInvoicesCountSince(userId, monthStart);
        double monthlyRevenue = getRevenueSince(userId, monthStart);
        
        stats.put("monthly_invoices", monthlyInvoices);
        stats.put("monthly_revenue", monthlyRevenue);
        
        return stats;
    }
    
    private JSONArray buildCustomersContext(String userId) throws JSONException {
        JSONArray customersArray = new JSONArray();
        
        List<Customer> customers = database.customerDao().getCustomersByUser(userId).getValue();
        if (customers != null) {
            for (Customer customer : customers) {
                JSONObject customerObj = new JSONObject();
                customerObj.put("name", customer.getName());
                customerObj.put("phone", customer.getPhone());
                customerObj.put("email", customer.getEmail());
                customerObj.put("total_purchases", calculateCustomerTotalPurchases(customer.getId()));
                customersArray.put(customerObj);
            }
        }
        
        return customersArray;
    }
    
    private JSONArray buildSuppliersContext(String userId) throws JSONException {
        JSONArray suppliersArray = new JSONArray();
        
        List<Supplier> suppliers = database.supplierDao().getSuppliersByUser(userId).getValue();
        if (suppliers != null) {
            for (Supplier supplier : suppliers) {
                JSONObject supplierObj = new JSONObject();
                supplierObj.put("name", supplier.getName());
                supplierObj.put("phone", supplier.getPhone());
                supplierObj.put("email", supplier.getEmail());
                suppliersArray.put(supplierObj);
            }
        }
        
        return suppliersArray;
    }
    
    private JSONArray buildProductsContext(String userId) throws JSONException {
        JSONArray productsArray = new JSONArray();
        
        List<Item> items = database.itemDao().getItemsByUser(userId).getValue();
        if (items != null) {
            for (Item item : items) {
                JSONObject itemObj = new JSONObject();
                itemObj.put("name", item.getName());
                itemObj.put("price", item.getPrice());
                itemObj.put("quantity", item.getQuantity());
                itemObj.put("category", item.getCategory());
                productsArray.put(itemObj);
            }
        }
        
        return productsArray;
    }
    
    private JSONObject buildInventoryContext(String userId) throws JSONException {
        JSONObject inventory = new JSONObject();
        
        List<Item> items = database.itemDao().getItemsByUser(userId).getValue();
        if (items != null) {
            double totalValue = 0;
            int lowStockItems = 0;
            
            for (Item item : items) {
                totalValue += item.getPrice() * item.getQuantity();
                if (item.getQuantity() < 10) { // حد أدنى للمخزون
                    lowStockItems++;
                }
            }
            
            inventory.put("total_value", totalValue);
            inventory.put("low_stock_items", lowStockItems);
            inventory.put("total_items", items.size());
        }
        
        return inventory;
    }
    
    private JSONArray buildInvoicesContext(String userId) throws JSONException {
        JSONArray invoicesArray = new JSONArray();
        
        List<Invoice> invoices = database.invoiceDao().getRecentInvoicesByUser(userId, 10).getValue();
        if (invoices != null) {
            for (Invoice invoice : invoices) {
                JSONObject invoiceObj = new JSONObject();
                invoiceObj.put("invoice_number", invoice.getInvoiceNumber());
                invoiceObj.put("total_amount", invoice.getTotalAmount());
                invoiceObj.put("status", invoice.getStatus());
                invoiceObj.put("date", invoice.getInvoiceDate());
                invoicesArray.put(invoiceObj);
            }
        }
        
        return invoicesArray;
    }
    
    private JSONArray buildPaymentsContext(String userId) throws JSONException {
        JSONArray paymentsArray = new JSONArray();
        
        List<Payment> payments = database.paymentDao().getRecentPaymentsByUser(userId, 10).getValue();
        if (payments != null) {
            for (Payment payment : payments) {
                JSONObject paymentObj = new JSONObject();
                paymentObj.put("amount", payment.getAmount());
                paymentObj.put("method", payment.getPaymentMethod());
                paymentObj.put("date", payment.getPaymentDate());
                paymentsArray.put(paymentObj);
            }
        }
        
        return paymentsArray;
    }
    
    private JSONArray buildAccountsContext(String userId) throws JSONException {
        JSONArray accountsArray = new JSONArray();
        
        List<Account> accounts = database.accountDao().getAccountsByUser(userId).getValue();
        if (accounts != null) {
            for (Account account : accounts) {
                JSONObject accountObj = new JSONObject();
                accountObj.put("name", account.getName());
                accountObj.put("type", account.getType());
                accountObj.put("balance", account.getBalance());
                accountsArray.put(accountObj);
            }
        }
        
        return accountsArray;
    }
    
    private JSONObject buildFinancialAnalysisContext(String userId) throws JSONException {
        JSONObject analysis = new JSONObject();
        
        // حساب الإجماليات
        double totalRevenue = calculateTotalRevenue(null);
        double totalExpenses = calculateTotalExpenses(null);
        double netProfit = totalRevenue - totalExpenses;
        
        analysis.put("total_revenue", totalRevenue);
        analysis.put("total_expenses", totalExpenses);
        analysis.put("net_profit", netProfit);
        analysis.put("profit_margin", totalRevenue > 0 ? (netProfit / totalRevenue) * 100 : 0);
        
        return analysis;
    }
    
    private JSONArray buildRecentActivitiesContext(String userId) throws JSONException {
        JSONArray activities = new JSONArray();
        
        // جمع الأنشطة الأخيرة
        List<AuditLog> auditLogs = database.auditLogDao().getRecentAuditLogsByUser(userId, 20).getValue();
        if (auditLogs != null) {
            for (AuditLog log : auditLogs) {
                JSONObject activity = new JSONObject();
                activity.put("action", log.getAction());
                activity.put("entity_type", log.getEntityType());
                activity.put("timestamp", log.getTimestamp());
                activities.put(activity);
            }
        }
        
        return activities;
    }
    
    // Helper methods
    private double calculateTotalRevenue(List<Invoice> invoices) {
        if (invoices == null) return 0.0;
        double total = 0.0;
        for (Invoice invoice : invoices) {
            total += invoice.getTotalAmount();
        }
        return total;
    }
    
    private double calculateTotalExpenses(List<Payment> payments) {
        if (payments == null) return 0.0;
        double total = 0.0;
        for (Payment payment : payments) {
            total += payment.getAmount();
        }
        return total;
    }
    
    private double calculateInventoryValue(List<Item> items) {
        if (items == null) return 0.0;
        double total = 0.0;
        for (Item item : items) {
            total += item.getPrice() * item.getQuantity();
        }
        return total;
    }
    
    private double calculateCustomerTotalPurchases(String customerId) {
        // حساب إجمالي مشتريات العميل
        return 0.0; // يجب تنفيذ هذا
    }
    
    private double calculateMonthlyGrowth(String userId) {
        // حساب معدل النمو الشهري
        return 0.0; // يجب تنفيذ هذا
    }
    
    private double calculateYearlyGrowth(String userId) {
        // حساب معدل النمو السنوي
        return 0.0; // يجب تنفيذ هذا
    }
    
    private List<Customer> getTopCustomers(String userId, int limit) {
        // جلب أفضل العملاء
        return null; // يجب تنفيذ هذا
    }
    
    private List<Item> getTopSellingProducts(String userId, int limit) {
        // جلب أفضل المنتجات مبيعاً
        return null; // يجب تنفيذ هذا
    }
    
    private SeasonalTrends analyzeSeasonalTrends(String userId) {
        // تحليل الاتجاهات الموسمية
        return new SeasonalTrends(); // يجب تنفيذ هذا
    }
    
    private InvoicePattern analyzeInvoicePatterns(String userId) {
        // تحليل أنماط الفواتير
        return new InvoicePattern(); // يجب تنفيذ هذا
    }
    
    private PaymentPattern analyzePaymentPatterns(String userId) {
        // تحليل أنماط المدفوعات
        return new PaymentPattern(); // يجب تنفيذ هذا
    }
    
    private List<String> detectSuddenChanges(String userId) {
        // كشف التغيرات المفاجئة
        return new java.util.ArrayList<>(); // يجب تنفيذ هذا
    }
    
    private long getMonthStartTimestamp() {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.set(java.util.Calendar.DAY_OF_MONTH, 1);
        cal.set(java.util.Calendar.HOUR_OF_DAY, 0);
        cal.set(java.util.Calendar.MINUTE, 0);
        cal.set(java.util.Calendar.SECOND, 0);
        cal.set(java.util.Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }
    
    private int getInvoicesCountSince(String userId, long timestamp) {
        // حساب عدد الفواتير منذ تاريخ معين
        return 0; // يجب تنفيذ هذا
    }
    
    private double getRevenueSince(String userId, long timestamp) {
        // حساب الإيرادات منذ تاريخ معين
        return 0.0; // يجب تنفيذ هذا
    }
    
    public void shutdown() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
    
    // Data classes
    public static class FinancialData {
        private double totalRevenue;
        private double totalExpenses;
        private double inventoryValue;
        private List<Invoice> invoices;
        private List<Payment> payments;
        private List<Account> accounts;
        private List<Item> items;
        
        public FinancialData(double totalRevenue, double totalExpenses, double inventoryValue,
                           List<Invoice> invoices, List<Payment> payments, 
                           List<Account> accounts, List<Item> items) {
            this.totalRevenue = totalRevenue;
            this.totalExpenses = totalExpenses;
            this.inventoryValue = inventoryValue;
            this.invoices = invoices;
            this.payments = payments;
            this.accounts = accounts;
            this.items = items;
        }
        
        public String toJSON() {
            try {
                JSONObject json = new JSONObject();
                json.put("total_revenue", totalRevenue);
                json.put("total_expenses", totalExpenses);
                json.put("inventory_value", inventoryValue);
                json.put("net_profit", totalRevenue - totalExpenses);
                return json.toString();
            } catch (JSONException e) {
                return "{}";
            }
        }
        
        // Getters
        public double getTotalRevenue() { return totalRevenue; }
        public double getTotalExpenses() { return totalExpenses; }
        public double getInventoryValue() { return inventoryValue; }
        public List<Invoice> getInvoices() { return invoices; }
        public List<Payment> getPayments() { return payments; }
        public List<Account> getAccounts() { return accounts; }
        public List<Item> getItems() { return items; }
    }
    
    public static class BusinessPerformanceData {
        private double monthlyGrowth;
        private double yearlyGrowth;
        private List<Customer> topCustomers;
        private List<Item> topProducts;
        private SeasonalTrends seasonalTrends;
        
        public BusinessPerformanceData(double monthlyGrowth, double yearlyGrowth,
                                     List<Customer> topCustomers, List<Item> topProducts,
                                     SeasonalTrends seasonalTrends) {
            this.monthlyGrowth = monthlyGrowth;
            this.yearlyGrowth = yearlyGrowth;
            this.topCustomers = topCustomers;
            this.topProducts = topProducts;
            this.seasonalTrends = seasonalTrends;
        }
        
        public String toJSON() {
            try {
                JSONObject json = new JSONObject();
                json.put("monthly_growth", monthlyGrowth);
                json.put("yearly_growth", yearlyGrowth);
                return json.toString();
            } catch (JSONException e) {
                return "{}";
            }
        }
        
        // Getters
        public double getMonthlyGrowth() { return monthlyGrowth; }
        public double getYearlyGrowth() { return yearlyGrowth; }
        public List<Customer> getTopCustomers() { return topCustomers; }
        public List<Item> getTopProducts() { return topProducts; }
        public SeasonalTrends getSeasonalTrends() { return seasonalTrends; }
    }
    
    public static class TransactionPattern {
        private InvoicePattern invoicePattern;
        private PaymentPattern paymentPattern;
        private List<String> suddenChanges;
        
        public TransactionPattern(InvoicePattern invoicePattern, PaymentPattern paymentPattern,
                                List<String> suddenChanges) {
            this.invoicePattern = invoicePattern;
            this.paymentPattern = paymentPattern;
            this.suddenChanges = suddenChanges;
        }
        
        public String toJSON() {
            try {
                JSONObject json = new JSONObject();
                json.put("invoice_pattern", "normal");
                json.put("payment_pattern", "normal");
                json.put("sudden_changes", new JSONArray(suddenChanges));
                return json.toString();
            } catch (JSONException e) {
                return "{}";
            }
        }
        
        // Getters
        public InvoicePattern getInvoicePattern() { return invoicePattern; }
        public PaymentPattern getPaymentPattern() { return paymentPattern; }
        public List<String> getSuddenChanges() { return suddenChanges; }
    }
    
    // Additional data classes
    public static class SeasonalTrends {
        // تنفيذ الاتجاهات الموسمية
    }
    
    public static class InvoicePattern {
        // تنفيذ أنماط الفواتير
    }
    
    public static class PaymentPattern {
        // تنفيذ أنماط المدفوعات
    }
}