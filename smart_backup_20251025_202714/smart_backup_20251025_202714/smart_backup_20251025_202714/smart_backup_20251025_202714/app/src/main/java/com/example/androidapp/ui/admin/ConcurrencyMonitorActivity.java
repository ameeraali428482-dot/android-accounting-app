package com.example.androidapp.ui.admin;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.example.androidapp.R;
import com.example.androidapp.utils.ConcurrentSessionManager;
import com.example.androidapp.utils.ChangeTrackingManager;
import com.example.androidapp.utils.PermissionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * لوحة مراقبة التزامن الرئيسية
 * تعرض إحصائيات شاملة عن نظام تعدد المستخدمين المتزامن
 */
public class ConcurrencyMonitorActivity extends AppCompatActivity {
    
    private static final String TAG = "ConcurrencyMonitor";
    
    private ConcurrentSessionManager sessionManager;
    private ChangeTrackingManager changeManager;
    private PermissionManager permissionManager;
    
    // عناصر الواجهة - الإحصائيات
    private MaterialTextView tvActiveLocksCount;
    private MaterialTextView tvActiveSessions;
    private MaterialTextView tvTodayChanges;
    private MaterialTextView tvConflictsToday;
    private MaterialTextView tvLastUpdate;
    
    // البطاقات
    private MaterialCardView cardActiveLocks;
    private MaterialCardView cardChangeLogs;
    private MaterialCardView cardActiveSessions;
    private MaterialCardView cardSettings;
    
    // أزرار الإجراءات
    private MaterialButton btnViewLocks;
    private MaterialButton btnViewChanges;
    private MaterialButton btnViewSessions;
    private MaterialButton btnSettings;
    
    // التحديث
    private SwipeRefreshLayout swipeRefreshLayout;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_concurrency_monitor);
        
        initializeComponents();
        setupUI();
        setupClickListeners();
        loadStatistics();
        
        // إعداد شريط الأدوات
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("مراقب التزامن");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }
    
    private void initializeComponents() {
        sessionManager = new ConcurrentSessionManager(this);
        changeManager = new ChangeTrackingManager(this);
        permissionManager = new PermissionManager(this);
        
        // التحقق من الصلاحيات
        if (!permissionManager.hasAdminPermission()) {
            finish();
            return;
        }
        
        // ربط عناصر الواجهة
        tvActiveLocksCount = findViewById(R.id.tv_active_locks_count);
        tvActiveSessions = findViewById(R.id.tv_active_sessions);
        tvTodayChanges = findViewById(R.id.tv_today_changes);
        tvConflictsToday = findViewById(R.id.tv_conflicts_today);
        tvLastUpdate = findViewById(R.id.tv_last_update);
        
        cardActiveLocks = findViewById(R.id.card_active_locks);
        cardChangeLogs = findViewById(R.id.card_change_logs);
        cardActiveSessions = findViewById(R.id.card_active_sessions);
        cardSettings = findViewById(R.id.card_settings);
        
        btnViewLocks = findViewById(R.id.btn_view_locks);
        btnViewChanges = findViewById(R.id.btn_view_changes);
        btnViewSessions = findViewById(R.id.btn_view_sessions);
        btnSettings = findViewById(R.id.btn_settings);
        
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
    }
    
    private void setupUI() {
        // إعداد التحديث بالسحب
        swipeRefreshLayout.setOnRefreshListener(this::loadStatistics);
        swipeRefreshLayout.setColorSchemeColors(getColor(R.color.primary));
    }
    
    private void setupClickListeners() {
        // عرض الأقفال النشطة
        btnViewLocks.setOnClickListener(v -> {
            Intent intent = new Intent(this, RecordLocksManagementActivity.class);
            startActivity(intent);
        });
        
        cardActiveLocks.setOnClickListener(v -> btnViewLocks.performClick());
        
        // عرض سجل التغييرات
        btnViewChanges.setOnClickListener(v -> {
            Intent intent = new Intent(this, ChangeLogActivity.class);
            startActivity(intent);
        });
        
        cardChangeLogs.setOnClickListener(v -> btnViewChanges.performClick());
        
        // عرض الجلسات النشطة
        btnViewSessions.setOnClickListener(v -> {
            Intent intent = new Intent(this, ActiveSessionsActivity.class);
            startActivity(intent);
        });
        
        cardActiveSessions.setOnClickListener(v -> btnViewSessions.performClick());
        
        // الإعدادات
        btnSettings.setOnClickListener(v -> {
            Intent intent = new Intent(this, ConcurrencySettingsActivity.class);
            startActivity(intent);
        });
        
        cardSettings.setOnClickListener(v -> btnSettings.performClick());
    }
    
    private void loadStatistics() {
        swipeRefreshLayout.setRefreshing(true);
        
        // تحميل الإحصائيات في خيط منفصل
        new Thread(() -> {
            try {
                // جلب الإحصائيات
                int activeLocksCount = sessionManager.getActiveLocksCount();
                int activeSessionsCount = sessionManager.getActiveSessionsCount();
                int todayChangesCount = changeManager.getTodayChangesCount();
                int conflictsCount = sessionManager.getTodayConflictsCount();
                
                // تحديث الواجهة في الخيط الرئيسي
                runOnUiThread(() -> {
                    updateStatistics(activeLocksCount, activeSessionsCount, 
                                   todayChangesCount, conflictsCount);
                    swipeRefreshLayout.setRefreshing(false);
                });
                
            } catch (Exception e) {
                runOnUiThread(() -> {
                    swipeRefreshLayout.setRefreshing(false);
                    // إظهار رسالة خطأ
                });
            }
        }).start();
    }
    
    private void updateStatistics(int activeLocksCount, int activeSessionsCount, 
                                int todayChangesCount, int conflictsCount) {
        
        // تحديث الأرقام
        tvActiveLocksCount.setText(String.valueOf(activeLocksCount));
        tvActiveSessions.setText(String.valueOf(activeSessionsCount));
        tvTodayChanges.setText(String.valueOf(todayChangesCount));
        tvConflictsToday.setText(String.valueOf(conflictsCount));
        
        // تحديث وقت آخر تحديث
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        tvLastUpdate.setText("آخر تحديث: " + sdf.format(new Date()));
        
        // تلوين البطاقات حسب الحالة
        updateCardColors(activeLocksCount, conflictsCount);
    }
    
    private void updateCardColors(int activeLocksCount, int conflictsCount) {
        // بطاقة الأقفال النشطة
        if (activeLocksCount > 10) {
            cardActiveLocks.setCardBackgroundColor(Color.parseColor("#FFEB3B")); // أصفر للتنبيه
        } else if (activeLocksCount > 0) {
            cardActiveLocks.setCardBackgroundColor(Color.parseColor("#4CAF50")); // أخضر عادي
        } else {
            cardActiveLocks.setCardBackgroundColor(Color.parseColor("#E3F2FD")); // أزرق فاتح
        }
        
        // بطاقة التعارضات
        if (conflictsCount > 5) {
            cardSettings.setCardBackgroundColor(Color.parseColor("#F44336")); // أحمر للخطر
        } else if (conflictsCount > 0) {
            cardSettings.setCardBackgroundColor(Color.parseColor("#FF9800")); // برتقالي للتحذير
        }
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_concurrency_monitor, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_refresh:
                loadStatistics();
                return true;
            case R.id.action_export_logs:
                exportLogs();
                return true;
            case R.id.action_clear_old_logs:
                clearOldLogs();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    private void exportLogs() {
        // تصدير السجلات
        // TODO: تنفيذ تصدير السجلات
    }
    
    private void clearOldLogs() {
        // حذف السجلات القديمة
        // TODO: تنفيذ حذف السجلات القديمة
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        loadStatistics(); // تحديث الإحصائيات عند العودة للنشاط
    }
}