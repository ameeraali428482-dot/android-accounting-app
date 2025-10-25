package com.example.androidapp.utils;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.entities.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import android.os.Handler;
import android.os.Looper;

/**
 * مدير الاقتراحات الذكي - يوفر اقتراحات ذكية ومخصصة لكل نوع إدخال
 * يدعم العملاء، الأصناف، الموردين، وغيرها من البيانات
 */
public class SmartSuggestionsManager {
    
    public enum SuggestionType {
        CUSTOMER_NAME,
        CUSTOMER_PHONE,
        CUSTOMER_EMAIL,
        PRODUCT_NAME,
        PRODUCT_CATEGORY,
        PRODUCT_BARCODE,
        SUPPLIER_NAME,
        SUPPLIER_PHONE,
        ACCOUNT_NAME,
        INVOICE_NUMBER,
        DESCRIPTION,
        AMOUNT,
        PAYMENT_METHOD,
        CURRENCY,
        LOCATION,
        GENERAL_TEXT
    }
    
    private Context context;
    private AppDatabase database;
    private ExecutorService executorService;
    private Handler mainHandler;
    private Map<SuggestionType, List<String>> cachedSuggestions;
    private Map<SuggestionType, Long> lastUpdateTime;
    private static final long CACHE_VALIDITY_DURATION = 5 * 60 * 1000; // 5 دقائق
    
    public SmartSuggestionsManager(Context context) {
        this.context = context;
        this.database = AppDatabase.getInstance(context);
        this.executorService = Executors.newFixedThreadPool(2);
        this.mainHandler = new Handler(Looper.getMainLooper());
        this.cachedSuggestions = new HashMap<>();
        this.lastUpdateTime = new HashMap<>();
        
        // تحميل البيانات الأساسية مسبقاً
        preloadBasicSuggestions();
    }
    
    /**
     * ربط العنصر بنظام الاقتراحات الذكي
     */
    public void attachSmartSuggestions(EditText editText, SuggestionType type) {
        if (editText instanceof AutoCompleteTextView) {
            attachToAutoCompleteTextView((AutoCompleteTextView) editText, type);
        } else {
            // تحويل EditText عادي إلى نظام اقتراحات
            convertToSmartEditText(editText, type);
        }
    }
    
    private void attachToAutoCompleteTextView(AutoCompleteTextView autoCompleteTextView, SuggestionType type) {
        // إعداد الـ Adapter الأولي
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, 
            android.R.layout.simple_dropdown_item_1line, new ArrayList<>());
        autoCompleteTextView.setAdapter(adapter);
        autoCompleteTextView.setThreshold(1);
        
        // تحديث الاقتراحات عند تغيير النص
        autoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() >= 1) {
                    updateSuggestions(autoCompleteTextView, adapter, type, s.toString());
                }
            }
            
            @Override
            public void afterTextChanged(Editable s) {}
        });
        
        // تحميل الاقتراحات الأولية
        loadInitialSuggestions(autoCompleteTextView, adapter, type);
    }
    
    private void convertToSmartEditText(EditText editText, SuggestionType type) {
        // إضافة تلميحات بصرية ومساعدة للنص
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateAndFormatInput(editText, type, s.toString());
            }
            
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }
    
    private void updateSuggestions(AutoCompleteTextView autoCompleteTextView, 
                                 ArrayAdapter<String> adapter, SuggestionType type, String query) {
        executorService.execute(() -> {
            List<String> suggestions = getSuggestionsForType(type, query);
            
            mainHandler.post(() -> {
                adapter.clear();
                adapter.addAll(suggestions);
                adapter.notifyDataSetChanged();
                
                if (!suggestions.isEmpty() && !autoCompleteTextView.isPopupShowing()) {
                    autoCompleteTextView.showDropDown();
                }
            });
        });
    }
    
    private void loadInitialSuggestions(AutoCompleteTextView autoCompleteTextView, 
                                      ArrayAdapter<String> adapter, SuggestionType type) {
        executorService.execute(() -> {
            List<String> suggestions = getCachedSuggestions(type);
            
            mainHandler.post(() -> {
                adapter.clear();
                adapter.addAll(suggestions);
                adapter.notifyDataSetChanged();
            });
        });
    }
    
    private List<String> getSuggestionsForType(SuggestionType type, String query) {
        List<String> suggestions = new ArrayList<>();
        String lowerQuery = query.toLowerCase().trim();
        
        switch (type) {
            case CUSTOMER_NAME:
                suggestions = getCustomerNameSuggestions(lowerQuery);
                break;
            case CUSTOMER_PHONE:
                suggestions = getCustomerPhoneSuggestions(lowerQuery);
                break;
            case CUSTOMER_EMAIL:
                suggestions = getCustomerEmailSuggestions(lowerQuery);
                break;
            case PRODUCT_NAME:
                suggestions = getProductNameSuggestions(lowerQuery);
                break;
            case PRODUCT_CATEGORY:
                suggestions = getProductCategorySuggestions(lowerQuery);
                break;
            case SUPPLIER_NAME:
                suggestions = getSupplierNameSuggestions(lowerQuery);
                break;
            case ACCOUNT_NAME:
                suggestions = getAccountNameSuggestions(lowerQuery);
                break;
            case PAYMENT_METHOD:
                suggestions = getPaymentMethodSuggestions(lowerQuery);
                break;
            case CURRENCY:
                suggestions = getCurrencySuggestions(lowerQuery);
                break;
            default:
                suggestions = getGeneralSuggestions(lowerQuery);
                break;
        }
        
        return suggestions;
    }
    
    private List<String> getCustomerNameSuggestions(String query) {
        try {
            List<Customer> customers = database.customerDao().searchCustomers("%" + query + "%");
            List<String> suggestions = new ArrayList<>();
            for (Customer customer : customers) {
                if (customer.getName() != null && 
                    customer.getName().toLowerCase().contains(query)) {
                    suggestions.add(customer.getName());
                }
            }
            return suggestions;
        } catch (Exception e) {
            return getDefaultCustomerNames(query);
        }
    }
    
    private List<String> getCustomerPhoneSuggestions(String query) {
        try {
            List<Customer> customers = database.customerDao().searchCustomers("%" + query + "%");
            List<String> suggestions = new ArrayList<>();
            for (Customer customer : customers) {
                if (customer.getPhone() != null && 
                    customer.getPhone().contains(query)) {
                    suggestions.add(customer.getPhone());
                }
            }
            return suggestions;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
    
    private List<String> getCustomerEmailSuggestions(String query) {
        try {
            List<Customer> customers = database.customerDao().searchCustomers("%" + query + "%");
            List<String> suggestions = new ArrayList<>();
            for (Customer customer : customers) {
                if (customer.getEmail() != null && 
                    customer.getEmail().toLowerCase().contains(query)) {
                    suggestions.add(customer.getEmail());
                }
            }
            return suggestions;
        } catch (Exception e) {
            return getDefaultEmailDomains(query);
        }
    }
    
    private List<String> getProductNameSuggestions(String query) {
        try {
            List<Product> products = database.productDao().searchProducts("%" + query + "%");
            List<String> suggestions = new ArrayList<>();
            for (Product product : products) {
                if (product.getName() != null && 
                    product.getName().toLowerCase().contains(query)) {
                    suggestions.add(product.getName());
                }
            }
            return suggestions;
        } catch (Exception e) {
            return getDefaultProductNames(query);
        }
    }
    
    private List<String> getProductCategorySuggestions(String query) {
        // فئات أساسية للمنتجات
        List<String> categories = Arrays.asList(
            "إلكترونيات", "ملابس", "أطعمة", "مشروبات", "أدوية", "مستحضرات تجميل",
            "أدوات منزلية", "كتب", "رياضة", "ألعاب", "سيارات", "عقارات",
            "خدمات", "تعليم", "صحة", "سفر", "مجوهرات", "أثاث"
        );
        
        return categories.stream()
            .filter(cat -> cat.toLowerCase().contains(query))
            .collect(java.util.stream.Collectors.toList());
    }
    
    private List<String> getSupplierNameSuggestions(String query) {
        try {
            List<Supplier> suppliers = database.supplierDao().searchSuppliers("%" + query + "%");
            List<String> suggestions = new ArrayList<>();
            for (Supplier supplier : suppliers) {
                if (supplier.getName() != null && 
                    supplier.getName().toLowerCase().contains(query)) {
                    suggestions.add(supplier.getName());
                }
            }
            return suggestions;
        } catch (Exception e) {
            return getDefaultSupplierNames(query);
        }
    }
    
    private List<String> getAccountNameSuggestions(String query) {
        try {
            List<Account> accounts = database.accountDao().searchAccounts("%" + query + "%");
            List<String> suggestions = new ArrayList<>();
            for (Account account : accounts) {
                if (account.getName() != null && 
                    account.getName().toLowerCase().contains(query)) {
                    suggestions.add(account.getName());
                }
            }
            return suggestions;
        } catch (Exception e) {
            return getDefaultAccountNames(query);
        }
    }
    
    private List<String> getPaymentMethodSuggestions(String query) {
        List<String> methods = Arrays.asList(
            "نقدي", "بطاقة ائتمان", "تحويل بنكي", "شيك", "فيزا", "ماستركارد",
            "باي بال", "تحويل فوري", "محفظة إلكترونية", "قسط", "آجل"
        );
        
        return methods.stream()
            .filter(method -> method.toLowerCase().contains(query))
            .collect(java.util.stream.Collectors.toList());
    }
    
    private List<String> getCurrencySuggestions(String query) {
        List<String> currencies = Arrays.asList(
            "ريال سعودي", "درهم إماراتي", "دينار كويتي", "ريال قطري", "دينار بحريني",
            "دولار أمريكي", "يورو", "جنيه إسترليني", "ين ياباني", "دولار كندي",
            "فرنك سويسري", "دولار أسترالي", "يوان صيني"
        );
        
        return currencies.stream()
            .filter(currency -> currency.toLowerCase().contains(query))
            .collect(java.util.stream.Collectors.toList());
    }
    
    private List<String> getGeneralSuggestions(String query) {
        // اقتراحات عامة بناءً على النشاط التجاري
        List<String> general = Arrays.asList(
            "فاتورة", "إيصال", "طلب", "عرض أسعار", "عقد", "اتفاقية",
            "دفعة", "خصم", "ضريبة", "شحن", "تأمين", "عمولة",
            "خدمة", "صيانة", "ضمان", "استبدال", "استرداد"
        );
        
        return general.stream()
            .filter(item -> item.toLowerCase().contains(query))
            .collect(java.util.stream.Collectors.toList());
    }
    
    // Cached suggestions methods
    private List<String> getCachedSuggestions(SuggestionType type) {
        if (isCacheValid(type)) {
            return cachedSuggestions.getOrDefault(type, new ArrayList<>());
        } else {
            return refreshCacheForType(type);
        }
    }
    
    private boolean isCacheValid(SuggestionType type) {
        Long lastUpdate = lastUpdateTime.get(type);
        return lastUpdate != null && 
               (System.currentTimeMillis() - lastUpdate) < CACHE_VALIDITY_DURATION;
    }
    
    private List<String> refreshCacheForType(SuggestionType type) {
        List<String> suggestions = getSuggestionsForType(type, "");
        cachedSuggestions.put(type, suggestions);
        lastUpdateTime.put(type, System.currentTimeMillis());
        return suggestions;
    }
    
    private void preloadBasicSuggestions() {
        executorService.execute(() -> {
            // تحميل الاقتراحات الأساسية
            for (SuggestionType type : SuggestionType.values()) {
                refreshCacheForType(type);
            }
        });
    }
    
    private void validateAndFormatInput(EditText editText, SuggestionType type, String input) {
        switch (type) {
            case CUSTOMER_PHONE:
            case SUPPLIER_PHONE:
                formatPhoneNumber(editText, input);
                break;
            case CUSTOMER_EMAIL:
                validateEmail(editText, input);
                break;
            case AMOUNT:
                formatAmount(editText, input);
                break;
        }
    }
    
    private void formatPhoneNumber(EditText editText, String input) {
        // تنسيق رقم الهاتف
        String cleaned = input.replaceAll("[^\\d+]", "");
        if (cleaned.startsWith("+966")) {
            // رقم سعودي
            if (cleaned.length() > 4) {
                String formatted = cleaned.substring(0, 4) + " " + 
                                 cleaned.substring(4, Math.min(7, cleaned.length()));
                if (cleaned.length() > 7) {
                    formatted += " " + cleaned.substring(7);
                }
                editText.removeTextChangedListener(null);
                editText.setText(formatted);
                editText.setSelection(formatted.length());
            }
        }
    }
    
    private void validateEmail(EditText editText, String input) {
        // التحقق من صحة البريد الإلكتروني
        if (input.contains("@") && !input.matches(".*@.*\\..*")) {
            editText.setError("تنسيق البريد الإلكتروني غير صحيح");
        } else {
            editText.setError(null);
        }
    }
    
    private void formatAmount(EditText editText, String input) {
        // تنسيق المبالغ المالية
        String cleaned = input.replaceAll("[^\\d.]", "");
        try {
            if (!cleaned.isEmpty()) {
                double amount = Double.parseDouble(cleaned);
                String formatted = String.format("%.2f", amount);
                editText.removeTextChangedListener(null);
                editText.setText(formatted);
                editText.setSelection(formatted.length());
            }
        } catch (NumberFormatException e) {
            // تجاهل الأخطاء
        }
    }
    
    // Default suggestions methods
    private List<String> getDefaultCustomerNames(String query) {
        List<String> names = Arrays.asList(
            "أحمد محمد", "فاطمة علي", "خالد السعد", "مريم أحمد", "محمد العتيبي",
            "نورا المطيري", "سعد الغامدي", "هند القحطاني", "علي الشهري", "سارة الزهراني"
        );
        return names.stream()
            .filter(name -> name.toLowerCase().contains(query))
            .collect(java.util.stream.Collectors.toList());
    }
    
    private List<String> getDefaultEmailDomains(String query) {
        List<String> domains = Arrays.asList(
            "@gmail.com", "@hotmail.com", "@yahoo.com", "@outlook.com", 
            "@icloud.com", "@live.com", "@msn.com"
        );
        if (query.contains("@")) {
            String beforeAt = query.substring(0, query.indexOf("@"));
            return domains.stream()
                .map(domain -> beforeAt + domain)
                .collect(java.util.stream.Collectors.toList());
        }
        return new ArrayList<>();
    }
    
    private List<String> getDefaultProductNames(String query) {
        List<String> products = Arrays.asList(
            "لابتوب", "هاتف ذكي", "تابلت", "كاميرا", "سماعات", "شاشة",
            "لوحة مفاتيح", "ماوس", "طابعة", "قلم", "دفتر", "كتاب"
        );
        return products.stream()
            .filter(product -> product.toLowerCase().contains(query))
            .collect(java.util.stream.Collectors.toList());
    }
    
    private List<String> getDefaultSupplierNames(String query) {
        List<String> suppliers = Arrays.asList(
            "شركة النور للتقنية", "مؤسسة الأمل التجارية", "شركة الرياض للمواد",
            "مؤسسة الخليج للتوريد", "شركة المملكة للخدمات", "مجموعة الشرق الأوسط"
        );
        return suppliers.stream()
            .filter(supplier -> supplier.toLowerCase().contains(query))
            .collect(java.util.stream.Collectors.toList());
    }
    
    private List<String> getDefaultAccountNames(String query) {
        List<String> accounts = Arrays.asList(
            "الصندوق", "البنك الأهلي", "بنك الرياض", "بنك ساب", "البنك العربي",
            "بنك الراجحي", "بنك الإنماء", "بنك البلاد", "حساب العملاء", "حساب الموردين"
        );
        return accounts.stream()
            .filter(account -> account.toLowerCase().contains(query))
            .collect(java.util.stream.Collectors.toList());
    }
    
    public void destroy() {
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}