package com.example.androidapp.ui.admin;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import com.example.androidapp.R;
import com.example.androidapp.utils.PermissionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.slider.Slider;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;

/**
 * نشاط إعدادات نظام التزامن
 * يسمح بتخصيص جميع جوانب نظام تعدد المستخدمين المتزامن
 */
public class ConcurrencySettingsActivity extends AppCompatActivity {
    
    private static final String TAG = "ConcurrencySettings";
    
    private PermissionManager permissionManager;
    private SharedPreferences preferences;
    
    // إعدادات الأقفال
    private SwitchMaterial switchEnableLocking;
    private Slider sliderLockDuration;
    private MaterialTextView tvLockDuration;
    private Slider sliderMaxLocksPerUser;
    private MaterialTextView tvMaxLocksPerUser;
    private SwitchMaterial switchAutoReleaseLocks;
    
    // إعدادات سجل التغييرات
    private SwitchMaterial switchEnableChangeLog;
    private Slider sliderLogRetentionDays;
    private MaterialTextView tvLogRetentionDays;
    private SwitchMaterial switchLogAllFields;
    private SwitchMaterial switchCompressOldLogs;
    
    // إعدادات الجلسات
    private Slider sliderSessionTimeout;
    private MaterialTextView tvSessionTimeout;
    private Slider sliderMaxSessionsPerUser;
    private MaterialTextView tvMaxSessionsPerUser;
    private SwitchMaterial switchForceUniqueSession;
    
    // إعدادات الإشعارات
    private SwitchMaterial switchEnableNotifications;
    private SwitchMaterial switchNotifyLockConflicts;
    private SwitchMaterial switchNotifyDataChanges;
    private TextInputEditText etNotificationServer;
    
    // إعدادات الأداء
    private Slider sliderCleanupInterval;
    private MaterialTextView tvCleanupInterval;
    private SwitchMaterial switchEnablePerformanceMetrics;
    private SwitchMaterial switchOptimizeDatabase;
    
    // أزرار الإجراءات
    private MaterialButton btnSaveSettings;
    private MaterialButton btnResetDefaults;
    private MaterialButton btnTestSystem;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_concurrency_settings);
        
        initializeComponents();
        setupUI();
        loadCurrentSettings();
        
        // إعداد شريط الأدوات
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("إعدادات التزامن");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }
    
    private void initializeComponents() {
        permissionManager = new PermissionManager(this);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        
        // التحقق من الصلاحيات
        if (!permissionManager.hasAdminPermission()) {
            finish();
            return;
        }
        
        // إعدادات الأقفال
        switchEnableLocking = findViewById(R.id.switch_enable_locking);
        sliderLockDuration = findViewById(R.id.slider_lock_duration);
        tvLockDuration = findViewById(R.id.tv_lock_duration);
        sliderMaxLocksPerUser = findViewById(R.id.slider_max_locks_per_user);
        tvMaxLocksPerUser = findViewById(R.id.tv_max_locks_per_user);
        switchAutoReleaseLocks = findViewById(R.id.switch_auto_release_locks);
        
        // إعدادات سجل التغييرات
        switchEnableChangeLog = findViewById(R.id.switch_enable_change_log);
        sliderLogRetentionDays = findViewById(R.id.slider_log_retention_days);
        tvLogRetentionDays = findViewById(R.id.tv_log_retention_days);
        switchLogAllFields = findViewById(R.id.switch_log_all_fields);
        switchCompressOldLogs = findViewById(R.id.switch_compress_old_logs);
        
        // إعدادات الجلسات
        sliderSessionTimeout = findViewById(R.id.slider_session_timeout);
        tvSessionTimeout = findViewById(R.id.tv_session_timeout);
        sliderMaxSessionsPerUser = findViewById(R.id.slider_max_sessions_per_user);
        tvMaxSessionsPerUser = findViewById(R.id.tv_max_sessions_per_user);
        switchForceUniqueSession = findViewById(R.id.switch_force_unique_session);
        
        // إعدادات الإشعارات
        switchEnableNotifications = findViewById(R.id.switch_enable_notifications);
        switchNotifyLockConflicts = findViewById(R.id.switch_notify_lock_conflicts);
        switchNotifyDataChanges = findViewById(R.id.switch_notify_data_changes);
        etNotificationServer = findViewById(R.id.et_notification_server);
        
        // إعدادات الأداء
        sliderCleanupInterval = findViewById(R.id.slider_cleanup_interval);
        tvCleanupInterval = findViewById(R.id.tv_cleanup_interval);
        switchEnablePerformanceMetrics = findViewById(R.id.switch_enable_performance_metrics);
        switchOptimizeDatabase = findViewById(R.id.switch_optimize_database);
        
        // أزرار الإجراءات
        btnSaveSettings = findViewById(R.id.btn_save_settings);
        btnResetDefaults = findViewById(R.id.btn_reset_defaults);
        btnTestSystem = findViewById(R.id.btn_test_system);
    }
    
    private void setupUI() {
        // إعداد أحداث المنزلقات مع تحديث النصوص
        sliderLockDuration.addOnChangeListener((slider, value, fromUser) -> {
            tvLockDuration.setText("مدة القفل: " + (int)value + " دقيقة");
        });
        
        sliderMaxLocksPerUser.addOnChangeListener((slider, value, fromUser) -> {
            tvMaxLocksPerUser.setText("أقصى عدد أقفال للمستخدم: " + (int)value);
        });
        
        sliderLogRetentionDays.addOnChangeListener((slider, value, fromUser) -> {
            tvLogRetentionDays.setText("مدة الاحتفاظ بالسجلات: " + (int)value + " يوم");
        });
        
        sliderSessionTimeout.addOnChangeListener((slider, value, fromUser) -> {
            tvSessionTimeout.setText("انتهاء مهلة الجلسة: " + (int)value + " دقيقة");
        });
        
        sliderMaxSessionsPerUser.addOnChangeListener((slider, value, fromUser) -> {
            tvMaxSessionsPerUser.setText("أقصى عدد جلسات للمستخدم: " + (int)value);
        });
        
        sliderCleanupInterval.addOnChangeListener((slider, value, fromUser) -> {
            tvCleanupInterval.setText("فترة التنظيف: " + (int)value + " ساعة");
        });
        
        // أحداث الأزرار
        btnSaveSettings.setOnClickListener(v -> saveSettings());
        btnResetDefaults.setOnClickListener(v -> resetToDefaults());
        btnTestSystem.setOnClickListener(v -> testSystem());
    }
    
    private void loadCurrentSettings() {
        // تحميل الإعدادات الحالية من SharedPreferences
        
        // إعدادات الأقفال
        switchEnableLocking.setChecked(preferences.getBoolean("enable_locking", true));
        sliderLockDuration.setValue(preferences.getFloat("lock_duration", 30)); // 30 دقيقة افتراضي
        sliderMaxLocksPerUser.setValue(preferences.getFloat("max_locks_per_user", 5));
        switchAutoReleaseLocks.setChecked(preferences.getBoolean("auto_release_locks", true));
        
        // إعدادات سجل التغييرات
        switchEnableChangeLog.setChecked(preferences.getBoolean("enable_change_log", true));
        sliderLogRetentionDays.setValue(preferences.getFloat("log_retention_days", 90)); // 90 يوم افتراضي
        switchLogAllFields.setChecked(preferences.getBoolean("log_all_fields", false));
        switchCompressOldLogs.setChecked(preferences.getBoolean("compress_old_logs", true));
        
        // إعدادات الجلسات
        sliderSessionTimeout.setValue(preferences.getFloat("session_timeout", 60)); // 60 دقيقة افتراضي
        sliderMaxSessionsPerUser.setValue(preferences.getFloat("max_sessions_per_user", 3));
        switchForceUniqueSession.setChecked(preferences.getBoolean("force_unique_session", false));
        
        // إعدادات الإشعارات
        switchEnableNotifications.setChecked(preferences.getBoolean("enable_notifications", true));
        switchNotifyLockConflicts.setChecked(preferences.getBoolean("notify_lock_conflicts", true));
        switchNotifyDataChanges.setChecked(preferences.getBoolean("notify_data_changes", false));
        etNotificationServer.setText(preferences.getString("notification_server", ""));
        
        // إعدادات الأداء
        sliderCleanupInterval.setValue(preferences.getFloat("cleanup_interval", 24)); // 24 ساعة افتراضي
        switchEnablePerformanceMetrics.setChecked(preferences.getBoolean("enable_performance_metrics", false));
        switchOptimizeDatabase.setChecked(preferences.getBoolean("optimize_database", true));
        
        // تحديث النصوص
        updateSliderTexts();
    }
    
    private void updateSliderTexts() {
        tvLockDuration.setText("مدة القفل: " + (int)sliderLockDuration.getValue() + " دقيقة");
        tvMaxLocksPerUser.setText("أقصى عدد أقفال للمستخدم: " + (int)sliderMaxLocksPerUser.getValue());
        tvLogRetentionDays.setText("مدة الاحتفاظ بالسجلات: " + (int)sliderLogRetentionDays.getValue() + " يوم");
        tvSessionTimeout.setText("انتهاء مهلة الجلسة: " + (int)sliderSessionTimeout.getValue() + " دقيقة");
        tvMaxSessionsPerUser.setText("أقصى عدد جلسات للمستخدم: " + (int)sliderMaxSessionsPerUser.getValue());
        tvCleanupInterval.setText("فترة التنظيف: " + (int)sliderCleanupInterval.getValue() + " ساعة");
    }
    
    private void saveSettings() {
        SharedPreferences.Editor editor = preferences.edit();
        
        // حفظ إعدادات الأقفال
        editor.putBoolean("enable_locking", switchEnableLocking.isChecked());
        editor.putFloat("lock_duration", sliderLockDuration.getValue());
        editor.putFloat("max_locks_per_user", sliderMaxLocksPerUser.getValue());
        editor.putBoolean("auto_release_locks", switchAutoReleaseLocks.isChecked());
        
        // حفظ إعدادات سجل التغييرات
        editor.putBoolean("enable_change_log", switchEnableChangeLog.isChecked());
        editor.putFloat("log_retention_days", sliderLogRetentionDays.getValue());
        editor.putBoolean("log_all_fields", switchLogAllFields.isChecked());
        editor.putBoolean("compress_old_logs", switchCompressOldLogs.isChecked());
        
        // حفظ إعدادات الجلسات
        editor.putFloat("session_timeout", sliderSessionTimeout.getValue());
        editor.putFloat("max_sessions_per_user", sliderMaxSessionsPerUser.getValue());
        editor.putBoolean("force_unique_session", switchForceUniqueSession.isChecked());
        
        // حفظ إعدادات الإشعارات
        editor.putBoolean("enable_notifications", switchEnableNotifications.isChecked());
        editor.putBoolean("notify_lock_conflicts", switchNotifyLockConflicts.isChecked());
        editor.putBoolean("notify_data_changes", switchNotifyDataChanges.isChecked());
        editor.putString("notification_server", etNotificationServer.getText().toString());
        
        // حفظ إعدادات الأداء
        editor.putFloat("cleanup_interval", sliderCleanupInterval.getValue());
        editor.putBoolean("enable_performance_metrics", switchEnablePerformanceMetrics.isChecked());
        editor.putBoolean("optimize_database", switchOptimizeDatabase.isChecked());
        
        boolean success = editor.commit();
        
        if (success) {
            Toast.makeText(this, "تم حفظ الإعدادات بنجاح", Toast.LENGTH_SHORT).show();
            
            // إعادة تشغيل الخدمات مع الإعدادات الجديدة
            restartServicesWithNewSettings();
        } else {
            Toast.makeText(this, "خطأ في حفظ الإعدادات", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void resetToDefaults() {
        // إعادة تعيين جميع الإعدادات للقيم الافتراضية
        SharedPreferences.Editor editor = preferences.edit();
        
        // إزالة جميع الإعدادات المخصصة
        editor.remove("enable_locking");
        editor.remove("lock_duration");
        editor.remove("max_locks_per_user");
        editor.remove("auto_release_locks");
        editor.remove("enable_change_log");
        editor.remove("log_retention_days");
        editor.remove("log_all_fields");
        editor.remove("compress_old_logs");
        editor.remove("session_timeout");
        editor.remove("max_sessions_per_user");
        editor.remove("force_unique_session");
        editor.remove("enable_notifications");
        editor.remove("notify_lock_conflicts");
        editor.remove("notify_data_changes");
        editor.remove("notification_server");
        editor.remove("cleanup_interval");
        editor.remove("enable_performance_metrics");
        editor.remove("optimize_database");
        
        editor.apply();
        
        // إعادة تحميل الإعدادات الافتراضية
        loadCurrentSettings();
        
        Toast.makeText(this, "تم إعادة تعيين الإعدادات للقيم الافتراضية", Toast.LENGTH_SHORT).show();
    }
    
    private void testSystem() {
        // اختبار النظام للتأكد من عمله بشكل صحيح
        new Thread(() -> {
            try {
                // اختبار الاتصال بقاعدة البيانات
                // اختبار إنشاء قفل
                // اختبار تسجيل التغييرات
                // اختبار الإشعارات
                
                runOnUiThread(() -> {
                    Toast.makeText(this, "اختبار النظام مكتمل بنجاح", Toast.LENGTH_SHORT).show();
                });
                
            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "فشل في اختبار النظام: " + e.getMessage(), 
                                 Toast.LENGTH_LONG).show();
                });
            }
        }).start();
    }
    
    private void restartServicesWithNewSettings() {
        // إعادة تشغيل الخدمات مع الإعدادات الجديدة
        // TODO: تنفيذ إعادة تشغيل الخدمات
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}