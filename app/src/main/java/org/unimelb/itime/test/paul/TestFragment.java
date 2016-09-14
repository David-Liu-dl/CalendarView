package org.unimelb.itime.test.paul;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.lling.photopicker.PhotoPickerActivity;

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

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Button btn = (Button) root.findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), PhotoPickerActivity.class);
                int selectedMode = PhotoPickerActivity.MODE_MULTI;
                intent.putExtra(PhotoPickerActivity.EXTRA_SELECT_MODE, selectedMode);
                int maxNum = 9;
                intent.putExtra(PhotoPickerActivity.EXTRA_MAX_MUN, maxNum);
                intent.putExtra(PhotoPickerActivity.EXTRA_SHOW_CAMERA,true);

                getActivity().startActivityForResult(intent,1);
            }
        });
    }


}
