package david.itimecalendar.calendar.calendar.mudules.weekview;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;


import com.developer.paul.itimerecycleviewgroup.ITimeAdapter;

import java.util.Calendar;

import david.itimecalendar.calendar.calendar.mudules.monthview.DayViewBodyCell;
import david.itimecalendar.calendar.util.MyCalendar;

/**
 * Created by yuhaoliu on 29/05/2017.
 */

public class WeekViewHeaderAdapter extends ITimeAdapter<WeekViewHeaderCell> {
    private Context context;

    public WeekViewHeaderAdapter(Context context) {
        this.context = context;
    }

    @Override
    public WeekViewHeaderCell onCreateViewHolder() {
        WeekViewHeaderCell head = new WeekViewHeaderCell(context);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        head.setLayoutParams(params);
        return head;
    }

    @Override
    public void onBindViewHolder(WeekViewHeaderCell view, int i) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, i);
        view.setCalendar(new MyCalendar(cal));
    }
}
