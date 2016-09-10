package org.unimelb.itime.test.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.JoinProperty;
import org.greenrobot.greendao.annotation.Keep;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.ToMany;
import org.unimelb.itime.vendor.listener.ITimeEventInterface;

import java.util.ArrayList;
import java.util.List;

import org.greenrobot.greendao.annotation.Generated;
import org.unimelb.itime.vendor.listener.ITimeInviteeInterface;
import org.greenrobot.greendao.DaoException;

/**
 * Created by yinchuandong on 22/08/2016.
 */

@Entity
public class Event implements ITimeEventInterface<Event>{
    @Id(autoincrement = true)
    private Long id;
    private String title;
    private String location;
    
    @Property
    @NotNull
    private long startTime;
    @Property
    @NotNull
    private long endTime;
    @Property
    @NotNull
    private int eventType;
    @Property
    @NotNull
    private int status;

//    @ToMany
//    private List<String> inviteesUrls;
    @ToMany(joinProperties = {
            @JoinProperty(name = "id", referencedName = "id")
    })
    private List<Invitee> invitee = new ArrayList<>();
    /** Used for active entity operations. */
    @Generated(hash = 1542254534)
    private transient EventDao myDao;
    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    public Event() {
    }

    @Generated(hash = 108715400)
    public Event(Long id, String title, String location, long startTime,
            long endTime, int eventType, int status) {
        this.id = id;
        this.title = title;
        this.location = location;
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
    public void setProposedTimeSlots(List<Long> proposedTimeSlots) {

    }

    @Override
    public List<Long> getProposedTimeSlots() {
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

    @Override
    public String getLocation() {
        return location;
    }

    @Override
    public void setLocation(String location) {
        this.location = location;
    }

    @Keep
    public List<Invitee> getInvitee() {
        return this.invitee;
    }

    public void setInvitee(List<Invitee> invitee) {
        this.invitee = invitee;
    }

//    public List<ITimeInviteeInterface> getInvitee() {
//        return this.invitee;
//    }
//
//    public void setInvitee(List<ITimeInviteeInterface> invitee) {
//        for (ITimeInviteeInterface inviteeInterface: invitee
//             ) {
//            this.invitee.add((Invitee)inviteeInterface);
//        }
////        this.invitee = (List<Invitee>) invitee;
//    }


    @Override
    public List<? extends ITimeInviteeInterface> getDisplayInvitee() {
        return getInvitee();
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

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1459865304)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getEventDao() : null;
    }
}
