package david.itimecalendar.calendar.ui.monthview;

import android.animation.LayoutTransition;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
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
import david.itimecalendar.calendar.ui.CalendarConfig;
import david.itimecalendar.calendar.ui.unitviews.RcdAllDayTimeslotView;
import david.itimecalendar.calendar.ui.weekview.TimeSlotView;
import david.itimecalendar.calendar.listeners.ITimeEventInterface;
import david.itimecalendar.calendar.listeners.ITimeEventPackageInterface;
import david.itimecalendar.calendar.listeners.ITimeTimeSlotInterface;
import david.itimecalendar.calendar.ui.unitviews.DraggableEventView;
import david.itimecalendar.calendar.ui.unitviews.DraggableTimeSlotView;
import david.itimecalendar.calendar.util.BaseUtil;
import david.itimecalendar.calendar.util.DensityUtil;
import david.itimecalendar.calendar.util.MyCalendar;
import david.itimecalendar.calendar.wrapper.WrapperEvent;
import david.itimecalendar.calendar.wrapper.WrapperTimeSlot;

/**
 * Created by yuhaoliu on 5/06/2017.
 */

public class DayViewAllDay extends FrameLayout {

    private CalendarConfig calendarConfig = new CalendarConfig();

    private ITimeRecycleViewGroup recycleViewGroup;
    private AllDayAdapter adapter;
    private TextView label;
    private int allDayEventHeight = 100;
    private int allDayTimeslotHeight = 0;
    private float leftBarWidth;
    private int NUM_CELL;
//    private boolean eventAsBg = false;
//    private boolean isTimeslotEnable = true;
//    private boolean isAlldayRcdTimeslotEnable = false;
    private TimeSlotView.TimeSlotPackage slotsInfo;

    int paddingLR = DensityUtil.dip2px(getContext(), 2);
    int paddingBT = DensityUtil.dip2px(getContext(), 2);

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
        this.setPadding(0,paddingBT,0,paddingBT);
        Context context = getContext();
        label = new TextView(context);
        label.setText("All day");
        label.setTextSize(11);
        label.setGravity(Gravity.CENTER);
        FrameLayout.LayoutParams labelParams = new LayoutParams((int)leftBarWidth, ViewGroup.LayoutParams.MATCH_PARENT);
        label.setLayoutParams(labelParams);
        this.addView(label);

        recycleViewGroup = new ITimeRecycleViewGroup(context, NUM_CELL);
        recycleViewGroup.setAllowScroll(false);
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

        private List<AllDayCell> cells = new ArrayList<>();

        @Override
        public AllDayCell onCreateViewHolder() {
            AllDayCell allDayCell = new AllDayCell(getContext());
            cells.add(allDayCell);
            return allDayCell;
        }

        @Override
        public void onBindViewHolder(AllDayCell item, int index) {
            item.reset();
            //should update visibility first, because currently setting item should not be considered.
            updateVisibility();
            updateLayout();

            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, index);
            MyCalendar calendar = new MyCalendar(cal);

            //for events
            item.setCalendar(calendar);
            item.setPackageInterface(eventPackage);

            //for timeslots
            //set timeslots
            if (calendarConfig.mode != CalendarConfig.Mode.EVENT && DayViewAllDay.this.slotsInfo != null){
                if (calendarConfig.isAllDayRcdEnable && !BaseUtil.isExpired(
                        cal.getTimeInMillis() + BaseUtil.getAllDayLong(cal.getTimeInMillis()))){
                    //add rcd button
                    WrapperTimeSlot wrapperTimeSlot = new WrapperTimeSlot(null);
                    item.addAllDayTimeslotButton(wrapperTimeSlot, calendar);
                }

                //add timeslot on top index
                List<WrapperTimeSlot> reals = slotsInfo.realSlots.get(calendar.getBeginOfDayMilliseconds());

                if (reals != null){
                    for (WrapperTimeSlot struct : reals
                            ) {
                        if (struct.getTimeSlot().isAllDay()){
                            item.addAllDayTimeslot(struct);
                        }
                    }
                }

            }
        }

        public void setPackageInterface(ITimeEventPackageInterface eventPackage) {
            this.eventPackage = eventPackage;
        }

        public List<AllDayCell> getAllCells(){
            return this.cells;
        }
    }

    private void updateVisibility(){
        List<AllDayCell> cells = adapter.getAllCells();
        for (AllDayCell cell:cells
                ) {
            if (calendarConfig.mode != CalendarConfig.Mode.EVENT && (calendarConfig.isAllDayRcdEnable || hasAllDayTimeslot())) {
                cell.getTimeslotLayout().setVisibility(VISIBLE);
            }else {
                cell.getTimeslotLayout().setVisibility(GONE);
            }
        }
    }

    private void updateLayout(){
        boolean hasAllDayEvent = hasAllDayEvent();
        ViewGroup.LayoutParams params = this.getLayoutParams();
        if (params == null){
            return;
        }

        int targetHeight = 0;

        //add timeslot height
        if (calendarConfig.mode != CalendarConfig.Mode.EVENT && calendarConfig.isAllDayRcdEnable){
            targetHeight += allDayTimeslotHeight;
        }

        //add event height
        if (hasAllDayEvent){
            targetHeight += allDayEventHeight;
        }

        //if has element to show, add height of container padding
        if (hasAllDayEvent || (calendarConfig.mode != CalendarConfig.Mode.EVENT && calendarConfig.isAllDayRcdEnable)){
            targetHeight += (paddingBT * 2);
        }

//        //if only timeslot add margin bottom
//        if (!hasAllDayEvent && isTimeslotEnable && isAlldayRcdTimeslotEnable){
//            targetHeight += DensityUtil.dip2px(getContext(),5);
//        }

        performLayoutChanged(targetHeight);
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

    private boolean hasAllDayTimeslot(){
        List<AllDayCell> items = adapter.getAllCompeletedItems();

        if (items == null){
            return false;
        }

        for (AllDayCell item:items
                ) {
            if (item.allDaySlots.size() > 0){
                return true;
            }
        }
        return false;
    }

    private class AllDayCell extends LinearLayout{
        private LinearLayout eventLayout;
        private FrameLayout timeslotLayout;

        MyCalendar calendar = new MyCalendar(Calendar.getInstance());
        List<ITimeEventInterface> allDayEvents = new ArrayList<>();
        List<ITimeTimeSlotInterface> allDaySlots = new ArrayList<>();
        List<ITimeTimeSlotInterface> rcdAllDaySlots = new ArrayList<>();

        public AllDayCell(Context context) {
            super(context);
            initViews();
        }

        private void initViews(){
            this.setLayoutTransition(new LayoutTransition());
            this.setOrientation(VERTICAL);
            Context context = getContext();

            timeslotLayout = new FrameLayout(context);
            timeslotLayout.setPadding(paddingLR,0,paddingLR,paddingBT);
            LinearLayout.LayoutParams timeslotParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,allDayTimeslotHeight);
            timeslotLayout.setLayoutParams(timeslotParams);
            this.addView(timeslotLayout);

            eventLayout = new LinearLayout(context);
            eventLayout.setOrientation(HORIZONTAL);
            LinearLayout.LayoutParams eventParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,allDayEventHeight);
            eventLayout.setLayoutParams(eventParams);
            this.addView(eventLayout);
        }

        private void addAllDayEvent(WrapperEvent wrapper) {
            int paddingLeft = DensityUtil.dip2px(getContext(),1);
            DraggableEventView new_dgEvent = new DraggableEventView(getContext(),wrapper.getEvent());
            new_dgEvent.setOnClickListener(new OnAllDayEventClick());
            LinearLayout.LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1f);
            new_dgEvent.setPadding(paddingLeft,0,0,0);
            new_dgEvent.setLayoutParams(params);
            this.eventLayout.addView(new_dgEvent);
            this.allDayEvents.add(wrapper.getEvent());

            //if bg mode
            if (calendarConfig.isAllDayEventAsBg){
                new_dgEvent.setToBg();
            }
        }

        private void addAllDayTimeslot(WrapperTimeSlot wrapperTimeSlot) {
            DraggableTimeSlotView new_timeslot = new DraggableTimeSlotView(getContext(),wrapperTimeSlot,true);
            new_timeslot.setOnClickListener(new OnAllDayTimeslotClick());
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            new_timeslot.setLayoutParams(params);
            this.timeslotLayout.addView(new_timeslot);
            this.allDaySlots.add(wrapperTimeSlot.getTimeSlot());
        }

        private void addAllDayTimeslotButton(WrapperTimeSlot wrapperTimeSlot, MyCalendar calendar) {
            RcdAllDayTimeslotView allDaySlotView = new RcdAllDayTimeslotView(getContext(),wrapperTimeSlot);
            allDaySlotView.setMyCalendar(calendar);
            allDaySlotView.setOnClickListener(new OnAllDayRcdTimeslotClick());
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            allDaySlotView.setLayoutParams(params);
            this.timeslotLayout.addView(allDaySlotView);
            this.rcdAllDaySlots.add(wrapperTimeSlot.getTimeSlot());
        }

        private void setPackageInterface(ITimeEventPackageInterface eventPackage){
            if (eventPackage == null){
                return;
            }

            boolean unconfirmedIncluded = calendarConfig.unconfirmedIncluded;

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

                        //check unconfirmed
                        if (!unconfirmedIncluded && !event.isConfirmed()){
                            continue;
                        }

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

                        //check unconfirmed
                        if (!unconfirmedIncluded && !event.isConfirmed()){
                            continue;
                        }

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

        private void setCalendar(MyCalendar calendar) {
            this.calendar = calendar;
        }

        public FrameLayout getTimeslotLayout() {
            return timeslotLayout;
        }

        public void setTimeslotLayout(FrameLayout timeslotLayout) {
            this.timeslotLayout = timeslotLayout;
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
    private int oldTargetValue = -1;
    private ValueAnimator layoutAnimator;

    private void performLayoutChanged(int targetHeight){
        if (targetHeight == oldTargetValue){
            return;
        }

        if (layoutAnimator != null && layoutAnimator.isRunning()){
            layoutAnimator.cancel();
        }
        oldTargetValue = targetHeight;

        layoutAnimator = ValueAnimator.ofInt(this.getHeight(), targetHeight);
        layoutAnimator.setDuration(animDuration);
        layoutAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                Integer value = (Integer) animation.getAnimatedValue();
                DayViewAllDay.this.getLayoutParams().height = value.intValue();
                DayViewAllDay.this.requestLayout();
            }
        });

        layoutAnimator.start();
    }

//    public void setAlldayRcdTimeslotEnable(boolean alldayTimeslotEnable) {
//        isAlldayRcdTimeslotEnable = alldayTimeslotEnable;
//        updateVisibility();
//        updateLayout();
//        adapter.notifyDataSetChanged();
//    }

    public TimeSlotView.TimeSlotPackage getSlotsInfo() {
        return slotsInfo;
    }

    public void setSlotsInfo(TimeSlotView.TimeSlotPackage slotsInfo) {
        this.slotsInfo = slotsInfo;
    }

    public void setCalendarConfig(CalendarConfig calendarConfig) {
        this.calendarConfig = calendarConfig;
        this.adapter.notifyDataSetChanged();
    }

    public void notifyDataSetChanged(){
        adapter.notifyDataSetChanged();
    }

    private AllDayEventListener allDayEventListener;
    private AllDayTimeslotListener allDayTimeslotListener;

    public AllDayEventListener getAllDayEventListener() {
        return allDayEventListener;
    }

    public void setAllDayEventListener(AllDayEventListener allDayEventListener) {
        this.allDayEventListener = allDayEventListener;
    }

    public AllDayTimeslotListener getAllDayTimeslotListener() {
        return allDayTimeslotListener;
    }

    public void setAllDayTimeslotListener(AllDayTimeslotListener allDayTimeslotListener) {
        this.allDayTimeslotListener = allDayTimeslotListener;
    }

    public interface AllDayTimeslotListener{
        void onAllDayRcdTimeslotClick(long dayBeginMilliseconds);
        void onAllDayTimeslotClick(DraggableTimeSlotView timeSlotView);
    }
    
    public interface AllDayEventListener{
        void onAllDayEventClick(ITimeEventInterface event);
    }

    private class OnAllDayEventClick implements OnClickListener {

        @Override
        public void onClick(View v) {
            DraggableEventView draggableEventView = (DraggableEventView) v;
            if (allDayEventListener != null){
                allDayEventListener.onAllDayEventClick(draggableEventView.getEvent());
            }
        }
    }

    private class OnAllDayTimeslotClick implements OnClickListener {

        @Override
        public void onClick(View v) {
            DraggableTimeSlotView draggableTimeSlotView = (DraggableTimeSlotView) v;
            if (allDayTimeslotListener != null){
                allDayTimeslotListener.onAllDayTimeslotClick(draggableTimeSlotView);
            }
        }
    }

    private class OnAllDayRcdTimeslotClick implements OnClickListener {

        @Override
        public void onClick(View v) {
            RcdAllDayTimeslotView allDaySlotView = (RcdAllDayTimeslotView) v;
            if (allDayTimeslotListener != null){
                allDayTimeslotListener.onAllDayRcdTimeslotClick(allDaySlotView.getAllDayBeginTime());
            }
        }
    }


}
