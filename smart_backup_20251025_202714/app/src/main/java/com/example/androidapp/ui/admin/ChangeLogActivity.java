package com.example.androidapp.ui.admin;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.example.androidapp.R;
import com.example.androidapp.data.entities.ChangeLog;
import com.example.androidapp.utils.ChangeTrackingManager;
import com.example.androidapp.utils.PermissionManager;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * نشاط مراقبة سجل التغييرات
 * يعرض جميع التغييرات التي تمت على البيانات مع إمكانيات البحث والتصفية
 */
public class ChangeLogActivity extends AppCompatActivity {
    
    private static final String TAG = "ChangeLogActivity";
    
    private ChangeTrackingManager changeManager;
    private PermissionManager permissionManager;
    
    // عناصر الواجهة
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private MaterialTextView tvEmptyState;
    private TextInputEditText etSearch;
    private Spinner spinnerEntityType;
    private Spinner spinnerChangeType;
    private ChipGroup chipGroupFilters;
    private Chip chipToday, chipWeek, chipMonth;
    
    private ChangeLogAdapter adapter;
    private List<ChangeLog> changeLogs = new ArrayList<>();
    private List<ChangeLog> filteredLogs = new ArrayList<>();
    
    // المرشحات
    private String selectedEntityType = "الكل";
    private String selectedChangeType = "الكل";
    private Date filterStartDate = null;
    private Date filterEndDate = null;
    private String searchQuery = "";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_log);
        
        initializeComponents();
        setupUI();
        setupFilters();
        loadChangeLogs();
        
        // إعداد شريط الأدوات
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("سجل التغييرات");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }
    
    private void initializeComponents() {
        changeManager = new ChangeTrackingManager(this);
        permissionManager = new PermissionManager(this);
        
        // التحقق من الصلاحيات
        if (!permissionManager.hasAdminPermission()) {
            finish();
            return;
        }
        
        recyclerView = findViewById(R.id.recycler_view_change_logs);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        tvEmptyState = findViewById(R.id.tv_empty_state);
        etSearch = findViewById(R.id.et_search);
        spinnerEntityType = findViewById(R.id.spinner_entity_type);
        spinnerChangeType = findViewById(R.id.spinner_change_type);
        chipGroupFilters = findViewById(R.id.chip_group_filters);
        chipToday = findViewById(R.id.chip_today);
        chipWeek = findViewById(R.id.chip_week);
        chipMonth = findViewById(R.id.chip_month);
        
        adapter = new ChangeLogAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }
    
    private void setupUI() {
        swipeRefreshLayout.setOnRefreshListener(this::loadChangeLogs);
        swipeRefreshLayout.setColorSchemeColors(getColor(R.color.primary));
        
        // إعداد البحث
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchQuery = s.toString();
                applyFilters();
            }
            
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }
    
    private void setupFilters() {
        // إعداد مرشح نوع الكيان
        String[] entityTypes = {"الكل", "فاتورة", "عميل", "صنف", "حساب"};
        ArrayAdapter<String> entityAdapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_spinner_item, entityTypes);
        entityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEntityType.setAdapter(entityAdapter);
        
        spinnerEntityType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedEntityType = entityTypes[position];
                applyFilters();
            }
            
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        
        // إعداد مرشح نوع التغيير
        String[] changeTypes = {"الكل", "إضافة", "تعديل", "حذف"};
        ArrayAdapter<String> changeAdapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_spinner_item, changeTypes);
        changeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerChangeType.setAdapter(changeAdapter);
        
        spinnerChangeType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedChangeType = changeTypes[position];
                applyFilters();
            }
            
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        
        // إعداد مرشحات التاريخ
        chipToday.setOnClickListener(v -> {
            clearDateChips();
            chipToday.setChecked(true);
            setDateFilter(getTodayRange());
        });
        
        chipWeek.setOnClickListener(v -> {
            clearDateChips();
            chipWeek.setChecked(true);
            setDateFilter(getWeekRange());
        });
        
        chipMonth.setOnClickListener(v -> {
            clearDateChips();
            chipMonth.setChecked(true);
            setDateFilter(getMonthRange());
        });
    }
    
    private void clearDateChips() {
        chipToday.setChecked(false);
        chipWeek.setChecked(false);
        chipMonth.setChecked(false);
    }
    
    private void setDateFilter(Date[] range) {
        filterStartDate = range[0];
        filterEndDate = range[1];
        applyFilters();
    }
    
    private Date[] getTodayRange() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        Date start = cal.getTime();
        
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        Date end = cal.getTime();
        
        return new Date[]{start, end};
    }
    
    private Date[] getWeekRange() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, -7);
        Date start = cal.getTime();
        Date end = new Date();
        
        return new Date[]{start, end};
    }
    
    private Date[] getMonthRange() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -1);
        Date start = cal.getTime();
        Date end = new Date();
        
        return new Date[]{start, end};
    }
    
    private void loadChangeLogs() {
        swipeRefreshLayout.setRefreshing(true);
        
        new Thread(() -> {
            try {
                List<ChangeLog> logs = changeManager.getAllChangeLogs();
                
                runOnUiThread(() -> {
                    changeLogs.clear();
                    changeLogs.addAll(logs);
                    applyFilters();
                    swipeRefreshLayout.setRefreshing(false);
                });
                
            } catch (Exception e) {
                runOnUiThread(() -> {
                    swipeRefreshLayout.setRefreshing(false);
                });
            }
        }).start();
    }
    
    private void applyFilters() {
        filteredLogs.clear();
        
        for (ChangeLog log : changeLogs) {
            boolean matches = true;
            
            // تصفية نوع الكيان
            if (!selectedEntityType.equals("الكل")) {
                String entityTypeName = getEntityTypeName(log.getEntityType());
                if (!entityTypeName.equals(selectedEntityType)) {
                    matches = false;
                }
            }
            
            // تصفية نوع التغيير
            if (!selectedChangeType.equals("الكل")) {
                String changeTypeName = getChangeTypeName(log.getChangeType());
                if (!changeTypeName.equals(selectedChangeType)) {
                    matches = false;
                }
            }
            
            // تصفية التاريخ
            if (filterStartDate != null && filterEndDate != null) {
                Date logDate = new Date(log.getTimestamp());
                if (logDate.before(filterStartDate) || logDate.after(filterEndDate)) {
                    matches = false;
                }
            }
            
            // تصفية البحث
            if (!searchQuery.isEmpty()) {
                String searchText = log.getUserId() + " " + log.getFieldName() + " " + 
                                  log.getOldValue() + " " + log.getNewValue();
                if (!searchText.toLowerCase().contains(searchQuery.toLowerCase())) {
                    matches = false;
                }
            }
            
            if (matches) {
                filteredLogs.add(log);
            }
        }
        
        adapter.notifyDataSetChanged();
        
        // إظهار حالة فارغة
        if (filteredLogs.isEmpty()) {
            tvEmptyState.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tvEmptyState.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }
    
    private String getEntityTypeName(String entityType) {
        switch (entityType) {
            case "Invoice": return "فاتورة";
            case "Customer": return "عميل";
            case "Item": return "صنف";
            case "Account": return "حساب";
            default: return entityType;
        }
    }
    
    private String getChangeTypeName(String changeType) {
        switch (changeType) {
            case "CREATE": return "إضافة";
            case "UPDATE": return "تعديل";
            case "DELETE": return "حذف";
            default: return changeType;
        }
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_change_log, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_refresh:
                loadChangeLogs();
                return true;
            case R.id.action_export:
                exportChangeLogs();
                return true;
            case R.id.action_clear_filters:
                clearFilters();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    private void exportChangeLogs() {
        // تصدير سجل التغييرات
        // TODO: تنفيذ تصدير السجلات
    }
    
    private void clearFilters() {
        etSearch.setText("");
        spinnerEntityType.setSelection(0);
        spinnerChangeType.setSelection(0);
        clearDateChips();
        filterStartDate = null;
        filterEndDate = null;
        applyFilters();
    }
    
    /**
     * محول عرض سجل التغييرات
     */
    private class ChangeLogAdapter extends RecyclerView.Adapter<ChangeLogAdapter.ChangeLogViewHolder> {
        
        @NonNull
        @Override
        public ChangeLogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_change_log, parent, false);
            return new ChangeLogViewHolder(view);
        }
        
        @Override
        public void onBindViewHolder(@NonNull ChangeLogViewHolder holder, int position) {
            ChangeLog changeLog = filteredLogs.get(position);
            holder.bind(changeLog);
        }
        
        @Override
        public int getItemCount() {
            return filteredLogs.size();
        }
        
        class ChangeLogViewHolder extends RecyclerView.ViewHolder {
            
            MaterialCardView cardView;
            MaterialTextView tvUserId;
            MaterialTextView tvEntityType;
            MaterialTextView tvFieldName;
            MaterialTextView tvOldValue;
            MaterialTextView tvNewValue;
            MaterialTextView tvTimestamp;
            MaterialTextView tvChangeType;
            
            public ChangeLogViewHolder(@NonNull View itemView) {
                super(itemView);
                
                cardView = itemView.findViewById(R.id.card_view);
                tvUserId = itemView.findViewById(R.id.tv_user_id);
                tvEntityType = itemView.findViewById(R.id.tv_entity_type);
                tvFieldName = itemView.findViewById(R.id.tv_field_name);
                tvOldValue = itemView.findViewById(R.id.tv_old_value);
                tvNewValue = itemView.findViewById(R.id.tv_new_value);
                tvTimestamp = itemView.findViewById(R.id.tv_timestamp);
                tvChangeType = itemView.findViewById(R.id.tv_change_type);
            }
            
            public void bind(ChangeLog changeLog) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
                
                tvUserId.setText("المستخدم: " + changeLog.getUserId());
                tvEntityType.setText("نوع البيانات: " + getEntityTypeName(changeLog.getEntityType()));
                tvFieldName.setText("الحقل: " + changeLog.getFieldName());
                tvOldValue.setText("القيمة السابقة: " + (changeLog.getOldValue() != null ? changeLog.getOldValue() : "فارغ"));
                tvNewValue.setText("القيمة الجديدة: " + (changeLog.getNewValue() != null ? changeLog.getNewValue() : "فارغ"));
                tvTimestamp.setText("التوقيت: " + sdf.format(new Date(changeLog.getTimestamp())));
                
                String changeTypeName = getChangeTypeName(changeLog.getChangeType());
                tvChangeType.setText(changeTypeName);
                
                // تلوين حسب نوع التغيير
                switch (changeLog.getChangeType()) {
                    case "CREATE":
                        tvChangeType.setTextColor(Color.parseColor("#4CAF50")); // أخضر
                        break;
                    case "UPDATE":
                        tvChangeType.setTextColor(Color.parseColor("#FF9800")); // برتقالي
                        break;
                    case "DELETE":
                        tvChangeType.setTextColor(Color.parseColor("#F44336")); // أحمر
                        break;
                }
            }
        }
    }
}