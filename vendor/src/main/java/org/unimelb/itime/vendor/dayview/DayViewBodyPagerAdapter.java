package org.unimelb.itime.vendor.dayview;

import android.support.v4.view.PagerAdapter;
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
        DayViewBody v = vLists.get(position % vLists.size());
        ViewGroup parent = (ViewGroup) v.getParent();
        if (parent != null){
            parent.removeView(v);
        }

//        v.resetViews();
//        v.reLoadEvents();
        int offset = position - upperBounds - (calendar.get(Calendar.DAY_OF_WEEK)-1);
        v.getCalendar().setOffset(offset);
        Calendar calendar = v.getCalendar().getCalendar();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        // yin: need to update
        long keyTime = calendar.getTimeInMillis();
        keyTime = Long.parseLong("1473652800000");
        v.setEventList(this.dayEventMap.get(keyTime));
        container.addView(v);

        return v;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
//        container.removeView(vLists.get(position % vLists.size()));
    }

    public void setDayEventMap(Map<Long, List<ITimeEventInterface>> dayEventMap){
        this.dayEventMap = dayEventMap;
    }


}