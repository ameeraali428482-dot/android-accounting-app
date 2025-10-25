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
 * كيان التذكيرات الدورية - لإدارة التذكيرات المتكررة والذكية
 */
@Entity(tableName = "periodic_reminders")
@TypeConverters({DateConverter.class})
public class PeriodicReminder {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    private String id;

    @ColumnInfo(name = "user_id")
    private String userId;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "description")
    private String description;

    @ColumnInfo(name = "reminder_type")
    private String reminderType; // INVOICE_DUE, STOCK_CHECK, BACKUP_REMINDER, PAYMENT_FOLLOW, etc.

    @ColumnInfo(name = "frequency")
    private String frequency; // DAILY, WEEKLY, MONTHLY, YEARLY, CUSTOM

    @ColumnInfo(name = "frequency_value")
    private int frequencyValue; // For custom frequency (e.g., every 3 days)

    @ColumnInfo(name = "start_date")
    private Date startDate;

    @ColumnInfo(name = "end_date")
    private Date endDate;

    @ColumnInfo(name = "next_trigger")
    private Date nextTrigger;

    @ColumnInfo(name = "last_triggered")
    private Date lastTriggered;

    @ColumnInfo(name = "trigger_time")
    private String triggerTime; // HH:MM format

    @ColumnInfo(name = "is_active")
    private boolean isActive;

    @ColumnInfo(name = "notification_channels")
    private String notificationChannels; // JSON array: ["APP", "SMS", "EMAIL", "WHATSAPP", "TELEGRAM"]

    @ColumnInfo(name = "target_contacts")
    private String targetContacts; // JSON array of contact IDs

    @ColumnInfo(name = "custom_message")
    private String customMessage;

    @ColumnInfo(name = "action_required")
    private boolean actionRequired;

    @ColumnInfo(name = "action_type")
    private String actionType; // OPEN_SCREEN, EXECUTE_FUNCTION, EXTERNAL_URL

    @ColumnInfo(name = "action_data")
    private String actionData; // JSON string with action parameters

    @ColumnInfo(name = "created_at")
    private Date createdAt;

    @ColumnInfo(name = "updated_at")
    private Date updatedAt;

    // Constructors
    public PeriodicReminder() {
        this.createdAt = new Date();
        this.updatedAt = new Date();
        this.isActive = true;
    }

    @Ignore
    public PeriodicReminder(@NonNull String id, String userId, String title, String reminderType, String frequency) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.reminderType = reminderType;
        this.frequency = frequency;
        this.createdAt = new Date();
        this.updatedAt = new Date();
        this.isActive = true;
    }

    // Getters and Setters
    @NonNull
    public String getId() { return id; }
    public void setId(@NonNull String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getReminderType() { return reminderType; }
    public void setReminderType(String reminderType) { this.reminderType = reminderType; }

    public String getFrequency() { return frequency; }
    public void setFrequency(String frequency) { this.frequency = frequency; }

    public int getFrequencyValue() { return frequencyValue; }
    public void setFrequencyValue(int frequencyValue) { this.frequencyValue = frequencyValue; }

    public Date getStartDate() { return startDate; }
    public void setStartDate(Date startDate) { this.startDate = startDate; }

    public Date getEndDate() { return endDate; }
    public void setEndDate(Date endDate) { this.endDate = endDate; }

    public Date getNextTrigger() { return nextTrigger; }
    public void setNextTrigger(Date nextTrigger) { this.nextTrigger = nextTrigger; }

    public Date getLastTriggered() { return lastTriggered; }
    public void setLastTriggered(Date lastTriggered) { this.lastTriggered = lastTriggered; }

    public String getTriggerTime() { return triggerTime; }
    public void setTriggerTime(String triggerTime) { this.triggerTime = triggerTime; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public String getNotificationChannels() { return notificationChannels; }
    public void setNotificationChannels(String notificationChannels) { this.notificationChannels = notificationChannels; }

    public String getTargetContacts() { return targetContacts; }
    public void setTargetContacts(String targetContacts) { this.targetContacts = targetContacts; }

    public String getCustomMessage() { return customMessage; }
    public void setCustomMessage(String customMessage) { this.customMessage = customMessage; }

    public boolean isActionRequired() { return actionRequired; }
    public void setActionRequired(boolean actionRequired) { this.actionRequired = actionRequired; }

    public String getActionType() { return actionType; }
    public void setActionType(String actionType) { this.actionType = actionType; }

    public String getActionData() { return actionData; }
    public void setActionData(String actionData) { this.actionData = actionData; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
}
