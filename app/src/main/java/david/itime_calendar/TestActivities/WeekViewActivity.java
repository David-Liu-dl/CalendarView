package david.itime_calendar.TestActivities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import david.itime_calendar.EventManager;
import david.itime_calendar.MainActivity;
import david.itime_calendar.R;
import david.itime_calendar.bean.Event;
import david.itimecalendar.calendar.mudules.monthview.DayViewBody;
import david.itimecalendar.calendar.mudules.monthview.EventController;
import david.itimecalendar.calendar.mudules.weekview.WeekView;
import david.itimecalendar.calendar.listeners.ITimeEventInterface;
import david.itimecalendar.calendar.unitviews.DraggableEventView;
import david.itimecalendar.calendar.util.MyCalendar;

/**
 * Created by yuhaoliu on 30/05/2017.
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
        weekview.setEventPackage(eventManager.getEventsMap());
//        weekview.setDisableCellScroll(true);
        weekview.setOnBodyEventListener(new DayViewBody.OnViewBodyEventListener() {
            @Override
            public void onAllDayEventClick(ITimeEventInterface event) {

            }

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

                eventManager.updateEvent((Event) event, eventView.getStartTimeM(), eventView.getEndTimeM());
                weekview.refresh();
            }
        });
    }
}
