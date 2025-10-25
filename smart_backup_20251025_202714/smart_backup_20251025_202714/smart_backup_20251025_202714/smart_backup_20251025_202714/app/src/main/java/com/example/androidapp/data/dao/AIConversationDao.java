package com.example.androidapp.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.example.androidapp.data.entities.AIConversation;

import java.util.Date;
import java.util.List;

/**
 * DAO لمحادثات الذكاء الاصطناعي - لإدارة العمليات على قاعدة البيانات لمحادثات AI
 */
@Dao
public interface AIConversationDao {

    @Query("SELECT * FROM ai_conversations ORDER BY created_at DESC")
    LiveData<List<AIConversation>> getAllConversations();

    @Query("SELECT * FROM ai_conversations WHERE user_id = :userId ORDER BY created_at DESC")
    LiveData<List<AIConversation>> getConversationsByUser(String userId);

    @Query("SELECT * FROM ai_conversations WHERE session_id = :sessionId ORDER BY created_at ASC")
    LiveData<List<AIConversation>> getConversationsBySession(String sessionId);

    @Query("SELECT * FROM ai_conversations WHERE conversation_type = :type ORDER BY created_at DESC")
    LiveData<List<AIConversation>> getConversationsByType(String type);

    @Query("SELECT * FROM ai_conversations WHERE user_id = :userId AND conversation_type = :type ORDER BY created_at DESC")
    LiveData<List<AIConversation>> getUserConversationsByType(String userId, String type);

    @Query("SELECT * FROM ai_conversations WHERE is_bookmarked = 1 AND user_id = :userId ORDER BY created_at DESC")
    LiveData<List<AIConversation>> getBookmarkedConversations(String userId);

    @Query("SELECT * FROM ai_conversations WHERE feedback_rating >= :minRating ORDER BY feedback_rating DESC, created_at DESC")
    LiveData<List<AIConversation>> getHighRatedConversations(int minRating);

    @Query("SELECT * FROM ai_conversations WHERE confidence_score >= :minConfidence ORDER BY confidence_score DESC, created_at DESC")
    LiveData<List<AIConversation>> getHighConfidenceConversations(float minConfidence);

    @Query("SELECT * FROM ai_conversations WHERE user_id = :userId AND (user_message LIKE '%' || :searchTerm || '%' OR ai_response LIKE '%' || :searchTerm || '%') ORDER BY created_at DESC")
    LiveData<List<AIConversation>> searchUserConversations(String userId, String searchTerm);

    @Query("SELECT * FROM ai_conversations WHERE created_at BETWEEN :startDate AND :endDate ORDER BY created_at DESC")
    LiveData<List<AIConversation>> getConversationsInDateRange(Date startDate, Date endDate);

    @Query("SELECT * FROM ai_conversations WHERE language = :language ORDER BY created_at DESC")
    LiveData<List<AIConversation>> getConversationsByLanguage(String language);

    @Query("SELECT * FROM ai_conversations WHERE id = :id")
    LiveData<AIConversation> getConversationById(String id);

    @Query("SELECT COUNT(*) FROM ai_conversations WHERE user_id = :userId")
    LiveData<Integer> getUserConversationCount(String userId);

    @Query("SELECT COUNT(*) FROM ai_conversations WHERE user_id = :userId AND conversation_type = :type")
    LiveData<Integer> getUserConversationCountByType(String userId, String type);

    @Query("SELECT COUNT(*) FROM ai_conversations WHERE is_bookmarked = 1 AND user_id = :userId")
    LiveData<Integer> getBookmarkedCount(String userId);

    @Query("SELECT AVG(feedback_rating) FROM ai_conversations WHERE feedback_rating > 0 AND user_id = :userId")
    LiveData<Float> getAverageUserRating(String userId);

    @Query("SELECT AVG(confidence_score) FROM ai_conversations WHERE confidence_score > 0 AND user_id = :userId")
    LiveData<Float> getAverageConfidenceScore(String userId);

    @Query("SELECT AVG(response_time_ms) FROM ai_conversations WHERE response_time_ms > 0")
    LiveData<Long> getAverageResponseTime();

    @Query("UPDATE ai_conversations SET is_bookmarked = :bookmarked, updated_at = :updatedAt WHERE id = :id")
    void updateBookmarkStatus(String id, boolean bookmarked, Date updatedAt);

    @Query("UPDATE ai_conversations SET feedback_rating = :rating, feedback_comment = :comment, updated_at = :updatedAt WHERE id = :id")
    void updateFeedback(String id, int rating, String comment, Date updatedAt);

    @Query("UPDATE ai_conversations SET title = :title, updated_at = :updatedAt WHERE id = :id")
    void updateTitle(String id, String title, Date updatedAt);

    @Query("DELETE FROM ai_conversations WHERE user_id = :userId")
    void deleteUserConversations(String userId);

    @Query("DELETE FROM ai_conversations WHERE created_at < :cutoffDate AND is_bookmarked = 0")
    void deleteOldUnbookmarkedConversations(Date cutoffDate);

    @Query("DELETE FROM ai_conversations WHERE session_id = :sessionId")
    void deleteConversationsBySession(String sessionId);

    @Insert
    void insert(AIConversation conversation);

    @Insert
    void insertAll(List<AIConversation> conversations);

    @Update
    void update(AIConversation conversation);

    @Delete
    void delete(AIConversation conversation);

    @Query("DELETE FROM ai_conversations WHERE id = :id")
    void deleteById(String id);

    // Advanced analytics queries
    @Query("SELECT conversation_type, COUNT(*) as count FROM ai_conversations WHERE user_id = :userId GROUP BY conversation_type ORDER BY count DESC")
    LiveData<List<ConversationTypeCount>> getConversationCountsByType(String userId);

    @Query("SELECT language, COUNT(*) as count FROM ai_conversations GROUP BY language ORDER BY count DESC")
    LiveData<List<ConversationLanguageCount>> getConversationCountsByLanguage();

    @Query("SELECT DATE(created_at) as date, COUNT(*) as count FROM ai_conversations WHERE user_id = :userId AND created_at >= :startDate GROUP BY DATE(created_at) ORDER BY date DESC")
    LiveData<List<ConversationDailyCount>> getDailyConversationCounts(String userId, Date startDate);

    @Query("SELECT feedback_rating as rating, COUNT(*) as count FROM ai_conversations WHERE feedback_rating > 0 GROUP BY feedback_rating ORDER BY rating DESC")
    LiveData<List<ConversationRatingCount>> getRatingDistribution();

    @Query("SELECT session_id, COUNT(*) as conversation_count, MIN(created_at) as session_start, MAX(created_at) as session_end FROM ai_conversations WHERE user_id = :userId GROUP BY session_id ORDER BY session_start DESC")
    LiveData<List<ConversationSessionSummary>> getUserSessionSummaries(String userId);

    // Helper classes for query results
    class ConversationTypeCount {
        public String conversation_type;
        public int count;
    }

    class ConversationLanguageCount {
        public String language;
        public int count;
    }

    class ConversationDailyCount {
        public String date;
        public int count;
    }

    class ConversationRatingCount {
        public int rating;
        public int count;
    }

    class ConversationSessionSummary {
        public String session_id;
        public int conversation_count;
        public Date session_start;
        public Date session_end;
    }
}
