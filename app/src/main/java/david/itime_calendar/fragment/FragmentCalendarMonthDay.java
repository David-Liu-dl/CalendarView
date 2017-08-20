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
import david.itimecalendar.calendar.listeners.ITimeCalendarMonthDayViewListener;
import david.itimecalendar.calendar.listeners.ITimeEventInterface;
import david.itimecalendar.calendar.ui.monthview.DayViewBody;
import david.itimecalendar.calendar.ui.monthview.MonthView;
import david.itimecalendar.calendar.ui.unitviews.DraggableEventView;
import david.itimecalendar.calendar.util.MyCalendar;

/**
 * Created by yuhaoliu on 8/06/2017.
 */

public class FragmentCalendarMonthDay extends Fragment {
    private View root;
    private EventManager eventManager;
    private MonthView monthDayView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_calendar_monthday, container, false);
        eventManager = EventManager.getInstance();
        initView();
        return root;
    }

    private void initView(){
        monthDayView = (MonthView) root.findViewById(R.id.month_view);
        //Set the data source with format of ITimeEventPackageInterface
        //ITimeEventPackageInterface is composed by two parts:
        //  1: regular events. 2: repeated events.
        monthDayView.setEventPackage(eventManager.getEventsMap());
        monthDayView.setITimeCalendarMonthDayViewListener(new MonthViewBodyListener());
    }

    private class MonthViewBodyListener implements ITimeCalendarMonthDayViewListener{

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
        public void onEventDragging(DraggableEventView eventView, MyCalendar curAreaCal, int touchX, int touchY, int viewX, int viewY, String locationTime) {

        }

        @Override
        public void onEventDragDrop(DraggableEventView draggableEventView) {

        }

        @Override
        public void onEventDragEnd(DraggableEventView eventView) {

        }

        @Override
        public void onAllDayEventClick(ITimeEventInterface iTimeEventInterface) {

        }

        @Override
        public void onDateChanged(Date date) {

        }

        @Override
        public void onHeaderFlingDateChanged(Date newestDate) {

        }
    }
}
