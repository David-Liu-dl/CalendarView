package org.unimelb.itime.vendor.dayview;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Calendar;

public class DayViewBodyPagerAdapter extends PagerAdapter {
    public String TAG = "MyAPP";

    private DayViewBody.BodyOnTouchListener bodyOnTouchListener;

    private Calendar calendar = Calendar.getInstance();

    ArrayList<View> vLists;
    int upperBounds;

    public DayViewBodyPagerAdapter(ArrayList<View> vLists, int upperBounds) {
        this.vLists = vLists;
        this.upperBounds = upperBounds;
    }

//    public void setOnBodyPageChanged(OnBodyPageChanged onBodyPageChanged){
//        this.onBodyPageChanged = onBodyPageChanged;
//    }

    public DayViewBody getViewByPosition(int position){
        DayViewBody viewAtPosition = (DayViewBody) vLists.get(position % vLists.size());

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
        DayViewBody v = (DayViewBody) vLists.get(position % vLists.size());
        ViewGroup parent = (ViewGroup) v.getParent();
        if (parent != null){
            parent.removeView(v);
        }

        v.getCalendar().setOffset(position - upperBounds - (calendar.get(Calendar.DAY_OF_WEEK)-1));
        v.resetViews();
        v.reLoadEvents();
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


    public void setOnCreateNewEvent(DayViewBody.OnCreateNewEvent onCreateNewEvent){
        for (int i = 0; i < vLists.size(); i++) {
            ((DayViewBody) vLists.get(i)).setOnCreateNewEvent(onCreateNewEvent);
        }
    }

    public void setBodyOnTouchListener(DayViewBody.BodyOnTouchListener bodyOnTouchListener) {
        this.bodyOnTouchListener = bodyOnTouchListener;
        for (int i = 0; i < vLists.size(); i++) {
            ((DayViewBody) vLists.get(i)).setBodyOnTouchListener(this.bodyOnTouchListener);
        }
    }

    public void setOnLoadEvents(DayViewBody.OnLoadEvents onLoadEvents) {
        for (int i = 0; i < vLists.size(); i++) {
            ((DayViewBody) vLists.get(i)).setOnLoadEvents(onLoadEvents);
        }
    }

    public void setOnEventChanged(DayViewBody.OnEventChanged onEventChanged) {
        for (int i = 0; i < vLists.size(); i++) {
            ((DayViewBody) vLists.get(i)).setOnEventChanged(onEventChanged);
        }
    }

    public void setOnDgOnClick(DayViewBody.OnDgClickListener onDgClickListener) {
        for (int i = 0; i < vLists.size(); i++) {
            ((DayViewBody) vLists.get(i)).setOnDgClickListener(onDgClickListener);
        }
    }
}