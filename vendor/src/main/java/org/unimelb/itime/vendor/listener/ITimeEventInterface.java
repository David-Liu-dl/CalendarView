package org.unimelb.itime.vendor.listener;

import java.util.ArrayList;

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


    void setProposedTimeSlots(ArrayList<Long> proposedTimeSlots);
    ArrayList<Long> getProposedTimeSlots();


}
