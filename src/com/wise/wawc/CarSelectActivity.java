package com.wise.wawc;

import com.wise.extend.CarAdapter;
import com.wise.pubclas.Constant;
import com.wise.pubclas.Variable;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;

/**
 * 选择车辆界面
 * @author honesty
 */
public class CarSelectActivity extends Activity {
    CarAdapter carAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_select);
        ImageView iv_activity_car_remind_menu = (ImageView) findViewById(R.id.iv_activity_car_remind_menu);
        iv_activity_car_remind_menu.setOnClickListener(onClickListener);

        GridView gv_activity_car_remind = (GridView) findViewById(R.id.gv_activity_car_remind);
        carAdapter = new CarAdapter(CarSelectActivity.this, Variable.carDatas);
        gv_activity_car_remind.setAdapter(carAdapter);

        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                Constant.ImageWidth, getResources().getDisplayMetrics());
        LayoutParams params = new LayoutParams((Variable.carDatas.size()
                * (px + 10) + 10), LayoutParams.WRAP_CONTENT);
        gv_activity_car_remind.setLayoutParams(params);
        gv_activity_car_remind.setColumnWidth(px);
        gv_activity_car_remind.setHorizontalSpacing(10);
        gv_activity_car_remind.setStretchMode(GridView.NO_STRETCH);
        gv_activity_car_remind.setNumColumns(Variable.carDatas.size());
        gv_activity_car_remind.setOnItemClickListener(onItemClickListener);

    }
    OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
            case R.id.iv_activity_car_remind_menu:
                finish();
                break;
            }
        }
    };

    OnItemClickListener onItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                long arg3) {
            int Obj_id = Variable.carDatas.get(arg2).getObj_id();
            String Obj_name = Variable.carDatas.get(arg2).getObj_name();
            Intent intent = new Intent();
            intent.putExtra("Obj_id", Obj_id);
            intent.putExtra("Obj_name", Obj_name);
            setResult(1, intent);
            finish();
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}