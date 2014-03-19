package com.wise.wawc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.wise.pubclas.BlurImage;
import com.wise.pubclas.Constant;
import com.wise.pubclas.GetSystem;
import com.wise.pubclas.NetThread;
import com.wise.pubclas.UploadUtil;
import com.wise.pubclas.UploadUtil.OnUploadProcessListener;
import com.wise.pubclas.Variable;
import com.wise.sharesdk.OnekeyShare;
import com.wise.sql.DBExcute;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
/**
 * 发表新文章,分享
 * @author 王庆文
 */
public class NewArticleActivity extends Activity implements PlatformActionListener,OnUploadProcessListener{
	private ImageView back = null;
	private ImageView takePhoto;
	private LinearLayout linearLayout = null;  //将照片动态添加到布局文件中
	EditText et_publish_article;	
	String Content;
	
	private TextView location;
	//index 0 :小图  index 1 : 大图
	private List<String> filePathList = null;
	
	private List<List> bitmapList = new ArrayList<List>();
	
	private List<ImageView> viewList = new ArrayList<ImageView>();  //存储添加的图片 上传完成在布局上移除
	
	private int imageNum = 0;  //上传图片成功张数
	private int imageSize = 0; //标识图片大小  0 ：  小图   1  大图
	
	private static final int removeImageCode = 1;
	private static final int publishArticle = 2;
	
	private MyHandler myHandler = new MyHandler();
	
	private ProgressDialog myDialog = null;
	
	private JSONArray jsonDatas = new JSONArray();
	private JSONObject imageUrl = null;
	
	private int screenWidth = 0;
	private int screenHeight = 0;
	
	private DBExcute dBExcute;
	
	String name = "";
	
	private SharedPreferences preferences = null;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_article);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		TextView publish = (TextView)findViewById(R.id.publish);
		publish.setOnClickListener(new ClickListener());
		back = (ImageView) findViewById(R.id.back);
		back.setOnClickListener(new ClickListener());
		takePhoto = (ImageView) findViewById(R.id.take_photo);
		takePhoto.setOnClickListener(new ClickListener());
		et_publish_article = (EditText)findViewById(R.id.et_publish_article);
		linearLayout = (LinearLayout) findViewById(R.id.my_linearLayout);
		dBExcute = new DBExcute();
		location = (TextView) findViewById(R.id.localtion);
		preferences = this.getSharedPreferences(Constant.sharedPreferencesName, Context.MODE_PRIVATE);
		
		if(!"".equals(Variable.Adress)){
			location.setText(Variable.Adress);
		}
		
		WindowManager manager = getWindowManager();
		Display display = manager.getDefaultDisplay();
		screenWidth = display.getWidth();
		screenHeight = display.getHeight();
	}
	class ClickListener implements OnClickListener{
		public void onClick(View v) {
			switch(v.getId()){
			case R.id.back:
				NewArticleActivity.this.finish();
				break;
			case R.id.publish:
				Content = et_publish_article.getText().toString().trim();
				if(Content.equals("")){
					Toast.makeText(NewArticleActivity.this, R.string.content_null, Toast.LENGTH_SHORT).show();
				}else{					
					if(!"".equals(preferences.getString(Constant.LocationCity,""))){
						if(bitmapList.size() > 0){
							UploadUtil.getInstance().setOnUploadProcessListener(NewArticleActivity.this);
							myDialog = ProgressDialog.show(NewArticleActivity.this, "图片上传", "正在上传");
							myDialog.setCancelable(true);
							UploadUtil.getInstance().uploadFile(bitmapList.get(0).get(imageSize).toString(), "image", Constant.BaseUrl + "upload_image?auth_code=" + Variable.auth_code, new HashMap<String, String>());
						}else{
							myDialog = ProgressDialog.show(NewArticleActivity.this, "数据提交", "提交中...");
							myDialog.setCancelable(true);
							Message msg = new Message();
							msg.what = removeImageCode;
							myHandler.sendMessage(msg);
						}
					}else{
						Toast.makeText(getApplicationContext(), "城市未选择",0).show();
					}
				}				
				break;
			case R.id.take_photo:
				File file = new File("");
                if (!file.exists()) {
                    file.mkdirs();// 创建文件夹
                }
                name = new DateFormat().format("yyyyMMdd_hhmmss",Calendar.getInstance(Locale.CHINA)) + "";
		       	//调用照相机
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);  
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Constant.picPath + name + ".jpg")));
                startActivityForResult(intent, 1); 
				break;
			default:
				return;
			}
		}
	}
	
	class MyHandler extends Handler{
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch(msg.what){
			case removeImageCode:
				if(viewList.size() > 0){
					for(int i = 0 ; i < viewList.size(); i ++){
						linearLayout.removeView(viewList.get(i));
					}
				}
				String imageDatas = jsonDatas.toString();
				String temp = "";
				if(!"[]".equals(imageDatas)){
					temp = imageDatas.replaceAll("\\\\", "");
				}else{
					temp = jsonDatas.toString();
				}
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("cust_id",Variable.cust_id));
				params.add(new BasicNameValuePair("city",preferences.getString(Constant.LocationCity, "")));
				params.add(new BasicNameValuePair("name",Variable.cust_name));
				params.add(new BasicNameValuePair("logo",Constant.UserIconUrl));
				params.add(new BasicNameValuePair("title","title"));
				params.add(new BasicNameValuePair("content",et_publish_article.getText().toString().trim()));
				params.add(new BasicNameValuePair("pics",temp));
				params.add(new BasicNameValuePair("lon",String.valueOf(Variable.Lon)));
				params.add(new BasicNameValuePair("lat",String.valueOf(Variable.Lat)));
				
				new Thread(new NetThread.postDataThread(myHandler, Constant.BaseUrl + "blog?auth_code=" + Variable.auth_code, params, publishArticle)).start();
				break;
				
			case publishArticle:
				try {
					JSONObject jsonObject = new JSONObject(msg.obj.toString());
					myDialog.dismiss();
					if(Integer.parseInt(jsonObject.getString("status_code")) == 0){
						Toast.makeText(getApplicationContext(), "发表成功", 0).show();
						NewArticleActivity.this.setResult(VehicleFriendActivity.newArticleResult);
						VehicleFriendActivity.newArticleBlogId = jsonObject.getInt("blog_id");
						
						
						
						ContentValues valuesType = new ContentValues();
						valuesType.put("Type_id", 1);
						valuesType.put("Blog_id", Integer.valueOf(jsonObject.getInt("blog_id")));
						dBExcute.InsertDB(NewArticleActivity.this, valuesType, Constant.TB_VehicleFriendType);
						NewArticleActivity.this.finish();
					}else{
						VehicleFriendActivity.newArticleBlogId = 0;
						Toast.makeText(getApplicationContext(), "发表失败，请重试...", 0).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				break;
			}
		}
	}
	

	private void ShowBitMap(Bitmap bitmap){
		filePathList = new ArrayList<String>();
		File file = null;
		String fileName = ""; 
		
		Bitmap small_image = null;
		Bitmap big_image = null;
		if(getSDPath() == null){
			Toast.makeText(getApplicationContext(), "SDCard Is Not Exist!", 0).show();
			return;
		}else{
			file = new File(Constant.VehiclePath);
			file.mkdirs();// 创建文件夹  
	        fileName = Constant.VehiclePath + name + ".jpg"; 
	        
	        createImage(fileName, bitmap);  //创建文件(临时)
	        File imageFile = new File(fileName);
	        //将图片压缩至屏幕大小(大图)
	        Bitmap myBitmap = BlurImage.zoomImg(fileName,screenWidth,screenHeight);
	        Log.e("bitmap size","bitmap size = " + myBitmap.getWidth());
            Log.e("bitmap size","bitmap size = " + myBitmap.getHeight());
	        //获取正方形图片
	        Bitmap squareBitmap = BlurImage.getSquareBitmap(myBitmap,screenWidth,screenHeight); 
	        //按照需要的尺寸压缩图片(小图)
	        createImage(Constant.VehiclePath + name + "square_image.jpg", squareBitmap);
	        GetSystem.getScreenInfor(NewArticleActivity.this);
	        small_image = BlurImage.decodeSampledBitmapFromPath(Constant.VehiclePath + name + "square_image.jpg",Variable.smallImageReqWidth,Variable.smallImageReqWidth);
	        File squareImage = new File(Constant.VehiclePath + name + "square_image.jpg");
	        
	        createImage(Constant.VehiclePath + name + "small_image.jpg", small_image);
	        createImage(Constant.VehiclePath + name + "big_image.jpg", myBitmap);
	        
	        filePathList.add(Constant.VehiclePath + name + "small_image.jpg");
	        filePathList.add(Constant.VehiclePath + name + "big_image.jpg");
	        bitmapList.add(filePathList);
	        if(imageFile.exists()){
	        	imageFile.delete();
	        }
	        if(squareImage.exists()){
	        	squareImage.delete();
	        }
		}
        
        //动态在LinearLayout中添加一张图片
        ImageView imageView = new ImageView(this);
        imageView.setLayoutParams(new LinearLayout.LayoutParams(takePhoto.getHeight() + 2,takePhoto.getHeight() + 2));
        imageView.setPadding(5, 0, 0, 0);
        imageView.setImageBitmap(small_image);
        linearLayout.addView(imageView);
        viewList.add(imageView);
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
        super.onActivityResult(requestCode, resultCode, data);  
        if (resultCode == Activity.RESULT_OK) {  
            String sdStatus = Environment.getExternalStorageState();  
            if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用  
                Toast.makeText(this, "没有多余内存",0).show();
                return;  
            }  
//            Bundle bundle = data.getExtras();    TODO
//            Bitmap bitmap = (Bitmap) bundle.get("data");//  
            Bitmap bitmap = BitmapFactory.decodeFile(Constant.picPath + name + ".jpg");
            Log.e("bitmap size","bitmap size = " + bitmap.getWidth());
            Log.e("bitmap size","bitmap size = " + bitmap.getHeight());
            ShowBitMap(bitmap);
        }  
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
	
	public String getSDPath(){
		  File sdDir = null;
		  boolean sdCardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED); //判断sd卡是否存在
		  if (sdCardExist){
			  sdDir = Environment.getExternalStorageDirectory();//获取跟目录
		  }
		  	return sdDir.toString();
		 }


	
	//图片上传的状态监听函数
	public void onUploadDone(int responseCode, String message) {
		StringBuffer sb = new StringBuffer();
		switch(responseCode){
		case UploadUtil.UPLOAD_SUCCESS_CODE:
			try {
				JSONObject jsonObject = new JSONObject(message);
				if(imageSize == 0){
					imageUrl = new JSONObject();
				}
			// 存储返回的图片url   
			if(imageNum < bitmapList.size()){
				//  小图上传成功
				if(imageSize == 0){
					File smallImage = new File(bitmapList.get(imageNum).get(imageSize).toString());
					if(smallImage.exists()){
							String imageFileUrl = jsonObject.getString("image_file_url");
							
							//TODO
							Log.e("小图上传成功返回的url:",imageFileUrl);
							imageUrl.putOpt("small_pic", imageFileUrl);
							String newPath = Constant.VehiclePath + imageFileUrl.substring(imageFileUrl.lastIndexOf("/") + 1);
							File newFile = new File(newPath);
							smallImage.renameTo(newFile);
					}
					imageSize = 1;  //上传大图
					UploadUtil.getInstance().uploadFile(bitmapList.get(imageNum).get(imageSize).toString(), "image", Constant.BaseUrl + "upload_image?auth_code=" + Variable.auth_code, new HashMap<String, String>());
					return;
				}else{
					File bigImage = new File(bitmapList.get(imageNum).get(imageSize).toString());
					if(bigImage.exists()){
						String imageFileUrl = jsonObject.getString("image_file_url").toString();
							imageUrl.putOpt("big_pic", imageFileUrl);
							String newPath = Constant.VehiclePath + imageFileUrl.substring(imageFileUrl.lastIndexOf("/") + 1);
							File newFile = new File(newPath);
							bigImage.renameTo(newFile);
							jsonDatas.put(imageUrl);
					}
					imageNum ++ ;
					imageSize = 0;  //上传下一张小图
					if(imageNum < bitmapList.size()){
						UploadUtil.getInstance().uploadFile(bitmapList.get(imageNum).get(imageSize).toString(), "image", Constant.BaseUrl + "upload_image?auth_code=" + Variable.auth_code, new HashMap<String, String>());
					}
				}
			}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			if(imageNum == bitmapList.size()){
				bitmapList.clear();
				filePathList.clear();
				Message msg = new Message();
				msg.what = removeImageCode;
				myHandler.sendMessage(msg);
				imageNum = 0;
			}
			break;
		case UploadUtil.UPLOAD_SERVER_ERROR_CODE:
			break;
		}
	}
	public void onUploadProcess(int uploadSize) {
	}
	public void initUpload(int fileSize) {
	}
	public void onCancel(Platform arg0, int arg1) {
	}
	public void onComplete(Platform arg0, int arg1, HashMap<String, Object> arg2) {
	}
	public void onError(Platform arg0, int arg1, Throwable arg2) {
	}
}
