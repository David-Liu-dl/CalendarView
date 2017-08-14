package david.itimecalendar.calendar.ui.monthview;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.ClipData;
import android.content.Context;
import android.graphics.Point;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import david.itimecalendar.calendar.listeners.ITimeEventInterface;
import david.itimecalendar.calendar.listeners.ITimeEventPackageInterface;
import david.itimecalendar.calendar.listeners.ITimeInviteeInterface;
import david.itimecalendar.calendar.ui.unitviews.DraggableEventView;
import david.itimecalendar.calendar.util.BaseUtil;
import david.itimecalendar.calendar.util.OverlapHelper;
import david.itimecalendar.calendar.util.DensityUtil;
import david.itimecalendar.calendar.util.MyCalendar;
import david.itimecalendar.calendar.wrapper.WrapperEvent;

import static david.itimecalendar.calendar.ui.monthview.DayViewBodyCell.DAY_CROSS_ALL_DAY;
import static david.itimecalendar.calendar.ui.monthview.DayViewBodyCell.DAY_CROSS_BEGIN;
import static david.itimecalendar.calendar.ui.monthview.DayViewBodyCell.DAY_CROSS_END;
import static david.itimecalendar.calendar.ui.monthview.DayViewBodyCell.REGULAR;
import static david.itimecalendar.calendar.ui.monthview.DayViewBodyCell.UNDEFINED;

/**
 * Created by yuhaoliu on 9/01/2017.
 */

public class EventController {
    private static final String TAG = "EventController";

    private DayViewBodyCell container;
    private Context context;
    private OnEventListener onEventListener;

    private Map<WrapperEvent, Integer> regularEventViewMap = new HashMap<>();
    private Map<ITimeEventInterface, DraggableEventView> uidDragViewMap = new HashMap<>();
    private ArrayList<DraggableEventView> allDayDgEventViews = new ArrayList<>();

    private long defaultEventDuration = 3600 * 1000;
    private boolean isTimeSlot = false;

    EventController(DayViewBodyCell container) {
        this.container = container;
        this.context = container.getContext();
    }

    void setEventList(ITimeEventPackageInterface eventPackage) {
        boolean unconfirmedIncluded = container.calendarConfig.unconfirmedIncluded;

        this.clearEvents();
        Map<Long, List<ITimeEventInterface>> regularDayEventMap = eventPackage.getRegularEventDayMap();
        Map<Long, List<ITimeEventInterface>> repeatedDayEventMap = eventPackage.getRepeatedEventDayMap();

            long startTime = container.getCalendar().getBeginOfDayMilliseconds();

            if (regularDayEventMap != null && regularDayEventMap.containsKey(startTime)){
                List<ITimeEventInterface> currentDayEvents = regularDayEventMap.get(startTime);
                for (ITimeEventInterface event : currentDayEvents) {
                    //check unconfirmed
                    if (!unconfirmedIncluded && !event.isConfirmed()){
                        continue;
                    }
                    // is shown in calendar
                    if (event.isShownInCalendar() == View.VISIBLE){
                        WrapperEvent wrapperEvent = new WrapperEvent(event);
                        wrapperEvent.setFromDayBegin(startTime);
                        if (!event.isAllDay()){
                            this.addRegularEvent(wrapperEvent);
                        }
                    }
                }
            }

            if (repeatedDayEventMap != null && repeatedDayEventMap.containsKey(startTime)){
                List<ITimeEventInterface> currentDayEvents = repeatedDayEventMap.get(startTime);
                for (ITimeEventInterface event : currentDayEvents) {
                    //check unconfirmed
                    if (!unconfirmedIncluded && !event.isConfirmed()){
                        continue;
                    }

                    if (event.isShownInCalendar() == View.VISIBLE){
                        WrapperEvent wrapperEvent = new WrapperEvent(event);
                        wrapperEvent.setFromDayBegin(startTime);
                        if (!event.isAllDay()){
                            this.addRegularEvent(wrapperEvent);
                        }
                    }
                }
            }

        calculateEventLayout(this.container.eventLayout);

        this.container.eventLayout.requestLayout(); // paul try
    }

    private void addRegularEvent(WrapperEvent wrapper) {
        final DayInnerBodyLayout eventLayout = container.eventLayout;
        final DraggableEventView newDragEventView = this.createDayDraggableEventView(wrapper, false);
        final DayInnerBodyLayout.LayoutParams params = (DayInnerBodyLayout.LayoutParams) newDragEventView.getLayoutParams();
        newDragEventView.setId(View.generateViewId());
        this.regularEventViewMap.put(wrapper, newDragEventView.getId());

        eventLayout.addView(newDragEventView, params);
        eventLayout.getEvents().add(wrapper);
        eventLayout.getDgEvents().add(newDragEventView);

        //if bg mode
        if (isTimeSlot){
            newDragEventView.setToBg();
        }
    }

    private boolean isWithin(ITimeEventInterface event, int index){
        long startTime = event.getStartTime();
        long endTime = event.getEndTime();

        MyCalendar calS = new MyCalendar(container.getCalendar());
        calS.setOffsetByDate(index);

        MyCalendar calE = new MyCalendar(container.getCalendar());
        calE.setOffsetByDate(index);
        calE.setHour(23);
        calE.setMinute(59);

        long todayStartTime =  calS.getBeginOfDayMilliseconds();
        long todayEndTime =  calE.getCalendar().getTimeInMillis();

        return
                todayEndTime >= startTime && todayStartTime <= endTime;
    }

    void clearEvents() {
        if (container.eventLayout != null) {
            container.eventLayout.clearViews();
        }

        this.regularEventViewMap.clear();
        this.allDayDgEventViews.clear();
        this.uidDragViewMap.clear();
    }

    private DraggableEventView createDayDraggableEventView(WrapperEvent wrapper, boolean isAllDayEvent) {
        ITimeEventInterface event = wrapper.getEvent();
        DraggableEventView event_view = new DraggableEventView(context, event, isAllDayEvent);
        event_view.setCalendar(container.getCalendar());
        event_view.setViewType(DraggableEventView.TYPE_NORMAL);
        int padding = DensityUtil.dip2px(context,1);
        event_view.setPadding(0,padding,0,padding);
        if (!container.isTimeSlotEnable){
            event_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onEventListener != null) {
                        onEventListener.onEventClick((DraggableEventView) view);
                    }
                }
            });
        }

        if (isAllDayEvent) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT,1f);
            event_view.setTag(event);
            event_view.setLayoutParams(params);
        } else {
            long duration = event.getEndTime() - event.getStartTime();
            int eventHeight =(int) (duration * container.heightPerMillisd);
            int height = getDayCrossHeight(wrapper);
//            DraggableEventView.LayoutParams params = new DraggableEventView.LayoutParams(eventHeight, height);
            DayInnerBodyLayout.LayoutParams params = new DayInnerBodyLayout.LayoutParams(eventHeight, height);
            params.relativeMarginLeft = container.unitViewLeftMargin;
            params.relativeMarginRight = container.unitViewRightMargin;

            if (!container.isTimeSlotEnable && getRegularEventType(wrapper) != DAY_CROSS_ALL_DAY){
                event_view.setOnLongClickListener(new EventLongClickListener());
            }
            event_view.setTag(event);
            event_view.setLayoutParams(params);
        }

        //add it to map
        uidDragViewMap.put(event, event_view);

        return event_view;
    }

    private int getDayCrossHeight(WrapperEvent wrapper){

        long allDayMilliseconds = BaseUtil.getAllDayLong(wrapper.getFromDayBegin());
        int type = getRegularEventType(wrapper);
        ITimeEventInterface event = wrapper.getEvent();

        int height;
        long duration;

        switch (type){
            case REGULAR:
                duration = event.getEndTime() - event.getStartTime();
                height =(int) (duration * container.heightPerMillisd);
                break;
            case DAY_CROSS_BEGIN:
                duration = (wrapper.getFromDayBegin() + allDayMilliseconds) - event.getStartTime();
                height =(int) (duration * container.heightPerMillisd);
                break;
            case DAY_CROSS_ALL_DAY:
                duration = allDayMilliseconds;
                height =(int) (duration * container.heightPerMillisd);
                break;
            case DAY_CROSS_END:
                duration = event.getEndTime() - wrapper.getFromDayBegin();
                height =(int) (duration * container.heightPerMillisd);
                break;
            default:
                duration = event.getEndTime() - event.getStartTime();
                height =(int) (duration * container.heightPerMillisd);
                break;
        }
        return height;
    }


    private DraggableEventView createTempDayDraggableEventView(float tapX, float tapY) {
        ITimeEventInterface event = this.initializeEvent();
        if (event == null) {
            throw new RuntimeException("need Class name in 'setEventClassName()'");
        }

        DraggableEventView event_view = new DraggableEventView(context, event, false);
        event_view.setDuration(defaultEventDuration);
        event_view.setViewType(DraggableEventView.TYPE_TEMP);
        int padding = DensityUtil.dip2px(context,1);
        event_view.setPadding(0,padding,0,0);

        int eventHeight = 1 * container.hourHeight;//one hour
        DayInnerBodyLayout.LayoutParams params = new DayInnerBodyLayout.LayoutParams(200, eventHeight);
        event_view.setY(tapY - eventHeight / 2);
        event_view.setOnLongClickListener(new EventLongClickListener());
        event_view.setLayoutParams(params);

        return event_view;
    }

    /**
     * calculate the position of item
     * it needs to be called when setting item or item position changed
     */
    private void calculateEventLayout(DayInnerBodyLayout eventLayout) {
        List<ArrayList<OverlapHelper.OverlappedEvent>> overlapGroups
                = container.xHelper.computeOverlapXObject(eventLayout.getEvents());
        for (ArrayList<OverlapHelper.OverlappedEvent> overlapGroup : overlapGroups
                ) {
            for (int i = 0; i < overlapGroup.size(); i++) {
                int startY = getEventY((WrapperEvent) overlapGroup.get(i).item);
                int overlapCount = overlapGroup.get(i).params.overlapCount;
                int indexInRow = overlapGroup.get(i).params.indexInRow;
                DraggableEventView eventView = (DraggableEventView) eventLayout.findViewById(regularEventViewMap.get(overlapGroup.get(i).item));
                eventView.setPosParam(new DraggableEventView.PosParam(startY, indexInRow, overlapCount, startY));
            }
        }
    }

    private int getEventY(WrapperEvent wrapper) {
        ITimeEventInterface event = wrapper.getEvent();
        int type = getRegularEventType(wrapper);

        int offset = 0;

        Date date;

        switch (type){
            case REGULAR:
                date = new Date(event.getStartTime());
                break;
            case DAY_CROSS_BEGIN:
                date = new Date(event.getStartTime());
                break;
            case DAY_CROSS_ALL_DAY:
                date = new Date(wrapper.getFromDayBegin());
                break;
            case DAY_CROSS_END:
                date = new Date(wrapper.getFromDayBegin());
                break;
            default:
                date = new Date(event.getStartTime());
                break;
        }

        String hourWithMinutes = container.sdf.format(date);
        String[] components = hourWithMinutes.split(":");
        float trickTime = Integer.valueOf(components[0]) + Integer.valueOf(components[1]) / (float) 100;
        int getStartY = container.nearestTimeSlotValue(trickTime) + offset;

        return getStartY;
    }

    /****************************************************************************************/

    private class EventLongClickListener implements View.OnLongClickListener {
        @Override
        public boolean onLongClick(final View view) {

            if (container.tempDragView != null || onEventListener !=null && onEventListener.isDraggable((DraggableEventView) view)){
                ClipData data = ClipData.newPlainText("", "");
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(
                        view){
                    @Override
                    public void onProvideShadowMetrics(Point outShadowSize, Point outShadowTouchPoint) {
                        final View view = getView();
                        if (view != null) {
                            outShadowSize.set(view.getWidth(), view.getHeight());
                            outShadowTouchPoint.set(outShadowSize.x / 2, 0);
                        } else {
//                            Log.e(View.VIEW_LOG_TAG, "Asked for drag thumb metrics but no view");
                        }
                    }
                };

                if (container.tempDragView != null) {
                    view.setVisibility(View.INVISIBLE);
                } else {
                    view.setVisibility(View.VISIBLE);
                }
                view.startDrag(data, shadowBuilder, view, 0);
            }

            return true;
        }
    }

    class EventDragListener implements View.OnDragListener {
        int currentEventNewHour = -1;
        int currentEventNewMinutes = -1;

        @Override
        public boolean onDrag(View v, DragEvent event) {
            DraggableEventView dgView = (DraggableEventView) event.getLocalState();
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    break;
                case DragEvent.ACTION_DRAG_LOCATION:
                    int rawX = (int) event.getX();
                    int rawY = (int) event.getY();

                    if (onEventListener != null) {
                        int nearestProperPosition = container.nearestQuarterTimeSlotKey(rawY);
                        String locationTime = (container.positionToTimeQuarterTreeMap.get(nearestProperPosition));
                        onEventListener.onEventDragging(dgView, container.getCalendar(), rawX, (int) event.getY(), locationTime);
                    } else {
                        Log.i(TAG, "onDrag: null onEventDragListener");
                    }
                    break;
                case DragEvent.ACTION_DRAG_ENTERED:
//                    container.msgWindow.setVisibility(View.VISIBLE);
                    if (dgView.getViewType() == DraggableEventView.TYPE_TEMP){
                        container.tempDragView = dgView;
                    }else{
                        container.tempDragView= null;
                    }
                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                    container.tempDragView = null;
                    break;
                case DragEvent.ACTION_DROP:
                    //handler ended things in here, because ended some time is not triggered
                    View finalView = (View) event.getLocalState();
                    finalView.setVisibility(View.VISIBLE);
//                    container.msgWindow.setVisibility(View.INVISIBLE);

                    float actionStopX = event.getX();
                    float actionStopY = event.getY();
                    // Dropped, reassign View to ViewGroup
                    int newX = (int) actionStopX - dgView.getWidth() / 2;
                    int newY = (int) actionStopY;
                    int[] reComputeResult = container.reComputePositionToSet(newX, newY, dgView, v);

                    //update the item time
                    String new_time = container.positionToTimeQuarterTreeMap.get(reComputeResult[1]);
                    //important! update item time after drag
                    String[] time_parts = new_time.split(":");
                    currentEventNewHour = Integer.valueOf(time_parts[0]);
                    currentEventNewMinutes = Integer.valueOf(time_parts[1]);

                    dgView.setCalendar(container.getCalendar());
                    dgView.getCalendar().setHour(currentEventNewHour);
                    dgView.getCalendar().setMinute(currentEventNewMinutes);
                    //set dropped container index

                    if (container.tempDragView == null && onEventListener != null) {
                        onEventListener.onEventDragDrop(dgView);
                    } else {
                        Log.i(TAG, "onDrop Not Called");
                    }

                    if (dgView.getViewType() == DraggableEventView.TYPE_TEMP) {
                        ViewGroup parent = (ViewGroup) dgView.getParent();
                        if(parent != null){
                            parent.removeView(dgView);
                        }
                        //important! update item time after drag via listener
                        if (onEventListener != null) {
                            onEventListener.onEventCreate(dgView);
                        }
                        //finally reset tempDragView to NULL.
                        container.tempDragView = null;
                    }
                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                    if (onEventListener != null) {
                        onEventListener.onEventDragEnd(dgView);
                    }
                    break;
            }

            return true;
        }
    }

    class CreateEventListener implements View.OnLongClickListener {

        @Override
        public boolean onLongClick(View v) {
            DayInnerBodyLayout container = (DayInnerBodyLayout) v;
            EventController.this.container.tempDragView = createTempDayDraggableEventView(EventController.this.container.nowTapX, EventController.this.container.nowTapY);
            container.addView(EventController.this.container.tempDragView);

            ObjectAnimator scaleX = ObjectAnimator.ofFloat(EventController.this.container.tempDragView, "scaleX", 0f,1f);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(EventController.this.container.tempDragView, "scaleY", 0f,1f);
            ObjectAnimator alpha = ObjectAnimator.ofFloat(EventController.this.container.tempDragView, "alpha", 0f,1f);
            alpha.setDuration(180);
            scaleX.setDuration(120);
            scaleY.setDuration(120);

            AnimatorSet createAnim = new AnimatorSet();
            createAnim.play(alpha).with(scaleY).with(scaleX);
            scaleX.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    View p= (View) EventController.this.container.tempDragView.getParent();
                    if (p != null){
                        p.invalidate();
                    }
                }
            });
            createAnim.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    Log.i(TAG, "onAnimationStart: ");
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    if (EventController.this.container.tempDragView != null
                            && EventController.this.container.tempDragView.getParent() != null){
                        EventController.this.container.tempDragView.performLongClick();
                    }
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });

            createAnim.start();

            return true;
        }
    }

    private ITimeEventInterface initializeEvent() {
        try {
            ITimeEventInterface t = new ITimeEventInterface() {
                @Override
                public String getEventUid() {
                    return null;
                }

                @Override
                public void setSummary(String summary) {

                }

                @Override
                public String getSummary() {
                    return null;
                }

                @Override
                public void setStartTime(long startTime) {

                }

                @Override
                public long getStartTime() {
                    return 0;
                }

                @Override
                public void setEndTime(long endTime) {

                }

                @Override
                public long getEndTime() {
                    return 0;
                }

                @Override
                public List<? extends ITimeInviteeInterface> getDisplayInvitee() {
                    return null;
                }

                @Override
                public void setHighLighted(boolean highlighted) {

                }

                @Override
                public boolean isHighlighted() {
                    return false;
                }

                @Override
                public void setAllDay(boolean allday) {

                }

                @Override
                public boolean isAllDay() {
                    return false;
                }

                @Override
                public int isShownInCalendar() {
                    return 0;
                }

                @Override
                public String getLocationName() {
                    return null;
                }

                @Override
                public String getEventType() {
                    return null;
                }

                @Override
                public boolean isConfirmed() {
                    return false;
                }

                @Override
                public int compareTo(@NonNull Object o) {
                    return 0;
                }
            };
            return t;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * DayDraggableEventView contains data source and all information about new status
     */
    public interface OnEventListener {
        //If current item view is draggable
        boolean isDraggable(DraggableEventView eventView);
        //while creating item view
        void onEventCreate(DraggableEventView eventView);
        //while clicking item
        void onEventClick(DraggableEventView eventView);
        //When start dragging
        void onEventDragStart(DraggableEventView eventView);
        //On dragging
        void onEventDragging(DraggableEventView eventView, MyCalendar curAreaCal, int x, int y, String locationTime);
        //When dragging finished
        void onEventDragDrop(DraggableEventView eventView);
        //When dragging discard
        void onEventDragEnd(DraggableEventView eventView);
    }

    void setOnEventListener(OnEventListener onEventListener) {
        this.onEventListener = onEventListener;
    }

    void showSingleEventAnim(ITimeEventInterface event){
        final DraggableEventView eventView = this.uidDragViewMap.get(event);
        if (eventView!=null){
            eventView.showAlphaAnim();
        }
    }

    void enableBgMode(){
        isTimeSlot = true;
    }

    private int getRegularEventType(WrapperEvent wrapper){

        long startTime = wrapper.getEvent().getStartTime();
        long endTime = wrapper.getEvent().getEndTime();
        long fromDayBegin = wrapper.getFromDayBegin();
        long todayBegin = fromDayBegin;
        long todayEnd = todayBegin + BaseUtil.getAllDayLong(todayBegin);

        //regular
        if (startTime >= todayBegin && endTime <= todayEnd){
            return REGULAR;
        }

        //Begin part
        if (startTime > todayBegin && endTime > todayEnd){
            return DAY_CROSS_BEGIN;
        }

        //All day part
        if (fromDayBegin == todayBegin && endTime > todayEnd){
            return DAY_CROSS_ALL_DAY;
        }

        //End part
        if (startTime < todayBegin && endTime > todayBegin){
            return DAY_CROSS_END;
        }

        return UNDEFINED;
    }

    public List<WrapperEvent> getTodayEvents(){
        return new ArrayList<>(regularEventViewMap.keySet());
    }
}
