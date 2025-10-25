package com.example.androidapp.ui.reports;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidapp.R;
import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.ui.common.VoiceInputEditText;
import com.example.androidapp.utils.Material3Helper;
import com.example.androidapp.utils.VoiceInputManager;
import com.example.androidapp.utils.SmartSuggestionsManager;
import com.example.androidapp.utils.SessionManager;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * نشاط التقارير المتطور - Material 3 Design
 * مع جميع الميزات الاحترافية والحديثة
 */
public class ModernReportsActivity extends AppCompatActivity implements 
        ReportTableAdapter.OnColumnHeaderClickListener,
        ReportCardAdapter.OnReportItemClickListener {

    // UI Components - Header
    private CollapsingToolbarLayout collapsingToolbar;
    private Toolbar toolbar;
    private TextView reportSummaryText;
    private TextView dateRangeText;
    private LinearProgressIndicator reportLoadingProgress;

    // UI Components - Filter Section
    private VoiceInputEditText searchEditText;
    private TextInputEditText startDateEditText;
    private TextInputEditText endDateEditText;
    private AutoCompleteTextView reportTypeDropdown;
    private AutoCompleteTextView categoryFilterDropdown;
    private AutoCompleteTextView statusFilterDropdown;
    
    // Filter Chips
    private ChipGroup quickFilterChipGroup;
    private Chip todayChip;
    private Chip thisWeekChip;
    private Chip thisMonthChip;
    private Chip thisYearChip;
    private Chip customRangeChip;
    
    // Action Buttons
    private MaterialButton filterButton;
    private MaterialButton clearFilterButton;
    private MaterialButton shareReportButton;
    private MaterialButton exportPdfButton;
    private MaterialButton exportExcelButton;
    private MaterialButton printReportButton;
    
    // View Type Toggle
    private MaterialButtonToggleGroup viewTypeToggleGroup;
    private MaterialButton tableViewButton;
    private MaterialButton chartViewButton;
    private MaterialButton cardViewButton;
    
    // Sort Indicators
    private MaterialCardView sortControlsCard;
    private TextView currentSortText;
    private MaterialButton resetSortButton;
    
    // Content Views
    private RecyclerView reportTableRecyclerView;
    private RecyclerView reportCardRecyclerView;
    private View chartViewContainer;
    private View emptyStateView;
    
    // Summary Cards
    private MaterialCardView summaryCard;
    private TextView totalRecordsText;
    private TextView totalAmountText;
    private TextView averageAmountText;
    private TextView highestAmountText;
    private TextView lowestAmountText;
    
    // FAB
    private FloatingActionButton generateReportFab;
    
    // Data and Adapters
    private List<ReportItem> originalReportData;
    private List<ReportItem> filteredReportData;
    private AdvancedReportTableAdapter tableAdapter;
    private AdvancedReportCardAdapter cardAdapter;
    
    // Managers
    private VoiceInputManager voiceInputManager;
    private SmartSuggestionsManager suggestionsManager;
    private Material3Helper material3Helper;
    private SessionManager sessionManager;
    
    // Formatters
    private NumberFormat currencyFormatter;
    private SimpleDateFormat dateFormatter;
    
    // Sort State
    private enum SortColumn { DATE, CUSTOMER, AMOUNT, STATUS, TYPE }
    private enum SortOrder { ASCENDING, DESCENDING }
    private SortColumn currentSortColumn = SortColumn.DATE;
    private SortOrder currentSortOrder = SortOrder.DESCENDING;
    
    // View State
    private enum ViewType { TABLE, CHART, CARD }
    private ViewType currentViewType = ViewType.TABLE;
    
    // Filter State
    private String currentSearchQuery = "";
    private Date filterStartDate;
    private Date filterEndDate;
    private String selectedReportType = "";
    private String selectedCategory = "";
    private String selectedStatus = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports_modern);
        
        // Initialize managers
        voiceInputManager = VoiceInputManager.getInstance(this);
        suggestionsManager = SmartSuggestionsManager.getInstance(this);
        material3Helper = Material3Helper.getInstance();
        sessionManager = new SessionManager(this);
        
        // Initialize formatters
        currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("ar", "SA"));
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        
        // Initialize views
        initializeViews();
        
        // Setup toolbar
        setupToolbar();
        
        // Apply Material 3 styling
        material3Helper.applyMaterial3Styling(this);
        
        // Setup listeners
        setupListeners();
        
        // Setup RecyclerViews
        setupRecyclerViews();
        
        // Setup dropdowns
        setupDropdowns();
        
        // Setup filter chips
        setupFilterChips();
        
        // Setup voice input
        setupVoiceInput();
        
        // Load initial data
        loadReportData();
        
        // Set default date range (current month)
        setDefaultDateRange();
    }

    private void initializeViews() {
        // Header
        collapsingToolbar = findViewById(R.id.collapsingToolbar);
        toolbar = findViewById(R.id.toolbar);
        reportSummaryText = findViewById(R.id.reportSummaryText);
        dateRangeText = findViewById(R.id.dateRangeText);
        reportLoadingProgress = findViewById(R.id.reportLoadingProgress);
        
        // Filter Section
        searchEditText = findViewById(R.id.searchEditText);
        startDateEditText = findViewById(R.id.startDateEditText);
        endDateEditText = findViewById(R.id.endDateEditText);
        reportTypeDropdown = findViewById(R.id.reportTypeDropdown);
        categoryFilterDropdown = findViewById(R.id.categoryFilterDropdown);
        statusFilterDropdown = findViewById(R.id.statusFilterDropdown);
        
        // Filter Chips
        quickFilterChipGroup = findViewById(R.id.quickFilterChipGroup);
        todayChip = findViewById(R.id.todayChip);
        thisWeekChip = findViewById(R.id.thisWeekChip);
        thisMonthChip = findViewById(R.id.thisMonthChip);
        thisYearChip = findViewById(R.id.thisYearChip);
        customRangeChip = findViewById(R.id.customRangeChip);
        
        // Action Buttons
        filterButton = findViewById(R.id.filterButton);
        clearFilterButton = findViewById(R.id.clearFilterButton);
        shareReportButton = findViewById(R.id.shareReportButton);
        exportPdfButton = findViewById(R.id.exportPdfButton);
        exportExcelButton = findViewById(R.id.exportExcelButton);
        printReportButton = findViewById(R.id.printReportButton);
        
        // View Type Toggle
        viewTypeToggleGroup = findViewById(R.id.viewTypeToggleGroup);
        tableViewButton = findViewById(R.id.tableViewButton);
        chartViewButton = findViewById(R.id.chartViewButton);
        cardViewButton = findViewById(R.id.cardViewButton);
        
        // Sort Controls
        sortControlsCard = findViewById(R.id.sortControlsCard);
        currentSortText = findViewById(R.id.currentSortText);
        resetSortButton = findViewById(R.id.resetSortButton);
        
        // Content Views
        reportTableRecyclerView = findViewById(R.id.reportTableRecyclerView);
        reportCardRecyclerView = findViewById(R.id.reportCardRecyclerView);
        chartViewContainer = findViewById(R.id.chartViewContainer);
        emptyStateView = findViewById(R.id.emptyStateView);
        
        // Summary
        summaryCard = findViewById(R.id.summaryCard);
        totalRecordsText = findViewById(R.id.totalRecordsText);
        totalAmountText = findViewById(R.id.totalAmountText);
        averageAmountText = findViewById(R.id.averageAmountText);
        highestAmountText = findViewById(R.id.highestAmountText);
        lowestAmountText = findViewById(R.id.lowestAmountText);
        
        // FAB
        generateReportFab = findViewById(R.id.generateReportFab);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        
        collapsingToolbar.setTitle("التقارير المتقدمة");
        collapsingToolbar.setExpandedTitleColor(getResources().getColor(android.R.color.white, getTheme()));
        collapsingToolbar.setCollapsedTitleTextColor(getResources().getColor(android.R.color.white, getTheme()));
    }

    private void setupListeners() {
        // Filter and Action Buttons
        filterButton.setOnClickListener(v -> applyFilters());
        clearFilterButton.setOnClickListener(v -> clearAllFilters());
        shareReportButton.setOnClickListener(v -> shareReport());
        exportPdfButton.setOnClickListener(v -> exportToPdf());
        exportExcelButton.setOnClickListener(v -> exportToExcel());
        printReportButton.setOnClickListener(v -> printReport());
        
        // View Type Toggle
        viewTypeToggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                if (checkedId == R.id.tableViewButton) {
                    switchToTableView();
                } else if (checkedId == R.id.chartViewButton) {
                    switchToChartView();
                } else if (checkedId == R.id.cardViewButton) {
                    switchToCardView();
                }
            }
        });
        
        // Sort Controls
        resetSortButton.setOnClickListener(v -> resetSort());
        
        // Date Pickers
        startDateEditText.setOnClickListener(v -> showDatePicker(true));
        endDateEditText.setOnClickListener(v -> showDatePicker(false));
        
        // FAB
        generateReportFab.setOnClickListener(v -> generateCustomReport());
        
        // Search
        searchEditText.setOnVoiceInputListener(result -> {
            currentSearchQuery = result;
            applyFilters();
        });
    }

    private void setupRecyclerViews() {
        // Initialize data lists
        originalReportData = new ArrayList<>();
        filteredReportData = new ArrayList<>();
        
        // Setup table view
        tableAdapter = new AdvancedReportTableAdapter(filteredReportData, this);
        reportTableRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        reportTableRecyclerView.setAdapter(tableAdapter);
        
        // Setup card view
        cardAdapter = new AdvancedReportCardAdapter(filteredReportData, this);
        reportCardRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        reportCardRecyclerView.setAdapter(cardAdapter);
        
        // Initially show table view
        switchToTableView();
    }

    private void setupDropdowns() {
        // Report Types
        String[] reportTypes = {
                "تقرير المبيعات",
                "تقرير المشتريات", 
                "تقرير العملاء",
                "تقرير الموردين",
                "تقرير المخزون",
                "تقرير الأرباح والخسائر",
                "تقرير التدفق النقدي",
                "تقرير الديون"
        };
        
        ArrayAdapter<String> typesAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_dropdown_item_1line, reportTypes);
        reportTypeDropdown.setAdapter(typesAdapter);
        
        // Categories
        String[] categories = {
                "جميع الفئات",
                "إلكترونيات",
                "ملابس",
                "أغذية",
                "أثاث",
                "كتب",
                "رياضة",
                "صحة وجمال"
        };
        
        ArrayAdapter<String> categoriesAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_dropdown_item_1line, categories);
        categoryFilterDropdown.setAdapter(categoriesAdapter);
        categoryFilterDropdown.setText(categories[0], false);
        
        // Status
        String[] statuses = {
                "جميع الحالات",
                "مكتمل",
                "معلق",
                "ملغي",
                "قيد المعالجة"
        };
        
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_dropdown_item_1line, statuses);
        statusFilterDropdown.setAdapter(statusAdapter);
        statusFilterDropdown.setText(statuses[0], false);
    }

    private void setupFilterChips() {
        quickFilterChipGroup.setSingleSelection(true);
        
        // Set default selection to this month
        thisMonthChip.setChecked(true);
        
        // Setup chip listeners
        quickFilterChipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (!checkedIds.isEmpty()) {
                int checkedId = checkedIds.get(0);
                applyQuickFilter(checkedId);
            }
        });
    }

    private void setupVoiceInput() {
        searchEditText.setVoiceInputManager(voiceInputManager);
        suggestionsManager.setupAutoComplete(searchEditText, "search_query");
    }

    private void setDefaultDateRange() {
        Calendar calendar = Calendar.getInstance();
        
        // End date: today
        filterEndDate = calendar.getTime();
        endDateEditText.setText(dateFormatter.format(filterEndDate));
        
        // Start date: first day of current month
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        filterStartDate = calendar.getTime();
        startDateEditText.setText(dateFormatter.format(filterStartDate));
        
        updateDateRangeDisplay();
    }

    private void applyQuickFilter(int chipId) {
        Calendar calendar = Calendar.getInstance();
        Date endDate = calendar.getTime(); // Today
        Date startDate;
        
        if (chipId == R.id.todayChip) {
            startDate = endDate;
        } else if (chipId == R.id.thisWeekChip) {
            calendar.add(Calendar.DAY_OF_YEAR, -7);
            startDate = calendar.getTime();
        } else if (chipId == R.id.thisMonthChip) {
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            startDate = calendar.getTime();
        } else if (chipId == R.id.thisYearChip) {
            calendar.set(Calendar.DAY_OF_YEAR, 1);
            startDate = calendar.getTime();
        } else {
            // Custom range - don't change dates
            return;
        }
        
        filterStartDate = startDate;
        filterEndDate = endDate;
        
        startDateEditText.setText(dateFormatter.format(startDate));
        endDateEditText.setText(dateFormatter.format(endDate));
        
        updateDateRangeDisplay();
        applyFilters();
    }

    private void showDatePicker(boolean isStartDate) {
        Calendar calendar = Calendar.getInstance();
        if (isStartDate && filterStartDate != null) {
            calendar.setTime(filterStartDate);
        } else if (!isStartDate && filterEndDate != null) {
            calendar.setTime(filterEndDate);
        }
        
        DatePickerDialog picker = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    Calendar selectedDate = Calendar.getInstance();
                    selectedDate.set(year, month, dayOfMonth);
                    
                    String dateString = dateFormatter.format(selectedDate.getTime());
                    
                    if (isStartDate) {
                        filterStartDate = selectedDate.getTime();
                        startDateEditText.setText(dateString);
                    } else {
                        filterEndDate = selectedDate.getTime();
                        endDateEditText.setText(dateString);
                    }
                    
                    // Select custom range chip
                    customRangeChip.setChecked(true);
                    updateDateRangeDisplay();
                    applyFilters();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        
        picker.show();
    }

    private void updateDateRangeDisplay() {
        if (filterStartDate != null && filterEndDate != null) {
            String startStr = dateFormatter.format(filterStartDate);
            String endStr = dateFormatter.format(filterEndDate);
            dateRangeText.setText(String.format("من %s إلى %s", startStr, endStr));
        }
    }

    private void loadReportData() {
        showLoading(true);
        
        // Simulate data loading (replace with actual database queries)
        new Thread(() -> {
            try {
                Thread.sleep(1000); // Simulate network delay
                
                List<ReportItem> data = generateSampleData();
                
                runOnUiThread(() -> {
                    originalReportData.clear();
                    originalReportData.addAll(data);
                    applyFilters();
                    showLoading(false);
                });
                
            } catch (InterruptedException e) {
                runOnUiThread(() -> showLoading(false));
            }
        }).start();
    }

    private List<ReportItem> generateSampleData() {
        List<ReportItem> data = new ArrayList<>();
        
        // Generate sample report data
        String[] customers = {"أحمد محمد", "فاطمة أحمد", "محمد علي", "نورا سعد", "خالد عبدالله"};
        String[] types = {"مبيعات", "مشتريات", "مرتجعات", "خدمات"};
        String[] statuses = {"مكتمل", "معلق", "ملغي", "قيد المعالجة"};
        String[] categories = {"إلكترونيات", "ملابس", "أغذية", "أثاث"};
        
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -30);
        
        for (int i = 0; i < 50; i++) {
            ReportItem item = new ReportItem();
            item.setId("RPT-" + (1000 + i));
            item.setCustomerName(customers[i % customers.length]);
            item.setType(types[i % types.length]);
            item.setStatus(statuses[i % statuses.length]);
            item.setCategory(categories[i % categories.length]);
            item.setAmount(1000 + (Math.random() * 9000));
            
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            item.setDate(calendar.getTime());
            
            data.add(item);
        }
        
        return data;
    }

    private void applyFilters() {
        filteredReportData.clear();
        
        for (ReportItem item : originalReportData) {
            boolean matches = true;
            
            // Search filter
            if (!currentSearchQuery.isEmpty()) {
                String searchLower = currentSearchQuery.toLowerCase();
                if (!item.getCustomerName().toLowerCase().contains(searchLower) &&
                    !item.getId().toLowerCase().contains(searchLower) &&
                    !item.getType().toLowerCase().contains(searchLower)) {
                    matches = false;
                }
            }
            
            // Date range filter
            if (filterStartDate != null && filterEndDate != null) {
                if (item.getDate().before(filterStartDate) || 
                    item.getDate().after(filterEndDate)) {
                    matches = false;
                }
            }
            
            // Type filter
            if (!selectedReportType.isEmpty() && 
                !selectedReportType.equals("تقرير المبيعات")) {
                // Apply specific type filtering logic
            }
            
            // Category filter
            if (!selectedCategory.isEmpty() && 
                !selectedCategory.equals("جميع الفئات") &&
                !item.getCategory().equals(selectedCategory)) {
                matches = false;
            }
            
            // Status filter
            if (!selectedStatus.isEmpty() && 
                !selectedStatus.equals("جميع الحالات") &&
                !item.getStatus().equals(selectedStatus)) {
                matches = false;
            }
            
            if (matches) {
                filteredReportData.add(item);
            }
        }
        
        // Apply current sort
        applySorting();
        
        // Update UI
        updateAdapters();
        updateSummary();
        updateEmptyState();
    }

    private void applySorting() {
        Collections.sort(filteredReportData, new Comparator<ReportItem>() {
            @Override
            public int compare(ReportItem o1, ReportItem o2) {
                int result = 0;
                
                switch (currentSortColumn) {
                    case DATE:
                        result = o1.getDate().compareTo(o2.getDate());
                        break;
                    case CUSTOMER:
                        result = o1.getCustomerName().compareTo(o2.getCustomerName());
                        break;
                    case AMOUNT:
                        result = Double.compare(o1.getAmount(), o2.getAmount());
                        break;
                    case STATUS:
                        result = o1.getStatus().compareTo(o2.getStatus());
                        break;
                    case TYPE:
                        result = o1.getType().compareTo(o2.getType());
                        break;
                }
                
                return currentSortOrder == SortOrder.ASCENDING ? result : -result;
            }
        });
        
        updateSortDisplay();
    }

    private void updateSortDisplay() {
        String columnName = getSortColumnDisplayName(currentSortColumn);
        String orderName = currentSortOrder == SortOrder.ASCENDING ? "تصاعدي" : "تنازلي";
        currentSortText.setText(String.format("مرتب حسب: %s (%s)", columnName, orderName));
    }

    private String getSortColumnDisplayName(SortColumn column) {
        switch (column) {
            case DATE: return "التاريخ";
            case CUSTOMER: return "العميل";
            case AMOUNT: return "المبلغ";
            case STATUS: return "الحالة";
            case TYPE: return "النوع";
            default: return "غير محدد";
        }
    }

    private void updateAdapters() {
        if (tableAdapter != null) {
            tableAdapter.notifyDataSetChanged();
        }
        if (cardAdapter != null) {
            cardAdapter.notifyDataSetChanged();
        }
    }

    private void updateSummary() {
        int totalRecords = filteredReportData.size();
        double totalAmount = 0;
        double highestAmount = 0;
        double lowestAmount = Double.MAX_VALUE;
        
        for (ReportItem item : filteredReportData) {
            totalAmount += item.getAmount();
            highestAmount = Math.max(highestAmount, item.getAmount());
            lowestAmount = Math.min(lowestAmount, item.getAmount());
        }
        
        double averageAmount = totalRecords > 0 ? totalAmount / totalRecords : 0;
        
        totalRecordsText.setText(String.valueOf(totalRecords));
        totalAmountText.setText(currencyFormatter.format(totalAmount));
        averageAmountText.setText(currencyFormatter.format(averageAmount));
        highestAmountText.setText(currencyFormatter.format(highestAmount));
        lowestAmountText.setText(totalRecords > 0 ? currencyFormatter.format(lowestAmount) : "0");
        
        // Update header summary
        reportSummaryText.setText(String.format("%d سجل - %s", totalRecords, currencyFormatter.format(totalAmount)));
    }

    private void updateEmptyState() {
        boolean isEmpty = filteredReportData.isEmpty();
        emptyStateView.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        
        if (currentViewType == ViewType.TABLE) {
            reportTableRecyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        } else if (currentViewType == ViewType.CARD) {
            reportCardRecyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        }
    }

    private void clearAllFilters() {
        currentSearchQuery = "";
        selectedReportType = "";
        selectedCategory = "جميع الفئات";
        selectedStatus = "جميع الحالات";
        
        searchEditText.setText("");
        reportTypeDropdown.setText("", false);
        categoryFilterDropdown.setText("جميع الفئات", false);
        statusFilterDropdown.setText("جميع الحالات", false);
        
        setDefaultDateRange();
        thisMonthChip.setChecked(true);
        
        applyFilters();
        showSnackbar("تم مسح جميع المرشحات", null, null);
    }

    private void resetSort() {
        currentSortColumn = SortColumn.DATE;
        currentSortOrder = SortOrder.DESCENDING;
        applySorting();
        updateAdapters();
        showSnackbar("تم إعادة تعيين الترتيب", null, null);
    }

    private void switchToTableView() {
        currentViewType = ViewType.TABLE;
        reportTableRecyclerView.setVisibility(View.VISIBLE);
        reportCardRecyclerView.setVisibility(View.GONE);
        chartViewContainer.setVisibility(View.GONE);
        sortControlsCard.setVisibility(View.VISIBLE);
        tableViewButton.setChecked(true);
    }

    private void switchToCardView() {
        currentViewType = ViewType.CARD;
        reportTableRecyclerView.setVisibility(View.GONE);
        reportCardRecyclerView.setVisibility(View.VISIBLE);
        chartViewContainer.setVisibility(View.GONE);
        sortControlsCard.setVisibility(View.VISIBLE);
        cardViewButton.setChecked(true);
    }

    private void switchToChartView() {
        currentViewType = ViewType.CHART;
        reportTableRecyclerView.setVisibility(View.GONE);
        reportCardRecyclerView.setVisibility(View.GONE);
        chartViewContainer.setVisibility(View.VISIBLE);
        sortControlsCard.setVisibility(View.GONE);
        chartViewButton.setChecked(true);
        
        // Load chart data
        loadChartData();
    }

    private void loadChartData() {
        // Implement chart loading logic
        showSnackbar("سيتم تحميل الرسم البياني", null, null);
    }

    private void generateCustomReport() {
        showSnackbar("سيتم فتح معالج إنشاء التقرير المخصص", null, null);
    }

    private void shareReport() {
        if (filteredReportData.isEmpty()) {
            showSnackbar("لا توجد بيانات للمشاركة", null, null);
            return;
        }
        
        StringBuilder reportText = new StringBuilder();
        reportText.append("تقرير متقدم\n");
        reportText.append("=================\n");
        reportText.append(String.format("عدد السجلات: %d\n", filteredReportData.size()));
        reportText.append(String.format("إجمالي المبلغ: %s\n", totalAmountText.getText()));
        reportText.append(String.format("الفترة: %s\n", dateRangeText.getText()));
        reportText.append("\nالتفاصيل:\n");
        
        for (ReportItem item : filteredReportData) {
            reportText.append(String.format("- %s | %s | %s\n", 
                item.getCustomerName(), 
                item.getType(),
                currencyFormatter.format(item.getAmount())));
        }
        
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, reportText.toString());
        startActivity(Intent.createChooser(shareIntent, "مشاركة التقرير"));
    }

    private void exportToPdf() {
        showSnackbar("سيتم تصدير التقرير إلى PDF", null, null);
    }

    private void exportToExcel() {
        showSnackbar("سيتم تصدير التقرير إلى Excel", null, null);
    }

    private void printReport() {
        if (filteredReportData.isEmpty()) {
            showSnackbar("لا توجد بيانات للطباعة", null, null);
            return;
        }
        
        PrintManager printManager = (PrintManager) getSystemService(PRINT_SERVICE);
        PrintDocumentAdapter printAdapter = new ReportPrintDocumentAdapter(this, filteredReportData);
        printManager.print("تقرير متقدم", printAdapter, new PrintAttributes.Builder().build());
    }

    private void showLoading(boolean show) {
        reportLoadingProgress.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void showSnackbar(String message, String actionText, View.OnClickListener actionListener) {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG);
        if (actionText != null && actionListener != null) {
            snackbar.setAction(actionText, actionListener);
        }
        snackbar.show();
    }

    // Interface implementations
    @Override
    public void onColumnHeaderClick(String columnName, boolean isAscending) {
        // Convert column name to sort column
        switch (columnName) {
            case "التاريخ":
                currentSortColumn = SortColumn.DATE;
                break;
            case "العميل":
                currentSortColumn = SortColumn.CUSTOMER;
                break;
            case "المبلغ":
                currentSortColumn = SortColumn.AMOUNT;
                break;
            case "الحالة":
                currentSortColumn = SortColumn.STATUS;
                break;
            case "النوع":
                currentSortColumn = SortColumn.TYPE;
                break;
        }
        
        currentSortOrder = isAscending ? SortOrder.ASCENDING : SortOrder.DESCENDING;
        applySorting();
        updateAdapters();
    }

    @Override
    public void onReportItemClick(ReportItem item) {
        // Handle item click - navigate to details
        showSnackbar("تم النقر على: " + item.getCustomerName(), null, null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_reports, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
                
            case R.id.action_refresh:
                loadReportData();
                return true;
                
            case R.id.action_settings:
                // Open report settings
                showSnackbar("سيتم فتح إعدادات التقارير", null, null);
                return true;
                
            case R.id.action_help:
                // Open help
                showSnackbar("سيتم فتح المساعدة", null, null);
                return true;
                
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}