package com.example.androidapp.utils;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.widget.Toast;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * مدير تحويل النص إلى كلام - يدعم اللغتين العربية والإنجليزية
 * مع إعدادات متقدمة للسرعة والنبرة
 */
public class TextToSpeechManager {
    private static TextToSpeechManager instance;
    private TextToSpeech textToSpeech;
    private Context context;
    private boolean isInitialized = false;
    private TTSCallback callback;
    
    public interface TTSCallback {
        void onSpeechStarted();
        void onSpeechCompleted();
        void onSpeechError(String error);
    }
    
    private TextToSpeechManager(Context context) {
        this.context = context.getApplicationContext();
        initializeTextToSpeech();
    }
    
    public static synchronized TextToSpeechManager getInstance(Context context) {
        if (instance == null) {
            instance = new TextToSpeechManager(context);
        }
        return instance;
    }
    
    /**
     * تهيئة محرك تحويل النص إلى كلام
     */
    private void initializeTextToSpeech() {
        textToSpeech = new TextToSpeech(context, status -> {
            if (status == TextToSpeech.SUCCESS) {
                setupLanguage();
                setupSpeechParameters();
                setupUtteranceListener();
                isInitialized = true;
            } else {
                Toast.makeText(context, "فشل في تهيئة تحويل النص إلى كلام", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    /**
     * إعداد اللغة بناءً على إعدادات النظام
     */
    private void setupLanguage() {
        Locale currentLocale = Locale.getDefault();
        
        // تجربة اللغة الحالية أولاً
        int result = textToSpeech.setLanguage(currentLocale);
        
        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
            // إذا لم تكن مدعومة، جرب العربية
            result = textToSpeech.setLanguage(new Locale("ar", "SA"));
            
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                // إذا فشلت العربية، استخدم الإنجليزية كافتراضي
                textToSpeech.setLanguage(Locale.US);
            }
        }
    }
    
    /**
     * إعداد معاملات الكلام
     */
    private void setupSpeechParameters() {
        textToSpeech.setSpeechRate(0.8f); // سرعة معتدلة
        textToSpeech.setPitch(1.0f); // نبرة طبيعية
    }
    
    /**
     * إعداد مستمع تقدم النطق
     */
    private void setupUtteranceListener() {
        textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {
                if (callback != null) {
                    callback.onSpeechStarted();
                }
            }
            
            @Override
            public void onDone(String utteranceId) {
                if (callback != null) {
                    callback.onSpeechCompleted();
                }
            }
            
            @Override
            public void onError(String utteranceId) {
                if (callback != null) {
                    callback.onSpeechError("خطأ في تحويل النص إلى كلام");
                }
            }
        });
    }
    
    /**
     * نطق النص المحدد
     */
    public void speak(String text, TTSCallback callback) {
        this.callback = callback;
        
        if (!isInitialized) {
            Toast.makeText(context, "محرك التحويل غير مهيأ بعد", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (text == null || text.trim().isEmpty()) {
            Toast.makeText(context, "لا يوجد نص لنطقه", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // إنشاء معرف فريد للنطق
        String utteranceId = "tts_" + System.currentTimeMillis();
        
        // إعداد معاملات النطق
        HashMap<String, String> params = new HashMap<>();
        params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, utteranceId);
        
        // بدء النطق
        int result = textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, params);
        
        if (result == TextToSpeech.ERROR) {
            Toast.makeText(context, "خطأ في بدء النطق", Toast.LENGTH_SHORT).show();
            if (callback != null) {
                callback.onSpeechError("خطأ في بدء النطق");
            }
        }
    }
    
    /**
     * نطق النص بدون callback
     */
    public void speak(String text) {
        speak(text, null);
    }
    
    /**
     * إيقاف النطق الحالي
     */
    public void stop() {
        if (isInitialized && textToSpeech.isSpeaking()) {
            textToSpeech.stop();
        }
    }
    
    /**
     * فحص ما إذا كان النطق جارياً
     */
    public boolean isSpeaking() {
        return isInitialized && textToSpeech.isSpeaking();
    }
    
    /**
     * تعديل سرعة النطق
     */
    public void setSpeechRate(float rate) {
        if (isInitialized) {
            textToSpeech.setSpeechRate(rate);
        }
    }
    
    /**
     * تعديل نبرة الصوت
     */
    public void setPitch(float pitch) {
        if (isInitialized) {
            textToSpeech.setPitch(pitch);
        }
    }
    
    /**
     * تغيير اللغة
     */
    public boolean setLanguage(Locale locale) {
        if (!isInitialized) {
            return false;
        }
        
        int result = textToSpeech.setLanguage(locale);
        return result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED;
    }
    
    /**
     * الحصول على اللغات المدعومة
     */
    public boolean isLanguageSupported(Locale locale) {
        if (!isInitialized) {
            return false;
        }
        
        int result = textToSpeech.isLanguageAvailable(locale);
        return result >= TextToSpeech.LANG_AVAILABLE;
    }
    
    /**
     * نطق محتوى مخصص للأنواع المختلفة من الوثائق
     */
    public void speakDocument(String documentType, String content) {
        String prefix = getDocumentPrefix(documentType);
        String fullText = prefix + ". " + content;
        speak(fullText);
    }
    
    /**
     * الحصول على مقدمة مناسبة لنوع الوثيقة
     */
    private String getDocumentPrefix(String documentType) {
        switch (documentType.toLowerCase()) {
            case "invoice":
            case "فاتورة":
                return "فاتورة رقم";
            case "receipt":
            case "إيصال":
                return "إيصال رقم";
            case "statement":
            case "كشف":
                return "كشف حساب";
            case "journal":
            case "قيد":
                return "قيد يومية";
            case "report":
            case "تقرير":
                return "تقرير";
            default:
                return "وثيقة";
        }
    }
    
    /**
     * تنظيف الموارد
     */
    public void shutdown() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        isInitialized = false;
    }
}