package com.wise.wawc;

import com.wise.data.Article;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
/**
 * 好友资料
 * @author 王庆文
 */
public class FriendInformationActivity extends Activity{
	ImageView back;
	Article article  = null;
	String cust_id = "";
	ImageView userHead = null;
	TextView userName = null;
	TextView carName = null;
	TextView carBrand = null;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.friend_information);
		back = (ImageView) findViewById(R.id.friend_information_back);
		article = (Article) getIntent().getSerializableExtra("article");
		cust_id = getIntent().getStringExtra("cust_id");
		back.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				FriendInformationActivity.this.finish();
			}
		});
	}
}
