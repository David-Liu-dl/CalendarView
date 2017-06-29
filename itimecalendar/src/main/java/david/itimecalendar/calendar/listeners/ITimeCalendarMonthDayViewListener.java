package david.itimecalendar.calendar.listeners;

import java.util.Date;

import david.itimecalendar.calendar.ui.monthview.DayViewBody;

/**
 * Created by yuhaoliu on 28/06/2017.
 */

public interface ITimeCalendarMonthDayViewListener extends DayViewBody.OnViewBodyEventListener {
    void onDateChanged(Date date);
}
