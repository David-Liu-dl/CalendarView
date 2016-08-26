package org.unimelb.itime.vendor.timeslot;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

/**
 * Created by Paul on 26/08/2016.
 */
public class TimeSlotViewLayoutParams extends RelativeLayout.LayoutParams{
    public int top = 0;
    public int left = 0;

    public TimeSlotViewLayoutParams(Context c, AttributeSet attrs) {
        super(c, attrs);
    }
    public TimeSlotViewLayoutParams(int width, int height) {
        super(width, height);
    }
    public TimeSlotViewLayoutParams(ViewGroup.LayoutParams source) {
        super(source);
    }


}
