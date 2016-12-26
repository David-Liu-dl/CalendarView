package org.unimelb.itime.vendor.helper;

import android.view.animation.AlphaAnimation;

/**
 * Created by yuhaoliu on 26/12/2016.
 */
public class Animation {
    private static Animation ourInstance = new Animation();

    public static Animation getInstance() {
        return ourInstance;
    }

    private Animation() {
    }

    public AlphaAnimation getFadeInAnim(){
        AlphaAnimation anim = new AlphaAnimation(1.0f, 0.0f);
        anim.setDuration(1000);

        return anim;
    }

    public AlphaAnimation getFadeOutAnim(){
        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(1000);

        return anim;
    }
}
