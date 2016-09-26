package org.unimelb.itime.test.paul;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;


import org.unimelb.itime.test.R;
import org.unimelb.itime.test.bean.Event;
import org.unimelb.itime.test.david.DBManager;
import org.unimelb.itime.test.david.EventManager;
import org.unimelb.itime.vendor.dayview.FlexibleLenViewBody;
import org.unimelb.itime.vendor.eventview.DayDraggableEventView;
import org.unimelb.itime.vendor.weekview.WeekView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class PaulActivity extends AppCompatActivity {

    private static final int PICK_PHOTO = 1;
    private static final String TAG = "MyAPP";
    private List<String> mResults;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paul);
        loadData();
        final WeekView weekView = (WeekView) findViewById(R.id.week_view);
        weekView.setEventClassName(Event.class);
        weekView.setOnBodyOuterListener(new FlexibleLenViewBody.OnBodyListener() {
            @Override
            public void onEventCreate(DayDraggableEventView eventView) {
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(eventView.getStartTimeM());
                Log.i(TAG, "cal: " + cal.getTime());
            }

            @Override
            public void onEventClick(DayDraggableEventView eventView) {
                Log.i(TAG, "click2: " + " title: " + eventView.getEvent().getTitle());
                weekView.reloadEvents();
            }

            @Override
            public void onEventDragStart(DayDraggableEventView eventView) {

            }

            @Override
            public void onEventDragging(DayDraggableEventView eventView, int x, int y) {

            }

            @Override
            public void onEventDragDrop(DayDraggableEventView eventView) {
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(eventView.getStartTimeM());
                Log.i(TAG, "onEventDragDrop: " + cal.getTime());
                weekView.reloadEvents();
            }
        });
//        weekView.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                Calendar cal = Calendar.getInstance();
//                long interval = 24 * 60 * 60 * 1000;
//                long duration = 60 * 60 * 1000;
//                for (int i = 0; i < 3; i++) {
//                    WeekView.TimeSlotStruct struct = new WeekView.TimeSlotStruct();
//                    struct.startTime = cal.getTimeInMillis() + i * interval;
//                    struct.endTime = struct.startTime + duration;
//                    weekView.addTimeSlot(struct);
//                }
//                Log.i(TAG, "run: done");
//                weekView.reloadTimeSlots();
//            }
//        },2000);
//        weekView.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                weekView.updateTimeSlotsDuration(1800 * 1000);
//            }
//        },6000);
        weekView.setDayEventMap(EventManager.getInstance().getEventsMap());
    }


    private void showResult(ArrayList<String> paths){
        if(mResults == null){
            mResults = new ArrayList<String>();

        }
        mResults.clear();
        mResults.addAll(paths);

    }


    private void loadData(){
        List<Event> allEvents = DBManager.getInstance(getApplicationContext()).getAllEvents();
        EventManager.getInstance().getEventsMap().clear();
        for (Event event: allEvents
                ) {
            EventManager.getInstance().addEvent(event);
        }

    }
}
