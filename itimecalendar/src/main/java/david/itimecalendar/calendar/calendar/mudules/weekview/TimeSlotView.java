package david.itimecalendar.calendar.calendar.mudules.weekview;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.developer.paul.recycleviewgroup.RecycleViewGroup;
import com.github.sundeepk.compactcalendarview.ITimeInnerCalendar.InnerCalendarTimeslotPackage;

import java.util.ArrayList;
import java.util.Date;

import david.itimecalendar.calendar.calendar.mudules.monthview.DayViewBodyCell;
import david.itimecalendar.calendar.calendar.mudules.monthview.TimeSlotController;
import david.itimecalendar.calendar.listeners.ITimeTimeSlotInterface;
import david.itimecalendar.calendar.unitviews.DraggableTimeSlotView;
import david.itimecalendar.calendar.unitviews.RecommendedSlotView;
import david.itimecalendar.calendar.unitviews.TimeSlotInnerCalendarView;
import david.itimecalendar.calendar.util.MyCalendar;
import david.itimecalendar.calendar.wrapper.WrapperTimeSlot;

/**
 * Created by yuhaoliu on 30/05/2017.
 */

public class TimeSlotView extends WeekView {
    private TimeSlotInnerCalendarView innerCalView;
    private FrameLayout staticLayer;
    private InnerCalendarTimeslotPackage innerSlotPackage = new InnerCalendarTimeslotPackage();

    public TimeSlotView(@NonNull Context context) {
        super(context);
        initViews();
    }

    public TimeSlotView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initViews();
    }

    public TimeSlotView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews();
    }

    private void initViews(){
        dayViewBody.setOnScrollListener(new RecycleViewGroup.OnScroll<DayViewBodyCell>() {
            @Override
            public void onPageSelected(DayViewBodyCell view) {
                innerCalView.setShowMonth(view.getCalendar().getCalendar().getTime());
            }

            @Override
            public void onHorizontalScroll(int dx, int preOffsetX) {
                headerRG.moveXBy(dx);
            }

            @Override
            public void onVerticalScroll(int dy, int preOffsetY) {

            }
        });

        setUpStaticLayer();
    }

    private void setUpStaticLayer(){
        //set up static layer
        staticLayer = new FrameLayout(context);
        FrameLayout.LayoutParams stcPageParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        innerCalView = new TimeSlotInnerCalendarView(context);
        innerCalView.setHeaderHeight((int)headerHeight);
        innerCalView.setSlotNumMap(innerSlotPackage);

        FrameLayout.LayoutParams innerCalViewParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        this.staticLayer.addView(innerCalView,innerCalViewParams);

        staticLayer.setVisibility(GONE);
        this.addView(staticLayer, stcPageParams);
    }

    private void updateInnerCalendarPackage(){
        TimeSlotPackage slotsInfo = dayViewBody.getTimeSlotPackage();
        innerSlotPackage.clear();

        for (WrapperTimeSlot wrapper:slotsInfo.realSlots
                ) {
            String strDate = innerSlotPackage.slotFmt.format(new Date(wrapper.getTimeSlot().getStartTime()));
            innerSlotPackage.add(strDate);
        }
        innerCalView.refreshSlotNum();
    }

    /*** for timeslot ***/

    public void enableTimeSlot(){
        super.dayViewBody.enableTimeSlot();
        //staticLayer become visible
        staticLayer.setVisibility(VISIBLE);
    }

    public void setOnTimeSlotInnerCalendar(TimeSlotInnerCalendarView.OnTimeSlotInnerCalendar onTimeSlotInnerCalendar) {
        this.innerCalView.setOnTimeSlotInnerCalendar(onTimeSlotInnerCalendar);
    }

    public void addTimeSlot(ITimeTimeSlotInterface slotInfo){
        super.dayViewBody.addTimeSlot(slotInfo);
        updateInnerCalendarPackage();
    }

    public void addTimeSlot(WrapperTimeSlot wrapperTimeSlot){
        super.dayViewBody.addTimeSlot(wrapperTimeSlot);
        updateInnerCalendarPackage();
    }

    public void removeTimeslot(ITimeTimeSlotInterface timeslot){
        super.dayViewBody.getTimeSlotPackage().removeTimeSlot(timeslot);
        updateInnerCalendarPackage();
    }

    public void removeTimeslot(WrapperTimeSlot wrapper){
        super.dayViewBody.getTimeSlotPackage().removeTimeSlot(wrapper);
        updateInnerCalendarPackage();
    }

    public void setOnTimeSlotListener(TimeSlotController.OnTimeSlotListener onTimeSlotOuterListener) {
        this.onTimeSlotOuterListener = onTimeSlotOuterListener;
        super.dayViewBody.setOnTimeSlotListener(onTimeSlotInnerListener);
    }

    public void resetTimeSlots(){
        super.dayViewBody.resetTimeSlots();
        innerSlotPackage.numSlotMap.clear();
    }
    TimeSlotController.OnTimeSlotListener onTimeSlotOuterListener;
    TimeSlotController.OnTimeSlotListener onTimeSlotInnerListener = new TimeSlotController.OnTimeSlotListener() {
        @Override
        public void onTimeSlotCreate(DraggableTimeSlotView draggableTimeSlotView) {
            if (onTimeSlotOuterListener != null){
                onTimeSlotOuterListener.onTimeSlotCreate(draggableTimeSlotView);
            }
        }

        @Override
        public void onTimeSlotClick(DraggableTimeSlotView draggableTimeSlotView) {
            if (onTimeSlotOuterListener != null){
                onTimeSlotOuterListener.onTimeSlotClick(draggableTimeSlotView);
            }
        }

        @Override
        public void onRcdTimeSlotClick(RecommendedSlotView v) {
            if (onTimeSlotOuterListener != null){
                onTimeSlotOuterListener.onRcdTimeSlotClick(v);
            }
        }

        @Override
        public void onTimeSlotDragStart(DraggableTimeSlotView draggableTimeSlotView) {
            if (onTimeSlotOuterListener != null){
                onTimeSlotOuterListener.onTimeSlotDragStart(draggableTimeSlotView);
            }
        }

        @Override
        public void onTimeSlotDragging(DraggableTimeSlotView draggableTimeSlotView, MyCalendar curAreaCal, int x, int y) {
            if (onTimeSlotOuterListener != null){
                onTimeSlotOuterListener.onTimeSlotDragging(draggableTimeSlotView, curAreaCal, x, y);
            }
        }

        @Override
        public void onTimeSlotDragDrop(DraggableTimeSlotView draggableTimeSlotView, long startTime, long endTime) {
            if (onTimeSlotOuterListener != null){
                onTimeSlotOuterListener.onTimeSlotDragDrop(draggableTimeSlotView, startTime, endTime);
            }
            updateInnerCalendarPackage();
        }

        @Override
        public void onTimeSlotEdit(DraggableTimeSlotView draggableTimeSlotView) {
            if (onTimeSlotOuterListener != null){
                onTimeSlotOuterListener.onTimeSlotEdit(draggableTimeSlotView);
            }
        }

        @Override
        public void onTimeSlotDelete(DraggableTimeSlotView draggableTimeSlotView) {
            if (onTimeSlotOuterListener != null){
                onTimeSlotOuterListener.onTimeSlotDelete(draggableTimeSlotView);
            }
        }
    };


    public static class TimeSlotPackage{
        public ArrayList<WrapperTimeSlot> rcdSlots = new ArrayList<>();
        public ArrayList<WrapperTimeSlot> realSlots = new ArrayList<>();

        public void clear(){
            rcdSlots.clear();
            realSlots.clear();
        }

        void removeTimeSlot(WrapperTimeSlot wrapperTimeSlot){
            if (wrapperTimeSlot.isRecommended() && !wrapperTimeSlot.isSelected()){
                rcdSlots.remove(wrapperTimeSlot);
            }else {
                realSlots.remove(wrapperTimeSlot);
            }
        }

        void removeTimeSlot(ITimeTimeSlotInterface itimeTimeSlotInterface){
            for (WrapperTimeSlot wrapper:rcdSlots
                 ) {
                if (wrapper.getTimeSlot() != null && wrapper.getTimeSlot() == itimeTimeSlotInterface){
                    rcdSlots.remove(wrapper);
                    return;
                }
            }

            for (WrapperTimeSlot wrapper:realSlots
                    ) {
                if (wrapper.getTimeSlot() != null && wrapper.getTimeSlot() == itimeTimeSlotInterface){
                    rcdSlots.remove(wrapper);
                    return;
                }
            }
        }
    }
}
