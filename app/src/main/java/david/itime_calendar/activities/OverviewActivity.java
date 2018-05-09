package david.itime_calendar.activities;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import david.itime_calendar.R;
import david.itime_calendar.fragment.FragmentCalendarAgenda;
import david.itime_calendar.fragment.FragmentCalendarMonthDay;
import david.itime_calendar.fragment.FragmentCalendarTimeslot;
import david.itime_calendar.fragment.FragmentCalendarWeekDay;

/**
 * Created by David Liu on 11/06/2017.
 * lyhmelbourne@gmail.com
 */

public class OverviewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);
        initView();
    }

    void initView() {
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tablayout);
        ViewPager viewPager = (ViewPager) findViewById(R.id.fragment_viewpager);
        viewPager.setAdapter(new SectionPagerAdapter(getSupportFragmentManager()));
        viewPager.getParent().requestDisallowInterceptTouchEvent(true);
        tabLayout.setupWithViewPager(viewPager);
    }

    public class SectionPagerAdapter extends FragmentPagerAdapter {

        SectionPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new FragmentCalendarMonthDay();
                case 1:
                    return new FragmentCalendarWeekDay();
                case 2:
                    return new FragmentCalendarTimeslot();
                case 3:
                    return new FragmentCalendarAgenda();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "DAY";
                case 1:
                    return "WEEK";
                case 2:
                    return "TS";
                case 3:
                    return "AGENDA";
                default:
                    return "N/A";
            }
        }
    }
}