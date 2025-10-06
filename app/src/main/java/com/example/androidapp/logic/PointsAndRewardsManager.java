package com.example.androidapp.logic;

import android.content.Context;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.example.androidapp.data.AppDatabase;
import com.example.androidapp.models.PointTransaction;
import com.example.androidapp.models.Reward;
import com.example.androidapp.models.UserReward;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PointsAndRewardsManager {

    private final AppDatabase database;
    private final ExecutorService executorService;

    public PointsAndRewardsManager(Context context) {
        database = AppDatabase.getDatabase(context);
        executorService = AppDatabase.databaseWriteExecutor;
    }

    /**
     * Adds points to a user's balance by creating a new PointTransaction.
     *
     * @param userId The ID of the user.
     * @param orgId The ID of the organization/company.
     * @param points The number of points to add.
     * @param description A description of the transaction.
     */
    public void addPoints(int userId, int orgId, int points, String description) {
        executorService.execute(() -> {
            PointTransaction transaction = new PointTransaction(
                    userId, orgId, points, "EARN", description, new Date()
            );
            database.pointTransactionDao().insert(transaction);
        });
    }

    /**
     * Redeems a reward for a user, deducting points and creating a UserReward entry.
     *
     * @param userId The ID of the user.
     * @param orgId The ID of the organization/company.
     * @param reward The Reward object to be redeemed.
     * @param callback Callback to notify about success or failure.
     */
    public void redeemReward(int userId, int orgId, Reward reward, RedemptionCallback callback) {
        executorService.execute(() -> {
            LiveData<Integer> totalPointsLiveData = database.pointTransactionDao().getTotalPointsForUser(userId, orgId);
            totalPointsLiveData.observeForever(totalPoints -> {
                if (totalPoints != null && totalPoints >= reward.getPointsRequired()) {
                    // Deduct points
                    PointTransaction deduction = new PointTransaction(
                            userId, orgId, -reward.getPointsRequired(), "REDEEM",
                            "Redeemed reward: " + reward.getName(), new Date()
                    );
                    database.pointTransactionDao().insert(deduction);

                    // Create UserReward entry
                    UserReward userReward = new UserReward(
                            userId, reward.getId(), orgId, new Date(), true
                    );
                    database.userRewardDao().insert(userReward);
                    callback.onSuccess();
                } else {
                    callback.onFailure("نقاط غير كافية لاسترداد المكافأة.");
                }
                // Remove observer to prevent memory leaks
                totalPointsLiveData.removeObserver(totalPoints -> {});
            });
        });
    }

    /**
     * Gets the total points for a specific user within an organization.
     *
     * @param userId The ID of the user.
     * @param orgId The ID of the organization/company.
     * @return LiveData containing the total points.
     */
    public LiveData<Integer> getUserTotalPoints(int userId, int orgId) {
        return database.pointTransactionDao().getTotalPointsForUser(userId, orgId);
    }

    public interface RedemptionCallback {
        void onSuccess();
        void onFailure(String errorMessage);
    }
}

