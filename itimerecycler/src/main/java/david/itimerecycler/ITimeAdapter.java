package david.itimerecycler;

import android.util.Log;
import android.view.View;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
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


    public void notifyDataSetChanged(){
        if (awesomeViewGroups==null){
            return;
        }
        for (AwesomeViewGroup awesomeViewgroup: awesomeViewGroups){
            onBindViewHolder(awesomeViewgroup.getItem(), awesomeViewgroup.getInRecycledViewIndex());
            Log.i("new index", "notifyDataSetChanged: " + awesomeViewgroup.getInRecycledViewIndex());
        }
    }

    //比较器类
    private class MapKeyComparator implements Comparator<Integer> {

        @Override
        public int compare(Integer o1, Integer o2) {
            return o1.compareTo(o2);
        }
    }
}
