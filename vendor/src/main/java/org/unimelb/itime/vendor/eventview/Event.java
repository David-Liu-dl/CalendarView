package org.unimelb.itime.vendor.eventview;

/**
 * Created by Paul on 1/08/2016.
 */
public class Event implements Comparable<Event> {

    private int id;
    private String title;
    private long startTime;
    private long endTime;
    private Type eventType;
    private Status status;

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

    public enum Type {
        PRIVATE, GROUP, PUBLIC
    }

    public enum Status {
        COMFIRM, PENDING
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setEventId(int id){ this.id = id;}

    public void setStartTime(long startTime){ this.startTime = startTime; }

    public void setEndTime(long endTime){ this.endTime = endTime; }

    public int getId(){ return id; }

    public long getStartTime(){return startTime;}

    public long getEndTime(){return endTime;}

    public String getTitle() {
        return title;
    }

    public void setEventType(Type eventType) {
        this.eventType = eventType;
    }

    public Type getEventType() {
        return eventType;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public int getDurationInMinute(){
        return (int)((endTime - startTime) /(1000*60));
    }
}
