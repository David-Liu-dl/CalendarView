package david.itime_calendar.TestActivities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import david.itime_calendar.EventManager;
import david.itime_calendar.MainActivity;
import david.itime_calendar.R;
import david.itime_calendar.bean.TimeSlot;
import david.itimecalendar.calendar.listeners.ITimeCalendarTimeslotViewListener;
import david.itimecalendar.calendar.ui.weekview.TimeSlotView;
import david.itimecalendar.calendar.ui.unitviews.DraggableTimeSlotView;
import david.itimecalendar.calendar.ui.unitviews.RcdRegularTimeSlotView;
import david.itimecalendar.calendar.util.MyCalendar;

/**
 * Created by yuhaoliu on 30/05/2017.
 */

public class TimeSlotActivity extends AppCompatActivity {
    private EventManager eventManager = MainActivity.eventManager;

    private Button switcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeslotview);
        switcher = (Button) findViewById(R.id.timeslot_switcher);
        doTest();
    }

    private void doTest(){
        final ArrayList<TimeSlot> slots = new ArrayList<>();
        initSlots(slots);

        final TimeSlotView timeslotView = (TimeSlotView) findViewById(R.id.timeslot_view);
        timeslotView.setViewMode(TimeSlotView.ViewMode.ALL_DAY_CREATE);

        timeslotView.setEventPackage(eventManager.getEventsMap());
//        timeslotView.setOnTimeSlotInnerCalendar(new TimeSlotInnerCalendarView.OnTimeSlotInnerCalendar() {
//            @Override
//            public void onCalendarBtnClick(View v, boolean result) {
//
//            }
//
//            @Override
//            public void onDayClick(Date dateClicked) {
//                timeslotView.scrollToDate(dateClicked);
//            }
//
//            @Override
//            public void onMonthScroll(Date firstDayOfNewMonth) {
//            }
//        });

        timeslotView.setOnTimeslotDurationChangedListener(new TimeSlotView.OnTimeslotDurationListener() {
            @Override
            public void onTimeslotDurationChanged(long duration) {
                if (duration == -1){
                    //means all day
//                    timeslotView.setViewMode(TimeSlotView.ViewMode.ALL_DAY_CREATE);
                }else{
                    timeslotView.setViewMode(TimeSlotView.ViewMode.NON_ALL_DAY_CREATE);
                    timeslotView.setTimeslotDuration(duration,false);
                }
            }

            @Override
            public void onTimeslotDurationBarClick() {
                timeslotView.getDurationBar().expandDurationBar();
            }
        });
        timeslotView.setITimeCalendarTimeslotViewListener(new ITimeCalendarTimeslotViewListener()  {
            @Override
            public void onAllDayRcdTimeslotClick(long dayBeginMilliseconds) {
                TimeSlot newSlot = new TimeSlot();
                newSlot.setIsAllDay(true);
                //ensure set the start time correctly, otherwise it cannot be shown
                newSlot.setStartTime(dayBeginMilliseconds);
                timeslotView.addTimeSlot(newSlot);
            }

            @Override
            public void onAllDayTimeslotClick(DraggableTimeSlotView timeSlotView) {
//                TimeSlot newSlot = new TimeSlot();
//                newSlot.setIsAllDay(true);
//                //ensure set the start time correctly, otherwise it cannot be shown
//                newSlot.setStartTime(dayBeginMilliseconds);
//                timeSlotView.addTimeSlot(newSlot);
            }

            @Override
            public void onTimeSlotCreate(DraggableTimeSlotView draggableTimeSlotView) {
                TimeSlot newSlot = new TimeSlot();
                newSlot.setStartTime(draggableTimeSlotView.getNewStartTime());
                newSlot.setEndTime(draggableTimeSlotView.getNewEndTime());
                timeslotView.addTimeSlot(newSlot);
            }

            @Override
            public void onTimeSlotClick(DraggableTimeSlotView draggableTimeSlotView) {
//                timeslotView.reloadTimeSlots(false);
            }


            @Override
            public void onRcdTimeSlotClick(RcdRegularTimeSlotView v) {
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
            public void onTimeSlotDragging(DraggableTimeSlotView draggableTimeSlotView, MyCalendar curAreaCal, int touchX, int touchY, int viewX, int viewY, String locationTime) {

            }


            @Override
            public void onTimeSlotDragDrop(DraggableTimeSlotView draggableTimeSlotView, long startTime, long endTime) {
                draggableTimeSlotView.getTimeslot().setStartTime(startTime);
                draggableTimeSlotView.getTimeslot().setEndTime(endTime);
            }

            @Override
            public void onTimeSlotDragEnd(DraggableTimeSlotView draggableTimeSlotView) {

            }

            @Override
            public void onTimeSlotEdit(DraggableTimeSlotView draggableTimeSlotView) {
                draggableTimeSlotView.getTimeslot().setStartTime(draggableTimeSlotView.getNewStartTime());
                draggableTimeSlotView.getTimeslot().setEndTime(draggableTimeSlotView.getNewEndTime());
                timeslotView.refresh();
            }

            @Override
            public void onTimeSlotDelete(DraggableTimeSlotView draggableTimeSlotView) {
                timeslotView.removeTimeslot(draggableTimeSlotView.getWrapper());
            }

            int a = 0;

            @Override
            public void onDateChanged(Date date) {
                if (a > 10){
                    return;
                }
                final ArrayList<TimeSlot> slots = new ArrayList<>();
                initSlots(slots, date);
                timeslotView.addTimeSlotList(slots);
                a ++ ;
            }
        });
        //Note: ensure calling setTimeslotDurationItems after setting listeners
        timeslotView.setTimeslotDurationItems(initList(),0);

//        timeslotView.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                for (TimeSlot slot:slots
//                        ) {
//                    timeslotView.addTimeSlot(slot);
//                }
//            }
//        },2000);




        switcher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (timeslotView.isTimeslotEnable){
//                    timeslotView.disableTimeSlot();
                }else {
//                    timeslotView.enableTimeSlot(false);
                }


            }
        });
    }
    private void initSlots(ArrayList<TimeSlot> slots, Date date){
        long startTime = date.getTime();
        long duration = 3600*1000;
        long dayInterval = 2* 3600 * 1000;
        for (int i = 0; i < 3; i++) {
            TimeSlot slot = new TimeSlot();
            slot.setStartTime(startTime);
            slot.setEndTime(startTime+duration);
            slot.setRecommended(true);
            slot.setIsAllDay(false);
            slots.add(slot);

            startTime += dayInterval;
        }
    }

    private void initSlots(ArrayList<TimeSlot> slots){
        Calendar cal = Calendar.getInstance();
        long startTime = cal.getTimeInMillis();
        long duration = 3600*1000;
        long dayInterval = 1 * 3600 * 1000;
        for (int i = 0; i < 6; i++) {
            TimeSlot slot = new TimeSlot();
            slot.setStartTime(startTime);
            slot.setEndTime(startTime+duration);
            slot.setRecommended(true);
            slot.setIsAllDay(false);
            slots.add(slot);

            startTime += dayInterval;
        }
    }

    private List initList(){
        List<TimeSlotView.DurationItem> list= new ArrayList<>();
        int target = 5;
        for (int i = 1; i < target+1; i++) {
            TimeSlotView.DurationItem item = new TimeSlotView.DurationItem();
            if (i == target){
                item.showName = "All Day";
                item.duration = -1;
            }else{
                item.showName = "" + i + " hrs";
                item.duration = i * 3600 * 1000;
            }

            list.add(item);
        }

        return list;
    }
}
