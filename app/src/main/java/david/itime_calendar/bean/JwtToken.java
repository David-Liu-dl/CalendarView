package david.itime_calendar.bean;

import java.io.Serializable;

/**
 * Created by David Liu on 12/08/2016.
 * lyhmelbourne@gmail.com
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
