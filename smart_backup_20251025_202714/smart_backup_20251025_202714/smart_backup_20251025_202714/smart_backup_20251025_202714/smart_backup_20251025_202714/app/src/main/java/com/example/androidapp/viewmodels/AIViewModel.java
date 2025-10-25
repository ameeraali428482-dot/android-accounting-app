package com.example.androidapp.viewmodels;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.dao.AIConversationDao;
import com.example.androidapp.data.entities.AIConversation;
import com.example.androidapp.services.AIIntegrationService;
import com.example.androidapp.utils.AIHelper;
import com.example.androidapp.utils.SessionManager;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * نموذج العرض للذكاء الاصطناعي - يدير المحادثات والتحليلات الذكية
 * AI ViewModel - manages conversations and intelligent analysis
 */
public class AIViewModel extends AndroidViewModel {

    private AIConversationDao aiConversationDao;
    private AIHelper aiHelper;
    private SessionManager sessionManager;
    private ExecutorService executorService;
    
    // LiveData for UI updates
    private MutableLiveData<List<AIConversation>> conversations = new MutableLiveData<>();
    private MutableLiveData<AIConversation> currentConversation = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private MutableLiveData<List<String>> suggestions = new MutableLiveData<>();
    private MutableLiveData<String> typingIndicator = new MutableLiveData<>();
    
    // Session management
    private String currentSessionId;
    private String currentUserId;
    private String currentConversationType = "GENERAL_CHAT";

    public AIViewModel(@NonNull Application application) {
        super(application);
        
        AppDatabase database = AppDatabase.getDatabase(application);
        aiConversationDao = database.aiConversationDao();
        aiHelper = new AIHelper(application);
        sessionManager = new SessionManager(application);
        executorService = Executors.newCachedThreadPool();
        
        // Initialize session
        initializeSession();
    }

    private void initializeSession() {
        currentUserId = sessionManager.getUserId();
        currentSessionId = UUID.randomUUID().toString();
    }

    /**
     * إرسال رسالة إلى الذكاء الاصطناعي
     * Send message to AI
     */
    public void sendMessage(String message) {
        if (message == null || message.trim().isEmpty()) {
            errorMessage.setValue("الرجاء إدخال رسالة");
            return;
        }

        isLoading.setValue(true);
        typingIndicator.setValue("الذكاء الاصطناعي يكتب...");

        executorService.execute(() -> {
            try {
                // استخدام AIIntegrationService لمعالجة المحادثة
                AIIntegrationService.AIResponseCallback callback = new AIIntegrationService.AIResponseCallback() {
                    @Override
                    public void onSuccess(AIConversation conversation) {
                        // تحديث UI في الخيط الرئيسي
                        currentConversation.postValue(conversation);
                        isLoading.postValue(false);
                        typingIndicator.postValue(null);
                        
                        // تحديث قائمة المحادثات
                        loadConversationsForCurrentSession();
                        
                        // الحصول على اقتراحات ذكية
                        loadSmartSuggestions(conversation.getAiResponse());
                    }

                    @Override
                    public void onError(String error) {
                        errorMessage.postValue(error);
                        isLoading.postValue(false);
                        typingIndicator.postValue(null);
                    }
                };

                // إنشاء خدمة AI ومعالجة المحادثة
                AIIntegrationService aiService = new AIIntegrationService();
                aiService.processAIConversation(
                    currentUserId,
                    currentConversationType,
                    message,
                    currentSessionId,
                    callback
                );

            } catch (Exception e) {
                errorMessage.postValue("حدث خطأ في إرسال الرسالة: " + e.getMessage());
                isLoading.postValue(false);
                typingIndicator.postValue(null);
            }
        });
    }

    /**
     * تغيير نوع المحادثة
     * Change conversation type
     */
    public void setConversationType(String conversationType) {
        this.currentConversationType = conversationType;
        // إنشاء جلسة جديدة لنوع المحادثة الجديد
        currentSessionId = UUID.randomUUID().toString();
        loadConversationsForCurrentSession();
    }

    /**
     * طلب تحليل متقدم (للمدراء فقط)
     * Request advanced analysis (for admins only)
     */
    public void requestAnalysis(String query, String analysisType) {
        if (query == null || query.trim().isEmpty()) {
            errorMessage.setValue("الرجاء إدخال استفسار التحليل");
            return;
        }

        isLoading.setValue(true);
        typingIndicator.setValue("يتم إعداد التحليل...");

        executorService.execute(() -> {
            try {
                aiHelper.sendAnalysisRequest(currentUserId, query, analysisType)
                    .thenAccept(response -> {
                        if (response.isSuccess()) {
                            // إنشاء كائن محادثة للتحليل
                            AIConversation analysisConversation = new AIConversation();
                            analysisConversation.setId(UUID.randomUUID().toString());
                            analysisConversation.setUserId(currentUserId);
                            analysisConversation.setConversationType(analysisType);
                            analysisConversation.setSessionId(currentSessionId);
                            analysisConversation.setUserMessage(query);
                            analysisConversation.setAiResponse(response.getMessage());
                            analysisConversation.setTitle("تحليل " + analysisType);

                            currentConversation.postValue(analysisConversation);
                            loadConversationsForCurrentSession();
                        } else {
                            errorMessage.postValue(response.getMessage());
                        }
                        
                        isLoading.postValue(false);
                        typingIndicator.postValue(null);
                    })
                    .exceptionally(throwable -> {
                        errorMessage.postValue("حدث خطأ في التحليل: " + throwable.getMessage());
                        isLoading.postValue(false);
                        typingIndicator.postValue(null);
                        return null;
                    });

            } catch (Exception e) {
                errorMessage.postValue("حدث خطأ في طلب التحليل: " + e.getMessage());
                isLoading.postValue(false);
                typingIndicator.postValue(null);
            }
        });
    }

    /**
     * تحميل الاقتراحات الذكية
     * Load smart suggestions
     */
    private void loadSmartSuggestions(String context) {
        executorService.execute(() -> {
            try {
                aiHelper.getSmartSuggestions(currentUserId, context)
                    .thenAccept(suggestionsList -> {
                        suggestions.postValue(suggestionsList);
                    })
                    .exceptionally(throwable -> {
                        // تجاهل أخطاء الاقتراحات
                        return null;
                    });
            } catch (Exception e) {
                // تجاهل أخطاء الاقتراحات
            }
        });
    }

    /**
     * تحميل محادثات الجلسة الحالية
     * Load conversations for current session
     */
    public void loadConversationsForCurrentSession() {
        if (currentSessionId != null) {
            LiveData<List<AIConversation>> sessionConversations = 
                aiConversationDao.getConversationsBySession(currentSessionId);
            conversations.setValue(sessionConversations.getValue());
        }
    }

    /**
     * تحميل جميع محادثات المستخدم
     * Load all user conversations
     */
    public void loadUserConversations() {
        if (currentUserId != null) {
            LiveData<List<AIConversation>> userConversations = 
                aiConversationDao.getConversationsByUser(currentUserId);
            conversations.setValue(userConversations.getValue());
        }
    }

    /**
     * إضافة تقييم للمحادثة
     * Add rating to conversation
     */
    public void rateConversation(String conversationId, int rating, String comment) {
        executorService.execute(() -> {
            try {
                // تحديث التقييم في قاعدة البيانات
                AIConversation conversation = new AIConversation();
                conversation.setId(conversationId);
                conversation.setFeedbackRating(rating);
                conversation.setFeedbackComment(comment);
                
                aiConversationDao.update(conversation);
                
                // إعادة تحميل المحادثات
                loadConversationsForCurrentSession();
                
            } catch (Exception e) {
                errorMessage.postValue("حدث خطأ في حفظ التقييم: " + e.getMessage());
            }
        });
    }

    /**
     * حذف محادثة
     * Delete conversation
     */
    public void deleteConversation(String conversationId) {
        executorService.execute(() -> {
            try {
                AIConversation conversation = new AIConversation();
                conversation.setId(conversationId);
                aiConversationDao.delete(conversation);
                
                // إعادة تحميل المحادثات
                loadConversationsForCurrentSession();
                
            } catch (Exception e) {
                errorMessage.postValue("حدث خطأ في حذف المحادثة: " + e.getMessage());
            }
        });
    }

    /**
     * بدء جلسة محادثة جديدة
     * Start new conversation session
     */
    public void startNewSession() {
        currentSessionId = UUID.randomUUID().toString();
        conversations.setValue(null);
        currentConversation.setValue(null);
        suggestions.setValue(null);
        errorMessage.setValue(null);
    }

    /**
     * البحث في المحادثات
     * Search conversations
     */
    public void searchConversations(String searchTerm) {
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            LiveData<List<AIConversation>> searchResults = 
                aiConversationDao.searchUserConversations(currentUserId, searchTerm);
            conversations.setValue(searchResults.getValue());
        } else {
            loadUserConversations();
        }
    }

    // Getters for LiveData
    public LiveData<List<AIConversation>> getConversations() { return conversations; }
    public LiveData<AIConversation> getCurrentConversation() { return currentConversation; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<String> getErrorMessage() { return errorMessage; }
    public LiveData<List<String>> getSuggestions() { return suggestions; }
    public LiveData<String> getTypingIndicator() { return typingIndicator; }

    // Getters for session info
    public String getCurrentSessionId() { return currentSessionId; }
    public String getCurrentUserId() { return currentUserId; }
    public String getCurrentConversationType() { return currentConversationType; }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}