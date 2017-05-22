package david.itimerecycler;

import android.util.Log;
import android.view.View;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by yuhaoliu on 15/05/2017.
 */

public abstract class ITimeAdapter {
    private TreeMap<Integer, View> showingMap = new TreeMap<>(new MapKeyComparator());

    public abstract View onCreateViewHolder();

    public abstract void onBindViewHolder(View item, int offset);

    void addViewOffset(View item, int offset){
        showingMap.put(offset,item);
        onBindViewHolder(item, offset);
    }

    void removeViewOffset(int offset){
        if (showingMap.containsKey(offset)){
            showingMap.remove(offset);
        }else {
            throw new RuntimeException("Offset error on " + offset);
        }
    }

    void updateBaseOffsetForMap(int newBaseOffset){
        //item record from -1, so now recover it
        int i = 0;
        newBaseOffset -= 1;
        TreeMap<Integer, View> newShowingMap = new TreeMap<>();

        for (Map.Entry<Integer, View> entry:showingMap.entrySet()
                ) {
            newShowingMap.put(newBaseOffset + i++, entry.getValue());
        }

        showingMap.clear();
        showingMap = newShowingMap;
        notifyDataSetChanged();
    }

    public void notifyDataSetChanged(){
        for (Map.Entry<Integer, View> entry:showingMap.entrySet()
             ) {
            onBindViewHolder(entry.getValue(), entry.getKey());
        }
    }

    public void showMap(){
        Log.i("map", "*****************");
        for (Map.Entry<Integer, View> entry:showingMap.entrySet()
                ) {
            onBindViewHolder(entry.getValue(), entry.getKey());
            Log.i("map", "" + entry.getKey() + " view: " + entry.getValue());
        }
        Log.i("map", "*****************");
    }
//
//    public static Map<String, String> sortMapByKey(Map<String, String> map) {
//        if (map == null || map.isEmpty()) {
//            return null;
//        }
//        Map<String, String> sortMap = new TreeMap<String, String>(new MapKeyComparator());
//        sortMap.putAll(map);
//        return sortMap;
//    }

    //比较器类
    private class MapKeyComparator implements Comparator<Integer> {

        @Override
        public int compare(Integer o1, Integer o2) {
            return o1.compareTo(o2);
        }
    }
}
