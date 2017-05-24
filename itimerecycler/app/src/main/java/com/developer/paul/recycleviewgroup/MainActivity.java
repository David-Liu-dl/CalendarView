package com.developer.paul.recycleviewgroup;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    private String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecycleViewGroup recycleViewGroup = (RecycleViewGroup) findViewById(R.id.recycleViewGroup);
        recycleViewGroup.setDisableCellScroll(true);
        recycleViewGroup.setScrollInterface(new RecycleViewGroup.ScrollInterface() {
            @Override
            public void getMovePercent(float percent) {
                Log.i(TAG, "getMovePercent: " + percent);
            }
        });


    }
}
