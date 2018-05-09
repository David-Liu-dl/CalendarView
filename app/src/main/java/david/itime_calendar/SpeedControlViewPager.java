package david.itime_calendar;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;

/**
 * Created by David Liu on 9/5/18.
 * lyhmelbourne@gmail.com
 */

public class SpeedControlViewPager extends ViewPager {
    private final int SWIPE_SPEED = 2500;
    private VelocityTracker mVelocityTracker;
    private int mMaxVelocity;

    public SpeedControlViewPager(Context context) {
        super(context);
        mMaxVelocity = ViewConfiguration.get(getContext()).getScaledMaximumFlingVelocity();
    }

    public SpeedControlViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        mMaxVelocity = ViewConfiguration.get(getContext()).getScaledMaximumFlingVelocity();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int actionType = ev.getAction();
        switch (actionType){
            case MotionEvent.ACTION_DOWN:
                if (mVelocityTracker == null) {
                    mVelocityTracker = VelocityTracker.obtain();
                } else {
                    mVelocityTracker.clear();
                }
                mVelocityTracker.addMovement(ev);
                break;
            case MotionEvent.ACTION_MOVE:
                mVelocityTracker.addMovement(ev);
                mVelocityTracker.computeCurrentVelocity(1000, mMaxVelocity);
                return Math.abs(mVelocityTracker.getXVelocity()) > SWIPE_SPEED;
        }

        return super.onInterceptTouchEvent(ev);
    }
}
