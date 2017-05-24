package david.itimecalendar.calendar.wrapper;


import david.itimecalendar.calendar.listeners.ITimeEventInterface;
import david.itimecalendar.calendar.util.OverlapHelper;

/**
 * Created by yuhaoliu on 4/01/2017.
 */

public class WrapperEvent implements OverlapHelper.OverlapInput<WrapperEvent> {
    private ITimeEventInterface event;
    private long fromDayBegin;
    private String vendorEventUid;
    private boolean isAnimated;


    public WrapperEvent(ITimeEventInterface event) {
        this.event = event;
    }

    public ITimeEventInterface getEvent() {
        return event;
    }

    public void setEvent(ITimeEventInterface event) {
        this.event = event;
    }

    public String getVendorEventUid() {
        return vendorEventUid;
    }

    public void setVendorEventUid(String vendorEventUid) {
        this.vendorEventUid = vendorEventUid;
    }

    public boolean isAnimated() {
        return isAnimated;
    }

    public void setAnimated(boolean animated) {
        isAnimated = animated;
    }

    public long getFromDayBegin() {
        return fromDayBegin;
    }

    public void setFromDayBegin(long fromDayBegin) {
        this.fromDayBegin = fromDayBegin;
    }

    @Override
    public int compareTo(WrapperEvent o) {
        return event.compareTo(o.getEvent());
    }

    @Override
    public long getStartTime() {
        return event.getStartTime();
    }

    @Override
    public long getEndTime() {
        return event.getEndTime();
    }
}
