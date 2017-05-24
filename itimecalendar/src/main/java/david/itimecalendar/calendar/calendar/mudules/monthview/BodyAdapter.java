package david.itimecalendar.calendar.calendar.mudules.monthview;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import david.horizontalscrollpageview.HorizontalScrollAdapter;
import david.itimecalendar.R;
import david.itimecalendar.calendar.listeners.ITimeEventPackageInterface;
import david.itimecalendar.calendar.util.MyCalendar;
import david.itimecalendar.calendar.wrapper.WrapperTimeSlot;
import david.itimerecycler.ITimeAdapter;

/**
 * Created by yuhaoliu on 9/05/2017.
 */

public class BodyAdapter extends ITimeAdapter {
    private ITimeEventPackageInterface eventPackage;
    private ArrayList<WrapperTimeSlot> slotsInfo = new ArrayList<>();
    private Context context;
    private AttributeSet attrs;
    private List<View> viewItems = new ArrayList<>();

    public BodyAdapter(Context context, AttributeSet attrs) {
        this.context = context;
        this.attrs = attrs;
    }

    public void setEventPackage(ITimeEventPackageInterface eventPackage) {
        this.eventPackage = eventPackage;
        this.notifyDataSetChanged();
    }

    public ArrayList<WrapperTimeSlot> getSlotsInfo() {
        return slotsInfo;
    }

    public void setSlotsInfo(ArrayList<WrapperTimeSlot> slotsInfo) {
        this.slotsInfo = slotsInfo;
    }

    @Override
    public View onCreateViewHolder() {
        View view = new DayViewBodyCell(context, attrs);
        viewItems.add(view);
        return view;
    }

    @Override
    public void onBindViewHolder(View item, int offset) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, offset);

        DayViewBodyCell body = (DayViewBodyCell) item;
        //set events
        if (this.eventPackage != null){
            body.setCalendar(new MyCalendar(cal));
            body.setEventList(eventPackage);
        }

        //set timeslots
        if (body.isTimeSlotEnable && this.slotsInfo != null){
            MyCalendar calendar = body.getCalendar();
            for (WrapperTimeSlot struct : slotsInfo
                 ) {
                if (calendar.contains(struct.getTimeSlot().getStartTime())){
                    if (struct.isRecommended() && !struct.isSelected()){
                        body.addRcdSlot(struct);
                    }else {
                        body.addSlot(struct,false);
                    }
                }
            }
        }

        body.refresh();
    }

    public List<View> getViewItems(){
        return this.viewItems;
    }
}
