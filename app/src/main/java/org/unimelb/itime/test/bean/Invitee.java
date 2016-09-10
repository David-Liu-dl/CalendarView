package org.unimelb.itime.test.bean;

import android.support.annotation.Nullable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Keep;
import org.unimelb.itime.vendor.listener.ITimeInviteeInterface;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by yuhaoliu on 10/09/2016.
 */
@Entity
public class Invitee implements ITimeInviteeInterface{

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Id(autoincrement = true)
    Long id;
    Long eventId;
    String inviteeUid;
    String photo = null;
    String name;

    @Keep
    public Invitee() {
    }

    @Keep
    public Invitee(@Nullable String photo, String name, String id) {
    }

    @Generated(hash = 2138699291)
    public Invitee(Long id, Long eventId, String inviteeUid, String photo,
            String name) {
        this.id = id;
        this.eventId = eventId;
        this.inviteeUid = inviteeUid;
        this.photo = photo;
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void setInviteeUid(String inviteeUid) {
        this.inviteeUid = inviteeUid;
    }

    @Override
    public String getInviteeUid() {
        return this.inviteeUid;
    }

    @Override
    public String getPhoto() {
        return this.photo;
    }

    @Override
    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }
}
