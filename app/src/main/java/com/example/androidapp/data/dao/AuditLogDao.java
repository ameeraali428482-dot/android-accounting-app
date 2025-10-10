package com.example.androidapp.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;
import java.util.List;
import com.example.androidapp.data.entities.AuditLog;




@Dao
public interface AuditLogDao {
    @Insert
    void insert(AuditLog auditlog);

    @Update
    void update(AuditLog auditlog);

    @Delete
    void delete(AuditLog auditlog);

    @Query("SELECT * FROM audit_logs")
    List<AuditLog> getAllAuditLogs();

    @Query("SELECT * FROM audit_logs WHERE id = :id LIMIT 1")
    AuditLog getAuditLogById(String id);
}
