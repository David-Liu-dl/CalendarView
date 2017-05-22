package david.itimecalendar.calendar.calendar.mudules.monthview;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
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
import david.itimerecycler.ITimeAdapter;

/**
 * Created by yuhaoliu on 9/05/2017.
 */

public class BodyAdapter extends ITimeAdapter {
    private ITimeEventPackageInterface eventPackage;
    private Context context;
    private List<View> viewItems = new ArrayList<>();

    public BodyAdapter(Context context) {
        this.context = context;
    }

    public void setEventPackage(ITimeEventPackageInterface eventPackage) {
        this.eventPackage = eventPackage;
        this.notifyDataSetChanged();
    }

    @Override
    public View onCreateViewHolder() {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.itime_day_view_body, null, false);
//        View view = inflater.inflate(R.layout.item_view, null, false);
        viewItems.add(view);
        return view;
    }

    @Override
    public void onBindViewHolder(View item, int offset) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, offset);
        TextView tv = (TextView) item.findViewById(R.id.test_tv);
        tv.setText(offset + "\n" + cal.getTime());
        if (this.eventPackage != null){
            DayViewBodyCell body = (DayViewBodyCell) item;
            body.setCalendar(new MyCalendar(cal));
            body.refresh(eventPackage);
        }
    }

    public List<View> getViewItems(){
        return this.viewItems;
//        return new ArrayList<>();
    }
}
