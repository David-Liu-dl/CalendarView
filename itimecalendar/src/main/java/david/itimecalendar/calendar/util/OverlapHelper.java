package david.itimecalendar.calendar.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import david.itimecalendar.calendar.listeners.ITimeComparable;

/**
 * Created by David Liu on 18/08/16.
 * ${COMPANY}
 * lyhmelbourne@gmail.com
 */
public class OverlapHelper <I extends ITimeComparable<I>> {
    private final long overlapTolerance = (15/2) * 60 * 1000;

    private List<I> eventModules = new ArrayList<>();
    private List<ArrayList<OverlappedEvent>> groupedOlpEventList = new ArrayList<>();

    public List<ArrayList<OverlappedEvent>> computeOverlapXObject(List<I> objs){
        this.eventModules = objs;
        groupedOlpEventList.clear();
        // sort item by start time first
        this.sortEvent();
        // get overlapped Groups
        ArrayList<ArrayList<I>> overlapEventGroups = divideOverlapGroup();
        // compute each item X in every group in overlapped groups
        for (ArrayList<I> list: overlapEventGroups
                ) {
            if (list.size() > 1){
                groupedOlpEventList.add(this.computeEventXPstInGroup(list));
            }else {
                //<overlapCount, indexInRow>
                OverlappedParams params = new OverlappedParams(1,0);
                //<params, item>
                OverlappedEvent param_event = new OverlappedEvent(params, list.get(0));
                ArrayList<OverlappedEvent> group = new ArrayList<>();
                group.add(param_event);
                groupedOlpEventList.add(group);
            }
        }

        return groupedOlpEventList;
    }

    public boolean  isConflicted(List<I> objs, I compare){
        long compareStartTime = compare.getStartTime();
        for (I obj:objs
             ) {
            long comparedStartTime = obj.getStartTime();
            long comparedEndTime = obj.getEndTime();
            if ((comparedStartTime <= compareStartTime) && (comparedEndTime > compareStartTime)){
                return true;
            }
        }
        return false;
    }
    
    private void sortEvent(){
        Collections.sort(eventModules);
    }

    private ArrayList<ArrayList<I>> divideOverlapGroup(){
        ArrayList<ArrayList<I>> overLapEventGroups = new ArrayList<>();

        // get today 00:00 milliseconds
        long startFlag = 0;
        long endFlag = 0;//-1 means 00:00 within today

        for (I wrapper:eventModules
                ) {
//            ITimeEventInterface item = wrapper.getEvent();
//            long startTime = item.getStartTime();
//            long endTime = item.getEndTime();

            long startTime = wrapper.getStartTime();
            long endTime = wrapper.getEndTime();

            if (startTime >= (endFlag - overlapTolerance)){
                //means no overlap with previous group, then create new group
                ArrayList<I> new_group = new ArrayList<>();
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

    private ArrayList<OverlappedEvent> computeEventXPstInGroup(ArrayList<I> group) {
        ArrayList<ColumnPackage> columnEvents = new ArrayList<>();
        int curColumn = 0;

        //iterate all events
        //init the very first item as root.
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

            long currentEventStartTime = group.get(i).getStartTime();
            boolean foundRoot = false;

            //finding the root for current item
            for (ColumnPackage columnEvent:columnEvents
                 ) {
                //compare with last item in column item
                if (currentEventStartTime >= (columnEvent.eventSlots.get(columnEvent.eventSlots.size() -1).event.getEndTime() - overlapTolerance)){
                    EventSlot childSlot = new EventSlot();
                    childSlot.event = group.get(i);
                    childSlot.row = columnEvent.eventSlots.size();
                    childSlot.columnStart = columnEvent.column;
                    columnEvent.eventSlots.add(childSlot);
                    foundRoot = true;
                    break;
                }
            }

            //if root not found, this item is a new root for new column.
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

        //compose the item with its parameters
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
        public I event;
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
        public I item;

        private OverlappedEvent(OverlappedParams params, I item) {
            this.params = params;
            this.item = item;
        }
    }
}
