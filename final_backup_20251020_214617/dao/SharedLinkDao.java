package com.example.androidapp.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;
import java.util.List;
import com.example.androidapp.data.entities.SharedLink;

@Dao
public interface SharedLinkDao {
    @Insert
    void insert(SharedLink sharedlink);

    @Update
    void update(SharedLink sharedlink);

    @Delete
    void delete(SharedLink sharedlink);

    @Query("SELECT * FROM shared_links")
    List<SharedLink> getAllSharedLinks();

    @Query("SELECT * FROM shared_links WHERE id = :id LIMIT 1")
    SharedLink getById(String id);

    @Query("SELECT * FROM shared_links WHERE companyId = :companyId")
    List<SharedLink> getSharedLinksByCompanyId(String companyId);
}
