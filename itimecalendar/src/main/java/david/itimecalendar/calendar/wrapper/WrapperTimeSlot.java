package david.itimecalendar.calendar.wrapper;


import david.itimecalendar.calendar.listeners.ITimeTimeSlotInterface;

/**
 * Created by yuhaoliu on 4/01/2017.
 */

public class WrapperTimeSlot {
    private ITimeTimeSlotInterface timeSlot = null;
    private boolean isSelected = false;
    private boolean isAnimated = true;
    private boolean isRead = false;
    private boolean isRecommended = false;

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

}
