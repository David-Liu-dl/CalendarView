package org.unimelb.itime.vendor.weekview;

import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import org.unimelb.itime.vendor.R;
import org.unimelb.itime.vendor.dayview.FlexibleLenViewBody;
import org.unimelb.itime.vendor.helper.MyCalendar;
import org.unimelb.itime.vendor.listener.ITimeEventInterface;

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

    private List<WeekView.TimeSlotStruct> slotsInfo;
    private Map<Long, List<ITimeEventInterface>> dayEventMap;

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

    public void setSlotsInfo(ArrayList<WeekView.TimeSlotStruct> slotsInfo) {
        this.slotsInfo = slotsInfo;
    }

    public void changeView(LinearLayout newView,int position){
        views.set(position,newView);
    }

    @Override
    public int getCount() {
        return startPst*2+1;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Date startDate = new Date();
        LinearLayout view = views.get(position%views.size());
        ViewGroup parent = (ViewGroup) view.getParent();
        if (parent != null){
            parent.removeView(view);
        }

        int dateOffset =(position - startPst) * 7 ;
        this.updateHeader(view, dateOffset);
        this.updateBody(view, dateOffset);

        container.addView(view);

        Date endDate = new Date();
        long diff = endDate.getTime() - startDate.getTime();
        Log.i(TAG, position + " diff: " + diff + " ~= " + (float)diff/1000);

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
//        doNotifyDataSetChangedOnce = true;
//        container.removeView(views.get(position % views.size()));
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


    private void updateHeader(ViewGroup parent, int offset){
        int count = parent.getChildCount();
        for (int i = 0; i < count; i++) {
            if (parent.getChildAt(i) instanceof WeekViewHeader){
                MyCalendar cal = new MyCalendar(this.startCal);
                cal.setOffsetByDate(offset);
                ((WeekViewHeader)parent.getChildAt(i)).setMyCalendar(cal);
            }
        }
    }

    private void updateBody(ViewGroup parent, int offset){
        int count = parent.getChildCount();
        for (int i = 0; i < count; i++) {
            if (parent.getChildAt(i) instanceof FlexibleLenViewBody){
                MyCalendar cal = new MyCalendar(this.startCal);
                cal.setOffsetByDate(offset);
                final FlexibleLenViewBody nowBody = ((FlexibleLenViewBody)parent.getChildAt(i));
                nowBody.setCalendar(cal);
                nowBody.resetViews();
                nowBody.setEventList(this.dayEventMap);

                nowBody.clearTimeSlots();
                if (this.slotsInfo != null && this.slotsInfo.size() != 0){
                    for (int j = 0; j < this.slotsInfo.size(); j++) {
                        WeekView.TimeSlotStruct struct = this.slotsInfo.get(j);
                        nowBody.addSlot(struct);
                    }
                }else {
                    Log.i(TAG, "slotsInfo: " + ((this.slotsInfo != null) ? "size 0":"null"));
                }

//                nowBody.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        nowBody.updateTimeSlotsDuration(2 * 3600 * 1000);
//                    }
//                },6000);
            }
        }
    }

    public FlexibleLenViewBody getViewBodyByPosition(int position){
        LinearLayout viewAtPosition = views.get(position % views.size());

        return (FlexibleLenViewBody) viewAtPosition.getChildAt(1);
    }


    public void reloadEvents(){
        for (LinearLayout weekView : views
                ) {
            FlexibleLenViewBody bodyView = (FlexibleLenViewBody)weekView.getChildAt(1);
            if (this.dayEventMap != null){
                bodyView.setEventList(this.dayEventMap);
            }
        }
    }

    public void updateTimeSlotsDuration(long duration){
        for (LinearLayout weekView : views
                ) {
            FlexibleLenViewBody bodyView = (FlexibleLenViewBody)weekView.getChildAt(1);
            bodyView.updateTimeSlotsDuration(duration);
        }
    }

    public void reloadTimeSlots(){
        for (LinearLayout weekView : views
                ) {
            FlexibleLenViewBody bodyView = (FlexibleLenViewBody)weekView.getChildAt(1);
            bodyView.clearTimeSlots();
            if (this.slotsInfo != null && this.slotsInfo.size() != 0){
                for (int j = 0; j < this.slotsInfo.size(); j++) {
                    WeekView.TimeSlotStruct struct = this.slotsInfo.get(j);
                    bodyView.addSlot(struct);
                }
            }else {
                Log.i(TAG, "slotsInfo: " + ((this.slotsInfo != null) ? "size 0":"null"));
            }
        }
    }
}
