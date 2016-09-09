package org.unimelb.itime.test.bean;

import android.support.annotation.Nullable;

import org.unimelb.itime.vendor.listener.ITimeContactInterface;

/**
 * Created by yuhaoliu on 17/08/16.
 */
public class Contact implements ITimeContactInterface {
    String id;
    String profile_photo_URL = null;
    String name;

    public Contact(@Nullable String profile_photo_URL, String name, String id) {
        if (profile_photo_URL != null){
            this.profile_photo_URL = profile_photo_URL;
        }
        this.id = id;
        this.name = name;
    }

    @Override
    public void setProfilePhotoUrl(String profilePhotoUrl) {
        this.profile_photo_URL = profilePhotoUrl;
    }

    @Override
    public String getProfilePhotoUrl() {
        return this.profile_photo_URL;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName(){
        return this.name;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return this.id;
    }
}
