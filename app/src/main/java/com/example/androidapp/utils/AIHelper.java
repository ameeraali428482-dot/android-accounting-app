package com.example.androidapp.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import androidx.room.Room;
import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.entities.*;
import org.json.JSONObject;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * مساعد الذكاء الاصطناعي المبسط - يوفر تحليلات ذكية ومحادثة تفاعلية للبيانات المحاسبية
 */
public class AIHelper {
    
    private static final String TAG = "AIHelper";
    private static final String PREFS_NAME = "ai_helper_prefs";
    private static final String AI_API_KEY = "ai_api_key";
    private static final String AI_API_URL = "ai_api_url";
    private static final String AI_ENABLED_KEY = "ai_enabled";
    
    // إعدادات الذكاء الاصطناعي
    private static final int MAX_TOKENS = 2048;
    private static final double TEMPERATURE = 0.7;
    private static final int CONNECTION_TIMEOUT = 30000;
    private static final int READ_TIMEOUT = 60000;
    
    // أنواع التحليلات
    public static final String ANALYSIS_TYPE_FINANCIAL = "FINANCIAL";
    public static final String ANALYSIS_TYPE_TRENDS = "TRENDS";
    
    private Context context;
    private SharedPreferences prefs;
    private AppDatabase database;
    private ExecutorService executorService;
    private RoleManager roleManager;
    
    public AIHelper(Context context) {
        this.context = context;
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        this.database = Room.databaseBuilder(context, AppDatabase.class, "app_database")
                .fallbackToDestructiveMigration()
                .build();
        this.executorService = Executors.newCachedThreadPool();
        this.roleManager = new RoleManager(context);
    }
    
    /**
     * إرسال رسالة للذكاء الاصطناعي للمحادثة العامة
     */
    public Future<AIResponse> sendChatMessage(String userId, String message, String userRole) {
        return executorService.submit(() -> {
            try {
                if (!isAIEnabled() || !hasAIPermission(userId)) {
                    return new AIResponse(false, "الذكاء الاصطناعي غير متاح أو لا تملك الصلاحية");
                }
                
                // إنشاء السياق للمحادثة
                String context = buildChatContext(userId, userRole);
                
                // إنشاء النص المرسل للذكاء الاصطناعي
                String prompt = buildChatPrompt(message, context, userRole);
                
                // إرسال الطلب للذكاء الاصطناعي
                String aiResponse = sendToAI(prompt, "USER_ASSISTANT");
                
                return new AIResponse(true, aiResponse);
                
            } catch (Exception e) {
                Log.e(TAG, "Error in chat message", e);
                return new AIResponse(false, "حدث خطأ أثناء المحادثة: " + e.getMessage());
            }
        });
    }
    
    /**
     * إرسال طلب تحليل للمدراء
     */
    public Future<AIResponse> sendAnalysisRequest(String userId, String query, String analysisType) {
        return executorService.submit(() -> {
            try {
                if (!isAIEnabled() || !hasAdminAIPermission(userId)) {
                    return new AIResponse(false, "الذكاء الاصطناعي للتحليل غير متاح أو لا تملك الصلاحية");
                }
                
                // جمع البيانات المطلوبة للتحليل
                AnalysisData analysisData = gatherAnalysisData(analysisType);
                
                // إنشاء النص المرسل للذكاء الاصطناعي
                String prompt = buildAnalysisPrompt(query, analysisType, analysisData);
                
                // إرسال الطلب للذكاء الاصطناعي
                String aiResponse = sendToAI(prompt, "ADMIN_ANALYST");
                
                return new AIResponse(true, aiResponse);
                
            } catch (Exception e) {
                Log.e(TAG, "Error in analysis request", e);
                return new AIResponse(false, "حدث خطأ أثناء التحليل: " + e.getMessage());
            }
        });
    }
    
    /**
     * إرسال طلب للذكاء الاصطناعي
     */
    private String sendToAI(String prompt, String role) throws Exception {
        String apiKey = getAIApiKey();
        String apiUrl = getAIApiUrl();
        
        if (apiKey.isEmpty() || apiUrl.isEmpty()) {
            throw new Exception("إعدادات الذكاء الاصطناعي غير مكتملة");
        }
        
        // إنشاء payload الطلب
        JSONObject requestBody = new JSONObject();
        requestBody.put("model", "gpt-3.5-turbo");
        requestBody.put("max_tokens", MAX_TOKENS);
        requestBody.put("temperature", TEMPERATURE);
        requestBody.put("prompt", prompt);
        
        // إرسال الطلب
        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        
        try {
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", "Bearer " + apiKey);
            connection.setConnectTimeout(CONNECTION_TIMEOUT);
            connection.setReadTimeout(READ_TIMEOUT);
            connection.setDoOutput(true);
            
            // إرسال البيانات
            try (OutputStream os = connection.getOutputStream();
                 OutputStreamWriter writer = new OutputStreamWriter(os, "UTF-8")) {
                writer.write(requestBody.toString());
                writer.flush();
            }
            
            // قراءة الاستجابة
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream(), "UTF-8"))) {
                    
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    
                    // استخراج النص من الاستجابة
                    return extractResponseText(response.toString());
                }
            } else {
                throw new Exception("خطأ في استدعاء الذكاء الاصطناعي: " + responseCode);
            }
            
        } finally {
            connection.disconnect();
        }
    }
    
    /**
     * استخراج النص من استجابة الذكاء الاصطناعي
     */
    private String extractResponseText(String jsonResponse) {
        try {
            JSONObject response = new JSONObject(jsonResponse);
            return response.optString("text", "لم يتم استلام رد من الذكاء الاصطناعي");
        } catch (Exception e) {
            Log.e(TAG, "Error parsing AI response", e);
            return "حدث خطأ في معالجة استجابة الذكاء الاصطناعي";
        }
    }
    
    /**
     * بناء نص المحادثة
     */
    private String buildChatContext(String userId, String userRole) {
        StringBuilder context = new StringBuilder();
        
        context.append("معلومات المستخدم:\n");
        context.append("- معرف المستخدم: ").append(userId).append("\n");
        context.append("- الدور: ").append(userRole).append("\n");
        
        return context.toString();
    }
    
    /**
     * بناء نص المحادثة
     */
    private String buildChatPrompt(String message, String context, String userRole) {
        StringBuilder prompt = new StringBuilder();
        
        prompt.append("السياق:\n").append(context).append("\n\n");
        prompt.append("دور المستخدم: ").append(userRole).append("\n\n");
        prompt.append("رسالة المستخدم:\n").append(message).append("\n\n");
        prompt.append("يرجى الرد بطريقة مناسبة ومفيدة باللغة العربية.");
        
        return prompt.toString();
    }
    
    /**
     * بناء نص التحليل
     */
    private String buildAnalysisPrompt(String query, String analysisType, AnalysisData data) {
        StringBuilder prompt = new StringBuilder();
        
        prompt.append("نوع التحليل المطلوب: ").append(analysisType).append("\n\n");
        prompt.append("الاستفسار: ").append(query).append("\n\n");
        prompt.append("يرجى تقديم تحليل مفيد باللغة العربية.");
        
        return prompt.toString();
    }
    
    /**
     * جمع بيانات التحليل
     */
    private AnalysisData gatherAnalysisData(String analysisType) {
        AnalysisData data = new AnalysisData();
        
        try {
            // جمع البيانات حسب نوع التحليل
            switch (analysisType) {
                case ANALYSIS_TYPE_FINANCIAL:
                    data.accounts = database.accountDao().getAllAccountsSync();
                    break;
                    
                case ANALYSIS_TYPE_TRENDS:
                    // جمع بيانات الاتجاهات
                    break;
                    
                default:
                    data.accounts = database.accountDao().getAllAccountsSync();
                    break;
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error gathering analysis data", e);
        }
        
        return data;
    }
    
    // Helper methods
    
    private boolean isAIEnabled() {
        return prefs.getBoolean(AI_ENABLED_KEY, false);
    }
    
    private boolean hasAIPermission(String userId) {
        return roleManager.hasPermission(userId, "ai_chat");
    }
    
    private boolean hasAdminAIPermission(String userId) {
        return roleManager.isAdmin(userId) && hasAIPermission(userId);
    }
    
    private String getAIApiKey() {
        return prefs.getString(AI_API_KEY, "");
    }
    
    private String getAIApiUrl() {
        return prefs.getString(AI_API_URL, "https://api.openai.com/v1/completions");
    }
    
    public void setAIApiKey(String apiKey) {
        prefs.edit().putString(AI_API_KEY, apiKey).apply();
    }
    
    public void setAIApiUrl(String apiUrl) {
        prefs.edit().putString(AI_API_URL, apiUrl).apply();
    }
    
    public void setAIEnabled(boolean enabled) {
        prefs.edit().putBoolean(AI_ENABLED_KEY, enabled).apply();
    }
    
    /**
     * تنظيف الموارد
     */
    public void cleanup() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
    
    // Classes مساعدة
    
    public static class AIResponse {
        private boolean success;
        private String message;
        private Map<String, Object> data;
        
        public AIResponse(boolean success, String message) {
            this.success = success;
            this.message = message;
            this.data = new HashMap<>();
        }
        
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public Map<String, Object> getData() { return data; }
        public void setData(String key, Object value) { data.put(key, value); }
    }
    
    public static class AnalysisData {
        public List<Account> accounts = new ArrayList<>();
        public List<Transaction> transactions = new ArrayList<>();
    }
}
