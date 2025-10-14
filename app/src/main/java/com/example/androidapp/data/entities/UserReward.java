package com.example.androidapp.data.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import java.util.Date;
import java.util.UUID;

@Entity(tableName = "user_rewards",
        foreignKeys = {
                @ForeignKey(entity = User.class, parentColumns = "id", childColumns = "userId", onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Reward.class, parentColumns = "id", childColumns = "rewardId", onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Company.class, parentColumns = "id", childColumns = "companyId", onDelete = ForeignKey.CASCADE)
        },
        indices = {@Index(value = "userId"), @Index(value = "rewardId"), @Index(value = "companyId")})
public class UserReward {
    @PrimaryKey
    @NonNull
    public String id;
    @NonNull
    public String userId;
    public String rewardId;
    @NonNull
    public String companyId;
    public Date redemptionDate;
    public boolean isRedeemed;

    // Constructor الأساسي لـ Room
    public UserReward(@NonNull String id, @NonNull String userId, String rewardId, @NonNull String companyId, Date redemptionDate, boolean isRedeemed) {
        this.id = id;
        this.userId = userId;
        this.rewardId = rewardId;
        this.companyId = companyId;
        this.redemptionDate = redemptionDate;
        this.isRedeemed = isRedeemed;
    }

    // Constructor المساعد
    @Ignore
    public UserReward(@NonNull String userId, String rewardId, @NonNull String companyId, Date redemptionDate, boolean isRedeemed) {
        this.id = UUID.randomUUID().toString();
        this.userId = userId;
        this.rewardId = rewardId;
        this.companyId = companyId;
        this.redemptionDate = redemptionDate;
        this.isRedeemed = isRedeemed;
    }

    // Getters and Setters
    @NonNull
    public String getId() { return id; }
    public void setId(@NonNull String id) { this.id = id; }
    @NonNull
    public String getUserId() { return userId; }
    public void setUserId(@NonNull String userId) { this.userId = userId; }
    public String getRewardId() { return rewardId; }
    public void setRewardId(String rewardId) { this.rewardId = rewardId; }
    @NonNull
    public String getCompanyId() { return companyId; }
    public void setCompanyId(@NonNull String companyId) { this.companyId = companyId; }
    public Date getRedemptionDate() { return redemptionDate; }
    public void setRedemptionDate(Date redemptionDate) { this.redemptionDate = redemptionDate; }
    public boolean isRedeemed() { return isRedeemed; }
    public void setRedeemed(boolean redeemed) { isRedeemed = redeemed; }
}
