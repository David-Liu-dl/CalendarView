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
                Log.i(TAG, "timeStart: " + timeStart );
                Log.i(TAG, "endTime: " + endTime );
                events.addAll(dbManager.queryEventList(timeStart,endTime));
                Log.i(TAG, "events size: " + events.size());
                if (events.size() > 50){
                    Log.i(TAG, "real size: " + events.size());
                    events.clear();
                    return events;
                }
                return events;
            }
        });
        getFragmentManager().beginTransaction().add(R.id.david_fragment, monthDayFragment).commit();
    }

    private void initDB(){
        Calendar calendar = Calendar.getInstance();
        List<Event> events = new ArrayList<>();
        long interval = 3600 * 1000;

        for (int i = 0; i < 10000; i++) {

            long startTime = calendar.getTimeInMillis();
            long endTime = startTime + interval * (i%30);
            Log.i(TAG, "startTime: " + startTime);
            Log.i(TAG, "endTime: " + endTime);
            Event event = new Event();
            event.setTitle("" + i);
            event.setEventType(0);
            event.setStatus(0);
            event.setStartTime(startTime);
            event.setEndTime(endTime);
            events.add(event);

            calendar.setTimeInMillis(endTime);
        }

        dbManager.insertEventList(events);
    }

}
