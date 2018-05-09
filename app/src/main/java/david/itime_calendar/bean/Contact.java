package david.itime_calendar.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

import java.io.Serializable;

import david.itimecalendar.calendar.listeners.ITimeContactInterface;

/**
 * Created by David Liu on 17/08/16.
 * lyhmelbourne@gmail.com
 */

@Entity
public class Contact implements ITimeContactInterface, Serializable {
    @Id()
    private String contactUid;
    private String photo = null;
    private String name;

    public Contact(){
    }

    @Generated(hash = 1462673109)
    public Contact(String contactUid, String photo, String name) {
        this.contactUid = contactUid;
        this.photo = photo;
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
    public void setContactUid(String contactUid) {
        this.contactUid = contactUid;
    }

    @Override
    public String getContactUid() {
        return this.contactUid;
    }
}
