package com.example.androidapp.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Update;
import androidx.room.Delete;
import androidx.room.Query;
import androidx.lifecycle.LiveData;

import com.example.androidapp.data.entities.PointTransaction;

import java.util.List;

@Dao
public interface PointTransactionDao extends BaseDao<PointTransaction> {
    @Query("SELECT * FROM point_transactions WHERE companyId = :companyId")
    LiveData<List<PointTransaction>> getAllPointTransactions(String companyId);

    @Query("SELECT * FROM point_transactions WHERE id = :id AND companyId = :companyId")
    LiveData<PointTransaction> getPointTransactionById(String id, String companyId);

    @Query("SELECT SUM(points) FROM point_transactions WHERE userId = :userId AND companyId = :companyId")
    LiveData<Integer> getTotalPointsForUser(String userId, String companyId);
}
