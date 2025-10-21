package com.example.androidapp.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import androidx.room.Room;
import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.entities.*;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * مساعد الذكاء الاصطناعي المتقدم - يوفر تحليلات ذكية ومحادثة تفاعلية للبيانات المحاسبية
 * Advanced AI Helper - provides intelligent analytics and interactive chat for accounting data
 */
public class AIHelper {
    
    private static final String TAG = "AIHelper";
    private static final String PREFS_NAME = "ai_helper_prefs";
    private static final String AI_API_KEY = "ai_api_key";
    private static final String AI_API_URL = "ai_api_url";
    private static final String CHAT_HISTORY_KEY = "chat_history";
    private static final String AI_ENABLED_KEY = "ai_enabled";
    
    // إعدادات الذكاء الاصطناعي
    private static final int MAX_TOKENS = 2048;
    private static final double TEMPERATURE = 0.7;
    private static final int MAX_CHAT_HISTORY = 50;
    private static final int CONNECTION_TIMEOUT = 30000;
    private static final int READ_TIMEOUT = 60000;
    
    // أنواع التحليلات
    public static final String ANALYSIS_TYPE_FINANCIAL = "FINANCIAL";
    public static final String ANALYSIS_TYPE_TRENDS = "TRENDS";
    public static final String ANALYSIS_TYPE_PREDICTIONS = "PREDICTIONS";
    public static final String ANALYSIS_TYPE_RECOMMENDATIONS = "RECOMMENDATIONS";
    public static final String ANALYSIS_TYPE_ANOMALIES = "ANOMALIES";
    public static final String ANALYSIS_TYPE_PERFORMANCE = "PERFORMANCE";
    public static final String ANALYSIS_TYPE_CASH_FLOW = "CASH_FLOW";
    public static final String ANALYSIS_TYPE_PROFITABILITY = "PROFITABILITY";
    
    // أدوار المحادثة
    public static final String ROLE_ADMIN_ANALYST = "ADMIN_ANALYST";
    public static final String ROLE_USER_ASSISTANT = "USER_ASSISTANT";
    
    private Context context;
    private SharedPreferences prefs;
    private AppDatabase database;
    private ExecutorService executorService;
    private Gson gson;
    private RoleManager roleManager;
    
    // تخزين محفوظات المحادثة
    private Map<String, List<ChatMessage>> chatHistory = new HashMap<>();
    
    public AIHelper(Context context) {
        this.context = context;
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        this.database = Room.databaseBuilder(context, AppDatabase.class, "app_database")
                .fallbackToDestructiveMigration()
                .build();
        this.executorService = Executors.newCachedThreadPool();
        this.gson = new Gson();
        this.roleManager = new RoleManager(context);
        
        loadChatHistory();
    }
    
    /**
     * إرسال رسالة للذكاء الاصطناعي للمحادثة العامة
     * Send message to AI for general chat
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
                String aiResponse = sendToAI(prompt, ROLE_USER_ASSISTANT);
                
                // حفظ المحادثة
                saveChatMessage(userId, message, aiResponse, ROLE_USER_ASSISTANT);
                
                return new AIResponse(true, aiResponse);
                
            } catch (Exception e) {
                Log.e(TAG, "Error in chat message", e);
                return new AIResponse(false, "حدث خطأ أثناء المحادثة: " + e.getMessage());
            }
        });
    }
    
    /**
     * إرسال طلب تحليل للمدراء (الذكاء الاصطناعي المتقدم)
     * Send analysis request for admins (advanced AI)
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
                String aiResponse = sendToAI(prompt, ROLE_ADMIN_ANALYST);
                
                // حفظ التحليل
                saveAnalysisResult(userId, query, analysisType, aiResponse);
                
                // حفظ المحادثة
                saveChatMessage(userId, query, aiResponse, ROLE_ADMIN_ANALYST);
                
                return new AIResponse(true, aiResponse);
                
            } catch (Exception e) {
                Log.e(TAG, "Error in analysis request", e);
                return new AIResponse(false, "حدث خطأ أثناء التحليل: " + e.getMessage());
            }
        });
    }
    
    /**
     * الحصول على اقتراحات ذكية للمستخدم
     * Get smart suggestions for user
     */
    public Future<List<String>> getSmartSuggestions(String userId, String context) {
        return executorService.submit(() -> {
            try {
                List<String> suggestions = new ArrayList<>();
                
                if (!isAIEnabled() || !hasAIPermission(userId)) {
                    return suggestions;
                }
                
                // تحليل السياق وإنشاء اقتراحات
                String prompt = buildSuggestionsPrompt(userId, context);
                String aiResponse = sendToAI(prompt, "SUGGESTIONS");
                
                // استخراج الاقتراحات من الاستجابة
                suggestions = parseSuggestions(aiResponse);
                
                return suggestions;
                
            } catch (Exception e) {
                Log.e(TAG, "Error getting suggestions", e);
                return new ArrayList<>();
            }
        });
    }
    
    /**
     * تحليل البيانات المالية تلقائياً
     * Automatic financial data analysis
     */
    public Future<AIResponse> performAutomaticAnalysis(String userId) {
        return executorService.submit(() -> {
            try {
                if (!hasAdminAIPermission(userId)) {
                    return new AIResponse(false, "لا تملك صلاحية للتحليل التلقائي");
                }
                
                // جمع البيانات للتحليل الشامل
                ComprehensiveAnalysisData data = gatherComprehensiveData();
                
                // إنشاء تحليل شامل
                String prompt = buildComprehensiveAnalysisPrompt(data);
                String aiResponse = sendToAI(prompt, ROLE_ADMIN_ANALYST);
                
                // حفظ التحليل التلقائي
                saveAutomaticAnalysis(userId, aiResponse);
                
                return new AIResponse(true, aiResponse);
                
            } catch (Exception e) {
                Log.e(TAG, "Error in automatic analysis", e);
                return new AIResponse(false, "حدث خطأ في التحليل التلقائي: " + e.getMessage());
            }
        });
    }
    
    /**
     * إرسال طلب للذكاء الاصطناعي
     * Send request to AI
     */
    private String sendToAI(String prompt, String role) throws Exception {
        String apiKey = getAIApiKey();
        String apiUrl = getAIApiUrl();
        
        if (apiKey.isEmpty() || apiUrl.isEmpty()) {
            throw new Exception("إعدادات الذكاء الاصطناعي غير مكتملة");
        }
        
        // إنشاء payload الطلب
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("model", "gpt-3.5-turbo");
        requestBody.addProperty("max_tokens", MAX_TOKENS);
        requestBody.addProperty("temperature", TEMPERATURE);
        
        // إضافة الرسائل
        JsonObject systemMessage = new JsonObject();
        systemMessage.addProperty("role", "system");
        systemMessage.addProperty("content", getSystemPrompt(role));
        
        JsonObject userMessage = new JsonObject();
        userMessage.addProperty("role", "user");
        userMessage.addProperty("content", prompt);
        
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
     * Extract text from AI response
     */
    private String extractResponseText(String jsonResponse) {
        try {
            JsonObject response = JsonParser.parseString(jsonResponse).getAsJsonObject();
            return response.getAsJsonArray("choices")
                    .get(0).getAsJsonObject()
                    .getAsJsonObject("message")
                    .get("content").getAsString();
        } catch (Exception e) {
            Log.e(TAG, "Error parsing AI response", e);
            return "حدث خطأ في معالجة استجابة الذكاء الاصطناعي";
        }
    }
    
    /**
     * بناء نص المحادثة
     * Build chat context
     */
    private String buildChatContext(String userId, String userRole) {
        StringBuilder context = new StringBuilder();
        
        context.append("معلومات المستخدم:\n");
        context.append("- معرف المستخدم: ").append(userId).append("\n");
        context.append("- الدور: ").append(userRole).append("\n");
        
        // إضافة إحصائيات سريعة
        try {
            QuickStats stats = getQuickStats();
            context.append("\nإحصائيات سريعة:\n");
            context.append("- عدد الحسابات: ").append(stats.accountsCount).append("\n");
            context.append("- عدد المعاملات اليوم: ").append(stats.todayTransactions).append("\n");
            context.append("- إجمالي الرصيد: ").append(stats.totalBalance).append("\n");
        } catch (Exception e) {
            Log.e(TAG, "Error getting quick stats", e);
        }
        
        return context.toString();
    }
    
    /**
     * بناء نص المحادثة
     * Build chat prompt
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
     * Build analysis prompt
     */
    private String buildAnalysisPrompt(String query, String analysisType, AnalysisData data) {
        StringBuilder prompt = new StringBuilder();
        
        prompt.append("نوع التحليل المطلوب: ").append(analysisType).append("\n\n");
        prompt.append("الاستفسار: ").append(query).append("\n\n");
        prompt.append("البيانات المالية:\n");
        prompt.append(data.toFormattedString()).append("\n\n");
        prompt.append("يرجى تقديم تحليل مفصل ودقيق مع التوصيات والاقتراحات باللغة العربية.");
        
        return prompt.toString();
    }
    
    /**
     * جمع بيانات التحليل
     * Gather analysis data
     */
    private AnalysisData gatherAnalysisData(String analysisType) {
        AnalysisData data = new AnalysisData();
        
        try {
            // جمع البيانات حسب نوع التحليل
            switch (analysisType) {
                case ANALYSIS_TYPE_FINANCIAL:
                    data.accounts = database.accountDao().getAllAccountsSync();
                    data.transactions = database.transactionDao().getRecentTransactionsSync(100);
                    break;
                    
                case ANALYSIS_TYPE_TRENDS:
                    data.transactions = database.transactionDao().getTransactionsByDateRangeSync(
                            System.currentTimeMillis() - (30L * 24 * 60 * 60 * 1000), // 30 days ago
                            System.currentTimeMillis()
                    );
                    break;
                    
                case ANALYSIS_TYPE_CASH_FLOW:
                    data.transactions = database.transactionDao().getAllTransactionsSync();
                    data.accounts = database.accountDao().getAllAccountsSync();
                    break;
                    
                default:
                    data.accounts = database.accountDao().getAllAccountsSync();
                    data.transactions = database.transactionDao().getRecentTransactionsSync(50);
                    break;
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error gathering analysis data", e);
        }
        
        return data;
    }
    
    /**
     * جمع البيانات الشاملة
     * Gather comprehensive data
     */
    private ComprehensiveAnalysisData gatherComprehensiveData() {
        ComprehensiveAnalysisData data = new ComprehensiveAnalysisData();
        
        try {
            data.accounts = database.accountDao().getAllAccountsSync();
            data.transactions = database.transactionDao().getAllTransactionsSync();
            data.categories = database.categoryDao().getAllCategoriesSync();
            
            // حساب الإحصائيات
            data.calculateStatistics();
            
        } catch (Exception e) {
            Log.e(TAG, "Error gathering comprehensive data", e);
        }
        
        return data;
    }
    
    /**
     * الحصول على النص النظامي للذكاء الاصطناعي
     * Get system prompt for AI
     */
    private String getSystemPrompt(String role) {
        switch (role) {
            case ROLE_ADMIN_ANALYST:
                return "أنت محلل مالي خبير متخصص في المحاسبة والتحليل المالي. " +
                       "مهمتك تحليل البيانات المالية وتقديم رؤى عميقة وتوصيات دقيقة. " +
                       "استخدم خبرتك في المحاسبة لتقديم تحليلات متقدمة ومفيدة باللغة العربية.";
                
            case ROLE_USER_ASSISTANT:
                return "أنت مساعد ذكي متخصص في أنظمة المحاسبة. " +
                       "مهمتك مساعدة المستخدمين في فهم واستخدام النظام المحاسبي. " +
                       "قدم إجابات واضحة ومفيدة باللغة العربية واستخدم أمثلة عملية.";
                
            default:
                return "أنت مساعد ذكي متخصص في المحاسبة. قدم المساعدة باللغة العربية.";
        }
    }
    
    /**
     * حفظ رسالة المحادثة
     * Save chat message
     */
    private void saveChatMessage(String userId, String userMessage, String aiResponse, String role) {
        try {
            List<ChatMessage> history = chatHistory.getOrDefault(userId, new ArrayList<>());
            
            // إضافة رسالة المستخدم
            history.add(new ChatMessage("user", userMessage, System.currentTimeMillis()));
            
            // إضافة رد الذكاء الاصطناعي
            history.add(new ChatMessage("assistant", aiResponse, System.currentTimeMillis()));
            
            // تحديد الحد الأقصى لحفوظات المحادثة
            if (history.size() > MAX_CHAT_HISTORY) {
                history = history.subList(history.size() - MAX_CHAT_HISTORY, history.size());
            }
            
            chatHistory.put(userId, history);
            saveChatHistory();
            
        } catch (Exception e) {
            Log.e(TAG, "Error saving chat message", e);
        }
    }
    
    /**
     * حفظ نتيجة التحليل
     * Save analysis result
     */
    private void saveAnalysisResult(String userId, String query, String analysisType, String result) {
        executorService.execute(() -> {
            try {
                // يمكن حفظ نتائج التحليل في جدول منفصل
                AuditLog auditLog = new AuditLog();
                auditLog.userId = userId;
                auditLog.action = "AI_ANALYSIS";
                auditLog.details = "Type: " + analysisType + ", Query: " + query;
                auditLog.timestamp = System.currentTimeMillis();
                
                database.auditLogDao().insert(auditLog);
            } catch (Exception e) {
                Log.e(TAG, "Error saving analysis result", e);
            }
        });
    }
    
    /**
     * حفظ التحليل التلقائي
     * Save automatic analysis
     */
    private void saveAutomaticAnalysis(String userId, String result) {
        executorService.execute(() -> {
            try {
                AuditLog auditLog = new AuditLog();
                auditLog.userId = userId;
                auditLog.action = "AI_AUTO_ANALYSIS";
                auditLog.details = "Automatic analysis performed";
                auditLog.timestamp = System.currentTimeMillis();
                
                database.auditLogDao().insert(auditLog);
            } catch (Exception e) {
                Log.e(TAG, "Error saving automatic analysis", e);
            }
        });
    }
    
    /**
     * حفظ محفوظات المحادثة
     * Save chat history
     */
    private void saveChatHistory() {
        try {
            String json = gson.toJson(chatHistory);
            prefs.edit().putString(CHAT_HISTORY_KEY, json).apply();
        } catch (Exception e) {
            Log.e(TAG, "Error saving chat history", e);
        }
    }
    
    /**
     * تحميل محفوظات المحادثة
     * Load chat history
     */
    private void loadChatHistory() {
        try {
            String json = prefs.getString(CHAT_HISTORY_KEY, "{}");
            // تحويل JSON إلى Map
            // يتطلب تنفيذ مخصص للتحويل
        } catch (Exception e) {
            Log.e(TAG, "Error loading chat history", e);
            chatHistory = new HashMap<>();
        }
    }
    
    /**
     * الحصول على إحصائيات سريعة
     * Get quick statistics
     */
    private QuickStats getQuickStats() {
        QuickStats stats = new QuickStats();
        
        try {
            stats.accountsCount = database.accountDao().getAccountsCountSync();
            
            long todayStart = System.currentTimeMillis() - (System.currentTimeMillis() % (24 * 60 * 60 * 1000));
            stats.todayTransactions = database.transactionDao().getTransactionsCountByDateSync(todayStart, System.currentTimeMillis());
            
            List<Account> accounts = database.accountDao().getAllAccountsSync();
            stats.totalBalance = accounts.stream().mapToDouble(a -> a.balance).sum();
            
        } catch (Exception e) {
            Log.e(TAG, "Error getting quick stats", e);
        }
        
        return stats;
    }
    
    /**
     * استخراج الاقتراحات من الاستجابة
     * Parse suggestions from response
     */
    private List<String> parseSuggestions(String response) {
        List<String> suggestions = new ArrayList<>();
        
        try {
            // تحليل النص واستخراج الاقتراحات
            String[] lines = response.split("\n");
            for (String line : lines) {
                line = line.trim();
                if (line.startsWith("-") || line.startsWith("•") || line.matches("^\\d+\\..*")) {
                    suggestions.add(line.replaceFirst("^[-•\\d+\\.\\s]+", ""));
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error parsing suggestions", e);
        }
        
        return suggestions;
    }
    
    // Helper methods
    
    private boolean isAIEnabled() {
        return prefs.getBoolean(AI_ENABLED_KEY, false);
    }
    
    private boolean hasAIPermission(String userId) {
        return roleManager.hasPermission(userId, RoleManager.PERMISSION_ACCESS_AI_CHAT);
    }
    
    private boolean hasAdminAIPermission(String userId) {
        return roleManager.isAdmin(userId) && hasAIPermission(userId);
    }
    
    private String getAIApiKey() {
        return prefs.getString(AI_API_KEY, "");
    }
    
    private String getAIApiUrl() {
        return prefs.getString(AI_API_URL, "https://api.openai.com/v1/chat/completions");
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
     * الحصول على محفوظات المحادثة للمستخدم
     * Get chat history for user
     */
    public List<ChatMessage> getChatHistory(String userId) {
        return new ArrayList<>(chatHistory.getOrDefault(userId, new ArrayList<>()));
    }
    
    /**
     * مسح محفوظات المحادثة للمستخدم
     * Clear chat history for user
     */
    public void clearChatHistory(String userId) {
        chatHistory.remove(userId);
        saveChatHistory();
    }
    
    /**
     * تنظيف الموارد
     * Cleanup resources
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
    
    public static class ChatMessage {
        public String role;
        public String content;
        public long timestamp;
        
        public ChatMessage(String role, String content, long timestamp) {
            this.role = role;
            this.content = content;
            this.timestamp = timestamp;
        }
    }
    
    public static class AnalysisData {
        public List<Account> accounts = new ArrayList<>();
        public List<Transaction> transactions = new ArrayList<>();
        
        public String toFormattedString() {
            StringBuilder sb = new StringBuilder();
            
            sb.append("الحسابات (").append(accounts.size()).append("):\n");
            for (Account account : accounts) {
                sb.append("- ").append(account.name).append(": ").append(account.balance).append("\n");
            }
            
            sb.append("\nالمعاملات (").append(transactions.size()).append("):\n");
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            for (Transaction transaction : transactions) {
                sb.append("- ").append(sdf.format(new Date(transaction.date)))
                  .append(": ").append(transaction.amount).append(" (")
                  .append(transaction.description).append(")\n");
            }
            
            return sb.toString();
        }
    }
    
    public static class ComprehensiveAnalysisData extends AnalysisData {
        public List<Category> categories = new ArrayList<>();
        public double totalIncome = 0;
        public double totalExpenses = 0;
        public double netProfit = 0;
        
        public void calculateStatistics() {
            for (Transaction transaction : transactions) {
                if (transaction.amount > 0) {
                    totalIncome += transaction.amount;
                } else {
                    totalExpenses += Math.abs(transaction.amount);
                }
            }
            netProfit = totalIncome - totalExpenses;
        }
    }
    
    public static class QuickStats {
        public int accountsCount = 0;
        public int todayTransactions = 0;
        public double totalBalance = 0;
    }
    
    private String buildSuggestionsPrompt(String userId, String context) {
        return "بناء على السياق التالي، قدم 5 اقتراحات مفيدة للمستخدم:\n" +
               "السياق: " + context + "\n\n" +
               "قدم الاقتراحات كقائمة نقطية باللغة العربية.";
    }
    
    private String buildComprehensiveAnalysisPrompt(ComprehensiveAnalysisData data) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("قم بإجراء تحليل شامل للبيانات المالية التالية:\n\n");
        prompt.append("إجمالي الإيرادات: ").append(data.totalIncome).append("\n");
        prompt.append("إجمالي المصروفات: ").append(data.totalExpenses).append("\n");
        prompt.append("صافي الربح: ").append(data.netProfit).append("\n\n");
        prompt.append(data.toFormattedString());
        prompt.append("\nيرجى تقديم تحليل مفصل يشمل:\n");
        prompt.append("1. تقييم الأداء المالي\n");
        prompt.append("2. نقاط القوة والضعف\n");
        prompt.append("3. التوصيات للتحسين\n");
        prompt.append("4. التنبؤات والتوقعات\n");
        
        return prompt.toString();
    }
}