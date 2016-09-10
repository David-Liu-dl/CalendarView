package org.unimelb.itime.vendor.listener;

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

    /**
     * Note: Display status:
     *  int[0] = color, Color.parse("#xxxxx")
     *  int[1] = status
     * @return int[] size=2
     */
//    int[] getDisplayStatus();

    void setLocation(String location);
    String getLocation();

    void setProposedTimeSlots(List<Long> proposedTimeSlots);
    List<Long> getProposedTimeSlots();

    String getInviteesUrls();

    void setInviteesUrls(String inviteesUrls);

}
