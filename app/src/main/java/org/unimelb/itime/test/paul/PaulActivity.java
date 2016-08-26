package org.unimelb.itime.test.paul;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import org.unimelb.itime.test.R;

public class PaulActivity extends AppCompatActivity {

    WeekViewFragment weekViewFragment;
    TestFragment testFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paul);

//        testFragment = new TestFragment();
//        getFragmentManager().beginTransaction().add(R.id.fragment,testFragment).commit();
        weekViewFragment = new WeekViewFragment();
//        getFragmentManager().beginTransaction().replace(R.id.fragment, weekViewFragment).commit();
        getFragmentManager().beginTransaction().add(R.id.fragment,weekViewFragment).commit();
    }


    public void goToFragment2(){

//        getFragmentManager().beginTransaction().replace(R.id.fragment, testFragment).commit();
        getFragmentManager().beginTransaction().hide(weekViewFragment).commit();
        if (testFragment == null || !testFragment.isAdded()){
            testFragment = new TestFragment();
            getFragmentManager().beginTransaction().add(R.id.fragment,testFragment).commit();
        }
        getFragmentManager().beginTransaction().show(testFragment).commit();
//        getFragmentManager().beginTransaction().remove(weekViewFragment).commit();
    }

    public void goToFragment1(){
        if(testFragment != null && testFragment.isAdded()){
            getFragmentManager().beginTransaction().hide(testFragment).commit();
        }
        if (weekViewFragment == null || !weekViewFragment.isAdded()){
            weekViewFragment = new WeekViewFragment();
            getFragmentManager().beginTransaction().add(R.id.fragment,weekViewFragment).commit();
        }
        getFragmentManager().beginTransaction().show(weekViewFragment).commit();
//        getFragmentManager().beginTransaction().replace(R.id.fragment, weekViewFragment).commit();

//        getFragmentManager().beginTransaction().hide(testFragment).commit();
//        getFragmentManager().beginTransaction().remove(testFragment).commit();
//        getFragmentManager().beginTransaction().add(R.id.fragment,weekViewFragment).commit();

    }
}
