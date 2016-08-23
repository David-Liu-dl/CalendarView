package org.unimelb.itime.vendor.helper;

import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;

/**
 * Created by Paul on 22/08/2016.
 */
public class MyPagerAdapter extends PagerAdapter {
    private ArrayList<LinearLayout> views;
    private boolean doNotifyDataSetChangedOnce = false;

    public MyPagerAdapter(ArrayList<LinearLayout> views){
        this.views = views;
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
//        Log.i("instantiateItem", String.valueOf(position));
        doNotifyDataSetChangedOnce = true;
        return views.get(position % views.size());
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        doNotifyDataSetChangedOnce = true;
//        Log.d("destroyItem", String.valueOf(position));
        container.removeView(views.get(position % views.size()));
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}
