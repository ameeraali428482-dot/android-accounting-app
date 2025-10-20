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
 * كيان التنبيهات الذكية - لإدارة التنبيهات المتقدمة والذكية
 */
@Entity(tableName = "smart_notifications")
@TypeConverters({DateConverter.class})
public class SmartNotification {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    private String id;

    @ColumnInfo(name = "user_id")
    private String userId;

    @ColumnInfo(name = "notification_type")
    private String notificationType; // ORDER_REMINDER, EXPIRY_WARNING, STOCK_ALERT, PAYMENT_DUE, etc.

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "message")
    private String message;

    @ColumnInfo(name = "related_entity_id")
    private String relatedEntityId; // ID of related invoice, item, account, etc.

    @ColumnInfo(name = "related_entity_type")
    private String relatedEntityType; // INVOICE, ITEM, ACCOUNT, CUSTOMER, etc.

    @ColumnInfo(name = "priority")
    private String priority; // HIGH, MEDIUM, LOW

    @ColumnInfo(name = "trigger_date")
    private Date triggerDate;

    @ColumnInfo(name = "is_recurring")
    private boolean isRecurring;

    @ColumnInfo(name = "recurrence_pattern")
    private String recurrencePattern; // DAILY, WEEKLY, MONTHLY, YEARLY

    @ColumnInfo(name = "is_read")
    private boolean isRead;

    @ColumnInfo(name = "is_dismissed")
    private boolean isDismissed;

    @ColumnInfo(name = "action_url")
    private String actionUrl; // Deep link to relevant screen

    @ColumnInfo(name = "metadata")
    private String metadata; // JSON string for additional data

    @ColumnInfo(name = "created_at")
    private Date createdAt;

    @ColumnInfo(name = "updated_at")
    private Date updatedAt;

    // Constructors
    public SmartNotification() {
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    @Ignore
    public SmartNotification(@NonNull String id, String userId, String notificationType, 
                           String title, String message) {
        this.id = id;
        this.userId = userId;
        this.notificationType = notificationType;
        this.title = title;
        this.message = message;
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    // Getters and Setters
    @NonNull
    public String getId() { return id; }
    public void setId(@NonNull String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getNotificationType() { return notificationType; }
    public void setNotificationType(String notificationType) { this.notificationType = notificationType; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getRelatedEntityId() { return relatedEntityId; }
    public void setRelatedEntityId(String relatedEntityId) { this.relatedEntityId = relatedEntityId; }

    public String getRelatedEntityType() { return relatedEntityType; }
    public void setRelatedEntityType(String relatedEntityType) { this.relatedEntityType = relatedEntityType; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public Date getTriggerDate() { return triggerDate; }
    public void setTriggerDate(Date triggerDate) { this.triggerDate = triggerDate; }

    public boolean isRecurring() { return isRecurring; }
    public void setRecurring(boolean recurring) { isRecurring = recurring; }

    public String getRecurrencePattern() { return recurrencePattern; }
    public void setRecurrencePattern(String recurrencePattern) { this.recurrencePattern = recurrencePattern; }

    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }

    public boolean isDismissed() { return isDismissed; }
    public void setDismissed(boolean dismissed) { isDismissed = dismissed; }

    public String getActionUrl() { return actionUrl; }
    public void setActionUrl(String actionUrl) { this.actionUrl = actionUrl; }

    public String getMetadata() { return metadata; }
    public void setMetadata(String metadata) { this.metadata = metadata; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
}
