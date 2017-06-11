package david.itime_calendar.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import david.itime_calendar.EventManager;
import david.itime_calendar.R;

/**
 * Created by yuhaoliu on 8/06/2017.
 */

public class FragmentCalendar extends Fragment {
    private EventManager eventManager;

    private FragmentCalendarMonthDay monthDayFragment;
    private FragmentCalendarAgenda agendaFragment;
    private FragmentCalendarWeekDay weekFragment;

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
        getFragmentManager().beginTransaction().add(R.id.fragment_container, monthDayFragment).commit();
    }

    public void toView(String name){
        if (name.equals(FragmentCalendarMonthDay.class.getSimpleName())){
            monthDayFragment = new FragmentCalendarMonthDay();
            getFragmentManager().beginTransaction().replace(R.id.fragment_container, monthDayFragment).commit();
        }else if (name.equals(FragmentCalendarWeekDay.class.getSimpleName())){
            weekFragment = new FragmentCalendarWeekDay();
            getFragmentManager().beginTransaction().replace(R.id.fragment_container, weekFragment).commit();
        }else if (name.equals(FragmentCalendarAgenda.class.getSimpleName())){
            agendaFragment = new FragmentCalendarAgenda();
            getFragmentManager().beginTransaction().replace(R.id.fragment_container, agendaFragment).commit();
        }
    }
}
