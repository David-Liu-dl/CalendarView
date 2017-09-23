package david.itimecalendar.calendar.ui.monthview;


import android.content.Context;
import android.content.res.TypedArray;
import android.text.LoginFilter;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;


import com.developer.paul.itimerecycleviewgroup.ITimeAdapter;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import david.itimecalendar.R;
import david.itimecalendar.calendar.ui.CalendarConfig;
import david.itimecalendar.calendar.ui.weekview.TimeSlotView;
import david.itimecalendar.calendar.listeners.ITimeEventPackageInterface;
import david.itimecalendar.calendar.util.BaseUtil;
import david.itimecalendar.calendar.util.CalendarPositionHelper;
import david.itimecalendar.calendar.util.MyCalendar;
import david.itimecalendar.calendar.util.OverlapHelper;
import david.itimecalendar.calendar.wrapper.WrapperEvent;
import david.itimecalendar.calendar.wrapper.WrapperTimeSlot;

/**
 * Created by yuhaoliu on 9/05/2017.
 */

public class BodyAdapter extends ITimeAdapter<DayViewBodyCell> {
    private CalendarConfig calendarConfig = new CalendarConfig();
    private CalendarPositionHelper calendarPositionHelper;

    private ITimeEventPackageInterface eventPackage;
    private TimeSlotView.TimeSlotPackage slotsInfo;
    private Context context;
    private AttributeSet attrs;
    private List<View> viewItems = new ArrayList<>();
    private int NUM_CELL = 1;
    private OverlapHelper overlapHelper = new OverlapHelper();

    public BodyAdapter(Context context, AttributeSet attrs, CalendarPositionHelper helper) {
        this.context = context;
        this.attrs = attrs;
        this.calendarPositionHelper = helper;
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
        cell.setPstHelper(calendarPositionHelper);
        viewItems.add(cell);
        return cell;
    }

    @Override
    public void onBindViewHolder(DayViewBodyCell body, int offset) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, offset);

        body.resetView();
        body.setCalendarConfig(calendarConfig);
        body.refreshLayoutListener();

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
        if (calendarConfig.mode != CalendarConfig.Mode.EVENT && this.slotsInfo != null){
            MyCalendar calendar = body.getCalendar();
            //add rcd first
            List<WrapperTimeSlot> rcds = slotsInfo.rcdSlots.get(calendar.getBeginOfDayMilliseconds());

            if (rcds != null){
                for (WrapperTimeSlot struct : rcds
                        ) {
                    if (struct.getTimeSlot().isAllDay()){
                        continue;
                    }

                    body.addRcdSlot(struct);
                }
            }

            //add timeslot on top index
            List<WrapperTimeSlot> reals = slotsInfo.realSlots.get(calendar.getBeginOfDayMilliseconds());
            if (reals != null){
                for (WrapperTimeSlot struct : reals
                        ) {
                    if (struct.getTimeSlot().isAllDay()){
                        continue;
                    }

                    //need to check out if conflict with event
                    if (TimeSlotView.mode == TimeSlotView.ViewMode.NON_ALL_DAY_SELECT){
                        List<WrapperEvent> todayEvents = body.getTodayEvents();
                        struct.setConflict(overlapHelper.isConflicted(todayEvents,struct));
                    }

                    body.addSlot(struct,false);
                }
            }
        }
    }

    public void setCalendarConfig(CalendarConfig calendarConfig) {
        this.calendarConfig = calendarConfig;
    }

    public List<View> getViewItems(){
        return this.viewItems;
    }
}
