package david.itimecalendar.calendar.calendar.mudules.monthview;


import android.support.v7.widget.RecyclerView;

import java.util.Calendar;

import david.horizontalscrollpageview.HorizontalScrollAdapter;
import david.itimecalendar.calendar.listeners.ITimeEventPackageInterface;
import david.itimecalendar.calendar.util.MyCalendar;

/**
 * Created by yuhaoliu on 9/05/2017.
 */

public class BodyAdapter extends HorizontalScrollAdapter {
    ITimeEventPackageInterface eventPackage;

    public BodyAdapter(int layout) {
        super(layout);
    }

    public void setEventPackage(ITimeEventPackageInterface eventPackage) {
        this.eventPackage = eventPackage;
    }

    @Override
    public void onBindViewHolderOuter(RecyclerView.ViewHolder holder, int position) {
        if (this.eventPackage != null){
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, position - HorizontalScrollAdapter.START_POSITION);
            DayViewBodyCell body = (DayViewBodyCell) holder.itemView;
            body.setCalendar(new MyCalendar(cal));
            body.refresh(eventPackage);
        }
    }
}
