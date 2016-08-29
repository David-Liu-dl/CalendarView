package org.unimelb.itime.test.david;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.unimelb.itime.test.R;
import org.unimelb.itime.test.bean.Event;
import org.unimelb.itime.vendor.dayview.DayViewBodyPagerAdapter;
import org.unimelb.itime.vendor.listener.ITimeEventInterface;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DavidActivity extends AppCompatActivity {
    private final String TAG= "MyAPP";
    DBManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_david);


        dbManager = DBManager.getInstance(this.getApplicationContext());
        //init DB
//        initData();
        doThings();


    }
    private void initData(){
        this.dbManager.clearDB();
        this.initDB();
        Log.i(TAG, "onCreate: ");
    }

    private void doThings(){
        CalendarMonthDayFragment monthDayFragment = new CalendarMonthDayFragment();
        monthDayFragment.setOnBodyPageChanged(new DayViewBodyPagerAdapter.OnBodyPageChanged() {
            @Override
            public List<ITimeEventInterface> updateEvent(long timeStart, long endTime) {
                List<ITimeEventInterface> events = new ArrayList<>();
                events.addAll(dbManager.queryEventList(timeStart,endTime));
                if (events.size() > 50){
                    events.clear();
                    return events;
                }
                return events;
            }
        });

        AttendeeFragment pageF = new AttendeeFragment();
        getFragmentManager().beginTransaction().add(R.id.david_fragment, monthDayFragment).commit();
    }

    private void initDB(){
        Calendar calendar = Calendar.getInstance();
        List<Event> events = new ArrayList<>();
        int[] type = {0,1,2};
        int[] status = {0,1};
        long interval = 3600 * 1000;
        int alldayCount = 0;
        for (int i = 0; i < 100000; i++) {

            long startTime = calendar.getTimeInMillis();
            long endTime = startTime + interval * (i%30);
            long duration = (endTime - startTime);

            Log.i(TAG, "startTime: " + startTime);
            Log.i(TAG, "endTime: " + endTime);
            Event event = new Event();
            event.setTitle("" + i);
            event.setEventType(i%type.length);
            event.setStatus(i%status.length);
            event.setStartTime(startTime);

            long realEnd = endTime;
            long temp = duration;
            while (temp > 3 * 60 * 60 * 1000 ){
                temp = temp/2;
                realEnd -= temp;
            }

            event.setEndTime(realEnd);
            events.add(event);

            if (duration >= 24 * 3600 * 1000 && alldayCount < 3){
                String title = "All day";
                for (int j = 0; j < 4; j++) {
                    Event event_clone = new Event();
                    event_clone.setTitle(title);
                    event_clone.setEventType(0);
                    event_clone.setStatus(0);
                    event_clone.setStartTime(startTime);
                    event_clone.setEndTime(endTime);
                    events.add(event_clone);
                    title = title + " all day";
                }
                alldayCount = 0;
            }

            calendar.setTimeInMillis(endTime);

        }

        dbManager.insertEventList(events);
    }

}
