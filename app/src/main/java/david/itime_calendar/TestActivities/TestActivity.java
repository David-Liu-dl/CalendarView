package david.itime_calendar.TestActivities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.developer.paul.itimerecycleviewgroup.ITimeAdapter;
import com.developer.paul.itimerecycleviewgroup.ITimeRecycleViewGroup;

import java.util.ArrayList;
import java.util.Calendar;

import david.itime_calendar.R;
import david.itime_calendar.bean.TimeSlot;
import david.itimecalendar.calendar.ui.unitviews.DraggableTimeSlotView;
import david.itimecalendar.calendar.wrapper.WrapperTimeSlot;

/**
 * Created by yuhaoliu on 30/05/2017.
 */

public class TestActivity extends AppCompatActivity {

    ITimeRecycleViewGroup recycleViewGroup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testview);

        recycleViewGroup = (ITimeRecycleViewGroup) findViewById(R.id.awesome_view);
        final TestAdapter adapter = new TestAdapter();

        recycleViewGroup.setOnScrollListener(new ITimeRecycleViewGroup.OnScroll() {
            @Override
            public void onPageSelected(View view) {
//                adapter.notifyDataSetChanged();
            }

            @Override
            public void onHorizontalScroll(int dx, int preOffsetX) {

            }

            @Override
            public void onVerticalScroll(int dy, int preOffsetY) {

            }
        });
        recycleViewGroup.setAdapter(adapter);

    }


    public class TestAdapter extends ITimeAdapter {

        @Override
        public View onCreateViewHolder() {
            LinearLayout a =  new LinearLayout(getApplicationContext());
            a.setOrientation(LinearLayout.VERTICAL);
            return a;
        }

        @Override
        public void onBindViewHolder(View item, int index) {

            LinearLayout linearLayout = (LinearLayout) item;
            linearLayout.setBackgroundColor(index%2 == 0 ? Color.RED : Color.BLUE);
            linearLayout.removeAllViews();

            WrapperTimeSlot wrapperTimeSlot = new WrapperTimeSlot(slots.get(0));
            DraggableTimeSlotView textView = new DraggableTimeSlotView(getApplicationContext(), wrapperTimeSlot, false);
            textView.setBackgroundColor(Color.WHITE);
            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 300);
            linearLayout.addView(textView,param);

            linearLayout.requestLayout();
        }
    }

    final ArrayList<TimeSlot> slots = initSlots(new ArrayList<TimeSlot>());

    private ArrayList<TimeSlot> initSlots(ArrayList<TimeSlot> slots){
        Calendar cal = Calendar.getInstance();
        long startTime = cal.getTimeInMillis();
        long duration = 3600*1000;
        long dayInterval = 3 * 3600 * 1000;
        for (int i = 0; i < 3; i++) {
            TimeSlot slot = new TimeSlot();
            slot.setStartTime(startTime);
            slot.setEndTime(startTime+duration);
            slot.setRecommended(true);
            slot.setIsAllDay(false);
            slots.add(slot);

            startTime += dayInterval;
        }

        return slots;
    }
}
