package com.example.androidapp.ui.admin;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.example.androidapp.R;
import com.example.androidapp.models.UserSession;
import com.example.androidapp.utils.ConcurrentSessionManager;
import com.example.androidapp.utils.PermissionManager;
import com.example.androidapp.services.RealTimeNotificationService;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.textview.MaterialTextView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * نشاط إدارة الجلسات النشطة
 * يعرض جميع الجلسات النشطة ويسمح بإدارتها ومراقبة نشاط المستخدمين
 */
public class ActiveSessionsActivity extends AppCompatActivity {
    
    private static final String TAG = "ActiveSessionsActivity";
    
    private ConcurrentSessionManager sessionManager;
    private PermissionManager permissionManager;
    private RealTimeNotificationService notificationService;
    
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private MaterialTextView tvEmptyState;
    private MaterialTextView tvTotalSessions;
    private MaterialTextView tvActiveUsers;
    private ActiveSessionsAdapter adapter;
    
    private List<UserSession> activeSessions = new ArrayList<>();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_sessions);
        
        initializeComponents();
        setupUI();
        loadActiveSessions();
        
        // إعداد شريط الأدوات
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("الجلسات النشطة");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }
    
    private void initializeComponents() {
        sessionManager = new ConcurrentSessionManager(this);
        permissionManager = new PermissionManager(this);
        notificationService = new RealTimeNotificationService();
        
        // التحقق من الصلاحيات
        if (!permissionManager.hasAdminPermission()) {
            finish();
            return;
        }
        
        recyclerView = findViewById(R.id.recycler_view_sessions);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        tvEmptyState = findViewById(R.id.tv_empty_state);
        tvTotalSessions = findViewById(R.id.tv_total_sessions);
        tvActiveUsers = findViewById(R.id.tv_active_users);
        
        adapter = new ActiveSessionsAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }
    
    private void setupUI() {
        swipeRefreshLayout.setOnRefreshListener(this::loadActiveSessions);
        swipeRefreshLayout.setColorSchemeColors(getColor(R.color.primary));
    }
    
    private void loadActiveSessions() {
        swipeRefreshLayout.setRefreshing(true);
        
        new Thread(() -> {
            try {
                List<UserSession> sessions = sessionManager.getAllActiveSessions();
                
                runOnUiThread(() -> {
                    activeSessions.clear();
                    activeSessions.addAll(sessions);
                    adapter.notifyDataSetChanged();
                    
                    updateStatistics();
                    
                    // إظهار حالة فارغة إذا لم توجد جلسات
                    if (activeSessions.isEmpty()) {
                        tvEmptyState.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    } else {
                        tvEmptyState.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                    
                    swipeRefreshLayout.setRefreshing(false);
                });
                
            } catch (Exception e) {
                runOnUiThread(() -> {
                    swipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(this, "خطأ في تحميل الجلسات", Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }
    
    private void updateStatistics() {
        tvTotalSessions.setText("إجمالي الجلسات: " + activeSessions.size());
        
        // حساب عدد المستخدمين الفريدين
        List<String> uniqueUsers = new ArrayList<>();
        for (UserSession session : activeSessions) {
            if (!uniqueUsers.contains(session.getUserId())) {
                uniqueUsers.add(session.getUserId());
            }
        }
        tvActiveUsers.setText("المستخدمين النشطين: " + uniqueUsers.size());
    }
    
    private void terminateSession(UserSession session) {
        new AlertDialog.Builder(this)
            .setTitle("إنهاء الجلسة")
            .setMessage("هل أنت متأكد من إنهاء جلسة المستخدم: " + session.getUserId() + "؟")
            .setPositiveButton("نعم", (dialog, which) -> {
                new Thread(() -> {
                    try {
                        boolean success = sessionManager.terminateSession(session.getSessionId());
                        
                        if (success) {
                            // إرسال إشعار للمستخدم
                            notificationService.notifySessionTerminated(session.getUserId());
                        }
                        
                        runOnUiThread(() -> {
                            if (success) {
                                Toast.makeText(this, "تم إنهاء الجلسة بنجاح", 
                                             Toast.LENGTH_SHORT).show();
                                loadActiveSessions(); // تحديث القائمة
                            } else {
                                Toast.makeText(this, "فشل في إنهاء الجلسة", 
                                             Toast.LENGTH_SHORT).show();
                            }
                        });
                        
                    } catch (Exception e) {
                        runOnUiThread(() -> 
                            Toast.makeText(this, "خطأ في إنهاء الجلسة", 
                                         Toast.LENGTH_SHORT).show());
                    }
                }).start();
            })
            .setNegativeButton("إلغاء", null)
            .show();
    }
    
    private void sendMessageToUser(UserSession session) {
        // إظهار حوار لإرسال رسالة للمستخدم
        // TODO: تنفيذ إرسال الرسائل
    }
    
    private void viewUserActivity(UserSession session) {
        // عرض نشاط المستخدم التفصيلي
        // TODO: تنفيذ عرض النشاط التفصيلي
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_active_sessions, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_refresh:
                loadActiveSessions();
                return true;
            case R.id.action_terminate_all:
                terminateAllSessions();
                return true;
            case R.id.action_broadcast_message:
                broadcastMessage();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    private void terminateAllSessions() {
        new AlertDialog.Builder(this)
            .setTitle("إنهاء جميع الجلسات")
            .setMessage("تحذير: سيتم إنهاء جميع الجلسات النشطة!\nسيتم تسجيل خروج جميع المستخدمين.")
            .setPositiveButton("تأكيد", (dialog, which) -> {
                new Thread(() -> {
                    try {
                        int terminatedCount = sessionManager.terminateAllSessions();
                        
                        // إرسال إشعار لجميع المستخدمين
                        notificationService.broadcastSessionTermination();
                        
                        runOnUiThread(() -> {
                            Toast.makeText(this, 
                                "تم إنهاء " + terminatedCount + " جلسة", 
                                Toast.LENGTH_SHORT).show();
                            loadActiveSessions();
                        });
                        
                    } catch (Exception e) {
                        runOnUiThread(() -> 
                            Toast.makeText(this, "خطأ في إنهاء الجلسات", 
                                         Toast.LENGTH_SHORT).show());
                    }
                }).start();
            })
            .setNegativeButton("إلغاء", null)
            .show();
    }
    
    private void broadcastMessage() {
        // إرسال رسالة لجميع المستخدمين النشطين
        // TODO: تنفيذ الإرسال الجماعي
    }
    
    /**
     * محول عرض الجلسات النشطة
     */
    private class ActiveSessionsAdapter extends RecyclerView.Adapter<ActiveSessionsAdapter.SessionViewHolder> {
        
        @NonNull
        @Override
        public SessionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_active_session, parent, false);
            return new SessionViewHolder(view);
        }
        
        @Override
        public void onBindViewHolder(@NonNull SessionViewHolder holder, int position) {
            UserSession session = activeSessions.get(position);
            holder.bind(session);
        }
        
        @Override
        public int getItemCount() {
            return activeSessions.size();
        }
        
        class SessionViewHolder extends RecyclerView.ViewHolder {
            
            MaterialCardView cardView;
            MaterialTextView tvUserId;
            MaterialTextView tvDeviceInfo;
            MaterialTextView tvLoginTime;
            MaterialTextView tvLastActivity;
            MaterialTextView tvActiveLocks;
            MaterialTextView tvSessionDuration;
            Chip chipOnlineStatus;
            MaterialButton btnTerminate;
            MaterialButton btnMessage;
            MaterialButton btnViewActivity;
            
            public SessionViewHolder(@NonNull View itemView) {
                super(itemView);
                
                cardView = itemView.findViewById(R.id.card_view);
                tvUserId = itemView.findViewById(R.id.tv_user_id);
                tvDeviceInfo = itemView.findViewById(R.id.tv_device_info);
                tvLoginTime = itemView.findViewById(R.id.tv_login_time);
                tvLastActivity = itemView.findViewById(R.id.tv_last_activity);
                tvActiveLocks = itemView.findViewById(R.id.tv_active_locks);
                tvSessionDuration = itemView.findViewById(R.id.tv_session_duration);
                chipOnlineStatus = itemView.findViewById(R.id.chip_online_status);
                btnTerminate = itemView.findViewById(R.id.btn_terminate);
                btnMessage = itemView.findViewById(R.id.btn_message);
                btnViewActivity = itemView.findViewById(R.id.btn_view_activity);
            }
            
            public void bind(UserSession session) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM HH:mm", Locale.getDefault());
                
                tvUserId.setText("المستخدم: " + session.getUserId());
                tvDeviceInfo.setText("الجهاز: " + session.getDeviceInfo());
                tvLoginTime.setText("وقت الدخول: " + sdf.format(new Date(session.getLoginTime())));
                tvLastActivity.setText("آخر نشاط: " + sdf.format(new Date(session.getLastActivityTime())));
                
                // عدد الأقفال النشطة للمستخدم
                int userLocks = sessionManager.getUserActiveLocks(session.getUserId());
                tvActiveLocks.setText("الأقفال النشطة: " + userLocks);
                
                // مدة الجلسة
                long duration = System.currentTimeMillis() - session.getLoginTime();
                long hours = duration / (1000 * 60 * 60);
                long minutes = (duration % (1000 * 60 * 60)) / (1000 * 60);
                tvSessionDuration.setText("مدة الجلسة: " + hours + "س " + minutes + "د");
                
                // حالة الاتصال
                boolean isOnline = (System.currentTimeMillis() - session.getLastActivityTime()) < 300000; // 5 دقائق
                if (isOnline) {
                    chipOnlineStatus.setText("متصل");
                    chipOnlineStatus.setChipBackgroundColorResource(R.color.green);
                } else {
                    chipOnlineStatus.setText("غير نشط");
                    chipOnlineStatus.setChipBackgroundColorResource(R.color.orange);
                }
                
                // تلوين البطاقة حسب عدد الأقفال
                if (userLocks > 5) {
                    cardView.setCardBackgroundColor(Color.parseColor("#FFEBEE")); // أحمر فاتح
                } else if (userLocks > 0) {
                    cardView.setCardBackgroundColor(Color.parseColor("#FFF3E0")); // برتقالي فاتح
                }
                
                // أحداث الأزرار
                btnTerminate.setOnClickListener(v -> terminateSession(session));
                btnMessage.setOnClickListener(v -> sendMessageToUser(session));
                btnViewActivity.setOnClickListener(v -> viewUserActivity(session));
            }
        }
    }
}

/**
 * نموذج جلسة المستخدم
 */
class UserSession {
    private String sessionId;
    private String userId;
    private String deviceInfo;
    private long loginTime;
    private long lastActivityTime;
    private String ipAddress;
    
    // البناء والـ getters والـ setters
    public UserSession(String sessionId, String userId, String deviceInfo, 
                      long loginTime, long lastActivityTime, String ipAddress) {
        this.sessionId = sessionId;
        this.userId = userId;
        this.deviceInfo = deviceInfo;
        this.loginTime = loginTime;
        this.lastActivityTime = lastActivityTime;
        this.ipAddress = ipAddress;
    }
    
    // Getters
    public String getSessionId() { return sessionId; }
    public String getUserId() { return userId; }
    public String getDeviceInfo() { return deviceInfo; }
    public long getLoginTime() { return loginTime; }
    public long getLastActivityTime() { return lastActivityTime; }
    public String getIpAddress() { return ipAddress; }
    
    // Setters
    public void setLastActivityTime(long lastActivityTime) { 
        this.lastActivityTime = lastActivityTime; 
    }
}