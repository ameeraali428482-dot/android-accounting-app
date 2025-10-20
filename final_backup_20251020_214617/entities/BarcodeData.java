package com.example.androidapp.data.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;
import com.example.androidapp.data.DateConverter;

import java.util.Date;

/**
 * كيان بيانات الباركود - لإدارة بيانات الباركود ورموز QR
 */
@Entity(tableName = "barcode_data")
@TypeConverters({DateConverter.class})
public class BarcodeData {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    private String id;

    @ColumnInfo(name = "barcode_value")
    private String barcodeValue; // The actual barcode string

    @ColumnInfo(name = "barcode_type")
    private String barcodeType; // QR_CODE, CODE_128, EAN_13, etc.

    @ColumnInfo(name = "content_type")
    private String contentType; // INVOICE, ITEM, CUSTOMER, SUPPLIER, GENERAL

    @ColumnInfo(name = "related_entity_id")
    private String relatedEntityId; // ID of the related entity

    @ColumnInfo(name = "related_entity_type")
    private String relatedEntityType; // INVOICE, ITEM, CUSTOMER, etc.

    @ColumnInfo(name = "encoded_data")
    private String encodedData; // JSON string with the full data

    @ColumnInfo(name = "display_text")
    private String displayText; // Human readable text

    @ColumnInfo(name = "image_path")
    private String imagePath; // Path to generated barcode image

    @ColumnInfo(name = "format_settings")
    private String formatSettings; // JSON with barcode generation settings

    @ColumnInfo(name = "width")
    private int width; // Barcode image width

    @ColumnInfo(name = "height")
    private int height; // Barcode image height

    @ColumnInfo(name = "is_active")
    private boolean isActive;

    @ColumnInfo(name = "scan_count")
    private int scanCount; // Number of times this barcode was scanned

    @ColumnInfo(name = "last_scanned")
    private Date lastScanned;

    @ColumnInfo(name = "expiry_date")
    private Date expiryDate; // Optional expiry for temporary barcodes

    @ColumnInfo(name = "created_by")
    private String createdBy; // User ID who created this barcode

    @ColumnInfo(name = "description")
    private String description;

    @ColumnInfo(name = "tags")
    private String tags; // JSON array of tags for searching

    @ColumnInfo(name = "version")
    private int version; // For data versioning

    @ColumnInfo(name = "encryption_key")
    private String encryptionKey; // For encrypted barcodes

    @ColumnInfo(name = "access_permissions")
    private String accessPermissions; // JSON with access control settings

    @ColumnInfo(name = "created_at")
    private Date createdAt;

    @ColumnInfo(name = "updated_at")
    private Date updatedAt;

    // Constructors
    public BarcodeData() {
        this.createdAt = new Date();
        this.updatedAt = new Date();
        this.isActive = true;
        this.scanCount = 0;
        this.version = 1;
    }

    @Ignore
    public BarcodeData(@NonNull String id, String barcodeValue, String barcodeType, String contentType) {
        this.id = id;
        this.barcodeValue = barcodeValue;
        this.barcodeType = barcodeType;
        this.contentType = contentType;
        this.createdAt = new Date();
        this.updatedAt = new Date();
        this.isActive = true;
        this.scanCount = 0;
        this.version = 1;
    }

    // Getters and Setters
    @NonNull
    public String getId() { return id; }
    public void setId(@NonNull String id) { this.id = id; }

    public String getBarcodeValue() { return barcodeValue; }
    public void setBarcodeValue(String barcodeValue) { this.barcodeValue = barcodeValue; }

    public String getBarcodeType() { return barcodeType; }
    public void setBarcodeType(String barcodeType) { this.barcodeType = barcodeType; }

    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }

    public String getRelatedEntityId() { return relatedEntityId; }
    public void setRelatedEntityId(String relatedEntityId) { this.relatedEntityId = relatedEntityId; }

    public String getRelatedEntityType() { return relatedEntityType; }
    public void setRelatedEntityType(String relatedEntityType) { this.relatedEntityType = relatedEntityType; }

    public String getEncodedData() { return encodedData; }
    public void setEncodedData(String encodedData) { this.encodedData = encodedData; }

    public String getDisplayText() { return displayText; }
    public void setDisplayText(String displayText) { this.displayText = displayText; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    public String getFormatSettings() { return formatSettings; }
    public void setFormatSettings(String formatSettings) { this.formatSettings = formatSettings; }

    public int getWidth() { return width; }
    public void setWidth(int width) { this.width = width; }

    public int getHeight() { return height; }
    public void setHeight(int height) { this.height = height; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public int getScanCount() { return scanCount; }
    public void setScanCount(int scanCount) { this.scanCount = scanCount; }

    public Date getLastScanned() { return lastScanned; }
    public void setLastScanned(Date lastScanned) { this.lastScanned = lastScanned; }

    public Date getExpiryDate() { return expiryDate; }
    public void setExpiryDate(Date expiryDate) { this.expiryDate = expiryDate; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }

    public int getVersion() { return version; }
    public void setVersion(int version) { this.version = version; }

    public String getEncryptionKey() { return encryptionKey; }
    public void setEncryptionKey(String encryptionKey) { this.encryptionKey = encryptionKey; }

    public String getAccessPermissions() { return accessPermissions; }
    public void setAccessPermissions(String accessPermissions) { this.accessPermissions = accessPermissions; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
}
