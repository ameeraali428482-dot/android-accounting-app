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
import com.example.androidapp.ui.common.VoiceInputEditText;
import com.example.androidapp.utils.Material3Helper;
import com.example.androidapp.utils.VoiceInputManager;
import com.example.androidapp.utils.SmartSuggestionsManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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

public class ReportsActivity extends AppCompatActivity {

    // UI Components
    private Toolbar toolbar;
    private VoiceInputEditText searchEditText;
    private TextInputEditText startDateEditText;
    private TextInputEditText endDateEditText;
    private AutoCompleteTextView reportTypeDropdown;
    
    // Filter and Action Buttons
    private MaterialButton filterButton;
    private MaterialButton clearFilterButton;
    private MaterialButton shareReportButton;
    private MaterialButton exportPdfButton;
    private MaterialButton printReportButton;
    
    // View Type Toggle
    private MaterialButtonToggleGroup viewTypeToggleGroup;
    private MaterialButton tableViewButton;
    private MaterialButton chartViewButton;
    private MaterialButton cardViewButton;
    
    // Sort Buttons
    private MaterialButton sortDateButton;
    private MaterialButton sortCustomerButton;
    private MaterialButton sortAmountButton;
    private MaterialButton sortStatusButton;
    
    // Content Views
    private RecyclerView reportTableRecyclerView;
    private RecyclerView reportCardRecyclerView;
    private View chartViewContainer;
    
    // Summary
    private TextView totalRecordsText;
    private TextView totalAmountText;
    private TextView averageAmountText;
    
    // FAB
    private FloatingActionButton generateReportFab;
    
    // Data and Adapters
    private List<ReportItem> reportData;
    private ReportTableAdapter tableAdapter;
    private ReportCardAdapter cardAdapter;
    
    // Managers
    private VoiceInputManager voiceInputManager;
    private SmartSuggestionsManager suggestionsManager;
    private Material3Helper material3Helper;
    
    // Formatters
    private NumberFormat currencyFormatter;
    private SimpleDateFormat dateFormatter;
    
    // Sort State
    private enum SortColumn { DATE, CUSTOMER, AMOUNT, STATUS }
    private enum SortOrder { ASCENDING, DESCENDING }
    private SortColumn currentSortColumn = SortColumn.DATE;
    private SortOrder currentSortOrder = SortOrder.DESCENDING;
    
    // Current View Type
    private enum ViewType { TABLE, CHART, CARD }
    private ViewType currentViewType = ViewType.TABLE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports_advanced);
        
        // Initialize formatters
        currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("ar", "SA"));
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd", new Locale("ar", "SA"));
        
        // Initialize managers
        voiceInputManager = VoiceInputManager.getInstance(this);
        suggestionsManager = SmartSuggestionsManager.getInstance(this);
        material3Helper = Material3Helper.getInstance();
        
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
        
        // Setup dropdowns and suggestions
        setupDropdownsAndSuggestions();
        
        // Load initial data
        loadReportData();
        
        // Setup default view
        switchToTableView();
    }

    private void initializeViews() {
        // Toolbar
        toolbar = findViewById(R.id.toolbar);
        
        // Search and Filter
        searchEditText = findViewById(R.id.searchEditText);
        startDateEditText = findViewById(R.id.startDateEditText);
        endDateEditText = findViewById(R.id.endDateEditText);
        reportTypeDropdown = findViewById(R.id.reportTypeDropdown);
        
        // Filter and Action Buttons
        filterButton = findViewById(R.id.filterButton);
        clearFilterButton = findViewById(R.id.clearFilterButton);
        shareReportButton = findViewById(R.id.shareReportButton);
        exportPdfButton = findViewById(R.id.exportPdfButton);
        printReportButton = findViewById(R.id.printReportButton);
        
        // View Type Toggle
        viewTypeToggleGroup = findViewById(R.id.viewTypeToggleGroup);
        tableViewButton = findViewById(R.id.tableViewButton);
        chartViewButton = findViewById(R.id.chartViewButton);
        cardViewButton = findViewById(R.id.cardViewButton);
        
        // Sort Buttons
        sortDateButton = findViewById(R.id.sortDateButton);
        sortCustomerButton = findViewById(R.id.sortCustomerButton);
        sortAmountButton = findViewById(R.id.sortAmountButton);
        sortStatusButton = findViewById(R.id.sortStatusButton);
        
        // Content Views
        reportTableRecyclerView = findViewById(R.id.reportTableRecyclerView);
        reportCardRecyclerView = findViewById(R.id.reportCardRecyclerView);
        chartViewContainer = findViewById(R.id.chartViewContainer);
        
        // Summary
        totalRecordsText = findViewById(R.id.totalRecordsText);
        totalAmountText = findViewById(R.id.totalAmountText);
        averageAmountText = findViewById(R.id.averageAmountText);
        
        // FAB
        generateReportFab = findViewById(R.id.generateReportFab);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    private void setupListeners() {
        // Filter Buttons
        filterButton.setOnClickListener(v -> applyFilters());
        clearFilterButton.setOnClickListener(v -> clearFilters());
        
        // Action Buttons
        shareReportButton.setOnClickListener(v -> shareReport());
        exportPdfButton.setOnClickListener(v -> exportToPdf());
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
        
        // Sort Buttons
        sortDateButton.setOnClickListener(v -> sortByColumn(SortColumn.DATE));
        sortCustomerButton.setOnClickListener(v -> sortByColumn(SortColumn.CUSTOMER));
        sortAmountButton.setOnClickListener(v -> sortByColumn(SortColumn.AMOUNT));
        sortStatusButton.setOnClickListener(v -> sortByColumn(SortColumn.STATUS));
        
        // Date Pickers
        startDateEditText.setOnClickListener(v -> showDatePicker(startDateEditText, "تاريخ البداية"));
        endDateEditText.setOnClickListener(v -> showDatePicker(endDateEditText, "تاريخ النهاية"));
        
        // FAB
        generateReportFab.setOnClickListener(v -> generateNewReport());
        
        // Voice Input Setup
        setupVoiceInput();
    }

    private void setupVoiceInput() {
        searchEditText.setVoiceInputManager(voiceInputManager);
        searchEditText.setOnVoiceInputListener(result -> {
            searchEditText.setText(result);
            applyFilters();
        });
    }

    private void setupRecyclerViews() {
        // Table RecyclerView
        reportTableRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        // Card RecyclerView
        reportCardRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setupDropdownsAndSuggestions() {
        // Report Type Dropdown
        String[] reportTypes = {
                "تقرير المبيعات",
                "تقرير المشتريات", 
                "تقرير العملاء",
                "تقرير المخزون",
                "تقرير الأرباح والخسائر",
                "تقرير التدفق النقدي",
                "تقرير الموردين",
                "تقرير الموظفين"
        };
        
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                reportTypes
        );
        reportTypeDropdown.setAdapter(adapter);
        reportTypeDropdown.setText(reportTypes[0], false);
        
        // Setup search suggestions
        suggestionsManager.setupAutoComplete(searchEditText, "search_reports");
    }

    private void loadReportData() {
        // Generate sample data
        reportData = generateSampleReportData();
        
        // Create adapters
        tableAdapter = new ReportTableAdapter(reportData, this::onReportItemClicked);
        cardAdapter = new ReportCardAdapter(reportData, this::onReportItemClicked, this::onShareItemClicked);
        
        // Set adapters
        reportTableRecyclerView.setAdapter(tableAdapter);
        reportCardRecyclerView.setAdapter(cardAdapter);
        
        // Update summary
        updateSummary();
    }

    private List<ReportItem> generateSampleReportData() {
        List<ReportItem> data = new ArrayList<>();
        
        // Generate 50 sample records
        String[] customers = {"أحمد محمد", "فاطمة أحمد", "محمد عبدالله", "نورا سالم", "خالد العثمان"};
        String[] statuses = {"مكتملة", "معلقة", "ملغية", "قيد المعالجة"};
        
        Calendar calendar = Calendar.getInstance();
        for (int i = 0; i < 50; i++) {
            calendar.add(Calendar.DAY_OF_MONTH, -i);
            
            ReportItem item = new ReportItem();
            item.setId("INV-" + String.format("%03d", i + 1));
            item.setDate(calendar.getTime());
            item.setCustomer(customers[i % customers.length]);
            item.setAmount(Math.random() * 5000 + 100);
            item.setStatus(statuses[i % statuses.length]);
            item.setType("فاتورة مبيعات");
            
            data.add(item);
        }
        
        return data;
    }

    private void applyFilters() {
        if (reportData == null) return;
        
        List<ReportItem> filteredData = new ArrayList<>(reportData);
        
        // Apply search filter
        String searchQuery = searchEditText.getText().toString().trim().toLowerCase();
        if (!searchQuery.isEmpty()) {
            filteredData.removeIf(item -> 
                !item.getCustomer().toLowerCase().contains(searchQuery) &&
                !item.getId().toLowerCase().contains(searchQuery)
            );
        }
        
        // Apply date range filter
        Date startDate = getDateFromEditText(startDateEditText);
        Date endDate = getDateFromEditText(endDateEditText);
        
        if (startDate != null || endDate != null) {
            filteredData.removeIf(item -> {
                Date itemDate = item.getDate();
                return (startDate != null && itemDate.before(startDate)) ||
                       (endDate != null && itemDate.after(endDate));
            });
        }
        
        // Update adapters with filtered data
        tableAdapter.updateData(filteredData);
        cardAdapter.updateData(filteredData);
        
        // Update summary
        updateSummaryWithData(filteredData);
        
        showSnackbar("تم تطبيق الفلتر - " + filteredData.size() + " نتيجة", null, null);
    }

    private void clearFilters() {
        searchEditText.setText("");
        startDateEditText.setText("");
        endDateEditText.setText("");
        reportTypeDropdown.setText("تقرير المبيعات", false);
        
        // Reset to original data
        if (reportData != null) {
            tableAdapter.updateData(reportData);
            cardAdapter.updateData(reportData);
            updateSummary();
        }
        
        showSnackbar("تم مسح جميع الفلاتر", null, null);
    }

    private void sortByColumn(SortColumn column) {
        if (currentSortColumn == column) {
            // Toggle sort order
            currentSortOrder = (currentSortOrder == SortOrder.ASCENDING) ? 
                              SortOrder.DESCENDING : SortOrder.ASCENDING;
        } else {
            // New column, start with descending
            currentSortColumn = column;
            currentSortOrder = SortOrder.DESCENDING;
        }
        
        // Apply sort
        List<ReportItem> currentData = tableAdapter.getCurrentData();
        Collections.sort(currentData, getComparatorForColumn(column));
        
        if (currentSortOrder == SortOrder.ASCENDING) {
            Collections.reverse(currentData);
        }
        
        // Update adapters
        tableAdapter.updateData(currentData);
        cardAdapter.updateData(currentData);
        
        // Update sort button texts
        updateSortButtonTexts();
        
        String orderText = (currentSortOrder == SortOrder.ASCENDING) ? "تصاعدي" : "تنازلي";
        showSnackbar("تم الترتيب " + orderText + " حسب " + getColumnDisplayName(column), null, null);
    }

    private Comparator<ReportItem> getComparatorForColumn(SortColumn column) {
        switch (column) {
            case DATE:
                return Comparator.comparing(ReportItem::getDate);
            case CUSTOMER:
                return Comparator.comparing(ReportItem::getCustomer);
            case AMOUNT:
                return Comparator.comparing(ReportItem::getAmount);
            case STATUS:
                return Comparator.comparing(ReportItem::getStatus);
            default:
                return Comparator.comparing(ReportItem::getDate);
        }
    }

    private void updateSortButtonTexts() {
        // Reset all buttons
        sortDateButton.setText("التاريخ ↕");
        sortCustomerButton.setText("العميل ↕");
        sortAmountButton.setText("المبلغ ↕");
        sortStatusButton.setText("الحالة ↕");
        
        // Update current sort column button
        String arrow = (currentSortOrder == SortOrder.ASCENDING) ? " ↑" : " ↓";
        switch (currentSortColumn) {
            case DATE:
                sortDateButton.setText("التاريخ" + arrow);
                break;
            case CUSTOMER:
                sortCustomerButton.setText("العميل" + arrow);
                break;
            case AMOUNT:
                sortAmountButton.setText("المبلغ" + arrow);
                break;
            case STATUS:
                sortStatusButton.setText("الحالة" + arrow);
                break;
        }
    }

    private String getColumnDisplayName(SortColumn column) {
        switch (column) {
            case DATE: return "التاريخ";
            case CUSTOMER: return "العميل";
            case AMOUNT: return "المبلغ";
            case STATUS: return "الحالة";
            default: return "";
        }
    }

    private void switchToTableView() {
        currentViewType = ViewType.TABLE;
        reportTableRecyclerView.setVisibility(View.VISIBLE);
        reportCardRecyclerView.setVisibility(View.GONE);
        chartViewContainer.setVisibility(View.GONE);
        
        // Show sort buttons for table view
        findViewById(R.id.sortDateButton).setVisibility(View.VISIBLE);
        findViewById(R.id.sortCustomerButton).setVisibility(View.VISIBLE);
        findViewById(R.id.sortAmountButton).setVisibility(View.VISIBLE);
        findViewById(R.id.sortStatusButton).setVisibility(View.VISIBLE);
    }

    private void switchToChartView() {
        currentViewType = ViewType.CHART;
        reportTableRecyclerView.setVisibility(View.GONE);
        reportCardRecyclerView.setVisibility(View.GONE);
        chartViewContainer.setVisibility(View.VISIBLE);
        
        // Hide sort buttons for chart view
        findViewById(R.id.sortDateButton).setVisibility(View.GONE);
        findViewById(R.id.sortCustomerButton).setVisibility(View.GONE);
        findViewById(R.id.sortAmountButton).setVisibility(View.GONE);
        findViewById(R.id.sortStatusButton).setVisibility(View.GONE);
    }

    private void switchToCardView() {
        currentViewType = ViewType.CARD;
        reportTableRecyclerView.setVisibility(View.GONE);
        reportCardRecyclerView.setVisibility(View.VISIBLE);
        chartViewContainer.setVisibility(View.GONE);
        
        // Hide sort buttons for card view
        findViewById(R.id.sortDateButton).setVisibility(View.GONE);
        findViewById(R.id.sortCustomerButton).setVisibility(View.GONE);
        findViewById(R.id.sortAmountButton).setVisibility(View.GONE);
        findViewById(R.id.sortStatusButton).setVisibility(View.GONE);
    }

    private void showDatePicker(TextInputEditText editText, String title) {
        Calendar calendar = Calendar.getInstance();
        
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(year, month, dayOfMonth);
                    editText.setText(dateFormatter.format(calendar.getTime()));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        
        datePickerDialog.setTitle(title);
        datePickerDialog.show();
    }

    private Date getDateFromEditText(TextInputEditText editText) {
        String dateText = editText.getText().toString().trim();
        if (dateText.isEmpty()) return null;
        
        try {
            return dateFormatter.parse(dateText);
        } catch (Exception e) {
            return null;
        }
    }

    private void updateSummary() {
        updateSummaryWithData(reportData);
    }

    private void updateSummaryWithData(List<ReportItem> data) {
        if (data == null || data.isEmpty()) {
            totalRecordsText.setText("0");
            totalAmountText.setText("0 ر.س");
            averageAmountText.setText("0 ر.س");
            return;
        }
        
        int totalRecords = data.size();
        double totalAmount = data.stream().mapToDouble(ReportItem::getAmount).sum();
        double averageAmount = totalAmount / totalRecords;
        
        totalRecordsText.setText(String.valueOf(totalRecords));
        totalAmountText.setText(currencyFormatter.format(totalAmount));
        averageAmountText.setText(currencyFormatter.format(averageAmount));
    }

    private void shareReport() {
        StringBuilder reportText = new StringBuilder();
        reportText.append("تقرير ").append(reportTypeDropdown.getText()).append("\n");
        reportText.append("تاريخ التوليد: ").append(dateFormatter.format(new Date())).append("\n\n");
        
        List<ReportItem> currentData = tableAdapter.getCurrentData();
        for (ReportItem item : currentData) {
            reportText.append("الرقم: ").append(item.getId()).append("\n");
            reportText.append("العميل: ").append(item.getCustomer()).append("\n");
            reportText.append("المبلغ: ").append(currencyFormatter.format(item.getAmount())).append("\n");
            reportText.append("الحالة: ").append(item.getStatus()).append("\n");
            reportText.append("---\n");
        }
        
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, reportText.toString());
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "تقرير " + reportTypeDropdown.getText());
        startActivity(Intent.createChooser(shareIntent, "مشاركة التقرير"));
    }

    private void exportToPdf() {
        // Implementation for PDF export
        showSnackbar("سيتم تصدير التقرير كـ PDF", null, null);
    }

    private void printReport() {
        PrintManager printManager = (PrintManager) getSystemService(PRINT_SERVICE);
        String jobName = "تقرير " + reportTypeDropdown.getText();
        
        // Create a simple print adapter (simplified implementation)
        PrintDocumentAdapter printAdapter = new ReportPrintDocumentAdapter(this, 
                tableAdapter.getCurrentData(), reportTypeDropdown.getText().toString());
        
        printManager.print(jobName, printAdapter, new PrintAttributes.Builder().build());
    }

    private void generateNewReport() {
        showSnackbar("سيتم فتح معالج إنشاء تقرير جديد", null, null);
    }

    private void onReportItemClicked(ReportItem item) {
        showSnackbar("عرض تفاصيل " + item.getId(), null, null);
    }

    private void onShareItemClicked(ReportItem item) {
        String shareText = String.format(
                "تفاصيل المعاملة:\n" +
                "الرقم: %s\n" +
                "العميل: %s\n" +
                "المبلغ: %s\n" +
                "التاريخ: %s\n" +
                "الحالة: %s",
                item.getId(),
                item.getCustomer(),
                currencyFormatter.format(item.getAmount()),
                dateFormatter.format(item.getDate()),
                item.getStatus()
        );
        
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        startActivity(Intent.createChooser(shareIntent, "مشاركة المعاملة"));
    }

    private void showSnackbar(String message, String actionText, View.OnClickListener actionListener) {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG);
        if (actionText != null && actionListener != null) {
            snackbar.setAction(actionText, actionListener);
        }
        snackbar.show();
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
                showSnackbar("تم تحديث التقرير", null, null);
                return true;
            case R.id.action_export_excel:
                exportToExcel();
                return true;
            case R.id.action_settings:
                openReportSettings();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void exportToExcel() {
        showSnackbar("سيتم تصدير التقرير كـ Excel", null, null);
    }

    private void openReportSettings() {
        showSnackbar("سيتم فتح إعدادات التقارير", null, null);
    }
}