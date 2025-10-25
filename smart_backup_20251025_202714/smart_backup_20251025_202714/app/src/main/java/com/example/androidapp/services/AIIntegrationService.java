package com.example.androidapp.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import androidx.annotation.Nullable;

import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.dao.AIConversationDao;
import com.example.androidapp.data.entities.AIConversation;
import com.example.androidapp.utils.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.*;

/**
 * خدمة تكامل الذكاء الاصطناعي - لمعالجة طلبات AI والحصول على ردود ذكية
 */
public class AIIntegrationService extends Service {

    private static final String AI_API_URL = "https://api.openai.com/v1/chat/completions"; // مثال
    private static final String AI_API_KEY = "your-api-key-here"; // يجب الحصول عليه من الإعدادات
    
    private AIConversationDao aiConversationDao;
    private SessionManager sessionManager;
    private OkHttpClient httpClient;
    private ExecutorService executorService;
    
    @Override
    public void onCreate() {
        super.onCreate();
        
        AppDatabase database = AppDatabase.getDatabase(this);
        aiConversationDao = database.aiConversationDao();
        sessionManager = new SessionManager(this);
        
        httpClient = new OkHttpClient.Builder()
                .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                .readTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
                .build();
                
        executorService = Executors.newFixedThreadPool(3);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * معالجة محادثة مع الذكاء الاصطناعي
     */
    public void processAIConversation(String userId, String conversationType, 
                                     String userMessage, String sessionId,
                                     AIResponseCallback callback) {
        executorService.execute(() -> {
            try {
                long startTime = System.currentTimeMillis();
                
                // إعداد السياق حسب نوع المحادثة
                String systemPrompt = buildSystemPrompt(conversationType, userId);
                
                // إرسال الطلب إلى AI
                String aiResponse = sendAIRequest(systemPrompt, userMessage, conversationType);
                
                long responseTime = System.currentTimeMillis() - startTime;
                
                // حفظ المحادثة في قاعدة البيانات
                AIConversation conversation = new AIConversation();
                conversation.setId(UUID.randomUUID().toString());
                conversation.setUserId(userId);
                conversation.setConversationType(conversationType);
                conversation.setSessionId(sessionId);
                conversation.setUserMessage(userMessage);
                conversation.setAiResponse(aiResponse);
                conversation.setResponseTimeMs(responseTime);
                conversation.setConfidenceScore(calculateConfidenceScore(aiResponse));
                conversation.setLanguage(detectLanguage(userMessage));
                
                // إضافة تحليل البيانات للمحادثات المحاسبية
                if ("ACCOUNTING_ANALYSIS".equals(conversationType)) {
                    addAccountingAnalysis(conversation, userId);
                }
                
                aiConversationDao.insert(conversation);
                
                // إرجاع النتيجة
                if (callback != null) {
                    callback.onSuccess(conversation);
                }
                
            } catch (Exception e) {
                e.printStackTrace();
                if (callback != null) {
                    callback.onError(e.getMessage());
                }
            }
        });
    }

    private String buildSystemPrompt(String conversationType, String userId) {
        StringBuilder prompt = new StringBuilder();
        
        switch (conversationType) {
            case "ACCOUNTING_ANALYSIS":
                prompt.append("أنت مساعد محاسبي ذكي متخصص في المحاسبة والتحليل المالي. ");
                prompt.append("مهمتك هي تحليل البيانات المحاسبية وتقديم نصائح مالية دقيقة ومفيدة. ");
                prompt.append("استخدم خبرتك في المحاسبة لتقديم اقتراحات عملية وتحذيرات مالية مناسبة.");
                break;
                
            case "GENERAL_CHAT":
                prompt.append("أنت مساعد ذكي ودود يساعد المستخدمين في أسئلتهم العامة. ");
                prompt.append("تحدث باللغة العربية وكن مفيداً ومهذباً في ردودك.");
                break;
                
            case "DATA_INSIGHTS":
                prompt.append("أنت محلل بيانات خبير متخصص في استخراج الرؤى من البيانات التجارية. ");
                prompt.append("قدم تحليلات عميقة ومفيدة للبيانات المقدمة.");
                break;
                
            case "FINANCIAL_ADVICE":
                prompt.append("أنت مستشار مالي خبير يقدم نصائح مالية احترافية. ");
                prompt.append("ركز على تقديم نصائح عملية وآمنة ومناسبة للسياق المقدم.");
                break;
        }
        
        return prompt.toString();
    }

    private String sendAIRequest(String systemPrompt, String userMessage, String conversationType) 
            throws IOException, JSONException {
        
        JSONObject requestBody = new JSONObject();
        requestBody.put("model", "gpt-3.5-turbo"); // أو النموذج المناسب
        
        JSONArray messages = new JSONArray();
        
        // رسالة النظام
        JSONObject systemMessage = new JSONObject();
        systemMessage.put("role", "system");
        systemMessage.put("content", systemPrompt);
        messages.put(systemMessage);
        
        // رسالة المستخدم
        JSONObject userMessageObj = new JSONObject();
        userMessageObj.put("role", "user");
        userMessageObj.put("content", userMessage);
        messages.put(userMessageObj);
        
        requestBody.put("messages", messages);
        requestBody.put("max_tokens", 1000);
        requestBody.put("temperature", 0.7);
        
        RequestBody body = RequestBody.create(
            requestBody.toString(),
            MediaType.get("application/json")
        );
        
        Request request = new Request.Builder()
                .url(AI_API_URL)
                .header("Authorization", "Bearer " + AI_API_KEY)
                .header("Content-Type", "application/json")
                .post(body)
                .build();
        
        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("فشل في الحصول على رد من AI: " + response.code());
            }
            
            String responseBody = response.body().string();
            JSONObject jsonResponse = new JSONObject(responseBody);
            
            JSONArray choices = jsonResponse.getJSONArray("choices");
            if (choices.length() > 0) {
                JSONObject firstChoice = choices.getJSONObject(0);
                JSONObject message = firstChoice.getJSONObject("message");
                return message.getString("content");
            }
            
            throw new IOException("لم يتم الحصول على رد صحيح من AI");
        }
    }

    private void addAccountingAnalysis(AIConversation conversation, String userId) {
        try {
            // يمكن إضافة تحليل للبيانات المحاسبية هنا
            // مثل جلب بيانات الفواتير، المدفوعات، الحسابات، إلخ
            
            JSONObject analysisData = new JSONObject();
            analysisData.put("analysis_type", "accounting_insight");
            analysisData.put("user_id", userId);
            analysisData.put("timestamp", new Date().getTime());
            
            conversation.setAnalysisResults(analysisData.toString());
            
            // إضافة اقتراحات
            JSONArray suggestions = new JSONArray();
            suggestions.put("راجع الفواتير المستحقة هذا الشهر");
            suggestions.put("تحقق من الحسابات المعلقة");
            suggestions.put("راجع تقرير الأرباح والخسائر");
            
            conversation.setSuggestions(suggestions.toString());
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private float calculateConfidenceScore(String aiResponse) {
        // حساب درجة الثقة بناءً على طول وجودة الرد
        if (aiResponse == null || aiResponse.trim().isEmpty()) {
            return 0.0f;
        }
        
        int length = aiResponse.length();
        if (length < 50) return 0.3f;
        if (length < 100) return 0.5f;
        if (length < 200) return 0.7f;
        if (length < 500) return 0.8f;
        return 0.9f;
    }

    private String detectLanguage(String text) {
        // كشف اللغة بناءً على الأحرف
        if (text.matches(".*[\u0600-\u06FF].*")) {
            return "ar"; // عربي
        }
        return "en"; // افتراضي إنجليزي
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }

    /**
     * واجهة للحصول على رد AI
     */
    public interface AIResponseCallback {
        void onSuccess(AIConversation conversation);
        void onError(String error);
    }
}
