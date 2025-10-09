package com.example.androidapp.data.entities;
import androidx.annotation.NonNull;
import androidx.annotation.NonNull;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

import java.util.Date;

@Entity(tableName = "user_trophies",
        primaryKeys = {"userId", "trophyId", "companyId"},
        foreignKeys = {
                @ForeignKey(entity = User.class,
                            parentColumns = "id",
                            childColumns = "userId",
                            onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Trophy.class,
                            parentColumns = "id",
                            childColumns = "trophyId",
                            onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Company.class,
                            parentColumns = "id",
                            childColumns = "companyId",
                            onDelete = ForeignKey.CASCADE)
        },
        indices = {@Index(value = "userId"), @Index(value = "trophyId"), @Index(value = "companyId")})
public class UserTrophy {
    public @NonNull String userId;
    public @NonNull String trophyId;
    public @NonNull String companyId;
    public Date achievedDate;

    public UserTrophy(String userId, String trophyId, String companyId, Date achievedDate) {
        this.userId = userId;
        this.trophyId = trophyId;
        this.companyId = companyId;
        this.achievedDate = achievedDate;
    }

    // Getters
    public String getUserId() {
        return userId;
    }

    public String getTrophyId() {
        return trophyId;
    }

    public String getCompanyId() {
        return companyId;
    }

    public Date getAchievedDate() {
        return achievedDate;
    }

    // Setters
    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setTrophyId(String trophyId) {
        this.trophyId = trophyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public void setAchievedDate(Date achievedDate) {
        this.achievedDate = achievedDate;
    }
}

