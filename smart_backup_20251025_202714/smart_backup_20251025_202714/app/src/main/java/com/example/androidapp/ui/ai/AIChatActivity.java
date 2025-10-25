package com.example.androidapp.ui.ai;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.androidapp.R;
import com.example.androidapp.data.entities.AIConversation;
import com.example.androidapp.viewmodels.AIViewModel;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

/**
 * نشاط محادثة الذكاء الاصطناعي - واجهة تفاعلية للمحادثة مع AI
 * AI Chat Activity - Interactive interface for AI conversations
 */
public class AIChatActivity extends AppCompatActivity implements AIChatAdapter.OnConversationActionListener {

    private AIViewModel aiViewModel;
    private RecyclerView recyclerViewChat;
    private EditText editTextMessage;
    private Button buttonSend;
    private FloatingActionButton fabNewChat;
    private ProgressBar progressBar;
    private TextView textViewTyping;
    private ChipGroup chipGroupSuggestions;
    private Spinner spinnerConversationType;
    private LinearLayout layoutAnalysisOptions;
    private Button buttonRequestAnalysis;
    private EditText editTextAnalysisQuery;
    private Spinner spinnerAnalysisType;
    
    private AIChatAdapter chatAdapter;
    private List<AIConversation> conversationList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai_chat);

        initViews();
        initViewModel();
        setupRecyclerView();
        setupListeners();
        setupObservers();
        setupConversationTypes();
        setupAnalysisTypes();
    }

    private void initViews() {
        recyclerViewChat = findViewById(R.id.recyclerViewChat);
        editTextMessage = findViewById(R.id.editTextMessage);
        buttonSend = findViewById(R.id.buttonSend);
        fabNewChat = findViewById(R.id.fabNewChat);
        progressBar = findViewById(R.id.progressBar);
        textViewTyping = findViewById(R.id.textViewTyping);
        chipGroupSuggestions = findViewById(R.id.chipGroupSuggestions);
        spinnerConversationType = findViewById(R.id.spinnerConversationType);
        layoutAnalysisOptions = findViewById(R.id.layoutAnalysisOptions);
        buttonRequestAnalysis = findViewById(R.id.buttonRequestAnalysis);
        editTextAnalysisQuery = findViewById(R.id.editTextAnalysisQuery);
        spinnerAnalysisType = findViewById(R.id.spinnerAnalysisType);

        // إعداد شريط الأدوات
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("المساعد الذكي");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initViewModel() {
        aiViewModel = new ViewModelProvider(this).get(AIViewModel.class);
        
        // تحميل محادثات الجلسة الحالية
        aiViewModel.loadConversationsForCurrentSession();
    }

    private void setupRecyclerView() {
        chatAdapter = new AIChatAdapter(conversationList, this);
        recyclerViewChat.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewChat.setAdapter(chatAdapter);
    }

    private void setupListeners() {
        // إرسال الرسالة
        buttonSend.setOnClickListener(v -> sendMessage());
        
        // إرسال الرسالة عند الضغط على Enter
        editTextMessage.setOnEditorActionListener((v, actionId, event) -> {
            sendMessage();
            return true;
        });

        // مراقبة تغيير النص لتفعيل/تعطيل زر الإرسال
        editTextMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                buttonSend.setEnabled(s.toString().trim().length() > 0);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // بدء محادثة جديدة
        fabNewChat.setOnClickListener(v -> {
            aiViewModel.startNewSession();
            conversationList.clear();
            chatAdapter.notifyDataSetChanged();
            chipGroupSuggestions.removeAllViews();
        });

        // تغيير نوع المحادثة
        spinnerConversationType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] types = {"GENERAL_CHAT", "ACCOUNTING_ANALYSIS", "DATA_INSIGHTS", "FINANCIAL_ADVICE"};
                if (position < types.length) {
                    aiViewModel.setConversationType(types[position]);
                    
                    // إظهار/إخفاء خيارات التحليل
                    layoutAnalysisOptions.setVisibility(
                        position > 0 ? View.VISIBLE : View.GONE
                    );
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // طلب تحليل متقدم
        buttonRequestAnalysis.setOnClickListener(v -> requestAnalysis());
    }

    private void setupObservers() {
        // مراقبة المحادثات
        aiViewModel.getConversations().observe(this, conversations -> {
            if (conversations != null) {
                conversationList.clear();
                conversationList.addAll(conversations);
                chatAdapter.notifyDataSetChanged();
                
                // التمرير إلى آخر رسالة
                if (!conversations.isEmpty()) {
                    recyclerViewChat.smoothScrollToPosition(conversations.size() - 1);
                }
            }
        });

        // مراقبة المحادثة الحالية
        aiViewModel.getCurrentConversation().observe(this, conversation -> {
            if (conversation != null) {
                // إضافة المحادثة الجديدة إلى القائمة
                conversationList.add(conversation);
                chatAdapter.notifyItemInserted(conversationList.size() - 1);
                recyclerViewChat.smoothScrollToPosition(conversationList.size() - 1);
            }
        });

        // مراقبة حالة التحميل
        aiViewModel.getIsLoading().observe(this, isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            buttonSend.setEnabled(!isLoading && editTextMessage.getText().toString().trim().length() > 0);
            buttonRequestAnalysis.setEnabled(!isLoading);
        });

        // مراقبة رسائل الخطأ
        aiViewModel.getErrorMessage().observe(this, errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
            }
        });

        // مراقبة مؤشر الكتابة
        aiViewModel.getTypingIndicator().observe(this, typingText -> {
            if (typingText != null && !typingText.isEmpty()) {
                textViewTyping.setText(typingText);
                textViewTyping.setVisibility(View.VISIBLE);
            } else {
                textViewTyping.setVisibility(View.GONE);
            }
        });

        // مراقبة الاقتراحات الذكية
        aiViewModel.getSuggestions().observe(this, suggestions -> {
            chipGroupSuggestions.removeAllViews();
            
            if (suggestions != null && !suggestions.isEmpty()) {
                for (String suggestion : suggestions) {
                    Chip chip = new Chip(this);
                    chip.setText(suggestion);
                    chip.setClickable(true);
                    chip.setOnClickListener(v -> {
                        editTextMessage.setText(suggestion);
                        editTextMessage.setSelection(suggestion.length());
                    });
                    chipGroupSuggestions.addView(chip);
                }
                chipGroupSuggestions.setVisibility(View.VISIBLE);
            } else {
                chipGroupSuggestions.setVisibility(View.GONE);
            }
        });
    }

    private void setupConversationTypes() {
        String[] conversationTypes = {
            "محادثة عامة",
            "تحليل محاسبي", 
            "رؤى البيانات",
            "استشارة مالية"
        };
        
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_spinner_item, conversationTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerConversationType.setAdapter(adapter);
    }

    private void setupAnalysisTypes() {
        String[] analysisTypes = {
            "FINANCIAL",
            "TRENDS", 
            "PREDICTIONS",
            "RECOMMENDATIONS",
            "ANOMALIES",
            "PERFORMANCE",
            "CASH_FLOW",
            "PROFITABILITY"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
            android.R.layout.simple_spinner_item, analysisTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAnalysisType.setAdapter(adapter);
    }

    private void sendMessage() {
        String message = editTextMessage.getText().toString().trim();
        if (!message.isEmpty()) {
            aiViewModel.sendMessage(message);
            editTextMessage.setText("");
            chipGroupSuggestions.setVisibility(View.GONE);
        }
    }

    private void requestAnalysis() {
        String query = editTextAnalysisQuery.getText().toString().trim();
        if (query.isEmpty()) {
            Toast.makeText(this, "الرجاء إدخال استفسار التحليل", Toast.LENGTH_SHORT).show();
            return;
        }

        String analysisType = spinnerAnalysisType.getSelectedItem().toString();
        aiViewModel.requestAnalysis(query, analysisType);
        editTextAnalysisQuery.setText("");
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        // إعادة تحميل المحادثات عند العودة للنشاط
        aiViewModel.loadConversationsForCurrentSession();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // تنظيف الموارد إذا لزم الأمر
    }

    // تنفيذ واجهة OnConversationActionListener
    @Override
    public void onRateConversation(String conversationId, int rating, String comment) {
        aiViewModel.rateConversation(conversationId, rating, comment);
        Toast.makeText(this, "تم حفظ التقييم", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDeleteConversation(String conversationId) {
        // إظهار تأكيد الحذف
        new android.app.AlertDialog.Builder(this)
            .setTitle("حذف المحادثة")
            .setMessage("هل أنت متأكد من حذف هذه المحادثة؟")
            .setPositiveButton("حذف", (dialog, which) -> {
                aiViewModel.deleteConversation(conversationId);
                Toast.makeText(this, "تم حذف المحادثة", Toast.LENGTH_SHORT).show();
            })
            .setNegativeButton("إلغاء", null)
            .show();
    }

    @Override
    public void onBookmarkConversation(String conversationId) {
        // سيتم تنفيذ هذا في المستقبل
        Toast.makeText(this, "تم إضافة/إزالة الإشارة المرجعية", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCopyText(String text) {
        android.content.ClipboardManager clipboard = 
            (android.content.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        android.content.ClipData clip = android.content.ClipData.newPlainText("AI Response", text);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(this, "تم نسخ النص", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onShareConversation(AIConversation conversation) {
        String shareText = "محادثة مع المساعد الذكي:\n\n" +
                          "السؤال: " + conversation.getUserMessage() + "\n\n" +
                          "الجواب: " + conversation.getAiResponse();
        
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "محادثة مع المساعد الذكي");
        
        startActivity(Intent.createChooser(shareIntent, "مشاركة المحادثة"));
    }
}