package com.wise.wawc;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
/**
 * 位置分享
 * @author honesty
 */
public class ShareLocationActivity extends Activity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_share_location);
		ImageView iv_activity_share_location_back = (ImageView)findViewById(R.id.iv_activity_share_location_back);
		iv_activity_share_location_back.setOnClickListener(onClickListener);
	}
	OnClickListener onClickListener = new OnClickListener() {		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.iv_activity_share_location_back:
				finish();
				break;

			default:
				break;
			}
		}
	};
}