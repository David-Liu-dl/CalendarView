package david.itimecalendar.calendar.ui.monthview;


import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;


import com.developer.paul.itimerecycleviewgroup.ITimeAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import david.itimecalendar.R;
import david.itimecalendar.calendar.ui.CalendarConfig;
import david.itimecalendar.calendar.ui.weekview.TimeSlotView;
import david.itimecalendar.calendar.listeners.ITimeEventPackageInterface;
import david.itimecalendar.calendar.util.MyCalendar;
import david.itimecalendar.calendar.util.OverlapHelper;
import david.itimecalendar.calendar.wrapper.WrapperEvent;
import david.itimecalendar.calendar.wrapper.WrapperTimeSlot;

/**
 * Created by yuhaoliu on 9/05/2017.
 */

public class BodyAdapter extends ITimeAdapter<DayViewBodyCell> {
    private CalendarConfig calendarConfig = new CalendarConfig();

    private ITimeEventPackageInterface eventPackage;
    private TimeSlotView.TimeSlotPackage slotsInfo;
    private Context context;
    private AttributeSet attrs;
    private List<View> viewItems = new ArrayList<>();
    private int NUM_CELL = 1;
    private OverlapHelper overlapHelper = new OverlapHelper();

    public BodyAdapter(Context context, AttributeSet attrs) {
        this.context = context;
        this.attrs = attrs;
        this.loadAttributes(attrs, context);
    }

    private void loadAttributes(AttributeSet attrs, Context context) {
        if (attrs != null && context != null) {
            TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.viewBody, 0, 0);
            try {
                NUM_CELL = typedArray.getInteger(R.styleable.viewBody_cellNum, NUM_CELL);
            } finally {
                typedArray.recycle();
            }
        }
    }

    public void setEventPackage(ITimeEventPackageInterface eventPackage) {
        this.eventPackage = eventPackage;
        this.notifyDataSetChanged();
    }

    public TimeSlotView.TimeSlotPackage getSlotsInfo() {
        return slotsInfo;
    }

    public void setSlotsInfo(TimeSlotView.TimeSlotPackage slotsInfo) {
        this.slotsInfo = slotsInfo;
    }

    @Override
    public DayViewBodyCell onCreateViewHolder() {
        DayViewBodyCell cell = new DayViewBodyCell(context, attrs);
        viewItems.add(cell);
        return cell;
    }

    @Override
    public void onBindViewHolder(DayViewBodyCell body, int offset) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, offset);

        body.resetView();
        body.setCalendarConfig(calendarConfig);

        //setBorderColor
        if (offset % NUM_CELL == 0){
            body.highlightCellBorder();
        }else {
            body.resetCellBorder();
        }

        //set events
        if (this.eventPackage != null){
            body.setCalendar(new MyCalendar(cal));
            body.setEventList(eventPackage);
        }

        //set timeslots
        if (body.isTimeSlotEnable && this.slotsInfo != null){
            MyCalendar calendar = body.getCalendar();
            //add rcd first
            for (WrapperTimeSlot struct : slotsInfo.rcdSlots
                    ) {
                if (struct.getTimeSlot().isAllDay()){
                    continue;
                }
                if (calendar.contains(struct.getTimeSlot().getStartTime())){
                    body.addRcdSlot(struct);
                }
            }
            //add timeslot on top index
            for (WrapperTimeSlot wrapper : slotsInfo.realSlots
                    ) {
                if (wrapper.getTimeSlot().isAllDay()){
                    continue;
                }
                if (calendar.contains(wrapper.getTimeSlot().getStartTime())){
                    //need to check out if conflict with event
                    if (TimeSlotView.mode == TimeSlotView.ViewMode.NON_ALL_DAY_SELECT){
                        List<WrapperEvent> todayEvents = body.getTodayEvents();
                        wrapper.setConflict(overlapHelper.isConflicted(todayEvents,wrapper));
                    }

                    body.addSlot(wrapper,false);
                }
            }
        }

        body.requestLayout();
    }

    public void setCalendarConfig(CalendarConfig calendarConfig) {
        this.calendarConfig = calendarConfig;
    }

    public List<View> getViewItems(){
        return this.viewItems;
    }
}
