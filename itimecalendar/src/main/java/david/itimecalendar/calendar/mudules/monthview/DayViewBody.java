package david.itimecalendar.calendar.mudules.monthview;

import android.animation.Animator;
import android.animation.LayoutTransition;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.developer.paul.itimerecycleviewgroup.ITimeRecycleViewGroup;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import david.itimecalendar.R;
import david.itimecalendar.calendar.mudules.weekview.TimeSlotView;
import david.itimecalendar.calendar.listeners.ITimeEventPackageInterface;
import david.itimecalendar.calendar.listeners.ITimeTimeSlotInterface;
import david.itimecalendar.calendar.unitviews.DraggableEventView;
import david.itimecalendar.calendar.unitviews.DraggableTimeSlotView;
import david.itimecalendar.calendar.unitviews.RecommendedSlotView;
import david.itimecalendar.calendar.util.BaseUtil;
import david.itimecalendar.calendar.util.DensityUtil;
import david.itimecalendar.calendar.util.MyCalendar;
import david.itimecalendar.calendar.wrapper.WrapperTimeSlot;

/**
 * Created by yuhaoliu on 11/05/2017.
 */

public class DayViewBody extends RelativeLayout {
    private static final String TAG = "DayViewBody";

    private DayViewAllDay allDayView;
    private FrameLayout leftTimeBarLayout;
    private ScrollView leftTimeBarLayoutContainer;
    private ITimeRecycleViewGroup bodyRecyclerView;

    private BodyAdapter dayViewBodyAdapter;
    private Context context;

    // unit with px
    private int hourHeight = 30;
    private int timeTextSize = 20;
    private int topSpace = 30;
    private int leftBarWidth = 100;
    private int NUM_LAYOUTS = 3;
    private int allDayEventHeight = 100;

    private int color_time_text = R.color.text_enable;

    private AttributeSet attrs;

    public DayViewBody(@NonNull Context context) {
        super(context);
        init();
    }

    public DayViewBody(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.loadAttributes(attrs,context);
        this.attrs = attrs;
        init();
    }

    public DayViewBody(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.loadAttributes(attrs,context);
        this.attrs = attrs;
        init();
    }

    private void loadAttributes(AttributeSet attrs, Context context) {
        if (attrs != null && context != null) {
            TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.viewBody, 0, 0);
            try {
                NUM_LAYOUTS = typedArray.getInteger(R.styleable.viewBody_cellNum, NUM_LAYOUTS);
                topSpace = (int)typedArray.getDimension(R.styleable.viewBody_topSpace, topSpace);
                leftBarWidth = (int)typedArray.getDimension(R.styleable.viewBody_leftBarWidth, leftBarWidth);
                hourHeight = (int)typedArray.getDimension(R.styleable.viewBody_hourHeight, hourHeight);
                allDayEventHeight = (int)typedArray.getDimension(R.styleable.viewBody_allDayEventHeight, allDayEventHeight);
            } finally {
                typedArray.recycle();
            }
        }
    }

    public int getLeftBarWidth() {
        return leftBarWidth;
    }

    private void init(){
        this.context = getContext();
        this.setLayoutTransition(new LayoutTransition());
        setUpAllDay();
        setUpBody();
    }

    private void setUpAllDay(){
        allDayView = new DayViewAllDay(context, attrs);
        allDayView.setBackgroundColor(getResources().getColor(R.color.divider_calbg));
        allDayView.setElevation(20);
        allDayView.setSlotsInfo(this.timeSlotPackage);
        allDayView.setTimeslotEnable(false);
        allDayView.setId(generateViewId());
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        allDayView.setLayoutParams(params);
        this.addView(allDayView);
    }

    private void setUpBody(){
        setUpLeftTimeBar();
        setUpCalendarBody();
        allDayView.bringToFront();
    }

//    public void setScrollInterface(ITimeRecycleViewGroup.ScrollInterface scrollInterface){
//        bodyRecyclerView.setScrollInterface(scrollInterface);
//    }

    private void setUpCalendarBody(){
        dayViewBodyAdapter = new BodyAdapter(getContext(), this.attrs);
        dayViewBodyAdapter.setSlotsInfo(this.timeSlotPackage);
        bodyRecyclerView = new ITimeRecycleViewGroup(context, NUM_LAYOUTS);
        bodyRecyclerView.setAdapter(dayViewBodyAdapter);
        bodyRecyclerView.setOnSetting(new ITimeRecycleViewGroup.OnSetting() {
            @Override
            public int getItemHeight(int i) {
                int childMaxHeight = (int)(hourHeight * 24.5f);
                return childMaxHeight;
            }
        });
        bodyRecyclerView.setOnScrollListener(new ITimeRecycleViewGroup.OnScroll<DayViewBodyCell>() {
            @Override
            public void onPageSelected(DayViewBodyCell view) {
                if (onScroll != null){
                    onScroll.onPageSelected(view);
                }
            }

            @Override
            public void onHorizontalScroll(int dx, int preOffsetX) {
                allDayView.getRecycleViewGroup().followScrollByX(dx);

                if (onScroll != null){
                    onScroll.onHorizontalScroll(dx, preOffsetX);
                }
            }

            @Override
            public void onVerticalScroll(int dy, int preOffsetY) {
//                synViewsVerticalPosition(dy, leftTimeBarLayout);
                leftTimeBarLayoutContainer.scrollBy(0,-dy);
                if (onScroll != null){
                    onScroll.onVerticalScroll(dy, preOffsetY);
                }
            }
        });
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.leftMargin = leftBarWidth;
        params.addRule(BELOW, allDayView.getId());
        bodyRecyclerView.setLayoutParams(params);
        this.addView(bodyRecyclerView);

        //set up inner body listener
        setUpBodyCellInnerListener();
    }

//    public void setDisableCellScroll(boolean isDisabled){
//        bodyRecyclerView.setDisableCellScroll(isDisabled);
//    }

    private boolean isSwiping = false;
    private AutoSwipeHelper swipeHelper = new AutoSwipeHelper();
    private void setUpBodyCellInnerListener(){
        if (this.dayViewBodyAdapter != null){
            List<View> items = dayViewBodyAdapter.getViewItems();
            for (View view:items
                    ) {
                DayViewBodyCell cell = (DayViewBodyCell) view;
                cell.setOnBodyListener(new EventController.OnEventListener() {
                    @Override
                    public boolean isDraggable(DraggableEventView eventView) {
                        return onEventListener != null && onEventListener.isDraggable(eventView);
                    }

                    @Override
                    public void onEventCreate(DraggableEventView eventView) {
                        if (onEventListener != null){
                            onEventListener.onEventCreate(eventView);
                        }
                        dayViewBodyAdapter.notifyDataSetChanged();
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
                            int rawX = swipeHelper.getRawX(x, curAreaCal);
                            swipeHelper.bodyAutoSwipe(rawX);
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
                        dayViewBodyAdapter.notifyDataSetChanged();
                    }
                });
            }
        }
    }

    private void setUpLeftTimeBar(){
        this.leftTimeBarLayoutContainer = new ScrollView(getContext()){
            /**
             * Disable scrolling on hand
             * @param ev
             * @return
             */
            @Override
            public boolean onInterceptTouchEvent(MotionEvent ev) {
                return false;
            }

            @Override
            public boolean onTouchEvent(MotionEvent ev) {
                    return false;
            }
        };
        //hide scrollbar
        this.leftTimeBarLayoutContainer.setVerticalScrollBarEnabled(false);
        RelativeLayout.LayoutParams leftTimeBarLayoutContainerParams = new RelativeLayout.LayoutParams(leftBarWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
        leftTimeBarLayoutContainerParams.addRule(BELOW, allDayView.getId());
        leftTimeBarLayoutContainer.setLayoutParams(leftTimeBarLayoutContainerParams);
        this.addView(leftTimeBarLayoutContainer);

        this.leftTimeBarLayout = new FrameLayout(getContext());
        int leftBarHeight = this.initTimeText(getHours());
        //add right side decoration
        FrameLayout.LayoutParams dctParams = new FrameLayout.LayoutParams(DensityUtil.dip2px(context,1), leftBarHeight);
        dctParams.gravity = Gravity.END;
        View dctView = new View(context);
        dctView.setBackgroundColor(getResources().getColor(R.color.divider_nav));
        dctView.setLayoutParams(dctParams);
        dctView.setPadding(0, 0, 0, 0);
        this.leftTimeBarLayout.addView(dctView);

//        RelativeLayout.LayoutParams leftTimeBarParams = new RelativeLayout.LayoutParams(leftBarWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
//        leftTimeBarParams.addRule(BELOW, allDayView.getId());
//        leftTimeBarLayout.setLayoutParams(leftTimeBarParams);
//        this.addView(leftTimeBarLayout);
        this.leftTimeBarLayoutContainer.addView(leftTimeBarLayout);
    }

//    private void synViewsVerticalPosition(float dy, View targetView){
//        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) targetView.getLayoutParams();
//        if (params!=null){
//            params.topMargin += dy;
//            targetView.setLayoutParams(params);
//        }
//    }

    private int initTimeText(String[] HOURS) {
        int height = DensityUtil.dip2px(context,20);
        int leftBarHeight = 0;
        for (int time = 0; time < HOURS.length; time++) {
            int timeTextY = hourHeight * time + topSpace;

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

            leftBarHeight = timeTextY;
        }

        // line is one hour height longer than tvs
        leftBarHeight += hourHeight;

        return leftBarHeight;
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
        this.dayViewBodyAdapter.setEventPackage(eventPackage);
        this.allDayView.setEventPackage(eventPackage);
    }

    private OnViewBodyEventListener onEventListener;
    private OnViewBodyTimeSlotListener onTimeslotListener;

    public void setOnBodyEventListener(OnViewBodyEventListener onEventListener) {
        this.onEventListener = onEventListener;
    }

    public void setOnBodyTimeslotListener(OnViewBodyTimeSlotListener onTimeslotListener) {
        this.onTimeslotListener = onTimeslotListener;
    }

    private ITimeRecycleViewGroup.OnScroll onScroll;

    public void setOnScrollListener(ITimeRecycleViewGroup.OnScroll onScroll){
        this.onScroll = onScroll;
    }

    public void smoothMoveWithOffset(int moveOffset){
        bodyRecyclerView.smoothMoveWithOffsetX(moveOffset, null);
    }

    public void scrollToDate(Date date){
        if (bodyRecyclerView.getFirstShowItem() == null){
            return;
        }

        MyCalendar currentFstShowDay = ((DayViewBodyCell) bodyRecyclerView.getFirstShowItem()).getCalendar();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int offset = BaseUtil.getDatesDifference(currentFstShowDay.getCalendar().getTimeInMillis(), cal.getTimeInMillis());
        //move body layout
        bodyRecyclerView.moveWithOffsetX(offset);
        //move allday layout
        allDayView.getRecycleViewGroup().moveWithOffsetX(offset);
    }

    public void refresh(){
        dayViewBodyAdapter.notifyDataSetChanged();
    }

    private TimeSlotView.TimeSlotPackage timeSlotPackage = new TimeSlotView.TimeSlotPackage();

    public TimeSlotView.TimeSlotPackage getTimeSlotPackage(){
        return timeSlotPackage;
    }

    public void enableTimeSlot(){
        allDayView.setTimeslotEnable(true);
        if (dayViewBodyAdapter != null){
            List<View> items = dayViewBodyAdapter.getViewItems();
            for (View view:items
                    ) {
                DayViewBodyCell cell = (DayViewBodyCell) view;
                cell.enableTimeSlot();
            }
        }
    }

    public void addTimeSlot(ITimeTimeSlotInterface slotInfo){
        WrapperTimeSlot wrapper = new WrapperTimeSlot(slotInfo);
        addSlotToPackage(wrapper);

        if (dayViewBodyAdapter != null){
            dayViewBodyAdapter.notifyDataSetChanged();
        }

        allDayView.notifyDataSetChanged();
    }

    public void addTimeSlot(WrapperTimeSlot wrapperTimeSlot){
        addSlotToPackage(wrapperTimeSlot);
        if (dayViewBodyAdapter != null){
            dayViewBodyAdapter.notifyDataSetChanged();
        }
        allDayView.notifyDataSetChanged();
    }

//    public void addTimeSlot(ITimeTimeSlotInterface slotInfo, boolean isSelected){
//        WrapperTimeSlot wrapper = new WrapperTimeSlot(slotInfo);
//        wrapper.setSelected(isSelected);
//        addSlotToPackage(wrapper);
//        if (dayViewBodyAdapter != null){
//            dayViewBodyAdapter.notifyDataSetChanged();
//        }
//        allDayView.notifyDataSetChanged();
//    }

    private void addSlotToPackage(WrapperTimeSlot wrapperSlot){
        if (wrapperSlot.isRecommended() && !wrapperSlot.isSelected()){
            timeSlotPackage.rcdSlots.add(wrapperSlot);
        }else {
            timeSlotPackage.realSlots.add(wrapperSlot);
        }
    }

    public void removeTimeslot(ITimeTimeSlotInterface timeslot){
        getTimeSlotPackage().realSlots.remove(timeslot);
    }

    public void removeTimeslot(TimeSlotView timeslotView){

    }

    public void resetTimeSlots(){
        timeSlotPackage.clear();
    }

    public void updateTimeSlotsDuration(long duration, boolean animate){
        if (dayViewBodyAdapter != null){
            List<View> items = dayViewBodyAdapter.getViewItems();
            for (View view:items
                    ) {
                DayViewBodyCell cell = (DayViewBodyCell) view;
                cell.updateTimeSlotsDuration(duration,animate);
            }
        }
    }

    TimeSlotController.OnTimeSlotListener onTimeSlotOuterListener;
    TimeSlotController.OnTimeSlotListener onTimeSlotInnerListener;

    private void initOnTimeSlotListener(){
        onTimeSlotInnerListener = new OnTimeSlotInnerListener();
        if (dayViewBodyAdapter != null){
            List<View> items = dayViewBodyAdapter.getViewItems();
            for (View view:items
                    ) {
                DayViewBodyCell cell = (DayViewBodyCell) view;
                cell.setOnTimeSlotListener(onTimeSlotInnerListener);
            }
        }
    }

    public void setOnTimeSlotListener(OnViewBodyTimeSlotListener onTimeSlotOuterListener) {
        this.initOnTimeSlotListener();
        this.allDayView.setAllDayTimeslotListener(onTimeSlotOuterListener);
        this.onTimeSlotOuterListener = onTimeSlotOuterListener;
    }

    private class OnTimeSlotInnerListener implements TimeSlotController.OnTimeSlotListener{
        @Override
        public void onTimeSlotCreate(DraggableTimeSlotView draggableTimeSlotView) {

            if (onTimeSlotOuterListener != null){
                onTimeSlotOuterListener.onTimeSlotCreate(draggableTimeSlotView);
            }
            dayViewBodyAdapter.notifyDataSetChanged();
        }

        @Override
        public void onTimeSlotClick(DraggableTimeSlotView draggableTimeSlotView) {
            if (onTimeSlotOuterListener != null){
                onTimeSlotOuterListener.onTimeSlotClick(draggableTimeSlotView);
            }

            dayViewBodyAdapter.notifyDataSetChanged();
        }

        @Override
        public void onRcdTimeSlotClick(RecommendedSlotView v) {
            if (onTimeSlotOuterListener != null){
                onTimeSlotOuterListener.onRcdTimeSlotClick(v);
            }

            dayViewBodyAdapter.notifyDataSetChanged();
        }

        @Override
        public void onTimeSlotDragStart(DraggableTimeSlotView draggableTimeSlotView) {
            if (onTimeSlotOuterListener != null){
                onTimeSlotOuterListener.onTimeSlotDragStart(draggableTimeSlotView);
            }
            dayViewBodyAdapter.notifyDataSetChanged();
        }

        @Override
        public void onTimeSlotDragging(DraggableTimeSlotView draggableTimeSlotView, MyCalendar curAreaCal,int x, int y) {
            if (!isSwiping){
                int rawX = swipeHelper.getRawX(x, curAreaCal);
                swipeHelper.bodyAutoSwipe(rawX);
            }else {
                Log.i(TAG, "onEventDragging: isSwiping , discard");
            }

            if (onTimeSlotOuterListener != null){
                onTimeSlotOuterListener.onTimeSlotDragging(draggableTimeSlotView, curAreaCal, x, y);
            }
        }

        @Override
        public void onTimeSlotDragDrop(DraggableTimeSlotView draggableTimeSlotView, long start, long end) {
            // calendar pass the start - 0, end - 0, calling outer listener with real start&end time
            if (onTimeSlotOuterListener != null){
                onTimeSlotOuterListener.onTimeSlotDragDrop(draggableTimeSlotView, draggableTimeSlotView.getNewStartTime(), draggableTimeSlotView.getNewEndTime());
            }
            dayViewBodyAdapter.notifyDataSetChanged();
        }

        @Override
        public void onTimeSlotEdit(DraggableTimeSlotView draggableTimeSlotView) {
//            if (onTimeSlotOuterListener != null){
//                onTimeSlotOuterListener.onTimeSlotEdit(draggableTimeSlotView);
//            }
//
//            dayViewBodyAdapter.notifyDataSetChanged();
        }

        @Override
        public void onTimeSlotDelete(DraggableTimeSlotView draggableTimeSlotView) {
//            if (onTimeSlotOuterListener != null){
//                onTimeSlotOuterListener.onTimeSlotDelete(draggableTimeSlotView);
//            }
//
//            dayViewBodyAdapter.notifyDataSetChanged();
        }
    }

    private class AutoSwipeHelper{
        private final long moveInterval = 700;
        private final int DIRECTION_LEFT = -1;
        private final int DIRECTION_STAY = 0;
        private final int DIRECTION_RIGHT = 1;

        private Animator.AnimatorListener animatorListener = new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isSwiping = false;
                    }
                },moveInterval/NUM_LAYOUTS);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isSwiping = false;
                    }
                },moveInterval/NUM_LAYOUTS);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        };

        void bodyAutoSwipe(int x){
            int direction = x > (bodyRecyclerView.getWidth()/2) ? DIRECTION_RIGHT:DIRECTION_LEFT;
            float threshold = getSwipeThreshHold(0.85f, direction);

            switch (direction){
                case DIRECTION_LEFT:
                    if (x < threshold){
                        isSwiping = true;
                        bodyRecyclerView.smoothMoveWithOffsetX(-1, animatorListener);
                    }
                    break;
                case DIRECTION_RIGHT:
                    if (x > threshold){
                        isSwiping = true;
                        bodyRecyclerView.smoothMoveWithOffsetX(1, animatorListener);
                    }
                    break;
                default:
                    break;
            }
        }

        int getRawX(int relativeX, MyCalendar curAreaCal){
            int recyclerViewWidth = bodyRecyclerView.getWidth();
            float cellWidth = (float) recyclerViewWidth/NUM_LAYOUTS;

            Calendar dropAtCal = curAreaCal.getCalendar();
            DayViewBodyCell cell = (DayViewBodyCell) bodyRecyclerView.getFirstShowItem();
            Calendar fstShowCal = cell.getCalendar().getCalendar();
            int offset = BaseUtil.getDatesDifference(fstShowCal.getTimeInMillis(),dropAtCal.getTimeInMillis());

            return (int) (offset * cellWidth) + relativeX;
        }

        private float getSwipeThreshHold(float percentFactor, int direction){
            int recyclerViewWidth = bodyRecyclerView.getWidth();
            float cellWidth = (float) recyclerViewWidth/NUM_LAYOUTS;

            if (direction == DIRECTION_LEFT){
                return cellWidth * (1 - percentFactor);
            }

            if (direction == DIRECTION_RIGHT){
                return recyclerViewWidth - cellWidth * (1 - percentFactor);
            }

            return recyclerViewWidth;
        }
    }

    public ITimeRecycleViewGroup getRecycler(){
        return this.bodyRecyclerView;
    }


    public void setAllDayListener(DayViewAllDay.AllDayTimeslotListener allDayTimeslotListener) {
        this.allDayView.setAllDayTimeslotListener(allDayTimeslotListener);
    }

    public void notifyDataSetChanged(){
        dayViewBodyAdapter.notifyDataSetChanged();
        allDayView.notifyDataSetChanged();
    }

    private Mode mode = Mode.REGULAR;

    public enum Mode{
        ALL_DAY, REGULAR
    }

    public void setViewMode(Mode mode){
        this.mode = mode;

        switch (this.mode){
            case ALL_DAY:
                allDayView.setAlldayTimeslotEnable(true);
                break;
            case REGULAR:
                allDayView.setAlldayTimeslotEnable(false);
                break;
        }
    }

    public interface OnViewBodyTimeSlotListener extends TimeSlotController.OnTimeSlotListener,DayViewAllDay.AllDayTimeslotListener {
    }

    public interface OnViewBodyEventListener extends EventController.OnEventListener, DayViewAllDay.AllDayEventListener {
    }
}
