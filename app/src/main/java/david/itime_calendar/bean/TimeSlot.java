package david.itime_calendar.bean;

import android.support.annotation.NonNull;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;

import java.io.Serializable;

import david.itimecalendar.calendar.listeners.ITimeTimeSlotInterface;

/**
 * Created by David Liu on 10/09/2016.
 * lyhmelbourne@gmail.com
 */

@Entity
public class TimeSlot implements ITimeTimeSlotInterface<TimeSlot>,Serializable {
    private String timeSlotUid;
    private String eventUid;
    private long startTime;
    private long endTime;
    private String status;
    private int accetpedNum;
    private int totalNum;
    private boolean isRecommended;
    private boolean isAllDay;

    @Generated(hash = 1384600152)
    public TimeSlot(String timeSlotUid, String eventUid, long startTime,
            long endTime, String status, int accetpedNum, int totalNum,
            boolean isRecommended, boolean isAllDay) {
        this.timeSlotUid = timeSlotUid;
        this.eventUid = eventUid;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.accetpedNum = accetpedNum;
        this.totalNum = totalNum;
        this.isRecommended = isRecommended;
        this.isAllDay = isAllDay;
    }

    @Generated(hash = 1337764006)
    public TimeSlot() {
    }
    

    @Override
    public void setStartTime(long l) {
        this.startTime = l;
    }

    @Override
    public long getStartTime() {
        return this.startTime;
    }

    @Override
    public void setEndTime(long l) {
        this.endTime = l;
    }

    @Override
    public long getEndTime() {
        return this.endTime;
    }

    @Override
    public void setStatus(String s) {
        this.status = s;
    }

    @Override
    public String getStatus() {
        return this.status;
    }

    @Override
    public int getAcceptedNum() {
        return accetpedNum;
    }

    @Override
    public void setAcceptedNum(int i) {
        this.accetpedNum = i;
    }

    @Override
    public int getTotalNum() {
        return totalNum;
    }

    @Override
    public void setTotalNum(int i) {
        this.totalNum = i;
    }

    @Override
    public String getTimeslotUid() {
        return this.timeSlotUid;
    }

    @Override
    public boolean isRecommended() {
        return isRecommended;
    }

    public void setRecommended(boolean recommended) {
        isRecommended = recommended;
    }

    public void setTimeSlotUid(String timeSlotUid) {
        this.timeSlotUid = timeSlotUid;
    }

    public int getAccetpedNum() {
        return this.accetpedNum;
    }

    public void setAccetpedNum(int accetpedNum) {
        this.accetpedNum = accetpedNum;
    }


    public String getEventUid() {
        return eventUid;
    }

    public void setEventUid(String eventUid) {
        this.eventUid = eventUid;
    }

    public String getTimeSlotUid() {
        return this.timeSlotUid;
    }

    public boolean getIsRecommended() {
        return this.isRecommended;
    }

    public void setIsRecommended(boolean isRecommended) {
        this.isRecommended = isRecommended;
    }

    @Override
    public void setIsAllDay(boolean isAllDay) {
        this.isAllDay = isAllDay;
    }

    @Override
    public boolean isAllDay() {
        return this.isAllDay;
    }

    @Override
    public int compareTo(@NonNull TimeSlot timeSlot) {
        long selfStartTime = this.getStartTime();
        long cmpTgtStartTime = timeSlot.getStartTime();
        int result = selfStartTime < cmpTgtStartTime ? -1 : 1;

        if (result == -1){
            return result;
        }else {
            return selfStartTime == cmpTgtStartTime ? 0 : 1;
        }
    }

    public boolean getIsAllDay() {
        return this.isAllDay;
    }
}
