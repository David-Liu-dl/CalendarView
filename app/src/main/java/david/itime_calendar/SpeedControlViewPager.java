package david.itime_calendar;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by David Liu on 9/5/18.
 * lyhmelbourne@gmail.com
 */

public class SpeedControlViewPager extends ViewPager {
    private static final float speedToScroll = 2.0f;

    public SpeedControlViewPager(Context context) {
        super(context);
    }

    public SpeedControlViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return super.onTouchEvent(ev);
    }
}
