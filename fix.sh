#!/bin/bash

# تصحيح وإعادة كتابة ملفات التطبيق المحاسب
# هذا البرنامج النصي سيصحح الأخطاء ويضيف الميزات المفقودة

echo "🔄 بدء تصحيح الملفات المحاسبية..."

# إنشاء ملف DetailedSettingsActivity.java مصحح
cat > app/src/main/java/com/example/androidapp/ui/settings/DetailedSettingsActivity.java << 'EOF'
package com.example.androidapp.ui.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;

import com.example.androidapp.R;
import com.example.androidapp.ui.common.EnhancedBaseActivity;

/**
 * نشاط الإعدادات المفصل للتطبيق
 * يحتوي على جميع إعدادات الفواتير والميزات الجديدة
 */
public class DetailedSettingsActivity extends EnhancedBaseActivity {

    // إعدادات الفواتير
    private CheckBox cbShowCustomerDetails;
    private CheckBox cbShowItemCodes;
    private CheckBox cbShowItemDescriptions;
    private CheckBox cbShowUnitPrices;
    private CheckBox cbShowQuantities;
    private CheckBox cbShowTotalPrices;
    private CheckBox cbShowTaxes;
    private CheckBox cbShowDiscount;
    private CheckBox cbShowNotes;
    private CheckBox cbShowCompanyLogo;
    private CheckBox cbShowCompanyStamp;
    private CheckBox cbShowBarcode;
    private CheckBox cbShowQRCode;
    private CheckBox cbShowPaymentTerms;
    private CheckBox cbShowDueDate;
    private CheckBox cbAutoCalculateTax;
    private CheckBox cbAutoSaveInvoices;
    private CheckBox cbPrintAfterSave;

    // إعدادات الميزات الصوتية والذكية
    private CheckBox cbVoiceInputEnabled;
    private CheckBox cbTTSEnabled;
    private CheckBox cbSuggestionsEnabled;
    private CheckBox cbAutoReadEnabled;
    private CheckBox cbVoiceInSearch;
    private CheckBox cbVoiceInForms;
    private CheckBox cbVoiceInInvoices;
    
    // إعدادات السرعة والجودة
    private SeekBar sbTTSSpeed;
    private TextView tvTTSSpeedValue;
    private SeekBar sbVoiceTimeout;
    private TextView tvVoiceTimeoutValue;
    
    // إعدادات التخصيص
    private CheckBox cbDarkTheme;
    private CheckBox cbLargeText;
    private CheckBox cbHighContrast;
    private CheckBox cbShowTooltips;
    
    // إعدادات الأمان
    private CheckBox cbRequirePasswordForReports;
    private CheckBox cbRequirePasswordForSettings;
    private CheckBox cbRequirePasswordForBackup;
    private CheckBox cbLogUserActions;
    
    private SharedPreferences preferences;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_settings);
        
        preferences = getSharedPreferences("AppSettings", Context.MODE_PRIVATE);
        
        initializeViews();
        loadCurrentSettings();
        setupListeners();
        
        // إعداد شريط الأدوات
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("الإعدادات المفصلة");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }
    
    private void initializeViews() {
        // إعدادات الفواتير
        cbShowCustomerDetails = findViewById(R.id.cb_show_customer_details);
        cbShowItemCodes = findViewById(R.id.cb_show_item_codes);
        cbShowItemDescriptions = findViewById(R.id.cb_show_item_descriptions);
        cbShowUnitPrices = findViewById(R.id.cb_show_unit_prices);
        cbShowQuantities = findViewById(R.id.cb_show_quantities);
        cbShowTotalPrices = findViewById(R.id.cb_show_total_prices);
        cbShowTaxes = findViewById(R.id.cb_show_taxes);
        cbShowDiscount = findViewById(R.id.cb_show_discount);
        cbShowNotes = findViewById(R.id.cb_show_notes);
        cbShowCompanyLogo = findViewById(R.id.cb_show_company_logo);
        cbShowCompanyStamp = findViewById(R.id.cb_show_company_stamp);
        cbShowBarcode = findViewById(R.id.cb_show_barcode);
        cbShowQRCode = findViewById(R.id.cb_show_qr_code);
        cbShowPaymentTerms = findViewById(R.id.cb_show_payment_terms);
        cbShowDueDate = findViewById(R.id.cb_show_due_date);
        cbAutoCalculateTax = findViewById(R.id.cb_auto_calculate_tax);
        cbAutoSaveInvoices = findViewById(R.id.cb_auto_save_invoices);
        cbPrintAfterSave = findViewById(R.id.cb_print_after_save);
        
        // إعدادات الميزات الصوتية
        cbVoiceInputEnabled = findViewById(R.id.cb_voice_input_enabled);
        cbTTSEnabled = findViewById(R.id.cb_tts_enabled);
        cbSuggestionsEnabled = findViewById(R.id.cb_suggestions_enabled);
        cbAutoReadEnabled = findViewById(R.id.cb_auto_read_enabled);
        cbVoiceInSearch = findViewById(R.id.cb_voice_in_search);
        cbVoiceInForms = findViewById(R.id.cb_voice_in_forms);
        cbVoiceInInvoices = findViewById(R.id.cb_voice_in_invoices);
        
        // إعدادات السرعة
        sbTTSSpeed = findViewById(R.id.sb_tts_speed);
        tvTTSSpeedValue = findViewById(R.id.tv_tts_speed_value);
        sbVoiceTimeout = findViewById(R.id.sb_voice_timeout);
        tvVoiceTimeoutValue = findViewById(R.id.tv_voice_timeout_value);
        
        // إعدادات التخصيص
        cbDarkTheme = findViewById(R.id.cb_dark_theme);
        cbLargeText = findViewById(R.id.cb_large_text);
        cbHighContrast = findViewById(R.id.cb_high_contrast);
        cbShowTooltips = findViewById(R.id.cb_show_tooltips);
        
        // إعدادات الأمان
        cbRequirePasswordForReports = findViewById(R.id.cb_require_password_reports);
        cbRequirePasswordForSettings = findViewById(R.id.cb_require_password_settings);
        cbRequirePasswordForBackup = findViewById(R.id.cb_require_password_backup);
        cbLogUserActions = findViewById(R.id.cb_log_user_actions);
    }
    
    private void loadCurrentSettings() {
        // تحميل إعدادات الفواتير
        cbShowCustomerDetails.setChecked(preferences.getBoolean("show_customer_details", true));
        cbShowItemCodes.setChecked(preferences.getBoolean("show_item_codes", true));
        cbShowItemDescriptions.setChecked(preferences.getBoolean("show_item_descriptions", true));
        cbShowUnitPrices.setChecked(preferences.getBoolean("show_unit_prices", true));
        cbShowQuantities.setChecked(preferences.getBoolean("show_quantities", true));
        cbShowTotalPrices.setChecked(preferences.getBoolean("show_total_prices", true));
        cbShowTaxes.setChecked(preferences.getBoolean("show_taxes", true));
        cbShowDiscount.setChecked(preferences.getBoolean("show_discount", true));
        cbShowNotes.setChecked(preferences.getBoolean("show_notes", true));
        cbShowCompanyLogo.setChecked(preferences.getBoolean("show_company_logo", true));
        cbShowCompanyStamp.setChecked(preferences.getBoolean("show_company_stamp", false));
        cbShowBarcode.setChecked(preferences.getBoolean("show_barcode", false));
        cbShowQRCode.setChecked(preferences.getBoolean("show_qr_code", true));
        cbShowPaymentTerms.setChecked(preferences.getBoolean("show_payment_terms", true));
        cbShowDueDate.setChecked(preferences.getBoolean("show_due_date", true));
        cbAutoCalculateTax.setChecked(preferences.getBoolean("auto_calculate_tax", true));
        cbAutoSaveInvoices.setChecked(preferences.getBoolean("auto_save_invoices", false));
        cbPrintAfterSave.setChecked(preferences.getBoolean("print_after_save", false));
        
        // تحميل إعدادات الميزات الصوتية
        cbVoiceInputEnabled.setChecked(preferences.getBoolean("voice_input_enabled", true));
        cbTTSEnabled.setChecked(preferences.getBoolean("tts_enabled", true));
        cbSuggestionsEnabled.setChecked(preferences.getBoolean("suggestions_enabled", true));
        cbAutoReadEnabled.setChecked(preferences.getBoolean("auto_read_enabled", false));
        cbVoiceInSearch.setChecked(preferences.getBoolean("voice_in_search", true));
        cbVoiceInForms.setChecked(preferences.getBoolean("voice_in_forms", true));
        cbVoiceInInvoices.setChecked(preferences.getBoolean("voice_in_invoices", true));
        
        // تحميل إعدادات السرعة
        float ttsSpeed = preferences.getFloat("tts_speed", 0.8f);
        sbTTSSpeed.setProgress((int) (ttsSpeed * 100));
        tvTTSSpeedValue.setText(String.format("%.1f", ttsSpeed));
        
        int voiceTimeout = preferences.getInt("voice_timeout", 5);
        sbVoiceTimeout.setProgress(voiceTimeout);
        tvVoiceTimeoutValue.setText(voiceTimeout + " ثواني");
        
        // تحميل إعدادات التخصيص
        cbDarkTheme.setChecked(preferences.getBoolean("dark_theme", false));
        cbLargeText.setChecked(preferences.getBoolean("large_text", false));
        cbHighContrast.setChecked(preferences.getBoolean("high_contrast", false));
        cbShowTooltips.setChecked(preferences.getBoolean("show_tooltips", true));
        
        // تحميل إعدادات الأمان
        cbRequirePasswordForReports.setChecked(preferences.getBoolean("require_password_reports", false));
        cbRequirePasswordForSettings.setChecked(preferences.getBoolean("require_password_settings", false));
        cbRequirePasswordForBackup.setChecked(preferences.getBoolean("require_password_backup", true));
        cbLogUserActions.setChecked(preferences.getBoolean("log_user_actions", true));
    }
    
    private void setupListeners() {
        // مستمعات إعدادات الفواتير
        cbShowCustomerDetails.setOnCheckedChangeListener((v, checked) -> 
            savePreference("show_customer_details", checked));
        cbShowItemCodes.setOnCheckedChangeListener((v, checked) -> 
            savePreference("show_item_codes", checked));
        cbShowItemDescriptions.setOnCheckedChangeListener((v, checked) -> 
            savePreference("show_item_descriptions", checked));
        cbShowUnitPrices.setOnCheckedChangeListener((v, checked) -> 
            savePreference("show_unit_prices", checked));
        cbShowQuantities.setOnCheckedChangeListener((v, checked) -> 
            savePreference("show_quantities", checked));
        cbShowTotalPrices.setOnCheckedChangeListener((v, checked) -> 
            savePreference("show_total_prices", checked));
        cbShowTaxes.setOnCheckedChangeListener((v, checked) -> 
            savePreference("show_taxes", checked));
        cbShowDiscount.setOnCheckedChangeListener((v, checked) -> 
            savePreference("show_discount", checked));
        cbShowNotes.setOnCheckedChangeListener((v, checked) -> 
            savePreference("show_notes", checked));
        cbShowCompanyLogo.setOnCheckedChangeListener((v, checked) -> 
            savePreference("show_company_logo", checked));
        cbShowCompanyStamp.setOnCheckedChangeListener((v, checked) -> 
            savePreference("show_company_stamp", checked));
        cbShowBarcode.setOnCheckedChangeListener((v, checked) -> 
            savePreference("show_barcode", checked));
        cbShowQRCode.setOnCheckedChangeListener((v, checked) -> 
            savePreference("show_qr_code", checked));
        cbShowPaymentTerms.setOnCheckedChangeListener((v, checked) -> 
            savePreference("show_payment_terms", checked));
        cbShowDueDate.setOnCheckedChangeListener((v, checked) -> 
            savePreference("show_due_date", checked));
        cbAutoCalculateTax.setOnCheckedChangeListener((v, checked) -> 
            savePreference("auto_calculate_tax", checked));
        cbAutoSaveInvoices.setOnCheckedChangeListener((v, checked) -> 
            savePreference("auto_save_invoices", checked));
        cbPrintAfterSave.setOnCheckedChangeListener((v, checked) -> 
            savePreference("print_after_save", checked));
        
        // مستمعات الميزات الصوتية
        cbVoiceInputEnabled.setOnCheckedChangeListener((v, checked) -> {
            savePreference("voice_input_enabled", checked);
            setVoiceInputEnabled(checked);
            if (checked) {
                Toast.makeText(this, "تم تفعيل الإدخال الصوتي", Toast.LENGTH_SHORT).show();
            }
        });
        
        cbTTSEnabled.setOnCheckedChangeListener((v, checked) -> {
            savePreference("tts_enabled", checked);
            setTTSEnabled(checked);
            if (checked) {
                speakText("تم تفعيل تحويل النص إلى كلام");
            }
        });
        
        cbSuggestionsEnabled.setOnCheckedChangeListener((v, checked) -> {
            savePreference("suggestions_enabled", checked);
            setSuggestionsEnabled(checked);
        });
        
        cbAutoReadEnabled.setOnCheckedChangeListener((v, checked) -> {
            savePreference("auto_read_enabled", checked);
            setAutoReadEnabled(checked);
        });
        
        cbVoiceInSearch.setOnCheckedChangeListener((v, checked) -> 
            savePreference("voice_in_search", checked));
        cbVoiceInForms.setOnCheckedChangeListener((v, checked) -> 
            savePreference("voice_in_forms", checked));
        cbVoiceInInvoices.setOnCheckedChangeListener((v, checked) -> 
            savePreference("voice_in_invoices", checked));
        
        // مستمعات إعدادات السرعة
        sbTTSSpeed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float speed = progress / 100.0f;
                if (speed < 0.1f) speed = 0.1f;
                if (speed > 2.0f) speed = 2.0f;
                
                tvTTSSpeedValue.setText(String.format("%.1f", speed));
                
                if (fromUser) {
                    setTTSSpeed(speed);
                    savePreference("tts_speed", speed);
                }
            }
            
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (isTTSEnabled()) {
                    speakText("سرعة الكلام الجديدة");
                }
            }
        });
        
        sbVoiceTimeout.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress < 3) progress = 3;
                if (progress > 15) progress = 15;
                
                tvVoiceTimeoutValue.setText(progress + " ثواني");
                
                if (fromUser) {
                    savePreference("voice_timeout", progress);
                }
            }
            
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        
        // مستمعات إعدادات التخصيص
        cbDarkTheme.setOnCheckedChangeListener((v, checked) -> {
            savePreference("dark_theme", checked);
            Toast.makeText(this, "سيتم تطبيق الثيم عند إعادة تشغيل التطبيق", Toast.LENGTH_LONG).show();
        });
        
        cbLargeText.setOnCheckedChangeListener((v, checked) -> {
            savePreference("large_text", checked);
            Toast.makeText(this, "سيتم تطبيق حجم النص عند إعادة تشغيل التطبيق", Toast.LENGTH_LONG).show();
        });
        
        cbHighContrast.setOnCheckedChangeListener((v, checked) -> 
            savePreference("high_contrast", checked));
        cbShowTooltips.setOnCheckedChangeListener((v, checked) -> 
            savePreference("show_tooltips", checked));
        
        // مستمعات إعدادات الأمان
        cbRequirePasswordForReports.setOnCheckedChangeListener((v, checked) -> 
            savePreference("require_password_reports", checked));
        cbRequirePasswordForSettings.setOnCheckedChangeListener((v, checked) -> 
            savePreference("require_password_settings", checked));
        cbRequirePasswordForBackup.setOnCheckedChangeListener((v, checked) -> 
            savePreference("require_password_backup", checked));
        cbLogUserActions.setOnCheckedChangeListener((v, checked) -> 
            savePreference("log_user_actions", checked));
    }
    
    private void savePreference(String key, boolean value) {
        preferences.edit().putBoolean(key, value).apply();
    }
    
    private void savePreference(String key, float value) {
        preferences.edit().putFloat(key, value).apply();
    }
    
    private void savePreference(String key, int value) {
        preferences.edit().putInt(key, value).apply();
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    protected void performSearch(String query) {
        // البحث في الإعدادات - يمكن تنفيذه لاحقاً
        Toast.makeText(this, "البحث في الإعدادات: " + query, Toast.LENGTH_SHORT).show();
    }
    
    @Override
    protected String getAutoReadContent() {
        return "صفحة الإعدادات المفصلة للتطبيق. تحتوي على إعدادات الفواتير والميزات الصوتية والتخصيص والأمان";
    }
}
EOF

# إنشاء ملف EnhancedAccountListActivity.java مصحح
cat > app/src/main/java/com/example/androidapp/ui/account/EnhancedAccountListActivity.java << 'EOF'
package com.example.androidapp.ui.account;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidapp.R;
import com.example.androidapp.data.entities.Account;
import com.example.androidapp.ui.account.viewmodel.AccountViewModel;
import com.example.androidapp.ui.common.EnhancedBaseActivity;
import com.example.androidapp.utils.SearchSuggestionManager;
import com.example.androidapp.utils.VoiceInputManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

/**
 * نشاط قائمة الحسابات المحسن
 * يحتوي على جميع الميزات الجديدة للبحث والإدخال الصوتي
 */
public class EnhancedAccountListActivity extends EnhancedBaseActivity {

    private RecyclerView recyclerView;
    private EnhancedAccountAdapter accountAdapter;
    private AccountViewModel viewModel;
    private FloatingActionButton fabAddAccount;
    private List<Account> allAccounts = new ArrayList<>();
    private List<Account> filteredAccounts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enhanced_account_list);

        initializeViews();
        setupRecyclerView();
        setupViewModel();
        setupFloatingActionButton();
        loadAccounts();

        // إعداد شريط الأدوات
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("دليل الحسابات");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * تهيئة المكونات
     */
    private void initializeViews() {
        recyclerView = findViewById(R.id.recycler_view_accounts);
        fabAddAccount = findViewById(R.id.fab_add_account);
    }

    /**
     * إعداد قائمة التمرير
     */
    private void setupRecyclerView() {
        accountAdapter = new EnhancedAccountAdapter(this, filteredAccounts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(accountAdapter);

        // إعداد مستمعات الأحداث للمحول
        accountAdapter.setOnItemClickListener(account -> {
            if (isTTSEnabled()) {
                String content = "حساب " + account.getName() +
                    ". رقم الحساب " + account.getCode() +
                    ". الرصيد " + account.getBalance() + " ريال";
                speakText(content);
            }
            openAccountDetails(account);
        });

        accountAdapter.setOnItemLongClickListener(account -> {
            if (isTTSEnabled()) {
                readAccountDetails(account);
            }
            return true;
        });
    }

    /**
     * إعداد نموذج البيانات
     */
    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(AccountViewModel.class);

        // مراقبة تغييرات الحسابات
        viewModel.getAllAccounts().observe(this, accounts -> {
            if (accounts != null) {
                allAccounts.clear();
                allAccounts.addAll(accounts);
                updateFilteredList("");

                if (isAutoReadEnabled() && !accounts.isEmpty()) {
                    speakText("تم تحميل " + accounts.size() + " حساب في دليل الحسابات");
                }
            }
        });
    }

    /**
     * إعداد زر الإضافة العائم
     */
    private void setupFloatingActionButton() {
        fabAddAccount.setOnClickListener(v -> {
            Intent intent = new Intent(this, EnhancedAccountDetailActivity.class);
            startActivity(intent);

            if (isTTSEnabled()) {
                speakText("فتح صفحة إضافة حساب جديد");
            }
        });
    }

    /**
     * تحميل الحسابات
     */
    private void loadAccounts() {
        viewModel.refreshAccounts();
    }

    /**
     * فتح تفاصيل الحساب
     */
    private void openAccountDetails(Account account) {
        Intent intent = new Intent(this, EnhancedAccountDetailActivity.class);
        intent.putExtra("account_id", account.getId());
        startActivity(intent);
    }

    /**
     * قراءة تفاصيل الحساب
     */
    private void readAccountDetails(Account account) {
        if (!isTTSEnabled()) {
            return;
        }

        StringBuilder content = new StringBuilder();
        content.append("تفاصيل الحساب. ");
        content.append("الاسم: ").append(account.getName()).append(". ");
        content.append("رقم الحساب: ").append(account.getCode()).append(". ");
        content.append("النوع: ").append(getAccountTypeName(account.getType())).append(". ");
        content.append("الرصيد الحالي: ").append(account.getBalance()).append(" ريال. ");

        if (account.getDescription() != null && !account.getDescription().isEmpty()) {
            content.append("الوصف: ").append(account.getDescription()).append(". ");
        }

        readDocument("حساب", content.toString());
    }

    /**
     * الحصول على اسم نوع الحساب
     */
    private String getAccountTypeName(String type) {
        if (type == null) return "غير محدد";

        switch (type.toLowerCase()) {
            case "asset": return "أصول";
            case "liability": return "خصوم";
            case "equity": return "حقوق ملكية";
            case "revenue": return "إيرادات";
            case "expense": return "مصروفات";
            default: return type;
        }
    }

    /**
     * تحديث القائمة المفلترة
     */
    private void updateFilteredList(String query) {
        filteredAccounts.clear();

        if (query.isEmpty()) {
            filteredAccounts.addAll(allAccounts);
        } else {
            String lowerQuery = query.toLowerCase();
            for (Account account : allAccounts) {
                if (matchesQuery(account, lowerQuery)) {
                    filteredAccounts.add(account);
                }
            }
        }

        accountAdapter.notifyDataSetChanged();

        // قراءة نتائج البحث
        if (isTTSEnabled() && !query.isEmpty()) {
            String resultText = filteredAccounts.size() + " نتيجة للبحث عن " + query;
            speakText(resultText);
        }
    }

    /**
     * فحص تطابق الحساب مع استعلام البحث
     */
    private boolean matchesQuery(Account account, String query) {
        return (account.getName() != null && account.getName().toLowerCase().contains(query)) ||
               (account.getCode() != null && account.getCode().toLowerCase().contains(query)) ||
               (account.getDescription() != null && account.getDescription().toLowerCase().contains(query)) ||
               (account.getType() != null && getAccountTypeName(account.getType()).toLowerCase().contains(query));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_account_list_enhanced, menu);

        // إعداد البحث المحسن
        setupEnhancedSearch(menu);

        return true;
    }

    /**
     * إعداد البحث المحسن مع الإدخال الصوتي
     */
    private void setupEnhancedSearch(Menu menu) {
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        if (searchView != null) {
            searchView.setQueryHint("البحث في الحسابات...");
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    performSearch(query);
                    return true;
                }
                
                @Override
                public boolean onQueryTextChange(String newText) {
                    updateFilteredList(newText);
                    return true;
                }
            });
            
            // إضافة الإدخال الصوتي لشريط البحث
            setupSearchViewVoiceInput(searchView);
        }
        
        // إعداد البحث الصوتي المستقل
        MenuItem voiceSearchItem = menu.findItem(R.id.action_voice_search);
        if (voiceSearchItem != null) {
            voiceSearchItem.setOnMenuItemClickListener(item -> {
                performVoiceSearch();
                return true;
            });
        }
    }
    
    /**
     * إعداد الإدخال الصوتي لشريط البحث
     */
    private void setupSearchViewVoiceInput(SearchView searchView) {
        // البحث عن مربع النص داخل SearchView
        androidx.appcompat.widget.SearchView.SearchAutoComplete searchEditText = 
            searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        
        if (searchEditText != null && isVoiceInputEnabled()) {
            // إضافة زر الإدخال الصوتي
            searchEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, android.R.drawable.ic_btn_speak_now, 0);
            searchEditText.setOnTouchListener((v, event) -> {
                if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (searchEditText.getRight() - searchEditText.getCompoundDrawables()[2].getBounds().width())) {
                        performSearchVoiceInput(searchEditText);
                        return true;
                    }
                }
                return false;
            });
        }
    }
    
    /**
     * تنفيذ الإدخال الصوتي للبحث
     */
    private void performSearchVoiceInput(androidx.appcompat.widget.SearchView.SearchAutoComplete searchEditText) {
        if (!isVoiceInputEnabled()) {
            Toast.makeText(this, "الإدخال الصوتي غير مفعل", Toast.LENGTH_SHORT).show();
            return;
        }
        
        voiceInputManager.startListening(searchEditText, new VoiceInputManager.VoiceInputCallback() {
            @Override
            public void onVoiceInputResult(String result) {
                searchEditText.setText(result);
                performSearch(result);
            }
            
            @Override
            public void onVoiceInputError(String error) {
                Toast.makeText(EnhancedAccountListActivity.this, "خطأ في البحث الصوتي: " + error, Toast.LENGTH_SHORT).show();
            }
            
            @Override
            public void onVoiceInputStarted() {
                // يمكن إضافة مؤشر بصري
            }
            
            @Override
            public void onVoiceInputStopped() {
                // إزالة المؤشر البصري
            }
        });
    }
    
    /**
     * البحث الصوتي المستقل
     */
    private void performVoiceSearch() {
        if (!isVoiceInputEnabled()) {
            Toast.makeText(this, "الإدخال الصوتي غير مفعل", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // إنشاء مربع نص مؤقت للبحث الصوتي
        android.widget.EditText tempEditText = new android.widget.EditText(this);
        
        voiceInputManager.startListening(tempEditText, new VoiceInputManager.VoiceInputCallback() {
            @Override
            public void onVoiceInputResult(String result) {
                performAdvancedVoiceSearch(result);
            }
            
            @Override
            public void onVoiceInputError(String error) {
                Toast.makeText(EnhancedAccountListActivity.this, "خطأ في البحث الصوتي: " + error, Toast.LENGTH_SHORT).show();
            }
            
            @Override
            public void onVoiceInputStarted() {
                if (isTTSEnabled()) {
                    speakText("ابدأ بقول ما تريد البحث عنه");
                }
            }
            
            @Override
            public void onVoiceInputStopped() {
                // انتهاء البحث الصوتي
            }
        });
    }
    
    /**
     * البحث الصوتي المتقدم
     */
    private void performAdvancedVoiceSearch(String query) {
        suggestionManager.performAdvancedSearch(query, 
            SearchSuggestionManager.SearchType.ACCOUNTS, 
            results -> {
                if (!results.isEmpty()) {
                    updateFilteredList(query);
                    if (isTTSEnabled()) {
                        speakText("تم العثور على " + results.size() + " نتائج للبحث عن " + query);
                    }
                } else {
                    if (isTTSEnabled()) {
                        speakText("لم يتم العثور على نتائج للبحث عن " + query);
                    }
                }
            });
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_sort_by_name:
                sortAccountsByName();
                return true;
            case R.id.action_sort_by_balance:
                sortAccountsByBalance();
                return true;
            case R.id.action_filter_by_type:
                showAccountTypeFilter();
                return true;
            case R.id.action_export:
                exportAccountsToExcel();
                return true;
            case R.id.action_read_summary:
                readAccountsSummary();
                return true;
            case R.id.action_settings:
                openAccountSettings();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    /**
     * ترتيب الحسابات حسب الاسم
     */
    private void sortAccountsByName() {
        filteredAccounts.sort((a1, a2) -> {
            if (a1.getName() == null) return 1;
            if (a2.getName() == null) return -1;
            return a1.getName().compareToIgnoreCase(a2.getName());
        });
        accountAdapter.notifyDataSetChanged();
        
        if (isTTSEnabled()) {
            speakText("تم ترتيب الحسابات حسب الاسم");
        }
    }
    
    /**
     * ترتيب الحسابات حسب الرصيد
     */
    private void sortAccountsByBalance() {
        filteredAccounts.sort((a1, a2) -> {
            return Double.compare(a2.getBalance(), a1.getBalance()); // ترتيب تنازلي
        });
        accountAdapter.notifyDataSetChanged();
        
        if (isTTSEnabled()) {
            speakText("تم ترتيب الحسابات حسب الرصيد من الأكبر للأصغر");
        }
    }
    
    /**
     * إظهار فلتر أنواع الحسابات
     */
    private void showAccountTypeFilter() {
        String[] types = {"جميع الأنواع", "أصول", "خصوم", "حقوق ملكية", "إيرادات", "مصروفات"};
        String[] typeValues = {"all", "asset", "liability", "equity", "revenue", "expense"};
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("فلترة حسب نوع الحساب");
        builder.setItems(types, (dialog, which) -> {
            filterAccountsByType(typeValues[which]);
            if (isTTSEnabled()) {
                speakText("تم تطبيق فلتر " + types[which]);
            }
        });
        builder.show();
    }
    
    /**
     * فلترة الحسابات حسب النوع
     */
    private void filterAccountsByType(String type) {
        if ("all".equals(type)) {
            filteredAccounts.clear();
            filteredAccounts.addAll(allAccounts);
        } else {
            filteredAccounts.clear();
            for (Account account : allAccounts) {
                if (type.equalsIgnoreCase(account.getType())) {
                    filteredAccounts.add(account);
                }
            }
        }
        accountAdapter.notifyDataSetChanged();
    }
    
    /**
     * تصدير الحسابات إلى إكسل
     */
    private void exportAccountsToExcel() {
        // سيتم تطوير هذه الميزة لاحقاً
        Toast.makeText(this, "سيتم تطوير تصدير الحسابات قريباً", Toast.LENGTH_SHORT).show();
    }
    
    /**
     * قراءة ملخص الحسابات
     */
    private void readAccountsSummary() {
        if (!isTTSEnabled()) {
            Toast.makeText(this, "تحويل النص إلى كلام غير مفعل", Toast.LENGTH_SHORT).show();
            return;
        }
        
        StringBuilder summary = new StringBuilder();
        summary.append("ملخص دليل الحسابات. ");
        summary.append("إجمالي عدد الحسابات: ").append(allAccounts.size()).append(". ");
        
        // حساب إجمالي الأرصدة
        double totalBalance = 0;
        int positiveBalances = 0;
        int negativeBalances = 0;
        
        for (Account account : allAccounts) {
            totalBalance += account.getBalance();
            if (account.getBalance() > 0) {
                positiveBalances++;
            } else if (account.getBalance() < 0) {
                negativeBalances++;
            }
        }
        
        summary.append("إجمالي الأرصدة: ").append(String.format("%.2f", totalBalance)).append(" ريال. ");
        summary.append("عدد الحسابات ذات الرصيد الموجب: ").append(positiveBalances).append(". ");
        summary.append("عدد الحسابات ذات الرصيد السالب: ").append(negativeBalances).append(". ");
        
        // إحصائيات حسب النوع
        int assets = 0, liabilities = 0, equity = 0, revenue = 0, expenses = 0;
        for (Account account : allAccounts) {
            if ("asset".equalsIgnoreCase(account.getType())) assets++;
            else if ("liability".equalsIgnoreCase(account.getType())) liabilities++;
            else if ("equity".equalsIgnoreCase(account.getType())) equity++;
            else if ("revenue".equalsIgnoreCase(account.getType())) revenue++;
            else if ("expense".equalsIgnoreCase(account.getType())) expenses++;
        }
        
        summary.append("الأصول: ").append(assets).append(". ");
        summary.append("الخصوم: ").append(liabilities).append(". ");
        summary.append("حقوق الملكية: ").append(equity).append(". ");
        summary.append("الإيرادات: ").append(revenue).append(". ");
        summary.append("المصروفات: ").append(expenses).append(". ");
        
        readDocument("ملخص دليل الحسابات", summary.toString());
    }
    
    /**
     * فتح إعدادات الحسابات
     */
    private void openAccountSettings() {
        Toast.makeText(this, "إعدادات الحسابات", Toast.LENGTH_SHORT).show();
    }
    
    @Override
    protected void performSearch(String query) {
        updateFilteredList(query);
    }
    
    @Override
    protected String getAutoReadContent() {
        return "صفحة دليل الحسابات. يحتوي على " + allAccounts.size() + " حساب. يمكنك البحث أو إضافة حساب جديد";
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // إعادة تحميل البيانات عند العودة للصفحة
        loadAccounts();
    }
}
EOF

# إنشاء ملف InvoiceDetailActivity.java مصحح
cat > app/src/main/java/com/example/androidapp/ui/invoice/InvoiceDetailActivity.java << 'EOF'
package com.example.androidapp.ui.invoice;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.lifecycle.ViewModelProvider;

import com.example.androidapp.R;
import com.example.androidapp.data.entities.Invoice;
import com.example.androidapp.data.entities.InvoiceItem;
import com.example.androidapp.ui.common.EnhancedBaseActivity;
import com.example.androidapp.ui.invoice.viewmodel.InvoiceViewModel;
import com.example.androidapp.utils.SessionManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class InvoiceDetailActivity extends EnhancedBaseActivity {

    // المكونات الأساسية للفاتورة
    private EditText etInvoiceNumber, etInvoiceDate, etInvoiceType, etSubTotal, etTax, etDiscount, etGrandTotal;
    private AutoCompleteTextView etCustomerName;
    private LinearLayout invoiceItemsContainer;
    private Button btnAddItem, btnSave, btnDelete, btnPreview, btnPrint, btnShare, btnReadInvoice;
    private ImageButton btnVoiceSearch;

    // المكونات المتقدمة
    private TextView tvInvoiceTitle, tvTotalInWords;
    private View layoutCustomerDetails, layoutCompanyInfo, layoutNotes;

    private InvoiceViewModel viewModel;
    private SessionManager sessionManager;
    private SharedPreferences invoiceSettings;
    private String companyId;
    private String invoiceId = null;
    private List<InvoiceItem> currentItems = new ArrayList<>();

    // إعدادات الفاتورة
    private boolean showCustomerDetails = true;
    private boolean showItemCodes = true;
    private boolean showTaxes = true;
    private boolean autoCalculateTax = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice_detail_enhanced);

        // تهيئة المدراء
        sessionManager = new SessionManager(this);
        invoiceSettings = getSharedPreferences("AppSettings", Context.MODE_PRIVATE);
        companyId = sessionManager.getCompanyId();

        if (companyId == null) {
            Toast.makeText(this, "معرف الشركة غير صالح", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // تحميل إعدادات الفاتورة
        loadInvoiceSettings();

        // تهيئة المكونات
        initializeViews();

        // إعداد الاقتراحات الذكية
        setupSmartSuggestions();

        // إعداد الأحداث
        setupEventListeners();

        // تهيئة ViewModel
        viewModel = new ViewModelProvider(this).get(InvoiceViewModel.class);

        // إعداد شريط الأدوات
        setupToolbar();

        // معالجة Intent للتحرير أو الإنشاء الجديد
        handleIntent();

        // إعداد التخطيط بناءً على الإعدادات
        setupLayoutBasedOnSettings();
    }

    /**
     * تحميل إعدادات الفاتورة من SharedPreferences
     */
    private void loadInvoiceSettings() {
        showCustomerDetails = invoiceSettings.getBoolean("show_customer_details", true);
        showItemCodes = invoiceSettings.getBoolean("show_item_codes", true);
        showTaxes = invoiceSettings.getBoolean("show_taxes", true);
        autoCalculateTax = invoiceSettings.getBoolean("auto_calculate_tax", true);
    }

    /**
     * تهيئة المكونات
     */
    private void initializeViews() {
        etInvoiceNumber = findViewById(R.id.et_invoice_number);
        etInvoiceDate = findViewById(R.id.et_invoice_date);
        etCustomerName = findViewById(R.id.et_customer_name);
        etInvoiceType = findViewById(R.id.et_invoice_type);
        etSubTotal = findViewById(R.id.et_sub_total);
        etTax = findViewById(R.id.et_tax);
        etDiscount = findViewById(R.id.et_discount);
        etGrandTotal = findViewById(R.id.et_grand_total);
        invoiceItemsContainer = findViewById(R.id.invoice_items_container);
        btnAddItem = findViewById(R.id.btn_add_item);
        btnSave = findViewById(R.id.btn_save_invoice);
        btnDelete = findViewById(R.id.btn_delete_invoice);
        btnPreview = findViewById(R.id.btn_preview_invoice);
        btnPrint = findViewById(R.id.btn_print_invoice);
        btnShare = findViewById(R.id.btn_share_invoice);
        btnReadInvoice = findViewById(R.id.btn_read_invoice);
        btnVoiceSearch = findViewById(R.id.btn_voice_search);

        // المكونات المتقدمة
        tvInvoiceTitle = findViewById(R.id.tv_invoice_title);
        tvTotalInWords = findViewById(R.id.tv_total_in_words);
        layoutCustomerDetails = findViewById(R.id.layout_customer_details);
        layoutCompanyInfo = findViewById(R.id.layout_company_info);
        layoutNotes = findViewById(R.id.layout_notes);

        // تعطيل الحسابات التلقائية
        etSubTotal.setEnabled(false);
        etGrandTotal.setEnabled(false);
    }

    /**
     * إعداد الاقتراحات الذكية
     */
    private void setupSmartSuggestions() {
        // اقتراحات أنواع الفواتير
        String[] invoiceTypes = {"فاتورة مبيعات", "فاتورة مشتريات", "فاتورة خدمات", "فاتورة مرتجعات"};
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_dropdown_item_1line, invoiceTypes);
        etInvoiceType.setAdapter(typeAdapter);

        // اقتراحات أسماء العملاء
        String[] customerNames = {"عميل نقدي", "شركة التقنية المتطورة", "مؤسسة النهضة", "شركة الأماني"};
        ArrayAdapter<String> customerAdapter = new ArrayAdapter<>(this,
            android.R.layout.simple_dropdown_item_1line, customerNames);
        etCustomerName.setAdapter(customerAdapter);
    }

    /**
     * إعداد مستمعات الأحداث
     */
    private void setupEventListeners() {
        btnAddItem.setOnClickListener(v -> addItemView(null));
        btnSave.setOnClickListener(v -> saveInvoice());
        btnDelete.setOnClickListener(v -> deleteInvoice());
        btnPreview.setOnClickListener(v -> previewInvoice());
        btnPrint.setOnClickListener(v -> printInvoice());
        btnShare.setOnClickListener(v -> shareInvoice());
        btnReadInvoice.setOnClickListener(v -> readInvoiceDetails());
        btnVoiceSearch.setOnClickListener(v -> performVoiceSearch());

        // مستمعات التغيير في الحسابات
        etTax.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) calculateTotals();
        });
        etDiscount.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) calculateTotals();
        });
    }

    /**
     * إعداد شريط الأدوات
     */
    private void setupToolbar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * معالجة Intent
     */
    private void handleIntent() {
        invoiceId = getIntent().getStringExtra("invoice_id");
        if (invoiceId != null) {
            setTitle("تعديل فاتورة");
            loadInvoiceDetails(invoiceId);
            btnDelete.setVisibility(View.VISIBLE);
        } else {
            setTitle("إضافة فاتورة جديدة");
            etInvoiceDate.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date()));
            btnDelete.setVisibility(View.GONE);
            addItemView(null); // إضافة عنصر فارغ للفاتورة الجديدة
        }
    }

    /**
     * إعداد التخطيط بناءً على الإعدادات
     */
    private void setupLayoutBasedOnSettings() {
        // إظهار/إخفاء تفاصيل العميل
        if (layoutCustomerDetails != null) {
            layoutCustomerDetails.setVisibility(showCustomerDetails ? View.VISIBLE : View.GONE);
        }
    }

    /**
     * تحميل تفاصيل الفاتورة
     */
    private void loadInvoiceDetails(String id) {
        viewModel.getInvoiceById(id, companyId).observe(this, invoice -> {
            if (invoice != null) {
                etInvoiceNumber.setText(invoice.getInvoiceNumber());
                etInvoiceDate.setText(invoice.getInvoiceDate());
                etCustomerName.setText(invoice.getCustomerName());
                etInvoiceType.setText(invoice.getInvoiceType());
                etSubTotal.setText(String.valueOf(invoice.getSubTotal()));
                etTax.setText(String.valueOf(invoice.getTaxAmount()));
                etDiscount.setText(String.valueOf(invoice.getDiscountAmount()));
                etGrandTotal.setText(String.valueOf(invoice.getTotalAmount()));

                // تحميل عناصر الفاتورة
                loadInvoiceItems(id);
            } else {
                Toast.makeText(this, "لم يتم العثور على الفاتورة", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    /**
     * تحميل عناصر الفاتورة
     */
    private void loadInvoiceItems(String invoiceId) {
        viewModel.getInvoiceItems(invoiceId).observe(this, items -> {
            if (items != null) {
                currentItems.clear();
                currentItems.addAll(items);
                invoiceItemsContainer.removeAllViews();
                for (InvoiceItem item : items) {
                    addItemView(item);
                }
                calculateTotals();
            }
        });
    }

    /**
     * إضافة عنصر فاتورة
     */
    private void addItemView(InvoiceItem item) {
        View itemView = getLayoutInflater().inflate(R.layout.invoice_item_row, invoiceItemsContainer, false);
        EditText etItemName = itemView.findViewById(R.id.itemName);
        EditText etItemCode = itemView.findViewById(R.id.itemCode);
        EditText etQuantity = itemView.findViewById(R.id.quantity);
        EditText etUnitPrice = itemView.findViewById(R.id.price);
        EditText etItemTotal = itemView.findViewById(R.id.total);
        View btnRemove = itemView.findViewById(R.id.btnDelete);

        etItemTotal.setEnabled(false);

        if (item != null) {
            etItemName.setText(item.getItemName());
            etItemCode.setText(item.getItemCode());
            etQuantity.setText(String.valueOf(item.getQuantity()));
            etUnitPrice.setText(String.valueOf(item.getUnitPrice()));
            etItemTotal.setText(String.valueOf(item.getTotal()));
        }

        btnRemove.setOnClickListener(v -> {
            invoiceItemsContainer.removeView(itemView);
            calculateTotals();
        });

        etQuantity.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) calculateItemTotal(itemView);
        });
        etUnitPrice.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) calculateItemTotal(itemView);
        });

        invoiceItemsContainer.addView(itemView);
        calculateTotals();
    }

    /**
     * حساب إجمالي العنصر
     */
    private void calculateItemTotal(View itemView) {
        EditText etQuantity = itemView.findViewById(R.id.quantity);
        EditText etUnitPrice = itemView.findViewById(R.id.price);
        EditText etItemTotal = itemView.findViewById(R.id.total);

        String quantityStr = etQuantity.getText().toString().trim();
        String unitPriceStr = etUnitPrice.getText().toString().trim();
        
        if (quantityStr.isEmpty() || unitPriceStr.isEmpty()) {
            etItemTotal.setText("0");
            return;
        }

        try {
            float quantity = Float.parseFloat(quantityStr);
            float unitPrice = Float.parseFloat(unitPriceStr);
            float itemTotal = quantity * unitPrice;
            etItemTotal.setText(String.valueOf(itemTotal));
            calculateTotals();
        } catch (NumberFormatException e) {
            etItemTotal.setText("0");
        }
    }

    /**
     * حساب الإجماليات
     */
    private void calculateTotals() {
        float subTotal = 0.0f;
        for (int i = 0; i < invoiceItemsContainer.getChildCount(); i++) {
            View itemView = invoiceItemsContainer.getChildAt(i);
            EditText etItemTotal = itemView.findViewById(R.id.total);
            String totalStr = etItemTotal.getText().toString().trim();
            if (!totalStr.isEmpty()) {
                try {
                    subTotal += Float.parseFloat(totalStr);
                } catch (NumberFormatException e) {
                    // تجاهل الأخطاء
                }
            }
        }
        etSubTotal.setText(String.valueOf(subTotal));

        float taxAmount = 0;
        String taxStr = etTax.getText().toString().trim();
        if (!taxStr.isEmpty()) {
            try {
                taxAmount = Float.parseFloat(taxStr);
            } catch (NumberFormatException e) {
                // تجاهل الأخطاء
            }
        }

        float discountAmount = 0;
        String discountStr = etDiscount.getText().toString().trim();
        if (!discountStr.isEmpty()) {
            try {
                discountAmount = Float.parseFloat(discountStr);
            } catch (NumberFormatException e) {
                // تجاهل الأخطاء
            }
        }

        float grandTotal = subTotal + taxAmount - discountAmount;
        etGrandTotal.setText(String.valueOf(grandTotal));

        // تحديث المبلغ كتابة
        updateTotalInWords(grandTotal);
    }

    /**
     * تحديث المبلغ كتابة
     */
    private void updateTotalInWords(float amount) {
        // هذه دالة مبسطة - يمكن تطويرها لتحويل الأرقام إلى كلمات
        String inWords = "مبلغ: " + amount + " ريال سعودي";
        if (tvTotalInWords != null) {
            tvTotalInWords.setText(inWords);
        }
    }

    /**
     * حفظ الفاتورة
     */
    private void saveInvoice() {
        String invoiceNumber = etInvoiceNumber.getText().toString().trim();
        String invoiceDate = etInvoiceDate.getText().toString().trim();
        String customerName = etCustomerName.getText().toString().trim();
        String invoiceType = etInvoiceType.getText().toString().trim();
        
        String subTotalStr = etSubTotal.getText().toString().trim();
        String taxStr = etTax.getText().toString().trim();
        String discountStr = etDiscount.getText().toString().trim();
        String grandTotalStr = etGrandTotal.getText().toString().trim();

        if (TextUtils.isEmpty(invoiceNumber) || TextUtils.isEmpty(invoiceDate) || 
            TextUtils.isEmpty(customerName) || TextUtils.isEmpty(invoiceType)) {
            Toast.makeText(this, "الرجاء تعبئة جميع الحقول الرئيسية", Toast.LENGTH_SHORT).show();
            return;
        }

        float subTotal = 0, taxAmount = 0, discountAmount = 0, grandTotal = 0;
        
        try {
            subTotal = Float.parseFloat(subTotalStr);
            taxAmount = Float.parseFloat(taxStr);
            discountAmount = Float.parseFloat(discountStr);
            grandTotal = Float.parseFloat(grandTotalStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "قيم الأرقام غير صالحة", Toast.LENGTH_SHORT).show();
            return;
        }

        Invoice invoice;
        if (invoiceId == null) {
            invoiceId = UUID.randomUUID().toString();
            invoice = new Invoice(invoiceId, companyId, customerName, null, invoiceNumber, 
                invoiceDate, null, grandTotal, "PENDING", invoiceType, 0, subTotal, taxAmount, discountAmount);
            viewModel.insert(invoice);
            Toast.makeText(this, "تم إضافة الفاتورة بنجاح", Toast.LENGTH_SHORT).show();
        } else {
            invoice = new Invoice(invoiceId, companyId, customerName, null, invoiceNumber, 
                invoiceDate, null, grandTotal, "PENDING", invoiceType, 0, subTotal, taxAmount, discountAmount);
            viewModel.update(invoice);
            Toast.makeText(this, "تم تحديث الفاتورة بنجاح", Toast.LENGTH_SHORT).show();
        }

        // حفظ عناصر الفاتورة
        saveInvoiceItems(invoiceId);
        
        finish();
    }

    /**
     * حفظ عناصر الفاتورة
     */
    private void saveInvoiceItems(String invoiceId) {
        // حذف العناصر القديمة أولاً
        viewModel.deleteInvoiceItems(invoiceId);
        
        // إضافة العناصر الجديدة
        for (int i = 0; i < invoiceItemsContainer.getChildCount(); i++) {
            View itemView = invoiceItemsContainer.getChildAt(i);
            saveInvoiceItem(itemView, invoiceId);
        }
    }

    /**
     * حفظ عنصر فاتورة فردي
     */
    private void saveInvoiceItem(View itemView, String invoiceId) {
        EditText etItemName = itemView.findViewById(R.id.itemName);
        EditText etItemCode = itemView.findViewById(R.id.itemCode);
        EditText etQuantity = itemView.findViewById(R.id.quantity);
        EditText etUnitPrice = itemView.findViewById(R.id.price);
        EditText etItemTotal = itemView.findViewById(R.id.total);

        String itemName = etItemName.getText().toString().trim();
        String itemCode = etItemCode.getText().toString().trim();
        
        if (TextUtils.isEmpty(itemName)) {
            return; // تخطي العناصر الفارغة
        }

        float quantity = 0, unitPrice = 0, total = 0;
        
        try {
            quantity = Float.parseFloat(etQuantity.getText().toString().trim());
            unitPrice = Float.parseFloat(etUnitPrice.getText().toString().trim());
            total = Float.parseFloat(etItemTotal.getText().toString().trim());
        } catch (NumberFormatException e) {
            // تجاهل الأخطاء
        }

        InvoiceItem item = new InvoiceItem(
            UUID.randomUUID().toString(),
            invoiceId,
            itemName,
            itemCode,
            quantity,
            unitPrice,
            total
        );

        viewModel.insertInvoiceItem(item);
    }

    /**
     * حذف الفاتورة
     */
    private void deleteInvoice() {
        if (invoiceId != null) {
            new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("حذف الفاتورة")
                .setMessage("هل أنت متأكد من حذف هذه الفاتورة؟")
                .setPositiveButton("نعم", (dialog, which) -> {
                    viewModel.getInvoiceById(invoiceId, companyId).observe(this, invoice -> {
                        if (invoice != null) {
                            viewModel.delete(invoice);
                            Toast.makeText(this, "تم حذف الفاتورة بنجاح", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                })
                .setNegativeButton("لا", null)
                .show();
        }
    }

    /**
     * معاينة الفاتورة
     */
    private void previewInvoice() {
        Toast.makeText(this, "معاينة الفاتورة", Toast.LENGTH_SHORT).show();
        // سيتم تطوير هذه الميزة لاحقاً
    }

    /**
     * طباعة الفاتورة
     */
    private void printInvoice() {
        Toast.makeText(this, "طباعة الفاتورة", Toast.LENGTH_SHORT).show();
        // سيتم تطوير هذه الميزة لاحقاً
    }

    /**
     * مشاركة الفاتورة
     */
    private void shareInvoice() {
        Toast.makeText(this, "مشاركة الفاتورة", Toast.LENGTH_SHORT).show();
        // سيتم تطوير هذه الميزة لاحقاً
    }

    /**
     * قراءة تفاصيل الفاتورة
     */
    private void readInvoiceDetails() {
        if (!isTTSEnabled()) {
            Toast.makeText(this, "تحويل النص إلى كلام غير مفعل", Toast.LENGTH_SHORT).show();
            return;
        }

        StringBuilder content = new StringBuilder();
        content.append("تفاصيل الفاتورة. ");
        content.append("رقم الفاتورة: ").append(etInvoiceNumber.getText().toString()).append(". ");
        content.append("التاريخ: ").append(etInvoiceDate.getText().toString()).append(". ");
        content.append("العميل: ").append(etCustomerName.getText().toString()).append(". ");
        content.append("النوع: ").append(etInvoiceType.getText().toString()).append(". ");
        content.append("الإجمالي: ").append(etGrandTotal.getText().toString()).append(" ريال. ");

        readDocument("فاتورة", content.toString());
    }

    /**
     * البحث الصوتي
     */
    private void performVoiceSearch() {
        if (!isVoiceInputEnabled()) {
            Toast.makeText(this, "الإدخال الصوتي غير مفعل", Toast.LENGTH_SHORT).show();
            return;
        }

        voiceInputManager.startListening(etCustomerName, new VoiceInputManager.VoiceInputCallback() {
            @Override
            public void onVoiceInputResult(String result) {
                etCustomerName.setText(result);
            }

            @Override
            public void onVoiceInputError(String error) {
                Toast.makeText(InvoiceDetailActivity.this, "خطأ في الإدخال الصوتي: " + error, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onVoiceInputStarted() {
                if (isTTSEnabled()) {
                    speakText("قل اسم العميل");
                }
            }

            @Override
            public void onVoiceInputStopped() {
                // انتهاء الإدخال الصوتي
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_invoice_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void performSearch(String query) {
        // البحث في الفاتورة - يمكن تنفيذه لاحقاً
        Toast.makeText(this, "البحث في الفاتورة: " + query, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected String getAutoReadContent() {
        return "صفحة تفاصيل الفاتورة. يمكنك إضافة أو تعديل الفاتورة وعناصرها";
    }
}
EOF

echo "✅ تم تصحيح الملفات بنجاح!"
echo "📁 الملفات المصححة:"
echo "   - DetailedSettingsActivity.java"
echo "   - EnhancedAccountListActivity.java" 
echo "   - InvoiceDetailActivity.java"
