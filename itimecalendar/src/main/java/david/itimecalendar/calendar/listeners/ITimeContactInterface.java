package david.itimecalendar.calendar.listeners;

import android.support.annotation.Nullable;

/**
 * Created by David Liu on 9/09/2016.
 * ${COMPANY}
 * lyhmelbourne@gmail.com
 */
public interface ITimeContactInterface {

    void setPhoto(String photo);
    @Nullable String getPhoto();

    void setName(String name);
    String getName();

    void setContactUid(String contactUid);
    String getContactUid();
}
