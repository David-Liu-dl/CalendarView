package org.unimelb.itime.test.david;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.unimelb.itime.test.R;
import org.unimelb.itime.test.bean.Event;
import org.unimelb.itime.vendor.agendaview.AgendaViewBody;
import org.unimelb.itime.vendor.agendaview.MonthAgendaView;
import org.unimelb.itime.vendor.dayview.DayViewBodyPagerAdapter;
import org.unimelb.itime.vendor.dayview.DayViewHeader;
import org.unimelb.itime.vendor.dayview.MonthDayView;
import org.unimelb.itime.vendor.listener.ITimeEventInterface;

import java.util.ArrayList;
import java.util.Calendar;
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
        loadData();
//        doMonthAgendaViewThings();
        doMonthDayViewThings();

    }
    private void initData(){
        this.dbManager.clearDB();
        this.initDB();
    }

    private void loadData(){
        List<Event> allEvents = dbManager.getAllEvents();
        EventManager.getInstance().getEventsMap().clear();
        for (Event event: allEvents
             ) {
            EventManager.getInstance().addEvent(event);
        }
    }

    private void doMonthDayViewThings(){
        MonthDayView monthDayFragment = (MonthDayView) findViewById(R.id.monthDayView);


        monthDayFragment.setOnCheckIfHasEvent(new DayViewHeader.OnCheckIfHasEvent() {
                @Override
                public boolean todayHasEvent(long startOfDay) {
                    Calendar calendar1 = Calendar.getInstance();
                    calendar1.setTimeInMillis(startOfDay);
                    return (EventManager.getInstance().getEventsMap().containsKey(startOfDay));
                }
        });

        monthDayFragment.setOnBodyPageChanged(new DayViewBodyPagerAdapter.OnBodyPageChanged() {

            @Override
            public List<ITimeEventInterface> loadEvents(long beginOfDayM) {

                if (EventManager.getInstance().getEventsMap().containsKey(beginOfDayM)){
                    Log.i(TAG, "size: " + EventManager.getInstance().getEventsMap().get(beginOfDayM).size());
                    return EventManager.getInstance().getEventsMap().get(beginOfDayM);
                }

                return null;
            }
        });
    }

//
//    private void doMonthAgendaViewThings(){
//        MonthAgendaView monthDayFragment = (MonthAgendaView) findViewById(R.id.monthAgendaView);
//
//        monthDayFragment.setOnCheckIfHasEvent(new DayViewHeader.OnCheckIfHasEvent() {
//                @Override
//                public boolean todayHasEvent(long startOfDay) {
//                    Calendar calendar1 = Calendar.getInstance();
//                    calendar1.setTimeInMillis(startOfDay);
//                    return (EventManager.getInstance().getEventsMap().containsKey(startOfDay));
//                }
//        });
//
//        monthDayFragment.setOnLoadEvents(new AgendaViewBody.OnLoadEvents() {
//            @Override
//            public List<ITimeEventInterface> loadTodayEvents(long beginOfDayMilliseconds) {
//                if (EventManager.getInstance().getEventsMap().containsKey(beginOfDayMilliseconds)){
//                    return EventManager.getInstance().getEventsMap().get(beginOfDayMilliseconds);
//                }
//                return null;
//            }
//        });
//    }

    private void initDB(){
        Calendar calendar = Calendar.getInstance();
        List<Event> events = new ArrayList<>();
        int[] type = {0,1,2};
        int[] status = {0,1};
        long interval = 3600 * 1000;
        int alldayCount = 0;
        for (int i = 1; i < 100; i++) {

            long startTime = calendar.getTimeInMillis();
            long endTime = startTime + interval * (i%30);
            long duration = (endTime - startTime);

            Event event = new Event();
            event.setTitle("" + i);
            event.setEventType(i%type.length);
            event.setStatus(i%status.length);
            event.setLocation("here");
            event.setStartTime(startTime);
            String urls;
            urls = ("http://esczx.baixing.com/uploadfile/2016/0427/20160427112336847.jpg");
            urls += "|" + ("http://education.news.cn/2015-05/04/127751980_14303593148421n.jpg");
            urls += "|" + ("http://i1.wp.com/pmcdeadline2.files.wordpress.com/2016/06/angelababy.jpg?crop=0px%2C107px%2C1980px%2C1327px&resize=446%2C299&ssl=1");
            event.setInvitees_urls(urls);

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
                    event_clone.setLocation("here");
                    event_clone.setInvitees_urls("");
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
