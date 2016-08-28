package org.unimelb.itime.vendor.weekview;

import android.content.Context;
import android.content.Intent;
import android.databinding.BindingMethod;
import android.databinding.BindingMethods;
import android.graphics.Canvas;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import org.unimelb.itime.vendor.R;
import org.unimelb.itime.vendor.eventview.Event;
import org.unimelb.itime.vendor.helper.MyCalendar;
import org.unimelb.itime.vendor.helper.MyPagerAdapter;
import org.unimelb.itime.vendor.listener.ITimeEventInterface;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Paul on 22/08/2016.
 */
@BindingMethods(
        {@BindingMethod(type = WeekView.class, attribute = "app:onWeekViewChange", method="setOnWeekViewChangeListener"),
        @BindingMethod(type = WeekView.class, attribute = "app:editEvent", method="setOnClickEventInterface")}
)

public class WeekView extends RelativeLayout{
    private MyPagerAdapter pagerAdapter;
    private ArrayList<LinearLayout> views = new ArrayList<LinearLayout>();
    private int currentPosition = 500;
    Calendar calendar;

    private int totalHeight;
    private int totalWidth;
    private int headerHeight;
    private int bodyHeight;

    private OnWeekViewChangeListener onWeekViewChangeListener;
    private ArrayList<ITimeEventInterface> eventArrayList;
    private OnClickEventInterface onClickEventInterface;

    public WeekView(Context context){
        super(context);
    }

    public WeekView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public OnWeekViewChangeListener getOnWeekViewChangeListener() {
        return onWeekViewChangeListener;
    }

    public void setOnWeekViewChangeListener(OnWeekViewChangeListener onWeekViewChangeListener) {
        this.onWeekViewChangeListener = onWeekViewChangeListener;
    }

    public void initAll(){
        calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        int todayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        calendar.add(Calendar.DATE, -(todayOfWeek - 1));

        for (int i = 0 ; i < 4 ; i ++){
            LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.week_view_pager_page,null);
            WeekViewHeader weekViewHeader = (WeekViewHeader) linearLayout.getChildAt(0);
            Calendar calendar1 = Calendar.getInstance();
            calendar1.set(Calendar.DAY_OF_MONTH,calendar.get(Calendar.DAY_OF_MONTH) + i * 7);
            weekViewHeader.setMyCalendar(new MyCalendar(calendar1));
            weekViewHeader.updateWidthHeight(totalWidth,headerHeight);
            weekViewHeader.initCurrentWeekHeaders();

            WeekViewBody weekViewBody = (WeekViewBody) linearLayout.getChildAt(1);
            weekViewBody.setMyCalendar(new MyCalendar(calendar1));
            weekViewBody.updateWidthHeight(totalWidth,bodyHeight);
            weekViewBody.setEvents(this.eventArrayList);
            weekViewBody.setOnClickEventInterface(onClickEventInterface);
            weekViewBody.initAll();
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
                int deltaPosition;
                if (currentPosition - lastPosition>0)
                    deltaPosition=1;
                else
                    deltaPosition=-1;
                if(onWeekViewChangeListener != null){
                    calendar.add(Calendar.DATE,(deltaPosition)*7);
                    onWeekViewChangeListener.onWeekChanged(calendar);
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
                    WeekViewHeader currentWeekViewHeader = (WeekViewHeader) curView.getChildAt(0);
                    MyCalendar currentWeekViewMyCalendar = currentWeekViewHeader.getMyCalendar();

//                    calendar.set(currentWeekViewMyCalendar.getYear(),currentWeekViewMyCalendar.getMonth(),currentWeekViewMyCalendar.getDay(),
//                            currentWeekViewMyCalendar.getHour(),currentWeekViewMyCalendar.getMinute());

//                    pagerAdapter.changeView(preView, (currentPosition-1)%size);
                    WeekViewHeader preWeekViewHeader = (WeekViewHeader) preView.getChildAt(0);
                    preWeekViewHeader.getMyCalendar().cloneFromMyCalendar(currentWeekViewMyCalendar);

                    preWeekViewHeader.getMyCalendar().setOffsetByDate(-7);
                    Log.i("preWeekView", String.valueOf(preWeekViewHeader.getMyCalendar().getMonth()));

                    Log.i("preWeekViewHeader", String.valueOf(preWeekViewHeader.getMyCalendar().getDay()));
                    preWeekViewHeader.initCurrentWeekHeaders();
                    // init?
                    WeekViewBody preWeekViewBody = (WeekViewBody) preView.getChildAt(1);
                    preWeekViewBody.getMyCalendar().cloneFromMyCalendar(currentWeekViewMyCalendar);
                    preWeekViewBody.getMyCalendar().setOffsetByDate(-7);
                    preWeekViewBody.setEvents(eventArrayList);
                    preWeekViewBody.setOnClickEventInterface(onClickEventInterface);
                    preWeekViewBody.initAll();
                    // init?

//                    pagerAdapter.changeView(nextView,(currentPosition + 1) % size);
                    WeekViewHeader nextWeekViewHeader = (WeekViewHeader) nextView.getChildAt(0);
                    nextWeekViewHeader.getMyCalendar().cloneFromMyCalendar(currentWeekViewMyCalendar);
                    nextWeekViewHeader.getMyCalendar().setOffsetByDate(+7);
                    nextWeekViewHeader.initCurrentWeekHeaders();
                    // init?
                    WeekViewBody nextWeekViewBody = (WeekViewBody) nextView.getChildAt(1);
                    nextWeekViewBody.getMyCalendar().cloneFromMyCalendar(currentWeekViewMyCalendar);
                    nextWeekViewBody.getMyCalendar().setOffsetByDate(+7);
                    nextWeekViewBody.setEvents(eventArrayList);
                    nextWeekViewBody.setOnClickEventInterface(onClickEventInterface);
                    nextWeekViewBody.initAll();
                    // init?
                    pagerAdapter.changeView(preView, (currentPosition-1)%size);
                    pagerAdapter.changeView(nextView,(currentPosition + 1) % size);

                }
            }
        });
    }

    //    set events
    public void setEvent(ArrayList<ITimeEventInterface> eventArrayList){
        this.eventArrayList = eventArrayList;
    }
//    end of setting events

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

    public OnClickEventInterface getOnClickEventInterface() {
        return onClickEventInterface;
    }

    public void setOnClickEventInterface(OnClickEventInterface onClickEventInterface) {
        this.onClickEventInterface = onClickEventInterface;
    }

    public interface OnWeekViewChangeListener{
        void onWeekChanged(Calendar calendar);
    }

    public interface OnClickEventInterface{
        void editEvent(ITimeEventInterface iTimeEventInterface);
    }

}
