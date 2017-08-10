package david.itimecalendar.calendar.wrapper;


import android.support.annotation.NonNull;

import david.itimecalendar.calendar.listeners.ITimeTimeSlotInterface;
import david.itimecalendar.calendar.ui.unitviews.DraggableTimeSlotView;
import david.itimecalendar.calendar.util.OverlapHelper;

/**
 * Created by yuhaoliu on 4/01/2017.
 */

public class WrapperTimeSlot implements OverlapHelper.OverlapInput<WrapperTimeSlot>{
    private DraggableTimeSlotView draggableTimeSlotView;
    private ITimeTimeSlotInterface timeSlot = null;
    private boolean isSelected = false;
    private boolean isAnimated = false;
    private boolean isRead = false;
    private boolean isRecommended = false;
    private boolean isConflict = false;

    public WrapperTimeSlot(ITimeTimeSlotInterface timeSlot) {
        this.timeSlot = timeSlot;
        if (timeSlot != null){
            this.isRecommended = timeSlot.isRecommended();
        }
    }

    public WrapperTimeSlot copyWrapperTimeslot(){
        WrapperTimeSlot wrapper = new WrapperTimeSlot(this.timeSlot);
        wrapper.setSelected(isSelected());
        wrapper.setRead(isRead());
        wrapper.setAnimated(isAnimated());
        return wrapper;
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

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public boolean isRecommended() {
        return isRecommended;
    }

    public void setRecommended(boolean recommended) {
        isRecommended = recommended;
    }

    public DraggableTimeSlotView getDraggableTimeSlotView() {
        return draggableTimeSlotView;
    }

    public void setDraggableTimeSlotView(DraggableTimeSlotView draggableTimeSlotView) {
        this.draggableTimeSlotView = draggableTimeSlotView;
    }

    public boolean isConflict() {
        return isConflict;
    }

    public void setConflict(boolean conflict) {
        isConflict = conflict;
    }

    @Override
    public long getStartTime() {
        return this.getTimeSlot().getStartTime();
    }

    @Override
    public long getEndTime() {
        return this.getTimeSlot().getEndTime();
    }

    @Override
    public int compareTo(@NonNull WrapperTimeSlot o) {
        return this.getTimeSlot().compareTo(o.getTimeSlot());
    }
}
