package org.unimelb.itime.vendor.listener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yinchuandong on 22/08/2016.
 */
public interface ITimeEventInterface<T> extends Comparable<T> {
    String getEventUid();

    void setTitle(String title);
    String getTitle();

    void setStartTime(long startTime);
    long getStartTime();

    void setEndTime(long endTime);
    long getEndTime();

//    void setDisplayEventType(int typeId);
    int getDisplayEventType();

//    void setDisplayStatus(int statusId);
    String getDisplayStatus();

    /**
     * Note: Display status:
     *  int[0] = color, Color.parse("#xxxxx")
     *  int[1] = status
     * @return int[] size=2
     */
//    int[] getDisplayStatus();

    void setLocation(String location);
    String getLocation();

    List<? extends ITimeInviteeInterface> getDisplayInvitee();
}
