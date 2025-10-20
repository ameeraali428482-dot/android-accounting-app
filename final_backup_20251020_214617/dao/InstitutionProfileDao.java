package com.example.androidapp.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.example.androidapp.data.entities.InstitutionProfile;

import java.util.Date;
import java.util.List;

/**
 * DAO لملف تعريف المؤسسة - لإدارة العمليات على قاعدة البيانات لملفات المؤسسات
 */
@Dao
public interface InstitutionProfileDao {

    @Query("SELECT * FROM institution_profiles ORDER BY created_at DESC")
    LiveData<List<InstitutionProfile>> getAllInstitutions();

    @Query("SELECT * FROM institution_profiles WHERE is_active = 1 ORDER BY name ASC")
    LiveData<List<InstitutionProfile>> getActiveInstitutions();

    @Query("SELECT * FROM institution_profiles WHERE institution_type = :type AND is_active = 1 ORDER BY name ASC")
    LiveData<List<InstitutionProfile>> getInstitutionsByType(String type);

    @Query("SELECT * FROM institution_profiles WHERE business_category = :category AND is_active = 1 ORDER BY average_rating DESC")
    LiveData<List<InstitutionProfile>> getInstitutionsByCategory(String category);

    @Query("SELECT * FROM institution_profiles WHERE is_verified = 1 AND is_active = 1 ORDER BY average_rating DESC")
    LiveData<List<InstitutionProfile>> getVerifiedInstitutions();

    @Query("SELECT * FROM institution_profiles WHERE subscription_type = :subscriptionType AND is_active = 1")
    LiveData<List<InstitutionProfile>> getInstitutionsBySubscription(String subscriptionType);

    @Query("SELECT * FROM institution_profiles WHERE city = :city AND is_active = 1 ORDER BY average_rating DESC")
    LiveData<List<InstitutionProfile>> getInstitutionsByCity(String city);

    @Query("SELECT * FROM institution_profiles WHERE country = :country AND is_active = 1 ORDER BY average_rating DESC")
    LiveData<List<InstitutionProfile>> getInstitutionsByCountry(String country);

    @Query("SELECT * FROM institution_profiles WHERE average_rating >= :minRating AND is_active = 1 ORDER BY average_rating DESC")
    LiveData<List<InstitutionProfile>> getHighRatedInstitutions(float minRating);

    @Query("SELECT * FROM institution_profiles WHERE total_referrals >= :minReferrals AND is_active = 1 ORDER BY total_referrals DESC")
    LiveData<List<InstitutionProfile>> getTopReferrers(int minReferrals);

    @Query("SELECT * FROM institution_profiles WHERE points_balance >= :minPoints AND is_active = 1 ORDER BY points_balance DESC")
    LiveData<List<InstitutionProfile>> getTopPointHolders(int minPoints);

    @Query("SELECT * FROM institution_profiles WHERE id = :id")
    LiveData<InstitutionProfile> getInstitutionById(String id);

    @Query("SELECT * FROM institution_profiles WHERE primary_email = :email")
    LiveData<InstitutionProfile> getInstitutionByEmail(String email);

    @Query("SELECT * FROM institution_profiles WHERE referral_code = :referralCode")
    LiveData<InstitutionProfile> getInstitutionByReferralCode(String referralCode);

    @Query("SELECT * FROM institution_profiles WHERE (name LIKE '%' || :searchTerm || '%' OR display_name LIKE '%' || :searchTerm || '%' OR description LIKE '%' || :searchTerm || '%') AND is_active = 1")
    LiveData<List<InstitutionProfile>> searchInstitutions(String searchTerm);

    @Query("SELECT * FROM institution_profiles WHERE subscription_expiry IS NOT NULL AND subscription_expiry <= :currentDate AND is_active = 1")
    List<InstitutionProfile> getExpiredSubscriptions(Date currentDate);

    @Query("SELECT * FROM institution_profiles WHERE subscription_expiry IS NOT NULL AND subscription_expiry BETWEEN :currentDate AND :warningDate AND is_active = 1")
    List<InstitutionProfile> getExpiringSubscriptions(Date currentDate, Date warningDate);

    @Query("SELECT COUNT(*) FROM institution_profiles WHERE is_active = 1")
    LiveData<Integer> getActiveInstitutionCount();

    @Query("SELECT COUNT(*) FROM institution_profiles WHERE institution_type = :type AND is_active = 1")
    LiveData<Integer> getInstitutionCountByType(String type);

    @Query("SELECT COUNT(*) FROM institution_profiles WHERE is_verified = 1 AND is_active = 1")
    LiveData<Integer> getVerifiedInstitutionCount();

    @Query("UPDATE institution_profiles SET is_verified = 1, verification_date = :verificationDate, updated_at = :updatedAt WHERE id = :id")
    void verifyInstitution(String id, Date verificationDate, Date updatedAt);

    @Query("UPDATE institution_profiles SET is_active = 0, updated_at = :updatedAt WHERE id = :id")
    void deactivateInstitution(String id, Date updatedAt);

    @Query("UPDATE institution_profiles SET average_rating = :rating, total_reviews = :reviews, updated_at = :updatedAt WHERE id = :id")
    void updateRating(String id, float rating, int reviews, Date updatedAt);

    @Query("UPDATE institution_profiles SET total_referrals = total_referrals + 1, updated_at = :updatedAt WHERE id = :id")
    void incrementReferrals(String id, Date updatedAt);

    @Query("UPDATE institution_profiles SET points_balance = points_balance + :points, updated_at = :updatedAt WHERE id = :id")
    void addPoints(String id, int points, Date updatedAt);

    @Query("UPDATE institution_profiles SET points_balance = points_balance - :points, updated_at = :updatedAt WHERE id = :id AND points_balance >= :points")
    void deductPoints(String id, int points, Date updatedAt);

    @Query("DELETE FROM institution_profiles WHERE is_active = 0 AND updated_at < :cutoffDate")
    void deleteOldInactiveInstitutions(Date cutoffDate);

    @Insert
    void insert(InstitutionProfile institution);

    @Insert
    void insertAll(List<InstitutionProfile> institutions);

    @Update
    void update(InstitutionProfile institution);

    @Delete
    void delete(InstitutionProfile institution);

    @Query("DELETE FROM institution_profiles WHERE id = :id")
    void deleteById(String id);

    // Advanced analytics queries
    @Query("SELECT institution_type, COUNT(*) as count FROM institution_profiles WHERE is_active = 1 GROUP BY institution_type ORDER BY count DESC")
    LiveData<List<InstitutionTypeCount>> getInstitutionCountsByType();

    @Query("SELECT business_category, COUNT(*) as count FROM institution_profiles WHERE is_active = 1 GROUP BY business_category ORDER BY count DESC")
    LiveData<List<InstitutionCategoryCount>> getInstitutionCountsByCategory();

    @Query("SELECT subscription_type, COUNT(*) as count FROM institution_profiles WHERE is_active = 1 GROUP BY subscription_type")
    LiveData<List<InstitutionSubscriptionCount>> getInstitutionCountsBySubscription();

    @Query("SELECT city, COUNT(*) as count FROM institution_profiles WHERE is_active = 1 AND city IS NOT NULL GROUP BY city ORDER BY count DESC LIMIT 20")
    LiveData<List<InstitutionCityCount>> getTopCitiesByInstitutionCount();

    // Helper classes for query results
    class InstitutionTypeCount {
        public String institution_type;
        public int count;
    }

    class InstitutionCategoryCount {
        public String business_category;
        public int count;
    }

    class InstitutionSubscriptionCount {
        public String subscription_type;
        public int count;
    }

    class InstitutionCityCount {
        public String city;
        public int count;
    }
}
