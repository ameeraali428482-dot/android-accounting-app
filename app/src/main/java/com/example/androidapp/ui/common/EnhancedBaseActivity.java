package com.example.androidapp.ui.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import com.example.androidapp.R;
import com.example.androidapp.utils.TextToSpeechManager;
import com.example.androidapp.utils.VoiceInputManager;

/**
 * النشاط الأساسي المحسن مع الميزات الصوتية والبحث
 */
public abstract class EnhancedBaseActivity extends AppCompatActivity {

    protected SharedPreferences preferences;
    protected VoiceInputManager voiceInputManager;
    protected TextToSpeechManager ttsManager;

    // مفاتيح التفضيلات
    protected static final String KEY_VOICE_INPUT_ENABLED = "voice_input_enabled";
    protected static final String KEY_TTS_ENABLED = "tts_enabled";
    protected static final String KEY_SUGGESTIONS_ENABLED = "suggestions_enabled";
    protected static final String KEY_AUTO_READ_ENABLED = "auto_read_enabled";
    protected static final String KEY_TTS_SPEED = "tts_speed";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        preferences = getSharedPreferences("AppSettings", Context.MODE_PRIVATE);
        initializeManagers();
    }

    /**
     * تهيئة المدراء
     */
    private void initializeManagers() {
        voiceInputManager = new VoiceInputManager(this);
        ttsManager = TextToSpeechManager.getInstance(this);
    }

    /**
     * إعداد مربعات النص مع الميزات المحسنة
     */
    protected void setupEnhancedEditTexts(ViewGroup parent) {
        for (int i = 0; i < parent.getChildCount(); i++) {
            View child = parent.getChildAt(i);
            if (child instanceof EditText) {
                setupSingleEditText((EditText) child);
            } else if (child instanceof ViewGroup) {
                setupEnhancedEditTexts((ViewGroup) child);
            }
        }
    }

    /**
     * إعداد مربع نص واحد مع جميع الميزات
     */
    private void setupSingleEditText(EditText editText) {
        if (!isVoiceInputEnabled()) {
            return;
        }
        
        // إنشاء زر الإدخال الصوتي
        ImageButton voiceButton = createVoiceButton();
        
        // إضافة الزر بجانب مربع النص
        addVoiceButtonToEditText(editText, voiceButton);
        
        // إعداد الإدخال الصوتي
        voiceInputManager.setupVoiceButton(editText, voiceButton);
        
        // إضافة إمكانية قراءة النص عند النقر المطول
        editText.setOnLongClickListener(v -> {
            String text = editText.getText().toString();
            if (!text.isEmpty() && isTTSEnabled()) {
                speakText(text);
                return true;
            }
            return false;
        });
    }

    /**
     * إنشاء زر الإدخال الصوتي
     */
    private ImageButton createVoiceButton() {
        ImageButton voiceButton = new ImageButton(this);
        voiceButton.setImageResource(android.R.drawable.ic_btn_speak_now);
        voiceButton.setBackground(null);
        voiceButton.setContentDescription("إدخال صوتي");
        
        // تحديد حجم الزر
        int size = (int) (24 * getResources().getDisplayMetrics().density);
        voiceButton.setLayoutParams(new ViewGroup.LayoutParams(size, size));
        
        return voiceButton;
    }

    /**
     * إضافة زر الإدخال الصوتي بجانب مربع النص
     */
    private void addVoiceButtonToEditText(EditText editText, ImageButton voiceButton) {
        ViewGroup parent = (ViewGroup) editText.getParent();
        if (parent != null) {
            int index = parent.indexOfChild(editText);
            
            // إنشاء حاوية أفقية للنص والزر
            android.widget.LinearLayout container = new android.widget.LinearLayout(this);
            container.setOrientation(android.widget.LinearLayout.HORIZONTAL);
            container.setLayoutParams(editText.getLayoutParams());
            
            // إزالة النص من الحاوية الأصلية
            parent.removeView(editText);
            
            // تعديل معاملات النص ليأخذ معظم المساحة
            android.widget.LinearLayout.LayoutParams editTextParams = 
                new android.widget.LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
            editText.setLayoutParams(editTextParams);
            
            // إضافة النص والزر للحاوية
            container.addView(editText);
            container.addView(voiceButton);
            
            // إضافة الحاوية للمكان الأصلي
            parent.addView(container, index);
        }
    }

    /**
     * نطق النص المحدد
     */
    protected void speakText(String text) {
        if (isTTSEnabled()) {
            ttsManager.speak(text, new TextToSpeechManager.TTSCallback() {
                @Override
                public void onSpeechStarted() {
                    // يمكن إضافة مؤشر بصري هنا
                }
                
                @Override
                public void onSpeechCompleted() {
                    // انتهاء النطق
                }
                
                @Override
                public void onSpeechError(String error) {
                    Toast.makeText(EnhancedBaseActivity.this, error, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    /**
     * قراءة محتوى وثيقة (فاتورة، قيد، إلخ)
     */
    protected void readDocument(String documentType, String content) {
        if (isTTSEnabled()) {
            ttsManager.speakDocument(documentType, content);
        }
    }

    /**
     * إعداد البحث في شريط الأدوات
     */
    protected void setupSearchView(Menu menu, int searchMenuId, OnSearchListener listener) {
        MenuItem searchItem = menu.findItem(searchMenuId);
        if (searchItem != null) {
            SearchView searchView = (SearchView) searchItem.getActionView();
            if (searchView != null) {
                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        if (listener != null) {
                            listener.onSearch(query);
                        }
                        return true;
                    }
                    
                    @Override
                    public boolean onQueryTextChange(String newText) {
                        if (listener != null) {
                            listener.onSearchTextChanged(newText);
                        }
                        return true;
                    }
                });
            }
        }
    }

    /**
     * واجهة للاستماع لأحداث البحث
     */
    public interface OnSearchListener {
        void onSearch(String query);
        void onSearchTextChanged(String query);
    }

    // طرق فحص الإعدادات
    protected boolean isVoiceInputEnabled() {
        return preferences.getBoolean(KEY_VOICE_INPUT_ENABLED, true);
    }
    
    protected boolean isTTSEnabled() {
        return preferences.getBoolean(KEY_TTS_ENABLED, true);
    }
    
    protected boolean isSuggestionsEnabled() {
        return preferences.getBoolean(KEY_SUGGESTIONS_ENABLED, true);
    }
    
    protected boolean isAutoReadEnabled() {
        return preferences.getBoolean(KEY_AUTO_READ_ENABLED, false);
    }

    // طرق تعديل الإعدادات
    protected void setVoiceInputEnabled(boolean enabled) {
        preferences.edit().putBoolean(KEY_VOICE_INPUT_ENABLED, enabled).apply();
    }
    
    protected void setTTSEnabled(boolean enabled) {
        preferences.edit().putBoolean(KEY_TTS_ENABLED, enabled).apply();
    }
    
    protected void setSuggestionsEnabled(boolean enabled) {
        preferences.edit().putBoolean(KEY_SUGGESTIONS_ENABLED, enabled).apply();
    }
    
    protected void setAutoReadEnabled(boolean enabled) {
        preferences.edit().putBoolean(KEY_AUTO_READ_ENABLED, enabled).apply();
    }
    
    protected void setTTSSpeed(float speed) {
        preferences.edit().putFloat(KEY_TTS_SPEED, speed).apply();
        ttsManager.setSpeechRate(speed);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        
        // معالجة أذونات التسجيل الصوتي
        if (requestCode == 200) { // PERMISSION_REQUEST_RECORD_AUDIO
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "تم منح إذن التسجيل الصوتي", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "تم رفض إذن التسجيل الصوتي", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        
        // تنظيف الموارد
        if (voiceInputManager != null) {
            voiceInputManager.destroy();
        }
        
        // ملاحظة: TTS هو singleton، لذا لا نقوم بإغلاقه هنا
    }

    /**
     * طريقة مجردة للأنشطة المشتقة لتنفيذ البحث المخصص
     */
    protected abstract void performSearch(String query);

    /**
     * طريقة مجردة لقراءة المحتوى التلقائي عند فتح النشاط
     */
    protected abstract String getAutoReadContent();

    @Override
    protected void onResume() {
        super.onResume();
        
        // قراءة المحتوى تلقائياً إذا كان مفعلاً
        if (isAutoReadEnabled()) {
            String content = getAutoReadContent();
            if (content != null && !content.isEmpty()) {
                // تأخير قليل للسماح للواجهة بالتحميل كاملة
                new android.os.Handler().postDelayed(() -> speakText(content), 1000);
            }
        }
    }
}
