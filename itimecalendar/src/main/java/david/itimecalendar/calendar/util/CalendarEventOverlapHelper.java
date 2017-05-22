package david.itimecalendar.calendar.util;


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
    private List<ArrayList<OverlappedEvent>> groupedOlpEventList = new ArrayList<>();

    public List<ArrayList<OverlappedEvent>> computeOverlapXForEvents(ArrayList<WrapperEvent> eventModules){
        this.eventModules = eventModules;
        groupedOlpEventList.clear();
        // sort event by start time first
        this.sortEvent();
        // get overlapped Groups
        ArrayList<ArrayList<WrapperEvent>> overlapEventGroups = divideOverlapGroup();
        // compute each event X in every group in overlapped groups
        for (ArrayList<WrapperEvent> list: overlapEventGroups
                ) {
            if (list.size() > 1){
                groupedOlpEventList.add(this.computeEventXPstInGroup(list));
            }else {
                //<overlapCount, indexInRow>
                OverlappedParams params = new OverlappedParams(1,0);
                //<params, event>
                OverlappedEvent param_event = new OverlappedEvent(params, list.get(0));
                ArrayList<OverlappedEvent> group = new ArrayList<>();
                group.add(param_event);
                groupedOlpEventList.add(group);
            }
        }

        return groupedOlpEventList;
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

    private ArrayList<OverlappedEvent> computeEventXPstInGroup(ArrayList<WrapperEvent> group) {
        ArrayList<ColumnPackage> columnEvents = new ArrayList<>();
        int curColumn = 0;

        //iterate all events
        //init the very first event as root.
        for (int i = 0; i < group.size(); i++) {
            if (columnEvents.size() == 0){
                EventSlot rootSlot = new EventSlot();
                rootSlot.event = group.get(0);
                rootSlot.row = 0;
                rootSlot.columnStart = 0;
                columnEvents.add(new ColumnPackage(curColumn, initColumn(rootSlot)));
                curColumn += 1;
                continue;
            }

            long currentEventStartTime = group.get(i).getEvent().getStartTime();
            boolean foundRoot = false;

            //finding the root for current event
            for (ColumnPackage columnEvent:columnEvents
                 ) {
                //compare with last event in column event
                if (currentEventStartTime >= (columnEvent.eventSlots.get(columnEvent.eventSlots.size() -1).event.getEvent().getEndTime() - overlapTolerance)){
                    EventSlot childSlot = new EventSlot();
                    childSlot.event = group.get(i);
                    childSlot.row = columnEvent.eventSlots.size();
                    childSlot.columnStart = columnEvent.column;
                    columnEvent.eventSlots.add(childSlot);
                    foundRoot = true;
                    break;
                }
            }

            //if root not found, this event is a new root for new column.
            if (!foundRoot){
                EventSlot rootSlot = new EventSlot();
                rootSlot.event = group.get(i);
                rootSlot.row = 0;
                rootSlot.columnStart = curColumn;
                columnEvents.add(new ColumnPackage(curColumn, initColumn(rootSlot)));
                curColumn += 1;
            }
        }


        ArrayList<OverlappedEvent> overlappedEventList = new ArrayList<>();

        //compose the event with its parameters
        for (ColumnPackage columnEvent:columnEvents
             ) {
            OverlappedParams param = new OverlappedParams(curColumn, columnEvent.column);
            for (EventSlot event:columnEvent.eventSlots
                 ) {
                OverlappedEvent param_event = new OverlappedEvent(param,event.event);
                overlappedEventList.add(param_event);
            }

        }

        return overlappedEventList;
    }

    private ArrayList<EventSlot> initColumn(EventSlot root){
        ArrayList<EventSlot> columnSlots = new ArrayList<>();
        columnSlots.add(root);

        return columnSlots;
    }

    private class EventSlot{
        public WrapperEvent event;
        int row;
        int columnStart;
    }

    private class ColumnPackage{
        int column;
        ArrayList<EventSlot> eventSlots;

        private ColumnPackage(int column, ArrayList<EventSlot> eventSlots) {
            this.column = column;
            this.eventSlots = eventSlots;
        }
    }

    public class OverlappedParams {
        public int overlapCount;
        public int indexInRow;

        private OverlappedParams(int overlapCount, int indexInRow) {
            this.overlapCount = overlapCount;
            this.indexInRow = indexInRow;
        }
    }

    public class OverlappedEvent {
        public OverlappedParams params;
        public WrapperEvent event;

        private OverlappedEvent(OverlappedParams params, WrapperEvent event) {
            this.params = params;
            this.event = event;
        }
    }

}
