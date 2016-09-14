//package org.unimelb.itime.vendor.dayview;
//
//import android.content.Context;
//import android.util.AttributeSet;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.widget.LinearLayout;
//import android.widget.RelativeLayout;
//
//
//import org.unimelb.itime.vendor.R;
//import org.unimelb.itime.vendor.eventview.DayDraggableEventView;
//import org.unimelb.itime.vendor.helper.MyCalendar;
//import org.unimelb.itime.vendor.listener.ITimeEventInterface;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//
///**
// * Created by yuhaoliu on 3/08/16.
// */
//public class DayViewBody extends RelativeLayout {
//    private static final String TAG = "MyAPP";
//
//    public final LayoutInflater mInflater;
//    public LinearLayout allDayContainer;
//    public RelativeLayout body_container;
//    public RelativeLayout timeRLayout;
//    public RelativeLayout dividerRLayout;
//    public ScrollContainerView scrollContainerView;
//
//    public DayViewBodyController dayViewController;
//
//    private List<ITimeEventInterface> events = new ArrayList<>();
//    private DayViewBodyController.OnBodyTouchListener bodyOnTouchListener;
//
//    public MyCalendar myCalendar;
//
//    public DayViewBody(Context context) {
//        this(context, null);
//    }
//
//    public DayViewBody(Context context, AttributeSet attrs) {
//        this(context, attrs, R.attr.calendarViewStyle);
//    }
//
//    public DayViewBody(Context context, AttributeSet attrs, int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//        this.mInflater = LayoutInflater.from(context);
//        inflate(context, R.layout.day_view_body, this);
//        dayViewController = new DayViewBodyController(attrs, context);
//    }
//
//    public MyCalendar getCalendar() {
//        return myCalendar;
//    }
//
//    public void setCalendar(MyCalendar myCalendar) {
//        this.myCalendar = myCalendar;
//        dayViewController.myCalendar = this.myCalendar;
//    }
//
//    public void setOnCreateNewEvent(DayViewBodyController.OnCreateNewEvent onCreateNewEvent){
//        dayViewController.setOnCreateNewEvent(onCreateNewEvent);
//    }
//
//
//    public void setOnLoadEvents(DayViewBodyController.OnLoadEvents onLoadEvents){
//        dayViewController.setOnLoadEvents(onLoadEvents);
//    }
//
//    public void setOnBodyTouchListener(DayViewBodyController.OnBodyTouchListener bodyOnTouchListener) {
//        this.bodyOnTouchListener = bodyOnTouchListener;
//        this.dayViewController.setOnBodyTouchListener(this.bodyOnTouchListener);
//    }
//
//    public void reloadEvents(){
//        this.dayViewController.reLoadEvents();
//    }
//
//    public void addEvent(final ITimeEventInterface new_event){
//        dayViewController.addEvent(new_event);
//        invalidateEvents();
//    }
//
//    public void removeEvent(ITimeEventInterface delete_event){
//        dayViewController.removeEvent(delete_event);
//    }
//
//    public void updateEvent(ITimeEventInterface old_event, ITimeEventInterface new_event){
//        dayViewController.updateEvent(old_event, new_event);
//    }
//
//    public void invalidateEvents(){
//        dayViewController.reDrawEvents();
//    }
//
//    public void resetView(){
//        this.events.clear();
//        dayViewController.resetViews();}
//
//    @Override
//    protected void onFinishInflate() {
//        super.onFinishInflate();
//        scrollContainerView = (ScrollContainerView) findViewById(R.id.customer_day_view);
//        body_container = (RelativeLayout) findViewById(R.id.body_container);
//        timeRLayout = (RelativeLayout) findViewById(R.id.timeReLayout);
//        dividerRLayout = (RelativeLayout) findViewById(R.id.eventRelativeLayout);
//        allDayContainer = (LinearLayout) findViewById(R.id.allDayContainer);
//
//        dayViewController.onFinishInflate(
//                scrollContainerView,
//                timeRLayout,
//                dividerRLayout,
//                body_container,
//                allDayContainer);
//
//        if (this.bodyOnTouchListener != null){
//            dayViewController.setOnBodyTouchListener(this.bodyOnTouchListener);
//        }else {
//            Log.i(TAG, "view: bodyOnTouchListener null ");
//        }
//    }
//
//    @Override
//    protected void onAttachedToWindow() {
//        super.onAttachedToWindow();
//        dayViewController.initBackgroundView();
//        dayViewController.bringDgViewsToFront();
//        if (myCalendar.isToday()){
//            dayViewController.addNowTimeLine();
//        }
//
//        dividerRLayout.invalidate();
//    }
//
//    public interface OnDayViewBodyEventListener{
//        void onEventClick(DayDraggableEventView dgView, ITimeEventInterface event);
//        void onEventLongClick(DayDraggableEventView dgView, ITimeEventInterface event);
//        void onEventDragStart(DayDraggableEventView dgView, ITimeEventInterface event);
//        void onEventDragging(DayDraggableEventView dgView, ITimeEventInterface event);
//        void onEventDragDrop(DayDraggableEventView dgView, ITimeEventInterface event);
//    }
//
//}
