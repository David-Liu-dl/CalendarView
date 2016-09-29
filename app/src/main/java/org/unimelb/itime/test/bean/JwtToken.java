package org.unimelb.itime.test.bean;

import java.io.Serializable;

/**
 * Created by yinchuandong on 12/08/2016.
 */
public class JwtToken implements Serializable {
    private String token;
    private String error;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
