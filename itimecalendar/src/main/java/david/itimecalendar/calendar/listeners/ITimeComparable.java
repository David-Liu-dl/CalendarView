package david.itimecalendar.calendar.listeners;

/**
 * Created by yuhaoliu on 12/8/17.
 */

public interface ITimeComparable<T> extends Comparable<T> {
    long getStartTime();
    long getEndTime();
}
