package com.example.accountingapp.advanced;

import android.content.Context;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.*;

public class DataMerger {
    private static final String TAG = "DataMerger";
    
    private static DataMerger instance;
    private Context context;
    
    private DataMerger(Context context) {
        this.context = context.getApplicationContext();
    }
    
    public static synchronized DataMerger getInstance(Context context) {
        if (instance == null) {
            instance = new DataMerger(context);
        }
        return instance;
    }
    
    // دمج الحسابات مع الحفاظ على البيانات الحالية
    public void mergeAccounts(JSONArray backupAccounts) throws JSONException {
        Log.d(TAG, "بدء دمج الحسابات");
        
        Set<String> existingAccountIds = getExistingAccountIds();
        List<JSONObject> accountsToAdd = new ArrayList<>();
        List<JSONObject> accountsToUpdate = new ArrayList<>();
        
        for (int i = 0; i < backupAccounts.length(); i++) {
            JSONObject backupAccount = backupAccounts.getJSONObject(i);
            String accountId = backupAccount.getString("id");
            
            if (existingAccountIds.contains(accountId)) {
                // حساب موجود - فحص للتحديث
                if (shouldUpdateAccount(accountId, backupAccount)) {
                    accountsToUpdate.add(backupAccount);
                }
            } else {
                // حساب جديد - إضافة
                accountsToAdd.add(backupAccount);
            }
        }
        
        // تطبيق التغييرات
        addNewAccounts(accountsToAdd);
        updateExistingAccounts(accountsToUpdate);
        
        // تسجيل النتائج
        ActivityLogManager.getInstance(context).logActivity(
            "DATA_MERGE",
            String.format("دمج الحسابات: %d جديد، %d محدث", 
                         accountsToAdd.size(), accountsToUpdate.size()),
            ActivityLogManager.PRIORITY_MEDIUM
        );
        
        Log.d(TAG, "انتهاء دمج الحسابات");
    }
    
    // دمج المعاملات
    public void mergeTransactions(JSONArray backupTransactions) throws JSONException {
        Log.d(TAG, "بدء دمج المعاملات");
        
        Set<String> existingTransactionIds = getExistingTransactionIds();
        List<JSONObject> transactionsToAdd = new ArrayList<>();
        int duplicatesSkipped = 0;
        int conflictsResolved = 0;
        
        for (int i = 0; i < backupTransactions.length(); i++) {
            JSONObject backupTransaction = backupTransactions.getJSONObject(i);
            String transactionId = backupTransaction.getString("id");
            
            if (existingTransactionIds.contains(transactionId)) {
                // معاملة موجودة - فحص التعارض
                MergeResult result = resolveTransactionConflict(transactionId, backupTransaction);
                
                if (result == MergeResult.UPDATED) {
                    conflictsResolved++;
                } else if (result == MergeResult.SKIPPED) {
                    duplicatesSkipped++;
                }
            } else {
                // معاملة جديدة
                transactionsToAdd.add(backupTransaction);
            }
        }
        
        // إضافة المعاملات الجديدة
        addNewTransactions(transactionsToAdd);
        
        // تسجيل النتائج
        ActivityLogManager.getInstance(context).logActivity(
            "DATA_MERGE",
            String.format("دمج المعاملات: %d جديد، %d تعارض محلول، %d متجاهل", 
                         transactionsToAdd.size(), conflictsResolved, duplicatesSkipped),
            ActivityLogManager.PRIORITY_MEDIUM
        );
        
        Log.d(TAG, "انتهاء دمج المعاملات");
    }
    
    // دمج الفئات
    public void mergeCategories(JSONArray backupCategories) throws JSONException {
        Log.d(TAG, "بدء دمج الفئات");
        
        Map<String, JSONObject> existingCategories = getExistingCategoriesMap();
        List<JSONObject> categoriesToAdd = new ArrayList<>();
        List<JSONObject> categoriesToUpdate = new ArrayList<>();
        
        for (int i = 0; i < backupCategories.length(); i++) {
            JSONObject backupCategory = backupCategories.getJSONObject(i);
            String categoryName = backupCategory.getString("name");
            
            if (existingCategories.containsKey(categoryName)) {
                // فئة موجودة - دمج الخصائص
                JSONObject mergedCategory = mergeCategoryProperties(
                    existingCategories.get(categoryName), 
                    backupCategory
                );
                categoriesToUpdate.add(mergedCategory);
            } else {
                // فئة جديدة
                categoriesToAdd.add(backupCategory);
            }
        }
        
        // تطبيق التغييرات
        addNewCategories(categoriesToAdd);
        updateExistingCategories(categoriesToUpdate);
        
        Log.d(TAG, "انتهاء دمج الفئات");
    }
    
    // دمج التقارير
    public void mergeReports(JSONArray backupReports) throws JSONException {
        Log.d(TAG, "بدء دمج التقارير");
        
        Set<String> existingReportIds = getExistingReportIds();
        List<JSONObject> reportsToAdd = new ArrayList<>();
        
        for (int i = 0; i < backupReports.length(); i++) {
            JSONObject backupReport = backupReports.getJSONObject(i);
            String reportId = backupReport.optString("id", "");
            
            // التقارير عادة لا تدمج، بل تضاف كنسخ جديدة إذا لم تكن موجودة
            if (!existingReportIds.contains(reportId)) {
                // إعادة توليد معرف جديد للتقرير لتجنب التعارض
                backupReport.put("id", generateNewReportId());
                backupReport.put("imported_from_backup", true);
                backupReport.put("import_date", System.currentTimeMillis());
                
                reportsToAdd.add(backupReport);
            }
        }
        
        // إضافة التقارير الجديدة
        addNewReports(reportsToAdd);
        
        Log.d(TAG, "انتهاء دمج التقارير");
    }
    
    // دمج سجل الأنشطة
    public void mergeActivityLog(JSONArray backupActivityLog) throws JSONException {
        Log.d(TAG, "بدء دمج سجل الأنشطة");
        
        ActivityLogManager activityManager = ActivityLogManager.getInstance(context);
        
        // إضافة جميع أنشطة النسخة الاحتياطية مع تمييزها
        for (int i = 0; i < backupActivityLog.length(); i++) {
            JSONObject activity = backupActivityLog.getJSONObject(i);
            
            // إضافة معلومات الاستيراد
            activity.put("imported_from_backup", true);
            activity.put("import_timestamp", System.currentTimeMillis());
            
            // إضافة النشاط
            activityManager.importActivity(activity);
        }
        
        // تسجيل عملية الدمج
        activityManager.logActivity(
            "ACTIVITY_LOG_MERGE",
            "تم دمج " + backupActivityLog.length() + " نشاط من النسخة الاحتياطية",
            ActivityLogManager.PRIORITY_LOW
        );
        
        Log.d(TAG, "انتهاء دمج سجل الأنشطة");
    }
    
    // حل تعارض المعاملات
    private MergeResult resolveTransactionConflict(String transactionId, JSONObject backupTransaction) {
        try {
            JSONObject existingTransaction = getExistingTransaction(transactionId);
            
            // مقارنة التواريخ والمبالغ
            long existingDate = existingTransaction.getLong("date");
            long backupDate = backupTransaction.getLong("date");
            double existingAmount = existingTransaction.getDouble("amount");
            double backupAmount = backupTransaction.getDouble("amount");
            
            // إذا كانت البيانات متطابقة، تجاهل
            if (existingDate == backupDate && existingAmount == backupAmount) {
                return MergeResult.SKIPPED;
            }
            
            // إذا كانت النسخة الاحتياطية أحدث، حدث
            if (backupTransaction.getLong("last_modified") > 
                existingTransaction.optLong("last_modified", 0)) {
                
                updateExistingTransaction(transactionId, backupTransaction);
                return MergeResult.UPDATED;
            }
            
            // خلاف ذلك، احتفظ بالحالي
            return MergeResult.SKIPPED;
            
        } catch (Exception e) {
            Log.e(TAG, "خطأ في حل تعارض المعاملة: " + transactionId, e);
            return MergeResult.ERROR;
        }
    }
    
    // دمج خصائص الفئات
    private JSONObject mergeCategoryProperties(JSONObject existing, JSONObject backup) 
            throws JSONException {
        
        JSONObject merged = new JSONObject(existing.toString());
        
        // دمج الألوان والرموز إذا لم تكن موجودة
        if (!merged.has("color") && backup.has("color")) {
            merged.put("color", backup.getString("color"));
        }
        
        if (!merged.has("icon") && backup.has("icon")) {
            merged.put("icon", backup.getString("icon"));
        }
        
        // دمج الوصف إذا كان أطول في النسخة الاحتياطية
        if (backup.has("description")) {
            String backupDesc = backup.getString("description");
            String existingDesc = merged.optString("description", "");
            
            if (backupDesc.length() > existingDesc.length()) {
                merged.put("description", backupDesc);
            }
        }
        
        // تحديث وقت آخر تعديل
        merged.put("last_modified", System.currentTimeMillis());
        merged.put("merged_from_backup", true);
        
        return merged;
    }
    
    // الحصول على معرفات الحسابات الموجودة
    private Set<String> getExistingAccountIds() {
        // هنا يجب جلب البيانات من قاعدة البيانات الفعلية
        return new HashSet<>();
    }
    
    // الحصول على معرفات المعاملات الموجودة
    private Set<String> getExistingTransactionIds() {
        return new HashSet<>();
    }
    
    // الحصول على معرفات التقارير الموجودة
    private Set<String> getExistingReportIds() {
        return new HashSet<>();
    }
    
    // الحصول على خريطة الفئات الموجودة
    private Map<String, JSONObject> getExistingCategoriesMap() {
        return new HashMap<>();
    }
    
    // فحص ما إذا كان يجب تحديث الحساب
    private boolean shouldUpdateAccount(String accountId, JSONObject backupAccount) {
        // منطق مقارنة التواريخ والتحديثات
        return false;
    }
    
    // الحصول على المعاملة الموجودة
    private JSONObject getExistingTransaction(String transactionId) throws JSONException {
        return new JSONObject();
    }
    
    // إضافة حسابات جديدة
    private void addNewAccounts(List<JSONObject> accounts) {
        for (JSONObject account : accounts) {
            // إضافة الحساب إلى قاعدة البيانات
            Log.d(TAG, "إضافة حساب جديد: " + account.optString("name", ""));
        }
    }
    
    // تحديث الحسابات الموجودة
    private void updateExistingAccounts(List<JSONObject> accounts) {
        for (JSONObject account : accounts) {
            // تحديث الحساب في قاعدة البيانات
            Log.d(TAG, "تحديث حساب موجود: " + account.optString("name", ""));
        }
    }
    
    // إضافة معاملات جديدة
    private void addNewTransactions(List<JSONObject> transactions) {
        for (JSONObject transaction : transactions) {
            Log.d(TAG, "إضافة معاملة جديدة");
        }
    }
    
    // تحديث معاملة موجودة
    private void updateExistingTransaction(String id, JSONObject transaction) {
        Log.d(TAG, "تحديث معاملة موجودة: " + id);
    }
    
    // إضافة فئات جديدة
    private void addNewCategories(List<JSONObject> categories) {
        for (JSONObject category : categories) {
            Log.d(TAG, "إضافة فئة جديدة: " + category.optString("name", ""));
        }
    }
    
    // تحديث الفئات الموجودة
    private void updateExistingCategories(List<JSONObject> categories) {
        for (JSONObject category : categories) {
            Log.d(TAG, "تحديث فئة موجودة: " + category.optString("name", ""));
        }
    }
    
    // إضافة تقارير جديدة
    private void addNewReports(List<JSONObject> reports) {
        for (JSONObject report : reports) {
            Log.d(TAG, "إضافة تقرير جديد");
        }
    }
    
    // توليد معرف تقرير جديد
    private String generateNewReportId() {
        return "report_" + System.currentTimeMillis() + "_" + 
               (int)(Math.random() * 1000);
    }
    
    // تعداد نتائج الدمج
    private enum MergeResult {
        ADDED,
        UPDATED,
        SKIPPED,
        ERROR
    }
}
