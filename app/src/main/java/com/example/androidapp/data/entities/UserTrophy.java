package com.example.androidapp.models;

import androidx.room.Entity;
import androidx.room.ForeignKey;

import java.util.Date;

@Entity(tableName = "user_trophies",
        primaryKeys = {"userId", "trophyId"},
        foreignKeys = {
                @ForeignKey(entity = User.class,
                            parentColumns = "id",
                            childColumns = "userId",
                            onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Trophy.class,
                            parentColumns = "id",
                            childColumns = "trophyId",
                            onDelete = ForeignKey.CASCADE)
        })
public class UserTrophy {
    public int userId;
    public int trophyId;
    public Date achievedDate;

    public UserTrophy(int userId, int trophyId, Date achievedDate) {
        this.userId = userId;
        this.trophyId = trophyId;
        this.achievedDate = achievedDate;
    }

    // Getters
    public int getUserId() {
        return userId;
    }

    public int getTrophyId() {
        return trophyId;
    }

    public Date getAchievedDate() {
        return achievedDate;
    }
}
