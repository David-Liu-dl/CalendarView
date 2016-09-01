package org.unimelb.itime.vendor.listener;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yinchuandong on 22/08/2016.
 */
public interface ITimeEventInterface<T> extends Comparable<T> {

    void setTitle(String title);
    String getTitle();

    void setStartTime(long startTime);
    long getStartTime();

    void setEndTime(long endTime);
    long getEndTime();

    void setEventType(int typeId);
    int getEventType();

    void setStatus(int statusId);
    int getStatus();

    void setLocation(String location);
    String getLocation();

    void setProposedTimeSlots(ArrayList<Long> proposedTimeSlots);
    ArrayList<Long> getProposedTimeSlots();

    String getInvitees_urls();

    void setInvitees_urls(String invitees_urls);

}
