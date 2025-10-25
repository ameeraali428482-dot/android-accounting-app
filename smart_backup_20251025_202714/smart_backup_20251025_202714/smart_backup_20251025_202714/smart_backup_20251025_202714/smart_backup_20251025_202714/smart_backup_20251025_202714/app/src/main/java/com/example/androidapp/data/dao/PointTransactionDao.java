package com.example.androidapp.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.example.androidapp.data.entities.PointTransaction;
import java.util.List;

@Dao
public interface PointTransactionDao {
    
    @Query("SELECT * FROM point_transactions WHERE companyId = :companyId")
    LiveData<List<PointTransaction>> getAllPointTransactions(String companyId);

    @Query("SELECT * FROM point_transactions WHERE id = :pointTransactionId AND companyId = :companyId")
    LiveData<PointTransaction> getPointTransactionById(String pointTransactionId, String companyId);

    @Query("SELECT * FROM point_transactions WHERE id = :pointTransactionId")
    PointTransaction getPointTransactionByIdSync(String pointTransactionId);

    @Query("SELECT * FROM point_transactions WHERE userId = :userId AND companyId = :companyId")
    LiveData<List<PointTransaction>> getPointTransactionsByUser(String userId, String companyId);

    @Query("SELECT SUM(points) FROM point_transactions WHERE userId = :userId AND companyId = :companyId")
    LiveData<Integer> getTotalPointsForUser(String userId, String companyId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(PointTransaction pointTransaction);

    @Update
    void update(PointTransaction pointTransaction);

    @Delete
    void delete(PointTransaction pointTransaction);

    @Query("DELETE FROM point_transactions WHERE companyId = :companyId")
    void deleteAllPointTransactions(String companyId);
}
