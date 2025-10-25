package com.example.androidapp.ui.account;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.example.androidapp.R;
import com.example.androidapp.data.entities.Account;
import com.example.androidapp.ui.account.viewmodel.AccountViewModel;
import com.example.androidapp.ui.common.EnhancedBaseActivity;
import com.example.androidapp.utils.SearchSuggestionManager;
import com.example.androidapp.utils.VoiceInputManager;

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
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("دليل الحسابات");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
            searchView.setQueryHint("البحث في الحسابات...");\n            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {\n                @Override\n                public boolean onQueryTextSubmit(String query) {\n                    performSearch(query);\n                    return true;\n                }\n                \n                @Override\n                public boolean onQueryTextChange(String newText) {\n                    updateFilteredList(newText);\n                    return true;\n                }\n            });\n            \n            // إضافة الإدخال الصوتي لشريط البحث\n            setupSearchViewVoiceInput(searchView);\n        }\n        \n        // إعداد البحث الصوتي المستقل\n        MenuItem voiceSearchItem = menu.findItem(R.id.action_voice_search);\n        if (voiceSearchItem != null) {\n            voiceSearchItem.setOnMenuItemClickListener(item -> {\n                performVoiceSearch();\n                return true;\n            });\n        }\n    }\n    \n    /**\n     * إعداد الإدخال الصوتي لشريط البحث\n     */\n    private void setupSearchViewVoiceInput(SearchView searchView) {\n        // البحث عن مربع النص داخل SearchView\n        androidx.appcompat.widget.SearchView.SearchAutoComplete searchEditText = \n            searchView.findViewById(androidx.appcompat.R.id.search_src_text);\n        \n        if (searchEditText != null && isVoiceInputEnabled()) {\n            // إضافة زر الإدخال الصوتي\n            searchEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, android.R.drawable.ic_btn_speak_now, 0);\n            searchEditText.setOnTouchListener((v, event) -> {\n                if (event.getAction() == android.view.MotionEvent.ACTION_UP) {\n                    if (event.getRawX() >= (searchEditText.getRight() - searchEditText.getCompoundDrawables()[2].getBounds().width())) {\n                        performSearchVoiceInput(searchEditText);\n                        return true;\n                    }\n                }\n                return false;\n            });\n        }\n    }\n    \n    /**\n     * تنفيذ الإدخال الصوتي للبحث\n     */\n    private void performSearchVoiceInput(androidx.appcompat.widget.SearchView.SearchAutoComplete searchEditText) {\n        if (!isVoiceInputEnabled()) {\n            Toast.makeText(this, \"الإدخال الصوتي غير مفعل\", Toast.LENGTH_SHORT).show();\n            return;\n        }\n        \n        voiceInputManager.startListening(searchEditText, new VoiceInputManager.VoiceInputCallback() {\n            @Override\n            public void onVoiceInputResult(String result) {\n                searchEditText.setText(result);\n                performSearch(result);\n            }\n            \n            @Override\n            public void onVoiceInputError(String error) {\n                Toast.makeText(EnhancedAccountListActivity.this, \"خطأ في البحث الصوتي: \" + error, Toast.LENGTH_SHORT).show();\n            }\n            \n            @Override\n            public void onVoiceInputStarted() {\n                // يمكن إضافة مؤشر بصري\n            }\n            \n            @Override\n            public void onVoiceInputStopped() {\n                // إزالة المؤشر البصري\n            }\n        });\n    }\n    \n    /**\n     * البحث الصوتي المستقل\n     */\n    private void performVoiceSearch() {\n        if (!isVoiceInputEnabled()) {\n            Toast.makeText(this, \"الإدخال الصوتي غير مفعل\", Toast.LENGTH_SHORT).show();\n            return;\n        }\n        \n        // إنشاء مربع نص مؤقت للبحث الصوتي\n        android.widget.EditText tempEditText = new android.widget.EditText(this);\n        \n        voiceInputManager.startListening(tempEditText, new VoiceInputManager.VoiceInputCallback() {\n            @Override\n            public void onVoiceInputResult(String result) {\n                performAdvancedVoiceSearch(result);\n            }\n            \n            @Override\n            public void onVoiceInputError(String error) {\n                Toast.makeText(EnhancedAccountListActivity.this, \"خطأ في البحث الصوتي: \" + error, Toast.LENGTH_SHORT).show();\n            }\n            \n            @Override\n            public void onVoiceInputStarted() {\n                if (isTTSEnabled()) {\n                    speakText(\"ابدأ بقول ما تريد البحث عنه\");\n                }\n            }\n            \n            @Override\n            public void onVoiceInputStopped() {\n                // انتهاء البحث الصوتي\n            }\n        });\n    }\n    \n    /**\n     * البحث الصوتي المتقدم\n     */\n    private void performAdvancedVoiceSearch(String query) {\n        suggestionManager.performAdvancedSearch(query, \n            SearchSuggestionManager.SearchType.ACCOUNTS, \n            results -> {\n                if (!results.isEmpty()) {\n                    updateFilteredList(query);\n                    if (isTTSEnabled()) {\n                        speakText(\"تم العثور على \" + results.size() + \" نتائج للبحث عن \" + query);\n                    }\n                } else {\n                    if (isTTSEnabled()) {\n                        speakText(\"لم يتم العثور على نتائج للبحث عن \" + query);\n                    }\n                }\n            });\n    }\n    \n    @Override\n    public boolean onOptionsItemSelected(MenuItem item) {\n        switch (item.getItemId()) {\n            case android.R.id.home:\n                onBackPressed();\n                return true;\n            case R.id.action_sort_by_name:\n                sortAccountsByName();\n                return true;\n            case R.id.action_sort_by_balance:\n                sortAccountsByBalance();\n                return true;\n            case R.id.action_filter_by_type:\n                showAccountTypeFilter();\n                return true;\n            case R.id.action_export:\n                exportAccountsToExcel();\n                return true;\n            case R.id.action_read_summary:\n                readAccountsSummary();\n                return true;\n            case R.id.action_settings:\n                openAccountSettings();\n                return true;\n            default:\n                return super.onOptionsItemSelected(item);\n        }\n    }\n    \n    /**\n     * ترتيب الحسابات حسب الاسم\n     */\n    private void sortAccountsByName() {\n        filteredAccounts.sort((a1, a2) -> {\n            if (a1.getName() == null) return 1;\n            if (a2.getName() == null) return -1;\n            return a1.getName().compareToIgnoreCase(a2.getName());\n        });\n        accountAdapter.notifyDataSetChanged();\n        \n        if (isTTSEnabled()) {\n            speakText(\"تم ترتيب الحسابات حسب الاسم\");\n        }\n    }\n    \n    /**\n     * ترتيب الحسابات حسب الرصيد\n     */\n    private void sortAccountsByBalance() {\n        filteredAccounts.sort((a1, a2) -> {\n            return Double.compare(a2.getBalance(), a1.getBalance()); // ترتيب تنازلي\n        });\n        accountAdapter.notifyDataSetChanged();\n        \n        if (isTTSEnabled()) {\n            speakText(\"تم ترتيب الحسابات حسب الرصيد من الأكبر للأصغر\");\n        }\n    }\n    \n    /**\n     * إظهار فلتر أنواع الحسابات\n     */\n    private void showAccountTypeFilter() {\n        String[] types = {\"جميع الأنواع\", \"أصول\", \"خصوم\", \"حقوق ملكية\", \"إيرادات\", \"مصروفات\"};\n        String[] typeValues = {\"all\", \"asset\", \"liability\", \"equity\", \"revenue\", \"expense\"};\n        \n        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);\n        builder.setTitle(\"فلترة حسب نوع الحساب\");\n        builder.setItems(types, (dialog, which) -> {\n            filterAccountsByType(typeValues[which]);\n            if (isTTSEnabled()) {\n                speakText(\"تم تطبيق فلتر \" + types[which]);\n            }\n        });\n        builder.show();\n    }\n    \n    /**\n     * فلترة الحسابات حسب النوع\n     */\n    private void filterAccountsByType(String type) {\n        if (\"all\".equals(type)) {\n            filteredAccounts.clear();\n            filteredAccounts.addAll(allAccounts);\n        } else {\n            filteredAccounts.clear();\n            for (Account account : allAccounts) {\n                if (type.equalsIgnoreCase(account.getType())) {\n                    filteredAccounts.add(account);\n                }\n            }\n        }\n        accountAdapter.notifyDataSetChanged();\n    }\n    \n    /**\n     * تصدير الحسابات إلى إكسل\n     */\n    private void exportAccountsToExcel() {\n        // سيتم تطوير هذه الميزة لاحقاً\n        Toast.makeText(this, \"سيتم تطوير تصدير الحسابات قريباً\", Toast.LENGTH_SHORT).show();\n    }\n    \n    /**\n     * قراءة ملخص الحسابات\n     */\n    private void readAccountsSummary() {\n        if (!isTTSEnabled()) {\n            Toast.makeText(this, \"تحويل النص إلى كلام غير مفعل\", Toast.LENGTH_SHORT).show();\n            return;\n        }\n        \n        StringBuilder summary = new StringBuilder();\n        summary.append(\"ملخص دليل الحسابات. \");\n        summary.append(\"إجمالي عدد الحسابات: \").append(allAccounts.size()).append(\". \");\n        \n        // حساب إجمالي الأرصدة\n        double totalBalance = 0;\n        int positiveBalances = 0;\n        int negativeBalances = 0;\n        \n        for (Account account : allAccounts) {\n            totalBalance += account.getBalance();\n            if (account.getBalance() > 0) {\n                positiveBalances++;\n            } else if (account.getBalance() < 0) {\n                negativeBalances++;\n            }\n        }\n        \n        summary.append(\"إجمالي الأرصدة: \").append(String.format(\"%.2f\", totalBalance)).append(\" ريال. \");\n        summary.append(\"عدد الحسابات ذات الرصيد الموجب: \").append(positiveBalances).append(\". \");\n        summary.append(\"عدد الحسابات ذات الرصيد السالب: \").append(negativeBalances).append(\". \");\n        \n        // إحصائيات حسب النوع\n        int assets = 0, liabilities = 0, equity = 0, revenue = 0, expenses = 0;\n        for (Account account : allAccounts) {\n            if (\"asset\".equalsIgnoreCase(account.getType())) assets++;\n            else if (\"liability\".equalsIgnoreCase(account.getType())) liabilities++;\n            else if (\"equity\".equalsIgnoreCase(account.getType())) equity++;\n            else if (\"revenue\".equalsIgnoreCase(account.getType())) revenue++;\n            else if (\"expense\".equalsIgnoreCase(account.getType())) expenses++;\n        }\n        \n        summary.append(\"الأصول: \").append(assets).append(\". \");\n        summary.append(\"الخصوم: \").append(liabilities).append(\". \");\n        summary.append(\"حقوق الملكية: \").append(equity).append(\". \");\n        summary.append(\"الإيرادات: \").append(revenue).append(\". \");\n        summary.append(\"المصروفات: \").append(expenses).append(\". \");\n        \n        readDocument(\"ملخص دليل الحسابات\", summary.toString());\n    }\n    \n    /**\n     * فتح إعدادات الحسابات\n     */\n    private void openAccountSettings() {\n        Toast.makeText(this, \"إعدادات الحسابات\", Toast.LENGTH_SHORT).show();\n    }\n    \n    @Override\n    protected void performSearch(String query) {\n        updateFilteredList(query);\n    }\n    \n    @Override\n    protected String getAutoReadContent() {\n        return \"صفحة دليل الحسابات. يحتوي على \" + allAccounts.size() + \" حساب. يمكنك البحث أو إضافة حساب جديد\";\n    }\n    \n    @Override\n    protected void onResume() {\n        super.onResume();\n        // إعادة تحميل البيانات عند العودة للصفحة\n        loadAccounts();\n    }\n}\n