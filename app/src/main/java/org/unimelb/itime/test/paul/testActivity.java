package org.unimelb.itime.test.paul;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import org.unimelb.itime.test.R;
import org.unimelb.itime.vendor.helper.MyCalendar;
import org.unimelb.itime.vendor.timeslotview.WeekTimeSlotViewHeader;

import java.util.Calendar;

/**
 * Created by Paul on 31/08/2016.
 */
public class testActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        WeekTimeSlotViewHeader weekTimeSlotViewHeader = (WeekTimeSlotViewHeader) findViewById(R.id.test_timeslot_header);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH,27);
        MyCalendar myCalendar = new MyCalendar(calendar);
        weekTimeSlotViewHeader.setMyCalendar(myCalendar);
    }
}
