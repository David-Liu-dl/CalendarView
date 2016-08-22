package org.unimelb.itime.test.paul;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import org.unimelb.itime.test.R;
import org.unimelb.itime.vendor.weekview.WeekViewBody;

public class PaulActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paul);
        WeekViewBody weekViewBody = (WeekViewBody)findViewById(R.id.week_view_body);
        weekViewBody.setNumOfHourShowInScreen(15);
    }
}
