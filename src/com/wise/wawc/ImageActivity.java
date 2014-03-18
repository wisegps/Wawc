package com.wise.wawc;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.wise.data.Article;
import com.wise.pubclas.Constant;
import com.wise.pubclas.GetSystem;
import com.wise.pubclas.Variable;
import com.wise.service.ImageAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Gallery;
import android.widget.HorizontalScrollView;
import android.widget.AdapterView.OnItemClickListener;
public class ImageActivity extends Activity {
	private Gallery gallery = null;
	private HorizontalScrollView horizontalScroller;
	private Bitmap bitmap = null;
	private ImageAdapter adapter = null;
	private List<Bitmap> imageModel = new ArrayList<Bitmap>();
	public static int screenHeight = 0;
	public static int screenWidth = 0;
	private Intent intent = null;
	private Article article = null;
	private MyHandler myHandler = null;
	private List<Map<String,String>> imageList = null;
	private MyThread mYThread = null;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.image_exalpoer);
		gallery = (Gallery) findViewById(R.id.gallery);
		
		gallery.setVerticalFadingEdgeEnabled(false);	
        gallery.setHorizontalFadingEdgeEnabled(false); // 设置view在水平滚动时，水平边不淡出。   TODO
		adapter = new ImageAdapter(ImageActivity.this, imageModel);
		screenWidth = getWindow().getWindowManager().getDefaultDisplay().getWidth();
		screenHeight = getWindow().getWindowManager().getDefaultDisplay().getHeight();
		
		gallery.setAdapter(adapter);
		mYThread = new MyThread();
		gallery.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
				ImageActivity.this.finish();
			}
		});
		myHandler = new MyHandler();
		intent = getIntent();
		article = (Article) intent.getSerializableExtra("article");
		imageList = article.getImageList();
		mYThread.start();
	}
	
	
	class MyThread extends Thread{
		public void run() {
			for(int j = 0 ; j < imageList.size() ; j ++){
				Map<String,String> imageMap = imageList.get(j);
				String str = imageMap.get("big_pic");
				Bitmap bitmap = imageIsExist(Constant.VehiclePath + str.substring(str.lastIndexOf("/")),str);
				if(bitmap != null){
					imageModel.add(bitmap);
				}
//				if(j == (imageList.size() - 1)){
//					myHandler.sendMessage(new Message());
//				}
//			Log.e("加载"+ (j+1)+"张",imageModel.size() + "");	
		}
			super.run();
		}
	}
	
	private Bitmap imageIsExist(String path,final String loadUrl) {
		File file = new File(path);
		if(file.exists()){
			bitmap = BitmapFactory.decodeFile(path);
			Log.e("本地存在","本地存在");
			return bitmap;
		}else{
			Log.e("服务器获取","服务器获取");
			new Thread(new Runnable() {
				public void run() {
					bitmap = GetSystem.getBitmapFromURL(loadUrl);
					if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
						File imagePath = new File(Constant.VehiclePath);
						if(!imagePath.exists()){
							imagePath.mkdir();
						}
						if(bitmap != null){
							createImage(Constant.VehiclePath + loadUrl.substring(loadUrl.lastIndexOf("/")),bitmap);
						}
							
					}
				}
			}).start();
		}
		return null;
	}
	
	public void createImage(String fileName,Bitmap bitmap){
		FileOutputStream b = null;
		try {  
            b = new FileOutputStream(fileName);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, b);// 把数据写入文件  
        } catch (FileNotFoundException e) {  
            e.printStackTrace();  
        } finally {  
            try {  
                b.flush();  
                b.close();  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }
	}
	
	class MyHandler extends Handler{
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Log.e("刷新...","刷新");
			adapter.refreshDatas(imageModel);
		}
	}
}
