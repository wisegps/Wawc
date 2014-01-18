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
import com.wise.pubclas.NetThread;
import com.wise.pubclas.UploadUtil;
import com.wise.pubclas.UploadUtil.OnUploadProcessListener;
import com.wise.pubclas.Variable;
import com.wise.sharesdk.OnekeyShare;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
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
	/**
	 * isSNS，true发布到微博orQQ控件；false发布新文章
	 */
	boolean isSNS = false;
	private Button back = null;
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
	
	private SharedPreferences preferences = null;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_article);
		Button publish = (Button)findViewById(R.id.publish);
		publish.setOnClickListener(new ClickListener());
		back = (Button) findViewById(R.id.back);
		back.setOnClickListener(new ClickListener());
		takePhoto = (ImageView) findViewById(R.id.take_photo);
		takePhoto.setOnClickListener(new ClickListener());
		et_publish_article = (EditText)findViewById(R.id.et_publish_article);
		linearLayout = (LinearLayout) findViewById(R.id.my_linearLayout);
		
		location = (TextView) findViewById(R.id.localtion);
		preferences = this.getSharedPreferences(Constant.sharedPreferencesName, Context.MODE_PRIVATE);
		
		if(!"".equals(Variable.Adress)){
			location.setText(Variable.Adress);
		}

		Intent intent = getIntent();
		isSNS = intent.getBooleanExtra("isSNS", false);
		if(isSNS){//初始化shareSDK
			ShareSDK.initSDK(this);
			Bitmap bitmap=intent.getParcelableExtra("bitmap");
			ShowBitMap(bitmap);
		}
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
					if(isSNS){//发布到到微博orQQ空间
						showShare(true, null);
					}else{//发布新文章（发图片）
						if(!"".equals(preferences.getString(Constant.LocationCity,""))){
							if(bitmapList.size() > 0){
								UploadUtil.getInstance().setOnUploadProcessListener(NewArticleActivity.this);
								myDialog = ProgressDialog.show(NewArticleActivity.this, "图片上传", "正在上传");
								myDialog.setCancelable(true);
								UploadUtil.getInstance().uploadFile(bitmapList.get(0).get(imageSize).toString(), "image", Constant.BaseUrl + "upload_image?auth_code=" + Variable.auth_code, new HashMap<String, String>());
							}else{
								myDialog = ProgressDialog.show(NewArticleActivity.this, "图片上传", "正在上传");
								myDialog.setCancelable(true);
								Message msg = new Message();
								msg.what = removeImageCode;
								myHandler.sendMessage(msg);
							}
						}else{
							Toast.makeText(getApplicationContext(), "城市未选择",0).show();
						}
					}
				}
				
				break;
			case R.id.take_photo:
		       	//调用照相机
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);  
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
				Log.e("生成的数据：",imageDatas);
				if(!"[]".equals(imageDatas)){
					temp = imageDatas.replaceAll("\\\\", "");
				}else{
					temp = jsonDatas.toString();
				}
				Log.e("处理的数据：",temp);
//				//发表文章
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("cust_id",Variable.cust_id));
				params.add(new BasicNameValuePair("city",preferences.getString(Constant.LocationCity, "")));
				params.add(new BasicNameValuePair("name",Variable.cust_name));
				params.add(new BasicNameValuePair("title","title"));
				params.add(new BasicNameValuePair("content",et_publish_article.getText().toString().trim()));
				
				params.add(new BasicNameValuePair("pics",temp));
				params.add(new BasicNameValuePair("lon",String.valueOf(Variable.Lon)));
				params.add(new BasicNameValuePair("lat",String.valueOf(Variable.Lat)));
				new Thread(new NetThread.postDataThread(myHandler, Constant.BaseUrl + "blog?auth_code=" + Variable.auth_code, params, publishArticle)).start();
				break;
				
			case publishArticle:
				Log.e("文章发表结果:",msg.obj.toString());
				try {
					JSONObject jsonObject = new JSONObject(msg.obj.toString());
					if(Integer.parseInt(jsonObject.getString("status_code")) == 0){
						myDialog.dismiss();
						Toast.makeText(getApplicationContext(), "发表成功", 0).show();
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
		String name = new DateFormat().format("yyyyMMdd_hhmmss",Calendar.getInstance(Locale.CHINA)) + "";
		File file = null;
		String fileName = ""; 
		
		Bitmap small_image = null;
		Bitmap big_image = null;
		if(getSDPath() == null){
			Toast.makeText(getApplicationContext(), "SDCard Is Not Exist!", 0).show();
			return;
		}else{
			file = new File(getSDPath() + "/myImage/");
			file.mkdirs();// 创建文件夹  
	        fileName = getSDPath() + "/myImage/" + name + ".jpg"; 
	        createImage(fileName, bitmap);  //创建文件
	        File imageFile = new File(fileName);
	        //按照指定的大小压缩图片
	        small_image = BlurImage.decodeSampledBitmapFromPath(fileName,Variable.smallImageReqHeight,Variable.smallImageReqHeight);
	        big_image = BlurImage.decodeSampledBitmapFromPath(fileName,Variable.bigImageReqHeight,Variable.bigImageReqHeight);
	        Log.e("图片存储路径：",getSDPath() + "/myImage/" + name + "small_image.jpg");
	        createImage(getSDPath() + "/myImage/" + name + "small_image.jpg", small_image);
	        createImage(getSDPath() + "/myImage/" + name + "big_image.jpg", big_image);
	        filePathList.add(getSDPath() + "/myImage/" + name + "small_image.jpg");
	        filePathList.add(getSDPath() + "/myImage/" + name + "big_image.jpg");
	        bitmapList.add(filePathList);
	        
	        Log.e("bitmapList.size()",bitmapList.size() + "");
	        
	        if(imageFile.exists()){
	        	imageFile.delete();
	        }
		}
        
        //动态在LinearLayout中添加一张图片
        ImageView imageView = new ImageView(this);
        imageView.setLayoutParams(new LinearLayout.LayoutParams(Variable.smallImageReqHeight,Variable.smallImageReqHeight));
        imageView.setPadding(5, 5, 5, 5);
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
            Bundle bundle = data.getExtras();  
            Bitmap bitmap = (Bitmap) bundle.get("data");// 获取相机返回的数据，并转换为Bitmap图片格式  
            ShowBitMap(bitmap);
        }  
    }
	
	
	private void showShare(boolean silent, String platform) {
		System.out.println("分享");
		final OnekeyShare oks = new OnekeyShare();
		oks.setNotification(R.drawable.ic_launcher, "app_name");
		//oks.setAddress("12345678901");
		oks.setTitle("share");
		//oks.setTitleUrl("http://sharesdk.cn");
		oks.setText(Content);
		//oks.setImagePath(MainActivity.TEST_IMAGE);
		//oks.setImageUrl("http://img.appgo.cn/imgs/sharesdk/content/2013/07/25/1374723172663.jpg");
		//oks.setUrl("http://www.sharesdk.cn");
		//oks.setFilePath(MainActivity.TEST_IMAGE);
		//oks.setComment("share");
		//oks.setSite("wise");
		//oks.setSiteUrl("http://sharesdk.cn");
		//oks.setVenueName("Share SDK");
		//oks.setVenueDescription("This is a beautiful place!");
		//oks.setLatitude(23.056081f);
		//oks.setLongitude(113.385708f);
		oks.setSilent(silent);
		if (platform != null) {
			oks.setPlatform(platform);
		}
		oks.show(NewArticleActivity.this);
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
					Log.e("创建","创建");
					imageUrl = new JSONObject();
				}
			// 存储返回的图片url   
			if(imageNum < bitmapList.size()){
				//  小图上传成功
				if(imageSize == 0){
					File smallImage = new File(bitmapList.get(imageNum).get(imageSize).toString());
					Log.e("smallImage",smallImage.toString());
					if(smallImage.exists()){
							String imageFileUrl = jsonObject.getString("image_file_url");
							imageUrl.putOpt("small_pic", imageFileUrl);
							String newPath = getSDPath() + "/myImage/" + imageFileUrl.substring(imageFileUrl.lastIndexOf("/") + 1);
							File newFile = new File(newPath);
							smallImage.renameTo(newFile);
					}
					imageSize = 1;  //上传大图
					UploadUtil.getInstance().uploadFile(bitmapList.get(imageNum).get(imageSize).toString(), "image", Constant.BaseUrl + "upload_image?auth_code=" + Variable.auth_code, new HashMap<String, String>());
					return;
				}else{
					File bigImage = new File(bitmapList.get(imageNum).get(imageSize).toString());
					Log.e("bigImage",bigImage.toString());
					if(bigImage.exists()){
						String imageFileUrl = jsonObject.getString("image_file_url").toString();
						Log.e("imageUrl == null",(imageUrl == null)+"");
							imageUrl.putOpt("big_pic", imageFileUrl);
							String newPath = getSDPath() + "/myImage/" + imageFileUrl.substring(imageFileUrl.lastIndexOf("/") + 1);
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
				Log.e("上传结果","第" + imageNum + "张上传成功");
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
				Log.e("上传成功" + imageNum,"上传成功");
			}
			break;
		case UploadUtil.UPLOAD_SERVER_ERROR_CODE:
			Log.e("服务器出错","服务器出错");
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
