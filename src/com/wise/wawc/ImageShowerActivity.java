package com.wise.wawc;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.ImageView;
/**
 * 查看大图界面
 * @author honesty
 */
public class ImageShowerActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_imageshower);
		ImageView iv_big = (ImageView)findViewById(R.id.iv_big);
		String ImagePath = getIntent().getStringExtra("ImagePath");
		//iv_big
		Bitmap bitmap = BitmapFactory.decodeFile(ImagePath); 
		if(bitmap != null){
		    iv_big.setImageBitmap(bitmap);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		finish();
		return true;
	}
}