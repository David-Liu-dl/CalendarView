package org.unimelb.itime.vendor.dayview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.animation.Interpolator;
import android.widget.Scroller;

/**
 * Created by yuhaoliu on 16/09/16.
 */
public class ScrollerCustomDuration extends Scroller{
    private double mScrollFactor = 1;
    private DisplayMetrics dm;
    private float scrollWidthFactory = 0.0f;

    public ScrollerCustomDuration(Context context) {
        super(context);
        dm = context.getResources().getDisplayMetrics();
    }

    public ScrollerCustomDuration(Context context, Interpolator interpolator) {
        super(context, interpolator);
        dm = context.getResources().getDisplayMetrics();
    }

    @SuppressLint("NewApi")
    public ScrollerCustomDuration(Context context, Interpolator interpolator, boolean flywheel) {
        super(context, interpolator, flywheel);
    }

    /**
     * Set the factor by which the duration will change
     */
    public void setScrollDurationFactor(double scrollFactor) {
        mScrollFactor = scrollFactor;
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy, int duration) {
        scrollWidthFactory = Math.abs((float) dx/dm.widthPixels);

        super.startScroll(startX, startY, dx, dy, (int) ((duration * mScrollFactor) * scrollWidthFactory));
    }

}
