package com.wise.wawc;

import java.io.File;
import cn.sharesdk.framework.ShareSDK;
import com.wise.data.CarData;
import com.wise.pubclas.BlurImage;
import com.wise.pubclas.Constant;
import com.wise.pubclas.GetSystem;
import com.wise.pubclas.Variable;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 位置分享
 * 
 * @author honesty
 */
public class ShareLocationActivity extends Activity {
    private static final String TAG = "ShareLocationActivity";
    EditText et_share_content;
    TextView tv_adress, tv_reason;
    ImageView iv_photo;
    String imagePath = "";
    String reason;
    CarData carData;
    boolean isDelete = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_location);
        ShareSDK.initSDK(this);
        ImageView iv_activity_share_location_back = (ImageView) findViewById(R.id.iv_activity_share_location_back);
        iv_activity_share_location_back.setOnClickListener(onClickListener);
        tv_adress = (TextView) findViewById(R.id.tv_adress);
        tv_reason = (TextView) findViewById(R.id.tv_reason);
        iv_photo = (ImageView) findViewById(R.id.iv_photo);
        iv_photo.setOnLongClickListener(onLongClickListener);
        iv_photo.setOnClickListener(onClickListener);
        ImageView iv_camera = (ImageView) findViewById(R.id.iv_camera);
        iv_camera.setOnClickListener(onClickListener);
        Button bt_activity_share = (Button) findViewById(R.id.bt_activity_share);
        bt_activity_share.setOnClickListener(onClickListener);
        et_share_content = (EditText) findViewById(R.id.et_share_content);
        Intent intent = getIntent();
        reason = intent.getStringExtra("reason");
        tv_reason.setText(reason);
        int index = intent.getIntExtra("index", 0);
        carData = Variable.carDatas.get(index);
        tv_adress.setText(carData.getAdress());
        Log.d(TAG, carData.toString());
    }
    

    OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
            case R.id.iv_activity_share_location_back:
                finish();
                break;
            case R.id.iv_photo:
                if(isDelete){
                    isDelete = false;
                    iv_photo.setVisibility(View.GONE);
                    iv_photo.setImageBitmap(null);
                }
                break;
            case R.id.iv_camera:
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Constant.picPath + Constant.ShareImage)));
                startActivityForResult(intent, 1);
                break;
            case R.id.bt_activity_share:
                String content = et_share_content.getText().toString().trim();
                
                String url = "http://api.map.baidu.com/geocoder?location="
                        + carData.getLat() + "," + carData.getLon()
                        + "&coord_type=bd09ll&output=html";
                StringBuffer sb = new StringBuffer();
                sb.append("【" + reason + "】 ");
                if(carData.getGps_time() != null && !carData.getGps_time().equals("")){
                    try {
                        sb.append(carData.getGps_time().substring(5, 16) + " ");
                    } catch (Exception e) {
                        e.printStackTrace();
                        sb.append(carData.getGps_time() + " ");
                    }
                }
                sb.append(carData.getObj_name());
                sb.append(" 位于" + carData.getAdress());
                if (!content.equals("")) {
                    sb.append(" (" + content + ")");
                }
                sb.append(" " + url);
                GetSystem.share(ShareLocationActivity.this, sb.toString(),
                        imagePath, Float.valueOf(carData.getLat()),
                        Float.valueOf(carData.getLon()),reason,url);
                break;
            }
        }
    };
    OnLongClickListener onLongClickListener = new OnLongClickListener() {        
        @Override
        public boolean onLongClick(View v) {
            switch (v.getId()) {
            case R.id.iv_photo:
                isDelete = true;
                iv_photo.setImageResource(R.drawable.body_icon_delete);
                break;
            }
            return true;
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            imagePath = "";
            // 修改图片大小
            Bitmap bitmap = BlurImage.decodeSampledBitmapFromPath(Constant.picPath + Constant.ShareImage, 480, 800);
            Log.d(TAG, bitmap.getWidth() + "," + bitmap.getHeight() + ",");
            // 再存储到sd卡
            GetSystem.saveImageSD(bitmap, Constant.picPath, Constant.ShareImage);
            // 显示到控件上
            bitmap = BlurImage.decodeSampledBitmapFromPath(Constant.picPath + Constant.ShareImage, 150, 150);
            Log.d(TAG, bitmap.getWidth() + "," + bitmap.getHeight() + ",");
            bitmap = BlurImage.getSquareBitmap(bitmap);
            Log.d(TAG, bitmap.getWidth() + "," + bitmap.getHeight() + ",");
            if (bitmap != null) {
                imagePath = Constant.picPath + Constant.ShareImage;
                //iv_photo.setImageBitmap(bitmap);
                iv_photo.setBackgroundDrawable(new BitmapDrawable(bitmap));
                iv_photo.setVisibility(View.VISIBLE);
                
                isDelete = false;
                iv_photo.setImageBitmap(null);
            }
            GetSystem.displayBriefMemory(ShareLocationActivity.this);
        }
    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Log.d(TAG, "dispatchTouchEvent");
        if(isDelete){
            if(ev.getAction() == MotionEvent.ACTION_DOWN){
                iv_photo.setImageBitmap(null);
            }            
        }
        return super.dispatchTouchEvent(ev);
    }
}