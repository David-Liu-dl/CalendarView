package david.itime_calendar.bean;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.ToMany;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import david.itime_calendar.RuleFactory.RuleInterface;
import david.itime_calendar.RuleFactory.RuleModel;
import david.itimecalendar.calendar.listeners.ITimeEventInterface;
import david.itimecalendar.calendar.listeners.ITimeInviteeInterface;

/**
 * Created by yinchuandong on 22/08/2016.
 */

@Entity
public class Event implements ITimeEventInterface<Event>, Serializable, Cloneable,RuleInterface {
    @Id
    private String eventUid = "";
    // for other calendars
    private String eventId = "";
    private String recurringEventUid = "";
    // for other calendars
    private String recurringEventId = "";
    private String calendarUid = "";
    private String iCalUID = "";
    private String hostUserUid = ""; // add by paul
    private String summary = "";
    private String url = "";
    private String location = "this is location";
    private String locationNote = "";
    private double locationLatitude;
    private double locationLongitude;
    private String note = "";
    private boolean isAllDay;
    private String eventStatus = "";


    private transient List<PhotoUrl> photoList = new ArrayList<>();
    private transient String[] recurrence = {};

    private String photo = "[]";

    @ToMany(referencedJoinProperty = "eventUid")
    private List<TimeSlot> timeslots = null;

    // later delete
    private transient long repeatEndsTime;
    private transient boolean isHost;
    private transient boolean highlighted;

    public RuleModel getRule() {
        return rule;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        Event event = null;
        try
        {
            event = (Event) super.clone();
        } catch (CloneNotSupportedException e){
            e.printStackTrace();
        }
        return event;
    }

    public void setRule(RuleModel rule) {
        this.rule = rule;
    }

    private transient RuleModel rule;

    @Property
    @NotNull
    private long startTime;
    @Property
    @NotNull
    private long endTime;
    @Property
    @NotNull
    private String eventType = "";

    @ToMany(referencedJoinProperty = "eventUid")
    private List<Invitee> invitee = null;
    /** Used for active entity operations. */
    @Generated(hash = 1542254534)
    private transient EventDao myDao;
    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    public Event() {
    }

    @Generated(hash = 1624900520)
    public Event(String eventUid, String eventId, String recurringEventUid, String recurringEventId,
            String calendarUid, String iCalUID, String hostUserUid, String summary, String url,
            String location, String locationNote, double locationLatitude, double locationLongitude,
            String note, boolean isAllDay, String eventStatus, String photo, long startTime,
            long endTime, @NotNull String eventType) {
        this.eventUid = eventUid;
        this.eventId = eventId;
        this.recurringEventUid = recurringEventUid;
        this.recurringEventId = recurringEventId;
        this.calendarUid = calendarUid;
        this.iCalUID = iCalUID;
        this.hostUserUid = hostUserUid;
        this.summary = summary;
        this.url = url;
        this.location = location;
        this.locationNote = locationNote;
        this.locationLatitude = locationLatitude;
        this.locationLongitude = locationLongitude;
        this.note = note;
        this.isAllDay = isAllDay;
        this.eventStatus = eventStatus;
        this.photo = photo;
        this.startTime = startTime;
        this.endTime = endTime;
        this.eventType = eventType;
    }

    @Override
    public void setSummary(String summary) {
        this.summary = summary;
    }

    @Override
    public String getSummary() {
        return this.summary;
    }

    @Override
    public List<? extends ITimeInviteeInterface> getDisplayInvitee() {
        if (this.invitee==null){
            this.invitee = this.getInvitee();
        }
        return this.invitee;
    }

    @Override
    public void setHighLighted(boolean hightlighted) {
        this.highlighted = hightlighted;
    }

    @Override
    public boolean isHighlighted() {
        return this.highlighted;
    }

    @Override
    public void setIsAllDay(boolean isAllDay) {
        this.isAllDay = isAllDay;
    }

    @Override
    public boolean getIsAllDay() {
        return false;
    }


    @Override
    public int isShownInCalendar() {
        return 0;
    }

    public void setEventId(String id){ this.eventUid = id;}

    public void setStartTime(long startTime){ this.startTime = startTime; }

    public void setEndTime(long endTime){ this.endTime = endTime; }

    public String getEventUid(){ return eventUid; }

    public long getStartTime(){return startTime;}

    public long getEndTime(){return endTime;}

    public int getDuration(){
        return (int)((endTime - startTime) /(1000*60));
    }

    public long getDurationMilliseconds(){
        return (endTime - startTime);
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

    public void setEventUid(String eventUid) {
        this.eventUid = eventUid;
    }

    public String getLocationName() {
        return location;
    }

    @Override
    public String getEventType() {
        return this.eventType;
    }

    @Override
    public boolean isConfirmed() {
        return eventStatus.equals("Confirmed");
    }

    public String getEventStatus() {
        return eventStatus;
    }

    public void setEventStatus(String eventStatus) {
        this.eventStatus = eventStatus;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setInvitee(List<Invitee> invitee) {
        this.invitee = invitee;
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }


    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }


    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }


    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 777091542)
    public synchronized void resetInvitee() {
        invitee = null;
    }


    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 1368951675)
    public List<Invitee> getInvitee() {
        if (invitee == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            InviteeDao targetDao = daoSession.getInviteeDao();
            List<Invitee> inviteeNew = targetDao._queryEvent_Invitee(eventUid);
            synchronized (this) {
                if(invitee == null) {
                    invitee = inviteeNew;
                }
            }
        }
        return invitee;
    }


    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1459865304)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getEventDao() : null;
    }


    public String getUrl() {
        return this.url;
    }


    public void setUrl(String url) {
        this.url = url;
    }

    public String[] getRecurrence() {
        return this.recurrence;
    }


    public void setRecurrence(String[] recurrence) {
        this.recurrence = recurrence;
    }


    public String getICalUID() {
        return this.iCalUID;
    }


    public void setICalUID(String iCalUID) {
        this.iCalUID = iCalUID;
    }


    public String getCalendarUid() {
        return this.calendarUid;
    }


    public void setCalendarUid(String calendarUid) {
        this.calendarUid = calendarUid;
    }


    public String getRecurringEventId() {
        return this.recurringEventId;
    }


    public void setRecurringEventId(String recurringEventId) {
        this.recurringEventId = recurringEventId;
    }


    public String getRecurringEventUid() {
        return this.recurringEventUid;
    }


    public void setRecurringEventUid(String recurringEventUid) {
        this.recurringEventUid = recurringEventUid;
    }


    public String getEventId() {
        return this.eventId;
    }

    public String getiCalUID() {
        return iCalUID;
    }

    public void setiCalUID(String iCalUID) {
        this.iCalUID = iCalUID;
    }



    public String getLocationNote() {
        return locationNote;
    }

    public void setLocationNote(String locationNote) {
        this.locationNote = locationNote;
    }

    public double getLocationLatitude() {
        return locationLatitude;
    }

    public void setLocationLatitude(double locationLatitude) {
        this.locationLatitude = locationLatitude;
    }

    public double getLocationLongitude() {
        return locationLongitude;
    }

    public void setLocationLongitude(double locationLongitude) {
        this.locationLongitude = locationLongitude;
    }


    public long getRepeatEndsTime() {
        return repeatEndsTime;
    }

    public void setRepeatEndsTime(long repeatEndsTime) {
        this.repeatEndsTime = repeatEndsTime;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 1734061039)
    public List<TimeSlot> getTimeslots() {
        if (timeslots == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            TimeSlotDao targetDao = daoSession.getTimeSlotDao();
            List<TimeSlot> timeslotsNew = targetDao._queryEvent_Timeslots(eventUid);
            synchronized (this) {
                if(timeslots == null) {
                    timeslots = timeslotsNew;
                }
            }
        }
        return timeslots;
    }

    public void setTimeslots(List<TimeSlot> timeslots) {
        this.timeslots = timeslots;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 481094641)
    public synchronized void resetTimeslots() {
        timeslots = null;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public boolean hasAttendee(){
        return invitee!=null;
    }

    public boolean hasTimeslots(){
        return timeslots!=null;
    }

    public boolean isHost() {
        return isHost;
    }

    public void setHost(boolean host) {
        isHost = host;
    }

    public String getHostUserUid() {
        return hostUserUid;
    }

    public void setHostUserUid(String hostUserUid) {
        this.hostUserUid = hostUserUid;
    }


    public List<PhotoUrl> getPhotoList() {
            Gson gson = new Gson();
            Type collectionType = new TypeToken<Collection<PhotoUrl>>(){}.getType();


        return gson.fromJson(photo, collectionType);
    }

    public void setPhoto(List<PhotoUrl> photoUrls) {
        Gson gson = new Gson();
        this.photo = gson.toJson(photoUrls);
        this.photoList = photoUrls;
    }



    public String getPhoto() {
        return this.photo;
    }



    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getLocation() {
        return this.location;
    }

}
