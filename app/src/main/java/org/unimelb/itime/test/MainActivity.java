package org.unimelb.itime.test;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import org.unimelb.itime.test.david.DavidActivity;
import org.unimelb.itime.test.david.YinActivity;
import org.unimelb.itime.test.paul.PaulActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    void initView(){
        Button paulBtn = (Button)findViewById(R.id.btn_paul);
        paulBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, PaulActivity.class);
                startActivity(intent);
            }
        });

        Button davidBtn = (Button)findViewById(R.id.btn_david);
        davidBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, DavidActivity.class);
                startActivity(intent);
            }
        });

        Button yinBtn = (Button)findViewById(R.id.btn_yin);
        yinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, YinActivity.class);
                startActivity(intent);
            }
        });
    }


}
