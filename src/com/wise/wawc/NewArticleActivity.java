package com.wise.wawc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;

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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
/**
 * 发表新文章
 * @author 王庆文
 */
public class NewArticleActivity extends Activity {
	private Button back = null;
	private ImageView takePhoto;
	private LinearLayout linearLayout = null;  //将照片动态添加到布局文件中
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_article);
		back = (Button) findViewById(R.id.back);
		back.setOnClickListener(new ClickListener());
		takePhoto = (ImageView) findViewById(R.id.take_photo);
		takePhoto.setOnClickListener(new ClickListener());
		
		linearLayout = (LinearLayout) findViewById(R.id.my_linearLayout);
	}
	class ClickListener implements OnClickListener{
		public void onClick(View v) {
			switch(v.getId()){
			case R.id.back:
				NewArticleActivity.this.finish();
				break;
			case R.id.publish:
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
}
