package com.wise.wawc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import com.wise.pubclas.Config;
import com.wise.sharesdk.OnekeyShare;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
/**
 * 发表新文章,分享
 * @author 王庆文
 */
public class NewArticleActivity extends Activity implements PlatformActionListener{
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
		Intent intent = getIntent();
		
		location = (TextView) findViewById(R.id.localtion);
		
		if(!"".equals(Config.Adress)){
			location.setText(Config.Adress);
		}
		
		isSNS = intent.getBooleanExtra("isSNS", false);
		if(isSNS){//初始化shareSDK
			ShareSDK.initSDK(this);
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
					}else{//发布新文章
						
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
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
        super.onActivityResult(requestCode, resultCode, data);  
        if (resultCode == Activity.RESULT_OK) {  
            String sdStatus = Environment.getExternalStorageState();  
            if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用  
                Toast.makeText(this, "没有多余内存",0).show();
                return;  
            }  
            String name = new DateFormat().format("yyyyMMdd_hhmmss",Calendar.getInstance(Locale.CHINA)) + ".jpg";     
            Toast.makeText(this, name, Toast.LENGTH_LONG).show();  
            Bundle bundle = data.getExtras();  
            Bitmap bitmap = (Bitmap) bundle.get("data");// 获取相机返回的数据，并转换为Bitmap图片格式  
          
            FileOutputStream b = null;  
            File file = new File("/sdcard/myImage/");  
            file.mkdirs();// 创建文件夹  
            String fileName = "/sdcard/myImage/"+name;  
  
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
            //动态在LinearLayout中添加一张图片
            ImageView imageView = new ImageView(this);
            imageView.setLayoutParams(new LinearLayout.LayoutParams(80,80));
            imageView.setPadding(5, 5, 5, 5);
            imageView.setImageBitmap(bitmap);
            linearLayout.addView(imageView);
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

	@Override
	public void onCancel(Platform arg0, int arg1) {}
	@Override
	public void onComplete(Platform arg0, int arg1, HashMap<String, Object> arg2) {}
	@Override
	public void onError(Platform arg0, int arg1, Throwable arg2) {}
}
