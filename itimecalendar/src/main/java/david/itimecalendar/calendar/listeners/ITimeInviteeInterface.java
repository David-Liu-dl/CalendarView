package david.itimecalendar.calendar.listeners;

import android.support.annotation.Nullable;

/**
 * Created by David Liu on 10/09/2016.
 * ${COMPANY}
 * lyhmelbourne@gmail.com
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
