package david.itimecalendar.calendar.listeners;

import java.util.Date;

import david.itimecalendar.calendar.ui.monthview.DayViewBody;

/**
 * Created by David Liu on 28/06/2017.
 * ${COMPANY}
 * lyhmelbourne@gmail.com
 */

public interface ITimeCalendarTimeslotViewListener extends DayViewBody.OnViewBodyTimeSlotListener{
    void onDateChanged(Date date);
}
