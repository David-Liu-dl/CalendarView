package david.itimecalendar.calendar.calendar.mudules.monthview;


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
import david.itimecalendar.calendar.calendar.mudules.weekview.TimeSlotView;
import david.itimecalendar.calendar.listeners.ITimeEventPackageInterface;
import david.itimecalendar.calendar.util.MyCalendar;
import david.itimecalendar.calendar.wrapper.WrapperTimeSlot;

/**
 * Created by yuhaoliu on 9/05/2017.
 */

public class BodyAdapter extends ITimeAdapter<DayViewBodyCell> {
    private ITimeEventPackageInterface eventPackage;
    private TimeSlotView.TimeSlotPackage slotsInfo;
    private Context context;
    private AttributeSet attrs;
    private List<View> viewItems = new ArrayList<>();
    private int NUM_CELL = 1;

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
                Log.i("", "loadAttributes: ");
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
        DayViewBodyCell view = new DayViewBodyCell(context, attrs);
        viewItems.add(view);
        return view;
    }

    @Override
    public void onBindViewHolder(DayViewBodyCell item, int offset) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, offset);

        DayViewBodyCell body = item;
        body.resetView();
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
            for (WrapperTimeSlot struct : slotsInfo.realSlots
                    ) {
                if (struct.getTimeSlot().isAllDay()){
                    continue;
                }
                if (calendar.contains(struct.getTimeSlot().getStartTime())){
                    body.addSlot(struct,false);
                }
            }
        }

        body.requestLayout();
    }

    public List<View> getViewItems(){
        return this.viewItems;
    }
}
