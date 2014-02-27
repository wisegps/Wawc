package com.wise.wawc;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
/**
 * 好友资料
 * @author 王庆文
 */
public class FriendInformationActivity extends Activity{
	ImageView back;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.friend_information);
		back = (ImageView) findViewById(R.id.friend_information_back);
		back.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				FriendInformationActivity.this.finish();
			}
		});
	}
}
