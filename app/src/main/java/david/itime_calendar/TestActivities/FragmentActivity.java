package david.itime_calendar.TestActivities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import david.itime_calendar.MainActivity;
import david.itime_calendar.R;
import david.itime_calendar.fragment.FragmentCalendar;
import david.itime_calendar.fragment.FragmentCalendarAgenda;
import david.itime_calendar.fragment.FragmentCalendarMonthDay;
import david.itime_calendar.fragment.FragmentCalendarWeekDay;

/**
 * Created by yuhaoliu on 11/06/2017.
 */

public class FragmentActivity extends AppCompatActivity {
    FragmentCalendar fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);
        initView();
        fragment = new FragmentCalendar();
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container,fragment, FragmentCalendar.class.getSimpleName()).commit();
    }

    void initView() {
        Button dayBtn = (Button) findViewById(R.id.btn_day);
        dayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragment.toView(FragmentCalendarMonthDay.class.getSimpleName());
            }
        });

        Button weekBtn = (Button) findViewById(R.id.btn_week);
        weekBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragment.toView(FragmentCalendarWeekDay.class.getSimpleName());
            }
        });

        Button agendaBtn = (Button) findViewById(R.id.btn_agenda);
        agendaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragment.toView(FragmentCalendarAgenda.class.getSimpleName());
            }
        });
    }
}
