package com.example.androidapp.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;
import java.util.List;
import com.example.androidapp.data.entities.Membership;




@Dao
public interface MembershipDao {
    @Insert
    void insert(Membership membership);

    @Update
    void update(Membership membership);

    @Delete
    void delete(Membership membership);

    @Query("SELECT * FROM memberships")
    List<Membership> getAllMemberships();

    @Query("SELECT * FROM memberships WHERE id = :id LIMIT 1")
    Membership getMembershipById(String id);
}
