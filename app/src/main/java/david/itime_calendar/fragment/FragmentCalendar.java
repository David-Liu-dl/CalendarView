package david.itime_calendar.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import david.itime_calendar.R;
import david.itime_calendar.manager.EventManager;

/**
 * Created by David Liu on 8/06/2017.
 * lyhmelbourne@gmail.com
 */


public class FragmentCalendar extends Fragment {
    private EventManager eventManager;

    private FragmentCalendarMonthDay monthDayFragment;
    private FragmentCalendarWeekDay weekFragment;
    private FragmentCalendarTimeslot timeslotFragment;
    private FragmentCalendarAgenda agendaFragment;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        eventManager = EventManager.getInstance();
        return inflater.inflate(R.layout.fragment_calendar, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initCalendars();
    }

    public void initCalendars(){
        monthDayFragment = new FragmentCalendarMonthDay();
        this.getChildFragmentManager().beginTransaction().add(R.id.child_fragment_container, monthDayFragment).commit();
    }

    public void toView(String name){
        if (name.equals(FragmentCalendarMonthDay.class.getSimpleName())){
            monthDayFragment = monthDayFragment == null ? new FragmentCalendarMonthDay() : monthDayFragment;
            this.getChildFragmentManager().beginTransaction().replace(R.id.child_fragment_container, monthDayFragment).commit();
        }else if (name.equals(FragmentCalendarTimeslot.class.getSimpleName())){
            timeslotFragment = timeslotFragment == null ? new FragmentCalendarTimeslot() : timeslotFragment;
            this.getChildFragmentManager().beginTransaction().replace(R.id.child_fragment_container, timeslotFragment).commit();
        }else if (name.equals(FragmentCalendarWeekDay.class.getSimpleName())){
            weekFragment = weekFragment == null ? new FragmentCalendarWeekDay() : weekFragment;
            this.getChildFragmentManager().beginTransaction().replace(R.id.child_fragment_container, weekFragment).commit();
        }else if (name.equals(FragmentCalendarAgenda.class.getSimpleName())){
            agendaFragment = agendaFragment == null ? new FragmentCalendarAgenda() : agendaFragment;
            this.getChildFragmentManager().beginTransaction().replace(R.id.child_fragment_container, agendaFragment).commit();
        }
    }
}
