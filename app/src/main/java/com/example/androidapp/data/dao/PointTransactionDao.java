package com.example.androidapp.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Update;
import androidx.room.Delete;
import androidx.room.Query;
import androidx.lifecycle.LiveData;

import com.example.androidapp.models.PointTransaction;

import java.util.List;

@Dao
public interface PointTransactionDao extends BaseDao<PointTransaction> {
    @Query("SELECT * FROM point_transactions WHERE orgId = :orgId")
    LiveData<List<PointTransaction>> getAllPointTransactions(int orgId);

    @Query("SELECT * FROM point_transactions WHERE id = :id AND orgId = :orgId")
    LiveData<PointTransaction> getPointTransactionById(int id, int orgId);

    @Query("SELECT SUM(points) FROM point_transactions WHERE userId = :userId AND orgId = :orgId")
    LiveData<Integer> getTotalPointsForUser(int userId, int orgId);

    @Insert
    long insert(PointTransaction pointTransaction);

    @Update
    void update(PointTransaction pointTransaction);

    @Delete
    void delete(PointTransaction pointTransaction);
}

