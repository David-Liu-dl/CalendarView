package org.unimelb.itime.vendor.contact.widgets;

import android.support.annotation.Nullable;

/**
 * Created by yuhaoliu on 17/08/16.
 */
public class Contact {
    String profile_photo_URL;
    String name;

    public Contact(@Nullable String profile_photo_URL, String name) {
        if (profile_photo_URL != null){
            this.profile_photo_URL = profile_photo_URL;
        }
        this.name = name;
    }

    public String getUrl(){
        return this.profile_photo_URL;
    }

    public String getName(){
        return this.name;
    }
}
