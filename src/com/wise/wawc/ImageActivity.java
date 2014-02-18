package com.wise.wawc;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.wise.pubclas.Constant;
import com.wise.pubclas.GetSystem;
import com.wise.pubclas.Variable;
import com.wise.service.ImageAdapter;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
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
	private Bitmap bitmap = null;
	int mImageResourceIds1[] = new int[]{R.drawable.a,R.drawable.ic_launcher,
			R.drawable.ic_launcher,R.drawable.ic_launcher,R.drawable.ic_launcher,R.drawable.ic_launcher,
			R.drawable.ic_launcher,R.drawable.ic_launcher,R.drawable.ic_launcher};
	private ImageAdapter adapter = null;
	private List<Bitmap> imageModel = new ArrayList<Bitmap>();
	public static int screenHeight = 0;
	public static int screenWidth = 0;
	private Intent intent = null;
	private int listIndex = 0;
	private MyHandler myHandler = null;
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
		gallery.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
				ImageActivity.this.finish();
			}
		});
		myHandler = new MyHandler();
		intent = getIntent();
		listIndex = intent.getIntExtra("position", 0);
		
		//启动线程下载图片
	final List<Map<String,String>> imageList = Variable.articleList.get(listIndex).getImageList();
		new Thread(new Runnable() {
			public void run() {
					for(int j = 0 ; j < imageList.size() ; j ++){
						Map<String,String> imageMap = imageList.get(j);
						final String str = imageMap.get("big_pic");
						Bitmap bitmap = imageIsExist(Constant.VehiclePath + str.substring(str.lastIndexOf("/")),str);
						if(bitmap != null){
						Log.e("执行了没",str);
						imageModel.add(bitmap);
					}
						if(j == (imageList.size() - 1)){
							myHandler.sendMessage(new Message());
						}
					Log.e("加载"+ (j+1)+"张",imageModel.size() + "");	
				}
			}
		}).start();
		Log.e(imageModel.size() + "",imageModel.size() + "");
	}
	
	private Bitmap imageIsExist(String path,final String loadUrl) {
		File file = new File(path);
		if(file.exists()){
			bitmap = BitmapFactory.decodeFile(path);
			Log.e("本地存在","本地存在");
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
						createImage(Constant.VehiclePath + loadUrl.substring(loadUrl.lastIndexOf("/")),bitmap);
					}
				}
			}).start();
		}
		return bitmap;
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
			adapter.refreshDatas(imageModel);
		}
	}
}
