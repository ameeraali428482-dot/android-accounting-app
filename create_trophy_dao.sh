#!/bin/bash

echo "Creating missing file: TrophyDao.java"

# Define the file path
DAO_FILE="app/src/main/java/com/example/androidapp/data/dao/TrophyDao.java"

# Create the directory if it doesn't exist
mkdir -p "$(dirname "$DAO_FILE")"

# Create the TrophyDao.java file with the correct content
cat > "$DAO_FILE" << 'EOP'
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
public interface TrophyDao {
    @Insert
    void insert(Trophy trophy);

    @Update
    void update(Trophy trophy);

    @Delete
    void delete(Trophy trophy);

    @Query("SELECT * FROM trophies WHERE companyId = :companyId ORDER BY pointsRequired ASC")
    LiveData<List<Trophy>> getAllTrophies(String companyId);

    @Query("SELECT * FROM trophies WHERE id = :trophyId AND companyId = :companyId")
    LiveData<Trophy> getTrophyById(String trophyId, String companyId);
}
EOP

echo "File TrophyDao.java created successfully."
