package org.unimelb.itime.test.paul;

import org.unimelb.itime.vendor.listener.ITimeEventInterface;
import org.unimelb.itime.vendor.listener.ITimeInviteeInterface;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Paul on 28/08/2016.
 */
public class TestEvent implements ITimeEventInterface {
    private String title;
    private long startTime;
    private long endTime;
    private int eventType;
    private int eventStatus;

    public TestEvent() {
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    @Override
    public long getStartTime() {
        return startTime;
    }

    @Override
    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    @Override
    public long getEndTime() {
        return endTime;
    }

    @Override
    public void setEventType(int typeId) {
        this.eventType = typeId;
    }

    @Override
    public int getEventType() {
        return eventType;
    }

    @Override
    public void setStatus(int statusId) {
        this.eventStatus = statusId;
    }

    @Override
    public int getStatus() {
        return eventStatus;
    }

    @Override
    public void setLocation(String location) {
    }

    @Override
    public String getLocation() {
        return null;
    }

    @Override
    public List<? extends ITimeInviteeInterface> getDisplayInvitee() {
        return null;
    }

    @Override
    public int compareTo(Object o) {
        return 0;
    }
}
