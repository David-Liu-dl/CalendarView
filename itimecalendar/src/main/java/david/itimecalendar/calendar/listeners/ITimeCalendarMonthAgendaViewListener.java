package david.itimecalendar.calendar.listeners;

import java.util.Date;

import david.itimecalendar.calendar.ui.agendaview.AgendaViewBody;

/**
 * Created by yuhaoliu on 28/06/2017.
 */

public interface ITimeCalendarMonthAgendaViewListener extends AgendaViewBody.OnEventClickListener{
    void onDateChanged(Date date);
    void onHeaderFlingDateChanged(Date newestDate);
}
