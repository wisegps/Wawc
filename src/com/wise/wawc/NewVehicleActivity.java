package com.wise.wawc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
/**
 * 添加新车辆
 * @author 王庆文
 */
public class NewVehicleActivity extends Activity {
	
	private Button cancleAdd = null;   //取消新车辆的添加
	private Button saveAdd = null;     //保存添加
	private ImageView choiceBrank = null;    //选择品牌
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_vehicle);
		cancleAdd = (Button) findViewById(R.id.new_vechile_back);
		saveAdd = (Button) findViewById(R.id.new_vechile_save);
		choiceBrank = (ImageView) findViewById(R.id.choice_brank);
		cancleAdd.setOnClickListener(new CilckListener());
		saveAdd.setOnClickListener(new CilckListener());
		choiceBrank.setOnClickListener(new CilckListener());
	}
	
	class CilckListener implements OnClickListener{
		public void onClick(View v) {
			switch(v.getId()){
			case R.id.new_vechile_back:
				NewVehicleActivity.this.finish();
				break;
			case R.id.new_vechile_save:
				Toast.makeText(getApplicationContext(), "添加新车辆成功", 0).show();
				break;
			case R.id.choice_brank:
				startActivity(new Intent(NewVehicleActivity.this,CarBrankListActivity.class));
				break;
			default:
				return;
			}
		}
	}
}
