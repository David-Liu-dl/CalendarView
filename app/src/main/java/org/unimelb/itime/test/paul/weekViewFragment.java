package org.unimelb.itime.test.paul;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.unimelb.itime.test.R;

/**
 * Created by Paul on 23/08/2016.
 */
public  class WeekViewFragment extends Fragment {
    private View root;
    private LayoutInflater inflater;
    private ViewGroup container;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (root == null)
            root= inflater.inflate(R.layout.fragment_week_view,container,false);
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
//        Log.i("onStart","here here");
    }

    @Override
    public void onStop() {
        super.onStop();
//        Log.i("onStop","here here");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        Log.i("onDestroy","here here");
    }

    @Override
    public void onResume() {
        super.onResume();
//        Log.i("onResume","here here");
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        weekViewPager.initAll();
        Button button = (Button)root.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((PaulActivity)getActivity()).goToFragment2();
            }
        });
    }



}
