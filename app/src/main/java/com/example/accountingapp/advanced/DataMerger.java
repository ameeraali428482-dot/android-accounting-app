package com.example.accountingapp.advanced;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataMerger {
    private static final String TAG = "DataMerger";
    
    private Context context;
    
    public DataMerger(Context context) {
        this.context = context.getApplicationContext();
    }
    
    // دمج النسخة الاحتياطية مع البيانات الحالية
    public MergeResult mergeBackupWithCurrentData(JSONObject backupData, JSONObject currentData) {
        MergeResult result = new MergeResult();
        
        try {
            // دمج البيانات بذكاء
            JSONObject mergedData = new JSONObject();
            
            // دمج المعاملات/الفواتير
            result.transactions = mergeTransactions(
                backupData.optJSONArray("transactions"),
                currentData.optJSONArray("transactions")
            );
            
            // دمج الحسابات
            result.accounts = mergeAccounts(
                backupData.optJSONArray("accounts"),
                currentData.optJSONArray("accounts")
            );
            
            // دمج العملاء
            result.customers = mergeCustomers(
                backupData.optJSONArray("customers"),
                currentData.optJSONArray("customers")
            );
            
            // دمج المنتجات
            result.items = mergeItems(
                backupData.optJSONArray("items"),
                currentData.optJSONArray("items")
            );
            
            result.success = true;
            result.message = "تم دمج البيانات بنجاح";
            
            Log.d(TAG, "تم دمج البيانات بنجاح");
            
        } catch (Exception e) {
            result.success = false;
            result.message = "خطأ في دمج البيانات: " + e.getMessage();
            Log.e(TAG, "خطأ في دمج البيانات", e);
        }
        
        return result;
    }
    
    // دمج المعاملات
    private MergeInfo mergeTransactions(JSONArray backupTransactions, JSONArray currentTransactions) {
        MergeInfo mergeInfo = new MergeInfo();
        Map<String, JSONObject> existingTransactions = new HashMap<>();
        
        try {
            // فهرسة المعاملات الحالية
            if (currentTransactions != null) {
                for (int i = 0; i < currentTransactions.length(); i++) {
                    JSONObject transaction = currentTransactions.getJSONObject(i);
                    String id = transaction.optString("id");
                    if (!id.isEmpty()) {
                        existingTransactions.put(id, transaction);
                    }
                }
            }
            
            // دمج المعاملات من النسخة الاحتياطية
            if (backupTransactions != null) {
                for (int i = 0; i < backupTransactions.length(); i++) {
                    JSONObject backupTransaction = backupTransactions.getJSONObject(i);
                    String id = backupTransaction.optString("id");
                    
                    if (existingTransactions.containsKey(id)) {
                        // المعاملة موجودة، فحص التحديثات
                        JSONObject existing = existingTransactions.get(id);
                        if (isNewer(backupTransaction, existing)) {
                            mergeInfo.updated.add(backupTransaction);
                        } else {
                            mergeInfo.conflicts.add(createConflict(backupTransaction, existing));
                        }
                    } else {
                        // معاملة جديدة
                        mergeInfo.added.add(backupTransaction);
                    }
                }
            }
            
        } catch (JSONException e) {
            Log.e(TAG, "خطأ في دمج المعاملات", e);
        }
        
        return mergeInfo;
    }
    
    // دمج الحسابات
    private MergeInfo mergeAccounts(JSONArray backupAccounts, JSONArray currentAccounts) {
        MergeInfo mergeInfo = new MergeInfo();
        Map<String, JSONObject> existingAccounts = new HashMap<>();
        
        try {
            // فهرسة الحسابات الحالية
            if (currentAccounts != null) {
                for (int i = 0; i < currentAccounts.length(); i++) {
                    JSONObject account = currentAccounts.getJSONObject(i);
                    String id = account.optString("id");
                    if (!id.isEmpty()) {
                        existingAccounts.put(id, account);
                    }
                }
            }
            
            // دمج الحسابات من النسخة الاحتياطية
            if (backupAccounts != null) {
                for (int i = 0; i < backupAccounts.length(); i++) {
                    JSONObject backupAccount = backupAccounts.getJSONObject(i);
                    String id = backupAccount.optString("id");
                    
                    if (existingAccounts.containsKey(id)) {
                        // الحساب موجود، فحص التحديثات
                        JSONObject existing = existingAccounts.get(id);
                        JSONObject merged = mergeAccountData(backupAccount, existing);
                        mergeInfo.updated.add(merged);
                    } else {
                        // حساب جديد
                        mergeInfo.added.add(backupAccount);
                    }
                }
            }
            
        } catch (JSONException e) {
            Log.e(TAG, "خطأ في دمج الحسابات", e);
        }
        
        return mergeInfo;
    }
    
    // دمج بيانات الحساب
    private JSONObject mergeAccountData(JSONObject backup, JSONObject current) throws JSONException {
        JSONObject merged = new JSONObject();
        
        // نسخ المعلومات الأساسية
        merged.put("id", backup.getString("id"));
        merged.put("name", backup.optString("name", current.optString("name")));
        merged.put("type", backup.optString("type", current.optString("type")));
        
        // دمج الأرصدة (أخذ الأحدث)
        long backupDate = backup.optLong("last_updated", 0);
        long currentDate = current.optLong("last_updated", 0);
        
        if (backupDate > currentDate) {
            merged.put("balance", backup.optDouble("balance"));
            merged.put("last_updated", backupDate);
        } else {
            merged.put("balance", current.optDouble("balance"));
            merged.put("last_updated", currentDate);
        }
        
        return merged;
    }
    
    // دمج العملاء
    private MergeInfo mergeCustomers(JSONArray backupCustomers, JSONArray currentCustomers) {
        return mergeGenericEntities(backupCustomers, currentCustomers, "customers");
    }
    
    // دمج المنتجات
    private MergeInfo mergeItems(JSONArray backupItems, JSONArray currentItems) {
        return mergeGenericEntities(backupItems, currentItems, "items");
    }
    
    // دمج الكيانات العامة
    private MergeInfo mergeGenericEntities(JSONArray backupEntities, JSONArray currentEntities, String entityType) {
        MergeInfo mergeInfo = new MergeInfo();
        Map<String, JSONObject> existingEntities = new HashMap<>();
        
        try {
            // فهرسة الكيانات الحالية
            if (currentEntities != null) {
                for (int i = 0; i < currentEntities.length(); i++) {
                    JSONObject entity = currentEntities.getJSONObject(i);
                    String id = entity.optString("id");
                    if (!id.isEmpty()) {
                        existingEntities.put(id, entity);
                    }
                }
            }
            
            // دمج الكيانات من النسخة الاحتياطية
            if (backupEntities != null) {
                for (int i = 0; i < backupEntities.length(); i++) {
                    JSONObject backupEntity = backupEntities.getJSONObject(i);
                    String id = backupEntity.optString("id");
                    
                    if (existingEntities.containsKey(id)) {
                        // الكيان موجود، فحص التحديثات
                        JSONObject existing = existingEntities.get(id);
                        if (isNewer(backupEntity, existing)) {
                            mergeInfo.updated.add(backupEntity);
                        }
                    } else {
                        // كيان جديد
                        mergeInfo.added.add(backupEntity);
                    }
                }
            }
            
        } catch (JSONException e) {
            Log.e(TAG, "خطأ في دمج " + entityType, e);
        }
        
        return mergeInfo;
    }
    
    // فحص ما إذا كان العنصر أحدث
    private boolean isNewer(JSONObject item1, JSONObject item2) {
        long date1 = item1.optLong("last_updated", 0);
        long date2 = item2.optLong("last_updated", 0);
        return date1 > date2;
    }
    
    // إنشاء تضارب
    private Conflict createConflict(JSONObject backup, JSONObject current) {
        Conflict conflict = new Conflict();
        conflict.id = backup.optString("id");
        conflict.backupVersion = backup;
        conflict.currentVersion = current;
        conflict.type = "data_conflict";
        return conflict;
    }
    
    // كلاس نتيجة الدمج
    public static class MergeResult {
        public boolean success;
        public String message;
        public MergeInfo transactions;
        public MergeInfo accounts;
        public MergeInfo customers;
        public MergeInfo items;
        public List<Conflict> conflicts = new ArrayList<>();
        
        public int getTotalAdded() {
            return transactions.added.size() + accounts.added.size() + 
                   customers.added.size() + items.added.size();
        }
        
        public int getTotalUpdated() {
            return transactions.updated.size() + accounts.updated.size() + 
                   customers.updated.size() + items.updated.size();
        }
        
        public int getTotalConflicts() {
            return transactions.conflicts.size() + accounts.conflicts.size() + 
                   customers.conflicts.size() + items.conflicts.size();
        }
    }
    
    // كلاس معلومات الدمج
    public static class MergeInfo {
        public List<JSONObject> added = new ArrayList<>();
        public List<JSONObject> updated = new ArrayList<>();
        public List<Conflict> conflicts = new ArrayList<>();
    }
    
    // كلاس التضارب
    public static class Conflict {
        public String id;
        public String type;
        public JSONObject backupVersion;
        public JSONObject currentVersion;
        public String resolution; // "keep_backup", "keep_current", "manual"
    }
}
