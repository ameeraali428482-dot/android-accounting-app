package com.example.androidapp.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.example.androidapp.data.entities.BarcodeData;

import java.util.Date;
import java.util.List;

/**
 * DAO لبيانات الباركود - لإدارة العمليات على قاعدة البيانات لبيانات الباركود
 */
@Dao
public interface BarcodeDataDao {

    @Query("SELECT * FROM barcode_data ORDER BY created_at DESC")
    LiveData<List<BarcodeData>> getAllBarcodes();

    @Query("SELECT * FROM barcode_data WHERE is_active = 1 ORDER BY created_at DESC")
    LiveData<List<BarcodeData>> getActiveBarcodes();

    @Query("SELECT * FROM barcode_data WHERE created_by = :userId ORDER BY created_at DESC")
    LiveData<List<BarcodeData>> getBarcodesByUser(String userId);

    @Query("SELECT * FROM barcode_data WHERE barcode_type = :type ORDER BY created_at DESC")
    LiveData<List<BarcodeData>> getBarcodesByType(String type);

    @Query("SELECT * FROM barcode_data WHERE content_type = :contentType ORDER BY created_at DESC")
    LiveData<List<BarcodeData>> getBarcodesByContentType(String contentType);

    @Query("SELECT * FROM barcode_data WHERE related_entity_type = :entityType AND related_entity_id = :entityId")
    LiveData<List<BarcodeData>> getBarcodesByEntity(String entityType, String entityId);

    @Query("SELECT * FROM barcode_data WHERE barcode_value = :barcodeValue")
    LiveData<BarcodeData> getBarcodeByValue(String barcodeValue);

    @Query("SELECT * FROM barcode_data WHERE id = :id")
    LiveData<BarcodeData> getBarcodeById(String id);

    @Query("SELECT * FROM barcode_data WHERE expiry_date IS NOT NULL AND expiry_date <= :currentDate AND is_active = 1")
    List<BarcodeData> getExpiredBarcodes(Date currentDate);

    @Query("SELECT * FROM barcode_data WHERE expiry_date IS NOT NULL AND expiry_date BETWEEN :currentDate AND :warningDate AND is_active = 1")
    List<BarcodeData> getExpiringBarcodes(Date currentDate, Date warningDate);

    @Query("SELECT * FROM barcode_data WHERE scan_count >= :minScans ORDER BY scan_count DESC")
    LiveData<List<BarcodeData>> getPopularBarcodes(int minScans);

    @Query("SELECT * FROM barcode_data WHERE last_scanned BETWEEN :startDate AND :endDate ORDER BY last_scanned DESC")
    LiveData<List<BarcodeData>> getRecentlyScannedBarcodes(Date startDate, Date endDate);

    @Query("SELECT * FROM barcode_data WHERE (display_text LIKE '%' || :searchTerm || '%' OR description LIKE '%' || :searchTerm || '%') AND is_active = 1")
    LiveData<List<BarcodeData>> searchBarcodes(String searchTerm);

    @Query("SELECT COUNT(*) FROM barcode_data WHERE created_by = :userId AND is_active = 1")
    LiveData<Integer> getUserBarcodeCount(String userId);

    @Query("SELECT COUNT(*) FROM barcode_data WHERE barcode_type = :type AND is_active = 1")
    LiveData<Integer> getBarcodeCountByType(String type);

    @Query("SELECT COUNT(*) FROM barcode_data WHERE content_type = :contentType AND is_active = 1")
    LiveData<Integer> getBarcodeCountByContentType(String contentType);

    @Query("SELECT SUM(scan_count) FROM barcode_data WHERE created_by = :userId")
    LiveData<Integer> getTotalScansByUser(String userId);

    @Query("UPDATE barcode_data SET scan_count = scan_count + 1, last_scanned = :scannedTime, updated_at = :updatedAt WHERE id = :id")
    void incrementScanCount(String id, Date scannedTime, Date updatedAt);

    @Query("UPDATE barcode_data SET is_active = 0, updated_at = :updatedAt WHERE id = :id")
    void deactivateBarcode(String id, Date updatedAt);

    @Query("UPDATE barcode_data SET is_active = 1, updated_at = :updatedAt WHERE id = :id")
    void activateBarcode(String id, Date updatedAt);

    @Query("UPDATE barcode_data SET expiry_date = :expiryDate, updated_at = :updatedAt WHERE id = :id")
    void updateExpiryDate(String id, Date expiryDate, Date updatedAt);

    @Query("UPDATE barcode_data SET description = :description, updated_at = :updatedAt WHERE id = :id")
    void updateDescription(String id, String description, Date updatedAt);

    @Query("UPDATE barcode_data SET is_active = 0, updated_at = :updatedAt WHERE expiry_date IS NOT NULL AND expiry_date <= :currentDate")
    void deactivateExpiredBarcodes(Date currentDate, Date updatedAt);

    @Query("DELETE FROM barcode_data WHERE created_by = :userId")
    void deleteUserBarcodes(String userId);

    @Query("DELETE FROM barcode_data WHERE is_active = 0 AND updated_at < :cutoffDate")
    void deleteOldInactiveBarcodes(Date cutoffDate);

    @Insert
    void insert(BarcodeData barcodeData);

    @Insert
    void insertAll(List<BarcodeData> barcodes);

    @Update
    void update(BarcodeData barcodeData);

    @Delete
    void delete(BarcodeData barcodeData);

    @Query("DELETE FROM barcode_data WHERE id = :id")
    void deleteById(String id);

    // Advanced analytics queries
    @Query("SELECT barcode_type, COUNT(*) as count FROM barcode_data WHERE is_active = 1 GROUP BY barcode_type ORDER BY count DESC")
    LiveData<List<BarcodeTypeCount>> getBarcodeCountsByType();

    @Query("SELECT content_type, COUNT(*) as count FROM barcode_data WHERE is_active = 1 GROUP BY content_type ORDER BY count DESC")
    LiveData<List<BarcodeContentTypeCount>> getBarcodeCountsByContentType();

    @Query("SELECT created_by, COUNT(*) as count FROM barcode_data WHERE is_active = 1 GROUP BY created_by ORDER BY count DESC LIMIT 10")
    LiveData<List<BarcodeUserCount>> getTopBarcodeCreators();

    @Query("SELECT DATE(created_at) as date, COUNT(*) as count FROM barcode_data WHERE created_at >= :startDate GROUP BY DATE(created_at) ORDER BY date DESC")
    LiveData<List<BarcodeDailyCount>> getDailyBarcodeCreations(Date startDate);

    @Query("SELECT DATE(last_scanned) as date, SUM(scan_count) as total_scans FROM barcode_data WHERE last_scanned >= :startDate GROUP BY DATE(last_scanned) ORDER BY date DESC")
    LiveData<List<BarcodeDailyScans>> getDailyBarcodeScans(Date startDate);

    // Helper classes for query results
    class BarcodeTypeCount {
        public String barcode_type;
        public int count;
    }

    class BarcodeContentTypeCount {
        public String content_type;
        public int count;
    }

    class BarcodeUserCount {
        public String created_by;
        public int count;
    }

    class BarcodeDailyCount {
        public String date;
        public int count;
    }

    class BarcodeDailyScans {
        public String date;
        public int total_scans;
    }
}
