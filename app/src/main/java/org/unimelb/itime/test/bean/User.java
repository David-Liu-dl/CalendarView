package org.unimelb.itime.test.bean;


import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by yinchuandong on 20/06/2016.
 */

@Entity
public class User {
    @Id
    private Long id;

    @Property(nameInDb = "USER_ID")
    private String userId;

    @Generated(hash = 1182691892)
    public User(Long id, String userId) {
        this.id = id;
        this.userId = userId;
    }

    @Generated(hash = 586692638)
    public User() {
    }

    public long getUniqueId() {
        return id;
    }

    public void setUniqueId(long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return this.id;
    }

}


