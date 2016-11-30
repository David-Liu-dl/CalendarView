package org.unimelb.itime.vendor.helper;

import android.util.Pair;

import org.unimelb.itime.vendor.listener.ITimeEventInterface;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by yuhaoliu on 18/08/16.
 */
public class CalendarEventOverlapHelper {
    private static final String TAG = "Helper";
    private final long overlapTolerance = (15/2) * 60 * 1000;
    private ArrayList<ITimeEventInterface> eventModules = new ArrayList<>();
    private List<ArrayList<Pair<Pair<Integer,Integer>,ITimeEventInterface>>> param_events = new ArrayList<>();

    public CalendarEventOverlapHelper() {
    }

    public List<ArrayList<Pair<Pair<Integer,Integer>,ITimeEventInterface>>> computeOverlapXForEvents(ArrayList<ITimeEventInterface> eventModules){
        this.eventModules = eventModules;
        param_events.clear();
//        printDateInfo();
        // sort event by start time first
        this.sortEvent();
        // get overlapped Groups
        ArrayList<ArrayList<ITimeEventInterface>> overlapEventGroups = divideOverlapGroup();
        // compute each event X in every group in overlapped groups
        for (ArrayList<ITimeEventInterface> list: overlapEventGroups
                ) {
            if (list.size() > 1){
                param_events.add(this.computeEventXPstInGroup(list));
            }else {
//                Log.i(TAG, "title: " + list.get(0).getTitle());
                Pair<Integer,Integer> param = new Pair<>(1, 0);
                Pair<Pair<Integer,Integer>,ITimeEventInterface> param_event = new Pair<>(param, list.get(0));
                ArrayList<Pair<Pair<Integer,Integer>,ITimeEventInterface>> single_list = new ArrayList<>();
                single_list.add(param_event);
                param_events.add(single_list);
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

            if (startTime >= (endFlag - overlapTolerance)){
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

        //iterate all events
        //init the very first event as root.
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

            //finding the root for current event
            for (Pair<Integer, ArrayList<EventSlot>> columnEvent:columnEvents
                 ) {
                //compare with last event in column event
                if (currentEventStartTime >= (columnEvent.second.get(columnEvent.second.size() -1).event.getEndTime() - overlapTolerance)){
                    EventSlot childSlot = new EventSlot();
                    childSlot.event = group.get(i);
                    childSlot.row = columnEvent.second.size();
                    childSlot.columnStart = columnEvent.first;
                    columnEvent.second.add(childSlot);
                    foundRoot = true;
                    break;
                }
            }

            //if root not found, this event is a new root for new column.
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

        //compose the event with its parameters
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
