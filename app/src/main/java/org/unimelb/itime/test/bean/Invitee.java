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
    String eventUid;
    String inviteeUid;
    String photo = null;
    String name;

    @Keep
    public Invitee() {
    }

    @Generated(hash = 935710098)
    public Invitee(String eventUid, String inviteeUid, String photo, String name) {
        this.eventUid = eventUid;
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

    public String getEventUid() {
        return eventUid;
    }

    public void setEventUid(String eventUid) {
        this.eventUid = eventUid;
    }
}
