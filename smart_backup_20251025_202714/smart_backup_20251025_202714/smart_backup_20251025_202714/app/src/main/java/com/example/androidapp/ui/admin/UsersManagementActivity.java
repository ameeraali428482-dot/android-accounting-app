package com.example.androidapp.ui.admin;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.androidapp.R;
import com.example.androidapp.adapters.UsersAdapter;
import com.example.androidapp.data.entities.User;
import com.example.androidapp.utils.PermissionManager;
import com.example.androidapp.utils.RoleManager;
import com.example.androidapp.viewmodels.UsersManagementViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.search.SearchBar;
import com.google.android.material.search.SearchView;

import java.util.List;

/**
 * شاشة إدارة المستخدمين المتقدمة
 * تتيح عرض وإدارة جميع المستخدمين والأدوار والصلاحيات
 */
public class UsersManagementActivity extends AppCompatActivity implements UsersAdapter.OnUserActionListener {
    
    private static final String TAG = "UsersManagementActivity";
    
    private UsersManagementViewModel viewModel;
    private PermissionManager permissionManager;
    private RoleManager roleManager;
    
    // واجهة المستخدم
    private SearchBar searchBar;
    private SearchView searchView;
    private RecyclerView recyclerView;
    private FloatingActionButton fabAddUser;
    
    // المحول
    private UsersAdapter usersAdapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_management);
        
        initializeComponents();
        setupUI();
        setupObservers();
        checkPermissions();
        loadUsers();
    }
    
    private void initializeComponents() {
        viewModel = new ViewModelProvider(this).get(UsersManagementViewModel.class);
        permissionManager = new PermissionManager(this);
        roleManager = new RoleManager(this);
        
        // ربط العناصر
        searchBar = findViewById(R.id.searchBar);
        searchView = findViewById(R.id.searchView);
        recyclerView = findViewById(R.id.recyclerView);
        fabAddUser = findViewById(R.id.fabAddUser);
        
        // إعداد المحول
        usersAdapter = new UsersAdapter(this);
    }
    
    private void setupUI() {
        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setTitle("إدارة المستخدمين");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
        // إعداد البحث
        searchView.getEditText().setOnEditorActionListener((v, actionId, event) -> {
            searchView.hide();
            searchUsers(searchView.getText().toString());
            return false;
        });
        
        // إعداد RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(usersAdapter);
        
        // إعداد أزرار العمل
        fabAddUser.setOnClickListener(v -> addNewUser());
    }
    
    private void setupObservers() {
        // مراقبة قائمة المستخدمين
        viewModel.getUsers().observe(this, users -> {
            if (users != null) {
                usersAdapter.updateUsers(users);
                updateEmptyState(users.isEmpty());
            }
        });
        
        // مراقبة حالة التحميل
        viewModel.getIsLoading().observe(this, isLoading -> {
            // عرض مؤشر التحميل
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
                        Toast.makeText(this, "ليس لديك صلاحية لإدارة المستخدمين", 
                            Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
            });
    }
    
    private void loadUsers() {
        viewModel.loadUsers();
    }
    
    private void searchUsers(String query) {
        viewModel.searchUsers(query);
    }
    
    private void addNewUser() {
        Intent intent = new Intent(this, UserFormActivity.class);
        intent.putExtra("mode", "create");
        startActivityForResult(intent, 100);
    }
    
    private void updateEmptyState(boolean isEmpty) {
        findViewById(R.id.emptyStateLayout).setVisibility(
            isEmpty ? View.VISIBLE : View.GONE
        );
        recyclerView.setVisibility(
            isEmpty ? View.GONE : View.VISIBLE
        );
    }
    
    // تنفيذ واجهة OnUserActionListener
    @Override
    public void onUserClick(User user) {
        Intent intent = new Intent(this, UserDetailActivity.class);
        intent.putExtra("user_id", user.getId());
        startActivity(intent);
    }
    
    @Override
    public void onEditUser(User user) {
        Intent intent = new Intent(this, UserFormActivity.class);
        intent.putExtra("mode", "edit");
        intent.putExtra("user_id", user.getId());
        startActivityForResult(intent, 101);
    }
    
    @Override
    public void onDeleteUser(User user) {
        new AlertDialog.Builder(this)
            .setTitle("تأكيد الحذف")
            .setMessage("هل أنت متأكد من حذف المستخدم: " + user.getName() + "؟")
            .setPositiveButton("حذف", (dialog, which) -> {
                viewModel.deleteUser(user.getId());
            })
            .setNegativeButton("إلغاء", null)
            .show();
    }
    
    @Override
    public void onToggleUserStatus(User user) {
        String action = user.isActive() ? "تعطيل" : "تفعيل";
        new AlertDialog.Builder(this)
            .setTitle("تأكيد " + action)
            .setMessage("هل أنت متأكد من " + action + " المستخدم: " + user.getName() + "؟")
            .setPositiveButton(action, (dialog, which) -> {
                viewModel.toggleUserStatus(user.getId(), !user.isActive());
            })
            .setNegativeButton("إلغاء", null)
            .show();
    }
    
    @Override
    public void onManageUserRoles(User user) {
        Intent intent = new Intent(this, UserRolesActivity.class);
        intent.putExtra("user_id", user.getId());
        intent.putExtra("user_name", user.getName());
        startActivity(intent);
    }
    
    @Override
    public void onViewUserSessions(User user) {
        Intent intent = new Intent(this, UserSessionsActivity.class);
        intent.putExtra("user_id", user.getId());
        intent.putExtra("user_name", user.getName());
        startActivity(intent);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_users_management, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        
        if (id == android.R.id.home) {
            finish();
            return true;
        } else if (id == R.id.action_filter) {
            showFilterDialog();
            return true;
        } else if (id == R.id.action_export) {
            exportUsers();
            return true;
        } else if (id == R.id.action_refresh) {
            loadUsers();
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }
    
    private void showFilterDialog() {
        // عرض حوار تصفية المستخدمين
        String[] filters = {"جميع المستخدمين", "المستخدمون النشطون", "المستخدمون المعطلون", 
                           "المستخدمون المتصلون", "الإداريون"};
        
        new AlertDialog.Builder(this)
            .setTitle("تصفية المستخدمين")
            .setItems(filters, (dialog, which) -> {
                viewModel.filterUsers(which);
            })
            .show();
    }
    
    private void exportUsers() {
        permissionManager.hasPermission(PermissionManager.PERM_EXPORT_DATA)
            .thenAccept(hasPermission -> {
                runOnUiThread(() -> {
                    if (hasPermission) {
                        viewModel.exportUsers();
                    } else {
                        Toast.makeText(this, "ليس لديك صلاحية لتصدير البيانات", 
                            Toast.LENGTH_SHORT).show();
                    }
                });
            });
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (resultCode == RESULT_OK) {
            if (requestCode == 100 || requestCode == 101) {
                // تم إضافة أو تعديل مستخدم
                loadUsers();
            }
        }
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // تحديث البيانات عند العودة للشاشة
        loadUsers();
    }
}