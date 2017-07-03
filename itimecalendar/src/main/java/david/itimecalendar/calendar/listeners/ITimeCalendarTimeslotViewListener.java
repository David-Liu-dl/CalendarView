package david.itimecalendar.calendar.listeners;

import java.util.Date;

import david.itimecalendar.calendar.ui.monthview.DayViewBody;

/**
 * Created by yuhaoliu on 28/06/2017.
 */

public interface ITimeCalendarTimeslotViewListener extends DayViewBody.OnViewBodyTimeSlotListener{
    void onDateChanged(Date date);
}
