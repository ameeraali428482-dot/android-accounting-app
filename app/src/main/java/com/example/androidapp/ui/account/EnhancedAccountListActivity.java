package com.example.androidapp.ui.account;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidapp.R;
import com.example.androidapp.data.entities.Account;
import com.example.androidapp.ui.account.viewmodel.AccountViewModel;
import com.example.androidapp.ui.common.EnhancedBaseActivity;
import com.example.androidapp.utils.SearchSuggestionManager;
import com.example.androidapp.utils.VoiceInputManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

/**
 * نشاط قائمة الحسابات المحسن
 * يحتوي على جميع الميزات الجديدة للبحث والإدخال الصوتي
 */
public class EnhancedAccountListActivity extends EnhancedBaseActivity {

    private RecyclerView recyclerView;
    private EnhancedAccountAdapter accountAdapter;
    private AccountViewModel viewModel;
    private FloatingActionButton fabAddAccount;
    private List<Account> allAccounts = new ArrayList<>();
    private List<Account> filteredAccounts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enhanced_account_list);

        initializeViews();
        setupRecyclerView();
        setupViewModel();
        setupFloatingActionButton();
        loadAccounts();

        // إعداد شريط الأدوات
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("دليل الحسابات");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * تهيئة المكونات
     */
    private void initializeViews() {
        recyclerView = findViewById(R.id.recycler_view_accounts);
        fabAddAccount = findViewById(R.id.fab_add_account);
    }

    /**
     * إعداد قائمة التمرير
     */
    private void setupRecyclerView() {
        accountAdapter = new EnhancedAccountAdapter(this, filteredAccounts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(accountAdapter);

        // إعداد مستمعات الأحداث للمحول
        accountAdapter.setOnItemClickListener(account -> {
            if (isTTSEnabled()) {
                String content = "حساب " + account.getName() +
                    ". رقم الحساب " + account.getCode() +
                    ". الرصيد " + account.getBalance() + " ريال";
                speakText(content);
            }
            openAccountDetails(account);
        });

        accountAdapter.setOnItemLongClickListener(account -> {
            if (isTTSEnabled()) {
                readAccountDetails(account);
            }
            return true;
        });
    }

    /**
     * إعداد نموذج البيانات
     */
    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(AccountViewModel.class);

        // مراقبة تغييرات الحسابات
        viewModel.getAllAccounts().observe(this, accounts -> {
            if (accounts != null) {
                allAccounts.clear();
                allAccounts.addAll(accounts);
                updateFilteredList("");

                if (isAutoReadEnabled() && !accounts.isEmpty()) {
                    speakText("تم تحميل " + accounts.size() + " حساب في دليل الحسابات");
                }
            }
        });
    }

    /**
     * إعداد زر الإضافة العائم
     */
    private void setupFloatingActionButton() {
        fabAddAccount.setOnClickListener(v -> {
            Intent intent = new Intent(this, EnhancedAccountDetailActivity.class);
            startActivity(intent);

            if (isTTSEnabled()) {
                speakText("فتح صفحة إضافة حساب جديد");
            }
        });
    }

    /**
     * تحميل الحسابات
     */
    private void loadAccounts() {
        viewModel.refreshAccounts();
    }

    /**
     * فتح تفاصيل الحساب
     */
    private void openAccountDetails(Account account) {
        Intent intent = new Intent(this, EnhancedAccountDetailActivity.class);
        intent.putExtra("account_id", account.getId());
        startActivity(intent);
    }

    /**
     * قراءة تفاصيل الحساب
     */
    private void readAccountDetails(Account account) {
        if (!isTTSEnabled()) {
            return;
        }

        StringBuilder content = new StringBuilder();
        content.append("تفاصيل الحساب. ");
        content.append("الاسم: ").append(account.getName()).append(". ");
        content.append("رقم الحساب: ").append(account.getCode()).append(". ");
        content.append("النوع: ").append(getAccountTypeName(account.getType())).append(". ");
        content.append("الرصيد الحالي: ").append(account.getBalance()).append(" ريال. ");

        if (account.getDescription() != null && !account.getDescription().isEmpty()) {
            content.append("الوصف: ").append(account.getDescription()).append(". ");
        }

        readDocument("حساب", content.toString());
    }

    /**
     * الحصول على اسم نوع الحساب
     */
    private String getAccountTypeName(String type) {
        if (type == null) return "غير محدد";

        switch (type.toLowerCase()) {
            case "asset": return "أصول";
            case "liability": return "خصوم";
            case "equity": return "حقوق ملكية";
            case "revenue": return "إيرادات";
            case "expense": return "مصروفات";
            default: return type;
        }
    }

    /**
     * تحديث القائمة المفلترة
     */
    private void updateFilteredList(String query) {
        filteredAccounts.clear();

        if (query.isEmpty()) {
            filteredAccounts.addAll(allAccounts);
        } else {
            String lowerQuery = query.toLowerCase();
            for (Account account : allAccounts) {
                if (matchesQuery(account, lowerQuery)) {
                    filteredAccounts.add(account);
                }
            }
        }

        accountAdapter.notifyDataSetChanged();

        // قراءة نتائج البحث
        if (isTTSEnabled() && !query.isEmpty()) {
            String resultText = filteredAccounts.size() + " نتيجة للبحث عن " + query;
            speakText(resultText);
        }
    }

    /**
     * فحص تطابق الحساب مع استعلام البحث
     */
    private boolean matchesQuery(Account account, String query) {
        return (account.getName() != null && account.getName().toLowerCase().contains(query)) ||
               (account.getCode() != null && account.getCode().toLowerCase().contains(query)) ||
               (account.getDescription() != null && account.getDescription().toLowerCase().contains(query)) ||
               (account.getType() != null && getAccountTypeName(account.getType()).toLowerCase().contains(query));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_account_list_enhanced, menu);

        // إعداد البحث المحسن
        setupEnhancedSearch(menu);

        return true;
    }

    /**
     * إعداد البحث المحسن مع الإدخال الصوتي
     */
    private void setupEnhancedSearch(Menu menu) {
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        if (searchView != null) {
            searchView.setQueryHint("البحث في الحسابات...");
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    performSearch(query);
                    return true;
                }
                
                @Override
                public boolean onQueryTextChange(String newText) {
                    updateFilteredList(newText);
                    return true;
                }
            });
            
            // إضافة الإدخال الصوتي لشريط البحث
            setupSearchViewVoiceInput(searchView);
        }
        
        // إعداد البحث الصوتي المستقل
        MenuItem voiceSearchItem = menu.findItem(R.id.action_voice_search);
        if (voiceSearchItem != null) {
            voiceSearchItem.setOnMenuItemClickListener(item -> {
                performVoiceSearch();
                return true;
            });
        }
    }
    
    /**
     * إعداد الإدخال الصوتي لشريط البحث
     */
    private void setupSearchViewVoiceInput(SearchView searchView) {
        // البحث عن مربع النص داخل SearchView
        androidx.appcompat.widget.SearchView.SearchAutoComplete searchEditText = 
            searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        
        if (searchEditText != null && isVoiceInputEnabled()) {
            // إضافة زر الإدخال الصوتي
            searchEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, android.R.drawable.ic_btn_speak_now, 0);
            searchEditText.setOnTouchListener((v, event) -> {
                if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (searchEditText.getRight() - searchEditText.getCompoundDrawables()[2].getBounds().width())) {
                        performSearchVoiceInput(searchEditText);
                        return true;
                    }
                }
                return false;
            });
        }
    }
    
    /**
     * تنفيذ الإدخال الصوتي للبحث
     */
    private void performSearchVoiceInput(androidx.appcompat.widget.SearchView.SearchAutoComplete searchEditText) {
        if (!isVoiceInputEnabled()) {
            Toast.makeText(this, "الإدخال الصوتي غير مفعل", Toast.LENGTH_SHORT).show();
            return;
        }
        
        voiceInputManager.startListening(searchEditText, new VoiceInputManager.VoiceInputCallback() {
            @Override
            public void onVoiceInputResult(String result) {
                searchEditText.setText(result);
                performSearch(result);
            }
            
            @Override
            public void onVoiceInputError(String error) {
                Toast.makeText(EnhancedAccountListActivity.this, "خطأ في البحث الصوتي: " + error, Toast.LENGTH_SHORT).show();
            }
            
            @Override
            public void onVoiceInputStarted() {
                // يمكن إضافة مؤشر بصري
            }
            
            @Override
            public void onVoiceInputStopped() {
                // إزالة المؤشر البصري
            }
        });
    }
    
    /**
     * البحث الصوتي المستقل
     */
    private void performVoiceSearch() {
        if (!isVoiceInputEnabled()) {
            Toast.makeText(this, "الإدخال الصوتي غير مفعل", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // إنشاء مربع نص مؤقت للبحث الصوتي
        android.widget.EditText tempEditText = new android.widget.EditText(this);
        
        voiceInputManager.startListening(tempEditText, new VoiceInputManager.VoiceInputCallback() {
            @Override
            public void onVoiceInputResult(String result) {
                performAdvancedVoiceSearch(result);
            }
            
            @Override
            public void onVoiceInputError(String error) {
                Toast.makeText(EnhancedAccountListActivity.this, "خطأ في البحث الصوتي: " + error, Toast.LENGTH_SHORT).show();
            }
            
            @Override
            public void onVoiceInputStarted() {
                if (isTTSEnabled()) {
                    speakText("ابدأ بقول ما تريد البحث عنه");
                }
            }
            
            @Override
            public void onVoiceInputStopped() {
                // انتهاء البحث الصوتي
            }
        });
    }
    
    /**
     * البحث الصوتي المتقدم
     */
    private void performAdvancedVoiceSearch(String query) {
        suggestionManager.performAdvancedSearch(query, 
            SearchSuggestionManager.SearchType.ACCOUNTS, 
            results -> {
                if (!results.isEmpty()) {
                    updateFilteredList(query);
                    if (isTTSEnabled()) {
                        speakText("تم العثور على " + results.size() + " نتائج للبحث عن " + query);
                    }
                } else {
                    if (isTTSEnabled()) {
                        speakText("لم يتم العثور على نتائج للبحث عن " + query);
                    }
                }
            });
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_sort_by_name:
                sortAccountsByName();
                return true;
            case R.id.action_sort_by_balance:
                sortAccountsByBalance();
                return true;
            case R.id.action_filter_by_type:
                showAccountTypeFilter();
                return true;
            case R.id.action_export:
                exportAccountsToExcel();
                return true;
            case R.id.action_read_summary:
                readAccountsSummary();
                return true;
            case R.id.action_settings:
                openAccountSettings();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    /**
     * ترتيب الحسابات حسب الاسم
     */
    private void sortAccountsByName() {
        filteredAccounts.sort((a1, a2) -> {
            if (a1.getName() == null) return 1;
            if (a2.getName() == null) return -1;
            return a1.getName().compareToIgnoreCase(a2.getName());
        });
        accountAdapter.notifyDataSetChanged();
        
        if (isTTSEnabled()) {
            speakText("تم ترتيب الحسابات حسب الاسم");
        }
    }
    
    /**
     * ترتيب الحسابات حسب الرصيد
     */
    private void sortAccountsByBalance() {
        filteredAccounts.sort((a1, a2) -> {
            return Double.compare(a2.getBalance(), a1.getBalance()); // ترتيب تنازلي
        });
        accountAdapter.notifyDataSetChanged();
        
        if (isTTSEnabled()) {
            speakText("تم ترتيب الحسابات حسب الرصيد من الأكبر للأصغر");
        }
    }
    
    /**
     * إظهار فلتر أنواع الحسابات
     */
    private void showAccountTypeFilter() {
        String[] types = {"جميع الأنواع", "أصول", "خصوم", "حقوق ملكية", "إيرادات", "مصروفات"};
        String[] typeValues = {"all", "asset", "liability", "equity", "revenue", "expense"};
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("فلترة حسب نوع الحساب");
        builder.setItems(types, (dialog, which) -> {
            filterAccountsByType(typeValues[which]);
            if (isTTSEnabled()) {
                speakText("تم تطبيق فلتر " + types[which]);
            }
        });
        builder.show();
    }
    
    /**
     * فلترة الحسابات حسب النوع
     */
    private void filterAccountsByType(String type) {
        if ("all".equals(type)) {
            filteredAccounts.clear();
            filteredAccounts.addAll(allAccounts);
        } else {
            filteredAccounts.clear();
            for (Account account : allAccounts) {
                if (type.equalsIgnoreCase(account.getType())) {
                    filteredAccounts.add(account);
                }
            }
        }
        accountAdapter.notifyDataSetChanged();
    }
    
    /**
     * تصدير الحسابات إلى إكسل
     */
    private void exportAccountsToExcel() {
        // سيتم تطوير هذه الميزة لاحقاً
        Toast.makeText(this, "سيتم تطوير تصدير الحسابات قريباً", Toast.LENGTH_SHORT).show();
    }
    
    /**
     * قراءة ملخص الحسابات
     */
    private void readAccountsSummary() {
        if (!isTTSEnabled()) {
            Toast.makeText(this, "تحويل النص إلى كلام غير مفعل", Toast.LENGTH_SHORT).show();
            return;
        }
        
        StringBuilder summary = new StringBuilder();
        summary.append("ملخص دليل الحسابات. ");
        summary.append("إجمالي عدد الحسابات: ").append(allAccounts.size()).append(". ");
        
        // حساب إجمالي الأرصدة
        double totalBalance = 0;
        int positiveBalances = 0;
        int negativeBalances = 0;
        
        for (Account account : allAccounts) {
            totalBalance += account.getBalance();
            if (account.getBalance() > 0) {
                positiveBalances++;
            } else if (account.getBalance() < 0) {
                negativeBalances++;
            }
        }
        
        summary.append("إجمالي الأرصدة: ").append(String.format("%.2f", totalBalance)).append(" ريال. ");
        summary.append("عدد الحسابات ذات الرصيد الموجب: ").append(positiveBalances).append(". ");
        summary.append("عدد الحسابات ذات الرصيد السالب: ").append(negativeBalances).append(". ");
        
        // إحصائيات حسب النوع
        int assets = 0, liabilities = 0, equity = 0, revenue = 0, expenses = 0;
        for (Account account : allAccounts) {
            if ("asset".equalsIgnoreCase(account.getType())) assets++;
            else if ("liability".equalsIgnoreCase(account.getType())) liabilities++;
            else if ("equity".equalsIgnoreCase(account.getType())) equity++;
            else if ("revenue".equalsIgnoreCase(account.getType())) revenue++;
            else if ("expense".equalsIgnoreCase(account.getType())) expenses++;
        }
        
        summary.append("الأصول: ").append(assets).append(". ");
        summary.append("الخصوم: ").append(liabilities).append(". ");
        summary.append("حقوق الملكية: ").append(equity).append(". ");
        summary.append("الإيرادات: ").append(revenue).append(". ");
        summary.append("المصروفات: ").append(expenses).append(". ");
        
        readDocument("ملخص دليل الحسابات", summary.toString());
    }
    
    /**
     * فتح إعدادات الحسابات
     */
    private void openAccountSettings() {
        Toast.makeText(this, "إعدادات الحسابات", Toast.LENGTH_SHORT).show();
    }
    
    @Override
    protected void performSearch(String query) {
        updateFilteredList(query);
    }
    
    @Override
    protected String getAutoReadContent() {
        return "صفحة دليل الحسابات. يحتوي على " + allAccounts.size() + " حساب. يمكنك البحث أو إضافة حساب جديد";
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // إعادة تحميل البيانات عند العودة للصفحة
        loadAccounts();
    }
}
