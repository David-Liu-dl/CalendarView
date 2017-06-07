package david.itimecalendar.calendar.calendar.mudules.monthview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.developer.paul.itimerecycleviewgroup.ITimeAdapter;
import com.developer.paul.itimerecycleviewgroup.ITimeRecycleViewGroup;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import david.itimecalendar.R;
import david.itimecalendar.calendar.calendar.mudules.weekview.TimeSlotView;
import david.itimecalendar.calendar.listeners.ITimeEventInterface;
import david.itimecalendar.calendar.listeners.ITimeEventPackageInterface;
import david.itimecalendar.calendar.listeners.ITimeTimeSlotInterface;
import david.itimecalendar.calendar.unitviews.DraggableEventView;
import david.itimecalendar.calendar.unitviews.DraggableTimeSlotView;
import david.itimecalendar.calendar.unitviews.RecommendedSlotView;
import david.itimecalendar.calendar.util.DensityUtil;
import david.itimecalendar.calendar.util.MyCalendar;
import david.itimecalendar.calendar.wrapper.WrapperEvent;
import david.itimecalendar.calendar.wrapper.WrapperTimeSlot;

/**
 * Created by yuhaoliu on 5/06/2017.
 */

public class DayViewAllDay extends FrameLayout {

    private static final String TAG = "DayViewAllDay";
    private ITimeRecycleViewGroup recycleViewGroup;
    private AllDayAdapter adapter;
    private TextView label;
    private int allDayEventHeight = 100;
    private int allDayTimeslotHeight = 0;
    private float leftBarWidth;
    private int NUM_CELL;
    private boolean isTimeSlotEnable = true;
    private TimeSlotView.TimeSlotPackage slotsInfo;

    public DayViewAllDay(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        loadAttributes(attrs, context);
        initViews();
    }

    private void loadAttributes(AttributeSet attrs, Context context) {
        if (attrs != null && context != null) {
            TypedArray typedArrayBody = context.getTheme().obtainStyledAttributes(attrs, R.styleable.viewBody, 0, 0);
            try {
                leftBarWidth = typedArrayBody.getDimension(R.styleable.viewBody_leftBarWidth, leftBarWidth);
                allDayEventHeight = (int) typedArrayBody.getDimension(R.styleable.viewBody_allDayEventHeight, allDayEventHeight);
                allDayTimeslotHeight = (int) typedArrayBody.getDimension(R.styleable.viewBody_allDayTimeslotHeight, allDayTimeslotHeight);
                NUM_CELL = typedArrayBody.getInteger(R.styleable.viewBody_cellNum, NUM_CELL);
            } finally {
                typedArrayBody.recycle();
            }
        }
    }

    private void initViews(){
        this.setBackgroundColor(Color.LTGRAY);

        Context context = getContext();
        label = new TextView(context);
        label.setText("All day");
        label.setGravity(Gravity.CENTER_VERTICAL);
        FrameLayout.LayoutParams labelParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        label.setLayoutParams(labelParams);
        this.addView(label);

        recycleViewGroup = new ITimeRecycleViewGroup(context, NUM_CELL);
        recycleViewGroup.setOnSetting(new ITimeRecycleViewGroup.OnSetting() {
            @Override
            public int getItemHeight(int heightSpec) {
                return allDayEventHeight + allDayTimeslotHeight;
            }
        });
        adapter = new AllDayAdapter();
        recycleViewGroup.setAdapter(adapter);
        LayoutParams recycleVGParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, allDayEventHeight + allDayTimeslotHeight);
        recycleViewGroup.setLayoutParams(recycleVGParams);
        recycleVGParams.leftMargin = (int)leftBarWidth;
        this.addView(recycleViewGroup);
    }

    private class AllDayAdapter extends ITimeAdapter<AllDayCell>{
        private ITimeEventPackageInterface eventPackage;

        @Override
        public AllDayCell onCreateViewHolder() {
            AllDayCell allDayCell = new AllDayCell(getContext());
            return allDayCell;
        }

        @Override
        public void onBindViewHolder(AllDayCell item, int index) {
            item.reset();
            //should update visibility first, because current setting item should not be considered.
            updateVisibility();

            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, index);
            MyCalendar calendar = new MyCalendar(cal);

            //for events
            item.setCalendar(calendar);
            item.setPackageInterface(eventPackage);

            //for timeslots
            //set timeslots
            if (isTimeSlotEnable && DayViewAllDay.this.slotsInfo != null){
                //add rcd first
                for (WrapperTimeSlot struct : slotsInfo.rcdSlots
                        ) {
                    if (struct.getTimeSlot().isAllDay() && calendar.contains(struct.getTimeSlot().getStartTime())){
                        item.addAllDayRcdTimeslot(struct);
                    }
                }
                //add timeslot on top index
                for (WrapperTimeSlot struct : slotsInfo.realSlots
                        ) {
                    if (struct.getTimeSlot().isAllDay() && calendar.contains(struct.getTimeSlot().getStartTime())){
                        item.addAllDayTimeslot(struct);
                    }
                }
            }

        }

        public void setPackageInterface(ITimeEventPackageInterface eventPackage) {
            this.eventPackage = eventPackage;
        }
    }

    private void updateVisibility(){
        boolean hasAllDayEvent = hasAllDayEvent();
        ViewGroup.LayoutParams params = this.getLayoutParams();
        if (params == null){
            return;
        }

        if (hasAllDayEvent){
            performExpand();
            return;
        }

        if (!hasAllDayEvent){
            performCollapse();
        }
    }

    private boolean hasAllDayEvent(){
        List<AllDayCell> items = adapter.getAllCompeletedItems();

        if (items == null){
            return false;
        }

        for (AllDayCell item:items
             ) {
            if (item.allDayEvents.size() > 0){
                return true;
            }
        }
        return false;
    }

    private class AllDayCell extends LinearLayout{
        int paddingLR = DensityUtil.dip2px(getContext(), 2);
        int paddingBT = DensityUtil.dip2px(getContext(), 2);

        private LinearLayout eventLayout;
        private FrameLayout timeslotLayout;

        MyCalendar calendar = new MyCalendar(Calendar.getInstance());
        List<ITimeEventInterface> allDayEvents = new ArrayList<>();
        List<ITimeTimeSlotInterface> allDaySlots = new ArrayList<>();

        public AllDayCell(Context context) {
            super(context);
            initViews();
        }

        private void initViews(){
            this.setPadding(0,paddingBT,0,paddingBT);
            this.setOrientation(VERTICAL);
            Context context = getContext();

            timeslotLayout = new FrameLayout(context);
            timeslotLayout.setPadding(paddingLR,0,paddingLR,0);
            LinearLayout.LayoutParams timeslotParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,allDayTimeslotHeight);
            timeslotLayout.setLayoutParams(timeslotParams);
            this.addView(timeslotLayout);

            eventLayout = new LinearLayout(context);
            eventLayout.setOrientation(HORIZONTAL);
            eventLayout.setPadding(paddingLR,paddingBT,paddingLR,0);
            LinearLayout.LayoutParams eventParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,allDayEventHeight);
            eventLayout.setLayoutParams(eventParams);
            this.addView(eventLayout);
        }

        private void addAllDayEvent(WrapperEvent wrapper) {
            DraggableEventView new_dgEvent = new DraggableEventView(getContext(),wrapper.getEvent(),true);
            new_dgEvent.setOnClickListener(new OnAllDayEventClick());
            LinearLayout.LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, allDayEventHeight,1f);
            new_dgEvent.setLayoutParams(params);
            this.eventLayout.addView(new_dgEvent);
            this.allDayEvents.add(wrapper.getEvent());
        }

        private void addAllDayTimeslot(WrapperTimeSlot wrapperTimeSlot) {
            DraggableTimeSlotView new_timeslot = new DraggableTimeSlotView(getContext(),wrapperTimeSlot,true);
            new_timeslot.setOnClickListener(new OnAllDayTimeslotClick());
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, allDayTimeslotHeight);
            new_timeslot.setLayoutParams(params);
            this.timeslotLayout.addView(new_timeslot);
            this.allDaySlots.add(wrapperTimeSlot.getTimeSlot());
        }

        private void addAllDayRcdTimeslot(WrapperTimeSlot wrapperTimeSlot) {
            RecommendedSlotView recommendedSlotView = new RecommendedSlotView(getContext(),wrapperTimeSlot,true);
            recommendedSlotView.setOnClickListener(new OnAllDayRcdTimeslotClick());
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, allDayTimeslotHeight);
            recommendedSlotView.setLayoutParams(params);
            this.timeslotLayout.addView(recommendedSlotView);
            this.allDaySlots.add(wrapperTimeSlot.getTimeSlot());
        }

        private void setPackageInterface(ITimeEventPackageInterface eventPackage){
            if (eventPackage == null){
                return;
            }

            Map<Long, List<ITimeEventInterface>> regularDayEventMap = eventPackage.getRegularEventDayMap();
            Map<Long, List<ITimeEventInterface>> repeatedDayEventMap = eventPackage.getRepeatedEventDayMap();

            long startTime = calendar.getBeginOfDayMilliseconds();

            if (regularDayEventMap != null && regularDayEventMap.containsKey(startTime)){
                List<ITimeEventInterface> currentDayEvents = regularDayEventMap.get(startTime);
                for (ITimeEventInterface event : currentDayEvents) {
                    // is shown in calendar
                    if (event.isShownInCalendar() == View.VISIBLE){
                        WrapperEvent wrapperEvent = new WrapperEvent(event);
                        wrapperEvent.setFromDayBegin(startTime);
                        if (event.isAllDay()){
                            this.addAllDayEvent(wrapperEvent);
                        }
                    }
                }
            }

            if (repeatedDayEventMap != null && repeatedDayEventMap.containsKey(startTime)){
                List<ITimeEventInterface> currentDayEvents = repeatedDayEventMap.get(startTime);
                for (ITimeEventInterface event : currentDayEvents) {
                    if (event.isShownInCalendar() == View.VISIBLE){
                        WrapperEvent wrapperEvent = new WrapperEvent(event);
                        wrapperEvent.setFromDayBegin(startTime);
                        if (event.isAllDay()){
                            this.addAllDayEvent(wrapperEvent);
                        }
                    }
                }
            }
        }

        private void reset(){
            //clear all views
            this.eventLayout.removeAllViews();
            this.timeslotLayout.removeAllViews();
            this.allDayEvents.clear();
            this.allDaySlots.clear();
        }

        public void setCalendar(MyCalendar calendar) {
            this.calendar = calendar;
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    public void setEventPackage(ITimeEventPackageInterface eventPackage) {
        this.adapter.setPackageInterface(eventPackage);
        this.adapter.notifyDataSetChanged();
    }

    public ITimeRecycleViewGroup getRecycleViewGroup(){
        return this.recycleViewGroup;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private long animDuration = 300;
    private ValueAnimator showAnim;
    private ValueAnimator hideAnim;

    private void performCollapse(){
        if (hideAnim != null && hideAnim.isRunning()){
            return;
        }

        if (showAnim != null && showAnim.isRunning()){
            showAnim.cancel();
        }

        hideAnim = ValueAnimator.ofInt(this.getHeight(), 0);
        hideAnim.setDuration(animDuration);
        hideAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                Integer value = (Integer) animation.getAnimatedValue();
                DayViewAllDay.this.getLayoutParams().height = value.intValue();
                DayViewAllDay.this.requestLayout();
            }
        });

        hideAnim.start();
    }

    private void performExpand(){
        if (showAnim != null && showAnim.isRunning()){
            return;
        }

        if (hideAnim != null && hideAnim.isRunning()){
            hideAnim.cancel();
        }

        showAnim = ValueAnimator.ofInt(this.getHeight(), allDayEventHeight);
        showAnim.setDuration(animDuration);
        showAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                Integer value = (Integer) animation.getAnimatedValue();
                DayViewAllDay.this.getLayoutParams().height = value.intValue();
                DayViewAllDay.this.requestLayout();
            }
        });

        showAnim.start();
    }

    public boolean isTimeSlotEnable() {
        return isTimeSlotEnable;
    }

    public void setTimeSlotEnable(boolean timeSlotEnable) {
        isTimeSlotEnable = timeSlotEnable;
    }

    public TimeSlotView.TimeSlotPackage getSlotsInfo() {
        return slotsInfo;
    }

    public void setSlotsInfo(TimeSlotView.TimeSlotPackage slotsInfo) {
        this.slotsInfo = slotsInfo;
    }

    public void notifyDataSetChanged(){
        adapter.notifyDataSetChanged();
    }

    private AllDayListener allDayListener;

    public AllDayListener getAllDayListener() {
        return allDayListener;
    }

    public void setAllDayListener(AllDayListener allDayListener) {
        this.allDayListener = allDayListener;
    }

    public interface AllDayListener{
        void onAllDayEventClick(ITimeEventInterface event);
        void onAllDayRcdTimeslotClick(RecommendedSlotView rcdView);
        void onAllDayTimeslotClick(DraggableTimeSlotView timeSlotView);
    }

    private class OnAllDayEventClick implements OnClickListener {

        @Override
        public void onClick(View v) {
            DraggableEventView draggableEventView = (DraggableEventView) v;
            if (allDayListener != null){
                allDayListener.onAllDayEventClick(draggableEventView.getEvent());
            }
        }
    }

    private class OnAllDayTimeslotClick implements OnClickListener {

        @Override
        public void onClick(View v) {
            DraggableTimeSlotView draggableTimeSlotView = (DraggableTimeSlotView) v;
            if (allDayListener != null){
                allDayListener.onAllDayTimeslotClick(draggableTimeSlotView);
            }
        }
    }

    private class OnAllDayRcdTimeslotClick implements OnClickListener {

        @Override
        public void onClick(View v) {
            RecommendedSlotView recommendedSlotView = (RecommendedSlotView) v;
            if (allDayListener != null){
                allDayListener.onAllDayRcdTimeslotClick(recommendedSlotView);
            }
        }
    }
}
