package com.example.androidapp.ui.bankreconciliation;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidapp.R;
import com.example.androidapp.models.BankReconciliationItem;
import com.example.androidapp.models.BankStatement;
import com.example.androidapp.ui.common.VoiceInputEditText;
import com.example.androidapp.utils.Material3Helper;
import com.example.androidapp.utils.VoiceInputManager;
import com.example.androidapp.utils.SmartSuggestionsManager;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * نشاط مطابقة كشف الحساب المصرفي - Material 3 Design
 * لمقارنة البيانات المحاسبية مع كشف الحساب المصرفي
 */
public class BankReconciliationActivity extends AppCompatActivity {

    // UI Components - Header
    private CollapsingToolbarLayout collapsingToolbar;
    private Toolbar toolbar;
    private TextView reconciliationDateText;
    private TextView lastReconciliationText;
    private LinearProgressIndicator reconciliationProgress;

    // Bank Information
    private TextInputEditText bankNameEditText;
    private TextInputEditText accountNumberEditText;
    private TextInputEditText statementDateEditText;
    private TextInputEditText statementBalanceEditText;

    // Book Balance Section
    private MaterialCardView bookBalanceCard;
    private TextView bookBalanceText;
    private TextView bookBalanceDateText;
    private MaterialButton refreshBookBalanceButton;

    // Statement Balance Section
    private MaterialCardView statementBalanceCard;
    private TextView statementBalanceDisplayText;
    private MaterialButton importStatementButton;
    private MaterialButton manualEntryButton;

    // Difference Analysis
    private MaterialCardView differenceCard;
    private TextView differenceAmountText;
    private TextView differenceStatusText;
    private ChipGroup differenceChipGroup;
    private Chip reconcileChip;
    private Chip discrepancyChip;

    // Reconciliation Tabs
    private TabLayout reconciliationTabLayout;
    private RecyclerView depositInTransitRecyclerView;
    private RecyclerView outstandingChecksRecyclerView;
    private RecyclerView bankErrorsRecyclerView;
    private RecyclerView bookErrorsRecyclerView;

    // Summary Section
    private MaterialCardView summaryCard;
    private TextView adjustedBookBalanceText;
    private TextView adjustedBankBalanceText;
    private TextView finalDifferenceText;
    private TextView reconciliationStatusText;

    // Action Buttons
    private MaterialButton saveReconciliationButton;
    private MaterialButton printReconciliationButton;
    private MaterialButton exportReconciliationButton;
    private MaterialButton clearAllButton;

    // FAB
    private ExtendedFloatingActionButton reconcileNowFab;

    // Data and Adapters
    private List<BankReconciliationItem> depositInTransitItems;
    private List<BankReconciliationItem> outstandingCheckItems;
    private List<BankReconciliationItem> bankErrorItems;
    private List<BankReconciliationItem> bookErrorItems;

    private BankReconciliationAdapter depositAdapter;
    private BankReconciliationAdapter checksAdapter;
    private BankReconciliationAdapter bankErrorsAdapter;
    private BankReconciliationAdapter bookErrorsAdapter;

    // Managers
    private VoiceInputManager voiceInputManager;
    private SmartSuggestionsManager suggestionsManager;
    private Material3Helper material3Helper;

    // Financial Data
    private double currentBookBalance = 0.0;
    private double currentStatementBalance = 0.0;
    private double adjustedBookBalance = 0.0;
    private double adjustedBankBalance = 0.0;
    private double finalDifference = 0.0;

    // Formatters
    private NumberFormat currencyFormatter;
    private SimpleDateFormat dateFormatter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_reconciliation);

        // Initialize managers
        voiceInputManager = VoiceInputManager.getInstance(this);
        suggestionsManager = SmartSuggestionsManager.getInstance(this);
        material3Helper = Material3Helper.getInstance();

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

        // Setup tabs
        setupTabs();

        // Setup RecyclerViews
        setupRecyclerViews();

        // Setup voice input
        setupVoiceInput();

        // Load initial data
        loadReconciliationData();

        // Set current date
        setCurrentDate();
    }

    private void initializeViews() {
        // Header
        collapsingToolbar = findViewById(R.id.collapsingToolbar);
        toolbar = findViewById(R.id.toolbar);
        reconciliationDateText = findViewById(R.id.reconciliationDateText);
        lastReconciliationText = findViewById(R.id.lastReconciliationText);
        reconciliationProgress = findViewById(R.id.reconciliationProgress);

        // Bank Information
        bankNameEditText = findViewById(R.id.bankNameEditText);
        accountNumberEditText = findViewById(R.id.accountNumberEditText);
        statementDateEditText = findViewById(R.id.statementDateEditText);
        statementBalanceEditText = findViewById(R.id.statementBalanceEditText);

        // Book Balance
        bookBalanceCard = findViewById(R.id.bookBalanceCard);
        bookBalanceText = findViewById(R.id.bookBalanceText);
        bookBalanceDateText = findViewById(R.id.bookBalanceDateText);
        refreshBookBalanceButton = findViewById(R.id.refreshBookBalanceButton);

        // Statement Balance
        statementBalanceCard = findViewById(R.id.statementBalanceCard);
        statementBalanceDisplayText = findViewById(R.id.statementBalanceDisplayText);
        importStatementButton = findViewById(R.id.importStatementButton);
        manualEntryButton = findViewById(R.id.manualEntryButton);

        // Difference Analysis
        differenceCard = findViewById(R.id.differenceCard);
        differenceAmountText = findViewById(R.id.differenceAmountText);
        differenceStatusText = findViewById(R.id.differenceStatusText);
        differenceChipGroup = findViewById(R.id.differenceChipGroup);
        reconcileChip = findViewById(R.id.reconcileChip);
        discrepancyChip = findViewById(R.id.discrepancyChip);

        // Tabs
        reconciliationTabLayout = findViewById(R.id.reconciliationTabLayout);
        depositInTransitRecyclerView = findViewById(R.id.depositInTransitRecyclerView);
        outstandingChecksRecyclerView = findViewById(R.id.outstandingChecksRecyclerView);
        bankErrorsRecyclerView = findViewById(R.id.bankErrorsRecyclerView);
        bookErrorsRecyclerView = findViewById(R.id.bookErrorsRecyclerView);

        // Summary
        summaryCard = findViewById(R.id.summaryCard);
        adjustedBookBalanceText = findViewById(R.id.adjustedBookBalanceText);
        adjustedBankBalanceText = findViewById(R.id.adjustedBankBalanceText);
        finalDifferenceText = findViewById(R.id.finalDifferenceText);
        reconciliationStatusText = findViewById(R.id.reconciliationStatusText);

        // Action Buttons
        saveReconciliationButton = findViewById(R.id.saveReconciliationButton);
        printReconciliationButton = findViewById(R.id.printReconciliationButton);
        exportReconciliationButton = findViewById(R.id.exportReconciliationButton);
        clearAllButton = findViewById(R.id.clearAllButton);

        // FAB
        reconcileNowFab = findViewById(R.id.reconcileNowFab);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        collapsingToolbar.setTitle("مطابقة كشف الحساب المصرفي");
        collapsingToolbar.setExpandedTitleColor(getResources().getColor(android.R.color.white, getTheme()));
        collapsingToolbar.setCollapsedTitleTextColor(getResources().getColor(android.R.color.white, getTheme()));
    }

    private void setupListeners() {
        // Bank Balance Actions
        refreshBookBalanceButton.setOnClickListener(v -> refreshBookBalance());
        importStatementButton.setOnClickListener(v -> importBankStatement());
        manualEntryButton.setOnClickListener(v -> manualStatementEntry());

        // Statement Balance Input
        statementBalanceEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                updateStatementBalance();
            }
        });

        // Date Picker
        statementDateEditText.setOnClickListener(v -> showDatePicker());

        // Action Buttons
        saveReconciliationButton.setOnClickListener(v -> saveReconciliation());
        printReconciliationButton.setOnClickListener(v -> printReconciliation());
        exportReconciliationButton.setOnClickListener(v -> exportReconciliation());
        clearAllButton.setOnClickListener(v -> clearAllAdjustments());

        // FAB
        reconcileNowFab.setOnClickListener(v -> performAutoReconciliation());
    }

    private void setupTabs() {
        reconciliationTabLayout.addTab(reconciliationTabLayout.newTab().setText("الودائع المعلقة"));
        reconciliationTabLayout.addTab(reconciliationTabLayout.newTab().setText("الشيكات المعلقة"));
        reconciliationTabLayout.addTab(reconciliationTabLayout.newTab().setText("أخطاء البنك"));
        reconciliationTabLayout.addTab(reconciliationTabLayout.newTab().setText("أخطاء الدفاتر"));

        reconciliationTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switchTabContent(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void setupRecyclerViews() {
        // Initialize data lists
        depositInTransitItems = new ArrayList<>();
        outstandingCheckItems = new ArrayList<>();
        bankErrorItems = new ArrayList<>();
        bookErrorItems = new ArrayList<>();

        // Setup adapters
        depositAdapter = new BankReconciliationAdapter(this, depositInTransitItems, 
            BankReconciliationAdapter.ItemType.DEPOSIT_IN_TRANSIT);
        checksAdapter = new BankReconciliationAdapter(this, outstandingCheckItems, 
            BankReconciliationAdapter.ItemType.OUTSTANDING_CHECK);
        bankErrorsAdapter = new BankReconciliationAdapter(this, bankErrorItems, 
            BankReconciliationAdapter.ItemType.BANK_ERROR);
        bookErrorsAdapter = new BankReconciliationAdapter(this, bookErrorItems, 
            BankReconciliationAdapter.ItemType.BOOK_ERROR);

        // Setup RecyclerViews
        depositInTransitRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        depositInTransitRecyclerView.setAdapter(depositAdapter);

        outstandingChecksRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        outstandingChecksRecyclerView.setAdapter(checksAdapter);

        bankErrorsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        bankErrorsRecyclerView.setAdapter(bankErrorsAdapter);

        bookErrorsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        bookErrorsRecyclerView.setAdapter(bookErrorsAdapter);

        // Set item change listeners
        setupAdapterListeners();
    }

    private void setupAdapterListeners() {
        // Deposit adapter listener
        depositAdapter.setOnItemChangeListener(new BankReconciliationAdapter.OnItemChangeListener() {
            @Override
            public void onItemAdded(BankReconciliationItem item) {
                calculateAdjustments();
                showSnackbar("تم إضافة وديعة معلقة", "تراجع", v -> {
                    depositInTransitItems.remove(item);
                    depositAdapter.notifyDataSetChanged();
                    calculateAdjustments();
                });
            }

            @Override
            public void onItemUpdated(BankReconciliationItem item) {
                calculateAdjustments();
            }

            @Override
            public void onItemDeleted(BankReconciliationItem item) {
                calculateAdjustments();
            }
        });

        // Similar listeners for other adapters...
    }

    private void setupVoiceInput() {
        // Setup voice input for bank name
        voiceInputManager.setupVoiceInput(bankNameEditText, this);
        
        // Setup smart suggestions for bank names
        List<String> bankSuggestions = new ArrayList<>();
        bankSuggestions.add("البنك الأهلي السعودي");
        bankSuggestions.add("بنك الرياض");
        bankSuggestions.add("بنك ساب");
        bankSuggestions.add("البنك السعودي الفرنسي");
        bankSuggestions.add("بنك الانماء");
        suggestionsManager.setupSuggestions(bankNameEditText, bankSuggestions);
    }

    private void loadReconciliationData() {
        showProgress(true);
        
        // TODO: Load from database
        // For now, load dummy data
        loadDummyData();
        
        // Calculate balances
        refreshBookBalance();
        calculateAdjustments();
        
        showProgress(false);
    }

    private void loadDummyData() {
        // Load dummy bank information
        bankNameEditText.setText("البنك الأهلي السعودي");
        accountNumberEditText.setText("123456789");
        currentBookBalance = 50000.0;
        currentStatementBalance = 48500.0;

        // Add dummy reconciliation items
        depositInTransitItems.add(new BankReconciliationItem(
            1, "وديعة معلقة", 2500.0, new Date(), "إيداع نقدي", false));
        
        outstandingCheckItems.add(new BankReconciliationItem(
            2, "شيك رقم 001", -1000.0, new Date(), "شيك لم يتم صرفه", false));
        
        bankErrorItems.add(new BankReconciliationItem(
            3, "خطأ بنكي", -500.0, new Date(), "رسوم مضاعفة", false));

        // Notify adapters
        depositAdapter.notifyDataSetChanged();
        checksAdapter.notifyDataSetChanged();
        bankErrorsAdapter.notifyDataSetChanged();
        bookErrorsAdapter.notifyDataSetChanged();
    }

    private void setCurrentDate() {
        String currentDate = dateFormatter.format(new Date());
        reconciliationDateText.setText("تاريخ المطابقة: " + currentDate);
        statementDateEditText.setText(currentDate);
    }

    private void switchTabContent(int position) {
        // Hide all RecyclerViews
        depositInTransitRecyclerView.setVisibility(View.GONE);
        outstandingChecksRecyclerView.setVisibility(View.GONE);
        bankErrorsRecyclerView.setVisibility(View.GONE);
        bookErrorsRecyclerView.setVisibility(View.GONE);

        // Show selected RecyclerView
        switch (position) {
            case 0: // Deposits in Transit
                depositInTransitRecyclerView.setVisibility(View.VISIBLE);
                break;
            case 1: // Outstanding Checks
                outstandingChecksRecyclerView.setVisibility(View.VISIBLE);
                break;
            case 2: // Bank Errors
                bankErrorsRecyclerView.setVisibility(View.VISIBLE);
                break;
            case 3: // Book Errors
                bookErrorsRecyclerView.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void refreshBookBalance() {
        // TODO: Calculate from accounting records
        // For now, use dummy value
        bookBalanceText.setText(currencyFormatter.format(currentBookBalance));
        bookBalanceDateText.setText("آخر تحديث: " + dateFormatter.format(new Date()));
        calculateAdjustments();
    }

    private void updateStatementBalance() {
        String balanceStr = statementBalanceEditText.getText().toString().trim();
        if (!balanceStr.isEmpty()) {
            try {
                currentStatementBalance = Double.parseDouble(balanceStr);
                statementBalanceDisplayText.setText(currencyFormatter.format(currentStatementBalance));
                calculateAdjustments();
            } catch (NumberFormatException e) {
                statementBalanceEditText.setError("يرجى إدخال رقم صحيح");
            }
        }
    }

    private void calculateAdjustments() {
        // Calculate adjusted book balance
        adjustedBookBalance = currentBookBalance;
        for (BankReconciliationItem item : depositInTransitItems) {
            if (item.isReconciled()) {
                adjustedBookBalance += item.getAmount();
            }
        }
        for (BankReconciliationItem item : outstandingCheckItems) {
            if (item.isReconciled()) {
                adjustedBookBalance += item.getAmount(); // Amount is already negative
            }
        }
        for (BankReconciliationItem item : bookErrorItems) {
            if (item.isReconciled()) {
                adjustedBookBalance += item.getAmount();
            }
        }

        // Calculate adjusted bank balance
        adjustedBankBalance = currentStatementBalance;
        for (BankReconciliationItem item : bankErrorItems) {
            if (item.isReconciled()) {
                adjustedBankBalance += item.getAmount();
            }
        }

        // Calculate final difference
        finalDifference = adjustedBookBalance - adjustedBankBalance;

        // Update UI
        updateSummaryDisplay();
    }

    private void updateSummaryDisplay() {
        adjustedBookBalanceText.setText(currencyFormatter.format(adjustedBookBalance));
        adjustedBankBalanceText.setText(currencyFormatter.format(adjustedBankBalance));
        finalDifferenceText.setText(currencyFormatter.format(Math.abs(finalDifference)));

        // Update difference status
        if (Math.abs(finalDifference) < 0.01) { // Consider as balanced if difference < 1 fils
            differenceStatusText.setText("متطابق");
            differenceStatusText.setTextColor(getResources().getColor(android.R.color.holo_green_dark, getTheme()));
            reconciliationStatusText.setText("مطابقة مكتملة");
            reconcileChip.setChecked(true);
        } else {
            differenceStatusText.setText("غير متطابق");
            differenceStatusText.setTextColor(getResources().getColor(android.R.color.holo_red_dark, getTheme()));
            reconciliationStatusText.setText("يوجد اختلاف: " + currencyFormatter.format(finalDifference));
            discrepancyChip.setChecked(true);
        }

        // Update difference amount text color
        if (finalDifference > 0) {
            differenceAmountText.setTextColor(getResources().getColor(android.R.color.holo_blue_dark, getTheme()));
        } else if (finalDifference < 0) {
            differenceAmountText.setTextColor(getResources().getColor(android.R.color.holo_orange_dark, getTheme()));
        } else {
            differenceAmountText.setTextColor(getResources().getColor(android.R.color.holo_green_dark, getTheme()));
        }
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
            this,
            (view, year, month, dayOfMonth) -> {
                calendar.set(year, month, dayOfMonth);
                String selectedDate = dateFormatter.format(calendar.getTime());
                statementDateEditText.setText(selectedDate);
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void importBankStatement() {
        // TODO: Implement bank statement import (CSV, Excel, PDF)
        showSnackbar("سيتم فتح خيارات استيراد كشف الحساب", null, null);
    }

    private void manualStatementEntry() {
        // Show dialog for manual entry
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("إدخال رصيد كشف الحساب يدوياً");
        builder.setMessage("يرجى إدخال الرصيد من كشف الحساب المصرفي");
        
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_manual_balance_entry, null);
        TextInputEditText balanceEditText = dialogView.findViewById(R.id.balanceEditText);
        
        builder.setView(dialogView);
        builder.setPositiveButton("موافق", (dialog, which) -> {
            String balanceStr = balanceEditText.getText().toString().trim();
            if (!balanceStr.isEmpty()) {
                statementBalanceEditText.setText(balanceStr);
                updateStatementBalance();
            }
        });
        builder.setNegativeButton("إلغاء", null);
        builder.show();
    }

    private void performAutoReconciliation() {
        showProgress(true);
        
        // TODO: Implement automatic reconciliation logic
        // For now, simulate processing
        reconcileNowFab.postDelayed(() -> {
            // Mark some items as reconciled
            for (BankReconciliationItem item : depositInTransitItems) {
                item.setReconciled(true);
            }
            for (BankReconciliationItem item : outstandingCheckItems) {
                item.setReconciled(true);
            }
            
            // Update adapters
            depositAdapter.notifyDataSetChanged();
            checksAdapter.notifyDataSetChanged();
            
            // Recalculate
            calculateAdjustments();
            
            showProgress(false);
            showSnackbar("تم تنفيذ المطابقة التلقائية", "موافق", null);
        }, 2000);
    }

    private void saveReconciliation() {
        // TODO: Save reconciliation to database
        showSnackbar("تم حفظ المطابقة بنجاح", "موافق", null);
    }

    private void printReconciliation() {
        // TODO: Generate and print reconciliation report
        showSnackbar("سيتم طباعة تقرير المطابقة", null, null);
    }

    private void exportReconciliation() {
        // TODO: Export reconciliation to Excel/PDF
        showSnackbar("سيتم تصدير تقرير المطابقة", null, null);
    }

    private void clearAllAdjustments() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("مسح جميع التعديلات");
        builder.setMessage("هل تريد حقاً مسح جميع عناصر المطابقة؟");
        builder.setPositiveButton("نعم", (dialog, which) -> {
            depositInTransitItems.clear();
            outstandingCheckItems.clear();
            bankErrorItems.clear();
            bookErrorItems.clear();
            
            depositAdapter.notifyDataSetChanged();
            checksAdapter.notifyDataSetChanged();
            bankErrorsAdapter.notifyDataSetChanged();
            bookErrorsAdapter.notifyDataSetChanged();
            
            calculateAdjustments();
            showSnackbar("تم مسح جميع التعديلات", "تراجع", null);
        });
        builder.setNegativeButton("لا", null);
        builder.show();
    }

    private void showProgress(boolean show) {
        reconciliationProgress.setVisibility(show ? View.VISIBLE : View.GONE);
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
        getMenuInflater().inflate(R.menu.menu_bank_reconciliation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
                
            case R.id.action_refresh:
                loadReconciliationData();
                return true;
                
            case R.id.action_settings:
                showSnackbar("سيتم فتح إعدادات المطابقة", null, null);
                return true;
                
            case R.id.action_help:
                showSnackbar("سيتم فتح دليل المطابقة", null, null);
                return true;
                
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}