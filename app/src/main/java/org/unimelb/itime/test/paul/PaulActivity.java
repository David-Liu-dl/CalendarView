package org.unimelb.itime.test.paul;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;


import org.unimelb.itime.test.R;
import org.unimelb.itime.test.bean.Event;
import org.unimelb.itime.test.bean.Invitee;
import org.unimelb.itime.test.david.DBManager;
import org.unimelb.itime.test.david.EventManager;
import org.unimelb.itime.vendor.eventview.WeekDraggableEventView;
import org.unimelb.itime.vendor.helper.MyCalendar;
import org.unimelb.itime.vendor.timeslot.TimeSlotView;
import org.unimelb.itime.vendor.timeslotview.WeekTimeSlotView;
import org.unimelb.itime.vendor.weekview.WeekView;
import org.unimelb.itime.vendor.weekview.WeekViewBody;
import org.unimelb.itime.vendor.weekview.WeekViewHeader;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PaulActivity extends AppCompatActivity {

    private static final int PICK_PHOTO = 1;
    private List<String> mResults;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paul);

        loadData();
        WeekView weekView = (WeekView) findViewById(R.id.week_view);
        weekView.setOnWeekBodyOutterListener(new WeekViewBody.OnWeekBodyListener() {
            @Override
            public void onEventCreate(WeekDraggableEventView eventView) {

            }

            @Override
            public void onEventClick(WeekDraggableEventView eventView) {
                Log.i("MyAPP", "onEventClick: ");
            }

            @Override
            public void onEventDragStart(WeekDraggableEventView eventView) {

            }

            @Override
            public void onEventDragging(WeekDraggableEventView eventView, int x, int y) {

            }

            @Override
            public void onEventDragDrop(WeekDraggableEventView eventView) {

            }
        });
        weekView.setEventMap(EventManager.getInstance().getEventsMap());
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
