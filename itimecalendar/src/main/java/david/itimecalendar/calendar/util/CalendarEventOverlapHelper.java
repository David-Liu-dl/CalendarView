package david.itimecalendar.calendar.util;

import android.util.Pair;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import david.itimecalendar.calendar.listeners.ITimeEventInterface;
import david.itimecalendar.calendar.wrapper.WrapperEvent;

/**
 * Created by yuhaoliu on 18/08/16.
 */
public class CalendarEventOverlapHelper {
    private final long overlapTolerance = (15/2) * 60 * 1000;

    private ArrayList<WrapperEvent> eventModules = new ArrayList<>();
    private List<ArrayList<Pair<Pair<Integer,Integer>,WrapperEvent>>> param_events = new ArrayList<>();

    public CalendarEventOverlapHelper() {
    }

    public List<ArrayList<Pair<Pair<Integer,Integer>,WrapperEvent>>> computeOverlapXForEvents(ArrayList<WrapperEvent> eventModules){
        this.eventModules = eventModules;
        param_events.clear();
        // sort event by start time first
        this.sortEvent();
        // get overlapped Groups
        ArrayList<ArrayList<WrapperEvent>> overlapEventGroups = divideOverlapGroup();
        // compute each event X in every group in overlapped groups
        for (ArrayList<WrapperEvent> list: overlapEventGroups
                ) {
            if (list.size() > 1){
                param_events.add(this.computeEventXPstInGroup(list));
            }else {
                Pair<Integer,Integer> param = new Pair<>(1, 0);
                Pair<Pair<Integer,Integer>,WrapperEvent> param_event = new Pair<>(param, list.get(0));
                ArrayList<Pair<Pair<Integer,Integer>,WrapperEvent>> single_list = new ArrayList<>();
                single_list.add(param_event);
                param_events.add(single_list);
            }
        }

        return param_events;
    }

    private void sortEvent(){
        Collections.sort(eventModules);
    }

    private ArrayList<ArrayList<WrapperEvent>> divideOverlapGroup(){
        ArrayList<ArrayList<WrapperEvent>> overLapEventGroups = new ArrayList<>();

        // get today 00:00 milliseconds
        long startFlag = 0;
        long endFlag = 0;//-1 means 00:00 within today

        for (WrapperEvent wrapper:eventModules
                ) {
            ITimeEventInterface event = wrapper.getEvent();
            long startTime = event.getStartTime();
            long endTime = event.getEndTime();

            if (startTime >= (endFlag - overlapTolerance)){
                //means no overlap with previous group, then create new group
                ArrayList<WrapperEvent> new_group = new ArrayList<>();
                new_group.add(wrapper);
                //add new group to groups
                overLapEventGroups.add(new_group);
            }else{
                overLapEventGroups.get(overLapEventGroups.size() - 1).add(wrapper);
            }
            //update flags
            startFlag = Math.min(startFlag, startTime);
            endFlag =  Math.max(endFlag, endTime);
        }

        return overLapEventGroups;
    }

    private ArrayList<Pair<Pair<Integer,Integer>,WrapperEvent>> computeEventXPstInGroup(ArrayList<WrapperEvent> group) {
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

            long currentEventStartTime = group.get(i).getEvent().getStartTime();
            boolean foundRoot = false;

            //finding the root for current event
            for (Pair<Integer, ArrayList<EventSlot>> columnEvent:columnEvents
                 ) {
                //compare with last event in column event
                if (currentEventStartTime >= (columnEvent.second.get(columnEvent.second.size() -1).event.getEvent().getEndTime() - overlapTolerance)){
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


        ArrayList<Pair<Pair<Integer,Integer>,WrapperEvent>> param_event_list = new ArrayList<>();

        //compose the event with its parameters
        for (Pair<Integer, ArrayList<EventSlot>> columnEvent:columnEvents
             ) {
            Pair<Integer,Integer> param = new Pair<>(column_now, columnEvent.first);
            for (EventSlot event:columnEvent.second
                 ) {
                Pair<Pair<Integer,Integer>, WrapperEvent> param_event = new Pair<>(param, event.event);
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
        public WrapperEvent event;
        public int row;
        public int columnStart;
        public int columnEnd;
    }


}
