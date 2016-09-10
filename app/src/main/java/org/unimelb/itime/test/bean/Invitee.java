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
public class Invitee extends Contact implements ITimeInviteeInterface{
    @Id()
    String id;
    String photo = null;
    String name;

    @Keep
    public Invitee() {

    }

    @Keep
    public Invitee(@Nullable String profile_photo_URL, String name, String id) {
        super(profile_photo_URL, name, id);
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoto() {
        return this.photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
