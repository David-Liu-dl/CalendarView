package david.itime_calendar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import david.itime_calendar.bean.Contact;
import david.itime_calendar.bean.Event;
import david.itime_calendar.bean.Invitee;
import david.itimecalendar.calendar.calendar.mudules.monthview.EventController;
import david.itimecalendar.calendar.calendar.mudules.monthview.MonthView;
import david.itimecalendar.calendar.listeners.ITimeEventInterface;
import david.itimecalendar.calendar.unitviews.DraggableEventView;
import david.itimecalendar.calendar.util.BaseUtil;
import david.itimecalendar.calendar.util.MyCalendar;

public class MainActivity extends AppCompatActivity {
    private DBManager dbManager;
    private EventManager eventManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.initTest();
        this.monthviewTest();

        ViewServer.get(this).addWindow(this);
    }

    private void initTest(){
        dbManager = DBManager.getInstance(this);
        eventManager = EventManager.getInstance();
        initData();
        loadData();
    }

    private void monthviewTest(){
        final MonthView monthView = (MonthView) findViewById(R.id.monthview);
        monthView.setOnBodyListener(new EventController.OnEventListener() {
            @Override
            public boolean isDraggable(DraggableEventView eventView) {
                return true;
            }

            @Override
            public void onEventCreate(DraggableEventView eventView) {

            }

            @Override
            public void onEventClick(DraggableEventView eventView) {

            }

            @Override
            public void onEventDragStart(DraggableEventView eventView) {

            }

            @Override
            public void onEventDragging(DraggableEventView eventView, MyCalendar curAreaCal, int x, int y) {

            }

            @Override
            public void onEventDragDrop(DraggableEventView eventView) {
                ITimeEventInterface event = eventView.getEvent();
                BaseUtil.printEventTime("before",event);

                eventManager.updateEvent((Event) event, eventView.getStartTimeM(), eventView.getEndTimeM());

                BaseUtil.printEventTime("end",event);
                monthView.refresh();
            }
        });
        monthView.setEventPackage(eventManager.getEventsMap());
//        monthView.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                int count = 0;
//
//                while (count < 2){
//                    try {
//                        Thread.sleep(3000);
//                        if (count == 0){
//                            Calendar cal = Calendar.getInstance();
//                            cal.add(Calendar.DATE, -17);
//                            monthView.scrollToDate(cal.getTime());
//                        }
//
//                        if (count == 1){
//                            Calendar cal = Calendar.getInstance();
//                            monthView.scrollToDate(cal.getTime());
//                        }
//
//                        count++;
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }, 1000);
    }

    private void initData(){
        this.dbManager.clearDB();
        this.initDB();
    }

    private void loadData() {
        List<Event> allEvents = dbManager.getAllEvents();
        EventManager.getInstance().getEventsMap().clearPackage();
        for (Event event : allEvents
                ) {
            EventManager.getInstance().addEvent(event);
        }
    }


    private void initDB(){
        Calendar calendar = Calendar.getInstance();
        List<Event> events = new ArrayList<>();
        List<Contact> contacts = initContact();
        int[] type = {0,1,2};
        int[] status = {0,1};
        long interval = 3600 * 1000;
        long startTime = calendar.getTimeInMillis();
        long endTime;
        for (int i = 1; i < 10; i++) {
            endTime = startTime + (3600*1000);

            Event event = new Event();
            event.setEventUid("" + i);
            event.setTitle("adawdwadwadaw" + i);
            event.setDisplayEventType(1);
            event.setDisplayStatus("#63ADF2|slash|icon_normal");
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

            startTime = startTime + 24*3600*1000;
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

    public void onDestroy() {
        super.onDestroy();
        ViewServer.get(this).removeWindow(this);
    }

    public void onResume() {
        super.onResume();
        ViewServer.get(this).setFocusedWindow(this);
    }
}
