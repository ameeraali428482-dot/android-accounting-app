package com.example.androidapp.utils;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.widget.Toast;

import java.util.Locale;

/**
 * مدير تحويل النص إلى كلام
 */
public class TextToSpeechManager {
    
    private static TextToSpeechManager instance;
    private TextToSpeech tts;
    private Context context;
    private boolean isInitialized = false;
    
    public static TextToSpeechManager getInstance(Context context) {
        if (instance == null) {
            instance = new TextToSpeechManager(context);
        }
        return instance;
    }
    
    private TextToSpeechManager(Context context) {
        this.context = context;
        initializeTTS();
    }
    
    private void initializeTTS() {
        tts = new TextToSpeech(context, status -> {
            if (status == TextToSpeech.SUCCESS) {
                int result = tts.setLanguage(new Locale("ar"));
                if (result == TextToSpeech.LANG_MISSING_DATA || 
                    result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Toast.makeText(context, "اللغة العربية غير مدعومة", Toast.LENGTH_SHORT).show();
                } else {
                    isInitialized = true;
                }
            } else {
                Toast.makeText(context, "فشل في تهيئة تحويل النص إلى كلام", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    public void speak(String text, TTSCallback callback) {
        if (!isInitialized || text == null || text.isEmpty()) {
            return;
        }
        
        tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
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
                    callback.onSpeechError("خطأ في النطق");
                }
            }
        });
        
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "tts_utterance");
    }
    
    public void speakDocument(String documentType, String content) {
        String fullText = documentType + ". " + content;
        speak(fullText, null);
    }
    
    public void setSpeechRate(float rate) {
        if (tts != null) {
            tts.setSpeechRate(rate);
        }
    }
    
    public interface TTSCallback {
        void onSpeechStarted();
        void onSpeechCompleted();
        void onSpeechError(String error);
    }
}
