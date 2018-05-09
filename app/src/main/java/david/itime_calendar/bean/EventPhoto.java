package david.itime_calendar.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;

import java.io.Serializable;

/**
 * Created by David Liu on 10/09/2016.
 * lyhmelbourne@gmail.com
 */

@Entity
public class EventPhoto implements Serializable {
    private int photoUid;

    private int eventId;
    private String url;

    public EventPhoto(){

    }


    @Generated(hash = 1204135514)
    public EventPhoto(int photoUid, int eventId, String url) {
        this.photoUid = photoUid;
        this.eventId = eventId;
        this.url = url;
    }


    public int getPhotoUid() {
        return photoUid;
    }

    public void setPhotoUid(int photoUid) {
        this.photoUid = photoUid;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
