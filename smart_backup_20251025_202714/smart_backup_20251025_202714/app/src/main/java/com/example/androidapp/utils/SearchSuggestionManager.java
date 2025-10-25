package com.example.androidapp.utils;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;

import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.dao.*;
import com.example.androidapp.data.entities.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * مدير اقتراحات البحث الذكية - يوفر اقتراحات من قاعدة البيانات
 * يدعم البحث في الحسابات، الأصناف، العملاء، والموردين
 */
public class SearchSuggestionManager {
    private Context context;
    private AppDatabase database;
    private ExecutorService executorService;
    
    // أنواع البحث المختلفة
    public enum SearchType {
        ACCOUNTS,
        ITEMS,
        CUSTOMERS,
        SUPPLIERS,
        INVOICES,
        EMPLOYEES,
        ALL
    }
    
    public SearchSuggestionManager(Context context) {
        this.context = context;
        this.database = AppDatabase.getInstance(context);
        this.executorService = Executors.newFixedThreadPool(2);
    }
    
    /**
     * إعداد اقتراحات البحث لمربع نص
     */
    public void setupSuggestions(AutoCompleteTextView editText, SearchType searchType) {
        SmartSuggestionAdapter adapter = new SmartSuggestionAdapter(context, searchType);
        editText.setAdapter(adapter);
        editText.setThreshold(1); // يبدأ الاقتراح من حرف واحد
        
        // إعداد مستمع تغيير النص
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() >= 1) {
                    adapter.getFilter().filter(s);
                }
            }
            
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }
    
    /**
     * محول ذكي للاقتراحات
     */
    private class SmartSuggestionAdapter extends ArrayAdapter<String> implements Filterable {
        private List<String> suggestions;
        private List<String> allSuggestions;
        private SearchType searchType;
        private Filter filter;
        
        public SmartSuggestionAdapter(Context context, SearchType searchType) {
            super(context, android.R.layout.simple_dropdown_item_1line);
            this.searchType = searchType;
            this.suggestions = new ArrayList<>();
            this.allSuggestions = new ArrayList<>();
            loadInitialSuggestions();
            
            // إنشاء فلتر مخصص
            this.filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults results = new FilterResults();
                    List<String> filteredSuggestions = new ArrayList<>();
                    
                    if (constraint != null && constraint.length() > 0) {
                        String query = constraint.toString().toLowerCase().trim();
                        
                        // البحث في الاقتراحات الحالية
                        for (String suggestion : allSuggestions) {
                            if (suggestion.toLowerCase().contains(query)) {
                                filteredSuggestions.add(suggestion);
                            }
                        }
                        
                        // البحث في قاعدة البيانات للحصول على نتائج إضافية
                        searchInDatabase(query, filteredSuggestions);
                    }
                    
                    results.values = filteredSuggestions;
                    results.count = filteredSuggestions.size();
                    return results;
                }
                
                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    suggestions.clear();
                    if (results != null && results.count > 0) {
                        suggestions.addAll((List<String>) results.values);
                    }
                    notifyDataSetChanged();
                }
            };
        }
        
        @Override
        public int getCount() {
            return suggestions.size();
        }
        
        @Override
        public String getItem(int position) {
            return suggestions.get(position);
        }
        
        @Override
        public Filter getFilter() {
            return filter;
        }
        
        /**
         * تحميل الاقتراحات الأولية
         */
        private void loadInitialSuggestions() {
            executorService.execute(() -> {
                List<String> initialSuggestions = new ArrayList<>();
                
                try {
                    switch (searchType) {
                        case ACCOUNTS:
                            List<Account> accounts = database.accountDao().getAllAccounts();
                            for (Account account : accounts) {
                                initialSuggestions.add(account.getName());
                                if (account.getCode() != null) {
                                    initialSuggestions.add(account.getCode());
                                }
                            }
                            break;
                            
                        case ITEMS:
                            List<Item> items = database.itemDao().getAllItems();
                            for (Item item : items) {
                                initialSuggestions.add(item.getName());
                                if (item.getCode() != null) {
                                    initialSuggestions.add(item.getCode());
                                }
                            }
                            break;
                            
                        case CUSTOMERS:
                            List<Customer> customers = database.customerDao().getAllCustomers();
                            for (Customer customer : customers) {
                                initialSuggestions.add(customer.getName());
                                if (customer.getPhone() != null) {
                                    initialSuggestions.add(customer.getPhone());
                                }
                            }
                            break;
                            
                        case EMPLOYEES:
                            List<Employee> employees = database.employeeDao().getAllEmployees();
                            for (Employee employee : employees) {
                                initialSuggestions.add(employee.getName());
                                if (employee.getEmployeeId() != null) {
                                    initialSuggestions.add(employee.getEmployeeId());
                                }
                            }
                            break;
                            
                        case INVOICES:
                            List<Invoice> invoices = database.invoiceDao().getAllInvoices();
                            for (Invoice invoice : invoices) {
                                if (invoice.getInvoiceNumber() != null) {
                                    initialSuggestions.add(invoice.getInvoiceNumber());
                                }
                            }
                            break;
                            
                        case ALL:
                            // تحميل من جميع الجداول
                            loadAllSuggestions(initialSuggestions);
                            break;
                    }
                    
                    // تحديث القائمة في الخيط الرئيسي
                    ((android.app.Activity) context).runOnUiThread(() -> {
                        allSuggestions.clear();
                        allSuggestions.addAll(initialSuggestions);
                    });
                    
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
        
        /**
         * تحميل اقتراحات من جميع الجداول
         */
        private void loadAllSuggestions(List<String> suggestions) {
            // الحسابات
            List<Account> accounts = database.accountDao().getAllAccounts();
            for (Account account : accounts) {
                suggestions.add("حساب: " + account.getName());
            }
            
            // الأصناف
            List<Item> items = database.itemDao().getAllItems();
            for (Item item : items) {
                suggestions.add("صنف: " + item.getName());
            }
            
            // العملاء
            List<Customer> customers = database.customerDao().getAllCustomers();
            for (Customer customer : customers) {
                suggestions.add("عميل: " + customer.getName());
            }
        }
        
        /**
         * البحث في قاعدة البيانات
         */
        private void searchInDatabase(String query, List<String> results) {
            try {
                switch (searchType) {
                    case ACCOUNTS:
                        List<Account> accounts = database.accountDao().searchAccounts("%" + query + "%");
                        for (Account account : accounts) {
                            String suggestion = account.getName();
                            if (!results.contains(suggestion)) {
                                results.add(suggestion);
                            }
                        }
                        break;
                        
                    case ITEMS:
                        List<Item> items = database.itemDao().searchItems("%" + query + "%");
                        for (Item item : items) {
                            String suggestion = item.getName();
                            if (!results.contains(suggestion)) {
                                results.add(suggestion);
                            }
                        }
                        break;
                        
                    case CUSTOMERS:
                        List<Customer> customers = database.customerDao().searchCustomers("%" + query + "%");
                        for (Customer customer : customers) {
                            String suggestion = customer.getName();
                            if (!results.contains(suggestion)) {
                                results.add(suggestion);
                            }
                        }
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * إعداد البحث السريع مع اقتراحات للأصناف في الفواتير
     */
    public void setupItemSuggestionsForInvoice(AutoCompleteTextView editText) {
        executorService.execute(() -> {
            try {
                List<Item> items = database.itemDao().getAllItems();
                List<String> itemSuggestions = new ArrayList<>();
                
                for (Item item : items) {
                    String suggestion = item.getName();
                    if (item.getCode() != null) {
                        suggestion += " (" + item.getCode() + ")";
                    }
                    if (item.getPrice() > 0) {
                        suggestion += " - " + item.getPrice() + " ر.س";
                    }
                    itemSuggestions.add(suggestion);
                }
                
                ((android.app.Activity) context).runOnUiThread(() -> {
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(context, 
                        android.R.layout.simple_dropdown_item_1line, itemSuggestions);
                    editText.setAdapter(adapter);
                    editText.setThreshold(1);
                });
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
    
    /**
     * البحث المتقدم مع فلترة
     */
    public interface SearchCallback {
        void onSearchResults(List<String> results);
    }
    
    public void performAdvancedSearch(String query, SearchType searchType, SearchCallback callback) {
        executorService.execute(() -> {
            List<String> results = new ArrayList<>();
            
            try {
                switch (searchType) {
                    case ACCOUNTS:
                        List<Account> accounts = database.accountDao().searchAccounts("%" + query + "%");
                        for (Account account : accounts) {
                            results.add(account.getName() + " (" + account.getCode() + ")");
                        }
                        break;
                        
                    case ITEMS:
                        List<Item> items = database.itemDao().searchItems("%" + query + "%");
                        for (Item item : items) {
                            String result = item.getName();
                            if (item.getCode() != null) {
                                result += " - كود: " + item.getCode();
                            }
                            if (item.getPrice() > 0) {
                                result += " - السعر: " + item.getPrice();
                            }
                            results.add(result);
                        }
                        break;
                        
                    case CUSTOMERS:
                        List<Customer> customers = database.customerDao().searchCustomers("%" + query + "%");
                        for (Customer customer : customers) {
                            String result = customer.getName();
                            if (customer.getPhone() != null) {
                                result += " - هاتف: " + customer.getPhone();
                            }
                            results.add(result);
                        }
                        break;
                }
                
                ((android.app.Activity) context).runOnUiThread(() -> {
                    if (callback != null) {
                        callback.onSearchResults(results);
                    }
                });
                
            } catch (Exception e) {
                e.printStackTrace();
                ((android.app.Activity) context).runOnUiThread(() -> {
                    if (callback != null) {
                        callback.onSearchResults(new ArrayList<>());
                    }
                });
            }
        });
    }
    
    /**
     * تنظيف الموارد
     */
    public void cleanup() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}