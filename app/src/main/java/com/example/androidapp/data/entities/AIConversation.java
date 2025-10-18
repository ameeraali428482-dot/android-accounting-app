package com.example.androidapp.data.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;
import com.example.androidapp.data.DateConverter;

import java.util.Date;

/**
 * كيان محادثات الذكاء الاصطناعي - لحفظ محادثات AI واقتراحات ذكية
 */
@Entity(tableName = "ai_conversations")
@TypeConverters({DateConverter.class})
public class AIConversation {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    private String id;

    @ColumnInfo(name = "user_id")
    private String userId;

    @ColumnInfo(name = "conversation_type")
    private String conversationType; // ACCOUNTING_ANALYSIS, GENERAL_CHAT, DATA_INSIGHTS, FINANCIAL_ADVICE

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "user_message")
    private String userMessage;

    @ColumnInfo(name = "ai_response")
    private String aiResponse;

    @ColumnInfo(name = "context_data")
    private String contextData; // JSON string with relevant business data

    @ColumnInfo(name = "analysis_results")
    private String analysisResults; // JSON string with AI analysis results

    @ColumnInfo(name = "suggestions")
    private String suggestions; // JSON array of AI suggestions

    @ColumnInfo(name = "confidence_score")
    private float confidenceScore;

    @ColumnInfo(name = "is_bookmarked")
    private boolean isBookmarked;

    @ColumnInfo(name = "feedback_rating")
    private int feedbackRating; // 1-5 stars

    @ColumnInfo(name = "feedback_comment")
    private String feedbackComment;

    @ColumnInfo(name = "session_id")
    private String sessionId; // To group related conversations

    @ColumnInfo(name = "language")
    private String language; // ar, en

    @ColumnInfo(name = "response_time_ms")
    private long responseTimeMs;

    @ColumnInfo(name = "data_sources")
    private String dataSources; // JSON array of data sources used for analysis

    @ColumnInfo(name = "created_at")
    private Date createdAt;

    @ColumnInfo(name = "updated_at")
    private Date updatedAt;

    // Constructors
    public AIConversation() {
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    public AIConversation(@NonNull String id, String userId, String conversationType, 
                         String userMessage, String aiResponse) {
        this.id = id;
        this.userId = userId;
        this.conversationType = conversationType;
        this.userMessage = userMessage;
        this.aiResponse = aiResponse;
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    // Getters and Setters
    @NonNull
    public String getId() { return id; }
    public void setId(@NonNull String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getConversationType() { return conversationType; }
    public void setConversationType(String conversationType) { this.conversationType = conversationType; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getUserMessage() { return userMessage; }
    public void setUserMessage(String userMessage) { this.userMessage = userMessage; }

    public String getAiResponse() { return aiResponse; }
    public void setAiResponse(String aiResponse) { this.aiResponse = aiResponse; }

    public String getContextData() { return contextData; }
    public void setContextData(String contextData) { this.contextData = contextData; }

    public String getAnalysisResults() { return analysisResults; }
    public void setAnalysisResults(String analysisResults) { this.analysisResults = analysisResults; }

    public String getSuggestions() { return suggestions; }
    public void setSuggestions(String suggestions) { this.suggestions = suggestions; }

    public float getConfidenceScore() { return confidenceScore; }
    public void setConfidenceScore(float confidenceScore) { this.confidenceScore = confidenceScore; }

    public boolean isBookmarked() { return isBookmarked; }
    public void setBookmarked(boolean bookmarked) { isBookmarked = bookmarked; }

    public int getFeedbackRating() { return feedbackRating; }
    public void setFeedbackRating(int feedbackRating) { this.feedbackRating = feedbackRating; }

    public String getFeedbackComment() { return feedbackComment; }
    public void setFeedbackComment(String feedbackComment) { this.feedbackComment = feedbackComment; }

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }

    public long getResponseTimeMs() { return responseTimeMs; }
    public void setResponseTimeMs(long responseTimeMs) { this.responseTimeMs = responseTimeMs; }

    public String getDataSources() { return dataSources; }
    public void setDataSources(String dataSources) { this.dataSources = dataSources; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
}
