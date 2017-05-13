package david.itimecalendar.calendar.listeners;

import java.util.List;

/**
 * Created by yinchuandong on 22/08/2016.
 */
public interface ITimeEventInterface<T> extends Comparable<T> {
    String getEventUid();

    void setTitle(String title);
    String getTitle();

    void setStartTime(long startTime);
    long getStartTime();

    void setEndTime(long endTime);
    long getEndTime();

    int getDisplayEventType();
    String getDisplayStatus();

    void setLocation(String location);
    String getLocation();

    List<? extends ITimeInviteeInterface> getDisplayInvitee();

    void setHighLighted(boolean highlighted);
    boolean isHighlighted();

    /**
     *
     * @return View.VISIBILITY
     */
    int isShownInCalendar();
}
