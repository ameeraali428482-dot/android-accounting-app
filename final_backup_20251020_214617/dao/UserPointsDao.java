package com.example.androidapp.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.example.androidapp.data.entities.UserPoints;

import java.util.Date;
import java.util.List;

/**
 * DAO لنقاط المستخدم - لإدارة العمليات على قاعدة البيانات لنقاط المستخدم
 */
@Dao
public interface UserPointsDao {

    @Query("SELECT * FROM user_points ORDER BY created_at DESC")
    LiveData<List<UserPoints>> getAllPointTransactions();

    @Query("SELECT * FROM user_points WHERE user_id = :userId ORDER BY created_at DESC")
    LiveData<List<UserPoints>> getPointTransactionsByUser(String userId);

    @Query("SELECT * FROM user_points WHERE transaction_type = :transactionType ORDER BY created_at DESC")
    LiveData<List<UserPoints>> getPointTransactionsByType(String transactionType);

    @Query("SELECT * FROM user_points WHERE user_id = :userId AND transaction_type = :transactionType ORDER BY created_at DESC")
    LiveData<List<UserPoints>> getUserPointTransactionsByType(String userId, String transactionType);

    @Query("SELECT * FROM user_points WHERE source_type = :sourceType ORDER BY created_at DESC")
    LiveData<List<UserPoints>> getPointTransactionsBySourceType(String sourceType);

    @Query("SELECT * FROM user_points WHERE category = :category ORDER BY created_at DESC")
    LiveData<List<UserPoints>> getPointTransactionsByCategory(String category);

    @Query("SELECT * FROM user_points WHERE is_expired = 0 AND expiry_date IS NOT NULL ORDER BY expiry_date ASC")
    LiveData<List<UserPoints>> getActivePointsWithExpiry();

    @Query("SELECT * FROM user_points WHERE expiry_date IS NOT NULL AND expiry_date <= :currentDate AND is_expired = 0")
    List<UserPoints> getExpiringPoints(Date currentDate);

    @Query("SELECT * FROM user_points WHERE recipient_user_id = :userId OR sender_user_id = :userId ORDER BY created_at DESC")
    LiveData<List<UserPoints>> getPointTransfers(String userId);

    @Query("SELECT * FROM user_points WHERE reward_level = :level ORDER BY created_at DESC")
    LiveData<List<UserPoints>> getPointTransactionsByRewardLevel(String level);

    @Query("SELECT * FROM user_points WHERE verification_status = :status ORDER BY created_at DESC")
    LiveData<List<UserPoints>> getPointTransactionsByVerificationStatus(String status);

    @Query("SELECT * FROM user_points WHERE created_at BETWEEN :startDate AND :endDate ORDER BY created_at DESC")
    LiveData<List<UserPoints>> getPointTransactionsInDateRange(Date startDate, Date endDate);

    @Query("SELECT * FROM user_points WHERE id = :id")
    LiveData<UserPoints> getPointTransactionById(String id);

    // Balance and summary queries
    @Query("SELECT SUM(CASE WHEN user_id = :userId THEN points_amount ELSE 0 END) FROM user_points WHERE (user_id = :userId OR recipient_user_id = :userId) AND is_expired = 0")
    LiveData<Integer> getCurrentPointBalance(String userId);

    @Query("SELECT SUM(points_amount) FROM user_points WHERE user_id = :userId AND transaction_type = 'EARN' AND created_at BETWEEN :startDate AND :endDate")
    LiveData<Integer> getPointsEarnedInPeriod(String userId, Date startDate, Date endDate);

    @Query("SELECT SUM(points_amount) FROM user_points WHERE user_id = :userId AND transaction_type = 'SPEND' AND created_at BETWEEN :startDate AND :endDate")
    LiveData<Integer> getPointsSpentInPeriod(String userId, Date startDate, Date endDate);

    @Query("SELECT COUNT(*) FROM user_points WHERE user_id = :userId")
    LiveData<Integer> getUserPointTransactionCount(String userId);

    @Query("SELECT COUNT(*) FROM user_points WHERE user_id = :userId AND transaction_type = :type")
    LiveData<Integer> getUserPointTransactionCountByType(String userId, String type);

    @Query("SELECT SUM(currency_equivalent) FROM user_points WHERE user_id = :userId AND is_redeemable = 1 AND is_expired = 0")
    LiveData<Double> getRedeemableValue(String userId);

    // Milestones and achievements
    @Query("SELECT DISTINCT milestone_reached FROM user_points WHERE user_id = :userId AND milestone_reached IS NOT NULL ORDER BY created_at DESC")
    LiveData<List<String>> getUserMilestones(String userId);

    @Query("SELECT DISTINCT badge_earned FROM user_points WHERE user_id = :userId AND badge_earned IS NOT NULL ORDER BY created_at DESC")
    LiveData<List<String>> getUserBadges(String userId);

    @Query("SELECT reward_level, MAX(created_at) as last_achieved FROM user_points WHERE user_id = :userId AND reward_level IS NOT NULL GROUP BY reward_level ORDER BY last_achieved DESC")
    LiveData<List<UserRewardLevel>> getUserRewardLevels(String userId);

    @Query("UPDATE user_points SET is_expired = 1, updated_at = :updatedAt WHERE expiry_date <= :currentDate AND is_expired = 0")
    void expirePoints(Date currentDate, Date updatedAt);

    @Query("UPDATE user_points SET verification_status = :status, updated_at = :updatedAt WHERE id = :id")
    void updateVerificationStatus(String id, String status, Date updatedAt);

    @Query("DELETE FROM user_points WHERE user_id = :userId")
    void deleteUserPoints(String userId);

    @Query("DELETE FROM user_points WHERE is_expired = 1 AND updated_at < :cutoffDate")
    void deleteOldExpiredPoints(Date cutoffDate);

    @Insert
    void insert(UserPoints points);

    @Insert
    void insertAll(List<UserPoints> pointsList);

    @Update
    void update(UserPoints points);

    @Delete
    void delete(UserPoints points);

    @Query("DELETE FROM user_points WHERE id = :id")
    void deleteById(String id);

    // Advanced analytics queries
    @Query("SELECT source_type, SUM(points_amount) as total_points FROM user_points WHERE user_id = :userId AND transaction_type = 'EARN' GROUP BY source_type ORDER BY total_points DESC")
    LiveData<List<PointSourceSummary>> getPointEarningBySource(String userId);

    @Query("SELECT category, SUM(points_amount) as total_points FROM user_points WHERE user_id = :userId GROUP BY category ORDER BY total_points DESC")
    LiveData<List<PointCategorySummary>> getPointsByCategory(String userId);

    @Query("SELECT DATE(created_at) as date, SUM(CASE WHEN transaction_type = 'EARN' THEN points_amount ELSE 0 END) as earned, SUM(CASE WHEN transaction_type = 'SPEND' THEN points_amount ELSE 0 END) as spent FROM user_points WHERE user_id = :userId AND created_at >= :startDate GROUP BY DATE(created_at) ORDER BY date DESC")
    LiveData<List<PointDailySummary>> getDailyPointSummary(String userId, Date startDate);

    @Query("SELECT user_id, SUM(points_amount) as total_points FROM user_points WHERE transaction_type = 'EARN' GROUP BY user_id ORDER BY total_points DESC LIMIT 10")
    LiveData<List<TopPointsUser>> getTopPointsEarners();

    @Query("SELECT reward_level, COUNT(DISTINCT user_id) as user_count FROM user_points WHERE reward_level IS NOT NULL GROUP BY reward_level ORDER BY user_count DESC")
    LiveData<List<RewardLevelCount>> getRewardLevelDistribution();

    // Helper classes for query results
    class UserRewardLevel {
        public String reward_level;
        public Date last_achieved;
    }

    class PointSourceSummary {
        public String source_type;
        public int total_points;
    }

    class PointCategorySummary {
        public String category;
        public int total_points;
    }

    class PointDailySummary {
        public String date;
        public int earned;
        public int spent;
    }

    class TopPointsUser {
        public String user_id;
        public int total_points;
    }

    class RewardLevelCount {
        public String reward_level;
        public int user_count;
    }
}
