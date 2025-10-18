package com.example.androidapp.data.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;
import com.example.androidapp.data.DateConverter;

import java.util.Date;

/**
 * كيان الإشعارات الخارجية - لإدارة الإشعارات عبر واتساب/تلجرام/SMS
 */
@Entity(tableName = "external_notifications")
@TypeConverters({DateConverter.class})
public class ExternalNotification {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    private String id;

    @ColumnInfo(name = "user_id")
    private String userId;

    @ColumnInfo(name = "recipient_id")
    private String recipientId; // Target user/contact ID

    @ColumnInfo(name = "notification_channel")
    private String notificationChannel; // WHATSAPP, TELEGRAM, SMS, EMAIL

    @ColumnInfo(name = "recipient_contact")
    private String recipientContact; // Phone number, email, username

    @ColumnInfo(name = "message_type")
    private String messageType; // TEXT, IMAGE, DOCUMENT, INVOICE, REMINDER

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "message")
    private String message;

    @ColumnInfo(name = "formatted_message")
    private String formattedMessage; // Message with platform-specific formatting

    @ColumnInfo(name = "attachment_url")
    private String attachmentUrl; // URL to attached file/image

    @ColumnInfo(name = "attachment_type")
    private String attachmentType; // IMAGE, PDF, EXCEL, etc.

    @ColumnInfo(name = "template_id")
    private String templateId; // ID of message template used

    @ColumnInfo(name = "related_entity_id")
    private String relatedEntityId; // Related invoice, payment, etc.

    @ColumnInfo(name = "related_entity_type")
    private String relatedEntityType; // INVOICE, PAYMENT, REMINDER, etc.

    @ColumnInfo(name = "priority")
    private String priority; // HIGH, MEDIUM, LOW

    @ColumnInfo(name = "status")
    private String status; // PENDING, SENT, DELIVERED, READ, FAILED

    @ColumnInfo(name = "scheduled_time")
    private Date scheduledTime; // When to send (for scheduled messages)

    @ColumnInfo(name = "sent_time")
    private Date sentTime;

    @ColumnInfo(name = "delivered_time")
    private Date deliveredTime;

    @ColumnInfo(name = "read_time")
    private Date readTime;

    @ColumnInfo(name = "retry_count")
    private int retryCount;

    @ColumnInfo(name = "max_retries")
    private int maxRetries;

    @ColumnInfo(name = "error_message")
    private String errorMessage;

    @ColumnInfo(name = "external_message_id")
    private String externalMessageId; // ID from external service

    @ColumnInfo(name = "cost")
    private double cost; // Cost of sending this notification

    @ColumnInfo(name = "currency")
    private String currency;

    @ColumnInfo(name = "api_response")
    private String apiResponse; // Full API response from external service

    @ColumnInfo(name = "metadata")
    private String metadata; // Additional JSON metadata

    @ColumnInfo(name = "is_bulk")
    private boolean isBulk; // Part of bulk sending

    @ColumnInfo(name = "bulk_id")
    private String bulkId; // ID of bulk operation

    @ColumnInfo(name = "created_at")
    private Date createdAt;

    @ColumnInfo(name = "updated_at")
    private Date updatedAt;

    // Constructors
    public ExternalNotification() {
        this.createdAt = new Date();
        this.updatedAt = new Date();
        this.status = "PENDING";
        this.retryCount = 0;
        this.maxRetries = 3;
    }

    public ExternalNotification(@NonNull String id, String userId, String recipientId,
                               String notificationChannel, String recipientContact,
                               String title, String message) {
        this.id = id;
        this.userId = userId;
        this.recipientId = recipientId;
        this.notificationChannel = notificationChannel;
        this.recipientContact = recipientContact;
        this.title = title;
        this.message = message;
        this.createdAt = new Date();
        this.updatedAt = new Date();
        this.status = "PENDING";
        this.retryCount = 0;
        this.maxRetries = 3;
    }

    // Getters and Setters
    @NonNull
    public String getId() { return id; }
    public void setId(@NonNull String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getRecipientId() { return recipientId; }
    public void setRecipientId(String recipientId) { this.recipientId = recipientId; }

    public String getNotificationChannel() { return notificationChannel; }
    public void setNotificationChannel(String notificationChannel) { this.notificationChannel = notificationChannel; }

    public String getRecipientContact() { return recipientContact; }
    public void setRecipientContact(String recipientContact) { this.recipientContact = recipientContact; }

    public String getMessageType() { return messageType; }
    public void setMessageType(String messageType) { this.messageType = messageType; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getFormattedMessage() { return formattedMessage; }
    public void setFormattedMessage(String formattedMessage) { this.formattedMessage = formattedMessage; }

    public String getAttachmentUrl() { return attachmentUrl; }
    public void setAttachmentUrl(String attachmentUrl) { this.attachmentUrl = attachmentUrl; }

    public String getAttachmentType() { return attachmentType; }
    public void setAttachmentType(String attachmentType) { this.attachmentType = attachmentType; }

    public String getTemplateId() { return templateId; }
    public void setTemplateId(String templateId) { this.templateId = templateId; }

    public String getRelatedEntityId() { return relatedEntityId; }
    public void setRelatedEntityId(String relatedEntityId) { this.relatedEntityId = relatedEntityId; }

    public String getRelatedEntityType() { return relatedEntityType; }
    public void setRelatedEntityType(String relatedEntityType) { this.relatedEntityType = relatedEntityType; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Date getScheduledTime() { return scheduledTime; }
    public void setScheduledTime(Date scheduledTime) { this.scheduledTime = scheduledTime; }

    public Date getSentTime() { return sentTime; }
    public void setSentTime(Date sentTime) { this.sentTime = sentTime; }

    public Date getDeliveredTime() { return deliveredTime; }
    public void setDeliveredTime(Date deliveredTime) { this.deliveredTime = deliveredTime; }

    public Date getReadTime() { return readTime; }
    public void setReadTime(Date readTime) { this.readTime = readTime; }

    public int getRetryCount() { return retryCount; }
    public void setRetryCount(int retryCount) { this.retryCount = retryCount; }

    public int getMaxRetries() { return maxRetries; }
    public void setMaxRetries(int maxRetries) { this.maxRetries = maxRetries; }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

    public String getExternalMessageId() { return externalMessageId; }
    public void setExternalMessageId(String externalMessageId) { this.externalMessageId = externalMessageId; }

    public double getCost() { return cost; }
    public void setCost(double cost) { this.cost = cost; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getApiResponse() { return apiResponse; }
    public void setApiResponse(String apiResponse) { this.apiResponse = apiResponse; }

    public String getMetadata() { return metadata; }
    public void setMetadata(String metadata) { this.metadata = metadata; }

    public boolean isBulk() { return isBulk; }
    public void setBulk(boolean bulk) { isBulk = bulk; }

    public String getBulkId() { return bulkId; }
    public void setBulkId(String bulkId) { this.bulkId = bulkId; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
}
