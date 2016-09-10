package org.unimelb.itime.test.bean;

import android.support.annotation.Nullable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Keep;
import org.unimelb.itime.vendor.listener.ITimeContactInterface;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by yuhaoliu on 17/08/16.
 */
@Entity
public class Contact implements ITimeContactInterface {
    @Id()
    String id;
    String photo = null;
    String name;

    public Contact(){
    }

    @Keep
    public Contact(@Nullable String photo, String name, String id) {
        if (photo != null){
            this.photo = photo;
        }
        this.id = id;
        this.name = name;
    }

    @Override
    public void setPhoto(String photo) {
        this.photo = photo;
    }

    @Override
    public String getPhoto() {
        return this.photo;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName(){
        return this.name;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return this.id;
    }
}
