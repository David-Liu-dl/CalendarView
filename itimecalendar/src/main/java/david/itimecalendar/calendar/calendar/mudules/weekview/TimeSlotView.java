package david.itimecalendar.calendar.calendar.mudules.weekview;

import android.animation.LayoutTransition;
import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.developer.paul.itimerecycleviewgroup.ITimeRecycleViewGroup;
import com.github.sundeepk.compactcalendarview.ITimeInnerCalendar.InnerCalendarTimeslotPackage;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import david.itimecalendar.R;
import david.itimecalendar.calendar.calendar.mudules.monthview.DayViewBody;
import david.itimecalendar.calendar.calendar.mudules.monthview.DayViewBodyCell;
import david.itimecalendar.calendar.listeners.ITimeEventInterface;
import david.itimecalendar.calendar.listeners.ITimeTimeSlotInterface;
import david.itimecalendar.calendar.unitviews.DraggableTimeSlotView;
import david.itimecalendar.calendar.unitviews.PopUpMenuBar;
import david.itimecalendar.calendar.unitviews.RecommendedSlotView;
import david.itimecalendar.calendar.unitviews.TimeSlotInnerCalendarView;
import david.itimecalendar.calendar.unitviews.TimeslotChangeView;
import david.itimecalendar.calendar.util.DensityUtil;
import david.itimecalendar.calendar.util.MyCalendar;
import david.itimecalendar.calendar.wrapper.WrapperTimeSlot;

/**
 * Created by yuhaoliu on 30/05/2017.
 */

public class TimeSlotView extends WeekView {
    private TimeSlotInnerCalendarView innerCalView;
    private PopUpMenuBar<String> popUpMenuBar;

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
        this.setLayoutTransition(new LayoutTransition());
        dayViewBody.setOnScrollListener(new ITimeRecycleViewGroup.OnScroll<DayViewBodyCell>() {
            @Override
            public void onPageSelected(DayViewBodyCell view) {
                innerCalView.setShowMonth(view.getCalendar().getCalendar().getTime());
            }

            @Override
            public void onHorizontalScroll(int dx, int preOffsetX) {
                headerRG.followScrollByX(dx);
            }

            @Override
            public void onVerticalScroll(int dy, int preOffsetY) {

            }
        });
        setUpStaticLayer();
        setUpTimeslotDurationWidget();
    }

    private void setUpTimeslotDurationWidget(){
        int popUpMenuHeight = DensityUtil.dip2px(context,50);
        popUpMenuBar = new PopUpMenuBar<>(context);
        popUpMenuBar.setOptHeight(popUpMenuHeight);
        RelativeLayout.LayoutParams popUpMenuBarParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        popUpMenuBarParams.addRule(ALIGN_PARENT_BOTTOM);
        popUpMenuBar.setLayoutParams(popUpMenuBarParams);
        popUpMenuBar.setOnItemSelectedListener(new PopUpMenuBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int position) {
                DurationItem selectedItem = durationData.get(position);
                long toDuration = selectedItem.duration;

                if (onTimeslotDurationChangedListener != null){
                    onTimeslotDurationChangedListener.onTimeslotDurationChanged(toDuration);
                }
            }
        });
        this.addView(popUpMenuBar);

        //fake occupation view for header part of popUpMenuBar
        View blankView = new View(context);
        RelativeLayout.LayoutParams blankVieWParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, popUpMenuHeight);
        blankVieWParams.addRule(ALIGN_PARENT_BOTTOM);
        blankView.setLayoutParams(blankVieWParams);
        blankView.setId(View.generateViewId());
        RelativeLayout.LayoutParams containerParams = (RelativeLayout.LayoutParams)container.getLayoutParams();
        containerParams.addRule(ABOVE, blankView.getId());
        this.addView(blankView);
    }

    private void setUpStaticLayer(){
        //set up static layer
        staticLayer = new FrameLayout(context);
        RelativeLayout.LayoutParams stcPageParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

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

    public void setOnTimeSlotListener(DayViewBody.OnTimeSlotViewBodyListener onTimeSlotViewBodyListener) {
        this.onTimeSlotViewBodyListener = onTimeSlotViewBodyListener;
        super.dayViewBody.setOnTimeSlotListener(onTimeSlotViewInnerListener);
    }

    public void resetTimeSlots(){
        super.dayViewBody.resetTimeSlots();
        innerSlotPackage.numSlotMap.clear();
    }

    DayViewBody.OnTimeSlotViewBodyListener onTimeSlotViewBodyListener;

    // add self needs to passed listener
    DayViewBody.OnTimeSlotViewBodyListener onTimeSlotViewInnerListener  = new DayViewBody.OnTimeSlotViewBodyListener() {
        @Override
        public void onAllDayEventClick(ITimeEventInterface event) {
            if (onTimeSlotViewBodyListener != null){
                onTimeSlotViewBodyListener.onAllDayEventClick(event);
            }
        }

        @Override
        public void onAllDayRcdTimeslotClick(RecommendedSlotView rcdView) {
            if (onTimeSlotViewBodyListener != null){
                onTimeSlotViewBodyListener.onAllDayRcdTimeslotClick(rcdView);
            }
        }

        @Override
        public void onAllDayTimeslotClick(DraggableTimeSlotView timeSlotView) {
            if (onTimeSlotViewBodyListener != null){
                onTimeSlotViewBodyListener.onAllDayTimeslotClick(timeSlotView);
            }
        }

        @Override
        public void onTimeSlotCreate(DraggableTimeSlotView draggableTimeSlotView) {
            if (onTimeSlotViewBodyListener != null){
                onTimeSlotViewBodyListener.onTimeSlotCreate(draggableTimeSlotView);
            }
        }

        @Override
        public void onTimeSlotClick(DraggableTimeSlotView draggableTimeSlotView) {
            if (onTimeSlotViewBodyListener != null){
                onTimeSlotViewBodyListener.onTimeSlotClick(draggableTimeSlotView);
            }
        }

        @Override
        public void onRcdTimeSlotClick(RecommendedSlotView v) {
            if (onTimeSlotViewBodyListener != null){
                onTimeSlotViewBodyListener.onRcdTimeSlotClick(v);
            }
        }

        @Override
        public void onTimeSlotDragStart(DraggableTimeSlotView draggableTimeSlotView) {
            if (onTimeSlotViewBodyListener != null){
                onTimeSlotViewBodyListener.onTimeSlotDragStart(draggableTimeSlotView);
            }
        }

        @Override
        public void onTimeSlotDragging(DraggableTimeSlotView draggableTimeSlotView, MyCalendar curAreaCal, int x, int y) {
            if (onTimeSlotViewBodyListener != null){
                onTimeSlotViewBodyListener.onTimeSlotDragging(draggableTimeSlotView, curAreaCal, x, y);
            }
        }

        @Override
        public void onTimeSlotDragDrop(DraggableTimeSlotView draggableTimeSlotView, long startTime, long endTime) {
            if (onTimeSlotViewBodyListener != null){
                onTimeSlotViewBodyListener.onTimeSlotDragDrop(draggableTimeSlotView, startTime, endTime);
            }
            updateInnerCalendarPackage();
        }

        @Override
        public void onTimeSlotEdit(DraggableTimeSlotView draggableTimeSlotView) {
            showTimeslotChangeDialog(draggableTimeSlotView);
        }

        @Override
        public void onTimeSlotDelete(DraggableTimeSlotView draggableTimeSlotView) {
            if (onTimeSlotViewBodyListener != null){
                onTimeSlotViewBodyListener.onTimeSlotDelete(draggableTimeSlotView);
            }
        }
    };

    private void showTimeslotChangeDialog(final DraggableTimeSlotView dgTimeslot){
        final TimeslotChangeView timeslotChangeView = new TimeslotChangeView(context,dgTimeslot.getTimeslot());
        timeslotChangeView.setBackground(getResources().getDrawable(R.drawable.itime_round_corner_bg));
        final PopupWindow pw = new PopupWindow(timeslotChangeView, 700, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        pw.setElevation(50);
        pw.setAnimationStyle(R.style.Widget_AppCompat_PopupWindow);
        pw.showAtLocation(timeslotChangeView, Gravity.CENTER,0,0);
        timeslotChangeView.setOnPopupWindowListener(new TimeslotChangeView.OnPopupWindowListener() {
            @Override
            public void onCancel() {
                pw.dismiss();
            }

            @Override
            public void onSave(long startTime) {
                dgTimeslot.setNewStartTime( startTime);
                if (onTimeSlotViewBodyListener != null){
                    onTimeSlotViewBodyListener.onTimeSlotEdit(dgTimeslot);
                    dayViewBody.refresh();
                }
                pw.dismiss();
            }
        });
    }

    private List<DurationItem> durationData;

    public void setTimeslotDurationItems(List<DurationItem> data){
        this.durationData = data;
        popUpMenuBar.setDate(getDurationItemNames());
    }

    private OnTimeslotDurationChangedListener onTimeslotDurationChangedListener;

    public void setOnTimeslotDurationChangedListener(OnTimeslotDurationChangedListener onTimeslotDurationChangedListener) {
        this.onTimeslotDurationChangedListener = onTimeslotDurationChangedListener;
    }

    public interface OnTimeslotDurationChangedListener {
        void onTimeslotDurationChanged(long duration);
    }

    public void setTimeslotDuration(long duration, boolean animate){
        dayViewBody.updateTimeSlotsDuration(duration,false);
    }

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

    public static class DurationItem{
        public String showName;
        public long duration;
    }

    private List<String> getDurationItemNames(){
        List<String> list = new ArrayList<>();
        if (durationData != null){
            for (DurationItem item:durationData
                 ) {
                list.add(item.showName);
            }
        }

        return list;
    }


}
