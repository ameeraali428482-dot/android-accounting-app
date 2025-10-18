package com.example.accountingapp;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.accountingapp.advanced.ActivityLogManager;

import java.util.List;
import java.util.ArrayList;

public class ActivityLogActivity extends AppCompatActivity {
    private RecyclerView recyclerViewActivityLog;
    private ActivityLogAdapter activityLogAdapter;
    private ActivityLogManager activityLogManager;
    private EditText editTextSearch;
    private Spinner spinnerActivityType;
    
    private List<ActivityLogManager.ActivityEntry> allActivities;
    private List<ActivityLogManager.ActivityEntry> filteredActivities;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_log);
        
        initializeComponents();
        setupRecyclerView();
        setupFilters();
        loadActivityLog();
    }
    
    private void initializeComponents() {
        activityLogManager = ActivityLogManager.getInstance(this);
        editTextSearch = findViewById(R.id.editTextSearch);
        spinnerActivityType = findViewById(R.id.spinnerActivityType);
        
        findViewById(R.id.btnClearLog).setOnClickListener(v -> clearActivityLog());
        findViewById(R.id.btnExportLog).setOnClickListener(v -> exportActivityLog());
    }
    
    private void setupRecyclerView() {
        recyclerViewActivityLog = findViewById(R.id.recyclerViewActivityLog);
        recyclerViewActivityLog.setLayoutManager(new LinearLayoutManager(this));
        
        activityLogAdapter = new ActivityLogAdapter(this);
        recyclerViewActivityLog.setAdapter(activityLogAdapter);
    }
    
    private void setupFilters() {
        // إعداد فلتر البحث
        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            
            @Override
            public void afterTextChanged(Editable s) {
                filterActivities();
            }
        });
        
        // إعداد فلتر نوع النشاط
        List<String> activityTypes = new ArrayList<>();
        activityTypes.add("جميع الأنشطة");
        
        for (ActivityLogManager.ActivityType type : ActivityLogManager.ActivityType.values()) {
            activityTypes.add(type.getArabicName());
        }
        
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, 
                android.R.layout.simple_spinner_item, activityTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerActivityType.setAdapter(adapter);
        
        spinnerActivityType.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                filterActivities();
            }
            
            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });
    }
    
    private void loadActivityLog() {
        allActivities = activityLogManager.getActivityEntries();
        filteredActivities = new ArrayList<>(allActivities);
        activityLogAdapter.setActivities(filteredActivities);
    }
    
    private void filterActivities() {
        String searchText = editTextSearch.getText().toString().toLowerCase().trim();
        int selectedTypePosition = spinnerActivityType.getSelectedItemPosition();
        
        filteredActivities.clear();
        
        for (ActivityLogManager.ActivityEntry activity : allActivities) {
            boolean matchesSearch = searchText.isEmpty() || 
                    activity.description.toLowerCase().contains(searchText) ||
                    activity.username.toLowerCase().contains(searchText) ||
                    activity.details.toLowerCase().contains(searchText);
            
            boolean matchesType = selectedTypePosition == 0 || // "جميع الأنشطة"
                    activity.type.ordinal() == (selectedTypePosition - 1);
            
            if (matchesSearch && matchesType) {
                filteredActivities.add(activity);
            }
        }
        
        activityLogAdapter.setActivities(filteredActivities);
    }
    
    private void clearActivityLog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("مسح سجل الأنشطة");
        builder.setMessage("هل تريد مسح جميع سجل الأنشطة نهائياً؟");
        
        builder.setPositiveButton("مسح", (dialog, which) -> {
            activityLogManager.clearActivityLog();
            loadActivityLog();
            android.widget.Toast.makeText(this, "تم مسح سجل الأنشطة", android.widget.Toast.LENGTH_SHORT).show();
        });
        
        builder.setNegativeButton("إلغاء", null);
        builder.show();
    }
    
    private void exportActivityLog() {
        String logData = activityLogManager.exportActivityLog();
        // TODO: تطبيق تصدير سجل الأنشطة
        android.widget.Toast.makeText(this, "سيتم تطبيق التصدير قريباً", android.widget.Toast.LENGTH_SHORT).show();
    }
}
