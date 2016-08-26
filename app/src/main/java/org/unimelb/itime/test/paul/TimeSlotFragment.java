package org.unimelb.itime.test.paul;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.unimelb.itime.test.R;
import org.unimelb.itime.vendor.eventview.Event;
import org.unimelb.itime.vendor.timeslotview.WeekTimeSlotView;
import org.unimelb.itime.vendor.weekview.WeekView;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Paul on 26/08/2016.
 */
public class TimeSlotFragment extends Fragment implements WeekView.OnWeekViewChangeListener{
    private View root;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root= inflater.inflate(R.layout.fragment_time_slot,container,false);
        WeekTimeSlotView weekTimeSlotView = (WeekTimeSlotView) root.findViewById(R.id.week_timeslot_view);
        // simulate timeSlots
        ArrayList<Long> simulateTimeSlots = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH,27);
        calendar.set(Calendar.HOUR_OF_DAY,10);
        calendar.set(Calendar.MINUTE,30);
        simulateTimeSlots.add(calendar.getTime().getTime());
        weekTimeSlotView.setTimeSlots(simulateTimeSlots,60);

        // simulate Events
        ArrayList<Event> eventArrayList = new ArrayList<>();
        Calendar calendar1 = Calendar.getInstance();
        calendar1.set(Calendar.DAY_OF_MONTH,26);
        calendar1.set(Calendar.HOUR_OF_DAY, 13);
        calendar1.set(Calendar.MINUTE,0);
        Event event = new Event();
        event.setTitle("itime meeting");
        event.setStartTime(calendar1.getTimeInMillis());
        event.setEndTime(calendar1.getTimeInMillis() + 3600000*2);
        event.setEventType(Event.Type.GROUP);
        event.setStatus(Event.Status.COMFIRM);
        eventArrayList.add(event);
        weekTimeSlotView.setEvent(eventArrayList);
        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        WeekTimeSlotView weekTimeSlotView = (WeekTimeSlotView) root.findViewById(R.id.week_timeslot_view);
//        ArrayList<Long> simulateTimeSlots = new ArrayList<>();
//        simulateTimeSlots.add(Long.parseLong("1472274935901"));
//        weekTimeSlotView.setTimeSlots(simulateTimeSlots,60);

    }


    @Override
    public void onWeekChanged(Calendar calendar) {
        Log.d("day", String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
    }
}
