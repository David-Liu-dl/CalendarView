package org.unimelb.itime.test.paul;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;


import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.unimelb.itime.test.R;
import org.unimelb.itime.test.bean.Event;
import org.unimelb.itime.test.david.DBManager;
import org.unimelb.itime.test.david.EventManager;
import org.unimelb.itime.vendor.dayview.FlexibleLenViewBody;
import org.unimelb.itime.vendor.eventview.DayDraggableEventView;
import org.unimelb.itime.vendor.weekview.WeekView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class PaulActivity extends AppCompatActivity {

    private static final int PICK_PHOTO = 1;
    private static final String TAG = "MyAPP";
    private List<String> mResults;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paul);
        loadData();

        Button back = (Button) findViewById(R.id.back);
        final WeekView weekView = (WeekView) findViewById(R.id.week_view);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weekView.backToToday();
            }
        });
        weekView.setEventClassName(Event.class);
        weekView.setOnBodyOuterListener(new FlexibleLenViewBody.OnBodyListener() {
            @Override
            public boolean isDraggable(DayDraggableEventView eventView) {
                return false;
            }

            @Override
            public void onEventCreate(DayDraggableEventView eventView) {
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(eventView.getStartTimeM());
                Log.i(TAG, "cal: " + cal.getTime());
            }

            @Override
            public void onEventClick(DayDraggableEventView eventView) {
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(eventView.getEvent().getStartTime());
                Log.i(TAG, "onEventClick: " + cal.getTime());
//                Log.i(TAG, "click2: " + " title: " + eventView.getEvent().getTitle());
//                weekView.reloadEvents();
            }

            @Override
            public void onEventDragStart(DayDraggableEventView eventView) {

            }

            @Override
            public void onEventDragging(DayDraggableEventView eventView, int x, int y) {

            }

            @Override
            public void onEventDragDrop(DayDraggableEventView eventView) {
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(eventView.getStartTimeM());
                Log.i(TAG, "onEventDragDrop: " + cal.getTime());
//                weekView.reloadEvents();
            }

        });

        weekView.setDayEventMap(EventManager.getInstance().getEventsMap());
    }

    private void showResult(ArrayList<String> paths) {
        if (mResults == null) {
            mResults = new ArrayList<String>();

        }
        mResults.clear();
        mResults.addAll(paths);

    }


    private void loadData() {
        List<Event> allEvents = DBManager.getInstance(getApplicationContext()).getAllEvents();
//        EventManager.getInstance().getEventsMap().clearPackage();
        for (Event event : allEvents
                ) {
            EventManager.getInstance().addEvent(event);
        }

    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
//        client.connect();
//        Action viewAction = Action.newAction(
//                Action.TYPE_VIEW, // TODO: choose an action type.
//                "Paul Page", // TODO: Define a title for the content shown.
//                // TODO: If you have web page content that matches this app activity's content,
//                // make sure this auto-generated web page URL is correct.
//                // Otherwise, set the URL to null.
//                Uri.parse("http://host/path"),
//                // TODO: Make sure this auto-generated app URL is correct.
//                Uri.parse("android-app://org.unimelb.itime.test.paul/http/host/path")
//        );
//        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
//        Action viewAction = Action.newAction(
//                Action.TYPE_VIEW, // TODO: choose an action type.
//                "Paul Page", // TODO: Define a title for the content shown.
//                // TODO: If you have web page content that matches this app activity's content,
//                // make sure this auto-generated web page URL is correct.
//                // Otherwise, set the URL to null.
//                Uri.parse("http://host/path"),
//                // TODO: Make sure this auto-generated app URL is correct.
//                Uri.parse("android-app://org.unimelb.itime.test.paul/http/host/path")
//        );
//        AppIndex.AppIndexApi.end(client, viewAction);
//        client.disconnect();
    }
}
