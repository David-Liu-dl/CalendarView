package org.unimelb.itime.vendor.weekview;

import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import org.unimelb.itime.vendor.R;
import org.unimelb.itime.vendor.helper.MyCalendar;
import org.unimelb.itime.vendor.listener.ITimeEventInterface;
import org.unimelb.itime.vendor.weekview.WeekView;
import org.unimelb.itime.vendor.weekview.WeekViewBody;
import org.unimelb.itime.vendor.weekview.WeekViewHeader;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Paul on 22/08/2016.
 */
public class WeekViewPagerAdapter extends PagerAdapter {

    private static final String TAG = "MyAPP";
    private ArrayList<LinearLayout> views;

    private Map<Long, List<ITimeEventInterface>> dayEventMap;
    private boolean doNotifyDataSetChangedOnce = false;
    private int startPst = 0;
    private MyCalendar startCal;

    public WeekViewPagerAdapter(int startPst, ArrayList<LinearLayout> views){

        this.views = views;
        this.startPst =startPst;

        Calendar calendar = Calendar.getInstance();
        int weekOfDay = calendar.get(Calendar.DAY_OF_WEEK);
        calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - weekOfDay + 1);
        MyCalendar startCal = new MyCalendar(calendar);
        this.startCal = startCal;
    }


    public ArrayList<LinearLayout> getViews(){
        return views;
    }

    public void changeView(LinearLayout newView,int position){
        views.set(position,newView);
    }

    @Override
    public int getCount() {
        if (doNotifyDataSetChangedOnce){
            doNotifyDataSetChangedOnce = false;
            notifyDataSetChanged();
        }
        return 1000;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = views.get(position%views.size());
        ViewGroup parent = (ViewGroup) view.getParent();
        if (parent != null)
            parent.removeView(view);
        container.addView(view);
        doNotifyDataSetChangedOnce = true;

        int dateOffset =(position - startPst) * 7 ;
        WeekViewHeader header = (WeekViewHeader) view.findViewById(R.id.week_header);
        WeekViewBody body = (WeekViewBody) view.findViewById(R.id.week_body);
        this.updateHeader(header, dateOffset);
        this.updateBody(body, dateOffset);

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        doNotifyDataSetChangedOnce = true;
        container.removeView(views.get(position % views.size()));
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    public Map<Long, List<ITimeEventInterface>> getDayEventMap() {
        return dayEventMap;
    }

    public void setDayEventMap(Map<Long, List<ITimeEventInterface>> dayEventMap) {
        this.dayEventMap = dayEventMap;
    }

    private List<ITimeEventInterface> getCurrentWeekEvents(MyCalendar cal){
        MyCalendar tempCal = new MyCalendar(cal);
        List<ITimeEventInterface> events = new ArrayList<>();

        if (this.dayEventMap != null){
            for (int i = 1; i < 8; i++) {
                long startTimeM = tempCal.getBeginOfDayMilliseconds();

                if (dayEventMap.containsKey(startTimeM)){
                    events.addAll(dayEventMap.get(startTimeM));
                }else {
                    Log.i(TAG, "current day : NULL EVENT");
                }
                tempCal.setOffsetByDate(1);
            }

            return events;
        }else{
            Log.i(TAG, "dayEventMap null: ");
        }

        return null;
    }

    private void updateHeader(WeekViewHeader header, int offset){
        MyCalendar cal = new MyCalendar(this.startCal);
        cal.setOffsetByDate(offset);
        header.setMyCalendar(cal);
    }

    private void updateBody(WeekViewBody body, int offset){
        MyCalendar cal = new MyCalendar(this.startCal);
        cal.setOffsetByDate(offset);
        body.setMyCalendar(cal);
        body.setEvents(getCurrentWeekEvents(cal));
    }
}
