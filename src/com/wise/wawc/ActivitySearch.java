package com.wise.wawc;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class ActivitySearch extends Activity{
    View view = null;
    //你要做什么常用命令
    private View wantRefuel;   //要加油
    private View wantMaintain;  //维保
    private View wantWash;    //洗车
    private View wantRescue;   //救援
    private View wantInsurance;  //报险
    private View wantPark;     //停车
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        

        view = findViewById(R.id.rl_activity_main_voice);
        view.setOnClickListener(onClickListener);

        wantRefuel = findViewById(R.id.rl_activity_main_oil);
        wantMaintain = findViewById(R.id.rl_activity_main_maintain);
        wantWash = findViewById(R.id.rl_activity_main_wash);
        wantRescue = findViewById(R.id.rl_activity_main_help);
        wantInsurance = findViewById(R.id.rl_activity_main_safety);
        wantPark = findViewById(R.id.rl_activity_main_park);

        wantRefuel.setOnClickListener(onClickListener);
        wantMaintain.setOnClickListener(onClickListener);
        wantWash.setOnClickListener(onClickListener);
        wantRescue.setOnClickListener(onClickListener);
        wantInsurance.setOnClickListener(onClickListener);
        wantPark.setOnClickListener(onClickListener);
    }
    
    OnClickListener onClickListener = new OnClickListener() {
        
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            
        }
    };
}
