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
import org.unimelb.itime.vendor.weekview.WeekViewBody;
import org.unimelb.itime.vendor.weekview.WeekViewHeader;


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

    private OnTimeSlotWeekViewChangeListener onTimeSlotWeekViewChangeListener;
    private Map<Long,Boolean> timeSlots;
    private int duration;
    private ArrayList<ITimeEventInterface> eventArrayList = new ArrayList<>();

    private OnTimeSlotClickListener onTimeSlotClickListener;
    private WeekTimeSlotViewBody currentPageWeekViewBody;
    private WeekTimeSlotViewHeader currentPageWeekViewHeader;
    Calendar firstSundayCalendar;


    public WeekTimeSlotView(Context context) {
        super(context);
        initAll();

    }

    public WeekTimeSlotView(Context context, AttributeSet attrs){
        super(context,attrs);
        initAll();

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
            this.timeSlots = timeSlots;
            this.duration = duration;
            currentPageWeekViewBody.setTimeSlots(timeSlots,duration);
            requestLayout();
        }
//        initAll();
    }
//     end of set time slots


//    set events
    public void setEvent(ArrayList<ITimeEventInterface> eventArrayList){
        this.eventArrayList = eventArrayList;
        currentPageWeekViewBody.setEvents(this.eventArrayList);
        requestLayout();
//        initAll();
    }
//    end of setting events

    public void addEvent(ITimeEventInterface event){
        this.eventArrayList.add(event);
        currentPageWeekViewBody.setEvents(this.eventArrayList);
        requestLayout();
//        initAll();
    }



    private void initAll(){
        calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        int todayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        calendar.add(Calendar.DATE, -(todayOfWeek - 1));

        // copy this calendar to pass to itime_main_program
        firstSundayCalendar = Calendar.getInstance();
        firstSundayCalendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));

        for (int i = 0 ; i < 4 ; i ++){
            LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.time_slot_view_pager_page,null);
            calendar.set(Calendar.DAY_OF_MONTH,calendar.get(Calendar.DAY_OF_MONTH) + i*7);

            WeekTimeSlotViewHeader weekTimeSlotViewHeader = (WeekTimeSlotViewHeader) linearLayout.getChildAt(0);
            weekTimeSlotViewHeader.setMyCalendar(new MyCalendar(calendar));

            WeekTimeSlotViewBody weekTimeSlotViewBody = (WeekTimeSlotViewBody) linearLayout.getChildAt(1);
            if (i==0)
                currentPageWeekViewBody = weekTimeSlotViewBody; // assign currentPageWeekViewBody
            weekTimeSlotViewBody.setMyCalendar(new MyCalendar(calendar));
//            weekTimeSlotViewBody.setOnTimeSlotClickListener(onTimeSlotClickListener);
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
//                Log.i("current position", String.valueOf(position));
                int deltaPosition;
                if (currentPosition - lastPosition>0)
                    deltaPosition=1;
                else
                    deltaPosition=-1;
                if(onTimeSlotWeekViewChangeListener != null){
                    firstSundayCalendar.add(Calendar.DATE,(deltaPosition)*7);
                    onTimeSlotWeekViewChangeListener.onWeekChanged(firstSundayCalendar);
                }
                lastPosition=position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state==1){
                    int size = pagerAdapter.getViews().size();
                    int currentPositionInViews = currentPosition % size;
                    LinearLayout curView = (LinearLayout)pagerAdapter.getViews().get(currentPositionInViews);

                    currentPageWeekViewBody = (WeekTimeSlotViewBody) curView.getChildAt(1);
                    LinearLayout preView = (LinearLayout)pagerAdapter.getViews().get((currentPosition - 1) % size);
                    LinearLayout nextView = (LinearLayout)pagerAdapter.getViews().get((currentPosition + 1) % size);


                    WeekTimeSlotViewHeader currentWeekTimeSlotViewHeader = (WeekTimeSlotViewHeader) curView.getChildAt(0);
                    MyCalendar currentWeekViewMyCalendar = currentWeekTimeSlotViewHeader.getMyCalendar();
                    WeekTimeSlotViewHeader preWeekTimeSlotViewHeader = (WeekTimeSlotViewHeader) preView.getChildAt(0);
                    preWeekTimeSlotViewHeader.getMyCalendar().cloneFromMyCalendar(currentWeekViewMyCalendar);
                    preWeekTimeSlotViewHeader.getMyCalendar().setOffsetByDate(-7);
                    MyCalendar myCalendar = preWeekTimeSlotViewHeader.getMyCalendar();
                    preWeekTimeSlotViewHeader.setMyCalendar(preWeekTimeSlotViewHeader.getMyCalendar());

                    // init?
                    WeekTimeSlotViewBody preWeekTimeSlotViewBody = (WeekTimeSlotViewBody) preView.getChildAt(1);
                    preWeekTimeSlotViewBody.getMyCalendar().cloneFromMyCalendar(currentWeekViewMyCalendar);
                    preWeekTimeSlotViewBody.getMyCalendar().setOffsetByDate(-7);
                    preWeekTimeSlotViewBody.setMyCalendar(preWeekTimeSlotViewBody.getMyCalendar());
                    preWeekTimeSlotViewBody.setTimeSlots(timeSlots,duration);
                    preWeekTimeSlotViewBody.setEvents(eventArrayList);
                    preWeekTimeSlotViewBody.setOnTimeSlotClickListener(onTimeSlotClickListener);
                    // init?

//                    pagerAdapter.changeView(nextView,(currentPosition + 1) % size);
                    WeekTimeSlotViewHeader nextWeekTimeSlotViewHeader = (WeekTimeSlotViewHeader) nextView.getChildAt(0);
                    nextWeekTimeSlotViewHeader.getMyCalendar().cloneFromMyCalendar(currentWeekViewMyCalendar);
                    nextWeekTimeSlotViewHeader.getMyCalendar().setOffsetByDate(+7);
                    nextWeekTimeSlotViewHeader.setMyCalendar(nextWeekTimeSlotViewHeader.getMyCalendar());
                    // init?
                    WeekTimeSlotViewBody nextWeekTimeSlotViewBody = (WeekTimeSlotViewBody) nextView.getChildAt(1);
                    nextWeekTimeSlotViewBody.getMyCalendar().cloneFromMyCalendar(currentWeekViewMyCalendar);
                    nextWeekTimeSlotViewBody.getMyCalendar().setOffsetByDate(+7);
                    nextWeekTimeSlotViewBody.setMyCalendar(nextWeekTimeSlotViewBody.getMyCalendar());
                    nextWeekTimeSlotViewBody.setTimeSlots(timeSlots,duration);
                    nextWeekTimeSlotViewBody.setEvents(eventArrayList);
                    nextWeekTimeSlotViewBody.setOnTimeSlotClickListener(onTimeSlotClickListener);
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
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }


    public OnTimeSlotClickListener getOnTimeSlotClickListener() {
        return onTimeSlotClickListener;
    }

    public void setOnTimeSlotClickListener(OnTimeSlotClickListener onTimeSlotClickListener) {
        this.onTimeSlotClickListener = onTimeSlotClickListener;
        currentPageWeekViewBody.setOnTimeSlotClickListener(this.onTimeSlotClickListener);
    }

    public interface OnTimeSlotWeekViewChangeListener{
        void onWeekChanged(Calendar calendar);
    }

    public interface  OnTimeSlotClickListener{
        void onTimeSlotClick(long time);
    }
}
