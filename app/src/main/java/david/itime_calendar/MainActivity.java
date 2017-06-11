package david.itime_calendar;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import david.itime_calendar.TestActivities.AgendaViewActivity;
import david.itime_calendar.TestActivities.DayViewActivity;
import david.itime_calendar.TestActivities.FragmentActivity;
import david.itime_calendar.TestActivities.TimeSlotActivity;
import david.itime_calendar.TestActivities.WeekViewActivity;
import david.itime_calendar.bean.Contact;
import david.itime_calendar.bean.Event;
import david.itime_calendar.bean.Invitee;

public class MainActivity extends AppCompatActivity {
    public static DBManager dbManager;
    public static EventManager eventManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.initView();
        this.initTest();

        ViewServer.get(this).addWindow(this);
    }


    private void initTest() {
        dbManager = DBManager.getInstance(this);
        eventManager = EventManager.getInstance();
        initData();
        loadData();
    }

    private void initData() {
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

    private void initDB() {
        Calendar calendar = Calendar.getInstance();
        List<Event> events = new ArrayList<>();
        List<Contact> contacts = initContact();
        int[] type = {0, 1, 2};
        int[] status = {0, 1};
        long allDayInterval = (24 * 3600 * 1000);
        long interval = (3600 * 1000);
        long startTime = calendar.getTimeInMillis();
        long endTime;
        for (int i = 1; i < 20; i++) {
            endTime = startTime + interval;
            Event event = new Event();
//            event.setIsAllDay(true);
            event.setIsAllDay(false);
//            event.setIsAllDay(i == 4);
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

            startTime = startTime + allDayInterval;
        }

        dbManager.insertEventList(events);
    }

    private List<Contact> initContact() {
        List<Contact> contacts = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            Contact contact = new Contact("" + i, "http://img.zybus.com/uploads/allimg/131213/1-131213111353.jpg", "name " + i);
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

    void initView() {
        Button dayBtn = (Button) findViewById(R.id.btn_day);
        dayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, DayViewActivity.class);
                startActivity(intent);
            }
        });

        Button weekBtn = (Button) findViewById(R.id.btn_week);
        weekBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, WeekViewActivity.class);
                startActivity(intent);
            }
        });

        Button timeslotBtn = (Button) findViewById(R.id.btn_timeslot);
        timeslotBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, TimeSlotActivity.class);
                startActivity(intent);
            }
        });

        Button agendaBtn = (Button) findViewById(R.id.btn_agenda);
        agendaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AgendaViewActivity.class);
                startActivity(intent);
            }
        });

        Button fragBtn = (Button) findViewById(R.id.btn_frag);
        fragBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, FragmentActivity.class);
                startActivity(intent);
            }
        });

//        timeslotBtn.performClick();
    }
}
