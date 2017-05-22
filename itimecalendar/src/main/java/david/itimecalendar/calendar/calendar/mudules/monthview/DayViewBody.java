package david.itimecalendar.calendar.calendar.mudules.monthview;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import david.itimecalendar.R;
import david.itimecalendar.calendar.listeners.ITimeEventPackageInterface;
import david.itimecalendar.calendar.unitviews.DraggableEventView;
import david.itimecalendar.calendar.util.BaseUtil;
import david.itimecalendar.calendar.util.DensityUtil;
import david.itimecalendar.calendar.util.MyCalendar;
import david.itimerecycler.RecycledViewGroup;

/**
 * Created by yuhaoliu on 11/05/2017.
 */

public class DayViewBody extends FrameLayout {
    private static final String TAG = "DayViewBody";
    private FrameLayout leftTimeBarLayout;

    private RecycledViewGroup bodyRecyclerView;
    private BodyAdapter bodyPagerAdapter;
    private Context context;

    private int leftBarWidth = 100;
    private int hourHeight = 30;
    private int timeTextSize = 20;
    private int spaceTop = 30;
    private int NUM_LAYOUTS = 3;

    private int color_time_text = R.color.text_enable;

    public DayViewBody(@NonNull Context context) {
        super(context);
        init();
    }

    public DayViewBody(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DayViewBody(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        this.context = getContext();
        this.hourHeight = DensityUtil.dip2px(context, hourHeight);
        setUpBody();
    }

    private void setUpBody(){
        setUpLeftTimeBar();
        setUpCalendarBody();
    }

    private void setUpCalendarBody(){
        bodyRecyclerView = new RecycledViewGroup(context, hourHeight, NUM_LAYOUTS);
        bodyPagerAdapter = new BodyAdapter(getContext());
        bodyRecyclerView.setAdapter(bodyPagerAdapter);
        bodyRecyclerView.setOnScrollListener(new RecycledViewGroup.OnScroll() {
            @Override
            public void onPageSelected(View v) {
                if (onScroll != null){
                    onScroll.onPageSelected(v);
                }
            }

            @Override
            public void onHorizontalScroll(float dx, float preOffsetX) {

            }

            @Override
            public void onVerticalScroll(float dy, float preOffsetY) {
                synViewsVerticalPosition(preOffsetY + dy, leftTimeBarLayout);
                if (onScroll != null){
                    onScroll.onVerticalScroll(dy, preOffsetY);
                }
            }
        });
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.leftMargin = leftBarWidth;
        this.addView(bodyRecyclerView, params);

        //set up inner body listener
        setUpBodyCellInnerListener();
    }

    private boolean isSwiping = false;

    private void setUpBodyCellInnerListener(){
        if (this.bodyPagerAdapter != null){
            List<View> items = bodyPagerAdapter.getViewItems();
            for (View view:items
                    ) {
                DayViewBodyCell cell = (DayViewBodyCell) view;
                cell.setOnBodyListener(new EventController.OnEventListener() {
                    private final int DIRECTION_LEFT = -1;
                    private final int DIRECTION_STAY = 0;
                    private final int DIRECTION_RIGHT = 1;

                    private Animation.AnimationListener animationListener = new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            isSwiping = false;
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    };

                    @Override
                    public boolean isDraggable(DraggableEventView eventView) {
                        return onEventListener != null && onEventListener.isDraggable(eventView);
                    }

                    @Override
                    public void onEventCreate(DraggableEventView eventView) {
                        if (onEventListener != null){
                            onEventListener.onEventCreate(eventView);
                        }
                    }

                    @Override
                    public void onEventClick(DraggableEventView eventView) {
                        if (onEventListener != null){
                            onEventListener.onEventClick(eventView);
                        }
                    }

                    @Override
                    public void onEventDragStart(DraggableEventView eventView) {
                        if (onEventListener != null){
                            onEventListener.onEventDragStart(eventView);
                        }
                    }

                    @Override
                    public void onEventDragging(DraggableEventView eventView, MyCalendar curAreaCal, int x, int y) {
                        if (!isSwiping){
                            int rawX = getRawX(x, curAreaCal);
                            this.bodyAutoSwipe(rawX);
                        }else {
                            Log.i(TAG, "onEventDragging: isSwiping , discard");
                        }

                        if (onEventListener != null){
                            onEventListener.onEventDragging(eventView,curAreaCal, x, y);
                        }
                    }

                    @Override
                    public void onEventDragDrop(DraggableEventView eventView) {
                        if (onEventListener != null){
                            onEventListener.onEventDragDrop(eventView);
                        }
                    }

                    private void bodyAutoSwipe(int x){
                        int direction = x > bodyRecyclerView.getWidth()/2 ? DIRECTION_RIGHT:DIRECTION_LEFT;
                        float threshold = getSwipeThreshHold(0.7f, direction);

                        switch (direction){
                            case DIRECTION_LEFT:
                                if (x < threshold){
                                    isSwiping = true;
                                    bodyRecyclerView.smoothMoveWithOffset(-1, animationListener);
                                }
                                break;
                            case DIRECTION_RIGHT:
                                if (x > threshold){
                                    isSwiping = true;
                                    bodyRecyclerView.smoothMoveWithOffset(1, animationListener);
                                }
                                break;
                            default:
                                break;
                        }
                    }

                    private int getRawX(int relativeX, MyCalendar curAreaCal){
                        int recyclerViewWidth = bodyRecyclerView.getWidth();
                        float cellWidth = (float) recyclerViewWidth/NUM_LAYOUTS;

                        Calendar dropAtCal = curAreaCal.getCalendar();
                        DayViewBodyCell cell = (DayViewBodyCell) bodyRecyclerView.getFirstShowItem();
                        Calendar fstShowCal = cell.getCalendar().getCalendar();

                        int offset = dropAtCal.get(Calendar.DATE) - fstShowCal.get(Calendar.DATE);

                        return (int) (offset * cellWidth) + relativeX;
                    }

                    private float getSwipeThreshHold(float percentFactor, int direction){
                        int recyclerViewWidth = bodyRecyclerView.getWidth();
                        float cellWidth = (float) recyclerViewWidth/NUM_LAYOUTS;

                        if (direction == DIRECTION_LEFT){
                            return cellWidth * percentFactor;
                        }

                        if (direction == DIRECTION_RIGHT){
                            return recyclerViewWidth - cellWidth * (1 - percentFactor);
                        }

                        return recyclerViewWidth;
                    }
                });
            }
        }
    }

    private void setUpLeftTimeBar(){
        this.leftTimeBarLayout = new FrameLayout(getContext());
        this.initTimeText(getHours());
        this.addView(leftTimeBarLayout, new FrameLayout.LayoutParams(leftBarWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    private void synViewsVerticalPosition(float toPositionY, View targetV){
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) targetV.getLayoutParams();
        if (params!=null){
            params.topMargin = (int) toPositionY;
            targetV.layout(0,params.topMargin, leftBarWidth,this.getHeight());
        }
    }

    private void scrollVertical(float dy, View view){
    }

    private void initTimeText(String[] HOURS) {
        int height = DensityUtil.dip2px(context,20);
        for (int time = 0; time < HOURS.length; time++) {
            int timeTextY = hourHeight * time + spaceTop;

            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(leftBarWidth, height);
            TextView timeView = new TextView(context);
            timeView.setTextColor(context.getResources().getColor(color_time_text));
            timeView.setText(HOURS[time]);
            timeView.setTextSize(11);
            timeView.setGravity(Gravity.CENTER);
            params.setMargins(0, timeTextY - height/2, 0, 0);
            timeView.setLayoutParams(params);

            timeTextSize = (int) timeView.getTextSize() + timeView.getPaddingTop();
            leftTimeBarLayout.addView(timeView);
        }
    }

    private String[] getHours() {
        String[] HOURS = new String[]{
                "00", "01", "02", "03", "04", "05", "06", "07",
                "08", "09", "10", "11", "12", "13", "14", "15",
                "16", "17", "18", "19", "20", "21", "22", "23",
                "24"
        };

        return HOURS;
    }

    /************************************************************************************/
    public void setEventPackage(ITimeEventPackageInterface eventPackage){
        this.bodyPagerAdapter.setEventPackage(eventPackage);
    }

    private EventController.OnEventListener onEventListener;
    public void setOnBodyListener(EventController.OnEventListener onEventListener) {
        this.onEventListener = onEventListener;
    }

    private RecycledViewGroup.OnScroll onScroll;
    public void setOnScrollListener(RecycledViewGroup.OnScroll onScroll){
        this.onScroll = onScroll;
    }

    public void smoothMoveWithOffset(int moveOffset){
        bodyRecyclerView.smoothMoveWithOffset(moveOffset, null);
    }

    public void scrollToDate(Date date){
        MyCalendar currentFstShowDay = ((DayViewBodyCell) bodyRecyclerView.getFirstShowItem()).getCalendar();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int offset = BaseUtil.getDatesDifference(currentFstShowDay.getCalendar().getTimeInMillis(), cal.getTimeInMillis());
        bodyRecyclerView.moveWithOffset(offset);
    }

    public void refresh(){
        bodyPagerAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
