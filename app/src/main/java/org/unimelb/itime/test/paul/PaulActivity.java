package org.unimelb.itime.test.paul;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;


import org.unimelb.itime.test.R;
import org.unimelb.itime.test.bean.Event;
import org.unimelb.itime.test.david.DBManager;
import org.unimelb.itime.test.david.EventManager;
import org.unimelb.itime.vendor.eventview.DayDraggableEventView;
import org.unimelb.itime.vendor.weekview.WeekView;

import java.util.ArrayList;
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
        WeekView weekView = (WeekView) findViewById(R.id.week_view);
        weekView.setEventClassName(Event.class);
//        weekView.setOnWeekBodyOutterListener(new WeekViewBody.OnWeekBodyListener() {
//            @Override
//            public void onEventCreate(DayDraggableEventView eventView) {
//
//            }
//
//            @Override
//            public void onEventClick(DayDraggableEventView eventView) {
//                Log.i("MyAPP", "onEventClick: ");
//            }
//
//            @Override
//            public void onEventDragStart(DayDraggableEventView eventView) {
//
//            }
//
//            @Override
//            public void onEventDragging(DayDraggableEventView eventView, int x, int y) {
//
//            }
//
//            @Override
//            public void onEventDragDrop(DayDraggableEventView eventView) {
//
//            }
//        });
        Log.i(TAG, "onCreate: " + EventManager.getInstance().getEventsMap().size());
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
