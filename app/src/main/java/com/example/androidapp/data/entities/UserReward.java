package com.example.androidapp.data.entities;
import androidx.room.ForeignKey;import androidx.room.Index;import androidx.room.TypeConverters;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "user_rewards")
public class UserReward {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public int userId;
    public int rewardId;
}
