package david.itime_calendar.TestActivities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.Calendar;

import david.itime_calendar.R;
import david.itime_calendar.bean.Event;
import david.itimecalendar.calendar.listeners.ITimeEventInterface;
import david.itimecalendar.calendar.mudules.monthview.DayViewBody;
import david.itimecalendar.calendar.mudules.monthview.MonthView;
import david.itimecalendar.calendar.unitviews.DraggableEventView;
import david.itimecalendar.calendar.util.BaseUtil;
import david.itimecalendar.calendar.util.MyCalendar;

import static david.itime_calendar.MainActivity.eventManager;

/**
 * Created by yuhaoliu on 30/05/2017.
 */

public class TestActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testview);
//        doTest();
    }

}
