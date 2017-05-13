package david.itime_calendar.bean;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Keep;
import org.greenrobot.greendao.annotation.ToOne;

import java.io.Serializable;

import david.itimecalendar.calendar.listeners.ITimeInviteeInterface;

/**
 * Created by yuhaoliu on 10/09/2016.
 */
@Entity
public class Invitee implements ITimeInviteeInterface, Serializable {
    @ToOne(joinProperty = "inviteeUid")
    private Contact contact;

    private String eventUid;
    private String inviteeUid;

    private String userUid;
    private String userId;

    @Generated(hash = 178378225)
    private transient String contact__resolvedKey;

    /** Used for active entity operations. */
    @Generated(hash = 1821175217)
    private transient InviteeDao myDao;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    @Keep
    public Invitee(String inviteeUid, String eventUid, Contact contact) {
        this.inviteeUid = inviteeUid;
        this.eventUid = eventUid;
        this.contact = contact;
    }

    @Generated(hash = 1925218761)
    public Invitee(String eventUid, String inviteeUid, String userUid, String userId) {
        this.eventUid = eventUid;
        this.inviteeUid = inviteeUid;
        this.userUid = userUid;
        this.userId = userId;
    }

    @Generated(hash = 15121660)
    public Invitee() {
    }

    @Override
    public String getName() {
        if(this.contact == null){
            this.contact = this.getContact();
        }
        return this.contact.getName();
    }

    @Override
    public String getInviteeUid() {
        return this.inviteeUid;
    }

    @Override
    public String getPhoto() {
        if(this.contact == null){
            this.contact = this.getContact();
        }
        return this.contact.getPhoto();
    }

    public String getEventUid() {
        return eventUid;
    }

    public void setEventUid(String eventUid) {
        this.eventUid = eventUid;
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

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 2090218591)
    public void setContact(Contact contact) {
        synchronized (this) {
            this.contact = contact;
            inviteeUid = contact == null ? null : contact.getContactUid();
            contact__resolvedKey = inviteeUid;
        }
    }

    /** To-one relationship, resolved on first access. */
    @Generated(hash = 110314383)
    public Contact getContact() {
        String __key = this.inviteeUid;
        if (contact__resolvedKey == null || contact__resolvedKey != __key) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            ContactDao targetDao = daoSession.getContactDao();
            Contact contactNew = targetDao.load(__key);
            synchronized (this) {
                contact = contactNew;
                contact__resolvedKey = __key;
            }
        }
        return contact;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1688020831)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getInviteeDao() : null;
    }

    public void setInviteeUid(String inviteeUid) {
        this.inviteeUid = inviteeUid;
    }

    public String getUserId() {
        return this.userId;
    }

    @Override
    public String getUserStatus() {
        return null;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserUid() {
        return this.userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }
}
