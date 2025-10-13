package com.example.androidapp.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;
import java.util.List;
import com.example.androidapp.data.entities.User;




@Dao
public interface UserDao {
    @Insert
    void insert(User user);

    @Update
    void update(User user);

    @Delete
    void delete(User user);

    @Query("SELECT * FROM users")
    List<User> getAllUsers();

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    User getUserById(String id);
}

    @Query("SELECT * FROM users")
    List<User> getAllUsersSync();

    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    User getUserByIdSync(String userId);
