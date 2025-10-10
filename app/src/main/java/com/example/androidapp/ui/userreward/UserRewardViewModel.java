package com.example.androidapp.ui.userreward;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.entities.UserReward;
import java.util.List;






public class UserRewardViewModel extends AndroidViewModel {
    private final LiveData<List<UserReward>> allUserRewards;
    private final AppDatabase database;

    public UserRewardViewModel(Application application) {
        super(application);
        database = AppDatabase.getDatabase(application);
        // This will need to be updated to filter by orgId
        allUserRewards = database.userRewardDao().getAllUserRewards(0); // Placeholder orgId
    }

    public LiveData<List<UserReward>> getAllUserRewards(int orgId) {
        return database.userRewardDao().getAllUserRewards(orgId);
    }

    public LiveData<List<UserReward>> getUserRewardsByUserId(int userId, int orgId) {
        return database.userRewardDao().getUserRewardsByUserId(userId, orgId);
    }

    public LiveData<UserReward> getUserRewardById(int id, int orgId) {
        return database.userRewardDao().getUserRewardById(id, orgId);
    }

    public void insert(UserReward userReward) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            database.userRewardDao().insert(userReward);
        });
    }

    public void update(UserReward userReward) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            database.userRewardDao().update(userReward);
        });
    }

    public void delete(UserReward userReward) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            database.userRewardDao().delete(userReward);
        });
    }
}

