package org.unimelb.itime.vendor.listener;

import android.support.annotation.Nullable;

/**
 * Created by yuhaoliu on 9/09/2016.
 */
public interface ITimeContactInterface {

    void setProfilePhotoUrl(String profilePhotoUrl);
    @Nullable String getProfilePhotoUrl();

    void setName(String name);
    String getName();

    void setId(String id);
    String getId();
}
