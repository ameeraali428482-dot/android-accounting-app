package com.example.androidapp.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.androidapp.data.entities.Trophy;
import java.util.List;





@Dao
public interface TrophyDao extends BaseDao<Trophy> {
    @Query("SELECT * FROM trophies WHERE companyId = :companyId ORDER BY name ASC")
    LiveData<List<Trophy>> getAllTrophies(String companyId);

    @Query("SELECT * FROM trophies WHERE id = :trophyId AND companyId = :companyId")
    LiveData<Trophy> getTrophyById(String trophyId, String companyId);
}
