package com.wise.wawc;

import java.util.ArrayList;
import java.util.List;
import com.wise.data.AdressData;
import com.wise.extend.AdressAdapter;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
/**
 * 处理地点
 * @author honesty
 */
public class DealAddressActivity extends Activity{
	List<AdressData> adressDatas = new ArrayList<AdressData>();
	AdressAdapter adressAdapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dealadress);
		ListView lv_activity_dealadress = (ListView)findViewById(R.id.lv_activity_dealadress);
		GetData();
		adressAdapter = new AdressAdapter(getApplicationContext(), adressDatas,DealAddressActivity.this);
		lv_activity_dealadress.setAdapter(adressAdapter);
		
		ImageView iv_activity_dealadress_back = (ImageView)findViewById(R.id.iv_activity_dealadress_back);
		iv_activity_dealadress_back.setOnClickListener(onClickListener);
	}
	
	OnClickListener onClickListener = new OnClickListener() {		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.iv_activity_dealadress_back:
				finish();
				break;

			default:
				break;
			}
		}
	};
	
	private void GetData(){
		for(int i = 0 ; i < 10 ; i++){
			AdressData AdressData = new AdressData();
			AdressData.setName("深圳市交通局");
			AdressData.setAdress("地址：沙河西路1号");
			AdressData.setPhone("电话：0755-8787878787");
			adressDatas.add(AdressData);
		}
	}
}