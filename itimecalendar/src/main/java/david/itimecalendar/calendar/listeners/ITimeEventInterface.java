package david.itimecalendar.calendar.listeners;

import java.util.List;

/**
 * Created by David Liu on 22/08/2016.
 * ${COMPANY}
 * lyhmelbourne@gmail.com
 */
public interface ITimeEventInterface<T> extends ITimeComparable<T> {
    String EVENT_TYPE_SOLO = "solo";
    String EVENT_TYPE_GROUP = "group";

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
     * @param allday
     */
    void setAllDay(boolean allday);
    boolean isAllDay();
    /**
     *
     * @return View.VISIBILITY
     */
    int isShownInCalendar();

    String getLocationName();
    String getEventType();
    boolean isConfirmed();
}
