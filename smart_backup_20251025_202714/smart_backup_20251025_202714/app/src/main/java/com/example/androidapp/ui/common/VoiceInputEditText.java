package com.example.androidapp.ui.common;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import androidx.annotation.Nullable;
import com.google.android.material.textfield.TextInputLayout;
import com.example.androidapp.R;
import com.example.androidapp.utils.VoiceInputManager;
import com.example.androidapp.utils.SmartSuggestionsManager;
import com.example.androidapp.utils.Material3Helper;

/**
 * EditText مخصص مع دعم الإدخال الصوتي والاقتراحات الذكية
 * يدعم Material Design 3 ويتضمن واجهة سهلة الاستخدام
 */
public class VoiceInputEditText extends LinearLayout {
    
    private TextInputLayout textInputLayout;
    private EditText editText;
    private ImageButton voiceButton;
    private VoiceInputManager voiceManager;
    private SmartSuggestionsManager suggestionsManager;
    
    private String hint;
    private SmartSuggestionsManager.SuggestionType suggestionType;
    private boolean voiceEnabled = true;
    private boolean suggestionsEnabled = true;
    
    public VoiceInputEditText(Context context) {
        super(context);
        init(context, null);
    }
    
    public VoiceInputEditText(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }
    
    public VoiceInputEditText(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }
    
    private void init(Context context, AttributeSet attrs) {
        setOrientation(HORIZONTAL);
        
        // إنشاء المكونات
        createComponents(context);
        
        // قراءة الخصائص من XML
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.VoiceInputEditText);
            
            hint = typedArray.getString(R.styleable.VoiceInputEditText_hint);
            voiceEnabled = typedArray.getBoolean(R.styleable.VoiceInputEditText_voiceEnabled, true);
            suggestionsEnabled = typedArray.getBoolean(R.styleable.VoiceInputEditText_suggestionsEnabled, true);
            
            int suggestionTypeIndex = typedArray.getInt(R.styleable.VoiceInputEditText_suggestionType, 0);
            suggestionType = SmartSuggestionsManager.SuggestionType.values()[suggestionTypeIndex];
            
            typedArray.recycle();
        }
        
        // تطبيق الإعدادات
        applySettings();
        
        // إعداد المديرات
        setupManagers();
    }
    
    private void createComponents(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.voice_input_edittext, this, true);
        
        textInputLayout = findViewById(R.id.textInputLayout);
        editText = findViewById(R.id.editText);
        voiceButton = findViewById(R.id.voiceButton);
    }
    
    private void applySettings() {
        if (hint != null) {
            textInputLayout.setHint(hint);
            editText.setHint(hint);
        }
        
        voiceButton.setVisibility(voiceEnabled ? VISIBLE : GONE);
        
        // تطبيق Material Design 3
        Material3Helper.Components.setupTextInputLayout(textInputLayout, Material3Helper.InputStyle.OUTLINED);
    }
    
    private void setupManagers() {
        if (getContext() instanceof android.app.Activity) {
            // إعداد مدير الصوت
            if (voiceEnabled) {
                voiceManager = new VoiceInputManager((android.app.Activity) getContext());
                setupVoiceInput();
            }
            
            // إعداد مدير الاقتراحات
            if (suggestionsEnabled && suggestionType != null) {
                suggestionsManager = new SmartSuggestionsManager(getContext());
                suggestionsManager.attachSmartSuggestions(editText, suggestionType);
            }
        }
    }
    
    private void setupVoiceInput() {
        voiceButton.setOnClickListener(v -> {
            if (voiceManager != null) {
                voiceManager.startVoiceInput(editText, new VoiceInputManager.VoiceInputCallback() {
                    @Override
                    public void onVoiceInputResult(String result) {
                        editText.setText(result);
                        editText.setSelection(result.length());
                    }
                    
                    @Override
                    public void onVoiceInputError(String error) {
                        // إظهار رسالة خطأ
                        textInputLayout.setError(error);
                        
                        // إزالة الخطأ بعد 3 ثوان
                        postDelayed(() -> textInputLayout.setError(null), 3000);
                    }
                    
                    @Override
                    public void onVoiceInputStarted() {
                        voiceButton.setAlpha(0.5f);
                        voiceButton.setEnabled(false);
                        textInputLayout.setHelperText("جاري الاستماع...");
                    }
                    
                    @Override
                    public void onVoiceInputStopped() {
                        voiceButton.setAlpha(1.0f);
                        voiceButton.setEnabled(true);
                        textInputLayout.setHelperText(null);
                    }
                });
            }
        });
    }
    
    // Getters and Setters
    public String getText() {
        return editText.getText().toString();
    }
    
    public void setText(String text) {
        editText.setText(text);
    }
    
    public void setHint(String hint) {
        this.hint = hint;
        textInputLayout.setHint(hint);
        editText.setHint(hint);
    }
    
    public void setError(String error) {
        textInputLayout.setError(error);
    }
    
    public void clearError() {
        textInputLayout.setError(null);
    }
    
    public EditText getEditText() {
        return editText;
    }
    
    public TextInputLayout getTextInputLayout() {
        return textInputLayout;
    }
    
    public void setSuggestionType(SmartSuggestionsManager.SuggestionType type) {
        this.suggestionType = type;
        if (suggestionsManager != null && suggestionsEnabled) {
            suggestionsManager.attachSmartSuggestions(editText, type);
        }
    }
    
    public void setVoiceEnabled(boolean enabled) {
        this.voiceEnabled = enabled;
        voiceButton.setVisibility(enabled ? VISIBLE : GONE);
    }
    
    public void setSuggestionsEnabled(boolean enabled) {
        this.suggestionsEnabled = enabled;
        if (enabled && suggestionType != null) {
            setupManagers();
        }
    }
    
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        
        // تنظيف الموارد
        if (suggestionsManager != null) {
            suggestionsManager.destroy();
        }
    }
}