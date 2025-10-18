package com.example.androidapp.data.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import java.util.Date;

@Entity(tableName = "reminders",
        foreignKeys = {
                @ForeignKey(entity = Company.class, parentColumns = "id", childColumns = "companyId", onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = User.class, parentColumns = "id", childColumns = "userId", onDelete = ForeignKey.SET_NULL)
        },
        indices = {@Index(value = "companyId"), @Index(value = "userId")})
public class Reminder {
    @PrimaryKey
    @NonNull
    private String id;
    private String companyId;
    private String userId;
    private String title;
    private String description;
    private Date reminderDateTime;
    private boolean isActive;
    private String notificationType;

    public Reminder(@NonNull String id, String companyId, String userId, String title, String description, Date reminderDateTime, boolean isActive, String notificationType) {
        this.id = id;
        this.companyId = companyId;
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.reminderDateTime = reminderDateTime;
        this.isActive = isActive;
        this.notificationType = notificationType;
    }

    @Ignore
    public Reminder() {}

    // Getters
    @NonNull
    public String getId() { return id; }
    public String getCompanyId() { return companyId; }
    public String getUserId() { return userId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public Date getReminderDateTime() { return reminderDateTime; }
    public boolean isActive() { return isActive; }
    public String getNotificationType() { return notificationType; }

    // Setters
    public void setId(@NonNull String id) { this.id = id; }
    public void setCompanyId(String companyId) { this.companyId = companyId; }
    public void setUserId(String userId) { this.userId = userId; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setReminderDateTime(Date reminderDateTime) { this.reminderDateTime = reminderDateTime; }
    public void setActive(boolean active) { isActive = active; }
    public void setNotificationType(String notificationType) { this.notificationType = notificationType; }
}
