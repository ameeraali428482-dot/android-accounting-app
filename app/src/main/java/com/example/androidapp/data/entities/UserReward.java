package com.example.androidapp.data.entities;

import androidx.room.TypeConverters;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.Ignore;
import androidx.room.Embedded;
import androidx.room.PrimaryKey;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;


@Entity(tableName = "user_rewards")
public class UserReward {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public int userId;
    public int rewardId;
}
