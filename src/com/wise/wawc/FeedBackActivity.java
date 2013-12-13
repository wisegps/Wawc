package com.wise.wawc;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class FeedBackActivity extends Activity {
	private Button feedBackCancle = null;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.feed_back);
		feedBackCancle = (Button) findViewById(R.id.feed_back_cancle);
		feedBackCancle.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				FeedBackActivity.this.finish();
			}
		});
	}
}
