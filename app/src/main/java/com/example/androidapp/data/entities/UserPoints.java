package com.example.androidapp.data.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;
import com.example.androidapp.data.DateConverter;

import java.util.Date;

/**
 * كيان نقاط المستخدم - لإدارة نظام النقاط والمكافآت
 */
@Entity(tableName = "user_points")
@TypeConverters({DateConverter.class})
public class UserPoints {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    private String id;

    @ColumnInfo(name = "user_id")
    private String userId;

    @ColumnInfo(name = "transaction_type")
    private String transactionType; // EARN, SPEND, TRANSFER, BONUS, PENALTY, EXPIRE

    @ColumnInfo(name = "points_amount")
    private int pointsAmount; // Can be positive or negative

    @ColumnInfo(name = "balance_before")
    private int balanceBefore;

    @ColumnInfo(name = "balance_after")
    private int balanceAfter;

    @ColumnInfo(name = "source_type")
    private String sourceType; // REFERRAL, PURCHASE, APP_SHARE, REVIEW, LOGIN_STREAK, etc.

    @ColumnInfo(name = "source_id")
    private String sourceId; // ID of related entity (invoice, referral, etc.)

    @ColumnInfo(name = "description")
    private String description;

    @ColumnInfo(name = "title")
    private String title; // Short title for the transaction

    @ColumnInfo(name = "category")
    private String category; // SOCIAL, BUSINESS, ACHIEVEMENT, BONUS

    @ColumnInfo(name = "multiplier")
    private float multiplier; // Point multiplier applied (e.g., 2x for premium users)

    @ColumnInfo(name = "expiry_date")
    private Date expiryDate; // When these points expire (if applicable)

    @ColumnInfo(name = "is_expired")
    private boolean isExpired;

    @ColumnInfo(name = "recipient_user_id")
    private String recipientUserId; // For point transfers

    @ColumnInfo(name = "sender_user_id")
    private String senderUserId; // For point transfers

    @ColumnInfo(name = "transfer_note")
    private String transferNote; // Note for point transfers

    @ColumnInfo(name = "reward_level")
    private String rewardLevel; // BRONZE, SILVER, GOLD, PLATINUM

    @ColumnInfo(name = "milestone_reached")
    private String milestoneReached; // Milestone achieved with this transaction

    @ColumnInfo(name = "badge_earned")
    private String badgeEarned; // Badge earned with this transaction

    @ColumnInfo(name = "conversion_rate")
    private float conversionRate; // Points to currency conversion rate

    @ColumnInfo(name = "currency_equivalent")
    private double currencyEquivalent; // Monetary value of points

    @ColumnInfo(name = "currency_code")
    private String currencyCode; // Currency for the equivalent value

    @ColumnInfo(name = "is_redeemable")
    private boolean isRedeemable; // Whether these points can be redeemed for value

    @ColumnInfo(name = "redemption_minimum")
    private int redemptionMinimum; // Minimum points needed for redemption

    @ColumnInfo(name = "tax_applicable")
    private boolean taxApplicable; // Whether tax applies to redemption

    @ColumnInfo(name = "location")
    private String location; // Geographic location where points were earned

    @ColumnInfo(name = "device_id")
    private String deviceId; // Device where transaction occurred

    @ColumnInfo(name = "ip_address")
    private String ipAddress; // IP address for fraud detection

    @ColumnInfo(name = "verification_status")
    private String verificationStatus; // VERIFIED, PENDING, REJECTED

    @ColumnInfo(name = "audit_trail")
    private String auditTrail; // JSON with detailed audit information

    @ColumnInfo(name = "metadata")
    private String metadata; // Additional JSON metadata

    @ColumnInfo(name = "created_at")
    private Date createdAt;

    @ColumnInfo(name = "updated_at")
    private Date updatedAt;

    // Constructors
    public UserPoints() {
        this.createdAt = new Date();
        this.updatedAt = new Date();
        this.verificationStatus = "VERIFIED";
        this.multiplier = 1.0f;
        this.isRedeemable = true;
    }

    public UserPoints(@NonNull String id, String userId, String transactionType, 
                     int pointsAmount, int balanceBefore, int balanceAfter) {
        this.id = id;
        this.userId = userId;
        this.transactionType = transactionType;
        this.pointsAmount = pointsAmount;
        this.balanceBefore = balanceBefore;
        this.balanceAfter = balanceAfter;
        this.createdAt = new Date();
        this.updatedAt = new Date();
        this.verificationStatus = "VERIFIED";
        this.multiplier = 1.0f;
        this.isRedeemable = true;
    }

    // Getters and Setters
    @NonNull
    public String getId() { return id; }
    public void setId(@NonNull String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getTransactionType() { return transactionType; }
    public void setTransactionType(String transactionType) { this.transactionType = transactionType; }

    public int getPointsAmount() { return pointsAmount; }
    public void setPointsAmount(int pointsAmount) { this.pointsAmount = pointsAmount; }

    public int getBalanceBefore() { return balanceBefore; }
    public void setBalanceBefore(int balanceBefore) { this.balanceBefore = balanceBefore; }

    public int getBalanceAfter() { return balanceAfter; }
    public void setBalanceAfter(int balanceAfter) { this.balanceAfter = balanceAfter; }

    public String getSourceType() { return sourceType; }
    public void setSourceType(String sourceType) { this.sourceType = sourceType; }

    public String getSourceId() { return sourceId; }
    public void setSourceId(String sourceId) { this.sourceId = sourceId; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public float getMultiplier() { return multiplier; }
    public void setMultiplier(float multiplier) { this.multiplier = multiplier; }

    public Date getExpiryDate() { return expiryDate; }
    public void setExpiryDate(Date expiryDate) { this.expiryDate = expiryDate; }

    public boolean isExpired() { return isExpired; }
    public void setExpired(boolean expired) { isExpired = expired; }

    public String getRecipientUserId() { return recipientUserId; }
    public void setRecipientUserId(String recipientUserId) { this.recipientUserId = recipientUserId; }

    public String getSenderUserId() { return senderUserId; }
    public void setSenderUserId(String senderUserId) { this.senderUserId = senderUserId; }

    public String getTransferNote() { return transferNote; }
    public void setTransferNote(String transferNote) { this.transferNote = transferNote; }

    public String getRewardLevel() { return rewardLevel; }
    public void setRewardLevel(String rewardLevel) { this.rewardLevel = rewardLevel; }

    public String getMilestoneReached() { return milestoneReached; }
    public void setMilestoneReached(String milestoneReached) { this.milestoneReached = milestoneReached; }

    public String getBadgeEarned() { return badgeEarned; }
    public void setBadgeEarned(String badgeEarned) { this.badgeEarned = badgeEarned; }

    public float getConversionRate() { return conversionRate; }
    public void setConversionRate(float conversionRate) { this.conversionRate = conversionRate; }

    public double getCurrencyEquivalent() { return currencyEquivalent; }
    public void setCurrencyEquivalent(double currencyEquivalent) { this.currencyEquivalent = currencyEquivalent; }

    public String getCurrencyCode() { return currencyCode; }
    public void setCurrencyCode(String currencyCode) { this.currencyCode = currencyCode; }

    public boolean isRedeemable() { return isRedeemable; }
    public void setRedeemable(boolean redeemable) { isRedeemable = redeemable; }

    public int getRedemptionMinimum() { return redemptionMinimum; }
    public void setRedemptionMinimum(int redemptionMinimum) { this.redemptionMinimum = redemptionMinimum; }

    public boolean isTaxApplicable() { return taxApplicable; }
    public void setTaxApplicable(boolean taxApplicable) { this.taxApplicable = taxApplicable; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public String getVerificationStatus() { return verificationStatus; }
    public void setVerificationStatus(String verificationStatus) { this.verificationStatus = verificationStatus; }

    public String getAuditTrail() { return auditTrail; }
    public void setAuditTrail(String auditTrail) { this.auditTrail = auditTrail; }

    public String getMetadata() { return metadata; }
    public void setMetadata(String metadata) { this.metadata = metadata; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
}
