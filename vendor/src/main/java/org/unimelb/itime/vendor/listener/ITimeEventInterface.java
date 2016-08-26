package org.unimelb.itime.vendor.listener;

import java.net.URL;
import java.util.ArrayList;

/**
 * Created by yinchuandong on 22/08/2016.
 */
public interface ITimeEventInterface {

    String getEventId();

    void setTitle(String title);
    String getTitle();

    void setNote(String note);
    String getNote();

    void setLocationAddress(String adress);
    String getLocationAddress();

    void setLocationNote(String locationNote);
    String getLocationNote();

    void setLocationLatitude(double latitude);
    Double getLocationLatitude();

    void setLocationLongitude(double longitude);
    Double getLocationLongitude();

    void setRepeatTypeId(int repeatTypeId);
    int getRepeatTypeId();

    void setUserId(String userId);
    String getUserId();

    void setAlertTimeId(int alertTimeId);
    int getAlertTimeId();

    void setEventTypeId(int eventTypeId);
    int getEventTypeId();

    void setVisibilityTypeId(int visibilityTypeId);
    int getVisibilityTypeId();

    void setEventSourceId(int eventSourceId);
    int getEventSourceId();

    void setCalendarTypeId(String calendarTypeId);
    String getCalendarTypeId();

    void setIsInfiniteRepeat(Boolean isInfiniteRepeat);
    Boolean getIsInfiniteRepeat();

    void setIsDeleted(Boolean isDeleted);
    Boolean getIsDeleted();

    void setRepeatEndsTime(Integer repeatEndsTime);
    Integer getRepeatEndsTime();

    void setHostEventId(String hostEventId);
    String getHostEventId();

    void setUserStatusId(int userStatusId);
    int getUserStatusId();

    void setEventPhotos(ArrayList<String> urls);
    ArrayList<String> getEventPhotos();

    void setUrl(String url);
    String getUrl();

    void setDuration(int duration);
    int getDuration();

    void setAttendees(ArrayList<String> userId);
    ArrayList<String> getAttendees();

    void setProposedTimeSlots(ArrayList<Long> proposedTimeSlots);
    ArrayList<Long> getProposedTimeSlots();
}
