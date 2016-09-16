package org.unimelb.itime.test.david;

import android.databinding.BaseObservable;
import android.util.Log;
import android.view.View;

/**
 * Created by yuhaoliu on 16/09/16.
 */
public class TestViewModel extends BaseObservable {

    public static final String TAG = "TestViewModel";

    public View.OnClickListener onBtnClick(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: fuck ");
            }
        };
    }
}
