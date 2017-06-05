package david.itimecalendar.calendar.calendar.mudules.monthview;

import android.animation.Animator;
import android.animation.LayoutTransition;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Rect;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daasuu.bl.ArrowDirection;
import com.daasuu.bl.BubbleLayout;
import com.developer.paul.itimerecycleviewgroup.ITimeRecycleViewGroup;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import david.itimecalendar.R;
import david.itimecalendar.calendar.calendar.mudules.weekview.TimeSlotView;
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
    private ITimeRecycleViewGroup bodyRecyclerView;
    private BubbleLayout bubble;

    private BodyAdapter bodyPagerAdapter;
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
                allDayEventHeight = (int)typedArray.getDimension(R.styleable.viewBody_allDayHeight, allDayEventHeight);
            } finally {
                typedArray.recycle();
            }
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (bubble.getVisibility() == VISIBLE){
            if (ev.getAction() == MotionEvent.ACTION_DOWN){
                Rect viewRect = new Rect();
                bubble.getGlobalVisibleRect(viewRect);
                if (viewRect.contains((int) ev.getRawX(), (int) ev.getRawY())) {
                    //when click on tooltips and it's visible
                    // pass it to tooltips
//                    return false;
                }else {
                    //when click outside of tooltips and it's visible
                    bubble.setVisibility(GONE);
//                    return true;
                }
            }else {
                bubble.setVisibility(GONE);
//                return false;
            }
        }

        return super.onInterceptTouchEvent(ev);
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
        allDayView.setId(generateViewId());
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, allDayEventHeight);
        this.addView(allDayView,params);
    }

    private void setUpBody(){
        setUpLeftTimeBar();
        setUpCalendarBody();
        allDayView.bringToFront();
        initBubbleView();
    }

//    public void setScrollInterface(ITimeRecycleViewGroup.ScrollInterface scrollInterface){
//        bodyRecyclerView.setScrollInterface(scrollInterface);
//    }

    private void setUpCalendarBody(){
        bodyPagerAdapter = new BodyAdapter(getContext(), this.attrs);
        bodyPagerAdapter.setSlotsInfo(this.timeSlotPackage);
        bodyRecyclerView = new ITimeRecycleViewGroup(context, NUM_LAYOUTS);
        bodyRecyclerView.setAdapter(bodyPagerAdapter);
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
                synViewsVerticalPosition(dy, leftTimeBarLayout);
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
        if (this.bodyPagerAdapter != null){
            List<View> items = bodyPagerAdapter.getViewItems();
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
                    }
                });
            }
        }
    }

    private void setUpLeftTimeBar(){
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

        RelativeLayout.LayoutParams leftTimeBarParams = new RelativeLayout.LayoutParams(leftBarWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
        leftTimeBarParams.addRule(BELOW, allDayView.getId());
        leftTimeBarLayout.setLayoutParams(leftTimeBarParams);
        this.addView(leftTimeBarLayout);
    }

    private void synViewsVerticalPosition(float dy, View targetView){
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) targetView.getLayoutParams();
        if (params!=null){
            params.topMargin += dy;
            targetView.setLayoutParams(params);
        }
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

    private void initBubbleView(){
        bubble = new BubbleLayout(getContext());
        int bubbleWidth = DensityUtil.dip2px(getContext(),110);
        int bubbleHeight = DensityUtil.dip2px(getContext(),35);
        bubble.setArrowDirection(ArrowDirection.BOTTOM);
        bubble.setCornersRadius(DensityUtil.dip2px(getContext(),10));
        bubble.setBubbleColor(getResources().getColor(R.color.timeslot_bubble_bg));
        bubble.setArrowHeight(20);
        bubble.setArrowWidth(20);
        bubble.setStrokeWidth(0);
        bubble.setVisibility(GONE);
        bubble.setArrowPosition(bubbleWidth/2 - bubble.getArrowWidth()/2);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(bubbleWidth, bubbleHeight);
        this.addView(bubble,params);

        LinearLayout bubbleMenuContainer = new LinearLayout(getContext());
        FrameLayout.LayoutParams bubbleParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        bubble.addView(bubbleMenuContainer,bubbleParams);

        TextView editBtn = new TextView(getContext());
        editBtn.setText("Edit");
        editBtn.setTextColor(Color.WHITE);
        editBtn.setTextSize(12);
        editBtn.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams editBtnParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
        editBtnParams.weight = 10;
        editBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Object tag = bubble.getTag();
                if (tag != null){
                    DraggableTimeSlotView slotView = (DraggableTimeSlotView) tag;
                    bubble.setVisibility(GONE);
                    bubble.setTag(null);
                    onTimeSlotInnerListener.onTimeSlotEdit(slotView);
                }
            }
        });
        bubbleMenuContainer.addView(editBtn,editBtnParams);

        ImageView slash = new ImageView(getContext());
        slash.setImageDrawable(getResources().getDrawable(R.drawable.icon_slash));
        slash.setPadding(0,20,0,20);
        LinearLayout.LayoutParams slashParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        bubbleMenuContainer.addView(slash,slashParams);

        TextView deleteBtn = new TextView(getContext());
        deleteBtn.setText("Delete");
        deleteBtn.setTextSize(12);
        deleteBtn.setTextColor(Color.WHITE);
        deleteBtn.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams dltBtnParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
        dltBtnParams.weight = 10;
        deleteBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Object tag = bubble.getTag();
                if (tag != null){
                    DraggableTimeSlotView slotView = (DraggableTimeSlotView) tag;
                    bubble.setVisibility(GONE);
                    bubble.setTag(null);
                    onTimeSlotInnerListener.onTimeSlotDelete(slotView);
                }
            }
        });
        bubbleMenuContainer.addView(deleteBtn,dltBtnParams);
    }

    private void showTimeSlotTools(DraggableTimeSlotView slotView){
        int slotRect[] = {0, 0};
        int bodyRect[] = {0, 0};
        slotView.getLocationOnScreen(slotRect);
        this.getLocationOnScreen(bodyRect);
        float posX = slotRect[0] - bodyRect[0];
        float posY = slotRect[1] - bodyRect[1];
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) bubble.getLayoutParams();
        int topMargin = (int)(posY - params.height);

        params.topMargin = topMargin>0?topMargin:0;
        params.leftMargin = (int)posX;

        bubble.setVisibility(View.VISIBLE);
        bubble.requestLayout();
    }

    /************************************************************************************/
    public void setEventPackage(ITimeEventPackageInterface eventPackage){
        this.bodyPagerAdapter.setEventPackage(eventPackage);
        this.allDayView.setEventPackage(eventPackage);
    }

    private EventController.OnEventListener onEventListener;
    public void setOnBodyListener(EventController.OnEventListener onEventListener) {
        this.onEventListener = onEventListener;
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
        bodyRecyclerView.moveWithOffsetX(offset);
    }

    public void refresh(){
        bodyPagerAdapter.notifyDataSetChanged();
    }

    private TimeSlotView.TimeSlotPackage timeSlotPackage = new TimeSlotView.TimeSlotPackage();

    public TimeSlotView.TimeSlotPackage getTimeSlotPackage(){
        return timeSlotPackage;
    }

    public void enableTimeSlot(){
        if (bodyPagerAdapter != null){
            List<View> items = bodyPagerAdapter.getViewItems();
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
        if (bodyPagerAdapter != null){
            bodyPagerAdapter.notifyDataSetChanged();
        }
    }

    public void addTimeSlot(WrapperTimeSlot wrapperTimeSlot){
        addSlotToPackage(wrapperTimeSlot);
        if (bodyPagerAdapter != null){
            bodyPagerAdapter.notifyDataSetChanged();
        }
    }

    public void addTimeSlot(ITimeTimeSlotInterface slotInfo, boolean isSelected){
        WrapperTimeSlot wrapper = new WrapperTimeSlot(slotInfo);
        wrapper.setSelected(isSelected);
        addSlotToPackage(wrapper);
        if (bodyPagerAdapter != null){
            bodyPagerAdapter.notifyDataSetChanged();
        }
    }

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
        if (bodyPagerAdapter != null){
            List<View> items = bodyPagerAdapter.getViewItems();
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
        if (bodyPagerAdapter != null){
            List<View> items = bodyPagerAdapter.getViewItems();
            for (View view:items
                    ) {
                DayViewBodyCell cell = (DayViewBodyCell) view;
                cell.setOnTimeSlotListener(onTimeSlotInnerListener);
            }
        }
    }

    public void setOnTimeSlotListener(TimeSlotController.OnTimeSlotListener onTimeSlotOuterListener) {
        this.initOnTimeSlotListener();
        this.onTimeSlotOuterListener = onTimeSlotOuterListener;
    }

    private class OnTimeSlotInnerListener implements TimeSlotController.OnTimeSlotListener{
        @Override
        public void onTimeSlotCreate(DraggableTimeSlotView draggableTimeSlotView) {

            if (onTimeSlotOuterListener != null){
                onTimeSlotOuterListener.onTimeSlotCreate(draggableTimeSlotView);
            }
//            bodyPagerAdapter.notifyDataSetChanged();
        }

        @Override
        public void onTimeSlotClick(DraggableTimeSlotView draggableTimeSlotView) {
            bubble.setTag(draggableTimeSlotView);
            showTimeSlotTools(draggableTimeSlotView);

            if (onTimeSlotOuterListener != null){
                onTimeSlotOuterListener.onTimeSlotClick(draggableTimeSlotView);
            }

            bodyPagerAdapter.notifyDataSetChanged();
        }

        @Override
        public void onRcdTimeSlotClick(RecommendedSlotView v) {
            if (onTimeSlotOuterListener != null){
                onTimeSlotOuterListener.onRcdTimeSlotClick(v);
            }

            bodyPagerAdapter.notifyDataSetChanged();
        }

        @Override
        public void onTimeSlotDragStart(DraggableTimeSlotView draggableTimeSlotView) {
            if (onTimeSlotOuterListener != null){
                onTimeSlotOuterListener.onTimeSlotDragStart(draggableTimeSlotView);
            }
            bodyPagerAdapter.notifyDataSetChanged();
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

            if (onTimeSlotOuterListener != null){
                onTimeSlotOuterListener.onTimeSlotDragDrop(draggableTimeSlotView, draggableTimeSlotView.getNewStartTime(), draggableTimeSlotView.getNewEndTime());
            }
            bodyPagerAdapter.notifyDataSetChanged();
        }

        @Override
        public void onTimeSlotEdit(DraggableTimeSlotView draggableTimeSlotView) {
            if (onTimeSlotOuterListener != null){
                onTimeSlotOuterListener.onTimeSlotEdit(draggableTimeSlotView);
            }

            bodyPagerAdapter.notifyDataSetChanged();
        }

        @Override
        public void onTimeSlotDelete(DraggableTimeSlotView draggableTimeSlotView) {
            if (onTimeSlotOuterListener != null){
                onTimeSlotOuterListener.onTimeSlotDelete(draggableTimeSlotView);
            }

            bodyPagerAdapter.notifyDataSetChanged();
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
}
