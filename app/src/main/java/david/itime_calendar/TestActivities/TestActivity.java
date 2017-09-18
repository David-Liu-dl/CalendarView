package david.itime_calendar.TestActivities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.developer.paul.itimerecycleviewgroup.ITimeAdapter;
import com.developer.paul.itimerecycleviewgroup.ITimeRecycleViewGroup;

import david.itime_calendar.R;

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
        TestAdapter adapter = new TestAdapter();
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
            Log.i("testac", "onBindViewHolder: " + index);
            LinearLayout linearLayout = (LinearLayout) item;
            linearLayout.setBackgroundColor(index%2 == 0 ? Color.RED : Color.BLUE);
            linearLayout.removeAllViews();
            for (int i = 0; i < index; i++) {
                TextView a = new TextView(getApplicationContext());
                a.setText("---" + i);
                a.setTextColor(Color.WHITE);
                linearLayout.addView(a);
            }



            linearLayout.requestLayout();
        }
    }

}
