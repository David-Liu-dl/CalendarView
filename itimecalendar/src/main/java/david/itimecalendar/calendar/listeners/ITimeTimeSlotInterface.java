package david.itimecalendar.calendar.listeners;

/**
 * Created by yuhaoliu on 10/09/2016.
 */
public interface ITimeTimeSlotInterface<T> extends Comparable<T>  {
    enum Status{
        CONFLICT, UNSELECTED, SELECTED
    }

    void setStartTime(long startTime);
    long getStartTime();
    void setEndTime(long endTime);
    long getEndTime();

    void setStatus(String status);
    String getStatus();

    int getAcceptedNum();
    void setAcceptedNum(int num);

    int getTotalNum();
    void setTotalNum(int num);

    String getTimeslotUid();

    boolean isRecommended();

    void setIsAllDay(boolean isAllDay);
    boolean isAllDay();
}
