package org.unimelb.itime.vendor.dayview;

import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import org.unimelb.itime.vendor.listener.ITimeEventInterface;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class DayViewBodyPagerAdapter extends PagerAdapter {
    public String TAG = "MyAPP";

    private DayViewBody.OnBodyTouchListener onBodyTouchListener;

    private Calendar calendar = Calendar.getInstance();

    ArrayList<DayViewBody> vLists;
    Map<Long, List<ITimeEventInterface>> dayEventMap;

    int upperBounds;
    int currentDayPos;

    public DayViewBodyPagerAdapter(ArrayList<DayViewBody> vLists, int upperBounds) {
        this.vLists = vLists;
        this.upperBounds = upperBounds;
    }

//    public void setOnBodyPageChanged(OnBodyPageChanged onBodyPageChanged){
//        this.onBodyPageChanged = onBodyPageChanged;
//    }

    public DayViewBody getViewByPosition(int position){
        DayViewBody viewAtPosition = vLists.get(position % vLists.size());

        return viewAtPosition;
    }

    @Override
    public int getCount() {
        return upperBounds*2+1;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        DayViewBody currentBodyView = vLists.get(position % vLists.size());
        ViewGroup parent = (ViewGroup) currentBodyView.getParent();
        if (parent != null){
            parent.removeView(currentBodyView);
        }

        int offset = position - upperBounds - (calendar.get(Calendar.DAY_OF_WEEK)-1);
        currentBodyView.getCalendar().setOffset(offset);
        currentBodyView.resetViews();
        long startTime = currentBodyView.getCalendar().getBeginOfDayMilliseconds();
        if (this.dayEventMap.containsKey(startTime)){
            currentBodyView.setEventList(this.dayEventMap.get(startTime));
        }
        container.addView(currentBodyView);
        Log.i(TAG, "instantiateItem: " + position);

        return currentBodyView;
    }

    public void reloadEvents(){
        for (DayViewBody bodyView : vLists
             ) {
            long startTime = bodyView.getCalendar().getBeginOfDayMilliseconds();
            if (this.dayEventMap.containsKey(startTime)){
                bodyView.setEventList(this.dayEventMap.get(startTime));
            }
        }
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
//        container.removeView(vLists.get(position % vLists.size()));
    }

    public void setDayEventMap(Map<Long, List<ITimeEventInterface>> dayEventMap){
        this.dayEventMap = dayEventMap;
    }


}