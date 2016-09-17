package org.unimelb.itime.vendor.timeslotview;

import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import org.unimelb.itime.vendor.R;
import org.unimelb.itime.vendor.helper.MyCalendar;
import org.unimelb.itime.vendor.listener.ITimeEventInterface;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * Created by Paul on 22/08/2016.
 */
public class WeekTimeSlotViewPagerAdapter extends PagerAdapter {

    private static final String TAG = "MyAPP";
    private List<LinearLayout> views;

    private Map<Long, List<ITimeEventInterface>> dayEventMap;
    private boolean doNotifyDataSetChangedOnce = false;
    private int startPst = 0;
    private MyCalendar startCal;
    private Map<Long,Boolean> timeSlots;
    private int duration;

    public WeekTimeSlotViewPagerAdapter(int startPst, List<LinearLayout> views){

        this.views = views;
        this.startPst =startPst;

        Calendar calendar = Calendar.getInstance();
        int weekOfDay = calendar.get(Calendar.DAY_OF_WEEK);
        calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - weekOfDay + 1);
        MyCalendar startCal = new MyCalendar(calendar);
        this.startCal = startCal;
    }


    public List<LinearLayout> getViews(){
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

        int dateOffset =(position - startPst) * 7;
        WeekTimeSlotViewHeader header = (WeekTimeSlotViewHeader) view.findViewById(R.id.time_slot_header);
        WeekTimeSlotViewBody body = (WeekTimeSlotViewBody) view.findViewById(R.id.time_slot_body);
        this.updateHeader(header, dateOffset);
        this.updateBody(body, dateOffset);

        Log.i(TAG, "instantiateItem: " + position);

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
            for (int i = 1; i < 7; i++) {

                long startTimeM = tempCal.getBeginOfDayMilliseconds();
                if (dayEventMap.containsKey(startTimeM)){
                    events.addAll(dayEventMap.get(startTimeM));
                }else {
                    Log.i(TAG, "getCurrentWeekEvents: NULL EVENT");
                }
                tempCal.setOffset(i);
            }

            return events;
        }else{
            Log.i(TAG, "dayEventMap null: ");
        }

        return null;
    }

    private void updateHeader(WeekTimeSlotViewHeader header, int offset){
        MyCalendar cal = new MyCalendar(Calendar.getInstance());
        cal.setOffset(offset);
        header.setMyCalendar(cal);
    }

    private void updateBody(WeekTimeSlotViewBody body, int offset){
        MyCalendar cal = new MyCalendar(Calendar.getInstance());
        cal.setOffset(offset);
        body.setMyCalendar(cal);
        body.setEvents(getCurrentWeekEvents(cal));
        body.setTimeSlots(this.timeSlots, this.duration);
    }

    public void setTimeSlots(Map<Long, Boolean> timeSlots, int duration) {
        this.timeSlots = timeSlots;
        this.duration = duration;
    }
}
