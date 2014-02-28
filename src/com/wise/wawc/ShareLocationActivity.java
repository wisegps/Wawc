package com.wise.wawc;

import cn.sharesdk.framework.ShareSDK;
import com.wise.pubclas.BlurImage;
import com.wise.pubclas.Constant;
import com.wise.pubclas.GetSystem;
import com.wise.pubclas.Variable;
import com.wise.sharesdk.OnekeyShare;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
/**
 * 位置分享
 * @author honesty
 */
public class ShareLocationActivity extends Activity{
    
    EditText et_share_content;
    TextView tv_adress;
    ImageView iv_photo;
    String[] mItem = {"救援","保险"};
    String imagePath = "";
    double Lat;
    double Lon;
    String Adress;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_share_location);
		ShareSDK.initSDK(this);
		ImageView iv_activity_share_location_back = (ImageView)findViewById(R.id.iv_activity_share_location_back);
		iv_activity_share_location_back.setOnClickListener(onClickListener);
		Spinner sp_reason = (Spinner)findViewById(R.id.sp_reason);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, mItem);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // 设置下拉列表的风格
		sp_reason.setAdapter(adapter);
		sp_reason.setOnItemSelectedListener(onItemSelectedListener);
		tv_adress = (TextView)findViewById(R.id.tv_adress);
		iv_photo = (ImageView)findViewById(R.id.iv_photo);
		ImageView iv_camera = (ImageView)findViewById(R.id.iv_camera);
		iv_camera.setOnClickListener(onClickListener);
		Button bt_activity_share = (Button)findViewById(R.id.bt_activity_share);
		bt_activity_share.setOnClickListener(onClickListener);
		et_share_content = (EditText)findViewById(R.id.et_share_content);
		Lat = Variable.Lat;
		Lon = Variable.Lon;
		Adress = Variable.Adress;
		tv_adress.setText(Adress);
	}
	OnClickListener onClickListener = new OnClickListener() {		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.iv_activity_share_location_back:
				finish();
				break;
			case R.id.iv_camera:
			    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);  
                startActivityForResult(intent, 1); 
				break;
			case R.id.bt_activity_share:
			    String content = et_share_content.getText().toString().trim();
			    if(content.equals("")){
			        Toast.makeText(ShareLocationActivity.this, "内容不能为空", Toast.LENGTH_SHORT).show();
			    }else{
			        showShare(true, null);
			    }
			    break;
			}
		}
	};
	OnItemSelectedListener onItemSelectedListener = new OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
            Toast.makeText(ShareLocationActivity.this, mItem[arg2], Toast.LENGTH_SHORT).show();            
        }
        @Override
        public void onNothingSelected(AdapterView<?> arg0) {}
    };
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            imagePath = "";
            String sdStatus = Environment.getExternalStorageState();  
            if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用  
                Toast.makeText(this, "没有多余内存",Toast.LENGTH_SHORT).show();
                return;  
            }  
            Bundle bundle = data.getExtras();
            Bitmap bitmap = (Bitmap) bundle.get("data");//获取相机返回的数据，并转换为Bitmap图片格式  
            GetSystem.saveImageSD(bitmap,Constant.picPath, Constant.ShareImage);
            //bitmap.recycle();
            //bitmap = null;            
            //修改图片大小
            bitmap = BlurImage.decodeSampledBitmapFromPath(Constant.picPath + Constant.ShareImage, 480, 800);
            //再存储到sd卡
            GetSystem.saveImageSD(bitmap,Constant.picPath, Constant.ShareImage);
            //显示到控件上
            bitmap = BlurImage.decodeSampledBitmapFromPath(Constant.picPath + Constant.ShareImage, 80, 80);  
            bitmap = BlurImage.getSquareBitmap(bitmap);
            if(bitmap != null){  
                imagePath = Constant.picPath + Constant.ShareImage;
                iv_photo.setImageBitmap(bitmap);
            }
            GetSystem.displayBriefMemory(ShareLocationActivity.this);
        }
    }
    
    private void showShare(boolean silent, String platform) {
        final OnekeyShare oks = new OnekeyShare();
        oks.setNotification(R.drawable.ic_launcher, "app_name");
        //oks.setAddress("12345678901");
        oks.setTitle("share");
        //oks.setTitleUrl("http://sharesdk.cn");
        oks.setText(et_share_content.getText().toString().trim());
        oks.setImagePath(imagePath);
        //oks.setImageUrl("http://img.appgo.cn/imgs/sharesdk/content/2013/07/25/1374723172663.jpg");
        //oks.setUrl("http://www.sharesdk.cn");
        //oks.setFilePath(imagePath);
        //oks.setComment("share");
        //oks.setSite("wise");
        //oks.setSiteUrl("http://sharesdk.cn");
        //oks.setVenueName("Share SDK");
        //oks.setVenueDescription("This is a beautiful place!");
        oks.setLatitude((float)Lat);
        oks.setLongitude((float)Lon);
        oks.setSilent(silent);
        if (platform != null) {
            oks.setPlatform(platform);
        }
        oks.show(ShareLocationActivity.this);
    }
}