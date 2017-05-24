package david.itime_calendar;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import david.itime_calendar.bean.Contact;
import david.itime_calendar.bean.Event;
import david.itime_calendar.bean.Invitee;
import david.itime_calendar.bean.TimeSlot;
import david.itimecalendar.calendar.calendar.mudules.weekview.WeekView;

public class MainActivity extends AppCompatActivity {
    private DBManager dbManager;
    private EventManager eventManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.initTest();
//        this.monthviewTest();
//        this.timeSlotTest();
//       this.weekViewHeaderTest();
        this.weekViewTest();

        ViewServer.get(this).addWindow(this);
    }

//    private void weekViewHeaderTest(){
//        final WeekViewHeader header = (WeekViewHeader)findViewById(R.id.week_header);
//        header.setStartDate(new Date());
//        Calendar calendar = Calendar.getInstance();
//        calendar.add(Calendar.DATE, 1);
//        final Date date = calendar.getTime();
//        header.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                ValueAnimator animtor = ValueAnimator.ofInt(0,100);
//                animtor.setDuration(1000);
////                animtor.setRepeatCount(ValueAnimator.INFINITE);
//                animtor.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//                    @Override
//                    public void onAnimationUpdate(ValueAnimator animation) {
//                        Log.i("updateDate", "onAnimationUpdate: updateDate ");
//                        header.updateDate(date, (int) animation.getAnimatedValue());
//                    }
//                });
//
//                animtor.start();
//            }
//        },1000);
//    }

    private void initTest(){
        dbManager = DBManager.getInstance(this);
        eventManager = EventManager.getInstance();
        initData();
        loadData();
    }

//    private void monthviewTest(){
//        final MonthView monthView = (MonthView) findViewById(R.id.monthview);
//        monthView.setOnBodyListener(new EventController.OnEventListener() {
//            @Override
//            public boolean isDraggable(DraggableEventView eventView) {
//                return true;
//            }
//
//            @Override
//            public void onEventCreate(DraggableEventView eventView) {
//
//            }
//
//            @Override
//            public void onEventClick(DraggableEventView eventView) {
//
//            }
//
//            @Override
//            public void onEventDragStart(DraggableEventView eventView) {
//
//            }
//
//            @Override
//            public void onEventDragging(DraggableEventView eventView, MyCalendar curAreaCal, int x, int y) {
//
//            }
//
//            @Override
//            public void onEventDragDrop(DraggableEventView eventView) {
//                ITimeEventInterface event = eventView.getEvent();
//                BaseUtil.printEventTime("before",event);
//
//                eventManager.updateEvent((Event) event, eventView.getStartTimeM(), eventView.getEndTimeM());
//
//                BaseUtil.printEventTime("end",event);
//                monthView.refresh();
//            }
//        });
//        monthView.setEventPackage(eventManager.getEventsMap());
//    }

    private void weekViewTest(){
        WeekView weekview = (WeekView) findViewById(R.id.week_view);
        weekview.setEventPackage(eventManager.getEventsMap());
    }

//    private void timeSlotTest(){
//        ArrayList<TimeSlot> slots = new ArrayList<>();
//        initSlots(slots);
//
//        final MonthView weekView = (MonthView) findViewById(R.id.monthview);
//        weekView.setEventPackage(eventManager.getEventsMap());
//        weekView.enableTimeSlot();
//        weekView.setOnTimeSlotInnerCalendar(new TimeSlotInnerCalendarView.OnTimeSlotInnerCalendar() {
//            @Override
//            public void onCalendarBtnClick(View v, boolean result) {
//
//            }
//
//            @Override
//            public void onDayClick(Date dateClicked) {
//                Calendar cal = Calendar.getInstance();
//                cal.setTime(dateClicked);
//                weekView.scrollToDate(cal.getTime());
//            }
//
//            @Override
//            public void onMonthScroll(Date firstDayOfNewMonth) {
//            }
//        });
//        weekView.setOnTimeSlotListener(new TimeSlotController.OnTimeSlotListener() {
//            @Override
//            public void onTimeSlotCreate(DraggableTimeSlotView draggableTimeSlotView) {
//                Log.i("", "onTimeSlotCreate: ");
//            }
//
//            @Override
//            public void onTimeSlotClick(DraggableTimeSlotView draggableTimeSlotView) {
////                weekView.reloadTimeSlots(false);
//            }
//
//            @Override
//            public void onRcdTimeSlotClick(RecommendedSlotView v) {
//                v.getWrapper().setSelected(true);
////                weekView.reloadTimeSlots(false);
//            }
//
//            @Override
//            public void onTimeSlotDragStart(DraggableTimeSlotView draggableTimeSlotView) {
//
//            }
//
//            @Override
//            public void onTimeSlotDragging(DraggableTimeSlotView draggableTimeSlotView, int x, int y) {
//
//            }
//
//            @Override
//            public void onTimeSlotDragDrop(DraggableTimeSlotView draggableTimeSlotView, long startTime, long endTime) {
//
//            }
//
//            @Override
//            public void onTimeSlotEdit(DraggableTimeSlotView draggableTimeSlotView) {
//            }
//
//            @Override
//            public void onTimeSlotDelete(DraggableTimeSlotView draggableTimeSlotView) {
//            }
//        });
//
//
//        HashMap<String, Integer> numSlot = new HashMap<>();
//
//
//        for (TimeSlot slot:slots
//                ) {
//            weekView.addTimeSlot(slot);
//        }
//    }

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

    private void initSlots(ArrayList<TimeSlot> slots){
        Calendar cal = Calendar.getInstance();
        long startTime = cal.getTimeInMillis();
        long duration = 3*3600*1000;
        for (int i = 0; i < 10; i++) {
            TimeSlot slot = new TimeSlot();
            slot.setStartTime(startTime);
            slot.setEndTime(startTime+duration);
            slot.setRecommended(true);
            slots.add(slot);

            startTime += 5 * 3600 * 1000;
        }
    }
}
