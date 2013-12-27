package com.wise.wawc;
import com.wise.service.ImageAdapter;

import android.app.Activity;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Gallery;
import android.widget.HorizontalScrollView;
import android.widget.AdapterView.OnItemClickListener;
public class ImageActivity extends Activity {
	private Gallery gallery = null;
	private HorizontalScrollView horizontalScroller;
	int mImageResourceIds[] = new int[]{R.drawable.image,R.drawable.image,
			R.drawable.image,R.drawable.image,R.drawable.image,R.drawable.image,
			R.drawable.image,R.drawable.image,R.drawable.image};
	private ImageAdapter adapter = null;
	
	WindowManager m = null;;
	Display d = null; // 为获取屏幕宽、高
	
	public static int screenHeight = 0;
	public static int screenWidth = 0;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.image_exalpoer);
		gallery = (Gallery) findViewById(R.id.gallery);
		adapter = new ImageAdapter(ImageActivity.this, mImageResourceIds);
		m = this.getWindowManager();
		d = m.getDefaultDisplay();
		screenHeight= d.getHeight();
		screenWidth = d.getWidth();
		
		gallery.setAdapter(adapter);
		gallery.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
				ImageActivity.this.finish();
			}
		});
	}
}
