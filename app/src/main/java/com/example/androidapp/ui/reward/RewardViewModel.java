package com.example.androidapp.ui.reward;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.dao.RewardDao;
import com.example.androidapp.data.entities.Reward;
import java.util.List;

public class RewardViewModel extends AndroidViewModel {
    private RewardDao rewardDao;

    public RewardViewModel(@NonNull Application application) {
        super(application);
        rewardDao = AppDatabase.getDatabase(application).rewardDao();
    }

    public LiveData<List<Reward>> getAllRewards(String companyId) {
        return rewardDao.getAllRewards(companyId);
    }

    public LiveData<Reward> getRewardById(String id, String companyId) {
        return rewardDao.getRewardById(id, companyId);
    }

    public void insert(Reward reward) {
        AppDatabase.databaseWriteExecutor.execute(() -> rewardDao.insert(reward));
    }

    public void update(Reward reward) {
        AppDatabase.databaseWriteExecutor.execute(() -> rewardDao.update(reward));
    }

    public void delete(Reward reward) {
        AppDatabase.databaseWriteExecutor.execute(() -> rewardDao.delete(reward));
    }
}
