package org.unimelb.itime.vendor.weekview;

import android.content.Context;
import android.util.AttributeSet;

import org.unimelb.itime.vendor.timeslot.TimeSlotView;

import java.util.ArrayList;

/**
 * Created by yuhaoliu on 24/09/16.
 */
public class WeekTimeSlotView extends WeekView {
    private ArrayList<TimeSlotView> slotViews = new ArrayList<>();

    public WeekTimeSlotView(Context context) {
        super(context);
    }

    public WeekTimeSlotView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WeekTimeSlotView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void addSlots(int startTime, int endTime, boolean status){

    }
}
