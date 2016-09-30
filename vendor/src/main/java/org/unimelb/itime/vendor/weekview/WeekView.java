package org.unimelb.itime.vendor.weekview;

import android.content.Context;
import android.databinding.BindingMethod;
import android.databinding.BindingMethods;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import org.unimelb.itime.vendor.dayview.DayViewHeader;
import org.unimelb.itime.vendor.dayview.FlexibleLenBodyViewPager;
import org.unimelb.itime.vendor.dayview.FlexibleLenViewBody;
import org.unimelb.itime.vendor.eventview.DayDraggableEventView;
import org.unimelb.itime.vendor.helper.DensityUtil;
import org.unimelb.itime.vendor.helper.MyCalendar;
import org.unimelb.itime.vendor.listener.ITimeEventInterface;
import org.unimelb.itime.vendor.timeslot.TimeSlotView;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by yuhaoliu on 10/08/16.
 */

@BindingMethods(
        {@BindingMethod(type = WeekView.class, attribute = "app:onWeekViewChange", method="setOnHeaderListener"),
        @BindingMethod(type = WeekView.class, attribute = "app:onWeekViewOutListener", method = "setOnTimeSlotOuterListener")}

)
public class WeekView extends LinearLayout {
    private final String TAG = "MyAPP";

    private Context context;
    final DisplayMetrics dm = getResources().getDisplayMetrics();

    private int upperBoundsOffset = 1;
    private int bodyCurrentPosition;

    private MyCalendar monthDayViewCalendar = new MyCalendar(Calendar.getInstance());

    ArrayList<WeekViewHeader> headerViewList;
    ArrayList<FlexibleLenViewBody> bodyViewList;
    ArrayList<LinearLayout> weekViewList;


    private FlexibleLenBodyViewPager weekViewPager;
    private WeekViewPagerAdapter adapter;

    private Map<Long, List<ITimeEventInterface>> dayEventMap;

    private int bodyPagerCurrentState = 0;

    private FlexibleLenViewBody.OnBodyListener OnBodyOuterListener;

    private FlexibleLenViewBody.OnTimeSlotListener onTimeSlotOuterListener;

    public WeekView(Context context) {
        super(context);
        initView();
    }

    public WeekView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public WeekView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    public void reloadEvents(){
        adapter.reloadEvents();
    }

    public void reloadTimeSlots(boolean animate){
        adapter.reloadTimeSlots(animate);
    }

    private void initView(){
        this.context = getContext();

        bodyViewList = new ArrayList<>();
        this.initBody();
        headerViewList = new ArrayList<>();
        this.initHeader();
        weekViewList = new ArrayList<>();
        this.initWeekViews();

        this.setUpWeekView();
    }

    private void initHeader(){
        int size = 4;
        int padding = DensityUtil.dip2px(context,5);
        //must be consistent with width of left bar in body part.
        int leftBarPadding = DensityUtil.dip2px(context,40);
        for (int i = 0; i < size; i++) {
            WeekViewHeader headerView = new WeekViewHeader(context);
            headerView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            headerView.setPadding(leftBarPadding,padding,0,padding);
            headerViewList.add(headerView);
        }
    }

    private void initWeekViews(){
        int size = 4;
        for (int i = 0; i < size; i++) {
            LinearLayout weekView = new LinearLayout(context);
            weekView.setOrientation(VERTICAL);
            weekView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            weekView.addView(this.headerViewList.get(i));
            weekView.addView(this.bodyViewList.get(i));
            this.weekViewList.add(weekView);
        }
    }

    /*--------------------*/

    public void setDayEventMap(Map<Long, List<ITimeEventInterface>> dayEventMap){
        this.dayEventMap = dayEventMap;
        if (adapter != null){
            adapter.setDayEventMap(this.dayEventMap);
        }else {
            Log.i(TAG, "adapter: null" );
        }
    }

    private void setUpWeekView(){
        weekViewPager = new FlexibleLenBodyViewPager(context);
        weekViewPager.setScrollDurationFactor(3);
        upperBoundsOffset = 500;
        bodyCurrentPosition = upperBoundsOffset;
        adapter = new WeekViewPagerAdapter(upperBoundsOffset,weekViewList);
        if (this.dayEventMap != null){
            adapter.setDayEventMap(this.dayEventMap);
        }
        adapter.setSlotsInfo(this.slotsInfo);
        weekViewPager.setAdapter(adapter);
        weekViewPager.setCurrentItem(upperBoundsOffset);
        this.addView(weekViewPager,new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        weekViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                bodyCurrentPosition = position;
                monthDayViewCalendar = adapter.getViewBodyByPosition(position).getCalendar();
                if (onHeaderListener != null){
                    onHeaderListener.onMonthChanged(monthDayViewCalendar);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                bodyPagerCurrentState = state;
            }
        });
    }


    private void initBody(){
        int size = 4;
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTime(new Date());
        int day_of_week = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        calendar.set(Calendar.DAY_OF_MONTH,calendar.get(Calendar.DAY_OF_MONTH) - day_of_week);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        for (int i = 0; i < size; i++) {
            FlexibleLenViewBody bodyView = new FlexibleLenViewBody(context,7);
            bodyView.setLayoutParams(new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            bodyView.setCalendar(new MyCalendar(calendar));
            bodyView.setOnBodyListener(new OnBodyInnerListener());
            bodyView.setOnTimeSlotListener(new OnTimeSlotInnerListener());
            bodyViewList.add(bodyView);
        }

    }

    private OnHeaderListener onHeaderListener;

    public void setOnHeaderListener(OnHeaderListener onHeaderListener){
        this.onHeaderListener = onHeaderListener;
    }


    public void setOnBodyOuterListener(FlexibleLenViewBody.OnBodyListener onBodyOuterListener){
        this.OnBodyOuterListener = onBodyOuterListener;
    }

    public interface OnHeaderListener{
        void onMonthChanged(MyCalendar calendar);
    }

    public class OnBodyInnerListener implements FlexibleLenViewBody.OnBodyListener{
        int parentWidth = dm.widthPixels;

        @Override
        public void onEventCreate(DayDraggableEventView eventView) {
            MyCalendar currentCal = (adapter.getViewBodyByPosition(bodyCurrentPosition)).getCalendar();
            MyCalendar eventNewCal = new MyCalendar(currentCal);
            eventNewCal.setOffsetByDate(eventView.getIndexInView());
            eventView.getNewCalendar().setDay(eventNewCal.getDay());
            eventView.getNewCalendar().setMonth(eventNewCal.getMonth());
            eventView.getNewCalendar().setYear(eventNewCal.getYear());

            if (OnBodyOuterListener != null){OnBodyOuterListener.onEventCreate(eventView);}
        }

        @Override
        public void onEventClick(DayDraggableEventView eventView) {
            if (OnBodyOuterListener != null){OnBodyOuterListener.onEventClick(eventView);}

        }

        @Override
        public void onEventDragStart(DayDraggableEventView eventView) {
            if (OnBodyOuterListener != null){OnBodyOuterListener.onEventDragStart(eventView);}

        }

        @Override
        public void onEventDragging(DayDraggableEventView eventView, int x, int y) {
            boolean isSwiping = bodyPagerCurrentState == 0;
            if (isSwiping){
                this.bodyAutoSwipe(eventView, x, y);
            }
            if (OnBodyOuterListener != null){OnBodyOuterListener.onEventDragging(eventView, x, y);}
        }

        @Override
        public void onEventDragDrop(DayDraggableEventView eventView) {
            MyCalendar currentCal = (adapter.getViewBodyByPosition(bodyCurrentPosition)).getCalendar();
            MyCalendar eventNewCal = new MyCalendar(currentCal);

            eventNewCal.setOffsetByDate(eventView.getIndexInView());
            eventView.getNewCalendar().setDay(eventNewCal.getDay());
            eventView.getNewCalendar().setMonth(eventNewCal.getMonth());
            eventView.getNewCalendar().setYear(eventNewCal.getYear());

            if (OnBodyOuterListener != null){OnBodyOuterListener.onEventDragDrop(eventView);}
        }

        private void bodyAutoSwipe(DayDraggableEventView eventView, int x, int y){
            int offset = x > (parentWidth * 0.85) ? 1 : (x <= parentWidth * 0.05 ? -1 : 0);
            if (offset != 0){
                int scrollTo = bodyCurrentPosition + offset;
                weekViewPager.setCurrentItem(scrollTo,true);
            }
        }
    }


    public void setOnTimeSlotOuterListener(FlexibleLenViewBody.OnTimeSlotListener onTimeSlotOuterListener){
        this.onTimeSlotOuterListener = onTimeSlotOuterListener;
    }

    public class OnTimeSlotInnerListener implements FlexibleLenViewBody.OnTimeSlotListener{

        @Override
        public void onTimeSlotCreate(TimeSlotView timeSlotView) {
            MyCalendar currentCal = new MyCalendar((adapter.getViewBodyByPosition(bodyCurrentPosition)).getCalendar());
            currentCal.setOffsetByDate(timeSlotView.getIndexInView());
            timeSlotView.getNewCalendar().setDay(currentCal.getDay());
            timeSlotView.getNewCalendar().setMonth(currentCal.getMonth());
            timeSlotView.getNewCalendar().setYear(currentCal.getYear());

            TimeSlotStruct newStruct = new TimeSlotStruct();
            newStruct.startTime = timeSlotView.getStartTimeM();
            newStruct.endTime = timeSlotView.getStartTimeM() + timeSlotView.getDuration();
            newStruct.status = false;

            timeSlotView.setTag(newStruct);
            addTimeSlot(newStruct);
            reloadTimeSlots(true);

            if (onTimeSlotOuterListener != null){
                onTimeSlotOuterListener.onTimeSlotCreate(timeSlotView);
            }
        }

        @Override
        public void onTimeSlotClick(TimeSlotView timeSlotView) {
            if (onTimeSlotOuterListener != null){
                onTimeSlotOuterListener.onTimeSlotClick(timeSlotView);
            }
        }

        @Override
        public void onTimeSlotDragStart(TimeSlotView timeSlotView) {
            if (onTimeSlotOuterListener != null){
                onTimeSlotOuterListener.onTimeSlotDragStart(timeSlotView);
            }
        }

        @Override
        public void onTimeSlotDragging(TimeSlotView timeSlotView, int x, int y) {
            if (onTimeSlotOuterListener != null){
                onTimeSlotOuterListener.onTimeSlotDragging(timeSlotView, x, y);
            }
        }

        @Override
        public void onTimeSlotDragDrop(TimeSlotView timeSlotView) {
            MyCalendar currentCal = new MyCalendar((adapter.getViewBodyByPosition(bodyCurrentPosition)).getCalendar());
            currentCal.setOffsetByDate(timeSlotView.getIndexInView());
            timeSlotView.getNewCalendar().setDay(currentCal.getDay());
            timeSlotView.getNewCalendar().setMonth(currentCal.getMonth());
            timeSlotView.getNewCalendar().setYear(currentCal.getYear());

            TimeSlotStruct struct = (TimeSlotStruct)timeSlotView.getTag();
            struct.startTime = timeSlotView.getStartTimeM();
            struct.endTime = timeSlotView.getEndTimeM();

            if (onTimeSlotOuterListener != null){
                onTimeSlotOuterListener.onTimeSlotDragDrop(timeSlotView);
            }

            adapter.reloadTimeSlots(false);

        }
    }


    /**
     *
     * @param className
     * @param <E>
     */
    public <E extends ITimeEventInterface> void setEventClassName(Class<E> className){

        for (FlexibleLenViewBody view: bodyViewList){
            view.setEventClassName(className);
        }

    }

    private ArrayList<TimeSlotStruct> slotsInfo = new ArrayList<>();

    public void enableTimeSlot(){
        if (adapter != null){
            adapter.enableTimeSlot();
        }
    }

    public void addTimeSlot(TimeSlotStruct slotInfo){
        slotsInfo.add(slotInfo);
    }

    public void updateTimeSlotsDuration(long duration, boolean animate){
        if (adapter != null){
            adapter.updateTimeSlotsDuration(duration,animate);
        }
    }

    public static class TimeSlotStruct{
        public long startTime = 0;
        public long endTime = 0;
        public boolean status = false;
        public Object object = null;
    }

}


