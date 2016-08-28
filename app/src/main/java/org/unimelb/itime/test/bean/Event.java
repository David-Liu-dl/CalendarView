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
    private String eventType;
    @Property
    @NotNull
    private String status;

    public Event() {
    }

    @Generated(hash = 409080067)
    public Event(Long id, String title, long startTime, long endTime,
            String eventType, String status) {
        this.id = id;
        this.title = title;
        this.startTime = startTime;
        this.endTime = endTime;
        this.eventType = eventType;
        this.status = status;
    }

    @Override
    public String getEventId() {
        return null;
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
    public void setNote(String note) {

    }

    @Override
    public String getNote() {
        return null;
    }

    @Override
    public void setLocationAddress(String adress) {

    }

    @Override
    public String getLocationAddress() {
        return null;
    }

    @Override
    public void setLocationNote(String locationNote) {

    }

    @Override
    public String getLocationNote() {
        return null;
    }

    @Override
    public void setLocationLatitude(double latitude) {

    }

    @Override
    public Double getLocationLatitude() {
        return null;
    }

    @Override
    public void setLocationLongitude(double longitude) {

    }

    @Override
    public Double getLocationLongitude() {
        return null;
    }

    @Override
    public void setRepeatTypeId(int repeatTypeId) {

    }

    @Override
    public int getRepeatTypeId() {
        return 0;
    }

    @Override
    public void setUserId(String userId) {

    }

    @Override
    public String getUserId() {
        return null;
    }

    @Override
    public void setAlertTimeId(int alertTimeId) {

    }

    @Override
    public int getAlertTimeId() {
        return 0;
    }

    @Override
    public void setEventTypeId(int eventTypeId) {

    }

    @Override
    public int getEventTypeId() {
        return 0;
    }

    @Override
    public void setVisibilityTypeId(int visibilityTypeId) {

    }

    @Override
    public int getVisibilityTypeId() {
        return 0;
    }

    @Override
    public void setEventSourceId(int eventSourceId) {

    }

    @Override
    public int getEventSourceId() {
        return 0;
    }

    @Override
    public void setCalendarTypeId(String calendarTypeId) {

    }

    @Override
    public String getCalendarTypeId() {
        return null;
    }

    @Override
    public void setIsInfiniteRepeat(Boolean isInfiniteRepeat) {

    }

    @Override
    public Boolean getIsInfiniteRepeat() {
        return null;
    }

    @Override
    public void setIsDeleted(Boolean isDeleted) {

    }

    @Override
    public Boolean getIsDeleted() {
        return null;
    }

    @Override
    public void setRepeatEndsTime(Integer repeatEndsTime) {

    }

    @Override
    public Integer getRepeatEndsTime() {
        return null;
    }

    @Override
    public void setHostEventId(String hostEventId) {

    }

    @Override
    public String getHostEventId() {
        return null;
    }

    @Override
    public void setUserStatusId(int userStatusId) {

    }

    @Override
    public int getUserStatusId() {
        return 0;
    }

    @Override
    public void setEventPhotos(ArrayList<String> urls) {

    }

    @Override
    public ArrayList<String> getEventPhotos() {
        return null;
    }

    @Override
    public void setUrl(String url) {

    }

    @Override
    public String getUrl() {
        return null;
    }

    @Override
    public void setDuration(int duration) {

    }

    @Override
    public int getDuration() {
        return 0;
    }

    @Override
    public void setAttendees(ArrayList<String> userId) {

    }

    @Override
    public ArrayList<String> getAttendees() {
        return null;
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

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getEventType() {
        return eventType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
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
