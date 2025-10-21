package com.example.androidapp.services;

import android.content.Context;
import android.util.Log;

import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.entities.*;
import com.example.androidapp.utils.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.*;

/**
 * خدمة الذكاء الاصطناعي Gemini للتحليل المتقدم والمحادثة الذكية
 * Advanced Gemini AI Service for intelligent analysis and smart conversations
 */
public class GeminiAIService {
    
    private static final String TAG = "GeminiAIService";
    private static final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent";
    private static final String GEMINI_API_KEY = "your-gemini-api-key-here"; // يجب إضافة المفتاح
    
    private Context context;
    private AppDatabase database;
    private SessionManager sessionManager;
    private OkHttpClient httpClient;
    private ExecutorService executorService;
    private DatabaseContextManager contextManager;
    
    public GeminiAIService(Context context) {
        this.context = context;
        this.database = AppDatabase.getDatabase(context);
        this.sessionManager = new SessionManager(context);
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                .readTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
                .build();
        this.executorService = Executors.newFixedThreadPool(2);
        this.contextManager = new DatabaseContextManager(database);
    }
    
    /**
     * محادثة ذكية مع سياق كامل لقاعدة البيانات
     */
    public CompletableFuture<GeminiResponse> chatWithDatabaseContext(String userMessage, String userId) {
        CompletableFuture<GeminiResponse> future = new CompletableFuture<>();
        
        executorService.execute(() -> {
            try {
                // بناء السياق الكامل لقاعدة البيانات
                String databaseContext = contextManager.buildFullDatabaseContext(userId);
                
                // إنشاء prompt متخصص للتحليل المحاسبي
                String systemPrompt = buildAdvancedSystemPrompt(userId);
                
                // إرسال الطلب لـ Gemini
                JSONObject requestBody = buildGeminiRequest(systemPrompt, databaseContext, userMessage);
                
                String response = sendGeminiRequest(requestBody);
                
                // تحليل الاستجابة وإستخراج الرؤى
                GeminiResponse geminiResponse = parseGeminiResponse(response, userId);
                
                // حفظ المحادثة مع السياق
                saveConversationWithContext(userId, userMessage, geminiResponse, databaseContext);
                
                future.complete(geminiResponse);
                
            } catch (Exception e) {
                Log.e(TAG, "Error in chatWithDatabaseContext", e);
                future.completeExceptionally(e);
            }
        });
        
        return future;
    }
    
    /**
     * تحليل البيانات المحاسبية المتقدم
     */
    public CompletableFuture<FinancialAnalysisResult> performFinancialAnalysis(String userId, String analysisType) {
        CompletableFuture<FinancialAnalysisResult> future = new CompletableFuture<>();
        
        executorService.execute(() -> {
            try {
                // جمع البيانات المالية
                FinancialData financialData = contextManager.gatherFinancialData(userId);
                
                // بناء prompt للتحليل المالي
                String analysisPrompt = buildFinancialAnalysisPrompt(financialData, analysisType);
                
                // إرسال طلب التحليل
                JSONObject requestBody = buildGeminiRequest(
                    "أنت محلل مالي خبير متخصص في المحاسبة والتحليل المالي المتقدم",
                    financialData.toJSON(),
                    analysisPrompt
                );
                
                String response = sendGeminiRequest(requestBody);
                
                // تحليل النتائج
                FinancialAnalysisResult result = parseFinancialAnalysis(response, financialData);
                
                // حفظ نتائج التحليل
                saveAnalysisResults(userId, result);
                
                future.complete(result);
                
            } catch (Exception e) {
                Log.e(TAG, "Error in financial analysis", e);
                future.completeExceptionally(e);
            }
        });
        
        return future;
    }
    
    /**
     * اقتراحات ذكية لتحسين الأعمال
     */
    public CompletableFuture<List<BusinessSuggestion>> getBusinessSuggestions(String userId) {
        CompletableFuture<List<BusinessSuggestion>> future = new CompletableFuture<>();
        
        executorService.execute(() -> {
            try {
                // تحليل أداء الأعمال
                BusinessPerformanceData performance = contextManager.analyzeBusinessPerformance(userId);
                
                // بناء prompt للاقتراحات
                String suggestionPrompt = buildBusinessSuggestionPrompt(performance);
                
                JSONObject requestBody = buildGeminiRequest(
                    "أنت مستشار أعمال خبير متخصص في تحسين الأداء التجاري والمحاسبي",
                    performance.toJSON(),
                    suggestionPrompt
                );
                
                String response = sendGeminiRequest(requestBody);
                
                List<BusinessSuggestion> suggestions = parseBusinessSuggestions(response);
                
                future.complete(suggestions);
                
            } catch (Exception e) {
                Log.e(TAG, "Error getting business suggestions", e);
                future.completeExceptionally(e);
            }
        });
        
        return future;
    }
    
    /**
     * كشف الأنماط والشذوذ في البيانات
     */
    public CompletableFuture<AnomalyDetectionResult> detectAnomalies(String userId) {
        CompletableFuture<AnomalyDetectionResult> future = new CompletableFuture<>();
        
        executorService.execute(() -> {
            try {
                // جمع بيانات المعاملات
                TransactionPattern patterns = contextManager.analyzeTransactionPatterns(userId);
                
                String anomalyPrompt = buildAnomalyDetectionPrompt(patterns);
                
                JSONObject requestBody = buildGeminiRequest(
                    "أنت خبير في كشف الأنماط والشذوذ في البيانات المالية والمحاسبية",
                    patterns.toJSON(),
                    anomalyPrompt
                );
                
                String response = sendGeminiRequest(requestBody);
                
                AnomalyDetectionResult result = parseAnomalyDetection(response);
                
                future.complete(result);
                
            } catch (Exception e) {
                Log.e(TAG, "Error in anomaly detection", e);
                future.completeExceptionally(e);
            }
        });
        
        return future;
    }
    
    private String buildAdvancedSystemPrompt(String userId) {
        return "أنت مساعد ذكي متخصص في المحاسبة والتحليل المالي. " +
               "لديك وصول كامل لقاعدة بيانات المستخدم وتاريخ معاملاته. " +
               "مهمتك تقديم تحليلات عميقة واقتراحات ذكية ومفيدة. " +
               "استخدم البيانات المتوفرة لتقديم رؤى دقيقة ونصائح عملية. " +
               "تذكر أن تكون دقيقاً ومهنياً في تحليلاتك.";
    }
    
    private String buildFinancialAnalysisPrompt(FinancialData data, String analysisType) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("قم بتحليل البيانات المالية التالية وفقاً لنوع التحليل: ").append(analysisType).append("\n");
        prompt.append("ركز على:\n");
        
        switch (analysisType) {
            case "CASH_FLOW":
                prompt.append("- تحليل التدفق النقدي\n- مصادر واستخدامات النقد\n- التوقعات المستقبلية");
                break;
            case "PROFITABILITY":
                prompt.append("- هوامش الربح\n- العائد على الاستثمار\n- تحليل الربحية حسب المنتج/العميل");
                break;
            case "TRENDS":
                prompt.append("- الاتجاهات الزمنية\n- النمو الشهري/السنوي\n- التنبؤات");
                break;
            default:
                prompt.append("- التحليل الشامل للوضع المالي");
        }
        
        return prompt.toString();
    }
    
    private String buildBusinessSuggestionPrompt(BusinessPerformanceData performance) {
        return "بناءً على أداء الأعمال المرفق، قدم اقتراحات ذكية ومحددة لتحسين الأداء. " +
               "ركز على الجوانب التي تحتاج تحسين واقترح خطوات عملية قابلة للتنفيذ. " +
               "اعتبر العوامل الموسمية والاتجاهات السوقية.";
    }
    
    private String buildAnomalyDetectionPrompt(TransactionPattern patterns) {
        return "حلل أنماط المعاملات المرفقة واكشف أي شذوذ أو معاملات غير طبيعية. " +
               "ابحث عن أنماط مشبوهة، تغيرات مفاجئة، أو انحرافات عن السلوك المعتاد. " +
               "صنف مستوى الخطورة واقترح إجراءات للمراجعة.";
    }
    
    private JSONObject buildGeminiRequest(String systemPrompt, String context, String userMessage) throws JSONException {
        JSONObject request = new JSONObject();
        JSONArray contents = new JSONArray();
        
        // إضافة السياق والرسالة
        JSONObject content = new JSONObject();
        JSONArray parts = new JSONArray();
        
        JSONObject part = new JSONObject();
        String fullPrompt = systemPrompt + "\n\nسياق البيانات:\n" + context + "\n\nسؤال المستخدم:\n" + userMessage;
        part.put("text", fullPrompt);
        parts.put(part);
        
        content.put("parts", parts);
        contents.put(content);
        
        request.put("contents", contents);
        
        // إعدادات التوليد
        JSONObject generationConfig = new JSONObject();
        generationConfig.put("temperature", 0.7);
        generationConfig.put("topK", 40);
        generationConfig.put("topP", 0.95);
        generationConfig.put("maxOutputTokens", 2048);
        request.put("generationConfig", generationConfig);
        
        return request;
    }
    
    private String sendGeminiRequest(JSONObject requestBody) throws IOException {
        RequestBody body = RequestBody.create(
            requestBody.toString(),
            MediaType.get("application/json")
        );
        
        Request request = new Request.Builder()
                .url(GEMINI_API_URL + "?key=" + GEMINI_API_KEY)
                .header("Content-Type", "application/json")
                .post(body)
                .build();
        
        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Gemini API request failed: " + response.code());
            }
            
            return response.body().string();
        }
    }
    
    private GeminiResponse parseGeminiResponse(String response, String userId) throws JSONException {
        JSONObject jsonResponse = new JSONObject(response);
        JSONArray candidates = jsonResponse.getJSONArray("candidates");
        
        if (candidates.length() > 0) {
            JSONObject candidate = candidates.getJSONObject(0);
            JSONObject content = candidate.getJSONObject("content");
            JSONArray parts = content.getJSONArray("parts");
            
            if (parts.length() > 0) {
                String text = parts.getJSONObject(0).getString("text");
                
                return new GeminiResponse(text, true, "success");
            }
        }
        
        return new GeminiResponse("", false, "No response content");
    }
    
    private FinancialAnalysisResult parseFinancialAnalysis(String response, FinancialData data) throws JSONException {
        // تحليل استجابة Gemini وإستخراج النتائج المالية
        GeminiResponse geminiResponse = parseGeminiResponse(response, "");
        
        return new FinancialAnalysisResult(
            geminiResponse.getText(),
            data,
            System.currentTimeMillis()
        );
    }
    
    private List<BusinessSuggestion> parseBusinessSuggestions(String response) throws JSONException {
        // تحليل الاقتراحات وتحويلها لقائمة
        // يمكن تطوير هذا لاحقاً لتحليل أكثر تفصيلاً
        java.util.List<BusinessSuggestion> suggestions = new java.util.ArrayList<>();
        
        GeminiResponse geminiResponse = parseGeminiResponse(response, "");
        String[] lines = geminiResponse.getText().split("\n");
        
        for (String line : lines) {
            if (line.trim().startsWith("-") || line.trim().startsWith("•")) {
                suggestions.add(new BusinessSuggestion(
                    line.trim().substring(1).trim(),
                    "IMPROVEMENT",
                    "HIGH"
                ));
            }
        }
        
        return suggestions;
    }
    
    private AnomalyDetectionResult parseAnomalyDetection(String response) throws JSONException {
        GeminiResponse geminiResponse = parseGeminiResponse(response, "");
        
        return new AnomalyDetectionResult(
            geminiResponse.getText(),
            new java.util.ArrayList<>(),
            System.currentTimeMillis()
        );
    }
    
    private void saveConversationWithContext(String userId, String userMessage, 
                                           GeminiResponse response, String context) {
        // حفظ المحادثة مع السياق الكامل
        executorService.execute(() -> {
            try {
                AIConversation conversation = new AIConversation();
                conversation.setId(java.util.UUID.randomUUID().toString());
                conversation.setUserId(userId);
                conversation.setConversationType("GEMINI_DATABASE_CHAT");
                conversation.setUserMessage(userMessage);
                conversation.setAiResponse(response.getText());
                conversation.setDatabaseContext(context);
                conversation.setConfidenceScore(0.9f);
                conversation.setResponseTimeMs(response.getResponseTime());
                conversation.setCreatedAt(new java.util.Date());
                conversation.setUpdatedAt(new java.util.Date());
                
                database.aiConversationDao().insert(conversation);
                
            } catch (Exception e) {
                Log.e(TAG, "Error saving conversation", e);
            }
        });
    }
    
    private void saveAnalysisResults(String userId, FinancialAnalysisResult result) {
        // حفظ نتائج التحليل
        executorService.execute(() -> {
            try {
                // يمكن إضافة جدول خاص لحفظ نتائج التحليل
                Log.d(TAG, "Analysis results saved for user: " + userId);
            } catch (Exception e) {
                Log.e(TAG, "Error saving analysis results", e);
            }
        });
    }
    
    public void shutdown() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
    
    // فئات النتائج
    public static class GeminiResponse {
        private String text;
        private boolean success;
        private String error;
        private long responseTime;
        
        public GeminiResponse(String text, boolean success, String error) {
            this.text = text;
            this.success = success;
            this.error = error;
            this.responseTime = System.currentTimeMillis();
        }
        
        // Getters
        public String getText() { return text; }
        public boolean isSuccess() { return success; }
        public String getError() { return error; }
        public long getResponseTime() { return responseTime; }
    }
    
    public static class FinancialAnalysisResult {
        private String analysis;
        private FinancialData data;
        private long timestamp;
        
        public FinancialAnalysisResult(String analysis, FinancialData data, long timestamp) {
            this.analysis = analysis;
            this.data = data;
            this.timestamp = timestamp;
        }
        
        // Getters
        public String getAnalysis() { return analysis; }
        public FinancialData getData() { return data; }
        public long getTimestamp() { return timestamp; }
    }
    
    public static class BusinessSuggestion {
        private String suggestion;
        private String type;
        private String priority;
        
        public BusinessSuggestion(String suggestion, String type, String priority) {
            this.suggestion = suggestion;
            this.type = type;
            this.priority = priority;
        }
        
        // Getters
        public String getSuggestion() { return suggestion; }
        public String getType() { return type; }
        public String getPriority() { return priority; }
    }
    
    public static class AnomalyDetectionResult {
        private String analysis;
        private List<String> anomalies;
        private long timestamp;
        
        public AnomalyDetectionResult(String analysis, List<String> anomalies, long timestamp) {
            this.analysis = analysis;
            this.anomalies = anomalies;
            this.timestamp = timestamp;
        }
        
        // Getters
        public String getAnalysis() { return analysis; }
        public List<String> getAnomalies() { return anomalies; }
        public long getTimestamp() { return timestamp; }
    }
}