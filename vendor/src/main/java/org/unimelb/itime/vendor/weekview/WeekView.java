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
import org.unimelb.itime.vendor.eventview.WeekDraggableEventView;
import org.unimelb.itime.vendor.helper.MyCalendar;
import org.unimelb.itime.vendor.helper.MyPagerAdapter;
import org.unimelb.itime.vendor.listener.ITimeEventInterface;
import org.unimelb.itime.vendor.timeslotview.WeekTimeSlotViewBody;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Paul on 22/08/2016.
 */
@BindingMethods(
        {@BindingMethod(type = WeekView.class, attribute = "app:onWeekViewChange", method="setOnWeekViewChangeListener"),
        @BindingMethod(type = WeekView.class, attribute = "app:onClickEditEvent", method="setOnClickEventInterface")}
)

public class WeekView extends RelativeLayout{
    private MyPagerAdapter pagerAdapter;
    private ArrayList<LinearLayout> views = new ArrayList<LinearLayout>();
    private int currentPosition = 500;
    Calendar calendar;
    Calendar firstSundayCalendar;

    private int totalHeight;
    private int totalWidth;
    private int headerHeight;
    private int bodyHeight;

    private OnWeekViewChangeListener onWeekViewChangeListener;
    private ArrayList<ITimeEventInterface> eventArrayList = new ArrayList<>();
    private OnClickEventInterface onClickEventInterface;
    private WeekViewBody currentPageWeekViewBody;

    public WeekView(Context context){
        super(context);
        initAll();
    }

    public WeekView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAll();
    }

    public OnWeekViewChangeListener getOnWeekViewChangeListener() {
        return onWeekViewChangeListener;
    }

    public void setOnWeekViewChangeListener(OnWeekViewChangeListener onWeekViewChangeListener) {
        this.onWeekViewChangeListener = onWeekViewChangeListener;
    }

    //    set events
    public void setEvent(ArrayList<ITimeEventInterface> eventArrayList){
        this.eventArrayList = eventArrayList;
        currentPageWeekViewBody.setEvents(this.eventArrayList);
        requestLayout();
    }

    public void initAll(){
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
            LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.week_view_pager_page,null);
            calendar.set(Calendar.DAY_OF_MONTH,calendar.get(Calendar.DAY_OF_MONTH) + i * 7);

            WeekViewHeader weekViewHeader = (WeekViewHeader) linearLayout.getChildAt(0);
            weekViewHeader.setMyCalendar(new MyCalendar(calendar));

            WeekViewBody weekViewBody = (WeekViewBody) linearLayout.getChildAt(1);
            if (i==0)
                currentPageWeekViewBody = weekViewBody;

            weekViewBody.setMyCalendar(new MyCalendar(calendar));
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
                    firstSundayCalendar.add(Calendar.DATE,(deltaPosition)*7);
                    onWeekViewChangeListener.onWeekChanged(firstSundayCalendar);
                }
                lastPosition=position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state==1){
                    int size = pagerAdapter.getViews().size();
                    int currentPositionInViews = currentPosition % size;

                    LinearLayout curView = (LinearLayout)pagerAdapter.getViews().get(currentPositionInViews);
                    currentPageWeekViewBody = (WeekViewBody) curView.getChildAt(1);
                    LinearLayout preView = (LinearLayout)pagerAdapter.getViews().get((currentPosition - 1) % size);
                    LinearLayout nextView = (LinearLayout)pagerAdapter.getViews().get((currentPosition + 1) % size);

                    WeekViewHeader currentWeekViewHeader = (WeekViewHeader) curView.getChildAt(0);
                    MyCalendar currentWeekViewMyCalendar = currentWeekViewHeader.getMyCalendar();

                    WeekViewHeader preWeekViewHeader = (WeekViewHeader) preView.getChildAt(0);
                    preWeekViewHeader.getMyCalendar().cloneFromMyCalendar(currentWeekViewMyCalendar);
                    preWeekViewHeader.getMyCalendar().setOffsetByDate(-7);
                    preWeekViewHeader.setMyCalendar(preWeekViewHeader.getMyCalendar());

                    // init?
                    WeekViewBody preWeekViewBody = (WeekViewBody) preView.getChildAt(1);
                    preWeekViewBody.getMyCalendar().cloneFromMyCalendar(currentWeekViewMyCalendar);
                    preWeekViewBody.getMyCalendar().setOffsetByDate(-7);
                    preWeekViewBody.setMyCalendar(preWeekViewBody.getMyCalendar());
                    preWeekViewBody.setEvents(eventArrayList);
                    preWeekViewBody.setOnClickEventInterface(onClickEventInterface);

                    // init?
                    WeekViewHeader nextWeekViewHeader = (WeekViewHeader) nextView.getChildAt(0);
                    nextWeekViewHeader.getMyCalendar().cloneFromMyCalendar(currentWeekViewMyCalendar);
                    nextWeekViewHeader.getMyCalendar().setOffsetByDate(+7);
                    nextWeekViewHeader.setMyCalendar(nextWeekViewHeader.getMyCalendar());
                    // init?
                    WeekViewBody nextWeekViewBody = (WeekViewBody) nextView.getChildAt(1);
                    nextWeekViewBody.getMyCalendar().cloneFromMyCalendar(currentWeekViewMyCalendar);
                    nextWeekViewBody.getMyCalendar().setOffsetByDate(+7);
                    nextWeekViewBody.setMyCalendar(nextWeekViewBody.getMyCalendar());
                    nextWeekViewBody.setEvents(eventArrayList);
                    nextWeekViewBody.setOnClickEventInterface(onClickEventInterface);
                    // init?
                    pagerAdapter.changeView(preView, (currentPosition-1)%size);
                    pagerAdapter.changeView(nextView,(currentPosition + 1) % size);

                }
            }
        });
    }


    public ArrayList<ITimeEventInterface> getEventArrayList(){
        return this.eventArrayList;
    }

//    end of setting events

//    **********************************************************************************

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        totalHeight = MeasureSpec.getSize(heightMeasureSpec);
        totalWidth = MeasureSpec.getSize(widthMeasureSpec);
    }


    public OnClickEventInterface getOnClickEventInterface() {
        return onClickEventInterface;
    }

    public void setOnClickEventInterface(OnClickEventInterface onClickEventInterface) {
        this.onClickEventInterface = onClickEventInterface;
        currentPageWeekViewBody.setOnClickEventInterface(onClickEventInterface);
    }

    public interface OnWeekViewChangeListener{
        void onWeekChanged(Calendar calendar);
    }

    public interface OnClickEventInterface{
        void onClickEditEvent(ITimeEventInterface iTimeEventInterface);
    }

    private WeekViewBody.OnWeekBodyListener onWeekBodyListener;
    public void setOnWeekBodyOutterListener(WeekViewBody.OnWeekBodyListener onWeekBodyListener){
        this.onWeekBodyListener = onWeekBodyListener;
    }

    class OnWeekBodyInnerListener implements WeekViewBody.OnWeekBodyListener{

        @Override
        public void onEventCreate(WeekDraggableEventView eventView) {
            if (onWeekBodyListener != null){onWeekBodyListener.onEventClick(eventView);}
        }

        @Override
        public void onEventClick(WeekDraggableEventView eventView) {
            if (onWeekBodyListener != null){onWeekBodyListener.onEventClick(eventView);}

        }

        @Override
        public void onEventDragStart(WeekDraggableEventView eventView) {
            if (onWeekBodyListener != null){onWeekBodyListener.onEventClick(eventView);}

        }

        @Override
        public void onEventDragging(WeekDraggableEventView eventView, int x, int y) {
            if (onWeekBodyListener != null){onWeekBodyListener.onEventDragging(eventView, x, y);}

        }

        @Override
        public void onEventDragDrop(WeekDraggableEventView eventView) {
            if (onWeekBodyListener != null){onWeekBodyListener.onEventDragDrop(eventView);}

        }
    }
}
