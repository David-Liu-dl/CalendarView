package org.unimelb.itime.vendor.weekview;

import android.content.Context;
import android.databinding.BindingMethod;
import android.databinding.BindingMethods;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import org.unimelb.itime.vendor.R;
import org.unimelb.itime.vendor.eventview.WeekDraggableEventView;
import org.unimelb.itime.vendor.helper.MyCalendar;
import org.unimelb.itime.vendor.listener.ITimeEventInterface;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * Created by Paul on 22/08/2016.
 */
@BindingMethods(
        {@BindingMethod(type = WeekView.class, attribute = "app:onWeekViewChange", method="setOnWeekViewChangeListener"),
        @BindingMethod(type = WeekView.class, attribute = "app:onWeekOutterListener", method="setOnWeekBodyOutterListener")}
)

public class WeekView extends RelativeLayout{
    private WeekViewPagerAdapter pagerAdapter;
    private ArrayList<LinearLayout> views = new ArrayList<>();
    private int currentPosition = 500;
    Calendar firstSundayCalendar;

    private int totalHeight;
    private int totalWidth;
    private int headerHeight;
    private int bodyHeight;

    private OnWeekViewChangeListener onWeekViewChangeListener;
    private Map<Long, List<ITimeEventInterface>> dayEventMap;

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
    public void setEventMap(Map<Long, List<ITimeEventInterface>> dayEventMap){
        this.dayEventMap = dayEventMap;
        if (this.pagerAdapter != null){
            pagerAdapter.setDayEventMap(dayEventMap);
        }
    }

    public void initAll(){
        Calendar calendar = Calendar.getInstance();
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
            views.add(linearLayout);
        }
        ViewPager viewPager = new ViewPager(getContext());
        this.addView(viewPager);
        pagerAdapter = new WeekViewPagerAdapter(currentPosition,views);
        pagerAdapter.setDayEventMap(this.dayEventMap);
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
            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        totalHeight = MeasureSpec.getSize(heightMeasureSpec);
        totalWidth = MeasureSpec.getSize(widthMeasureSpec);
    }

    public interface OnWeekViewChangeListener{
        void onWeekChanged(Calendar calendar);
    }

    public WeekViewBody.OnWeekBodyListener getOnWeekBodyListener() {
        return onWeekBodyListener;
    }

    private WeekViewBody.OnWeekBodyListener onWeekBodyListener;

    public void setOnWeekBodyOutterListener(WeekViewBody.OnWeekBodyListener onWeekBodyListener){
        this.onWeekBodyListener = onWeekBodyListener;
        for (LinearLayout view:views
             ) {
            WeekViewBody body = (WeekViewBody) view.findViewById(R.id.week_body);
            body.setOnWeekBodyListener(new OnWeekBodyInnerListener());
        }
    }


    class OnWeekBodyInnerListener implements WeekViewBody.OnWeekBodyListener{

        @Override
        public void onEventCreate(WeekDraggableEventView eventView) {
//            if (onWeekBodyListener != null){}
            onWeekBodyListener.onEventClick(eventView);
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
