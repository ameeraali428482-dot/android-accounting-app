package com.example.androidapp.ui.admin;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.androidapp.R;
import com.example.androidapp.adapters.RolesAdapter;
import com.example.androidapp.adapters.PermissionsAdapter;
import com.example.androidapp.utils.PermissionManager;
import com.example.androidapp.utils.RoleManager;
import com.example.androidapp.viewmodels.PermissionsManagementViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textview.MaterialTextView;

/**
 * شاشة إدارة الأدوار والصلاحيات
 * تتيح إنشاء وتعديل وحذف الأدوار والصلاحيات
 */
public class PermissionsManagementActivity extends AppCompatActivity {
    
    private static final String TAG = "PermissionsManagementActivity";
    
    private PermissionsManagementViewModel viewModel;
    private PermissionManager permissionManager;
    private RoleManager roleManager;
    
    // واجهة المستخدم
    private TabLayout tabLayout;
    private RecyclerView recyclerView;
    private FloatingActionButton fabAdd;
    private MaterialTextView tvEmptyState;
    
    // المحولات
    private RolesAdapter rolesAdapter;
    private PermissionsAdapter permissionsAdapter;
    
    private int currentTab = 0; // 0: الأدوار، 1: الصلاحيات
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permissions_management);
        
        initializeComponents();
        setupUI();
        setupObservers();
        checkPermissions();
        loadData();
    }
    
    private void initializeComponents() {
        viewModel = new ViewModelProvider(this).get(PermissionsManagementViewModel.class);
        permissionManager = new PermissionManager(this);
        roleManager = new RoleManager(this);
        
        // ربط العناصر
        tabLayout = findViewById(R.id.tabLayout);
        recyclerView = findViewById(R.id.recyclerView);
        fabAdd = findViewById(R.id.fabAdd);
        tvEmptyState = findViewById(R.id.tvEmptyState);
        
        // إعداد المحولات
        rolesAdapter = new RolesAdapter(this::onRoleSelected, this::onRoleEdit, this::onRoleDelete);
        permissionsAdapter = new PermissionsAdapter(this::onPermissionSelected, 
                                                   this::onPermissionEdit, this::onPermissionDelete);
    }
    
    private void setupUI() {
        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setTitle("إدارة الأدوار والصلاحيات");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
        // إعداد التبويبات
        tabLayout.addTab(tabLayout.newTab().setText("الأدوار"));
        tabLayout.addTab(tabLayout.newTab().setText("الصلاحيات"));
        
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                currentTab = tab.getPosition();
                updateRecyclerView();
                updateFabIcon();
            }
            
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
        
        // إعداد RecyclerView
        updateRecyclerView();
        
        // إعداد أزرار العمل
        fabAdd.setOnClickListener(v -> addNewItem());
        updateFabIcon();
    }
    
    private void setupObservers() {
        // مراقبة الأدوار
        viewModel.getRoles().observe(this, roles -> {
            if (roles != null) {
                rolesAdapter.updateRoles(roles);
                updateEmptyState();
            }
        });
        
        // مراقبة الصلاحيات
        viewModel.getPermissions().observe(this, permissions -> {
            if (permissions != null) {
                permissionsAdapter.updatePermissions(permissions);
                updateEmptyState();
            }
        });
        
        // مراقبة حالة التحميل
        viewModel.getIsLoading().observe(this, isLoading -> {
            findViewById(R.id.progressBar).setVisibility(
                isLoading ? View.VISIBLE : View.GONE
            );
        });
        
        // مراقبة الرسائل
        viewModel.getMessage().observe(this, message -> {
            if (message != null && !message.isEmpty()) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void checkPermissions() {
        permissionManager.hasPermission(PermissionManager.PERM_MANAGE_USERS)
            .thenAccept(hasPermission -> {
                runOnUiThread(() -> {
                    if (!hasPermission) {
                        Toast.makeText(this, "ليس لديك صلاحية لإدارة الأدوار والصلاحيات", 
                            Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
            });
    }
    
    private void loadData() {
        viewModel.loadRoles();
        viewModel.loadPermissions();
    }
    
    private void updateRecyclerView() {
        if (currentTab == 0) {
            // عرض الأدوار
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(rolesAdapter);
        } else {
            // عرض الصلاحيات
            recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
            recyclerView.setAdapter(permissionsAdapter);
        }
    }
    
    private void updateFabIcon() {
        if (currentTab == 0) {
            fabAdd.setImageResource(R.drawable.ic_add_role);
        } else {
            fabAdd.setImageResource(R.drawable.ic_add_permission);
        }
    }
    
    private void updateEmptyState() {
        boolean isEmpty;
        String emptyMessage;
        
        if (currentTab == 0) {
            isEmpty = rolesAdapter.getItemCount() == 0;
            emptyMessage = "لا توجد أدوار محددة";
        } else {
            isEmpty = permissionsAdapter.getItemCount() == 0;
            emptyMessage = "لا توجد صلاحيات محددة";
        }
        
        tvEmptyState.setText(emptyMessage);
        tvEmptyState.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }
    
    private void addNewItem() {
        if (currentTab == 0) {
            showCreateRoleDialog();
        } else {
            showCreatePermissionDialog();
        }
    }
    
    private void showCreateRoleDialog() {
        // عرض حوار إنشاء دور جديد
        // يمكن استخدام DialogFragment أو Activity منفصلة
        RoleCreateDialogFragment dialog = new RoleCreateDialogFragment();
        dialog.setOnRoleCreatedListener(roleName -> {
            viewModel.createRole(roleName);
        });
        dialog.show(getSupportFragmentManager(), "create_role");
    }
    
    private void showCreatePermissionDialog() {
        // عرض حوار إنشاء صلاحية جديدة
        PermissionCreateDialogFragment dialog = new PermissionCreateDialogFragment();
        dialog.setOnPermissionCreatedListener((permissionName, description) -> {
            viewModel.createPermission(permissionName, description);
        });
        dialog.show(getSupportFragmentManager(), "create_permission");
    }
    
    // معالجات الأحداث للأدوار
    private void onRoleSelected(String roleName) {
        // عرض تفاصيل الدور
        RoleDetailDialogFragment dialog = RoleDetailDialogFragment.newInstance(roleName);
        dialog.show(getSupportFragmentManager(), "role_detail");
    }
    
    private void onRoleEdit(String roleName) {
        // تعديل الدور
        RoleEditDialogFragment dialog = RoleEditDialogFragment.newInstance(roleName);
        dialog.setOnRoleUpdatedListener(() -> {
            viewModel.loadRoles();
        });
        dialog.show(getSupportFragmentManager(), "edit_role");
    }
    
    private void onRoleDelete(String roleName) {
        new AlertDialog.Builder(this)
            .setTitle("تأكيد الحذف")
            .setMessage("هل أنت متأكد من حذف الدور: " + roleName + "؟")
            .setPositiveButton("حذف", (dialog, which) -> {
                viewModel.deleteRole(roleName);
            })
            .setNegativeButton("إلغاء", null)
            .show();
    }
    
    // معالجات الأحداث للصلاحيات
    private void onPermissionSelected(String permissionName) {
        // عرض تفاصيل الصلاحية
        PermissionDetailDialogFragment dialog = PermissionDetailDialogFragment.newInstance(permissionName);
        dialog.show(getSupportFragmentManager(), "permission_detail");
    }
    
    private void onPermissionEdit(String permissionName) {
        // تعديل الصلاحية
        PermissionEditDialogFragment dialog = PermissionEditDialogFragment.newInstance(permissionName);
        dialog.setOnPermissionUpdatedListener(() -> {
            viewModel.loadPermissions();
        });
        dialog.show(getSupportFragmentManager(), "edit_permission");
    }
    
    private void onPermissionDelete(String permissionName) {
        new AlertDialog.Builder(this)
            .setTitle("تأكيد الحذف")
            .setMessage("هل أنت متأكد من حذف الصلاحية: " + permissionName + "؟")
            .setPositiveButton("حذف", (dialog, which) -> {
                viewModel.deletePermission(permissionName);
            })
            .setNegativeButton("إلغاء", null)
            .show();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_permissions_management, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        
        if (id == android.R.id.home) {
            finish();
            return true;
        } else if (id == R.id.action_bulk_assign) {
            showBulkAssignDialog();
            return true;
        } else if (id == R.id.action_export) {
            exportRolesAndPermissions();
            return true;
        } else if (id == R.id.action_import) {
            importRolesAndPermissions();
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }
    
    private void showBulkAssignDialog() {
        // عرض حوار تعيين الأدوار والصلاحيات بالجملة
        BulkAssignDialogFragment dialog = new BulkAssignDialogFragment();
        dialog.show(getSupportFragmentManager(), "bulk_assign");
    }
    
    private void exportRolesAndPermissions() {
        viewModel.exportRolesAndPermissions();
    }
    
    private void importRolesAndPermissions() {
        // فتح حوار اختيار الملف للاستيراد
        viewModel.importRolesAndPermissions();
    }
}