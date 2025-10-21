package com.example.androidapp.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.RecognitionListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * مدير الإدخال الصوتي - يوفر إمكانية تحويل الكلام إلى نص
 * يدعم اللغتين العربية والإنجليزية مع إدارة متقدمة للأذونات
 */
public class VoiceInputManager implements RecognitionListener {
    private static final int VOICE_RECOGNITION_REQUEST_CODE = 1001;
    private static final int PERMISSION_REQUEST_RECORD_AUDIO = 200;
    
    private Activity activity;
    private SpeechRecognizer speechRecognizer;
    private EditText targetEditText;
    private VoiceInputCallback callback;
    private boolean isListening = false;
    
    public interface VoiceInputCallback {
        void onVoiceInputResult(String result);
        void onVoiceInputError(String error);
        void onVoiceInputStarted();
        void onVoiceInputStopped();
    }
    
    public VoiceInputManager(Activity activity) {
        this.activity = activity;
        initializeSpeechRecognizer();
    }
    
    private void initializeSpeechRecognizer() {
        if (SpeechRecognizer.isRecognitionAvailable(activity)) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(activity);
            speechRecognizer.setRecognitionListener(this);
        }
    }
    
    /**
     * بدء تسجيل الصوت وتحويله إلى نص
     */
    public void startListening(EditText editText, VoiceInputCallback callback) {
        this.targetEditText = editText;
        this.callback = callback;
        
        if (!checkPermissions()) {
            requestPermissions();
            return;
        }
        
        if (speechRecognizer == null) {
            initializeSpeechRecognizer();
        }
        
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, getCurrentLanguage());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, getCurrentLanguage());
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, activity.getPackageName());
        intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);
        
        isListening = true;
        speechRecognizer.startListening(intent);
        
        if (callback != null) {
            callback.onVoiceInputStarted();
        }
    }
    
    /**
     * إيقاف التسجيل
     */
    public void stopListening() {
        if (speechRecognizer != null && isListening) {
            speechRecognizer.stopListening();
            isListening = false;
            if (callback != null) {
                callback.onVoiceInputStopped();
            }
        }
    }
    
    /**
     * تحديد اللغة الحالية بناءً على إعدادات النظام
     */
    private String getCurrentLanguage() {
        Locale locale = Locale.getDefault();
        String language = locale.getLanguage();
        
        if ("ar".equals(language)) {
            return "ar-SA"; // العربية السعودية
        } else {
            return "en-US"; // الإنجليزية الأمريكية
        }
    }
    
    /**
     * فحص أذونات التسجيل
     */
    private boolean checkPermissions() {
        return ContextCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO) 
               == PackageManager.PERMISSION_GRANTED;
    }
    
    /**
     * طلب أذونات التسجيل
     */
    private void requestPermissions() {
        ActivityCompat.requestPermissions(activity, 
            new String[]{Manifest.permission.RECORD_AUDIO}, 
            PERMISSION_REQUEST_RECORD_AUDIO);
    }
    
    /**
     * إعداد زر الإدخال الصوتي لمربع نص
     */
    public void setupVoiceButton(EditText editText, ImageButton voiceButton) {
        voiceButton.setOnClickListener(v -> {
            if (isListening) {
                stopListening();
            } else {
                startListening(editText, new VoiceInputCallback() {
                    @Override
                    public void onVoiceInputResult(String result) {
                        if (editText != null) {
                            String currentText = editText.getText().toString();
                            String newText = currentText.isEmpty() ? result : currentText + " " + result;
                            editText.setText(newText);
                            editText.setSelection(newText.length());
                        }
                    }
                    
                    @Override
                    public void onVoiceInputError(String error) {
                        Toast.makeText(activity, "خطأ في التعرف على الصوت: " + error, Toast.LENGTH_SHORT).show();
                    }
                    
                    @Override
                    public void onVoiceInputStarted() {
                        voiceButton.setImageResource(android.R.drawable.ic_media_pause);
                    }
                    
                    @Override
                    public void onVoiceInputStopped() {
                        voiceButton.setImageResource(android.R.drawable.ic_btn_speak_now);
                    }
                });
            }
        });
    }
    
    // تنفيذ RecognitionListener
    @Override
    public void onReadyForSpeech(android.os.Bundle params) {
        // جاهز للتسجيل
    }
    
    @Override
    public void onBeginningOfSpeech() {
        // بدء التحدث
    }
    
    @Override
    public void onRmsChanged(float rmsdB) {
        // تغيير قوة الصوت
    }
    
    @Override
    public void onBufferReceived(byte[] buffer) {
        // استلام البيانات الصوتية
    }
    
    @Override
    public void onEndOfSpeech() {
        // انتهاء التحدث
        isListening = false;
    }
    
    @Override
    public void onError(int error) {
        isListening = false;
        String errorMessage = getErrorMessage(error);
        if (callback != null) {
            callback.onVoiceInputError(errorMessage);
            callback.onVoiceInputStopped();
        }
    }
    
    @Override
    public void onResults(android.os.Bundle results) {
        isListening = false;
        ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        if (matches != null && !matches.isEmpty()) {
            String bestMatch = matches.get(0);
            if (callback != null) {
                callback.onVoiceInputResult(bestMatch);
                callback.onVoiceInputStopped();
            }
        }
    }
    
    @Override
    public void onPartialResults(android.os.Bundle partialResults) {
        // نتائج جزئية أثناء التحدث
    }
    
    @Override
    public void onEvent(int eventType, android.os.Bundle params) {
        // أحداث إضافية
    }
    
    /**
     * تحويل رمز الخطأ إلى رسالة مفهومة
     */
    private String getErrorMessage(int errorCode) {
        switch (errorCode) {
            case SpeechRecognizer.ERROR_AUDIO:
                return "خطأ في التسجيل الصوتي";
            case SpeechRecognizer.ERROR_CLIENT:
                return "خطأ في العميل";
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                return "أذونات غير كافية";
            case SpeechRecognizer.ERROR_NETWORK:
                return "خطأ في الشبكة";
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                return "انتهت مهلة الاتصال";
            case SpeechRecognizer.ERROR_NO_MATCH:
                return "لم يتم العثور على تطابق";
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                return "المعرف مشغول";
            case SpeechRecognizer.ERROR_SERVER:
                return "خطأ في الخادم";
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                return "انتهت مهلة التحدث";
            default:
                return "خطأ غير معروف";
        }
    }
    
    /**
     * تنظيف الموارد
     */
    public void destroy() {
        if (speechRecognizer != null) {
            speechRecognizer.destroy();
        }
    }
}