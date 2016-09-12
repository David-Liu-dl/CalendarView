package org.unimelb.itime.test.david;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.unimelb.itime.test.R;
import org.unimelb.itime.test.bean.Contact;
import org.unimelb.itime.test.bean.Event;
import org.unimelb.itime.test.bean.Invitee;
import org.unimelb.itime.test.paul.PaulActivity;
import org.unimelb.itime.vendor.dayview.DayViewBody;
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

//        initData();
        loadData();
//        doInviteesThings();

//        doMonthAgendaViewThings();
//        displayAllInvitee();
        doMonthDayViewThings();
    }

    private void doInviteesThings(){
        InviteeFragment inviteeFragment = new InviteeFragment();

        getFragmentManager().beginTransaction().add(R.id.fragment, inviteeFragment).commit();
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
            List<Invitee> invitee = event.getInvitee();
            Log.i(TAG, "loadData: " + invitee.size());
            EventManager.getInstance().addEvent(event);
        }

    }

    private void doMonthDayViewThings(){
        final MonthDayView monthDayFragment = (MonthDayView) findViewById(R.id.monthDayView);

        monthDayFragment.setOnCheckIfHasEvent(new DayViewHeader.OnCheckIfHasEvent() {
                @Override
                public boolean todayHasEvent(long startOfDay) {
                    Calendar calendar1 = Calendar.getInstance();
                    calendar1.setTimeInMillis(startOfDay);
                    return (EventManager.getInstance().getEventsMap().containsKey(startOfDay));
                }
        });

        monthDayFragment.setOnLoadEvents(new DayViewBody.OnLoadEvents() {
            @Override
            public List<ITimeEventInterface> loadEvents(long beginOfDayM) {
                if (EventManager.getInstance().getEventsMap().containsKey(beginOfDayM)){
                return EventManager.getInstance().getEventsMap().get(beginOfDayM);
            }
                return null;
            }
        });

        monthDayFragment.setOnEventChanged(new DayViewBody.OnEventChanged() {
            @Override
            public void OnEventChanged(ITimeEventInterface event) {
//                Intent intent = new Intent(DavidActivity.this, PaulActivity.class);
//                startActivity(intent);
            }
        });

        monthDayFragment.setOnDgClick(new DayViewBody.OnDgClickListener() {
            @Override
            public void onDgClick(ITimeEventInterface event) {
            }
        });

//        monthDayFragment.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                Event event = new Event();
//                event.setTitle("new added");
//                event.setStartTime(Calendar.getInstance().getTimeInMillis());
//                event.setEndTime(Calendar.getInstance().getTimeInMillis() + 60 * 60 * 1000);
//                EventManager.getInstance().addEvent(event);
//                monthDayFragment.reloadCurrentBodyEvents();
//                monthDayFragment.invalidate();
//            }
//        },5000);
    }

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
        List<Contact> contacts = initContact();

        int[] type = {0,1,2};
        int[] status = {0,1};
        long interval = 3600 * 1000;
        int alldayCount = 0;
        for (int i = 1; i < 100; i++) {

            long startTime = calendar.getTimeInMillis();
            long endTime = startTime + interval * (i%30);
            long duration = (endTime - startTime);

            Event event = new Event();
            event.setEventUid("" + i);
            event.setTitle("" + i);
            event.setEventType(i%type.length);
            event.setStatus(i%status.length);
            event.setLocation("here");
            event.setStartTime(startTime);

            List<Invitee> inviteeList = new ArrayList<>();

            Invitee invitee1 = new Invitee();
            invitee1.setEventUid("" + i);
            invitee1.setContact(contacts.get(0));
            invitee1.setInviteeUid(contacts.get(0).getContactUid());
            inviteeList.add(invitee1);

            Invitee invitee2 = new Invitee();
            invitee2.setEventUid("" + i);
            invitee2.setContact(contacts.get(1));
            invitee2.setInviteeUid(contacts.get(1).getContactUid());
            inviteeList.add(invitee2);

            dbManager.insertInviteeList(inviteeList);
            event.setInvitee(inviteeList);

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
//                    event_clone.setInviteesUrls("");
                    title = title + " all day";
                }
                alldayCount = 0;
            }

            calendar.setTimeInMillis(endTime);

        }

        dbManager.insertEventList(events);
    }

    private List<Contact> initContact(){
        List<Contact> contacts = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            Contact contact = new Contact(""+i, "http://img.zybus.com/uploads/allimg/131213/1-131213111353.jpg", "name " + i);
            contacts.add(contact);
            dbManager.insertContact(contact);
        }

        return contacts;
    }

}
