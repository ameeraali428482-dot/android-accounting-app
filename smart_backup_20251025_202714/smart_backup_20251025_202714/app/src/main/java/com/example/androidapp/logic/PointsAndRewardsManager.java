package com.example.androidapp.logic;

import android.content.Context;
import androidx.lifecycle.LiveData;
import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.data.entities.PointTransaction;
import com.example.androidapp.data.entities.Reward;
import com.example.androidapp.data.entities.UserReward;
import java.util.Date;

public class PointsAndRewardsManager {

    private final AppDatabase database;

    public PointsAndRewardsManager(Context context) {
        database = AppDatabase.getDatabase(context);
    }

    public void addPoints(String userId, String orgId, int points, String description) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            PointTransaction transaction = new PointTransaction(
                    userId, orgId, points, "EARN", description, new Date()
            );
            database.pointTransactionDao().insert(transaction);
        });
    }

    public void redeemReward(String userId, String orgId, Reward reward, RedemptionCallback callback) {
        LiveData<Integer> totalPointsLiveData = database.pointTransactionDao().getTotalPointsForUser(userId, orgId);
        
        totalPointsLiveData.observeForever(new androidx.lifecycle.Observer<Integer>() {
            @Override
            public void onChanged(Integer totalPoints) {
                totalPointsLiveData.removeObserver(this);
                AppDatabase.databaseWriteExecutor.execute(() -> {
                    if (totalPoints != null && totalPoints >= reward.getPointsRequired()) {
                        PointTransaction deduction = new PointTransaction(
                                userId, orgId, -reward.getPointsRequired(), "REDEEM",
                                "Redeemed reward: " + reward.getName(), new Date()
                        );
                        database.pointTransactionDao().insert(deduction);

                        UserReward userReward = new UserReward(
                                userId, reward.getId(), orgId, new Date(), true
                        );
                        database.userRewardDao().insert(userReward);
                        if (callback != null) {
                            callback.onSuccess();
                        }
                    } else {
                        if (callback != null) {
                            callback.onFailure("نقاط غير كافية لاسترداد المكافأة.");
                        }
                    }
                });
            }
        });
    }

    public LiveData<Integer> getUserTotalPoints(String userId, String orgId) {
        return database.pointTransactionDao().getTotalPointsForUser(userId, orgId);
    }

    public interface RedemptionCallback {
        void onSuccess();
        void onFailure(String errorMessage);
    }
}
