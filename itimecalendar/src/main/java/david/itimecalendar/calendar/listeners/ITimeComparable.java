package david.itimecalendar.calendar.listeners;

/**
 * Created by David Liu on 12/8/17.
 * ${COMPANY}
 * lyhmelbourne@gmail.com
 */

public interface ITimeComparable<T> extends Comparable<T> {
    long getStartTime();
    long getEndTime();
}
