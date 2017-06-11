package david.itime_calendar.TestActivities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import david.itime_calendar.R;
import david.itimecalendar.calendar.mudules.agendaview.MonthAgendaView;

import static david.itime_calendar.MainActivity.eventManager;

/**
 * Created by yuhaoliu on 30/05/2017.
 */

public class AgendaViewActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agendaview);
        doTest();
    }

    private void doTest(){
        MonthAgendaView agendaView = (MonthAgendaView) findViewById(R.id.agenda_view);
        agendaView.setDayEventMap(eventManager.getEventsMap());
    }
}
