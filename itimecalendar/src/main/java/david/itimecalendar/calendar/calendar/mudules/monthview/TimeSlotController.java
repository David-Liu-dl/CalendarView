package david.itimecalendar.calendar.calendar.mudules.monthview;

import android.content.ClipData;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import david.itimecalendar.calendar.listeners.ITimeTimeSlotInterface;
import david.itimecalendar.calendar.unitviews.DraggableEventView;
import david.itimecalendar.calendar.unitviews.DraggableTimeSlotView;
import david.itimecalendar.calendar.unitviews.RecommendedSlotView;
import david.itimecalendar.calendar.util.DensityUtil;
import david.itimecalendar.calendar.util.MyCalendar;
import david.itimecalendar.calendar.util.OverlapHelper;
import david.itimecalendar.calendar.wrapper.WrapperEvent;
import david.itimecalendar.calendar.wrapper.WrapperTimeSlot;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

/**
 * Created by yuhaoliu on 9/01/2017.
 */

public class TimeSlotController {
    private static final String TAG = "Tim1eSlotManager";

    private DayViewBodyCell container;
    private OnTimeSlotListener onTimeSlotListener;
    private long defaultTsDuration = 3600 * 1000;

    private ArrayList<DraggableTimeSlotView> slotViews = new ArrayList<>();
    private ArrayList<RecommendedSlotView> rcdSlotViews = new ArrayList<>();
//    private WeekView.OnRcdTimeSlot onRcdTimeSlot;


    TimeSlotController(DayViewBodyCell container) {
        this.container = container;
    }

    /**
     * TimeSlotView contains data source(ITimeTimeSlotInterface)
     * and all information about new status
     */
    public interface OnTimeSlotListener {
        //While creating time block
        void onTimeSlotCreate(DraggableTimeSlotView draggableTimeSlotView);
        //While clicking existed time block
        void onTimeSlotClick(DraggableTimeSlotView draggableTimeSlotView);
        //while clicking recommended time slot
        void onRcdTimeSlotClick(RecommendedSlotView v);
        //When start dragging
        void onTimeSlotDragStart(DraggableTimeSlotView draggableTimeSlotView);

        /**
         * On dragging
         * @param draggableTimeSlotView : The view on dragging
         * @param x : current X position of View
         * @param y : current Y position of View
         */
        void onTimeSlotDragging(DraggableTimeSlotView draggableTimeSlotView, MyCalendar curAreaCal, int x, int y);

        /**
         * When dragging ended
         * @param draggableTimeSlotView : The view on drop
         * @param startTime : dropped X position of View
         * @param endTime : dropped Y position of View
         */
        void onTimeSlotDragDrop(DraggableTimeSlotView draggableTimeSlotView, long startTime, long endTime);

        void onTimeSlotEdit(DraggableTimeSlotView draggableTimeSlotView);
        void onTimeSlotDelete(DraggableTimeSlotView draggableTimeSlotView);
    }

    void setOnTimeSlotListener(OnTimeSlotListener onTimeSlotListener) {
        this.onTimeSlotListener = onTimeSlotListener;
    }

//    public void setOnRcdTimeSlot(final WeekView.OnRcdTimeSlot onRcdTimeSlot){
//        this.onRcdTimeSlot = onRcdTimeSlot;
//    }

    void onTimeSlotEdit(DraggableTimeSlotView slotView){
        if (this.onTimeSlotListener != null){
            this.onTimeSlotListener.onTimeSlotEdit(slotView);
        }
    }

    void onTimeSlotDelete(DraggableTimeSlotView slotView){
        if (this.onTimeSlotListener != null){
            this.onTimeSlotListener.onTimeSlotDelete(slotView);
        }
    }

    private class OnRcdClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            if (onTimeSlotListener != null){
                onTimeSlotListener.onRcdTimeSlotClick((RecommendedSlotView) v);
            }
        }
    }

    class CreateTimeSlotListener implements View.OnLongClickListener {

        @Override
        public boolean onLongClick(View v) {
            DayInnerBodyLayout container = (DayInnerBodyLayout) v;
            TimeSlotController.this.container.tempDragView = createTimeSlotView(new WrapperTimeSlot(null));
            DayInnerBodyLayout.LayoutParams params = (DayInnerBodyLayout.LayoutParams)TimeSlotController.this.container.tempDragView.getLayoutParams();
            params.top = (int) TimeSlotController.this.container.nowTapY;
            container.addView(TimeSlotController.this.container.tempDragView);
//            BaseUtil.relayoutChildren(container);
            TimeSlotController.this.container.tempDragView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    TimeSlotController.this.container.tempDragView.performLongClick();
                }
            }, 100);

            return true;
        }
    }

    class TimeSlotDragListener implements View.OnDragListener {
        int currentEventNewHour = -1;
        int currentEventNewMinutes = -1;

        @Override
        public boolean onDrag(View v, DragEvent event) {
            DraggableTimeSlotView tsView = (DraggableTimeSlotView) event.getLocalState();

            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    break;
                case DragEvent.ACTION_DRAG_LOCATION:
                    int rawX = (int) (container.layoutWidthPerDay + event.getX());

//                    container.scrollViewAutoScroll(item);

                    if (onTimeSlotListener != null) {
                        onTimeSlotListener.onTimeSlotDragging(tsView, container.getCalendar(),rawX, (int) event.getY());
                    } else {
                        Log.i(TAG, "onDrag: null onEventDragListener");
                    }
//                    container.msgWindowFollow(rawX, (int) item.getY(), index, (View) item.getLocalState());
                    break;
                case DragEvent.ACTION_DRAG_ENTERED:
//                    container.msgWindow.setVisibility(VISIBLE);
                    if (tsView.getType() == DraggableEventView.TYPE_TEMP){
                        container.tempDragView = tsView;
                    }else{
                        container.tempDragView= null;
                    }

                    break;
                case DragEvent.ACTION_DRAG_EXITED:
//                    container.msgWindow.setVisibility(INVISIBLE);
                    container.tempDragView = null;
                    break;
                case DragEvent.ACTION_DROP:
                    //handler ended things in here, because ended some time is not triggered
                    View finalView = (View) event.getLocalState();
                    finalView.setVisibility(VISIBLE);
                    finalView.getBackground().setAlpha(255);
//                    container.msgWindow.setVisibility(INVISIBLE);

                    float actionStopX = event.getX();
                    float actionStopY = event.getY();
                    // Dropped, reassign View to ViewGroup
                    int newX = (int) actionStopX - tsView.getWidth() / 2;
                    int newY = (int) actionStopY - tsView.getHeight() / 2;
                    int[] reComputeResult = container.reComputePositionToSet(newX, newY, tsView, v);

                    //update the item time
                    String new_time = container.positionToTimeTreeMap.get(reComputeResult[1]);
                    //important! update item time after drag
                    String[] time_parts = new_time.split(":");
                    currentEventNewHour = Integer.valueOf(time_parts[0]);
                    currentEventNewMinutes = Integer.valueOf(time_parts[1]);

                    Calendar cal = Calendar.getInstance();
                    cal.setTimeInMillis(container.getCalendar().getBeginOfDayMilliseconds());
                    cal.set(Calendar.HOUR_OF_DAY, currentEventNewHour);
                    cal.set(Calendar.MINUTE, currentEventNewMinutes);
                    tsView.setNewStartTime(cal.getTimeInMillis());

                    if (container.tempDragView == null && onTimeSlotListener != null) {
                        onTimeSlotListener.onTimeSlotDragDrop(tsView, 0, 0);
                    } else {
                        Log.i(TAG, "onDrop Not Called");
                    }

                    if (tsView.getType() == DraggableEventView.TYPE_TEMP) {
                        ViewGroup parent = (ViewGroup) tsView.getParent();
                        if(parent != null){
                            parent.removeView(tsView);
                        }
                        //important! update item time after drag via listener
                        if (onTimeSlotListener != null) {
                            onTimeSlotListener.onTimeSlotCreate(tsView);
                        }
                        //finally reset tempDragView to NULL.
                        container.tempDragView = null;
                    }

                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                    break;
                default:
                    break;
            }

            return true;
        }
    }

    private class TimeSlotLongClickListener implements View.OnLongClickListener {
        @Override
        public boolean onLongClick(View view) {
            ClipData data = ClipData.newPlainText("", "");
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(
                    view);
            view.startDrag(data, shadowBuilder, view, 0);
            if (container.tempDragView != null) {
                view.setVisibility(INVISIBLE);
            } else {
                view.setVisibility(VISIBLE);
            }
            view.getBackground().setAlpha(128);
            return false;
        }
    }

    private DraggableTimeSlotView createTimeSlotView(WrapperTimeSlot wrapper){
        int paddingLR = DensityUtil.dip2px(container.context, 2);
        DraggableTimeSlotView draggableTimeSlotView = new DraggableTimeSlotView(container.context, wrapper, false);
        draggableTimeSlotView.setPadding(paddingLR, 0, paddingLR, 0);
        if (wrapper.getTimeSlot() != null){
            ITimeTimeSlotInterface timeslot = wrapper.getTimeSlot();
            draggableTimeSlotView.setType(DraggableTimeSlotView.TYPE_NORMAL);
            draggableTimeSlotView.setTimes(timeslot.getStartTime(), timeslot.getEndTime());
            draggableTimeSlotView.setIsSelected(wrapper.isSelected());
            DayInnerBodyLayout.LayoutParams params = new DayInnerBodyLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, container.layoutWidthPerDay);
            draggableTimeSlotView.setLayoutParams(params);
        }else {
            long duration = this.slotViews.size() == 0 ? defaultTsDuration : this.slotViews.get(0).getDuration();
            draggableTimeSlotView.setDuration(duration);
            int tempViewHeight = (int)(duration/((float)(3600*1000)) * container.hourHeight);
            draggableTimeSlotView.setType(DraggableTimeSlotView.TYPE_TEMP);
            DayInnerBodyLayout.LayoutParams params = new DayInnerBodyLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, tempViewHeight);
            draggableTimeSlotView.setLayoutParams(params);
        }

        if (wrapper.getTimeSlot() != null && wrapper.isAnimated()){
            draggableTimeSlotView.showAlphaAnim();
        }

        draggableTimeSlotView.setOnLongClickListener(new TimeSlotLongClickListener());
        draggableTimeSlotView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DraggableTimeSlotView draggableTimeSlotView = (DraggableTimeSlotView) v;
                if (onTimeSlotListener != null){
                    onTimeSlotListener.onTimeSlotClick(draggableTimeSlotView);
                }
            }
        });

        return draggableTimeSlotView;
    }



    private RecommendedSlotView createRcdTimeSlotView(WrapperTimeSlot wrapper){
        RecommendedSlotView recommendedSlotView = new RecommendedSlotView(container.context, wrapper, false);
        recommendedSlotView.setOnClickListener(new OnRcdClickListener());
        if (wrapper.getTimeSlot() != null){
            DayInnerBodyLayout.LayoutParams params = new DayInnerBodyLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, container.layoutWidthPerDay);
            params.relativeMarginLeft = container.unitViewLeftMargin;
            params.relativeMarginRight = container.unitViewLeftMargin;
            int marginL = DensityUtil.dip2px(container.context, 2);
            params.left = marginL;
            recommendedSlotView.setLayoutParams(params);
        }

        return recommendedSlotView;
    }


    void enableTimeSlot(){
        container.isTimeSlotEnable = true;
        //remove previous listeners
        container.eventLayout.setOnDragListener(new TimeSlotDragListener());
        container.eventLayout.setOnLongClickListener(new CreateTimeSlotListener());

        for (int j = 0; j < container.eventLayout.getChildCount(); j++) {
            if (container.eventLayout.getChildAt(j) instanceof DraggableEventView){
                container.eventLayout.getChildAt(j).setOnLongClickListener(null);

            }
        }
    }

    private void calculateTimeSlotLayout(DayInnerBodyLayout eventLayout) {
        List<ArrayList<OverlapHelper.OverlappedEvent>> overlapGroups
                = container.xHelper.computeOverlapXForEvents(eventLayout.getSlots());
        for (ArrayList<OverlapHelper.OverlappedEvent> overlapGroup : overlapGroups
                ) {
            for (int i = 0; i < overlapGroup.size(); i++) {
                WrapperTimeSlot wrapperTimeSlot = (WrapperTimeSlot) overlapGroup.get(i).item;
                final int topMargin = getSlotTopMargin(wrapperTimeSlot.getTimeSlot().getStartTime());
                int startY = topMargin;
                int overlapCount = overlapGroup.get(i).params.overlapCount;
                int indexInRow = overlapGroup.get(i).params.indexInRow;
                DraggableTimeSlotView draggableTimeslotView = wrapperTimeSlot.getDraggableTimeSlotView();
                draggableTimeslotView.setPosParam(new DraggableTimeSlotView.PosParam(startY, indexInRow, overlapCount, startY));
            }
        }
    }

    void addSlot(WrapperTimeSlot wrapper, boolean animate){
        DraggableTimeSlotView draggableTimeSlotView = createTimeSlotView(wrapper);
        wrapper.setDraggableTimeSlotView(draggableTimeSlotView);
        container.eventLayout.addView(draggableTimeSlotView, draggableTimeSlotView.getLayoutParams());
        draggableTimeSlotView.bringToFront();
        draggableTimeSlotView.setVisibility(VISIBLE);
        resizeTimeSlot(draggableTimeSlotView,animate);
        slotViews.add(draggableTimeSlotView);
        container.eventLayout.getSlots().add(wrapper);
        draggableTimeSlotView.requestLayout();
        //re-calculate overlapped timeslots
        calculateTimeSlotLayout(container.eventLayout);
    }


    void addRecommended(WrapperTimeSlot wrapper){
        RecommendedSlotView rcdSlotView = createRcdTimeSlotView(wrapper);
        container.eventLayout.addView(rcdSlotView, rcdSlotView.getLayoutParams());
        rcdSlotView.bringToFront();
        rcdSlotView.setVisibility(VISIBLE);
        resizeRcdTimeSlot(rcdSlotView);
        rcdSlotViews.add(rcdSlotView);
        rcdSlotView.requestLayout();
    }

    void updateTimeSlotsDuration(long duration, boolean animate){
        defaultTsDuration = duration;

        for (DraggableTimeSlotView tsV : this.slotViews
                ) {
            long startTime = tsV.getTimeslot().getStartTime();
            tsV.getTimeslot().setEndTime(startTime + duration);
            resizeTimeSlot(tsV,animate);
        }
//        BaseUtil.relayoutChildren(container.eventLayout);
    }

    void clearTimeslots(){
        for (DraggableTimeSlotView draggableTimeSlotView :slotViews
                ) {
            ViewGroup parent = (ViewGroup) draggableTimeSlotView.getParent();
            if (parent != null){
                parent.removeView(draggableTimeSlotView);
            }
        }

        this.slotViews.clear();

        for (RecommendedSlotView rcdView : rcdSlotViews
                ) {
            ViewGroup parent = (ViewGroup) rcdView.getParent();
            if (parent != null){
                parent.removeView(rcdView);
            }
        }

        this.rcdSlotViews.clear();

        this.container.eventLayout.clearViews();
    }

    void showSingleTimeslotAnim(ITimeTimeSlotInterface timeslot){
        final DraggableTimeSlotView timeslotViewDraggable = findTimeslotView(slotViews, timeslot);
        if (timeslotViewDraggable !=null){
            timeslotViewDraggable.showAlphaAnim();
        }
    }

    private void resizeTimeSlot(DraggableTimeSlotView draggableTimeSlotView, boolean animate){
        final DayInnerBodyLayout.LayoutParams params = (DayInnerBodyLayout.LayoutParams) draggableTimeSlotView.getLayoutParams();
        ITimeTimeSlotInterface timeslot = draggableTimeSlotView.getTimeslot();
        long duration = timeslot.getEndTime() - timeslot.getStartTime();
        final int slotHeight = getSlotHeight(duration);
        final int topMargin = getSlotTopMargin(draggableTimeSlotView.getNewStartTime());

        ((DayInnerBodyLayout.LayoutParams) draggableTimeSlotView.getLayoutParams()).top = topMargin;

        if (animate){
            ResizeAnimation resizeAnimation = new ResizeAnimation(
                    draggableTimeSlotView,
                    slotHeight,
                    ResizeAnimation.Type.HEIGHT,
                    600
            );

            draggableTimeSlotView.startAnimation(resizeAnimation);
        }else {
            params.height = slotHeight;
        }

        draggableTimeSlotView.setLayoutParams(draggableTimeSlotView.getLayoutParams());
    }

    private void resizeRcdTimeSlot(RecommendedSlotView rcd){
        long duration = rcd.getWrapper().getTimeSlot().getEndTime() - rcd.getWrapper().getTimeSlot().getStartTime();
        final int slotHeight = getSlotHeight(duration);
        final int topMargin = getSlotTopMargin(rcd.getWrapper().getTimeSlot().getStartTime());

        ((DayInnerBodyLayout.LayoutParams) rcd.getLayoutParams()).height = slotHeight;
        ((DayInnerBodyLayout.LayoutParams) rcd.getLayoutParams()).top = topMargin;
    }

    private int getSlotHeight(long duration){
        final int slotHeight = (int) (((float) duration / (3600 * 1000)) * container.hourHeight);
        return slotHeight;
    }

    private int getSlotTopMargin(long startTime){
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String hourWithMinutes = sdf.format(new Date(startTime));
        String[] components = hourWithMinutes.split(":");
        float trickTime = Integer.valueOf(components[0]) + (float) Integer.valueOf(components[1]) / 100;
        final int topMargin = container.nearestTimeSlotValue(trickTime);

        return topMargin;
    }

    private DraggableTimeSlotView findTimeslotView(ArrayList<DraggableTimeSlotView> draggableTimeSlotViews, ITimeTimeSlotInterface timeslot){
        for (DraggableTimeSlotView timeslotViewDraggable : draggableTimeSlotViews
                ) {
            ITimeTimeSlotInterface slot = timeslotViewDraggable.getTimeslot();
            if (slot != null && slot.getTimeslotUid().equals(timeslot.getTimeslotUid())){
                return timeslotViewDraggable;
            }
        }
        return null;
    }

    private void relocateDraggableTimeslotView(View parent, View orgView, View fakeView, float currentX, float currentY){
        //handler ended things in here, because ended some time is not triggered
        fakeView.setVisibility(VISIBLE);
        // Dropped, reassign View to ViewGroup
        int newX = (int) currentX - fakeView.getWidth() / 2;
        int newY = (int) currentY - fakeView.getHeight() / 2;

        int[] reComputeResult = container.reComputePositionToSet(newX, newY, fakeView, parent);

        //update the item time
        String new_time = container.positionToTimeTreeMap.get(reComputeResult[1]);
        //important! update item time after drag
        String[] time_parts = new_time.split(":");
        int currentEventNewHour = Integer.valueOf(time_parts[0]);
        int currentEventNewMinutes = Integer.valueOf(time_parts[1]);

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(container.getCalendar().getBeginOfDayMilliseconds());
        cal.set(Calendar.HOUR_OF_DAY, currentEventNewHour);
        cal.set(Calendar.MINUTE, currentEventNewMinutes);
    }
}
