package org.unimelb.itime.vendor.listener;

import android.support.annotation.Nullable;

/**
 * Created by yuhaoliu on 10/09/2016.
 */
public interface ITimeInviteeInterface {

    void setPhoto(String photo);
    @Nullable
    String getPhoto();

    void setName(String name);
    String getName();

    void setInviteeUid(String inviteeUid);
    String getInviteeUid();
}
