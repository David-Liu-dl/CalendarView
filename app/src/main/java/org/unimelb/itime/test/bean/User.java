package org.unimelb.itime.test.bean;


import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;

import java.io.Serializable;

/**
 * Created by yinchuandong on 20/06/2016.
 */

@Entity(
        active = false
)
public class User implements Serializable {

    private String id;
    private String userId;
    private String personalAlias;
    private String bindingEmail;
    private String bindingFacebookId;
    private String bindingPhone;
    private String profilePhotoUrl;
    private long defaultEventAlertTimeId;
    private String deviceToken;
    private String deviceId;
    private String defaultRatingVisibilityTypeId;
    private String defaultEventVisibilityTypeId;
    private String ifAcceptPublicEventPush;
    private String averageRatingValue;
    private String createdAt;
    private String updatedAt;
    @Generated(hash = 205950960)
    public User(String id, String userId, String personalAlias, String bindingEmail,
                String bindingFacebookId, String bindingPhone, String profilePhotoUrl,
                long defaultEventAlertTimeId, String deviceToken, String deviceId,
                String defaultRatingVisibilityTypeId, String defaultEventVisibilityTypeId,
                String ifAcceptPublicEventPush, String averageRatingValue, String createdAt,
                String updatedAt) {
        this.id = id;
        this.userId = userId;
        this.personalAlias = personalAlias;
        this.bindingEmail = bindingEmail;
        this.bindingFacebookId = bindingFacebookId;
        this.bindingPhone = bindingPhone;
        this.profilePhotoUrl = profilePhotoUrl;
        this.defaultEventAlertTimeId = defaultEventAlertTimeId;
        this.deviceToken = deviceToken;
        this.deviceId = deviceId;
        this.defaultRatingVisibilityTypeId = defaultRatingVisibilityTypeId;
        this.defaultEventVisibilityTypeId = defaultEventVisibilityTypeId;
        this.ifAcceptPublicEventPush = ifAcceptPublicEventPush;
        this.averageRatingValue = averageRatingValue;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    @Generated(hash = 586692638)
    public User() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPersonalAlias() {
        return personalAlias;
    }

    public void setPersonalAlias(String personalAlias) {
        this.personalAlias = personalAlias;
    }

    public String getBindingEmail() {
        return bindingEmail;
    }

    public void setBindingEmail(String bindingEmail) {
        this.bindingEmail = bindingEmail;
    }

    public String getBindingFacebookId() {
        return bindingFacebookId;
    }

    public void setBindingFacebookId(String bindingFacebookId) {
        this.bindingFacebookId = bindingFacebookId;
    }

    public String getBindingPhone() {
        return bindingPhone;
    }

    public void setBindingPhone(String bindingPhone) {
        this.bindingPhone = bindingPhone;
    }

    public String getProfilePhotoUrl() {
        return profilePhotoUrl;
    }

    public void setProfilePhotoUrl(String profilePhotoUrl) {
        this.profilePhotoUrl = profilePhotoUrl;
    }

    public long getDefaultEventAlertTimeId() {
        return defaultEventAlertTimeId;
    }

    public void setDefaultEventAlertTimeId(long defaultEventAlertTimeId) {
        this.defaultEventAlertTimeId = defaultEventAlertTimeId;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDefaultRatingVisibilityTypeId() {
        return defaultRatingVisibilityTypeId;
    }

    public void setDefaultRatingVisibilityTypeId(String defaultRatingVisibilityTypeId) {
        this.defaultRatingVisibilityTypeId = defaultRatingVisibilityTypeId;
    }

    public String getDefaultEventVisibilityTypeId() {
        return defaultEventVisibilityTypeId;
    }

    public void setDefaultEventVisibilityTypeId(String defaultEventVisibilityTypeId) {
        this.defaultEventVisibilityTypeId = defaultEventVisibilityTypeId;
    }

    public String getIfAcceptPublicEventPush() {
        return ifAcceptPublicEventPush;
    }

    public void setIfAcceptPublicEventPush(String ifAcceptPublicEventPush) {
        this.ifAcceptPublicEventPush = ifAcceptPublicEventPush;
    }

    public String getAverageRatingValue() {
        return averageRatingValue;
    }

    public void setAverageRatingValue(String averageRatingValue) {
        this.averageRatingValue = averageRatingValue;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}


