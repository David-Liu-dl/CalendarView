package david.itimecalendar.calendar.listeners;

import java.util.List;

/**
 * Created by yinchuandong on 22/08/2016.
 */
public interface ITimeEventInterface<T> extends Comparable<T> {
    int ALLDAY = 1;
    int NON_ALLDAY = 0;

    String getEventUid();

    void setSummary(String summary);
    String getSummary();

    void setStartTime(long startTime);
    long getStartTime();

    void setEndTime(long endTime);
    long getEndTime();

    List<? extends ITimeInviteeInterface> getDisplayInvitee();

    void setHighLighted(boolean highlighted);
    boolean isHighlighted();

    /**
     *
     * @param isAllDay
     */
    void setIsAllDay(int isAllDay);
    int getIsAllDay();
    /**
     *
     * @return View.VISIBILITY
     */
    int isShownInCalendar();
}
