package david.itimecalendar.calendar.calendar.mudules.monthview;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
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
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.LinearLayout;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import david.itimecalendar.calendar.listeners.ITimeEventInterface;
import david.itimecalendar.calendar.listeners.ITimeEventPackageInterface;
import david.itimecalendar.calendar.listeners.ITimeInviteeInterface;
import david.itimecalendar.calendar.unitviews.DraggableEventView;
import david.itimecalendar.calendar.util.BaseUtil;
import david.itimecalendar.calendar.util.OverlapHelper;
import david.itimecalendar.calendar.util.DensityUtil;
import david.itimecalendar.calendar.util.MyCalendar;
import david.itimecalendar.calendar.wrapper.WrapperEvent;

import static david.itimecalendar.calendar.calendar.mudules.monthview.DayViewBodyCell.DAY_CROSS_ALL_DAY;
import static david.itimecalendar.calendar.calendar.mudules.monthview.DayViewBodyCell.DAY_CROSS_BEGIN;
import static david.itimecalendar.calendar.calendar.mudules.monthview.DayViewBodyCell.DAY_CROSS_END;
import static david.itimecalendar.calendar.calendar.mudules.monthview.DayViewBodyCell.REGULAR;
import static david.itimecalendar.calendar.calendar.mudules.monthview.DayViewBodyCell.UNDEFINED;

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

    private OverlapHelper xHelper = new OverlapHelper();

    private Class<?> eventClassName;

    private boolean isTimeSlot = false;

    EventController(DayViewBodyCell container) {
        this.container = container;
        this.context = container.getContext();
    }

    void setEventList(ITimeEventPackageInterface eventPackage) {
        this.clearAllEvents();
        Map<Long, List<ITimeEventInterface>> regularDayEventMap = eventPackage.getRegularEventDayMap();
        Map<Long, List<ITimeEventInterface>> repeatedDayEventMap = eventPackage.getRepeatedEventDayMap();

            long startTime = container.getCalendar().getBeginOfDayMilliseconds();

            if (regularDayEventMap != null && regularDayEventMap.containsKey(startTime)){
                List<ITimeEventInterface> currentDayEvents = regularDayEventMap.get(startTime);
                for (ITimeEventInterface event : currentDayEvents) {
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

        this.container.eventLayout.invalidate();
    }

    private void addAllDayEvent(WrapperEvent wrapper, int index) {
//        if (container.topAllDayLayout.getVisibility() != View.VISIBLE){
//            container.topAllDayLayout.setVisibility(View.VISIBLE);
//            ((FrameLayout.LayoutParams)container.getScrollView().getLayoutParams()).setMargins(0,container.topAllDayHeight,0,0);
//        }
//        int offset = index;
//        if (offset > -1 && offset < container.displayLen) {
//            DraggableEventView new_dgEvent = this.createDayDraggableEventView(wrapper, true);
//            DayInnerHeaderEventLayout allDayEventLayout = container.allDayEventLayouts.get(offset);
//            allDayEventLayout.addView(new_dgEvent);
//            allDayEventLayout.getDgEvents().add(new_dgEvent);
//            allDayEventLayout.getEvents().add(wrapper.getEvent());
//        }else {
//            Log.i(TAG, "event in header offset error: " + offset);
//        }
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

    void clearAllEvents() {
//        if (container.topAllDayEventLayouts != null) {
//            for (DayInnerHeaderEventLayout allDayEventLayout:container.allDayEventLayouts
//                    ) {
//                allDayEventLayout.resetView();
//            }
//        }

        if (container.eventLayout != null) {
            container.eventLayout.resetView();
        }

        this.regularEventViewMap.clear();
        this.allDayDgEventViews.clear();
        this.uidDragViewMap.clear();
    }

    private DraggableEventView createDayDraggableEventView(WrapperEvent wrapper, boolean isAllDayEvent) {
        ITimeEventInterface event = wrapper.getEvent();
        DraggableEventView event_view = new DraggableEventView(context, event, isAllDayEvent);
        event_view.setCalendar(container.getCalendar());
        event_view.setType(DraggableEventView.TYPE_NORMAL);
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
        event_view.setType(DraggableEventView.TYPE_TEMP);
        int padding = DensityUtil.dip2px(context,1);
        event_view.setPadding(0,padding,0,0);

        int eventHeight = 1 * container.hourHeight;//one hour
//        DraggableEventView.LayoutParams params = new DraggableEventView.LayoutParams(200, eventHeight);
        DayInnerBodyLayout.LayoutParams params = new DayInnerBodyLayout.LayoutParams(200, eventHeight);
        event_view.setY(tapY - eventHeight / 2);
        event_view.setOnLongClickListener(new EventLongClickListener());
        event_view.setLayoutParams(params);

        return event_view;
    }

    /**
     * calculate the position of event
     * it needs to be called when setting event or event position changed
     */
    private void calculateEventLayout(DayInnerBodyLayout eventLayout) {
        List<ArrayList<OverlapHelper.OverlappedEvent>> overlapGroups
                = xHelper.computeOverlapXForEvents(eventLayout.getEvents());
        for (ArrayList<OverlapHelper.OverlappedEvent> overlapGroup : overlapGroups
                ) {
            for (int i = 0; i < overlapGroup.size(); i++) {
                int startY = getEventY((WrapperEvent) overlapGroup.get(i).event);
                int overlapCount = overlapGroup.get(i).params.overlapCount;
                int indexInRow = overlapGroup.get(i).params.indexInRow;
                DraggableEventView eventView = (DraggableEventView) eventLayout.findViewById(regularEventViewMap.get(overlapGroup.get(i).event));
                eventView.setPosParam(new DraggableEventView.PosParam(startY, indexInRow, overlapCount, startY));
            }
        }
    }

    private void calculateTimeSlotLayout(DayInnerBodyLayout eventLayout) {
        List<ArrayList<OverlapHelper.OverlappedEvent>> overlapGroups
                = xHelper.computeOverlapXForEvents(eventLayout.getEvents());
        for (ArrayList<OverlapHelper.OverlappedEvent> overlapGroup : overlapGroups
                ) {
            for (int i = 0; i < overlapGroup.size(); i++) {
                int startY = getEventY((WrapperEvent) overlapGroup.get(i).event);
                int overlapCount = overlapGroup.get(i).params.overlapCount;
                int indexInRow = overlapGroup.get(i).params.indexInRow;
                DraggableEventView eventView = (DraggableEventView) eventLayout.findViewById(regularEventViewMap.get(overlapGroup.get(i).event));
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
//                        super.onProvideShadowMetrics(outShadowSize, outShadowTouchPoint);
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
                YoYo.with(Techniques.Bounce).
                        pivot(YoYo.CENTER_PIVOT, YoYo.CENTER_PIVOT)
                        .interpolate(new AccelerateDecelerateInterpolator())
                        .duration(1000)
                        .playOn(view);

                ValueAnimator alpha = ValueAnimator.ofObject(new ArgbEvaluator(), DraggableEventView.OPACITY_INT, 255);
                alpha.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                    @Override
                    public void onAnimationUpdate(ValueAnimator animator) {
                        view.getBackground().setAlpha((int) animator.getAnimatedValue());
                    }

                });
                alpha.setDuration(500);
                alpha.start();
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
//                    Log.i(TAG, "ACTION_DRAG_STARTED: " + container.getCalendar().getCalendar().getTime());
                    break;
                case DragEvent.ACTION_DRAG_LOCATION:
                    Log.i(TAG, "ACTION_DRAG_LOCATION: " + this + " " + container.getCalendar().getCalendar().getTime());

                    int rawX = (int) event.getX();
                    int rawY = (int) event.getY();
//                    container.scrollViewAutoScroll(event);

                    if (onEventListener != null) {
                        onEventListener.onEventDragging(dgView, container.getCalendar(), rawX, (int) event.getY());
                    } else {
                        Log.i(TAG, "onDrag: null onEventDragListener");
                    }
//                    container.msgWindowFollow(rawX, (int) event.getY(), index, (View) event.getLocalState());
                    break;
                case DragEvent.ACTION_DRAG_ENTERED:
//                    Log.i(TAG, "ACTION_DRAG_ENTERED:" + container.getCalendar().getCalendar().getTime());
//                    container.msgWindow.setVisibility(View.VISIBLE);
                    if (dgView.getType() == DraggableEventView.TYPE_TEMP){
                        container.tempDragView = dgView;
                    }else{
                        container.tempDragView= null;
                    }
                    break;
                case DragEvent.ACTION_DRAG_EXITED:
//                    Log.i(TAG, "ACTION_DRAG_EXITED:" + container.getCalendar().getCalendar().getTime());
//                    container.msgWindow.setVisibility(View.INVISIBLE);
                    container.tempDragView = null;
                    break;
                case DragEvent.ACTION_DROP:
//                    Log.i(TAG, "ACTION_DROP:" + container.getCalendar().getCalendar().getTime());
                    //handler ended things in here, because ended some time is not triggered
                    dgView.getBackground().setAlpha(DraggableEventView.OPACITY_INT);
                    View finalView = (View) event.getLocalState();
                    finalView.getBackground().setAlpha(DraggableEventView.OPACITY_INT);
                    finalView.setVisibility(View.VISIBLE);
//                    container.msgWindow.setVisibility(View.INVISIBLE);

                    float actionStopX = event.getX();
                    float actionStopY = event.getY();
                    // Dropped, reassign View to ViewGroup
                    int newX = (int) actionStopX - dgView.getWidth() / 2;
                    int newY = (int) actionStopY;
                    int[] reComputeResult = container.reComputePositionToSet(newX, newY, dgView, v);

                    //update the event time
                    String new_time = container.positionToTimeQuarterTreeMap.get(reComputeResult[1]);
                    //important! update event time after drag
                    String[] time_parts = new_time.split(":");
                    currentEventNewHour = Integer.valueOf(time_parts[0]);
                    currentEventNewMinutes = Integer.valueOf(time_parts[1]);

                    dgView.setCalendar(container.getCalendar());
                    dgView.getCalendar().setHour(currentEventNewHour);
                    dgView.getCalendar().setMinute(currentEventNewMinutes);
                    //set dropped container index
//                    dgView.setIndexInView(index);

                    if (container.tempDragView == null && onEventListener != null) {
                        onEventListener.onEventDragDrop(dgView);
                    } else {
                        Log.i(TAG, "onDrop Not Called");
                    }

                    if (dgView.getType() == DraggableEventView.TYPE_TEMP) {
                        ViewGroup parent = (ViewGroup) dgView.getParent();
                        if(parent != null){
                            parent.removeView(dgView);
                        }
                        //important! update event time after drag via listener
                        if (onEventListener != null) {
                            onEventListener.onEventCreate(dgView);
                        }
                        //finally reset tempDragView to NULL.
                        container.tempDragView = null;
                    }
                    Log.i(TAG, "onDrag: drop ");
                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                    if (dgView != null){
                        dgView.getBackground().setAlpha(DraggableEventView.OPACITY_INT);
                    }
                    break;
            }

            return true;
        }
    }

    class CreateEventListener implements View.OnLongClickListener {

        @Override
        public boolean onLongClick(View v) {
//            if (container.tempDragView == null) {
            DayInnerBodyLayout container = (DayInnerBodyLayout) v;
            EventController.this.container.tempDragView = createTempDayDraggableEventView(EventController.this.container.nowTapX, EventController.this.container.nowTapY);
            EventController.this.container.tempDragView.setAlpha(1);
            container.addView(EventController.this.container.tempDragView);
//            BaseUtil.relayoutChildren(container);

            ObjectAnimator scaleX = ObjectAnimator.ofFloat(EventController.this.container.tempDragView, "scaleX", 0f,1f);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(EventController.this.container.tempDragView, "scaleY", 0f,1f);
            ObjectAnimator alpha = ObjectAnimator.ofFloat(EventController.this.container.tempDragView, "alpha", 0f,1f);
            alpha.setDuration(180);
            scaleX.setDuration(120);
            scaleY.setDuration(120);

            AnimatorSet scaleDown = new AnimatorSet();
            scaleDown.play(alpha).with(scaleY).with(scaleX);
            scaleX.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    View p= (View) EventController.this.container.tempDragView.getParent();
                    if (p != null){
                        p.invalidate();
                    }
                }
            });
            scaleDown.addListener(new Animator.AnimatorListener() {
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

            scaleDown.start();

//            EventController.this.container.tempDragView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//                @Override
//                public void onGlobalLayout() {
//                    EventController.this.container.tempDragView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//                    ObjectAnimator scaleX = ObjectAnimator.ofFloat(EventController.this.container.tempDragView, "scaleX", 0f,1f);
//                    ObjectAnimator scaleY = ObjectAnimator.ofFloat(EventController.this.container.tempDragView, "scaleY", 0f,1f);
//                    ObjectAnimator alpha = ObjectAnimator.ofFloat(EventController.this.container.tempDragView, "alpha", 0f,1f);
//                    alpha.setDuration(180);
//                    scaleX.setDuration(120);
//                    scaleY.setDuration(120);
//
//                    AnimatorSet scaleDown = new AnimatorSet();
//                    scaleDown.play(alpha).with(scaleY).with(scaleX);
//                    scaleX.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//                        @Override
//                        public void onAnimationUpdate(ValueAnimator valueAnimator) {
//                            View p= (View) EventController.this.container.tempDragView.getParent();
//                            if (p != null){
//                                p.invalidate();
//                            }
//                        }
//                    });
//                    scaleDown.addListener(new Animator.AnimatorListener() {
//                        @Override
//                        public void onAnimationStart(Animator animation) {
//                            Log.i(TAG, "onAnimationStart: ");
//                        }
//
//                        @Override
//                        public void onAnimationEnd(Animator animation) {
//                            if (EventController.this.container.tempDragView != null
//                                    && EventController.this.container.tempDragView.getParent() != null){
//                                EventController.this.container.tempDragView.performLongClick();
//                            }
//                        }
//
//                        @Override
//                        public void onAnimationCancel(Animator animation) {
//
//                        }
//
//                        @Override
//                        public void onAnimationRepeat(Animator animation) {
//
//                        }
//                    });
//
//                    scaleDown.start();
//                }
//            });
//            }

            return true;
        }
    }

    private ITimeEventInterface initializeEvent() {
        try {
            ITimeEventInterface t = new EventModule();
            return t;
        } catch (Exception e) {
            return null;
        }
    }

    class EventModule implements ITimeEventInterface{

        @Override
        public String getEventUid() {
            return null;
        }

        @Override
        public void setTitle(String title) {

        }

        @Override
        public String getTitle() {
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
        public int getDisplayEventType() {
            return 0;
        }

        @Override
        public String getDisplayStatus() {
            return null;
        }

        @Override
        public void setLocation(String location) {

        }

        @Override
        public String getLocation() {
            return null;
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
        public void setIsAllDay(boolean isAllDay) {

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
        public int compareTo(@NonNull Object o) {
            return 0;
        }
    }

    /**
     * DayDraggableEventView contains data source and all information about new status
     */
    public interface OnEventListener {
        //If current event view is draggable
        boolean isDraggable(DraggableEventView eventView);
        //while creating event view
        void onEventCreate(DraggableEventView eventView);
        //while clicking event
        void onEventClick(DraggableEventView eventView);
        //When start dragging
        void onEventDragStart(DraggableEventView eventView);
        //On dragging
        void onEventDragging(DraggableEventView eventView, MyCalendar curAreaCal, int x, int y);
        //When dragging ended
        void onEventDragDrop(DraggableEventView eventView);
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
}
