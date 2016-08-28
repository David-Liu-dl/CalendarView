package org.unimelb.itime.vendor.helper;

import android.util.Log;
import android.util.Pair;

import org.unimelb.itime.vendor.listener.ITimeEventInterface;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by yuhaoliu on 18/08/16.
 */
public class CalendarEventOverlapHelper {
    private static final String TAG = "Helper";
    private ArrayList<ITimeEventInterface> eventModules = new ArrayList<>();
    private ArrayList<Pair<Pair<Integer,Integer>,ITimeEventInterface>> param_events = new ArrayList<>();

    public CalendarEventOverlapHelper() {
    }

    public ArrayList<Pair<Pair<Integer,Integer>,ITimeEventInterface>> computeOverlapXForEvents(ArrayList<ITimeEventInterface> eventModules){
        this.eventModules = eventModules;
        param_events.clear();
//        printDateInfo();
        // sort event by start time first
        this.sortEvent();
        // get overlapped Groups
        ArrayList<ArrayList<ITimeEventInterface>> overlapEventGroups = divideOverlapGroup();
        Log.i(TAG, "group size: " + overlapEventGroups.size());
        // compute each event X in every group in overlapped groups
        for (ArrayList<ITimeEventInterface> list: overlapEventGroups
                ) {
            Log.i(TAG, "For new group: ");
            if (list.size() > 1){
                param_events.addAll(this.computeEventXPstInGroup(list));
            }else {
                Pair<Integer,Integer> param = new Pair<>(1, 0);
                Pair<Pair<Integer,Integer>,ITimeEventInterface> param_event = new Pair<>(param, list.get(0));
                param_events.add(param_event);
            }
        }

        return param_events;
    }

    private void sortEvent(){
        Collections.sort(eventModules);
    }

    private ArrayList<ArrayList<ITimeEventInterface>> divideOverlapGroup(){
        ArrayList<ArrayList<ITimeEventInterface>> overLapEventGroups = new ArrayList<>();

        // get today 00:00 milliseconds
        long startFlag = 0;
        long endFlag = 0;//-1 means 00:00 within today

        for (ITimeEventInterface event:eventModules
                ) {
            long startTime = event.getStartTime();
            long endTime = event.getEndTime();

            if (startTime > endFlag){
                //means no overlap with previous group, then create new group
                ArrayList<ITimeEventInterface> new_group = new ArrayList<>();
                new_group.add(event);
                //add new group to groups
                overLapEventGroups.add(new_group);
            }else{
                overLapEventGroups.get(overLapEventGroups.size() - 1).add(event);
            }
            //update flags
            startFlag = Math.min(startFlag, startTime);
            endFlag =  Math.max(endFlag, endTime);
        }

        return overLapEventGroups;
    }

    private ArrayList<Pair<Pair<Integer,Integer>,ITimeEventInterface>> computeEventXPstInGroup(ArrayList<ITimeEventInterface> group) {
        ArrayList<Pair<Integer, ArrayList<EventSlot>>> columnEvents = new ArrayList<>();
        int column_now = 0;

        for (int i = 0; i < group.size(); i++) {
            if (columnEvents.size() == 0){
                EventSlot rootSlot = new EventSlot();
                rootSlot.event = group.get(0);
                rootSlot.row = 0;
                rootSlot.columnStart = 0;
                columnEvents.add(new Pair<>(column_now,initColumn(rootSlot)));
                column_now += 1;
                continue;
            }

            long currentEventStartTime = group.get(i).getStartTime();
            boolean foundRoot = false;
            for (Pair<Integer, ArrayList<EventSlot>> columnEvent:columnEvents
                 ) {
                //compare with last event in column event
                if (currentEventStartTime > columnEvent.second.get(columnEvent.second.size() -1).event.getEndTime()){
                    EventSlot childSlot = new EventSlot();
                    childSlot.event = group.get(i);
                    childSlot.row = columnEvent.second.size();
                    childSlot.columnStart = columnEvent.first;
                    columnEvent.second.add(childSlot);
                    foundRoot = true;
                    break;
                }
            }
            if (!foundRoot){
                EventSlot rootSlot = new EventSlot();
                rootSlot.event = group.get(i);
                rootSlot.row = 0;
                rootSlot.columnStart = column_now;
                columnEvents.add(new Pair<>(column_now,initColumn(rootSlot)));
                column_now += 1;
            }
        }


        ArrayList<Pair<Pair<Integer,Integer>,ITimeEventInterface>> param_event_list = new ArrayList<>();

        for (Pair<Integer, ArrayList<EventSlot>> columnEvent:columnEvents
             ) {
            Pair<Integer,Integer> param = new Pair<>(column_now, columnEvent.first);
            for (EventSlot event:columnEvent.second
                 ) {
                Pair<Pair<Integer,Integer>, ITimeEventInterface> param_event = new Pair<>(param, event.event);
                param_event_list.add(param_event);
            }

        }

        return param_event_list;
    }

    private ArrayList<EventSlot> initColumn(EventSlot root){
        ArrayList<EventSlot> columnSlots = new ArrayList<>();
        columnSlots.add(root);

        return columnSlots;
    }

    class EventSlot{
        public ITimeEventInterface event;
        public int row;
        public int columnStart;
        public int columnEnd;
    }


}
