package org.unimelb.itime.test.paul;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.lling.photopicker.PhotoPickerActivity;

import org.unimelb.itime.test.R;
import org.unimelb.itime.vendor.helper.MyCalendar;
import org.unimelb.itime.vendor.weekview.WeekView;
import org.unimelb.itime.vendor.weekview.WeekViewBody;
import org.unimelb.itime.vendor.weekview.WeekViewHeader;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class PaulActivity extends AppCompatActivity {

    WeekViewFragment weekViewFragment;
    TestFragment testFragment;
    TimeSlotFragment timeSlotFragment;
    private static final int PICK_PHOTO = 1;
    private List<String> mResults;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paul);


//        WeekView.OnWeekViewChangeListener onWeekViewChangeListener = new WeekView.OnWeekViewChangeListener() {
//            @Override
//            public void onWeekChanged(Calendar calendar) {
//                Log.i("day is ", String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
//            }
//        };

        testFragment = new TestFragment();
        getFragmentManager().beginTransaction().add(R.id.fragment,testFragment).commit();
//        weekViewFragment = new WeekViewFragment();
//        timeSlotFragment = new TimeSlotFragment();

//        getFragmentManager().beginTransaction().add(R.id.fragment, weekViewFragment).commit();
//        getFragmentManager().beginTransaction().add(R.id.fragment,timeSlotFragment).commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_PHOTO){
            if(resultCode == RESULT_OK){
                ArrayList<String> result = data.getStringArrayListExtra(PhotoPickerActivity.KEY_RESULT);
                showResult(result);
            }
        }
    }

    private void showResult(ArrayList<String> paths){
        if(mResults == null){
            mResults = new ArrayList<String>();

        }
        mResults.clear();
        mResults.addAll(paths);

    }



}
