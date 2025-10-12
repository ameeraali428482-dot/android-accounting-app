package com.example.androidapp.ui.admin;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.androidapp.R;
import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.entities.Role;
import com.example.androidapp.data.entities.User;
import com.example.androidapp.data.entities.UserRole;
import com.example.androidapp.ui.common.GenericAdapter;
import com.example.androidapp.utils.SessionManager;
import java.util.ArrayList;
import java.util.List;

public class AdminUserDetailActivity extends AppCompatActivity {

    private TextView tvUserName, tvUserEmail;
    private RecyclerView rvRoles;
    private GenericAdapter<Role> rolesAdapter;
    private AppDatabase database;
    private SessionManager sessionManager;
    private String userId = null;
    private User currentUser;
    private List<Role> allRoles;
    private List<Role> selectedRoles = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_user_detail);

        database = AppDatabase.getDatabase(this);
        sessionManager = new SessionManager(this);

        initViews();
        setupRolesRecyclerView();
        loadAllRoles();

        userId = getIntent().getStringExtra("user_id");
        if (userId != null) {
            setTitle("تفاصيل المستخدم");
            loadUser();
        } else {
            Toast.makeText(this, "معرف المستخدم غير صالح", Toast.LENGTH_SHORT).show();
            finish();
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initViews() {
        tvUserName = findViewById(R.id.tvUserName);
        tvUserEmail = findViewById(R.id.tvUserEmail);
        rvRoles = findViewById(R.id.rvRoles);
    }

    private void setupRolesRecyclerView() {
        rvRoles.setLayoutManager(new LinearLayoutManager(this));
        
        rolesAdapter = new GenericAdapter<Role>(new ArrayList<>(), R.layout.role_list_row) {
            @Override
            protected void bindView(View view, Role role) {
                TextView tvRoleName = view.findViewById(R.id.tvRoleName);
                TextView tvRoleDescription = view.findViewById(R.id.tvRoleDescription);

                tvRoleName.setText(role.getName());
                tvRoleDescription.setText(role.getDescription());

                if (selectedRoles.contains(role)) {
                    view.setBackgroundColor(getResources().getColor(R.color.light_blue));
                } else {
                    view.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                }
            }

            @Override
            protected void onItemClick(Role role) {
                if (selectedRoles.contains(role)) {
                    selectedRoles.remove(role);
                } else {
                    selectedRoles.add(role);
                }
                rolesAdapter.notifyDataSetChanged();
            }
        };
        
        rvRoles.setAdapter(rolesAdapter);
    }

    private void loadAllRoles() {
        database.roleDao().getAllRoles(sessionManager.getCurrentCompanyId())
                .observe(this, roles -> {
                    if (roles != null) {
                        allRoles = roles;
                        rolesAdapter.updateData(allRoles);
                        if (userId != null && currentUser != null) {
                            updateSelectedRoles();
                        }
                    }
                });
    }

    private void loadUser() {
        database.userDao().getUserById(userId)
                .observe(this, user -> {
                    if (user != null) {
                        currentUser = user;
                        tvUserName.setText(user.getUsername());
                        tvUserEmail.setText(user.getEmail());

                        database.userDao().getRolesForUser(userId)
                                .observe(this, roles -> {
                                    selectedRoles = new ArrayList<>(roles);
                                    updateSelectedRoles();
                                });
                    }
                });
    }

    private void updateSelectedRoles() {
        if (allRoles != null && selectedRoles != null) {
            rolesAdapter.updateData(allRoles);
            rolesAdapter.notifyDataSetChanged();
        }
    }

    private void saveUserRoles() {
        if (currentUser == null) {
            Toast.makeText(this, "خطأ: لا يوجد مستخدم", Toast.LENGTH_SHORT).show();
            return;
        }

        AppDatabase.databaseWriteExecutor.execute(() -> {
            database.userRoleDao().deleteAllUserRoles(userId);

            for (Role role : selectedRoles) {
                database.userRoleDao().insert(new UserRole(userId, role.getId()));
            }

            runOnUiThread(() -> {
                Toast.makeText(this, "تم تحديث أدوار المستخدم بنجاح", Toast.LENGTH_SHORT).show();
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
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else if (item.getItemId() == R.id.action_save) {
            saveUserRoles();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
