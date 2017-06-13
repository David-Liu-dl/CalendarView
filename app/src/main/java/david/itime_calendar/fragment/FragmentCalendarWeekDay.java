package david.itime_calendar.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import david.itime_calendar.EventManager;
import david.itime_calendar.R;
import david.itimecalendar.calendar.listeners.ITimeEventInterface;
import david.itimecalendar.calendar.ui.monthview.DayViewBody;
import david.itimecalendar.calendar.ui.weekview.WeekView;
import david.itimecalendar.calendar.ui.unitviews.DraggableEventView;
import david.itimecalendar.calendar.util.MyCalendar;

/**
 * Created by yuhaoliu on 8/06/2017.
 */

public class FragmentCalendarWeekDay extends Fragment {
    private View root;
    private EventManager eventManager;
    private WeekView weekView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_calendar_weekday, container, false);
        eventManager = EventManager.getInstance();
        initView();
        return root;
    }

    private void initView(){
        weekView = (WeekView) root.findViewById(R.id.week_view);
        //Set the data source with format of ITimeEventPackageInterface
        //ITimeEventPackageInterface is composed by two parts:
        //  1: regular events. 2: repeated events.
        weekView.setEventPackage(eventManager.getEventsMap());
        weekView.setOnBodyEventListener(new WeekViewBodyListener());
    }

    private class WeekViewBodyListener implements DayViewBody.OnViewBodyEventListener{

        @Override
        public boolean isDraggable(DraggableEventView draggableEventView) {
            return false;
        }

        @Override
        public void onEventCreate(DraggableEventView draggableEventView) {

        }

        @Override
        public void onEventClick(DraggableEventView draggableEventView) {

        }

        @Override
        public void onEventDragStart(DraggableEventView draggableEventView) {

        }

        @Override
        public void onEventDragging(DraggableEventView draggableEventView, MyCalendar myCalendar, int i, int i1) {

        }

        @Override
        public void onEventDragDrop(DraggableEventView draggableEventView) {

        }

        @Override
        public void onAllDayEventClick(ITimeEventInterface iTimeEventInterface) {

        }
    }
}
