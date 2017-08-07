package david.itime_calendar.TestActivities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import java.util.Date;

import david.itime_calendar.R;
import david.itimecalendar.calendar.ui.agendaview.MonthAgendaView;

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
        final MonthAgendaView agendaView = (MonthAgendaView) findViewById(R.id.agenda_view);
        agendaView.setDayEventMap(eventManager.getEventsMap());

        TextView todayBtn = (TextView) findViewById(R.id.today);
        todayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                agendaView.scrollToDate(new Date());
            }
        });
    }
}
