package david.itime_calendar.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import java.util.Date;

import david.itime_calendar.EventManager;
import david.itime_calendar.R;
import david.itimecalendar.calendar.listeners.ITimeCalendarTimeslotViewListener;
import david.itimecalendar.calendar.ui.monthview.DayViewBody;
import david.itimecalendar.calendar.ui.weekview.TimeSlotView;
import david.itimecalendar.calendar.ui.unitviews.DraggableTimeSlotView;
import david.itimecalendar.calendar.ui.unitviews.RecommendedSlotView;
import david.itimecalendar.calendar.util.MyCalendar;

/**
 * Created by yuhaoliu on 8/06/2017.
 */

public class FragmentCalendarTimeslot extends Fragment {
    private View root;
    private EventManager eventManager;
    private TimeSlotView timeSlotView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_calendar_timeslot, container, false);
        eventManager = EventManager.getInstance();
        initView();
        return root;
    }

    private void initView(){
        timeSlotView = (TimeSlotView) root.findViewById(R.id.timeslot_view);
        //Set the data source with format of ITimeEventPackageInterface
        //ITimeEventPackageInterface is composed by two parts:
        //  1: regular events. 2: repeated events.
        timeSlotView.setEventPackage(eventManager.getEventsMap());
        timeSlotView.setITimeCalendarTimeslotViewListener(new TimeslotViewBodyListener());
    }

    private class TimeslotViewBodyListener implements ITimeCalendarTimeslotViewListener{

        @Override
        public void onAllDayRcdTimeslotClick(long l) {

        }

        @Override
        public void onAllDayTimeslotClick(DraggableTimeSlotView draggableTimeSlotView) {

        }

        @Override
        public void onTimeSlotCreate(DraggableTimeSlotView draggableTimeSlotView) {

        }

        @Override
        public void onTimeSlotClick(DraggableTimeSlotView draggableTimeSlotView) {

        }

        @Override
        public void onRcdTimeSlotClick(RecommendedSlotView recommendedSlotView) {

        }

        @Override
        public void onTimeSlotDragStart(DraggableTimeSlotView draggableTimeSlotView) {

        }

        @Override
        public void onTimeSlotDragging(DraggableTimeSlotView draggableTimeSlotView, MyCalendar curAreaCal, int x, int y, String locationTime) {

        }


        @Override
        public void onTimeSlotDragDrop(DraggableTimeSlotView draggableTimeSlotView, long l, long l1) {

        }

        @Override
        public void onTimeSlotDragEnd(DraggableTimeSlotView draggableTimeSlotView) {

        }

        @Override
        public void onTimeSlotEdit(DraggableTimeSlotView draggableTimeSlotView) {

        }

        @Override
        public void onTimeSlotDelete(DraggableTimeSlotView draggableTimeSlotView) {

        }

        @Override
        public void onDateChanged(Date date) {

        }
    }
}
