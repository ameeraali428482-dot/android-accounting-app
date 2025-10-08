package com.example.androidapp.ui.reward;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.models.Reward;

import java.util.List;

public class RewardViewModel extends AndroidViewModel {
    private final LiveData<List<Reward>> allRewards;
    private final AppDatabase database;

    public RewardViewModel(Application application) {
        super(application);
        database = AppDatabase.getDatabase(application);
        // This will need to be updated to filter by orgId
        allRewards = database.rewardDao().getAllRewards(0); // Placeholder orgId
    }

    public LiveData<List<Reward>> getAllRewards(int orgId) {
        return database.rewardDao().getAllRewards(orgId);
    }

    public LiveData<Reward> getRewardById(int id, int orgId) {
        return database.rewardDao().getRewardById(id, orgId);
    }

    public void insert(Reward reward) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            database.rewardDao().insert(reward);
        });
    }

    public void update(Reward reward) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            database.rewardDao().update(reward);
        });
    }

    public void delete(Reward reward) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            database.rewardDao().delete(reward);
        });
    }
}

