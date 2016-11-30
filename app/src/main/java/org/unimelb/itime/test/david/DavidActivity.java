package org.unimelb.itime.test.david;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.unimelb.itime.test.R;
import org.unimelb.itime.test.RuleFactory.RuleFactory;
import org.unimelb.itime.test.RuleFactory.RuleModel;
import org.unimelb.itime.test.bean.Contact;
import org.unimelb.itime.test.bean.Event;
import org.unimelb.itime.test.bean.Invitee;
import org.unimelb.itime.vendor.dayview.FlexibleLenViewBody;
import org.unimelb.itime.vendor.dayview.MonthDayView;
import org.unimelb.itime.vendor.eventview.DayDraggableEventView;
import org.unimelb.itime.vendor.helper.MyCalendar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DavidActivity extends AppCompatActivity {
    private final String TAG= "MyAPP";
    private DBManager dbManager;
    private EventManager eventManager;
    private MonthDayView monthDayView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_david);

//        Calendar cal1 = Calendar.getInstance();
//
//        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
//        Date startDate = new Date();
//        try {
//            startDate = df.parse("20150611");
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//
//        cal1.setTime(startDate);
//
//        Calendar cal2 = Calendar.getInstance();
////        cal2.add(Calendar.DATE,3);
//
//        Calendar cal3 = Calendar.getInstance();
//        cal3.add(Calendar.MONTH,1);
//
//        String[] recurrence = {"RRULE:FREQ=WEEKLY;INTERVAL=1"
//        };
//
//        RuleModel module = RuleFactory.getInstance().getRuleModel(cal1.getTimeInMillis(), cal2.getTimeInMillis(), recurrence);
//        ArrayList<Long> result = module.getOccurenceDates(cal2.getTimeInMillis(),cal3.getTimeInMillis());
//        Calendar printCal = Calendar.getInstance();
//        for (Long timeM:result
//             ) {
//            printCal.setTimeInMillis(timeM);
//            Log.i(TAG, "Date: " + printCal.getTime());
//        }

//        Cloner cloner=new Cloner();
//        Object cloned = cloner.deepClone(someObject);
        eventThing();
    }
    private void eventThing(){
        dbManager = DBManager.getInstance(this);
        eventManager = EventManager.getInstance();
        initData();
        loadData();
//        doInviteesThings();

//        doMonthAgendaViewThings();
//        displayAllInvitee();
        doMonthDayViewThings();
    }

    private void doMonthDayViewThings(){
        Button back = (Button) findViewById(R.id.back);
        monthDayView = (MonthDayView) findViewById(R.id.monthDayView);
        monthDayView.setDayEventMap(eventManager.getEventsMap());
        monthDayView.setEventClassName(Event.class);
        monthDayView.setOnHeaderListener(new MonthDayView.OnHeaderListener() {
            @Override
            public void onMonthChanged(MyCalendar calendar) {
                Log.i(TAG, "onMonthChanged: " + calendar.getCalendar().getTime());
            }
        });
        monthDayView.setOnBodyOuterListener(new FlexibleLenViewBody.OnBodyListener() {
            @Override
            public boolean isDraggable(DayDraggableEventView eventView) {
                return true;
            }

            @Override
            public void onEventCreate(DayDraggableEventView eventView) {

            }

            @Override
            public void onEventClick(DayDraggableEventView eventView) {
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(eventView.getStartTimeM());

                EventManager.getInstance().updateEvent((Event) eventView.getEvent(),10,10);

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
                Log.i(TAG, "onEventDragDrop: s" + cal.getTime());
                cal.setTimeInMillis(eventView.getEndTimeM());
                Log.i(TAG, "onEventDragDrop: " + cal.getTime());
            }

        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                monthDayView.backToToday();
            }
        });

    }

//    private void doMonthAgendaViewThings(){
//        Button back = (Button) findViewById(R.id.back);
//        final MonthAgendaView monthDayView = (MonthAgendaView) findViewById(R.id.monthAgendaView);
//        monthDayView.setDayEventMap(eventManager.getEventsMap());
//        back.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                monthDayView.backToToday();
//            }
//        });
//    }

//    private void doInviteesThings(){
//        InviteeFragment inviteeFragment = new InviteeFragment();
//        getFragmentManager().beginTransaction().add(R.id.fragment, inviteeFragment).commit();
//    }

    private void initData(){
        this.dbManager.clearDB();
        this.initDB();
    }

    private void loadData(){
        List<Event> allEvents = dbManager.getAllEvents();
        EventManager.getInstance().getEventsMap().clearPackage();
        Event testE = null;
        for (Event event: allEvents
             ) {
            String[] rec = {"RRULE:FREQ=WEEKLY;INTERVAL=1"};
            event.setRecurrence(rec);
            EventManager.getInstance().addEvent(event);
//            testE = event;
        }
//        final Event e=testE;
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    Event newE = (Event) e.clone();
//                    newE.setEndTime(newE.getStartTime() + 60*3600);
//                    EventManager.getInstance().updateRepeatedEvent(newE);
//
//                } catch (CloneNotSupportedException e1) {
//                    e1.printStackTrace();
//                }
//            }
//        }, 5000);

//        EventManager.getInstance().loadRepeatedEvent(nowRepeatedStartAt.getTimeInMillis(),nowRepeatedEndAt.getTimeInMillis());


    }

//    private void doMonthDayViewThings(){
//        final MonthDayView monthDayFragment = (MonthDayView) findViewById(R.id.monthDayView);
//
////        monthDayFragment.postDelayed(new Runnable() {
////            @Override
////            public void run() {
////                Event event = new Event();
////                event.setTitle("new added");
////                event.setStartTime(Calendar.getInstance().getTimeInMillis());
////                event.setEndTime(Calendar.getInstance().getTimeInMillis() + 60 * 60 * 1000);
////                EventManager.getInstance().addEvent(event);
////            }
////        },5000);
//    }

//    private void doMonthAgendaViewThings(){
//        MonthAgendaView monthDayFragment = (MonthAgendaView) findViewById(R.id.monthAgendaView);
//
//        monthDayFragment.setDayEventMap(EventManager.getInstance().getEventsMap());
//    }

    private void initDB(){
        Calendar calendar = Calendar.getInstance();
        List<Event> events = new ArrayList<>();
        List<Contact> contacts = initContact();
        calendar.add(Calendar.DATE, -699);
        int[] type = {0,1,2};
        int[] status = {0,1};
        long interval = 3600 * 1000;
        for (int i = 1; i < 5; i++) {
            long startTime = calendar.getTimeInMillis();
            long endTime = startTime + interval;
            long duration = (endTime - startTime);

            Event event = new Event();
            event.setEventUid("" + i);
            event.setTitle("adawdwadwadaw" + i);
            event.setDisplayEventType(i%type.length);
            event.setDisplayStatus(i%status.length);
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

            event.setEndTime(endTime);
            events.add(event);

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
