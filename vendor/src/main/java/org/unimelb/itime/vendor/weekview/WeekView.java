package org.unimelb.itime.vendor.weekview;

import android.content.Context;
import android.graphics.Canvas;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import org.unimelb.itime.vendor.R;
import org.unimelb.itime.vendor.helper.MyCalendar;
import org.unimelb.itime.vendor.helper.MyPagerAdapter;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Paul on 22/08/2016.
 */
public class WeekView extends LinearLayout{
    private MyPagerAdapter pagerAdapter;
    private ArrayList<LinearLayout> views = new ArrayList<LinearLayout>();
    private int currentPosition = 500;

    private int totalHeight;
    private int totalWidth;
    private int headerHeight;
    private int bodyHeight;


    public WeekView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void initAll(){
        Calendar calendar = Calendar.getInstance();
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
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentPosition = position;
                Log.i("currentPosition", String.valueOf(currentPosition));
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state==1){
                    Log.i("onPageScrollStateChanged","changed");
                    int size = pagerAdapter.getViews().size();
                    int currentPositionInViews = currentPosition % size;

                    LinearLayout curView = (LinearLayout)pagerAdapter.getViews().get(currentPositionInViews);
                    LinearLayout preView = (LinearLayout)pagerAdapter.getViews().get((currentPosition - 1) % size);
                    LinearLayout nextView = (LinearLayout)pagerAdapter.getViews().get((currentPosition + 1) % size);
                    WeekViewHeader currentWeekViewHeader = (WeekViewHeader) curView.getChildAt(0);
                    MyCalendar currentWeekViewMyCalendar = currentWeekViewHeader.getMyCalendar();

//                    pagerAdapter.changeView(preView, (currentPosition-1)%size);
                    WeekViewHeader preWeekViewHeader = (WeekViewHeader) preView.getChildAt(0);
                    preWeekViewHeader.getMyCalendar().cloneFromMyCalendar(currentWeekViewMyCalendar);

                    preWeekViewHeader.getMyCalendar().setOffset(-7);
                    Log.i("preWeekView", String.valueOf(preWeekViewHeader.getMyCalendar().getMonth()));

                    Log.i("preWeekViewHeader", String.valueOf(preWeekViewHeader.getMyCalendar().getDay()));
                    preWeekViewHeader.initCurrentWeekHeaders();
                    // init?
                    WeekViewBody preWeekViewBody = (WeekViewBody) preView.getChildAt(1);
                    preWeekViewBody.getMyCalendar().cloneFromMyCalendar(currentWeekViewMyCalendar);
                    preWeekViewBody.getMyCalendar().setOffset(-7);
                    preWeekViewBody.initAll();
                    // init?

//                    pagerAdapter.changeView(nextView,(currentPosition + 1) % size);
                    WeekViewHeader nextWeekViewHeader = (WeekViewHeader) nextView.getChildAt(0);
                    nextWeekViewHeader.getMyCalendar().cloneFromMyCalendar(currentWeekViewMyCalendar);
                    nextWeekViewHeader.getMyCalendar().setOffset(+7);
                    nextWeekViewHeader.initCurrentWeekHeaders();
                    // init?
                    WeekViewBody nextWeekViewBody = (WeekViewBody) nextView.getChildAt(1);
                    nextWeekViewBody.getMyCalendar().cloneFromMyCalendar(currentWeekViewMyCalendar);
                    nextWeekViewBody.getMyCalendar().setOffset(+7);
                    nextWeekViewBody.initAll();
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
        Log.i("onSizeChanged","here here");
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        Log.i("on finishInflate","here here");
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        totalHeight = MeasureSpec.getSize(heightMeasureSpec);
        totalWidth = MeasureSpec.getSize(widthMeasureSpec);
        updateWidthHeight(totalWidth,totalHeight);
        Log.i("on Measure"," here here");
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
        Log.i("onLayout","here here");
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.i("onDraw","onDraw");
    }
}
