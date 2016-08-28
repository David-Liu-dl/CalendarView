package org.unimelb.itime.vendor.dayview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;


import org.unimelb.itime.vendor.R;
import org.unimelb.itime.vendor.helper.MyCalendar;
import org.unimelb.itime.vendor.listener.ITimeEventInterface;

/**
 * Created by yuhaoliu on 3/08/16.
 */
public class DayViewBody extends RelativeLayout {
    private static final String TAG = "MyAPP";



    public final LayoutInflater mInflater;
    public LinearLayout allDayContainer;
    public RelativeLayout body_container;
    public RelativeLayout timeRLayout;
    public RelativeLayout dividerRLayout;
    public ScrollContainerView scrollContainerView;

    public DayViewBodyController dayViewController;

    public MyCalendar myCalendar;

    public DayViewBody(Context context) {
        this(context, null);
    }

    public DayViewBody(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.calendarViewStyle);
    }

    public DayViewBody(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mInflater = LayoutInflater.from(context);
        inflate(context, R.layout.day_view_body, this);
//        setOrientation(VERTICAL);
        dayViewController = new DayViewBodyController(attrs, context);
    }

    public MyCalendar getCalendar() {
        return myCalendar;
    }

    public void setCalendar(MyCalendar myCalendar) {
        this.myCalendar = myCalendar;
    }

    public void addEvent(final ITimeEventInterface new_event){
        dividerRLayout.post(new Runnable() {
            @Override
            public void run() {
                dayViewController.addEvent(new_event);
                invalidateEvents();
            }
        });
    }

    public void removeEvent(ITimeEventInterface delete_event){
        dayViewController.removeEvent(delete_event);
    }

    public void updateEvent(ITimeEventInterface old_event, ITimeEventInterface new_event){
        dayViewController.updateEvent(old_event, new_event);
    }

    public void invalidateEvents(){
        dayViewController.reDrawEvents();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        scrollContainerView = (ScrollContainerView) findViewById(R.id.customer_day_view);
        body_container = (RelativeLayout) findViewById(R.id.body_container);
        timeRLayout = (RelativeLayout) findViewById(R.id.timeReLayout);
        dividerRLayout = (RelativeLayout) findViewById(R.id.eventRelativeLayout);
        allDayContainer = (LinearLayout) findViewById(R.id.allDayContainer);

        dayViewController.onFinishInflate(
                scrollContainerView,
                timeRLayout,
                dividerRLayout,
                body_container,
                allDayContainer);
    }
      

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        Log.i(TAG, "onAttachedToWindow: ");
        dayViewController.resetViews();

        dayViewController.initBackgroundView();



        if (myCalendar.isToday()){
            dayViewController.addNowTimeLine();
        }

        dividerRLayout.invalidate();
    }

//    private ArrayList<ITimeEventInterface> simulateEvent(){
//        String[] titles = {"This is test", "I'm an event","What's Up?","Hello?","What's Up?","What's Up?","What's Up?"};
//        Event.Type[] types = {Event.Type.PRIVATE,Event.Type.GROUP,Event.Type.PUBLIC,Event.Type.PUBLIC,Event.Type.PUBLIC,Event.Type.PUBLIC,Event.Type.PUBLIC};
//        Event.Status[] statuses = { Event.Status.COMFIRM, Event.Status.PENDING, Event.Status.PENDING, Event.Status.PENDING, Event.Status.PENDING, Event.Status.PENDING, Event.Status.PENDING};
//        ArrayList<ITimeEventInterface> events = new ArrayList<>();
//        Date dt = new Date();
//        dt.setTime(Calendar.getInstance().getTimeInMillis());
//        long interval = 3600 * 1000;
//        for (int i = 0; i < 3; i++) {
//            ITimeEventInterface event = new Event();
//            event.setTitle(titles[i]);
//            event.setStatus(statuses[i]);
//            event.setEventType(types[i]);
//            event.setStartTime(dt.getTime());
//            event.setEndTime(dt.getTime() + (int)(interval*(i+1)));
//            events.add(event);
//        }
//
//        return events;
//    }
}
