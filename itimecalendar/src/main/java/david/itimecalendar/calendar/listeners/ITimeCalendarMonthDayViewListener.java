package david.itimecalendar.calendar.listeners;

import java.util.Date;

import david.itimecalendar.calendar.ui.monthview.DayViewBody;

/**
 * Created by David Liu on 28/06/2017.
 * ${COMPANY}
 * lyhmelbourne@gmail.com
 */

public interface ITimeCalendarMonthDayViewListener extends DayViewBody.OnViewBodyEventListener {
    void onDateChanged(Date date);
    void onHeaderFlingDateChanged(Date newestDate);
}
