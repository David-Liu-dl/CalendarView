package org.unimelb.itime.test.paul;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.unimelb.itime.test.R;
import org.unimelb.itime.test.bean.Contact;
import org.unimelb.itime.test.bean.Event;
import org.unimelb.itime.test.bean.Invitee;
import org.unimelb.itime.test.david.DBManager;
import org.unimelb.itime.vendor.listener.ITimeEventInterface;
import org.unimelb.itime.vendor.weekview.WeekView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Paul on 23/08/2016.
 */
public class WeekViewFragment extends Fragment implements WeekView.OnWeekViewChangeListener {
    private View root;
    private LayoutInflater inflater;
    private ViewGroup container;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (root == null) {
            root = inflater.inflate(R.layout.fragment_week_view, container, false);
            WeekView weekView = (WeekView) root.findViewById(R.id.week_view);
            // simulate Events




//            initDB();
            List<Event> list = DBManager.getInstance(getActivity().getBaseContext()).getAllEvents();
            for (Event ev: list) {
                ev.getInvitee();
            }
            weekView.setEvent(new ArrayList<ITimeEventInterface>(list));

            weekView.setOnClickEventInterface(new WeekView.OnClickEventInterface() {
                @Override
                public void onClickEditEvent(ITimeEventInterface iTimeEventInterface) {
                    List<Invitee> invarr = ((Event)iTimeEventInterface).getInvitee();
                    Contact contact = invarr.get(0).getContact();
                    Log.i("title", iTimeEventInterface.getTitle());
                }
            });
        }
        return root;
    }

    private void initDB(){
        Calendar calendar = Calendar.getInstance();
        List<Event> events = new ArrayList<>();
        List<Contact> contacts = initContact();

        int[] type = {0,1,2};
        int[] status = {0,1};
        long interval = 3600 * 1000;
        int alldayCount = 0;
        for (int i = 1; i < 100; i++) {

            long startTime = calendar.getTimeInMillis();
            long endTime = startTime + interval * (i%30);
            long duration = (endTime - startTime);

            Event event = new Event();
            event.setEventUid("" + i);
            event.setTitle("" + i);
            event.setEventType(i%type.length);
            event.setStatus(i%status.length);
            event.setLocation("here");
            event.setStartTime(startTime);
//            if (i%2 == 0) {
//                event.setHostUserUid("1"); // "1" refers to I am host
//            }else{
//                event.setHostUserUid("2"); // "2" refers to invitee
//            }

            List<Invitee> inviteeList = new ArrayList<>();

            Invitee invitee1 = new Invitee();
            invitee1.setEventUid("" + i);
            invitee1.setContact(contacts.get(0));
            invitee1.setInviteeUid(contacts.get(0).getContactUid());
            inviteeList.add(invitee1);

            Invitee invitee2 = new Invitee();
            invitee2.setEventUid("" + i);
            invitee2.setContact(contacts.get(1));
            invitee2.setInviteeUid(contacts.get(1).getContactUid());
            inviteeList.add(invitee2);

            DBManager.getInstance(getActivity().getBaseContext()).insertInviteeList(inviteeList);
            event.setInvitee(inviteeList);

            long realEnd = endTime;
            long temp = duration;
            while (temp > 3 * 60 * 60 * 1000 ){
                temp = temp/2;
                realEnd -= temp;
            }

            event.setEndTime(realEnd);
            events.add(event);

//            if (duration >= 24 * 3600 * 1000 && alldayCount < 3){
//                String title = "All day";
//                for (int j = 0; j < 4; j++) {
//                    Event event_clone = new Event();
//                    event_clone.setTitle(title);
//                    event_clone.setEventType(0);
//                    event_clone.setStatus(0);
//                    event_clone.setStartTime(startTime);
//                    event_clone.setEndTime(endTime);
//                    event_clone.setLocation("here");
////                    event_clone.setInviteesUrls("");
//                    title = title + " all day";
//                }
//                alldayCount = 0;
//            }

            calendar.setTimeInMillis(endTime);

        }

        DBManager.getInstance(getActivity().getBaseContext()).insertEventList(events);
    }

    private List<Contact> initContact(){
        List<Contact> contacts = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            Contact contact = new Contact(""+i, "http://img.zybus.com/uploads/allimg/131213/1-131213111353.jpg", "name " + i);
            contacts.add(contact);
            DBManager.getInstance(getActivity().getBaseContext()).insertContact(contact);
        }

        return contacts;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }


    @Override
    public void onWeekChanged(Calendar calendar) {
        Log.d("day", String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
    }
}
