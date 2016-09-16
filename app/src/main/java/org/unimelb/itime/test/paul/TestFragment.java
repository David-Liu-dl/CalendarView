package org.unimelb.itime.test.paul;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


import org.unimelb.itime.test.R;

import java.util.ArrayList;

/**
 * Created by Paul on 23/08/2016.
 */
public class TestFragment extends Fragment {
    private View root;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (root == null){
            root = inflater.inflate(R.layout.fragment_test,container,false);
        }
        return root;
    }



}
