package org.unimelb.itime.test.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Property;
import org.unimelb.itime.vendor.listener.ITimeEventInterface;

import java.util.ArrayList;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by yinchuandong on 22/08/2016.
 */

@Entity
public class Event implements ITimeEventInterface<Event>{
    @Id(autoincrement = true)
    private Long id;
    private String title;
    @Property
    @NotNull
    private long startTime;
    @Property
    @NotNull
    private long endTime;
//    private ITimeEventInterface.Type eventType;
//    private ITimeEventInterface.Status status;
    @Property
    @NotNull
    private int eventType;
    @Property
    @NotNull
    private int status;

    public Event() {
    }

    @Generated(hash = 101606351)
    public Event(Long id, String title, long startTime, long endTime,
            int eventType, int status) {
        this.id = id;
        this.title = title;
        this.startTime = startTime;
        this.endTime = endTime;
        this.eventType = eventType;
        this.status = status;
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String getTitle() {
        return this.title;
    }

    @Override
    public void setProposedTimeSlots(ArrayList<Long> proposedTimeSlots) {

    }

    @Override
    public ArrayList<Long> getProposedTimeSlots() {
        return null;
    }

    public void setEventId(Long id){ this.id = id;}

    public void setStartTime(long startTime){ this.startTime = startTime; }

    public void setEndTime(long endTime){ this.endTime = endTime; }

    public Long getId(){ return id; }

    public long getStartTime(){return startTime;}

    public long getEndTime(){return endTime;}

    public void setEventType(int eventType) {
        this.eventType = eventType;
    }

    public int getEventType() {
        return eventType;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getDurationInMinute(){
        return (int)((endTime - startTime) /(1000*60));
    }

    @Override
    public int compareTo(Event event) {
        long selfStartTime = this.getStartTime();
        long cmpTgtStartTime = event.getStartTime();
        int result = selfStartTime < cmpTgtStartTime ? -1 : 1;

        if (result == -1){
            return result;
        }else {
            return selfStartTime == cmpTgtStartTime ? 0 : 1;
        }
    }

    public void setId(Long id) {
        this.id = id;
    }
}
