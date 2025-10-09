#!/bin/bash

echo "Starting comprehensive fix script..."

# --- Create Menu Files ---
mkdir -p app/src/main/res/menu

cat > app/src/main/res/menu/menu_list.xml << 'EOM'
<?xml version="1.0" encoding="utf-8"?>
<menu xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto">
    <item
        android:id="@+id/action_refresh"
        android:title="Refresh"
        android:icon="@android:drawable/ic_menu_rotate"
        app:showAsAction="ifRoom" />
</menu>
EOM

cat > app/src/main/res/menu/menu_detail.xml << 'EOM'
<?xml version="1.0" encoding="utf-8"?>
<menu xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto">
    <item
        android:id="@+id/action_save"
        android:title="Save"
        android:icon="@android:drawable/ic_menu_save"
        app:showAsAction="ifRoom" />
    <item
        android:id="@+id/action_delete"
        android:title="Delete"
        app:showAsAction="never" />
</menu>
EOM

echo "Menu files created."

# --- Fix Activity Files ---

# OrderListActivity
cat > app/src/main/java/com/example/androidapp/ui/order/OrderListActivity.java << 'EOP'
package com.example.androidapp.ui.order;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.androidapp.R;
import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.entities.Order;
import com.example.androidapp.ui.common.GenericAdapter;
import com.example.androidapp.utils.SessionManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class OrderListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private GenericAdapter<Order> adapter;
    private AppDatabase database;
    private SessionManager sessionManager;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("ar", "SA"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);

        database = AppDatabase.getDatabase(this);
        sessionManager = new SessionManager(this);

        initViews();
        setupRecyclerView();
        loadOrders();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recycler_view);
        FloatingActionButton fab = findViewById(R.id.fab);

        setTitle("إدارة الطلبيات");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fab.setOnClickListener(v -> {
            Intent intent = new Intent(this, OrderDetailActivity.class);
            startActivity(intent);
        });
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        adapter = new GenericAdapter<>(
                new ArrayList<>(),
                R.layout.order_list_row,
                (view, order) -> {
                    TextView tvOrderId = view.findViewById(R.id.tv_order_id);
                    TextView tvOrderDate = view.findViewById(R.id.tv_order_date);
                    TextView tvTotalAmount = view.findViewById(R.id.tv_order_total_amount);
                    TextView tvStatus = view.findViewById(R.id.tv_order_status);
                    TextView tvNotes = view.findViewById(R.id.tv_order_notes);

                    tvOrderId.setText("طلبية #" + order.getId());
                    tvOrderDate.setText("التاريخ: " + dateFormat.format(order.getOrderDate()));
                    tvTotalAmount.setText("المبلغ: " + currencyFormat.format(order.getTotalAmount()));
                    tvStatus.setText(order.getStatus());
                    
                    if (order.getNotes() != null && !order.getNotes().isEmpty()) {
                        tvNotes.setText(order.getNotes());
                    } else {
                        tvNotes.setText("لا توجد ملاحظات");
                    }

                    int statusBackground;
                    switch (order.getStatus()) {
                        case "Completed":
                            statusBackground = R.drawable.status_active_background;
                            break;
                        case "Processing":
                            statusBackground = R.drawable.status_draft_background;
                            break;
                        case "Cancelled":
                            statusBackground = R.drawable.status_inactive_background;
                            break;
                        default:
                            statusBackground = R.drawable.status_pending_background;
                            break;
                    }
                    tvStatus.setBackgroundResource(statusBackground);
                },
                order -> {
                    Intent intent = new Intent(this, OrderDetailActivity.class);
                    intent.putExtra("order_id", order.getId());
                    startActivity(intent);
                }
        );
        
        recyclerView.setAdapter(adapter);
    }

    private void loadOrders() {
        database.orderDao().getAllOrders(sessionManager.getCurrentCompanyId()).observe(this, orders -> {
            if (orders != null) {
                adapter.updateData(orders);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else if (item.getItemId() == R.id.action_refresh) {
            loadOrders();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadOrders();
    }
}
EOP

# RoleListActivity
cat > app/src/main/java/com/example/androidapp/ui/role/RoleListActivity.java << 'EOP'
package com.example.androidapp.ui.role;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.androidapp.R;
import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.entities.Role;
import com.example.androidapp.ui.common.GenericAdapter;
import com.example.androidapp.utils.SessionManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;

public class RoleListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private GenericAdapter<Role> adapter;
    private AppDatabase database;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role_list);

        database = AppDatabase.getDatabase(this);
        sessionManager = new SessionManager(this);

        initViews();
        setupRecyclerView();
        loadRoles();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recycler_view);
        FloatingActionButton fab = findViewById(R.id.fab);

        setTitle("إدارة الأدوار");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fab.setOnClickListener(v -> {
            Intent intent = new Intent(this, RoleDetailActivity.class);
            startActivity(intent);
        });
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        adapter = new GenericAdapter<>(
                new ArrayList<>(),
                R.layout.role_list_row,
                (view, role) -> {
                    TextView tvRoleName = view.findViewById(R.id.tv_role_name);
                    TextView tvRoleDescription = view.findViewById(R.id.tv_role_description);

                    tvRoleName.setText(role.getName());
                    tvRoleDescription.setText(role.getDescription());
                },
                role -> {
                    Intent intent = new Intent(this, RoleDetailActivity.class);
                    intent.putExtra("role_id", role.getId());
                    startActivity(intent);
                }
        );
        
        recyclerView.setAdapter(adapter);
    }

    private void loadRoles() {
        database.roleDao().getAllRoles(sessionManager.getCurrentCompanyId()).observe(this, roles -> {
            if (roles != null) {
                adapter.updateData(roles);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else if (item.getItemId() == R.id.action_refresh) {
            loadRoles();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadRoles();
    }
}
EOP

# RoleDetailActivity
cat > app/src/main/java/com/example/androidapp/ui/role/RoleDetailActivity.java << 'EOP'
package com.example.androidapp.ui.role;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.androidapp.R;
import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.entities.Permission;
import com.example.androidapp.data.entities.Role;
import com.example.androidapp.ui.common.GenericAdapter;
import com.example.androidapp.utils.SessionManager;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RoleDetailActivity extends AppCompatActivity {
    private EditText etRoleName, etRoleDescription;
    private RecyclerView rvPermissions;
    private GenericAdapter<Permission> permissionsAdapter;
    private AppDatabase database;
    private SessionManager sessionManager;
    private String roleId = null;
    private Role currentRole;
    private List<Permission> allPermissions = new ArrayList<>();
    private List<Permission> selectedPermissions = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role_detail);

        database = AppDatabase.getDatabase(this);
        sessionManager = new SessionManager(this);

        initViews();
        setupPermissionsRecyclerView();
        loadAllPermissions();

        roleId = getIntent().getStringExtra("role_id");
        if (roleId != null) {
            setTitle("تعديل الدور");
            loadRole();
        } else {
            setTitle("إضافة دور جديد");
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initViews() {
        etRoleName = findViewById(R.id.et_role_name);
        etRoleDescription = findViewById(R.id.et_role_description);
        rvPermissions = findViewById(R.id.rv_permissions);
    }

    private void setupPermissionsRecyclerView() {
        rvPermissions.setLayoutManager(new LinearLayoutManager(this));
        permissionsAdapter = new GenericAdapter<>(
                new ArrayList<>(),
                R.layout.permission_list_row,
                (view, permission) -> {
                    TextView tvPermissionName = view.findViewById(R.id.tv_permission_name);
                    tvPermissionName.setText(permission.getAction());
                    if (selectedPermissions.stream().anyMatch(p -> p.getId().equals(permission.getId()))) {
                        view.setBackgroundColor(getResources().getColor(R.color.light_blue));
                    } else {
                        view.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                    }
                },
                permission -> {
                    if (selectedPermissions.stream().anyMatch(p -> p.getId().equals(permission.getId()))) {
                        selectedPermissions.removeIf(p -> p.getId().equals(permission.getId()));
                    } else {
                        selectedPermissions.add(permission);
                    }
                    permissionsAdapter.notifyDataSetChanged();
                }
        );
        rvPermissions.setAdapter(permissionsAdapter);
    }

    private void loadAllPermissions() {
        database.permissionDao().getAllPermissions().observe(this, permissions -> {
            if (permissions != null) {
                allPermissions = permissions;
                permissionsAdapter.updateData(allPermissions);
                if (roleId != null) {
                    loadRole();
                }
            }
        });
    }

    private void loadRole() {
        database.roleDao().getRoleById(roleId).observe(this, role -> {
            if (role != null) {
                currentRole = role;
                etRoleName.setText(currentRole.getName());
                etRoleDescription.setText(currentRole.getDescription());
                database.permissionDao().getPermissionsForRole(roleId).observe(this, permissions -> {
                    selectedPermissions = new ArrayList<>(permissions);
                    permissionsAdapter.notifyDataSetChanged();
                });
            }
        });
    }

    private void saveRole() {
        String name = etRoleName.getText().toString().trim();
        String description = etRoleDescription.getText().toString().trim();

        if (name.isEmpty()) {
            etRoleName.setError("اسم الدور مطلوب");
            return;
        }

        AppDatabase.databaseWriteExecutor.execute(() -> {
            if (roleId == null) {
                String newRoleId = UUID.randomUUID().toString();
                Role newRole = new Role(newRoleId, name, description, sessionManager.getCurrentCompanyId(), false);
                database.roleDao().insert(newRole);
                for (Permission p : selectedPermissions) {
                    database.roleDao().insertRolePermission(newRoleId, p.getId());
                }
            } else {
                currentRole.setName(name);
                currentRole.setDescription(description);
                database.roleDao().update(currentRole);
                database.roleDao().deleteRolePermissions(roleId);
                for (Permission p : selectedPermissions) {
                    database.roleDao().insertRolePermission(roleId, p.getId());
                }
            }

            runOnUiThread(() -> {
                Toast.makeText(this, "تم حفظ الدور بنجاح", Toast.LENGTH_SHORT).show();
                finish();
            });
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            finish();
            return true;
        } else if (itemId == R.id.action_save) {
            saveRole();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
EOP

# --- Fix DAO files ---

# RoleDao
cat > app/src/main/java/com/example/androidapp/data/dao/RoleDao.java << 'EOP'
package com.example.androidapp.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;
import java.util.List;
import com.example.androidapp.data.entities.Role;
import com.example.androidapp.data.entities.Permission;

@Dao
public interface RoleDao {
    @Insert
    void insert(Role role);

    @Update
    void update(Role role);

    @Delete
    void delete(Role role);

    @Query("SELECT * FROM roles WHERE companyId = :companyId")
    LiveData<List<Role>> getAllRoles(String companyId);

    @Query("SELECT * FROM roles WHERE id = :id LIMIT 1")
    LiveData<Role> getRoleById(String id);

    @Query("SELECT p.* FROM permissions p INNER JOIN role_permissions rp ON p.id = rp.permissionId WHERE rp.roleId = :roleId")
    LiveData<List<Permission>> getPermissionsForRole(String roleId);

    @Query("INSERT INTO role_permissions (roleId, permissionId) VALUES (:roleId, :permissionId)")
    void insertRolePermission(String roleId, String permissionId);

    @Query("DELETE FROM role_permissions WHERE roleId = :roleId")
    void deleteRolePermissions(String roleId);
}
EOP

# JournalEntryViewModel
cat > app/src/main/java/com/example/androidapp/ui/journalentry/viewmodel/JournalEntryViewModel.java << 'EOP'
package com.example.androidapp.ui.journalentry.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.dao.JournalEntryDao;
import com.example.androidapp.data.entities.JournalEntry;
import java.util.List;

public class JournalEntryViewModel extends AndroidViewModel {
    private JournalEntryDao journalEntryDao;

    public JournalEntryViewModel(@NonNull Application application) {
        super(application);
        journalEntryDao = AppDatabase.getDatabase(application).journalEntryDao();
    }

    public LiveData<List<JournalEntry>> getAllJournalEntries(String companyId) {
        return journalEntryDao.getAllJournalEntries(companyId);
    }

    public LiveData<JournalEntry> getJournalEntryById(String journalEntryId, String companyId) {
        return journalEntryDao.getJournalEntryById(journalEntryId, companyId);
    }

    public void insert(JournalEntry journalEntry) {
        AppDatabase.databaseWriteExecutor.execute(() -> journalEntryDao.insert(journalEntry));
    }

    public void update(JournalEntry journalEntry) {
        AppDatabase.databaseWriteExecutor.execute(() -> journalEntryDao.update(journalEntry));
    }

    public void delete(JournalEntry journalEntry) {
        AppDatabase.databaseWriteExecutor.execute(() -> journalEntryDao.delete(journalEntry));
    }
}
EOP

# JournalEntryDao
cat > app/src/main/java/com/example/androidapp/data/dao/JournalEntryDao.java << 'EOP'
package com.example.androidapp.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;
import com.example.androidapp.data.entities.JournalEntry;
import java.util.List;

@Dao
public interface JournalEntryDao {
    @Insert
    void insert(JournalEntry journalEntry);

    @Update
    void update(JournalEntry journalEntry);

    @Delete
    void delete(JournalEntry journalEntry);

    @Query("SELECT * FROM journal_entries WHERE companyId = :companyId")
    LiveData<List<JournalEntry>> getAllJournalEntries(String companyId);

    @Query("SELECT * FROM journal_entries WHERE id = :id AND companyId = :companyId LIMIT 1")
    LiveData<JournalEntry> getJournalEntryById(String id, String companyId);

    @Query("SELECT COUNT(*) FROM journal_entries WHERE referenceNumber = :referenceNumber AND companyId = :companyId")
    int countJournalEntriesByReferenceNumber(String referenceNumber, String companyId);
}
EOP

# SessionManager
cat > app/src/main/java/com/example/androidapp/utils/SessionManager.java << 'EOP'
package com.example.androidapp.utils;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class SessionManager {
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context _context;

    int PRIVATE_MODE = 0;

    private static final String PREF_NAME = "AndroidAppPref";
    private static final String IS_LOGIN = "IsLoggedIn";
    public static final String KEY_FIREBASE_UID = "firebaseUid";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_USER_ID = "userId";
    public static final String KEY_CURRENT_ORG_ID = "currentOrgId";
    public static final String KEY_CURRENT_BRANCH_ID = "currentBranchId";
    public static final String KEY_ASSOCIATED_ORGS = "associatedOrgs";

    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void createLoginSession(String firebaseUid, String username, String userId, String currentOrgId, String currentBranchId, Map<String, UserOrgAssociation> associatedOrgs) {
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_FIREBASE_UID, firebaseUid);
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_USER_ID, userId);
        editor.putString(KEY_CURRENT_ORG_ID, currentOrgId);
        editor.putString(KEY_CURRENT_BRANCH_ID, currentBranchId);

        Gson gson = new Gson();
        String json = gson.toJson(associatedOrgs);
        editor.putString(KEY_ASSOCIATED_ORGS, json);

        editor.commit();
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGIN, false);
    }

    public void logoutUser() {
        editor.clear();
        editor.commit();
    }

    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<>();
        user.put(KEY_FIREBASE_UID, pref.getString(KEY_FIREBASE_UID, null));
        user.put(KEY_USERNAME, pref.getString(KEY_USERNAME, null));
        user.put(KEY_USER_ID, pref.getString(KEY_USER_ID, null));
        user.put(KEY_CURRENT_ORG_ID, pref.getString(KEY_CURRENT_ORG_ID, null));
        user.put(KEY_CURRENT_BRANCH_ID, pref.getString(KEY_CURRENT_BRANCH_ID, null));
        return user;
    }

    public String getCurrentUserId() {
        return pref.getString(KEY_USER_ID, null);
    }
    
    public String getCurrentCompanyId() {
        return pref.getString(KEY_CURRENT_ORG_ID, null);
    }

    public Map<String, UserOrgAssociation> getAssociatedOrgs() {
        Gson gson = new Gson();
        String json = pref.getString(KEY_ASSOCIATED_ORGS, null);
        if (json == null) {
            return new HashMap<>();
        }
        Type type = new TypeToken<Map<String, UserOrgAssociation>>() {}.getType();
        return gson.fromJson(json, type);
    }

    public void setCurrentOrg(String orgId, String branchId) {
        editor.putString(KEY_CURRENT_ORG_ID, orgId);
        editor.putString(KEY_CURRENT_BRANCH_ID, branchId);
        editor.commit();
    }

    public static class UserOrgAssociation {
        public String orgId;
        public String orgName;
        public String roleId;
        public String roleName;
        public String branchId;
        public String branchName;

        public UserOrgAssociation(String orgId, String orgName, String roleId, String roleName, String branchId, String branchName) {
            this.orgId = orgId;
            this.orgName = orgName;
            this.roleId = roleId;
            this.roleName = roleName;
            this.branchId = branchId;
            this.branchName = branchName;
        }
    }
}
EOP

echo "Comprehensive fixes applied successfully."
