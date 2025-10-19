package com.example.androidapp.utils;

import android.content.Context;
import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.entities.Item;
import com.example.androidapp.data.entities.Invoice;
import com.example.androidapp.data.entities.Customer;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SmartSuggestionsManager {
    
    private static SmartSuggestionsManager instance;
    private AppDatabase database;
    private ExecutorService executor;
    private Context context;
    
    private SmartSuggestionsManager(Context context) {
        this.context = context.getApplicationContext();
        this.database = AppDatabase.getInstance(this.context);
        this.executor = Executors.newFixedThreadPool(2);
    }
    
    public static synchronized SmartSuggestionsManager getInstance(Context context) {
        if (instance == null) {
            instance = new SmartSuggestionsManager(context);
        }
        return instance;
    }
    
    public interface SuggestionCallback {
        void onSuggestionsReceived(List<String> suggestions);
        void onError(String error);
    }
    
    public void getProductSuggestions(String query, SuggestionCallback callback) {
        executor.execute(() -> {
            try {
                // تصحيح النوع من List<Product> إلى List<Item>
                List<Item> products = database.productDao().searchProducts(query);
                List<String> suggestions = new ArrayList<>();
                
                for (Item product : products) {
                    suggestions.add(product.getName());
                }
                
                // Post results to main thread
                if (callback != null) {
                    callback.onSuggestionsReceived(suggestions);
                }
                
            } catch (Exception e) {
                e.printStackTrace();
                if (callback != null) {
                    callback.onError("Failed to load product suggestions: " + e.getMessage());
                }
            }
        });
    }
    
    public void getCustomerSuggestions(String query, SuggestionCallback callback) {
        executor.execute(() -> {
            try {
                List<Customer> customers = database.customerDao().searchCustomers(query);
                List<String> suggestions = new ArrayList<>();
                
                for (Customer customer : customers) {
                    suggestions.add(customer.getName());
                }
                
                if (callback != null) {
                    callback.onSuggestionsReceived(suggestions);
                }
                
            } catch (Exception e) {
                e.printStackTrace();
                if (callback != null) {
                    callback.onError("Failed to load customer suggestions: " + e.getMessage());
                }
            }
        });
    }
    
    public void getRecentInvoiceSuggestions(SuggestionCallback callback) {
        executor.execute(() -> {
            try {
                List<Invoice> recentInvoices = database.invoiceDao().getAllInvoices();
                List<String> suggestions = new ArrayList<>();
                
                // Get the last 5 invoices
                int limit = Math.min(5, recentInvoices.size());
                for (int i = 0; i < limit; i++) {
                    Invoice invoice = recentInvoices.get(i);
                    suggestions.add("Invoice #" + invoice.getInvoiceNumber());
                }
                
                if (callback != null) {
                    callback.onSuggestionsReceived(suggestions);
                }
                
            } catch (Exception e) {
                e.printStackTrace();
                if (callback != null) {
                    callback.onError("Failed to load invoice suggestions: " + e.getMessage());
                }
            }
        });
    }
    
    public void getLowStockAlerts(SuggestionCallback callback) {
        executor.execute(() -> {
            try {
                // تصحيح النوع من List<Product> إلى List<Item>
                List<Item> lowStockItems = database.productDao().getLowStockProducts();
                List<String> alerts = new ArrayList<>();
                
                for (Item item : lowStockItems) {
                    alerts.add(item.getName() + " - Stock: " + item.getQuantity());
                }
                
                if (callback != null) {
                    callback.onSuggestionsReceived(alerts);
                }
                
            } catch (Exception e) {
                e.printStackTrace();
                if (callback != null) {
                    callback.onError("Failed to load low stock alerts: " + e.getMessage());
                }
            }
        });
    }
    
    public void shutdown() {
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
        }
    }
}
