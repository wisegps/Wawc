package com.wise.wawc;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
/**
 * 发表新文章
 * @author 王庆文
 */
public class NewArticleActivity extends Activity {
	private Button back = null;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_article);
		back = (Button) findViewById(R.id.back);
		back.setOnClickListener(new ClickListener());
	}
	
	class ClickListener implements OnClickListener{
		public void onClick(View v) {
			switch(v.getId()){
			case R.id.back:
				NewArticleActivity.this.finish();
				break;
			case R.id.publish:
				break;
			default:
				return;
			}
		}
	}
}
