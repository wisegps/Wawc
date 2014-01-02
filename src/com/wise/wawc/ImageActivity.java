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
	int mImageResourceIds[] = new int[]{R.drawable.a,R.drawable.image,
			R.drawable.image,R.drawable.image,R.drawable.image,R.drawable.image,
			R.drawable.image,R.drawable.image,R.drawable.image};
	private ImageAdapter adapter = null;
	
	public static int screenHeight = 0;
	public static int screenWidth = 0;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.image_exalpoer);
		gallery = (Gallery) findViewById(R.id.gallery);
		
		gallery.setVerticalFadingEdgeEnabled(false);	
        gallery.setHorizontalFadingEdgeEnabled(false);//);// 设置view在水平滚动时，水平边不淡出。
		adapter = new ImageAdapter(ImageActivity.this, mImageResourceIds);
		screenWidth = getWindow().getWindowManager().getDefaultDisplay().getWidth();
		screenHeight = getWindow().getWindowManager().getDefaultDisplay().getHeight();
		
		gallery.setAdapter(adapter);
		gallery.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
				ImageActivity.this.finish();
			}
		});
	}
}
