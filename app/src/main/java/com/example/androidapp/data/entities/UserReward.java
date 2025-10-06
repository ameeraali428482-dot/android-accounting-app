package com.example.androidapp.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ForeignKey;
import androidx.room.TypeConverters;

import java.util.Date;

import com.example.androidapp.data.DateConverter;

@Entity(tableName = "user_rewards",
        foreignKeys = {
                @ForeignKey(entity = User.class,
                            parentColumns = "id",
                            childColumns = "userId",
                            onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Reward.class,
                            parentColumns = "id",
                            childColumns = "rewardId",
                            onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Company.class,
                            parentColumns = "id",
                            childColumns = "orgId",
                            onDelete = ForeignKey.CASCADE)
        })
@TypeConverters({DateConverter.class})
public class UserReward {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public int userId;
    public int rewardId;
    public int orgId;
    public Date redemptionDate;
    public boolean isRedeemed;

    public UserReward(int userId, int rewardId, int orgId, Date redemptionDate, boolean isRedeemed) {
        this.userId = userId;
        this.rewardId = rewardId;
        this.orgId = orgId;
        this.redemptionDate = redemptionDate;
        this.isRedeemed = isRedeemed;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getRewardId() {
        return rewardId;
    }

    public void setRewardId(int rewardId) {
        this.rewardId = rewardId;
    }

    public int getOrgId() {
        return orgId;
    }

    public void setOrgId(int orgId) {
        this.orgId = orgId;
    }

    public Date getRedemptionDate() {
        return redemptionDate;
    }

    public void setRedemptionDate(Date redemptionDate) {
        this.redemptionDate = redemptionDate;
    }

    public boolean isRedeemed() {
        return isRedeemed;
    }

    public void setRedeemed(boolean redeemed) {
        isRedeemed = redeemed;
    }
}

