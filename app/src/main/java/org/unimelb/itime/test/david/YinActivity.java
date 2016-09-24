package org.unimelb.itime.test.david;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.unimelb.itime.test.R;
import org.unimelb.itime.test.bean.Contact;
import org.unimelb.itime.test.bean.Event;
import org.unimelb.itime.test.bean.Invitee;
import org.unimelb.itime.vendor.agendaview.MonthAgendaView;
import org.unimelb.itime.vendor.dayview.FlexibleLenViewBody;
import org.unimelb.itime.vendor.dayview.MonthDayView;
import org.unimelb.itime.vendor.eventview.DayDraggableEventView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class YinActivity extends AppCompatActivity {
    private final static String TAG = "MyAPP";
    private DBManager dbManager;
    private EventManager eventManager;
    private MonthDayView monthDayView;
    private MonthAgendaView monthAgendaView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yin);
        dbManager = DBManager.getInstance(this);
        eventManager = EventManager.getInstance();

//        initDB();

        init();

    }

    private void init(){
        loadData();
        monthDayView = (MonthDayView) findViewById(R.id.monthDayView);
        monthDayView.setDayEventMap(eventManager.getEventsMap());
        monthDayView.setEventClassName(Event.class);
        monthDayView.setOnBodyOuterListener(new FlexibleLenViewBody.OnBodyListener() {
            @Override
            public void onEventCreate(DayDraggableEventView eventView) {

            }

            @Override
            public void onEventClick(DayDraggableEventView eventView) {
                Log.i(TAG, "onEventClick: " + eventView.getEvent().getTitle());
            }

            @Override
            public void onEventDragStart(DayDraggableEventView eventView) {
                eventView.setEvent(new Event());
            }

            @Override
            public void onEventDragging(DayDraggableEventView eventView, int x, int y) {

            }

            @Override
            public void onEventDragDrop(DayDraggableEventView eventView) {
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(eventView.getStartTimeM());
                Log.i(TAG, "onEventDragDrop: " + cal.getTime());
            }

        });
//
//        monthDayView.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                Event event = new Event();
//                event.setEventUid("213123");
//                event.setTitle("new added");
//                event.setEventType(1);
//                event.setStatus(1);
//                event.setLocation("here");
//                event.setStartTime(Calendar.getInstance().getTimeInMillis());
//                event.setEndTime(Calendar.getInstance().getTimeInMillis() + 60 * 60 * 1000);
//                EventManager.getInstance().addEvent(event);
//
//                monthDayView.reloadEvents();
//            }
//        },5000);
    }

    private void loadData(){
        List<Event> allEvents = dbManager.getAllEvents();
        EventManager.getInstance().getEventsMap().clear();
        for (Event event: allEvents
                ) {
            EventManager.getInstance().addEvent(event);
        }

    }

    private void initAllDayDB(){

    }

    private void initDB(){
        dbManager.clearDB();
        Calendar calendar = Calendar.getInstance();
        List<Event> events = new ArrayList<>();
        List<Contact> contacts = initContact();

        int[] type = {0,1,2};
        int[] status = {0,1};
        long interval = 3600 * 1000;
        int alldayCount = 0;
        String uuuid = "";
        for (int i = 1; i < 100; i++) {

            long startTime = calendar.getTimeInMillis();
//            long endTime = startTime + interval * (i%30);
            long endTime = startTime + 25*3600*1000;
            long duration = (endTime - startTime);

            Event event = new Event();
            event.setEventUid("" + i);
            event.setEventType(i%type.length);
            event.setStatus(i%status.length);
            event.setLocation("here");
            event.setStartTime(startTime);
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(startTime);
            SimpleDateFormat sdf = new SimpleDateFormat("dd HH:mm");
            event.setTitle(sdf.format(cal.getTime()) + "");

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
                for (int j = 0; j < 2; j++) {
                    uuuid = uuuid + "a";
                    Event event_clone = new Event();
                    event_clone.setEventUid(uuuid);
                    event_clone.setTitle(title);
                    event_clone.setEventType(0);
                    event_clone.setStatus(0);
                    event_clone.setStartTime(startTime);
                    event_clone.setEndTime(endTime);
                    event_clone.setLocation("here");
//                    event_clone.setInviteesUrls("");
                    title = title + " all day";
                    events.add(event_clone);
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
