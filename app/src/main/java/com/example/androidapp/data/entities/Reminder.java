package com.example.androidapp.data.entities;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "reminders",
        foreignKeys = {
                @ForeignKey(entity = Company.class,
                           parentColumns = "id",
                           childColumns = "companyId",
                           onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = User.class,
                           parentColumns = "id",
                           childColumns = "assignedToId",
                           onDelete = ForeignKey.SET_NULL)
        },
        indices = {@Index(value = "companyId"), @Index(value = "assignedToId")})
public class Reminder {
    @PrimaryKey
    private String id;
    private String companyId;
    private String title;
    private String description;
    private String dueDate;
    private String priority;
    private boolean isCompleted;
    private boolean isExecuted;
    private String actionType;
    private String actionPayload; // JSON string
    private String assignedToId;
    private String createdAt;

    public Reminder(String id, String companyId, String title, String description, String dueDate, String priority, boolean isCompleted, boolean isExecuted, String actionType, String actionPayload, String assignedToId, String createdAt) {
        this.id = id;
        this.companyId = companyId;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.priority = priority;
        this.isCompleted = isCompleted;
        this.isExecuted = isExecuted;
        this.actionType = actionType;
        this.actionPayload = actionPayload;
        this.assignedToId = assignedToId;
        this.createdAt = createdAt;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getCompanyId() {
        return companyId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getDueDate() {
        return dueDate;
    }

    public String getPriority() {
        return priority;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public boolean isExecuted() {
        return isExecuted;
    }

    public String getActionType() {
        return actionType;
    }

    public String getActionPayload() {
        return actionPayload;
    }

    public String getAssignedToId() {
        return assignedToId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public void setExecuted(boolean executed) {
        isExecuted = executed;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public void setActionPayload(String actionPayload) {
        this.actionPayload = actionPayload;
    }

    public void setAssignedToId(String assignedToId) {
        this.assignedToId = assignedToId;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}

