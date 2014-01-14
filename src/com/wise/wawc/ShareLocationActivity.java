package com.wise.wawc;

import com.wise.pubclas.BlurImage;
import com.wise.pubclas.Constant;
import com.wise.pubclas.GetSystem;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
/**
 * 位置分享
 * @author honesty
 */
public class ShareLocationActivity extends Activity{
    ImageView iv_photo;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_share_location);
		ImageView iv_activity_share_location_back = (ImageView)findViewById(R.id.iv_activity_share_location_back);
		iv_activity_share_location_back.setOnClickListener(onClickListener);
		Spinner sp_reason = (Spinner)findViewById(R.id.sp_reason);
		String[] mItem = {"救援","保险"};
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, mItem);
		sp_reason.setAdapter(adapter);
		iv_photo = (ImageView)findViewById(R.id.iv_photo);
		ImageView iv_camera = (ImageView)findViewById(R.id.iv_camera);
		iv_camera.setOnClickListener(onClickListener);
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
			}
		}
	};
	OnItemSelectedListener onItemSelectedListener = new OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
                long arg3) {
            
        }
        @Override
        public void onNothingSelected(AdapterView<?> arg0) {}
    };
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {  
            String sdStatus = Environment.getExternalStorageState();  
            if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用  
                Toast.makeText(this, "没有多余内存",Toast.LENGTH_SHORT).show();
                return;  
            }  
            Bundle bundle = data.getExtras();  
            Bitmap bitmap = (Bitmap) bundle.get("data");//获取相机返回的数据，并转换为Bitmap图片格式  
            GetSystem.saveImageSD(bitmap, Constant.ShareImage);
            Bitmap bimage = BitmapFactory.decodeFile(Constant.BasePath + Constant.ShareImage);
            if(bimage != null){            
                iv_photo.setImageBitmap(bimage);
            }
        }
    }
}