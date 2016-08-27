package org.unimelb.itime.test.david;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import org.unimelb.itime.test.R;

public class DavidActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_david);

        ContactsPageFragment pageF = new ContactsPageFragment();
        CalendarMonthDayFragment monthDayFragment = new CalendarMonthDayFragment();
        getFragmentManager().beginTransaction().add(R.id.david_fragment, pageF).commit();

    }
}
