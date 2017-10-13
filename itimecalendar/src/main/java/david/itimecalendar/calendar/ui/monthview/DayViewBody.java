package david.itimecalendar.calendar.ui.monthview;

import android.animation.Animator;
import android.animation.LayoutTransition;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.developer.paul.itimerecycleviewgroup.ITimeRecycleViewGroup;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import david.itimecalendar.R;
import david.itimecalendar.calendar.ui.CalendarConfig;
import david.itimecalendar.calendar.ui.weekview.TimeSlotView;
import david.itimecalendar.calendar.listeners.ITimeEventPackageInterface;
import david.itimecalendar.calendar.listeners.ITimeTimeSlotInterface;
import david.itimecalendar.calendar.ui.unitviews.DraggableEventView;
import david.itimecalendar.calendar.ui.unitviews.DraggableTimeSlotView;
import david.itimecalendar.calendar.ui.unitviews.RcdRegularTimeSlotView;
import david.itimecalendar.calendar.util.BaseUtil;
import david.itimecalendar.calendar.util.CalendarPositionHelper;
import david.itimecalendar.calendar.util.DensityUtil;
import david.itimecalendar.calendar.util.MyCalendar;
import david.itimecalendar.calendar.wrapper.WrapperTimeSlot;

/**
 * Created by yuhaoliu on 11/05/2017.
 */

public class DayViewBody extends RelativeLayout {
    private static final String TAG = "DayViewBody";

    private CalendarPositionHelper calendarPositionHelper = new CalendarPositionHelper();

    public DayViewAllDay allDayView;
    private FrameLayout leftTimeBarLayout;
    private ScrollView leftTimeBarLayoutContainer;

    private FrameLayout bodyContainer;
    private ITimeRecycleViewGroup bodyRecyclerView;
    private TextView msgWindow;

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

    private VelocityTracker mVelocityTracker;
    private int mMaxVelocity;
    private float mVelocityX;
    private float mVelocityY;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int action = MotionEventCompat.getActionMasked(ev);
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:{
                if (mVelocityTracker == null) {
                    mVelocityTracker = VelocityTracker.obtain();
                } else {
                    mVelocityTracker.clear();
                }
                break;
            }
            case MotionEvent.ACTION_MOVE:{
                mVelocityTracker.addMovement(ev);
                mVelocityTracker.computeCurrentVelocity(1000, mMaxVelocity);
                mVelocityX = mVelocityTracker.getXVelocity();
                mVelocityY = mVelocityTracker.getYVelocity();
                break;
            }
        }

        return super.onInterceptTouchEvent(ev);
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
        this.mMaxVelocity = ViewConfiguration.get(getContext()).getScaledMaximumFlingVelocity();
        this.context = getContext();
        this.setLayoutTransition(new LayoutTransition());
        setUpAllDay();
        setUpBody();
    }

    private void setUpAllDay(){
        allDayView = new DayViewAllDay(context, attrs);
        allDayView.setBackgroundColor(getResources().getColor(R.color.divider_calbg));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            allDayView.setElevation(20);
        }
        allDayView.setSlotsInfo(this.timeSlotPackage);
        allDayView.setId(generateViewId());
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        allDayView.setLayoutParams(params);
        this.addView(allDayView);
    }

    private void setUpBody(){
        setUpLeftTimeBar();
        setUpCalendarBody();
        allDayView.bringToFront();
        initMsgWindow();
    }

    private void initMsgWindow() {
        msgWindow = new TextView(context);
        msgWindow.setTextColor(context.getResources().getColor(R.color.now_end_time));
        msgWindow.setText("SUN 00:00");
        msgWindow.setTextSize(14);
        msgWindow.setGravity(Gravity.LEFT);
        msgWindow.setVisibility(View.INVISIBLE);
        msgWindow.measure(0, 0);
        int height = msgWindow.getMeasuredHeight(); //get height
        int width = msgWindow.getMeasuredWidth();
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(width+10, height);
        params.setMargins(0, 0, 0, 0);
        msgWindow.setLayoutParams(params);
        bodyContainer.addView(msgWindow);
    }

//    public void setScrollInterface(ITimeRecycleViewGroup.ScrollInterface scrollInterface){
//        bodyRecyclerView.setScrollInterface(scrollInterface);
//    }

    private void setUpCalendarBody(){
        bodyContainer = new FrameLayout(getContext());

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.leftMargin = leftBarWidth;
        layoutParams.addRule(BELOW, allDayView.getId());
        bodyContainer.setLayoutParams(layoutParams);
        this.addView(bodyContainer);

        dayViewBodyAdapter = new BodyAdapter(getContext(), this.attrs, this.calendarPositionHelper);
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

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        bodyRecyclerView.setLayoutParams(params);
        bodyContainer.addView(bodyRecyclerView);

        //set up inner body listener
        setUpBodyCellInnerListener();
    }

    private boolean isSwiping = false;
    private AutoSwipeHelper swipeHelper = new AutoSwipeHelper();

    private void setUpBodyCellInnerListener(){
        if (this.dayViewBodyAdapter != null){
            List<View> items = dayViewBodyAdapter.getViewItems();
            for (View view:items
                    ) {
                DayViewBodyCell cell = (DayViewBodyCell) view;
                cell.setOnBodyListener(new EventController.OnEventListener() {
                    int draggingStartPoint = 0;

                    @Override
                    public boolean isDraggable(DraggableEventView eventView) {
                        return onEventListener != null && onEventListener.isDraggable(eventView);
                    }

                    @Override
                    public void onEventCreate(DraggableEventView eventView) {
                        if (msgWindow.getVisibility() == View.VISIBLE){
                            msgWindow.setVisibility(View.GONE);
                        }

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
                        draggingStartPoint = swipeHelper.getRawX((int) eventView.getX(), eventView.getCalendar());
                        Log.i(TAG, "bodyAutoSwipe: draggingStartPoint: " + draggingStartPoint);
                        if (onEventListener != null){
                            onEventListener.onEventDragStart(eventView);
                        }
                    }

                    @Override
                    public void onEventDragging(DraggableEventView eventView, MyCalendar curAreaCal, int touchX, int touchY, int viewX, int viewY, String locationTime) {
                        if (msgWindow.getVisibility() != View.VISIBLE){
                            msgWindow.setVisibility(View.VISIBLE);
                        }

                        msgWindowFollow(viewX, viewY,locationTime);

                        scrollViewAutoScrollY(touchY);

                        if (!isSwiping){
                            int rawX = swipeHelper.getRawX(touchX, curAreaCal);
                            int diff = rawX - draggingStartPoint;
                            swipeHelper.bodyAutoSwipe(rawX, diff);
                        }else {
                            Log.i(TAG, "onEventDragging: isSwiping , discard");
                        }

                        if (onEventListener != null){
                            onEventListener.onEventDragging(eventView,curAreaCal, touchX, touchY, viewX, viewY, locationTime);
                        }
                    }

                    @Override
                    public void onEventDragDrop(DraggableEventView eventView) {
                        if (msgWindow.getVisibility() == View.VISIBLE){
                            msgWindow.setVisibility(View.GONE);
                        }

                        if (onEventListener != null){
                            onEventListener.onEventDragDrop(eventView);
                        }
                        dayViewBodyAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onEventDragEnd(DraggableEventView eventView) {
                        if (msgWindow.getVisibility() == View.VISIBLE){
                            msgWindow.setVisibility(View.GONE);
                        }
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
        this.leftTimeBarLayoutContainer.addView(leftTimeBarLayout);
    }

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

    private void msgWindowFollow(int tapX, int tapY, String locationTime) {
        float toX;
        float toY;

        toY = tapY - leftTimeBarLayoutContainer.getScrollY() - msgWindow.getHeight();
        if (toY <= 0){
            toY = 0;
        }

        toX = 0;
        if (tapX < msgWindow.getWidth() * 1.5){
            toX = bodyContainer.getWidth() - msgWindow.getWidth();
        }

        msgWindow.setText(locationTime);
        msgWindow.setTranslationX(toX);
        msgWindow.setTranslationY(toY);
    }

    private int autoScrollFlag;
    private int autoScrollRange;

    private void scrollViewAutoScrollY(int y) {
        autoScrollFlag = DensityUtil.dip2px(context,20);
        autoScrollRange = DensityUtil.dip2px(context,10);

        float needPositionY_top = y - autoScrollFlag;
        float needPositionY_bottom = y + autoScrollFlag;

        int top = (leftTimeBarLayoutContainer.getScrollY());
        int bottom = (leftTimeBarLayoutContainer.getScrollY() + bodyContainer.getHeight());

        // TODO: 26/7/17 Paul: need to fix wrong parameter sign
        if (top > needPositionY_top) {
            bodyRecyclerView.scrollByYSmoothly(autoScrollRange);
        } else if (bottom < needPositionY_bottom) {
            bodyRecyclerView.scrollByYSmoothly(-autoScrollRange);
        }
    }
    /************************************************************************************/
    public void setEventPackage(ITimeEventPackageInterface eventPackage){
        this.dayViewBodyAdapter.setEventPackage(eventPackage);
        this.allDayView.setEventPackage(eventPackage);
    }

    private OnViewBodyEventListener onEventListener;
    private OnViewBodyTimeSlotListener onTimeslotListener;

    public void hideBodyTimeslot(){
        List<View> holders = dayViewBodyAdapter.getViewItems();
        for (View view:holders
             ) {
            DayViewBodyCell cell = (DayViewBodyCell) view;
            cell.hideTimeslot();
        }
    }

    public void showBodyTimeslot(){
        List<View> holders = dayViewBodyAdapter.getViewItems();
        for (View view:holders
                ) {
            DayViewBodyCell cell = (DayViewBodyCell) view;
            cell.showTimeslot();
        }
    }

    public void setOnBodyEventListener(OnViewBodyEventListener onEventListener) {
        this.onEventListener = onEventListener;
        this.allDayView.setAllDayEventListener(onEventListener);
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

    public void scrollToDate(final Date date, final boolean toTime){
        if (bodyRecyclerView.getFirstShowItem() == null){
            return;
        }

        if (!bodyRecyclerView.isShown()){
            ViewTreeObserver vto = getViewTreeObserver();
            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    DayViewBody.this.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    actuallScroll(date, toTime);
                }
            });
        }else {
            actuallScroll(date, toTime);
        }
    }

    private void actuallScroll(Date date, boolean toTime){
        MyCalendar currentFstShowDay = ((DayViewBodyCell) bodyRecyclerView.getFirstShowItem()).getCalendar();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int dateDiff = BaseUtil.getDatesDifference(currentFstShowDay.getCalendar().getTimeInMillis(), cal.getTimeInMillis());
        //move body layout
        bodyRecyclerView.moveWithOffsetX(dateDiff);
        //move allday layout
        allDayView.getRecycleViewGroup().moveWithOffsetX(dateDiff);
        //scroll body to time
        if (toTime){
            String time = DayViewBodyCell.sdf.format(date);
            String[] components = time.split(":");
            float trickTime = Integer.valueOf(components[0]) + Integer.valueOf(components[1]) / (float) 100;
            int targetY = calendarPositionHelper.nearestTimeSlotValue(trickTime);
            int diffY = -targetY - (int)bodyRecyclerView.getAwesomeScrollY();
            bodyRecyclerView.scrollByY(diffY);
        }
    }

    public void refresh(){
        dayViewBodyAdapter.notifyDataSetChanged();
    }

    private TimeSlotView.TimeSlotPackage timeSlotPackage = new TimeSlotView.TimeSlotPackage();

    public TimeSlotView.TimeSlotPackage getTimeSlotPackage(){
        return timeSlotPackage;
    }

    public void addTimeSlot(ITimeTimeSlotInterface slotInfo){
        WrapperTimeSlot wrapper = new WrapperTimeSlot(slotInfo);
        addSlotToPackage(wrapper);

        if (slotInfo.isAllDay()){
            allDayView.notifyDataSetChanged();
        }else if (dayViewBodyAdapter != null){
            dayViewBodyAdapter.notifyDataSetChanged();
        }
    }

    public void addTimeSlots(List<? extends ITimeTimeSlotInterface> slotsInfo){
        if (slotsInfo == null || slotsInfo.size() == 0){
            return;
        }

        for (ITimeTimeSlotInterface slotInfo:slotsInfo
             ) {
            WrapperTimeSlot wrapper = new WrapperTimeSlot(slotInfo);
            addSlotToPackage(wrapper);
        }

        if (slotsInfo.get(0).isAllDay()){
            allDayView.notifyDataSetChanged();
        }else if (dayViewBodyAdapter != null){
            dayViewBodyAdapter.notifyDataSetChanged();
        }
    }

    public void addTimeSlot(WrapperTimeSlot wrapperTimeSlot){
        addSlotToPackage(wrapperTimeSlot);
        if (wrapperTimeSlot.getTimeSlot().isAllDay()){
            allDayView.notifyDataSetChanged();
        }else if (dayViewBodyAdapter != null){
            dayViewBodyAdapter.notifyDataSetChanged();
        }
    }

    private void addSlotToPackage(WrapperTimeSlot wrapperSlot){
        if (wrapperSlot.isRecommended() && !wrapperSlot.isSelected()){
            timeSlotPackage.addRcdTimesSlot(wrapperSlot);
        }else {
            timeSlotPackage.addRealTimesSlot(wrapperSlot);
        }
    }

    public void removeTimeslot(ITimeTimeSlotInterface timeslot){
        getTimeSlotPackage().realSlots.remove(timeslot);
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
        int draggingStartPoint = 0;

        @Override
        public void onTimeSlotCreate(DraggableTimeSlotView draggableTimeSlotView) {
            if (msgWindow.getVisibility() == View.VISIBLE){
                msgWindow.setVisibility(View.GONE);
            }

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
        public void onRcdTimeSlotClick(RcdRegularTimeSlotView v) {
            if (onTimeSlotOuterListener != null){
                onTimeSlotOuterListener.onRcdTimeSlotClick(v);
            }

            dayViewBodyAdapter.notifyDataSetChanged();
        }

        @Override
        public void onTimeSlotDragStart(DraggableTimeSlotView draggableTimeSlotView) {
            draggingStartPoint = swipeHelper.getRawX((int) draggableTimeSlotView.getX(), draggableTimeSlotView.getCalendar());

            if (onTimeSlotOuterListener != null){
                onTimeSlotOuterListener.onTimeSlotDragStart(draggableTimeSlotView);
            }
            dayViewBodyAdapter.notifyDataSetChanged();
        }

        @Override
        public void onTimeSlotDragging(DraggableTimeSlotView draggableTimeSlotView, MyCalendar curAreaCal, int touchX, int touchY, int viewX, int viewY, String locationTime) {
            if (msgWindow.getVisibility() != View.VISIBLE){
                msgWindow.setVisibility(View.VISIBLE);
            }

            msgWindowFollow(viewX, viewY,locationTime);

            scrollViewAutoScrollY(touchY);

            if (!isSwiping){
                int rawX = swipeHelper.getRawX(touchX, curAreaCal);
                int diff = rawX - draggingStartPoint;
                swipeHelper.bodyAutoSwipe(rawX, diff);
            }else {
                Log.i(TAG, "onEventDragging: isSwiping , discard");
            }

            if (onTimeSlotOuterListener != null){
                onTimeSlotOuterListener.onTimeSlotDragging(draggableTimeSlotView, curAreaCal, touchX, touchY, viewX, viewY,locationTime);
            }
        }

        @Override
        public void onTimeSlotDragDrop(DraggableTimeSlotView draggableTimeSlotView, long start, long end) {
            if (msgWindow.getVisibility() == View.VISIBLE){
                msgWindow.setVisibility(View.GONE);
            }

            // calendar pass the start - 0, end - 0, calling outer listener with real start&end time
            if (onTimeSlotOuterListener != null){
                onTimeSlotOuterListener.onTimeSlotDragDrop(draggableTimeSlotView, draggableTimeSlotView.getNewStartTime(), draggableTimeSlotView.getNewEndTime());
            }
            dayViewBodyAdapter.notifyDataSetChanged();
        }

        @Override
        public void onTimeSlotDragEnd(DraggableTimeSlotView draggableTimeSlotView) {
            if (msgWindow.getVisibility() == View.VISIBLE){
                msgWindow.setVisibility(View.GONE);
            }
        }

        @Override
        public void onTimeSlotEdit(DraggableTimeSlotView draggableTimeSlotView) {
        }

        @Override
        public void onTimeSlotDelete(DraggableTimeSlotView draggableTimeSlotView) {
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

        void bodyAutoSwipe(int x, int diff){
            int direction = x > (bodyRecyclerView.getWidth()/2) ? DIRECTION_RIGHT:DIRECTION_LEFT;
            float swipeDistanceThreshold = bodyRecyclerView.getWidth()/3;
            float locationThreshold = getSwipeThreshHold(0.7f, direction);

           if (Math.abs(diff) < swipeDistanceThreshold){
                return;
            }

            switch (direction){
                case DIRECTION_LEFT:
                    if (x < locationThreshold){
                        isSwiping = true;
//                        bodyRecyclerView.smoothMoveWithOffsetX(-1, animatorListener);
                    }
                    break;
                case DIRECTION_RIGHT:
                    if (x > locationThreshold){
                        isSwiping = true;
//                        bodyRecyclerView.smoothMoveWithOffsetX(1, animatorListener);
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

    public void notifyDataSetChanged(){
        dayViewBodyAdapter.notifyDataSetChanged();
        allDayView.notifyDataSetChanged();
    }

    public void setCalendarConfig(CalendarConfig calendarConfig) {
        this.allDayView.setCalendarConfig(calendarConfig);
        this.dayViewBodyAdapter.setCalendarConfig(calendarConfig);
        this.dayViewBodyAdapter.notifyDataSetChanged();
    }

//    public void isScrolling(){
//        bodyRecyclerView.isscro
//    }

    public interface OnViewBodyTimeSlotListener extends TimeSlotController.OnTimeSlotListener,DayViewAllDay.AllDayTimeslotListener {
    }

    public interface OnViewBodyEventListener extends EventController.OnEventListener, DayViewAllDay.AllDayEventListener {
    }
}
