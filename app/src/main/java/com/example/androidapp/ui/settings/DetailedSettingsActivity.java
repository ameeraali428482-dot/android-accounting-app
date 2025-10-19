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
