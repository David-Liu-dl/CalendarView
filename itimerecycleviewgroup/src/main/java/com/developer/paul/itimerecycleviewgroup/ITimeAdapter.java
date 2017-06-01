package com.developer.paul.itimerecycleviewgroup;

import android.view.View;

import java.util.List;

/**
 * Created by Paul on 31/5/17.
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
