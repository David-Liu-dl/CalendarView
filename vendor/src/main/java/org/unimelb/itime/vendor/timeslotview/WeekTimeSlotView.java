package org.unimelb.itime.vendor.timeslotview;

import android.content.Context;
import android.databinding.BindingMethod;
import android.databinding.BindingMethods;
import android.graphics.Canvas;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import org.unimelb.itime.vendor.R;
import org.unimelb.itime.vendor.eventview.Event;
import org.unimelb.itime.vendor.eventview.WeekDraggableEventView;
import org.unimelb.itime.vendor.helper.MyCalendar;
import org.unimelb.itime.vendor.helper.MyPagerAdapter;
import org.unimelb.itime.vendor.listener.ITimeEventInterface;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

/**
 * Created by Paul on 22/08/2016.
 */
@BindingMethods({
        @BindingMethod(type = WeekTimeSlotView.class, attribute = "app:onTimeSlotWeekViewChange", method="setOnTimeSlotWeekViewChangeListener"),
        @BindingMethod(type = WeekTimeSlotView.class, attribute = "app:onTimeSlotClick", method ="setOnTimeSlotClickListener" )
        })
public class WeekTimeSlotView extends RelativeLayout{


    private MyPagerAdapter pagerAdapter;
    private ArrayList<LinearLayout> views = new ArrayList<LinearLayout>();
    private int currentPosition = 500;
    Calendar calendar;

    private int totalHeight;
    private int totalWidth;
    private int headerHeight;
    private int bodyHeight;

    private OnTimeSlotWeekViewChangeListener onTimeSlotWeekViewChangeListener;
    private Map<Long,Boolean> timeSlots;
    private int duration;
    private ArrayList<ITimeEventInterface> eventArrayList = new ArrayList<>();

    private OnTimeSlotClickListener onTimeSlotClickListener;


    public WeekTimeSlotView(Context context) {
        super(context);
//        if (!EventBus.getDefault().isRegistered(this))
//            EventBus.getDefault().register(this);
    }

    public WeekTimeSlotView(Context context, AttributeSet attrs){
        super(context,attrs);
//        if (!EventBus.getDefault().isRegistered(this))
//            EventBus.getDefault().register(this);
    }

    public OnTimeSlotWeekViewChangeListener getOnWeekViewChangeListener() {
        return onTimeSlotWeekViewChangeListener;
    }

    public void setOnTimeSlotWeekViewChangeListener(OnTimeSlotWeekViewChangeListener onWeekViewChangeListener) {
        this.onTimeSlotWeekViewChangeListener = onWeekViewChangeListener;
    }

//    set time slots
    public void setTimeSlots(Map<Long,Boolean> timeSlots,int duration){
        if (timeSlots!=null && timeSlots.size()>0){
            Log.d("set time slot","duration");
            this.timeSlots = timeSlots;
            this.duration = duration;
        }
        initAll();
    }
//     end of set time slots


//    set events
    public void setEvent(ArrayList<ITimeEventInterface> eventArrayList){
        this.eventArrayList = eventArrayList;
        initAll();
    }
//    end of setting events

    public void addEvent(ITimeEventInterface event){
        this.eventArrayList.add(event);
        initAll();
    }

    private void initAll(){
        calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        int todayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        calendar.add(Calendar.DATE, -(todayOfWeek - 1));

        for (int i = 0 ; i < 4 ; i ++){
            LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.time_slot_view_pager_page,null);
            WeekTimeSlotViewHeader weekTimeSlotViewHeader = (WeekTimeSlotViewHeader) linearLayout.getChildAt(0);
            Calendar calendar1 = Calendar.getInstance();
            calendar1.set(Calendar.DAY_OF_MONTH,calendar.get(Calendar.DAY_OF_MONTH) + i * 7);
            weekTimeSlotViewHeader.setMyCalendar(new MyCalendar(calendar1));
            weekTimeSlotViewHeader.updateWidthHeight(totalWidth,headerHeight);
            weekTimeSlotViewHeader.initCurrentWeekHeaders();

            WeekTimeSlotViewBody weekTimeSlotViewBody = (WeekTimeSlotViewBody) linearLayout.getChildAt(1);
            weekTimeSlotViewBody.setMyCalendar(new MyCalendar(calendar1));
            weekTimeSlotViewBody.updateWidthHeight(totalWidth,bodyHeight);
            weekTimeSlotViewBody.setTimeSlots(this.timeSlots,this.duration); // set timeslots
            weekTimeSlotViewBody.setEvents(this.eventArrayList); // set events;
            weekTimeSlotViewBody.setOnTimeSlotClickListener(onTimeSlotClickListener);
            weekTimeSlotViewBody.initAll();
            views.add(linearLayout);
        }

        ViewPager viewPager = new ViewPager(getContext());
        this.addView(viewPager);
        pagerAdapter = new MyPagerAdapter(views);

        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(1);
        viewPager.setCurrentItem(currentPosition);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            private int lastPosition = 500;
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentPosition = position;
                Log.i("current position", String.valueOf(position));
                int deltaPosition;
                if (currentPosition - lastPosition>0)
                    deltaPosition=1;
                else
                    deltaPosition=-1;
                if(onTimeSlotWeekViewChangeListener != null){
                    calendar.add(Calendar.DATE,(deltaPosition)*7);
                    onTimeSlotWeekViewChangeListener.onWeekChanged(calendar);
//                    Log.i("deltaPosition", String.valueOf(deltaPosition));
                    Log.i("calendar day", String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
                }
                lastPosition=position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state==1){
                    int size = pagerAdapter.getViews().size();
                    int currentPositionInViews = currentPosition % size;
                    LinearLayout curView = (LinearLayout)pagerAdapter.getViews().get(currentPositionInViews);
                    LinearLayout preView = (LinearLayout)pagerAdapter.getViews().get((currentPosition - 1) % size);
                    LinearLayout nextView = (LinearLayout)pagerAdapter.getViews().get((currentPosition + 1) % size);
                    WeekTimeSlotViewHeader currentWeekTimeSlotViewHeader = (WeekTimeSlotViewHeader) curView.getChildAt(0);
                    MyCalendar currentWeekViewMyCalendar = currentWeekTimeSlotViewHeader.getMyCalendar();
                    Log.i("current calendar", String.valueOf(currentWeekViewMyCalendar.getYear() + " "+
                    String.valueOf(currentWeekViewMyCalendar.getMonth() + " "+
                    String.valueOf(currentWeekViewMyCalendar.getDay()))));
                    WeekTimeSlotViewHeader preWeekTimeSlotViewHeader = (WeekTimeSlotViewHeader) preView.getChildAt(0);
                    preWeekTimeSlotViewHeader.getMyCalendar().cloneFromMyCalendar(currentWeekViewMyCalendar);

                    preWeekTimeSlotViewHeader.getMyCalendar().setOffsetByDate(-7);
                    Log.i("preWeekView", String.valueOf(preWeekTimeSlotViewHeader.getMyCalendar().getMonth()));

                    Log.i("preTimeSlotViewHeader", String.valueOf(preWeekTimeSlotViewHeader.getMyCalendar().getDay()));
                    preWeekTimeSlotViewHeader.initCurrentWeekHeaders();
                    // init?
                    WeekTimeSlotViewBody preWeekTimeSlotViewBody = (WeekTimeSlotViewBody) preView.getChildAt(1);
                    preWeekTimeSlotViewBody.getMyCalendar().cloneFromMyCalendar(currentWeekViewMyCalendar);
                    preWeekTimeSlotViewBody.getMyCalendar().setOffsetByDate(-7);
                    preWeekTimeSlotViewBody.setTimeSlots(timeSlots,duration);
                    preWeekTimeSlotViewBody.setEvents(eventArrayList);
                    preWeekTimeSlotViewBody.setOnTimeSlotClickListener(onTimeSlotClickListener);
                    preWeekTimeSlotViewBody.initAll();
                    // init?

//                    pagerAdapter.changeView(nextView,(currentPosition + 1) % size);
                    WeekTimeSlotViewHeader nextWeekTimeSlotViewHeader = (WeekTimeSlotViewHeader) nextView.getChildAt(0);
                    nextWeekTimeSlotViewHeader.getMyCalendar().cloneFromMyCalendar(currentWeekViewMyCalendar);
                    nextWeekTimeSlotViewHeader.getMyCalendar().setOffsetByDate(+7);
                    nextWeekTimeSlotViewHeader.initCurrentWeekHeaders();
                    // init?
                    WeekTimeSlotViewBody nextWeekTimeSlotViewBody = (WeekTimeSlotViewBody) nextView.getChildAt(1);
                    nextWeekTimeSlotViewBody.getMyCalendar().cloneFromMyCalendar(currentWeekViewMyCalendar);
                    nextWeekTimeSlotViewBody.getMyCalendar().setOffsetByDate(+7);
                    nextWeekTimeSlotViewBody.setTimeSlots(timeSlots,duration);
                    nextWeekTimeSlotViewBody.setEvents(eventArrayList);
                    nextWeekTimeSlotViewBody.setOnTimeSlotClickListener(onTimeSlotClickListener);
                    nextWeekTimeSlotViewBody.initAll();
                    // init?
                    pagerAdapter.changeView(preView, (currentPosition-1)%size);
                    pagerAdapter.changeView(nextView,(currentPosition + 1) % size);

                }
            }
        });
    }

    //    **********************************************************************************
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
//        initAll();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        totalHeight = MeasureSpec.getSize(heightMeasureSpec);
        totalWidth = MeasureSpec.getSize(widthMeasureSpec);
        updateWidthHeight(totalWidth,totalHeight);
    }

    private void updateWidthHeight(int width,int height){
        this.headerHeight = height/6;
        this.bodyHeight = height - height/6;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        initAll();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    public OnTimeSlotClickListener getOnTimeSlotClickListener() {
        return onTimeSlotClickListener;
    }

    public void setOnTimeSlotClickListener(OnTimeSlotClickListener onTimeSlotClickListener) {
        this.onTimeSlotClickListener = onTimeSlotClickListener;
    }

    public interface OnTimeSlotWeekViewChangeListener{
        void onWeekChanged(Calendar calendar);
    }

    public interface  OnTimeSlotClickListener{
        void onTimeSlotClick(long time);
    }
}
