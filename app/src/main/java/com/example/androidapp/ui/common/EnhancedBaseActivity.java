package com.example.androidapp.ui.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;

import com.example.androidapp.R;
import com.example.androidapp.utils.VoiceInputManager;
import com.example.androidapp.utils.TextToSpeechManager;
import com.example.androidapp.utils.SearchSuggestionManager;

/**
 * فئة أساسية محسنة لجميع الأنشطة - تحتوي على جميع الميزات المطلوبة:
 * - الإدخال الصوتي لجميع مربعات النص
 * - تحويل النص إلى كلام
 * - اقتراحات البحث الذكية
 * - إعدادات متقدمة للواجهة
 */
public abstract class EnhancedBaseActivity extends AppCompatActivity {
    protected VoiceInputManager voiceInputManager;
    protected TextToSpeechManager ttsManager;
    protected SearchSuggestionManager suggestionManager;
    
    // إعدادات التطبيق
    private SharedPreferences preferences;
    private static final String PREFS_NAME = "AppSettings";
    private static final String KEY_VOICE_INPUT_ENABLED = "voice_input_enabled";
    private static final String KEY_TTS_ENABLED = "tts_enabled";
    private static final String KEY_SUGGESTIONS_ENABLED = "suggestions_enabled";
    private static final String KEY_TTS_SPEED = "tts_speed";
    private static final String KEY_AUTO_READ_ENABLED = "auto_read_enabled";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // تهيئة المدراء
        initializeManagers();
        
        // تحميل الإعدادات
        loadSettings();
    }
    
    /**
     * تهيئة جميع المدراء المطلوبة
     */
    private void initializeManagers() {
        voiceInputManager = new VoiceInputManager(this);
        ttsManager = TextToSpeechManager.getInstance(this);
        suggestionManager = new SearchSuggestionManager(this);
        preferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }
    
    /**
     * تحميل الإعدادات المحفوظة
     */
    private void loadSettings() {
        // تحميل إعدادات TTS
        float ttsSpeed = preferences.getFloat(KEY_TTS_SPEED, 0.8f);
        ttsManager.setSpeechRate(ttsSpeed);
    }
    
    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        
        // إعداد الشريط العلوي
        setupToolbar();
        
        // إعداد جميع مربعات النص بعد تحميل التخطيط
        post(() -> setupAllEditTexts());
    }
    
    /**
     * إعداد الشريط العلوي مع خيارات إضافية
     */
    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
            }
        }
    }
    
    /**
     * تشغيل مهمة بعد تحميل التخطيط
     */
    private void post(Runnable runnable) {
        findViewById(android.R.id.content).post(runnable);
    }
    
    /**
     * إعداد جميع مربعات النص في النشاط
     */
    private void setupAllEditTexts() {
        ViewGroup rootView = findViewById(android.R.id.content);
        setupEditTextsRecursively(rootView);
    }
    
    /**
     * إعداد مربعات النص بشكل تكراري في جميع المجموعات
     */
    private void setupEditTextsRecursively(ViewGroup viewGroup) {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View child = viewGroup.getChildAt(i);
            
            if (child instanceof EditText) {
                setupSingleEditText((EditText) child);
            } else if (child instanceof AutoCompleteTextView) {
                setupAutoCompleteTextView((AutoCompleteTextView) child);
            } else if (child instanceof ViewGroup) {\n                setupEditTextsRecursively((ViewGroup) child);\n            }\n        }\n    }\n    \n    /**\n     * إعداد مربع نص واحد مع جميع الميزات\n     */\n    private void setupSingleEditText(EditText editText) {\n        if (!isVoiceInputEnabled()) {\n            return;\n        }\n        \n        // إنشاء زر الإدخال الصوتي\n        ImageButton voiceButton = createVoiceButton();\n        \n        // إضافة الزر بجانب مربع النص\n        addVoiceButtonToEditText(editText, voiceButton);\n        \n        // إعداد الإدخال الصوتي\n        voiceInputManager.setupVoiceButton(editText, voiceButton);\n        \n        // إضافة إمكانية قراءة النص عند النقر المطول\n        editText.setOnLongClickListener(v -> {\n            String text = editText.getText().toString();\n            if (!text.isEmpty() && isTTSEnabled()) {\n                speakText(text);\n                return true;\n            }\n            return false;\n        });\n    }\n    \n    /**\n     * إعداد مربع النص مع الإكمال التلقائي\n     */\n    private void setupAutoCompleteTextView(AutoCompleteTextView autoCompleteTextView) {\n        setupSingleEditText(autoCompleteTextView);\n        \n        if (isSuggestionsEnabled()) {\n            // تحديد نوع الاقتراحات بناءً على اسم المربع أو السياق\n            SearchSuggestionManager.SearchType searchType = determineSearchType(autoCompleteTextView);\n            suggestionManager.setupSuggestions(autoCompleteTextView, searchType);\n        }\n    }\n    \n    /**\n     * تحديد نوع البحث بناءً على السياق\n     */\n    private SearchSuggestionManager.SearchType determineSearchType(AutoCompleteTextView editText) {\n        String hint = editText.getHint() != null ? editText.getHint().toString().toLowerCase() : \"\";\n        String tag = editText.getTag() != null ? editText.getTag().toString().toLowerCase() : \"\";\n        \n        if (hint.contains(\"حساب\") || hint.contains(\"account\") || tag.contains(\"account\")) {\n            return SearchSuggestionManager.SearchType.ACCOUNTS;\n        } else if (hint.contains(\"صنف\") || hint.contains(\"item\") || tag.contains(\"item\")) {\n            return SearchSuggestionManager.SearchType.ITEMS;\n        } else if (hint.contains(\"عميل\") || hint.contains(\"customer\") || tag.contains(\"customer\")) {\n            return SearchSuggestionManager.SearchType.CUSTOMERS;\n        } else if (hint.contains(\"موظف\") || hint.contains(\"employee\") || tag.contains(\"employee\")) {\n            return SearchSuggestionManager.SearchType.EMPLOYEES;\n        } else if (hint.contains(\"فاتورة\") || hint.contains(\"invoice\") || tag.contains(\"invoice\")) {\n            return SearchSuggestionManager.SearchType.INVOICES;\n        } else {\n            return SearchSuggestionManager.SearchType.ALL;\n        }\n    }\n    \n    /**\n     * إنشاء زر الإدخال الصوتي\n     */\n    private ImageButton createVoiceButton() {\n        ImageButton voiceButton = new ImageButton(this);\n        voiceButton.setImageResource(android.R.drawable.ic_btn_speak_now);\n        voiceButton.setBackground(null);\n        voiceButton.setContentDescription(\"إدخال صوتي\");\n        \n        // تحديد حجم الزر\n        int size = (int) (24 * getResources().getDisplayMetrics().density);\n        voiceButton.setLayoutParams(new ViewGroup.LayoutParams(size, size));\n        \n        return voiceButton;\n    }\n    \n    /**\n     * إضافة زر الإدخال الصوتي بجانب مربع النص\n     */\n    private void addVoiceButtonToEditText(EditText editText, ImageButton voiceButton) {\n        // هذه الطريقة تحتاج لتعديل بناءً على نوع التخطيط\n        // يمكن تحسينها لتدعم أنواع مختلفة من التخطيطات\n        \n        ViewGroup parent = (ViewGroup) editText.getParent();\n        if (parent != null) {\n            int index = parent.indexOfChild(editText);\n            \n            // إنشاء حاوية أفقية للنص والزر\n            android.widget.LinearLayout container = new android.widget.LinearLayout(this);\n            container.setOrientation(android.widget.LinearLayout.HORIZONTAL);\n            container.setLayoutParams(editText.getLayoutParams());\n            \n            // إزالة النص من الحاوية الأصلية\n            parent.removeView(editText);\n            \n            // تعديل معاملات النص ليأخذ معظم المساحة\n            android.widget.LinearLayout.LayoutParams editTextParams = \n                new android.widget.LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);\n            editText.setLayoutParams(editTextParams);\n            \n            // إضافة النص والزر للحاوية\n            container.addView(editText);\n            container.addView(voiceButton);\n            \n            // إضافة الحاوية للمكان الأصلي\n            parent.addView(container, index);\n        }\n    }\n    \n    /**\n     * نطق النص المحدد\n     */\n    protected void speakText(String text) {\n        if (isTTSEnabled()) {\n            ttsManager.speak(text, new TextToSpeechManager.TTSCallback() {\n                @Override\n                public void onSpeechStarted() {\n                    // يمكن إضافة مؤشر بصري هنا\n                }\n                \n                @Override\n                public void onSpeechCompleted() {\n                    // انتهاء النطق\n                }\n                \n                @Override\n                public void onSpeechError(String error) {\n                    Toast.makeText(EnhancedBaseActivity.this, error, Toast.LENGTH_SHORT).show();\n                }\n            });\n        }\n    }\n    \n    /**\n     * قراءة محتوى وثيقة (فاتورة، قيد، إلخ)\n     */\n    protected void readDocument(String documentType, String content) {\n        if (isTTSEnabled()) {\n            ttsManager.speakDocument(documentType, content);\n        }\n    }\n    \n    /**\n     * إعداد البحث في شريط الأدوات\n     */\n    protected void setupSearchView(Menu menu, int searchMenuId, OnSearchListener listener) {\n        MenuItem searchItem = menu.findItem(searchMenuId);\n        if (searchItem != null) {\n            SearchView searchView = (SearchView) searchItem.getActionView();\n            if (searchView != null) {\n                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {\n                    @Override\n                    public boolean onQueryTextSubmit(String query) {\n                        if (listener != null) {\n                            listener.onSearch(query);\n                        }\n                        return true;\n                    }\n                    \n                    @Override\n                    public boolean onQueryTextChange(String newText) {\n                        if (listener != null) {\n                            listener.onSearchTextChanged(newText);\n                        }\n                        return true;\n                    }\n                });\n                \n                // إضافة الإدخال الصوتي لشريط البحث إذا أمكن\n                setupSearchViewVoiceInput(searchView);\n            }\n        }\n    }\n    \n    /**\n     * إعداد الإدخال الصوتي لشريط البحث\n     */\n    private void setupSearchViewVoiceInput(SearchView searchView) {\n        // البحث عن مربع النص داخل SearchView\n        androidx.appcompat.widget.SearchView.SearchAutoComplete searchEditText = \n            searchView.findViewById(androidx.appcompat.R.id.search_src_text);\n        \n        if (searchEditText != null && isVoiceInputEnabled()) {\n            setupSingleEditText(searchEditText);\n        }\n    }\n    \n    /**\n     * واجهة للاستماع لأحداث البحث\n     */\n    public interface OnSearchListener {\n        void onSearch(String query);\n        void onSearchTextChanged(String query);\n    }\n    \n    // طرق فحص الإعدادات\n    protected boolean isVoiceInputEnabled() {\n        return preferences.getBoolean(KEY_VOICE_INPUT_ENABLED, true);\n    }\n    \n    protected boolean isTTSEnabled() {\n        return preferences.getBoolean(KEY_TTS_ENABLED, true);\n    }\n    \n    protected boolean isSuggestionsEnabled() {\n        return preferences.getBoolean(KEY_SUGGESTIONS_ENABLED, true);\n    }\n    \n    protected boolean isAutoReadEnabled() {\n        return preferences.getBoolean(KEY_AUTO_READ_ENABLED, false);\n    }\n    \n    // طرق تعديل الإعدادات\n    protected void setVoiceInputEnabled(boolean enabled) {\n        preferences.edit().putBoolean(KEY_VOICE_INPUT_ENABLED, enabled).apply();\n    }\n    \n    protected void setTTSEnabled(boolean enabled) {\n        preferences.edit().putBoolean(KEY_TTS_ENABLED, enabled).apply();\n    }\n    \n    protected void setSuggestionsEnabled(boolean enabled) {\n        preferences.edit().putBoolean(KEY_SUGGESTIONS_ENABLED, enabled).apply();\n    }\n    \n    protected void setAutoReadEnabled(boolean enabled) {\n        preferences.edit().putBoolean(KEY_AUTO_READ_ENABLED, enabled).apply();\n    }\n    \n    protected void setTTSSpeed(float speed) {\n        preferences.edit().putFloat(KEY_TTS_SPEED, speed).apply();\n        ttsManager.setSpeechRate(speed);\n    }\n    \n    @Override\n    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {\n        super.onRequestPermissionsResult(requestCode, permissions, grantResults);\n        \n        // معالجة أذونات التسجيل الصوتي\n        if (requestCode == 200) { // PERMISSION_REQUEST_RECORD_AUDIO\n            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {\n                Toast.makeText(this, \"تم منح إذن التسجيل الصوتي\", Toast.LENGTH_SHORT).show();\n            } else {\n                Toast.makeText(this, \"تم رفض إذن التسجيل الصوتي\", Toast.LENGTH_SHORT).show();\n            }\n        }\n    }\n    \n    @Override\n    protected void onDestroy() {\n        super.onDestroy();\n        \n        // تنظيف الموارد\n        if (voiceInputManager != null) {\n            voiceInputManager.destroy();\n        }\n        \n        if (suggestionManager != null) {\n            suggestionManager.cleanup();\n        }\n        \n        // ملاحظة: TTS هو singleton، لذا لا نقوم بإغلاقه هنا\n    }\n    \n    /**\n     * طريقة مجردة للأنشطة المشتقة لتنفيذ البحث المخصص\n     */\n    protected abstract void performSearch(String query);\n    \n    /**\n     * طريقة مجردة لقراءة المحتوى التلقائي عند فتح النشاط\n     */\n    protected abstract String getAutoReadContent();\n    \n    @Override\n    protected void onResume() {\n        super.onResume();\n        \n        // قراءة المحتوى تلقائياً إذا كان مفعلاً\n        if (isAutoReadEnabled()) {\n            String content = getAutoReadContent();\n            if (content != null && !content.isEmpty()) {\n                // تأخير قليل للسماح للواجهة بالتحميل كاملة\n                new android.os.Handler().postDelayed(() -> speakText(content), 1000);\n            }\n        }\n    }\n}\n