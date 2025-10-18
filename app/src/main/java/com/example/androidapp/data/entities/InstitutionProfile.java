package com.example.androidapp.data.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;
import com.example.androidapp.data.DateConverter;

import java.util.Date;

/**
 * كيان ملف تعريف المؤسسة - لإدارة ملفات المؤسسات والشركات
 */
@Entity(tableName = "institution_profiles")
@TypeConverters({DateConverter.class})
public class InstitutionProfile {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    private String id;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "display_name")
    private String displayName;

    @ColumnInfo(name = "description")
    private String description;

    @ColumnInfo(name = "logo_url")
    private String logoUrl;

    @ColumnInfo(name = "cover_image_url")
    private String coverImageUrl;

    @ColumnInfo(name = "institution_type")
    private String institutionType; // COMPANY, CLINIC, RETAIL, RESTAURANT, SERVICE, etc.

    @ColumnInfo(name = "business_category")
    private String businessCategory;

    @ColumnInfo(name = "registration_number")
    private String registrationNumber;

    @ColumnInfo(name = "tax_id")
    private String taxId;

    @ColumnInfo(name = "primary_email")
    private String primaryEmail;

    @ColumnInfo(name = "secondary_email")
    private String secondaryEmail;

    @ColumnInfo(name = "primary_phone")
    private String primaryPhone;

    @ColumnInfo(name = "secondary_phone")
    private String secondaryPhone;

    @ColumnInfo(name = "whatsapp_number")
    private String whatsappNumber;

    @ColumnInfo(name = "telegram_username")
    private String telegramUsername;

    @ColumnInfo(name = "website_url")
    private String websiteUrl;

    @ColumnInfo(name = "facebook_url")
    private String facebookUrl;

    @ColumnInfo(name = "instagram_url")
    private String instagramUrl;

    @ColumnInfo(name = "twitter_url")
    private String twitterUrl;

    @ColumnInfo(name = "linkedin_url")
    private String linkedinUrl;

    // Address Information
    @ColumnInfo(name = "address_line1")
    private String addressLine1;

    @ColumnInfo(name = "address_line2")
    private String addressLine2;

    @ColumnInfo(name = "city")
    private String city;

    @ColumnInfo(name = "state")
    private String state;

    @ColumnInfo(name = "postal_code")
    private String postalCode;

    @ColumnInfo(name = "country")
    private String country;

    @ColumnInfo(name = "latitude")
    private double latitude;

    @ColumnInfo(name = "longitude")
    private double longitude;

    // Business Hours
    @ColumnInfo(name = "business_hours")
    private String businessHours; // JSON string with weekly schedule

    @ColumnInfo(name = "timezone")
    private String timezone;

    // Rating and Reviews
    @ColumnInfo(name = "average_rating")
    private float averageRating;

    @ColumnInfo(name = "total_reviews")
    private int totalReviews;

    // Status and Verification
    @ColumnInfo(name = "is_verified")
    private boolean isVerified;

    @ColumnInfo(name = "verification_date")
    private Date verificationDate;

    @ColumnInfo(name = "is_active")
    private boolean isActive;

    @ColumnInfo(name = "subscription_type")
    private String subscriptionType; // FREE, BASIC, PREMIUM, ENTERPRISE

    @ColumnInfo(name = "subscription_expiry")
    private Date subscriptionExpiry;

    // Connection and Integration
    @ColumnInfo(name = "app_download_url")
    private String appDownloadUrl;

    @ColumnInfo(name = "referral_code")
    private String referralCode;

    @ColumnInfo(name = "total_referrals")
    private int totalReferrals;

    @ColumnInfo(name = "points_balance")
    private int pointsBalance;

    // Metadata
    @ColumnInfo(name = "settings")
    private String settings; // JSON string for various settings

    @ColumnInfo(name = "tags")
    private String tags; // JSON array of tags for searching

    @ColumnInfo(name = "created_at")
    private Date createdAt;

    @ColumnInfo(name = "updated_at")
    private Date updatedAt;

    // Constructors
    public InstitutionProfile() {
        this.createdAt = new Date();
        this.updatedAt = new Date();
        this.isActive = true;
        this.averageRating = 0.0f;
        this.totalReviews = 0;
        this.totalReferrals = 0;
        this.pointsBalance = 0;
    }

    public InstitutionProfile(@NonNull String id, String name, String institutionType) {
        this.id = id;
        this.name = name;
        this.institutionType = institutionType;
        this.createdAt = new Date();
        this.updatedAt = new Date();
        this.isActive = true;
        this.averageRating = 0.0f;
        this.totalReviews = 0;
        this.totalReferrals = 0;
        this.pointsBalance = 0;
    }

    // Getters and Setters
    @NonNull
    public String getId() { return id; }
    public void setId(@NonNull String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getLogoUrl() { return logoUrl; }
    public void setLogoUrl(String logoUrl) { this.logoUrl = logoUrl; }

    public String getCoverImageUrl() { return coverImageUrl; }
    public void setCoverImageUrl(String coverImageUrl) { this.coverImageUrl = coverImageUrl; }

    public String getInstitutionType() { return institutionType; }
    public void setInstitutionType(String institutionType) { this.institutionType = institutionType; }

    public String getBusinessCategory() { return businessCategory; }
    public void setBusinessCategory(String businessCategory) { this.businessCategory = businessCategory; }

    public String getRegistrationNumber() { return registrationNumber; }
    public void setRegistrationNumber(String registrationNumber) { this.registrationNumber = registrationNumber; }

    public String getTaxId() { return taxId; }
    public void setTaxId(String taxId) { this.taxId = taxId; }

    public String getPrimaryEmail() { return primaryEmail; }
    public void setPrimaryEmail(String primaryEmail) { this.primaryEmail = primaryEmail; }

    public String getSecondaryEmail() { return secondaryEmail; }
    public void setSecondaryEmail(String secondaryEmail) { this.secondaryEmail = secondaryEmail; }

    public String getPrimaryPhone() { return primaryPhone; }
    public void setPrimaryPhone(String primaryPhone) { this.primaryPhone = primaryPhone; }

    public String getSecondaryPhone() { return secondaryPhone; }
    public void setSecondaryPhone(String secondaryPhone) { this.secondaryPhone = secondaryPhone; }

    public String getWhatsappNumber() { return whatsappNumber; }
    public void setWhatsappNumber(String whatsappNumber) { this.whatsappNumber = whatsappNumber; }

    public String getTelegramUsername() { return telegramUsername; }
    public void setTelegramUsername(String telegramUsername) { this.telegramUsername = telegramUsername; }

    public String getWebsiteUrl() { return websiteUrl; }
    public void setWebsiteUrl(String websiteUrl) { this.websiteUrl = websiteUrl; }

    public String getFacebookUrl() { return facebookUrl; }
    public void setFacebookUrl(String facebookUrl) { this.facebookUrl = facebookUrl; }

    public String getInstagramUrl() { return instagramUrl; }
    public void setInstagramUrl(String instagramUrl) { this.instagramUrl = instagramUrl; }

    public String getTwitterUrl() { return twitterUrl; }
    public void setTwitterUrl(String twitterUrl) { this.twitterUrl = twitterUrl; }

    public String getLinkedinUrl() { return linkedinUrl; }
    public void setLinkedinUrl(String linkedinUrl) { this.linkedinUrl = linkedinUrl; }

    public String getAddressLine1() { return addressLine1; }
    public void setAddressLine1(String addressLine1) { this.addressLine1 = addressLine1; }

    public String getAddressLine2() { return addressLine2; }
    public void setAddressLine2(String addressLine2) { this.addressLine2 = addressLine2; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public String getBusinessHours() { return businessHours; }
    public void setBusinessHours(String businessHours) { this.businessHours = businessHours; }

    public String getTimezone() { return timezone; }
    public void setTimezone(String timezone) { this.timezone = timezone; }

    public float getAverageRating() { return averageRating; }
    public void setAverageRating(float averageRating) { this.averageRating = averageRating; }

    public int getTotalReviews() { return totalReviews; }
    public void setTotalReviews(int totalReviews) { this.totalReviews = totalReviews; }

    public boolean isVerified() { return isVerified; }
    public void setVerified(boolean verified) { isVerified = verified; }

    public Date getVerificationDate() { return verificationDate; }
    public void setVerificationDate(Date verificationDate) { this.verificationDate = verificationDate; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public String getSubscriptionType() { return subscriptionType; }
    public void setSubscriptionType(String subscriptionType) { this.subscriptionType = subscriptionType; }

    public Date getSubscriptionExpiry() { return subscriptionExpiry; }
    public void setSubscriptionExpiry(Date subscriptionExpiry) { this.subscriptionExpiry = subscriptionExpiry; }

    public String getAppDownloadUrl() { return appDownloadUrl; }
    public void setAppDownloadUrl(String appDownloadUrl) { this.appDownloadUrl = appDownloadUrl; }

    public String getReferralCode() { return referralCode; }
    public void setReferralCode(String referralCode) { this.referralCode = referralCode; }

    public int getTotalReferrals() { return totalReferrals; }
    public void setTotalReferrals(int totalReferrals) { this.totalReferrals = totalReferrals; }

    public int getPointsBalance() { return pointsBalance; }
    public void setPointsBalance(int pointsBalance) { this.pointsBalance = pointsBalance; }

    public String getSettings() { return settings; }
    public void setSettings(String settings) { this.settings = settings; }

    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
}
