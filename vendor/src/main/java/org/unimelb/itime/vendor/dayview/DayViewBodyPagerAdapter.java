package org.unimelb.itime.vendor.dayview;

import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Calendar;

public class DayViewBodyPagerAdapter extends PagerAdapter {
    public String TAG = "MyAPP";
    private Calendar calendar = Calendar.getInstance();
    ArrayList<View> vLists;
    int upperBounds;

    public DayViewBodyPagerAdapter(ArrayList<View> vLists, int upperBounds) {
        this.vLists = vLists;
        this.upperBounds = upperBounds;
    }

    public ArrayList<View> getLists(){
        return vLists;
    }
    public int getRealCount() {
        return getLists().size();
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
        DayViewBody v = (DayViewBody) vLists.get(position % vLists.size());
        ViewGroup parent = (ViewGroup) v.getParent();
        if (parent != null){
            parent.removeView(v);
        }
        v.getCalendar().setOffset(position - upperBounds - (calendar.get(Calendar.DAY_OF_WEEK)-1));
        container.addView(v);
        Log.i(TAG, "instantiateItem: " + position);
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


}