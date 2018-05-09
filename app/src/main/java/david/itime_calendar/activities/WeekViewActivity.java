package david.itime_calendar.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.Calendar;
import java.util.Date;

import david.itime_calendar.R;
import david.itime_calendar.bean.Event;
import david.itime_calendar.manager.EventManager;
import david.itimecalendar.calendar.listeners.ITimeCalendarWeekDayViewListener;
import david.itimecalendar.calendar.ui.weekview.WeekView;
import david.itimecalendar.calendar.listeners.ITimeEventInterface;
import david.itimecalendar.calendar.ui.unitviews.DraggableEventView;
import david.itimecalendar.calendar.util.MyCalendar;

/**
 * Created by David Liu on 30/05/2017.
 * lyhmelbourne@gmail.com
 */


public class WeekViewActivity extends AppCompatActivity {
    private EventManager eventManager = MainActivity.eventManager;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weekview);

        doTest();
    }

    private void doTest(){
        final WeekView weekview = (WeekView) findViewById(R.id.week_view);
        weekview.getCalendarConfig().enableEvent();
        weekview.setEventPackage(eventManager.getEventsMap());
        weekview.setITimeCalendarWeekDayViewListener(new ITimeCalendarWeekDayViewListener() {
            @Override
            public void onAllDayEventClick(ITimeEventInterface event) {

            }

            @Override
            public boolean isDraggable(DraggableEventView eventView) {
                return true;
            }

            @Override
            public void onEventCreate(DraggableEventView eventView) {
                Event event = new Event();
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(eventView.getStartTimeM());
                cal.setTimeInMillis(eventView.getEndTimeM());
                event.setStartTime(eventView.getStartTimeM());
                event.setEndTime(eventView.getEndTimeM());
                eventManager.addEvent(event);
            }

            @Override
            public void onEventClick(DraggableEventView eventView) {

            }

            @Override
            public void onEventDragStart(DraggableEventView eventView) {

            }

            @Override
            public void onEventDragging(DraggableEventView eventView, MyCalendar curAreaCal, int touchX, int touchY, int viewX, int viewY, String locationTime) {

            }


            @Override
            public void onEventDragDrop(DraggableEventView eventView) {
                ITimeEventInterface event = eventView.getEvent();

                eventManager.updateEvent((Event) event, eventView.getStartTimeM(), eventView.getEndTimeM());
                weekview.refresh();
            }

            @Override
            public void onEventDragEnd(DraggableEventView eventView) {

            }

            @Override
            public void onDateChanged(Date date) {

            }
        });
    }
}
