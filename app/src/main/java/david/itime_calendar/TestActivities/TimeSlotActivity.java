package david.itime_calendar.TestActivities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import david.itime_calendar.EventManager;
import david.itime_calendar.MainActivity;
import david.itime_calendar.R;
import david.itime_calendar.bean.TimeSlot;
import david.itimecalendar.calendar.calendar.mudules.monthview.MonthView;
import david.itimecalendar.calendar.calendar.mudules.monthview.TimeSlotController;
import david.itimecalendar.calendar.calendar.mudules.weekview.TimeSlotView;
import david.itimecalendar.calendar.calendar.mudules.weekview.WeekView;
import david.itimecalendar.calendar.listeners.ITimeEventInterface;
import david.itimecalendar.calendar.listeners.ITimeTimeSlotInterface;
import david.itimecalendar.calendar.unitviews.DraggableTimeSlotView;
import david.itimecalendar.calendar.unitviews.RecommendedSlotView;
import david.itimecalendar.calendar.unitviews.TimeSlotInnerCalendarView;
import david.itimecalendar.calendar.util.MyCalendar;

/**
 * Created by yuhaoliu on 30/05/2017.
 */

public class TimeSlotActivity extends AppCompatActivity {
    private EventManager eventManager = MainActivity.eventManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeslotview);

        doTest();
    }

    private void doTest(){
        ArrayList<TimeSlot> slots = new ArrayList<>();
        initSlots(slots);

        final TimeSlotView timeslotView = (TimeSlotView) findViewById(R.id.timeslot_view);
        timeslotView.setEventPackage(eventManager.getEventsMap());
        timeslotView.enableTimeSlot();
        timeslotView.setOnTimeSlotInnerCalendar(new TimeSlotInnerCalendarView.OnTimeSlotInnerCalendar() {
            @Override
            public void onCalendarBtnClick(View v, boolean result) {

            }

            @Override
            public void onDayClick(Date dateClicked) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(dateClicked);
                timeslotView.scrollToDate(cal.getTime());
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
            }
        });
        timeslotView.setOnTimeSlotListener(new TimeSlotController.OnTimeSlotListener() {
            @Override
            public void onTimeSlotCreate(DraggableTimeSlotView draggableTimeSlotView) {
                Log.i("", "onTimeSlotCreate: ");
            }

            @Override
            public void onTimeSlotClick(DraggableTimeSlotView draggableTimeSlotView) {
//                timeslotView.reloadTimeSlots(false);
            }


            @Override
            public void onRcdTimeSlotClick(RecommendedSlotView v) {
                v.getWrapper().setSelected(true);
                TimeSlot newSlot = new TimeSlot();
                newSlot.setStartTime(v.getWrapper().getTimeSlot().getStartTime());
                newSlot.setEndTime(v.getWrapper().getTimeSlot().getEndTime());
                timeslotView.addTimeSlot(newSlot);
            }

            @Override
            public void onTimeSlotDragStart(DraggableTimeSlotView draggableTimeSlotView) {

            }

            @Override
            public void onTimeSlotDragging(DraggableTimeSlotView draggableTimeSlotView, MyCalendar curRealCal, int x, int y) {

            }

            @Override
            public void onTimeSlotDragDrop(DraggableTimeSlotView draggableTimeSlotView, long startTime, long endTime) {
                draggableTimeSlotView.getTimeslot().setStartTime(startTime);
                draggableTimeSlotView.getTimeslot().setEndTime(endTime);
            }

            @Override
            public void onTimeSlotEdit(DraggableTimeSlotView draggableTimeSlotView) {
            }

            @Override
            public void onTimeSlotDelete(DraggableTimeSlotView draggableTimeSlotView) {
                timeslotView.removeTimeslot(draggableTimeSlotView.getWrapper());
            }
        });


        HashMap<String, Integer> numSlot = new HashMap<>();


        for (TimeSlot slot:slots
                ) {
            timeslotView.addTimeSlot(slot);
        }
    }

    private void initSlots(ArrayList<TimeSlot> slots){
        Calendar cal = Calendar.getInstance();
        long startTime = cal.getTimeInMillis();
        long duration = 3*3600*1000;
        for (int i = 0; i < 10; i++) {
            TimeSlot slot = new TimeSlot();
            slot.setStartTime(startTime);
            slot.setEndTime(startTime+duration);
            slot.setRecommended(true);
            slots.add(slot);

            startTime += 5 * 3600 * 1000;
        }
    }
}
