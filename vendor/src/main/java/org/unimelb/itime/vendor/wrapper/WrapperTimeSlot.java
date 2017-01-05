package org.unimelb.itime.vendor.wrapper;

import org.unimelb.itime.vendor.listener.ITimeTimeSlotInterface;
import org.unimelb.itime.vendor.weekview.WeekView;

/**
 * Created by yuhaoliu on 4/01/2017.
 */

public class WrapperTimeSlot {
    private ITimeTimeSlotInterface timeSlot = null;
    private boolean isSelected = false;
    private boolean isAnimated = false;

    public WrapperTimeSlot(ITimeTimeSlotInterface timeSlot) {
        this.timeSlot = timeSlot;
    }

    public boolean isAnimated() {
        return isAnimated;
    }

    public void setAnimated(boolean animated) {
        isAnimated = animated;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public ITimeTimeSlotInterface getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(ITimeTimeSlotInterface timeSlot) {
        this.timeSlot = timeSlot;
    }

}
