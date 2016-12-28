package org.unimelb.itime.vendor.listener;

import android.support.annotation.Nullable;

/**
 * Created by yuhaoliu on 10/09/2016.
 */
public interface ITimeInviteeInterface {

    @Nullable
    String getPhoto();

    String getName();

    String getInviteeUid();

    String getUserUid();

    String getUserId();

    /**
     *
     * @return {activated, unactivated}
     */
    String getUserStatus();
}
