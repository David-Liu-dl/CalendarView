package david.itimecalendar.calendar.mudules.weekview;

import android.animation.LayoutTransition;
import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.developer.paul.itimerecycleviewgroup.ITimeRecycleViewGroup;
import com.github.sundeepk.compactcalendarview.ITimeTimeslotCalendar.InnerCalendarTimeslotPackage;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import david.itimecalendar.R;
import david.itimecalendar.calendar.mudules.monthview.DayViewBody;
import david.itimecalendar.calendar.mudules.monthview.DayViewBodyCell;
import david.itimecalendar.calendar.listeners.ITimeTimeSlotInterface;
import david.itimecalendar.calendar.unitviews.DraggableTimeSlotView;
import david.itimecalendar.calendar.unitviews.TimeslotDurationWidget;
import david.itimecalendar.calendar.unitviews.RecommendedSlotView;
import david.itimecalendar.calendar.unitviews.TimeSlotInnerCalendarView;
import david.itimecalendar.calendar.unitviews.TimeslotChangeView;
import david.itimecalendar.calendar.unitviews.TimeslotToolBar;
import david.itimecalendar.calendar.util.DensityUtil;
import david.itimecalendar.calendar.util.MyCalendar;
import david.itimecalendar.calendar.wrapper.WrapperTimeSlot;

/**
 * Created by yuhaoliu on 30/05/2017.
 */

public class TimeSlotView extends WeekView {
    private TimeSlotInnerCalendarView innerCalView;
    private TimeslotDurationWidget<String> popUpMenuBar;

    private FrameLayout staticLayer;
    private InnerCalendarTimeslotPackage innerSlotPackage = new InnerCalendarTimeslotPackage();
    private TimeslotToolBar timeslotToolBar;


    public TimeSlotView(@NonNull Context context) {
        super(context);
        setUpViews();
    }

    public TimeSlotView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setUpViews();
    }

    public TimeSlotView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setUpViews();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (timeslotToolBar.getVisibility() == VISIBLE){
            if (ev.getAction() == MotionEvent.ACTION_DOWN){
                Rect viewRect = new Rect();
                timeslotToolBar.getGlobalVisibleRect(viewRect);
                if (viewRect.contains((int) ev.getRawX(), (int) ev.getRawY())) {
                    //when click on tooltips and it's visible
                    // pass it to tooltips
                }else {
                    //when click outside of tooltips and it's visible
                    timeslotToolBar.setVisibility(GONE);
                }
            }else {
                timeslotToolBar.setVisibility(GONE);
            }
        }

        return super.onInterceptTouchEvent(ev);
    }

    private void setUpViews(){
        this.setLayoutTransition(new LayoutTransition());
        this.dayViewBody.setOnScrollListener(new ITimeRecycleViewGroup.OnScroll<DayViewBodyCell>() {
            @Override
            public void onPageSelected(DayViewBodyCell view) {
                innerCalView.setCurrentDate(view.getCalendar().getCalendar().getTime());
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
        setUpBubbleView();
    }

    private void setUpBubbleView(){
        timeslotToolBar = new TimeslotToolBar(getContext());
        int bubbleWidth = DensityUtil.dip2px(getContext(),110);
        int bubbleHeight = DensityUtil.dip2px(getContext(),35);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(bubbleWidth, bubbleHeight);
        this.addView(timeslotToolBar,params);

        timeslotToolBar.setOnButtonClickListener(new TimeslotToolBar.OnButtonClickListener() {
            @Override
            public void onEditClick() {
                Object tag = timeslotToolBar.getTag();
                if (tag != null){
                    DraggableTimeSlotView slotView = (DraggableTimeSlotView) tag;
                    timeslotToolBar.setVisibility(GONE);
                    timeslotToolBar.setTag(null);
                    onTimeSlotViewBodyInnerListener.onTimeSlotEdit(slotView);
                }
            }

            @Override
            public void onDeleteClick() {
                Object tag = timeslotToolBar.getTag();
                if (tag != null){
                    DraggableTimeSlotView slotView = (DraggableTimeSlotView) tag;
                    timeslotToolBar.setVisibility(GONE);
                    timeslotToolBar.setTag(null);
                    onTimeSlotViewBodyInnerListener.onTimeSlotDelete(slotView);
                }
            }
        });
    }

    private void showTimeSlotTools(DraggableTimeSlotView slotView, boolean editable){
        timeslotToolBar.setBubbleEditable(editable);

        int slotRect[] = {0, 0};
        int bodyRect[] = {0, 0};
        slotView.getLocationOnScreen(slotRect);
        this.getLocationOnScreen(bodyRect);
        float posX = slotRect[0] - bodyRect[0];
        float posY = slotRect[1] - bodyRect[1];
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) timeslotToolBar.getLayoutParams();
        int topMargin = (int)(posY - params.height);

        params.topMargin = topMargin>0?topMargin:0;
        params.leftMargin = (int)posX;
        timeslotToolBar.setVisibility(View.VISIBLE);
        timeslotToolBar.requestLayout();
    }

    private void setUpTimeslotDurationWidget(){
        int popUpMenuHeight = DensityUtil.dip2px(context,50);
        popUpMenuBar = new TimeslotDurationWidget<>(context);
        popUpMenuBar.setOptHeight(popUpMenuHeight);
        RelativeLayout.LayoutParams popUpMenuBarParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        popUpMenuBarParams.addRule(ALIGN_PARENT_BOTTOM);
        popUpMenuBar.setLayoutParams(popUpMenuBarParams);
        popUpMenuBar.setOnItemSelectedListener(new TimeslotDurationWidget.OnItemSelectedListener() {
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
        innerCalView.setOnTimeSlotInnerCalendar(new TimeSlotInnerCalendarView.OnTimeSlotInnerCalendar() {
            @Override
            public void onCalendarBtnClick(View v, boolean result) {
            }

            @Override
            public void onDayClick(Date dateClicked) {
                TimeSlotView.this.scrollToDate(dateClicked);
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
            }
        });
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

//    public void setOnTimeSlotInnerCalendar(TimeSlotInnerCalendarView.OnTimeSlotInnerCalendar onTimeSlotInnerCalendar) {
//        this.innerCalView.setOnTimeSlotInnerCalendar(onTimeSlotInnerCalendar);
//    }

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

    public void setOnTimeSlotListener(DayViewBody.OnViewBodyTimeSlotListener onTimeSlotViewBodyListener) {
        this.onTimeSlotViewBodyOuterListener = onTimeSlotViewBodyListener;
        super.dayViewBody.setOnTimeSlotListener(onTimeSlotViewBodyInnerListener);
    }

    public void resetTimeSlots(){
        super.dayViewBody.resetTimeSlots();
        innerSlotPackage.numSlotMap.clear();
    }

    DayViewBody.OnViewBodyTimeSlotListener onTimeSlotViewBodyOuterListener;

    // add self needs to passed listener
    DayViewBody.OnViewBodyTimeSlotListener onTimeSlotViewBodyInnerListener = new DayViewBody.OnViewBodyTimeSlotListener() {
        @Override
        public void onAllDayRcdTimeslotClick(long dayBeginMilliseconds) {
            if (onTimeSlotViewBodyOuterListener != null){
                onTimeSlotViewBodyOuterListener.onAllDayRcdTimeslotClick(dayBeginMilliseconds);
            }
        }

        @Override
        public void onAllDayTimeslotClick(DraggableTimeSlotView timeSlotView) {
            timeslotToolBar.setTag(timeSlotView);
            showTimeSlotTools(timeSlotView, false);

            if (onTimeSlotViewBodyOuterListener != null){
                onTimeSlotViewBodyOuterListener.onAllDayTimeslotClick(timeSlotView);
            }

        }

        @Override
        public void onTimeSlotCreate(DraggableTimeSlotView draggableTimeSlotView) {
            if (onTimeSlotViewBodyOuterListener != null){
                onTimeSlotViewBodyOuterListener.onTimeSlotCreate(draggableTimeSlotView);
            }
        }

        @Override
        public void onTimeSlotClick(DraggableTimeSlotView draggableTimeSlotView) {
            timeslotToolBar.setTag(draggableTimeSlotView);
            showTimeSlotTools(draggableTimeSlotView, true);

            if (onTimeSlotViewBodyOuterListener != null){
                onTimeSlotViewBodyOuterListener.onTimeSlotClick(draggableTimeSlotView);
            }
        }

        @Override
        public void onRcdTimeSlotClick(RecommendedSlotView v) {
            if (onTimeSlotViewBodyOuterListener != null){
                onTimeSlotViewBodyOuterListener.onRcdTimeSlotClick(v);
            }
        }

        @Override
        public void onTimeSlotDragStart(DraggableTimeSlotView draggableTimeSlotView) {
            if (onTimeSlotViewBodyOuterListener != null){
                onTimeSlotViewBodyOuterListener.onTimeSlotDragStart(draggableTimeSlotView);
            }
        }

        @Override
        public void onTimeSlotDragging(DraggableTimeSlotView draggableTimeSlotView, MyCalendar curAreaCal, int x, int y) {
            if (onTimeSlotViewBodyOuterListener != null){
                onTimeSlotViewBodyOuterListener.onTimeSlotDragging(draggableTimeSlotView, curAreaCal, x, y);
            }
        }

        @Override
        public void onTimeSlotDragDrop(DraggableTimeSlotView draggableTimeSlotView, long startTime, long endTime) {
            if (onTimeSlotViewBodyOuterListener != null){
                onTimeSlotViewBodyOuterListener.onTimeSlotDragDrop(draggableTimeSlotView, startTime, endTime);
            }
            updateInnerCalendarPackage();
        }

        @Override
        public void onTimeSlotEdit(DraggableTimeSlotView draggableTimeSlotView) {
            showTimeslotChangeDialog(draggableTimeSlotView);
        }

        @Override
        public void onTimeSlotDelete(DraggableTimeSlotView draggableTimeSlotView) {
            if (onTimeSlotViewBodyOuterListener != null){
                onTimeSlotViewBodyOuterListener.onTimeSlotDelete(draggableTimeSlotView);
            }

            dayViewBody.notifyDataSetChanged();
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
                if (onTimeSlotViewBodyOuterListener != null){
                    onTimeSlotViewBodyOuterListener.onTimeSlotEdit(dgTimeslot);
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

    public void setViewMode(DayViewBody.Mode mode){
        dayViewBody.setViewMode(mode);
    }
}
