package com.developer.paul.itimerecycleviewgroup;

import android.util.Log;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by David Liu on 31/5/17.
 * ${COMPANY}
 * lyhmelbourne@gmail.com
 */

public abstract class ITimeAdapter<T extends View> {
    private List<AwesomeViewGroup> awesomeViewGroups;

    public abstract T onCreateViewHolder();

    public void setAwesomeViewGroups(List<AwesomeViewGroup> awesomeViewGroups) {
        this.awesomeViewGroups = awesomeViewGroups;
    }

    public List<T> getAllCompeletedItems(){
        List<T> items = new ArrayList<>();

        for (int i = 1; i < awesomeViewGroups.size() -1; i++) {
            if (awesomeViewGroups.get(i).getItem() == null){
                return null;
            }
            items.add((T)awesomeViewGroups.get(i).getItem());
        }

        return items;
    }

    public void onCreateViewHolders(){
        if (awesomeViewGroups==null){
            return;
        }
        for (AwesomeViewGroup awesomeViewGroup: awesomeViewGroups){
            awesomeViewGroup.setItem(onCreateViewHolder());
            onBindViewHolder((T)awesomeViewGroup.getItem(), awesomeViewGroup.getInRecycledViewIndex());
        }
    }

    public abstract void onBindViewHolder(T item, int index);


    public void notifyDataSetChanged(AwesomeViewGroup awesomeViewGroup){
        onBindViewHolder((T)awesomeViewGroup.getItem(), awesomeViewGroup.getInRecycledViewIndex());
    }

    public void notifyDataSetChanged(){
        if (awesomeViewGroups==null){
            return;
        }
        for (AwesomeViewGroup awesomeViewgroup: awesomeViewGroups){
            onBindViewHolder((T)awesomeViewgroup.getItem(), awesomeViewgroup.getInRecycledViewIndex());
        }
    }
}
