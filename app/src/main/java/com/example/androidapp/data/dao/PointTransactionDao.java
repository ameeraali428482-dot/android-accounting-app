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

    @Query("SELECT * FROM pointtransactions WHERE companyId = :companyId")
    LiveData<List<PointTransaction>> getAllPointTransactions(String companyId);

    @Query("SELECT * FROM pointtransactions WHERE id = :id AND companyId = :companyId LIMIT 1")
    LiveData<PointTransaction> getPointTransactionById(String id, String companyId);

    @Query("SELECT SUM(points) FROM pointtransactions WHERE userId = :userId AND companyId = :companyId")
    LiveData<Integer> getTotalPointsForUser(String userId, String companyId);

    @Query("SELECT * FROM pointtransactions WHERE userId = :userId AND companyId = :companyId")
    LiveData<List<PointTransaction>> getPointTransactionsByUserId(String userId, String companyId);

    @Query("SELECT * FROM pointtransactions WHERE id = :id AND companyId = :companyId LIMIT 1")
    PointTransaction getPointTransactionByIdSync(String id, String companyId);
}
