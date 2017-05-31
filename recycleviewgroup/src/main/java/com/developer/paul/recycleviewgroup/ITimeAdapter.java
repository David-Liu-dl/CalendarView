package com.developer.paul.recycleviewgroup;

/**
 * Created by Paul on 22/5/17.
 */


import android.util.Log;
import android.view.View;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by yuhaoliu on 15/05/2017.
 */

public abstract class ITimeAdapter {
    private List<AwesomeViewGroup> awesomeViewGroups;

    public abstract View onCreateViewHolder();

    public void setAwesomeViewGroups(List<AwesomeViewGroup> awesomeViewGroups) {
        this.awesomeViewGroups = awesomeViewGroups;

    }

    public void onCreateViewHolders(){
        if (awesomeViewGroups==null){
            return;
        }
        for (AwesomeViewGroup awesomeViewGroup: awesomeViewGroups){
            awesomeViewGroup.setItem(onCreateViewHolder());
            onBindViewHolder(awesomeViewGroup.getItem(), awesomeViewGroup.getInRecycledViewIndex());
        }
    }

    public abstract void onBindViewHolder(View item, int index);


    public void notifyDataSetChanged(AwesomeViewGroup awesomeViewGroup){
        onBindViewHolder(awesomeViewGroup.getItem(), awesomeViewGroup.getInRecycledViewIndex());
    }

    public void notifyDataSetChanged(){
        if (awesomeViewGroups==null){
            return;
        }
        for (AwesomeViewGroup awesomeViewgroup: awesomeViewGroups){
            onBindViewHolder(awesomeViewgroup.getItem(), awesomeViewgroup.getInRecycledViewIndex());
        }
    }
}