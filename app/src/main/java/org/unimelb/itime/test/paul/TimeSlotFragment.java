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
import org.unimelb.itime.vendor.listener.ITimeEventInterface;
import org.unimelb.itime.vendor.timeslot.TimeSlotView;
import org.unimelb.itime.vendor.timeslotview.WeekTimeSlotView;
import org.unimelb.itime.vendor.weekview.WeekView;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Paul on 26/08/2016.
 */
public class TimeSlotFragment extends Fragment implements WeekTimeSlotView.OnTimeSlotWeekViewChangeListener{
    private View root;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root= inflater.inflate(R.layout.fragment_time_slot,container,false);
        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();

    }

    @Override
    public void onWeekChanged(Calendar calendar) {
        Log.i("onWeekChanged", String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
    }

    private void initData()
    {
        WeekTimeSlotView weekTimeSlotView = (WeekTimeSlotView) root.findViewById(R.id.week_timeslot_view);
//         simulate timeSlots
        Map<Long,Boolean> simulateTimeSlots = new HashMap<>();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH,8);
        calendar.set(Calendar.HOUR_OF_DAY,1);
        calendar.set(Calendar.MINUTE,0);
        simulateTimeSlots.put(calendar.getTime().getTime(),true);
        simulateTimeSlots.put(calendar.getTimeInMillis()+3600000*24,true);
        simulateTimeSlots.put(calendar.getTimeInMillis() + 3600000*24*3,true);
        simulateTimeSlots.put(calendar.getTimeInMillis() + 3600000*24*6, true);
        simulateTimeSlots.put(calendar.getTimeInMillis() + 3600000*24*8,true);
        simulateTimeSlots.put(calendar.getTimeInMillis() - 3600000*24*6, true);
        weekTimeSlotView.setTimeSlots(simulateTimeSlots,60);

        // simulate Events
        ArrayList<ITimeEventInterface> eventArrayList = new ArrayList<>();
        Calendar calendar1 = Calendar.getInstance();
        calendar1.set(Calendar.DAY_OF_MONTH,7);
        calendar1.set(Calendar.HOUR_OF_DAY, 5);
        calendar1.set(Calendar.MINUTE,0);

        TestEvent testEvent = new TestEvent();
        testEvent.setTitle("itime meeting");
        testEvent.setStartTime(calendar1.getTimeInMillis());
        testEvent.setEndTime(calendar1.getTimeInMillis() + 3600000*2);
        testEvent.setEventType(1);
        testEvent.setStatus(5);
        eventArrayList.add(testEvent);
        weekTimeSlotView.setEvent(eventArrayList);


        weekTimeSlotView.setOnTimeSlotWeekViewChangeListener(new WeekTimeSlotView.OnTimeSlotWeekViewChangeListener() {
            @Override
            public void onWeekChanged(Calendar calendar) {
                Log.i("onWeekChanged", String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
            }
        });

        weekTimeSlotView.setOnTimeSlotClickListener(new WeekTimeSlotView.OnTimeSlotClickListener(){
            @Override
            public void onTimeSlotClick(long time) {
                Log.i("onclick timeSlot","on click");
            }
        });

    }
}
