package com.example.androidapp.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.androidapp.data.entities.PointTransaction;

import java.util.List;

@Dao
public interface PointTransactionDao {
    @Insert
    void insert(PointTransaction pointTransaction);

    @Update
    void update(PointTransaction pointTransaction);

    @Delete
    void delete(PointTransaction pointTransaction);

    @Query("SELECT * FROM pointtransactions WHERE companyId = :companyId ORDER BY date DESC")
    LiveData<List<PointTransaction>> getAllPointTransactions(String companyId);

    @Query("SELECT * FROM pointtransactions WHERE id = :pointTransactionId AND companyId = :companyId")
    LiveData<PointTransaction> getPointTransactionById(String pointTransactionId, String companyId);

    @Query("SELECT * FROM pointtransactions WHERE id = :pointTransactionId")
    PointTransaction getPointTransactionByIdSync(String pointTransactionId);

    @Query("SELECT * FROM pointtransactions WHERE userId = :userId AND companyId = :companyId ORDER BY date DESC")
    LiveData<List<PointTransaction>> getPointTransactionsByUser(String userId, String companyId);

    @Query("SELECT SUM(points) FROM pointtransactions WHERE userId = :userId AND companyId = :companyId AND type = 'EARN'")
    LiveData<Integer> getTotalPointsForUser(String userId, String companyId);

    @Query("DELETE FROM pointtransactions WHERE companyId = :companyId")
    void deleteAllPointTransactions(String companyId);
}
