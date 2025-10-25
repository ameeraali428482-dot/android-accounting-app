package com.example.androidapp.ui.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.androidapp.R;
import com.example.androidapp.adapters.ActiveSessionsAdapter;
import com.example.androidapp.adapters.UserActivitiesAdapter;
import com.example.androidapp.utils.ConcurrentSessionManager;
import com.example.androidapp.utils.PermissionManager;
import com.example.androidapp.viewmodels.AdminDashboardViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textview.MaterialTextView;

/**
 * لوحة تحكم الإدارة المتقدمة
 * تعرض الجلسات النشطة، أنشطة المستخدمين، والإحصائيات
 */
public class AdminDashboardActivity extends AppCompatActivity {
    
    private static final String TAG = "AdminDashboardActivity";
    
    private AdminDashboardViewModel viewModel;
    private ConcurrentSessionManager sessionManager;
    private PermissionManager permissionManager;
    
    // واجهة المستخدم
    private TabLayout tabLayout;
    private RecyclerView recyclerView;
    private FloatingActionButton fabRefresh;
    
    // إحصائيات
    private MaterialTextView tvActiveUsers, tvTotalSessions, tvOnlineUsers, tvDataChanges;
    
    // المحولات
    private ActiveSessionsAdapter sessionsAdapter;
    private UserActivitiesAdapter activitiesAdapter;
    
    private int currentTab = 0; // 0: الجلسات، 1: الأنشطة
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);
        
        initializeComponents();
        setupUI();
        setupObservers();
        checkPermissions();
    }
    
    private void initializeComponents() {
        viewModel = new ViewModelProvider(this).get(AdminDashboardViewModel.class);
        sessionManager = ConcurrentSessionManager.getInstance(this);
        permissionManager = new PermissionManager(this);
        
        // ربط العناصر
        tabLayout = findViewById(R.id.tabLayout);
        recyclerView = findViewById(R.id.recyclerView);
        fabRefresh = findViewById(R.id.fabRefresh);
        
        tvActiveUsers = findViewById(R.id.tvActiveUsers);
        tvTotalSessions = findViewById(R.id.tvTotalSessions);
        tvOnlineUsers = findViewById(R.id.tvOnlineUsers);
        tvDataChanges = findViewById(R.id.tvDataChanges);
        
        // إعداد المحولات
        sessionsAdapter = new ActiveSessionsAdapter(this::onSessionSelected);
        activitiesAdapter = new UserActivitiesAdapter();
    }
    
    private void setupUI() {
        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setTitle("لوحة تحكم الإدارة");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
        // إعداد التبويبات
        tabLayout.addTab(tabLayout.newTab().setText("الجلسات النشطة"));
        tabLayout.addTab(tabLayout.newTab().setText("أنشطة المستخدمين"));
        
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                currentTab = tab.getPosition();
                updateRecyclerView();
            }
            
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
        
        // إعداد RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        updateRecyclerView();
        
        // إعداد أزرار العمل
        fabRefresh.setOnClickListener(v -> refreshData());
    }
    
    private void setupObservers() {
        // مراقبة الجلسات النشطة
        sessionManager.getActiveSessionsLiveData().observe(this, sessions -> {
            if (sessions != null) {
                sessionsAdapter.updateSessions(sessions);
                updateStatistics();
            }
        });
        
        // مراقبة أنشطة المستخدمين
        sessionManager.getUserActivitiesLiveData().observe(this, activities -> {
            if (activities != null) {
                activitiesAdapter.updateActivities(activities);
                updateStatistics();
            }
        });
        
        // مراقبة إحصائيات النظام
        viewModel.getSystemStatistics().observe(this, stats -> {
            if (stats != null) {
                displayStatistics(stats);
            }
        });
    }
    
    private void checkPermissions() {
        permissionManager.hasPermission(PermissionManager.PERM_MANAGE_USERS)
            .thenAccept(hasPermission -> {
                runOnUiThread(() -> {
                    if (!hasPermission) {
                        Toast.makeText(this, "ليس لديك صلاحية لعرض هذه الصفحة", 
                            Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
            });
    }
    
    private void updateRecyclerView() {
        if (currentTab == 0) {
            recyclerView.setAdapter(sessionsAdapter);
        } else {
            recyclerView.setAdapter(activitiesAdapter);
        }
    }
    
    private void updateStatistics() {
        viewModel.refreshStatistics();
    }
    
    private void displayStatistics(AdminDashboardViewModel.SystemStatistics stats) {
        tvActiveUsers.setText(String.valueOf(stats.activeUsers));
        tvTotalSessions.setText(String.valueOf(stats.totalSessions));
        tvOnlineUsers.setText(String.valueOf(stats.onlineUsers));
        tvDataChanges.setText(String.valueOf(stats.recentChanges));
    }
    
    private void refreshData() {
        viewModel.refreshData();
        Toast.makeText(this, "تم تحديث البيانات", Toast.LENGTH_SHORT).show();
    }
    
    private void onSessionSelected(ConcurrentSessionManager.ActiveSession session) {
        Intent intent = new Intent(this, SessionDetailActivity.class);
        intent.putExtra("session_id", session.sessionId);
        intent.putExtra("user_id", session.userId);
        startActivity(intent);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_admin_dashboard, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        
        if (id == android.R.id.home) {
            finish();
            return true;
        } else if (id == R.id.action_users) {
            startActivity(new Intent(this, UsersManagementActivity.class));
            return true;
        } else if (id == R.id.action_permissions) {
            startActivity(new Intent(this, PermissionsManagementActivity.class));
            return true;
        } else if (id == R.id.action_settings) {
            startActivity(new Intent(this, AdminSettingsActivity.class));
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        refreshData();
    }
}