package com.example.androidapp;

import android.app.DatePickerDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.accountingapp.advanced.ActivityLogManager;
import java.text.SimpleDateFormat;
import java.util.*;

public class ActivityLogActivity extends AppCompatActivity {
    private static final String TAG = "ActivityLogActivity";
    
    private RecyclerView activitiesRecyclerView;
    private ActivityLogAdapter activityAdapter;
    private EditText searchEditText;
    private Spinner typeFilterSpinner;
    private Spinner priorityFilterSpinner;
    private Button dateFromButton;
    private Button dateToButton;
    private TextView statsTextView;
    private ProgressBar progressBar;
    
    private ActivityLogManager activityLogManager;
    private SimpleDateFormat dateFormat;
    private Calendar fromDate;
    private Calendar toDate;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_log);
        
        initializeManagers();
        setupToolbar();
        initializeViews();
        setupFilters();
        loadActivities();
    }
    
    private void initializeManagers() {
        activityLogManager = ActivityLogManager.getInstance(this);
        dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        fromDate = Calendar.getInstance();
        toDate = Calendar.getInstance();
        
        // تعيين التاريخ الافتراضي (آخر 30 يوم)
        fromDate.add(Calendar.DAY_OF_MONTH, -30);
    }
    
    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("سجل الأنشطة");
        }
    }
    
    private void initializeViews() {
        activitiesRecyclerView = findViewById(R.id.activitiesRecyclerView);
        searchEditText = findViewById(R.id.searchEditText);
        typeFilterSpinner = findViewById(R.id.typeFilterSpinner);
        priorityFilterSpinner = findViewById(R.id.priorityFilterSpinner);
        dateFromButton = findViewById(R.id.dateFromButton);
        dateToButton = findViewById(R.id.dateToButton);
        statsTextView = findViewById(R.id.statsTextView);
        progressBar = findViewById(R.id.progressBar);
        
        // إعداد RecyclerView
        activityAdapter = new ActivityLogAdapter();
        activitiesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        activitiesRecyclerView.setAdapter(activityAdapter);
        
        // إعداد البحث
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            
            @Override
            public void afterTextChanged(Editable s) {
                performSearch();
            }
        });
        
        // إعداد أزرار التاريخ
        dateFromButton.setOnClickListener(v -> showDatePicker(true));
        dateToButton.setOnClickListener(v -> showDatePicker(false));
        
        updateDateButtons();
    }
    
    private void setupFilters() {
        // إعداد مرشح النوع
        String[] activityTypes = {
            "الكل",
            ActivityLogManager.TYPE_LOGIN,
            ActivityLogManager.TYPE_LOGOUT,
            ActivityLogManager.TYPE_CREATE_ACCOUNT,
            ActivityLogManager.TYPE_UPDATE_ACCOUNT,
            ActivityLogManager.TYPE_DELETE_ACCOUNT,
            ActivityLogManager.TYPE_TRANSACTION,
            ActivityLogManager.TYPE_REPORT,
            ActivityLogManager.TYPE_BACKUP,
            ActivityLogManager.TYPE_RESTORE,
            ActivityLogManager.TYPE_ADMIN_ACTION,
            ActivityLogManager.TYPE_SECURITY,
            ActivityLogManager.TYPE_ERROR
        };
        
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(this,
            android.R.layout.simple_spinner_item, activityTypes);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeFilterSpinner.setAdapter(typeAdapter);
        
        // إعداد مرشح الأولوية
        String[] priorities = {"الكل", "منخفض", "متوسط", "عالي", "حرج"};
        ArrayAdapter<String> priorityAdapter = new ArrayAdapter<>(this,
            android.R.layout.simple_spinner_item, priorities);
        priorityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        priorityFilterSpinner.setAdapter(priorityAdapter);
        
        // إعداد مستمعي التغيير
        typeFilterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                loadActivities();
            }
            
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        
        priorityFilterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                loadActivities();
            }
            
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }
    
    private void loadActivities() {
        new AsyncTask<Void, Void, List<ActivityLogManager.ActivityEntry>>() {
            @Override
            protected void onPreExecute() {
                progressBar.setVisibility(View.VISIBLE);
            }
            
            @Override
            protected List<ActivityLogManager.ActivityEntry> doInBackground(Void... voids) {
                ActivityLogManager.ActivityFilter filter = createFilter();
                return activityLogManager.getActivityLog(filter);
            }
            
            @Override
            protected void onPostExecute(List<ActivityLogManager.ActivityEntry> activities) {
                progressBar.setVisibility(View.GONE);
                activityAdapter.updateActivities(activities);
                updateStats(activities);
            }
        }.execute();
    }
    
    private void performSearch() {
        String searchTerm = searchEditText.getText().toString().trim();
        
        if (searchTerm.isEmpty()) {
            loadActivities();
            return;
        }
        
        new AsyncTask<String, Void, List<ActivityLogManager.ActivityEntry>>() {
            @Override
            protected List<ActivityLogManager.ActivityEntry> doInBackground(String... terms) {
                return activityLogManager.searchActivities(terms[0], 100);
            }
            
            @Override
            protected void onPostExecute(List<ActivityLogManager.ActivityEntry> activities) {
                activityAdapter.updateActivities(activities);
                updateStats(activities);
            }
        }.execute(searchTerm);
    }
    
    private ActivityLogManager.ActivityFilter createFilter() {
        ActivityLogManager.ActivityFilter filter = new ActivityLogManager.ActivityFilter();
        
        // نوع النشاط
        int typePosition = typeFilterSpinner.getSelectedItemPosition();
        if (typePosition > 0) {
            String[] types = getResources().getStringArray(R.array.activity_types);
            filter.type = types[typePosition - 1];
        }
        
        // الأولوية
        int priorityPosition = priorityFilterSpinner.getSelectedItemPosition();
        if (priorityPosition > 0) {
            filter.minPriority = priorityPosition;
        }
        
        // التاريخ
        filter.fromTime = fromDate.getTimeInMillis();
        filter.toTime = toDate.getTimeInMillis();
        
        // عدد النتائج
        filter.limit = 200;
        
        return filter;
    }
    
    private void showDatePicker(boolean isFromDate) {
        Calendar calendar = isFromDate ? fromDate : toDate;
        
        DatePickerDialog datePickerDialog = new DatePickerDialog(
            this,
            (view, year, month, dayOfMonth) -> {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                
                if (isFromDate) {
                    calendar.set(Calendar.HOUR_OF_DAY, 0);
                    calendar.set(Calendar.MINUTE, 0);
                    calendar.set(Calendar.SECOND, 0);
                } else {
                    calendar.set(Calendar.HOUR_OF_DAY, 23);
                    calendar.set(Calendar.MINUTE, 59);
                    calendar.set(Calendar.SECOND, 59);
                }
                
                updateDateButtons();
                loadActivities();
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        );
        
        datePickerDialog.show();
    }
    
    private void updateDateButtons() {
        dateFromButton.setText("من: " + dateFormat.format(fromDate.getTime()));
        dateToButton.setText("إلى: " + dateFormat.format(toDate.getTime()));
    }
    
    private void updateStats(List<ActivityLogManager.ActivityEntry> activities) {
        if (activities.isEmpty()) {
            statsTextView.setText("لا توجد أنشطة");
            return;
        }
        
        // حساب الإحصائيات
        Map<String, Integer> typeCount = new HashMap<>();
        Map<Integer, Integer> priorityCount = new HashMap<>();
        
        for (ActivityLogManager.ActivityEntry activity : activities) {
            typeCount.put(activity.type, typeCount.getOrDefault(activity.type, 0) + 1);
            priorityCount.put(activity.priority, priorityCount.getOrDefault(activity.priority, 0) + 1);
        }
        
        StringBuilder stats = new StringBuilder();
        stats.append("إجمالي الأنشطة: ").append(activities.size()).append("\n");
        
        // أنواع الأنشطة
        stats.append("الأنواع الأكثر: ");
        typeCount.entrySet().stream()
            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
            .limit(3)
            .forEach(entry -> stats.append(entry.getKey()).append(" (").append(entry.getValue()).append(") "));
        
        statsTextView.setText(stats.toString());
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_log_menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_export:
                exportActivityLog();
                return true;
            case R.id.action_clear_old:
                showClearOldDialog();
                return true;
            case R.id.action_refresh:
                loadActivities();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    private void exportActivityLog() {
        // هنا يمكن إضافة كود لتصدير سجل الأنشطة
        Toast.makeText(this, "تصدير سجل الأنشطة غير متاح حالياً", Toast.LENGTH_SHORT).show();
    }
    
    private void showClearOldDialog() {
        // هنا يمكن إضافة حوار لمسح الأنشطة القديمة
        Toast.makeText(this, "مسح الأنشطة القديمة غير متاح حالياً", Toast.LENGTH_SHORT).show();
    }
}
