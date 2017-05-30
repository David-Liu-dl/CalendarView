package david.itime_calendar.TestActivities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import david.itime_calendar.R;
import david.itime_calendar.bean.Event;
import david.itimecalendar.calendar.calendar.mudules.monthview.EventController;
import david.itimecalendar.calendar.calendar.mudules.monthview.MonthView;
import david.itimecalendar.calendar.listeners.ITimeEventInterface;
import david.itimecalendar.calendar.unitviews.DraggableEventView;
import david.itimecalendar.calendar.util.BaseUtil;
import david.itimecalendar.calendar.util.MyCalendar;

import static david.itime_calendar.MainActivity.eventManager;

/**
 * Created by yuhaoliu on 30/05/2017.
 */

public class DayViewActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dayview);
        doTest();
    }

    private void doTest(){
        final MonthView monthView = (MonthView) findViewById(R.id.day_view);
        monthView.setOnBodyListener(new EventController.OnEventListener() {
            @Override
            public boolean isDraggable(DraggableEventView eventView) {
                return true;
            }

            @Override
            public void onEventCreate(DraggableEventView eventView) {

            }

            @Override
            public void onEventClick(DraggableEventView eventView) {

            }

            @Override
            public void onEventDragStart(DraggableEventView eventView) {

            }

            @Override
            public void onEventDragging(DraggableEventView eventView, MyCalendar curAreaCal, int x, int y) {

            }

            @Override
            public void onEventDragDrop(DraggableEventView eventView) {
                ITimeEventInterface event = eventView.getEvent();
                BaseUtil.printEventTime("before",event);

                eventManager.updateEvent((Event) event, eventView.getStartTimeM(), eventView.getEndTimeM());

                BaseUtil.printEventTime("end",event);
                monthView.refresh();
            }
        });
        monthView.setEventPackage(eventManager.getEventsMap());
    }
}
