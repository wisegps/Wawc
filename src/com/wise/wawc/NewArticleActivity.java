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
import com.wise.sql.DBExcute;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import android.R.integer;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 发表新文章,分享
 * 
 * @author 王庆文
 */
public class NewArticleActivity extends Activity implements
        PlatformActionListener, OnUploadProcessListener {
    private ImageView back = null;
    private ImageView takePhoto;
    private LinearLayout linearLayout = null; // 将照片动态添加到布局文件中
    EditText et_publish_article;
    String Content;

    private TextView location;
    List<PicData> picDatas = new ArrayList<PicData>();

    private int imageNum = 0; // 上传图片成功张数
    private int imageSize = 0; // 标识图片大小 0 ： 小图 1 大图

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
    boolean isDelete = false;

    private SharedPreferences preferences = null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_article);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        TextView publish = (TextView) findViewById(R.id.publish);
        publish.setOnClickListener(new ClickListener());
        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(new ClickListener());
        takePhoto = (ImageView) findViewById(R.id.take_photo);
        takePhoto.setOnClickListener(new ClickListener());
        et_publish_article = (EditText) findViewById(R.id.et_publish_article);
        linearLayout = (LinearLayout) findViewById(R.id.my_linearLayout);
        dBExcute = new DBExcute();
        location = (TextView) findViewById(R.id.localtion);
        preferences = this.getSharedPreferences(Constant.sharedPreferencesName,
                Context.MODE_PRIVATE);

        if (!"".equals(Variable.Adress)) {
            location.setText(Variable.Adress);
        }

        WindowManager manager = getWindowManager();
        Display display = manager.getDefaultDisplay();
        screenWidth = display.getWidth();
        screenHeight = display.getHeight();
    }

    class ClickListener implements OnClickListener {
        public void onClick(View v) {
            switch (v.getId()) {
            case R.id.back:
                NewArticleActivity.this.finish();
                break;
            case R.id.publish:
                Content = et_publish_article.getText().toString().trim();
                if (Content.equals("")) {
                    Toast.makeText(NewArticleActivity.this,
                            R.string.content_null, Toast.LENGTH_SHORT).show();
                } else {
                    if (!"".equals(preferences.getString(Constant.LocationCity,
                            ""))) {
                        if (picDatas.size() > 0) {
                            UploadUtil.getInstance()
                                    .setOnUploadProcessListener(
                                            NewArticleActivity.this);
                            myDialog = ProgressDialog.show(
                                    NewArticleActivity.this, "图片上传", "正在上传");
                            myDialog.setCancelable(true);
                            UploadUtil.getInstance().uploadFile(
                                    picDatas.get(0).small_pic,
                                    "image",
                                    Constant.BaseUrl
                                            + "upload_image?auth_code="
                                            + Variable.auth_code,
                                    new HashMap<String, String>());
                        } else {
                            myDialog = ProgressDialog.show(
                                    NewArticleActivity.this, "数据提交", "提交中...");
                            myDialog.setCancelable(true);
                            Message msg = new Message();
                            msg.what = removeImageCode;
                            myHandler.sendMessage(msg);
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "城市未选择", 0)
                                .show();
                    }
                }
                break;
            case R.id.take_photo:
                File file = new File(Constant.VehiclePath);
                if (!file.exists()) {
                    file.mkdirs();// 创建文件夹
                }
                name = new DateFormat().format("yyyyMMdd_hhmmss",
                        Calendar.getInstance(Locale.CHINA))
                        + "";
                // 调用照相机
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(
                        MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(new File(Constant.VehiclePath + name
                                + ".jpg")));
                startActivityForResult(intent, 1);
                break;
            case 1:                
                isDelete = false;
                for (int i = 0 ; i < picDatas.size() ; i++) {
                    PicData picData = picDatas.get(i);
                    if(picData.getTag().equals(String.valueOf(v.getTag()))){
                        if(picData.isDelete){
                            picDatas.remove(i);
                            linearLayout.removeView(v);
                        }else{
                            Intent intent1 = new Intent(NewArticleActivity.this, ImageShowerActivity.class);
                            intent1.putExtra("ImagePath", picData.getBig_pic());
                            startActivity(intent1);
                        }                        
                        break;
                    }
                }
                break;
            }
        }
    }
    OnLongClickListener onLongClickListener = new OnLongClickListener() {
        
        @Override
        public boolean onLongClick(View v) {
            switch (v.getId()) {
            case 1:
                isDelete = true;
                ((ImageView)v).setImageResource(R.drawable.body_icon_delete);
                for (PicData picData : picDatas) {
                    if(picData.getTag().equals(String.valueOf(v.getTag()))){
                        picData.setDelete(true);
                        break;
                    }
                }
                break;
            }
            return true;
        }
    };
    //点击清空
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if(isDelete){
            if(ev.getAction() == MotionEvent.ACTION_DOWN){
                for(int i = 0 ; i < linearLayout.getChildCount() ; i++){
                    ((ImageView)linearLayout.getChildAt(i)).setImageBitmap(null);
                }
            }            
        }
        return super.dispatchTouchEvent(ev);
    };

    class MyHandler extends Handler {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
            case removeImageCode:
                linearLayout.removeAllViews();
                String imageDatas = jsonDatas.toString();
                String temp = "";
                if (!"[]".equals(imageDatas)) {
                    temp = imageDatas.replaceAll("\\\\", "");
                } else {
                    temp = jsonDatas.toString();
                }
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("cust_id", Variable.cust_id));
                params.add(new BasicNameValuePair("city", preferences
                        .getString(Constant.LocationCity, "")));
                params.add(new BasicNameValuePair("name", Variable.cust_name));
                params.add(new BasicNameValuePair("logo", Constant.UserIconUrl));
                params.add(new BasicNameValuePair("title", "title"));
                params.add(new BasicNameValuePair("content", et_publish_article
                        .getText().toString().trim()));
                params.add(new BasicNameValuePair("pics", temp));
                params.add(new BasicNameValuePair("lon", String
                        .valueOf(Variable.Lon)));
                params.add(new BasicNameValuePair("lat", String
                        .valueOf(Variable.Lat)));

                new Thread(new NetThread.postDataThread(myHandler,
                        Constant.BaseUrl + "blog?auth_code="
                                + Variable.auth_code, params, publishArticle))
                        .start();
                break;

            case publishArticle:
                try {
                    JSONObject jsonObject = new JSONObject(msg.obj.toString());
                    myDialog.dismiss();
                    if (Integer.parseInt(jsonObject.getString("status_code")) == 0) {
                        Toast.makeText(getApplicationContext(), "发表成功", 0)
                                .show();
                        NewArticleActivity.this
                                .setResult(VehicleFriendActivity.newArticleResult);
                        VehicleFriendActivity.newArticleBlogId = jsonObject
                                .getInt("blog_id");

                        ContentValues valuesType = new ContentValues();
                        valuesType.put("Type_id", 1);
                        valuesType.put("Blog_id",
                                Integer.valueOf(jsonObject.getInt("blog_id")));
                        dBExcute.InsertDB(NewArticleActivity.this, valuesType,
                                Constant.TB_VehicleFriendType);
                        NewArticleActivity.this.finish();
                    } else {
                        VehicleFriendActivity.newArticleBlogId = 0;
                        Toast.makeText(getApplicationContext(), "发表失败，请重试...",
                                0).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    @SuppressWarnings("deprecation")
    private void ShowBitMap(Bitmap bitmap) {
        File file = null;
        String fileName = "";

        Bitmap small_image = null;
        Bitmap big_image = null;
        if (getSDPath() == null) {
            Toast.makeText(getApplicationContext(), "SDCard Is Not Exist!", 0)
                    .show();
            return;
        } else {
            PicData picData = new PicData();
            file = new File(Constant.VehiclePath);
            file.mkdirs();// 创建文件夹
            // fileName = Constant.VehiclePath + name + ".jpg";
            //
            // createImage(fileName, bitmap); //创建文件(临时)
            // File imageFile = new File(fileName);
            // 将图片压缩至屏幕大小(大图)
            Bitmap myBitmap = BlurImage.decodeSampledBitmapFromPath(
                    Constant.VehiclePath + name + ".jpg", 480, 800);
            // 存储到SD卡 将要上传的大图(无 需删除)
            GetSystem.saveImageSD(myBitmap, Constant.VehiclePath, name
                    + "big_image.jpg", 50);
            Log.e("上传的大图", "宽 = " + myBitmap.getWidth());
            Log.e("上传的大图", "高= " + myBitmap.getHeight());
            Log.e("大图资源大小",
                    "size= " + myBitmap.getRowBytes() * myBitmap.getHeight()
                            / (1024 * 1024) + "M");
            // 获取正方形图片
            Bitmap squareBitmap = BlurImage.getSquareBitmap(myBitmap);
            Log.e("未压缩的正方形图片", "宽 = " + squareBitmap.getWidth());
            Log.e("未压缩的正方形图片", "高= " + squareBitmap.getHeight());
            // 存储到内存卡 用户压缩得到小图（需 要删除）
            GetSystem.saveImageSD(squareBitmap, Constant.VehiclePath, name
                    + "square_image.jpg", 50);
            small_image = BlurImage.decodeSampledBitmapFromPath(
                    Constant.VehiclePath + name + "square_image.jpg", 180, 180);
            // 存储到sd卡 上传需要 的小图 （无需删除）
            GetSystem.saveImageSD(small_image, Constant.VehiclePath, name
                    + "small_image.jpg", 50);

            Log.e("上传的小图", "宽 = " + small_image.getWidth());
            Log.e("上传的小图", "高 = " + small_image.getHeight());
            Log.e("小图资源大小",
                    "size= " + small_image.getRowBytes()
                            * small_image.getHeight() + "bytes");
            picData.setSmall_pic(Constant.VehiclePath + name
                    + "small_image.jpg");
            picData.setBig_pic(Constant.VehiclePath + name + "big_image.jpg");

            if (new File(Constant.VehiclePath, name + "square_image.jpg")
                    .exists()) {
                new File(Constant.VehiclePath, name + "square_image.jpg")
                        .delete();
            }
            if (new File(Constant.VehiclePath + name + ".jpg").exists()) {
                new File(Constant.VehiclePath + name + ".jpg").delete();
            }

            // 动态在LinearLayout中添加一张图片
            ImageView imageView = new ImageView(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(takePhoto.getHeight(), takePhoto.getHeight());  
            params.setMargins(5, 0, 0, 0);  
            imageView.setLayoutParams(params);  
            imageView.setBackgroundDrawable(new BitmapDrawable(small_image));
            imageView.setId(1);
            imageView.setOnClickListener(new ClickListener());
            imageView.setOnLongClickListener(onLongClickListener);
            linearLayout.addView(imageView);
            tag++;
            imageView.setTag(""+tag);
            picData.setTag(""+tag);
            picDatas.add(picData);
        }
    }

    int tag = 0;

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            String sdStatus = Environment.getExternalStorageState();
            if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
                Toast.makeText(this, "没有多余内存", 0).show();
                return;
            }
            // Bundle bundle = data.getExtras(); //TODO
            // Bitmap bitmap = (Bitmap) bundle.get("data");//
            Bitmap bitmap = BitmapFactory.decodeFile(Constant.VehiclePath
                    + name + ".jpg");
            // GetSystem.saveImageSD(bitmap, Constant.VehiclePath, name +
            // ".jpg");
            // Log.e("bitmap size","bitmap size = " + bitmap.getWidth());
            // Log.e("bitmap size","bitmap size = " + bitmap.getHeight());
            ShowBitMap(bitmap);
        }
    }

    public void createImage(String fileName, Bitmap bitmap) {
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

    public String getSDPath() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();// 获取跟目录
        }
        return sdDir.toString();
    }

    // 图片上传的状态监听函数
    public void onUploadDone(int responseCode, String message) {
        StringBuffer sb = new StringBuffer();
        switch (responseCode) {
        case UploadUtil.UPLOAD_SUCCESS_CODE:
            try {
                JSONObject jsonObject = new JSONObject(message);
                if (imageSize == 0) {
                    imageUrl = new JSONObject();
                }
                // 存储返回的图片url
                if (imageNum < picDatas.size()) {
                    // 小图上传成功
                    if (imageSize == 0) {
                        File smallImage = new File(picDatas.get(imageNum).small_pic);
                        if (smallImage.exists()) {
                            String imageFileUrl = jsonObject
                                    .getString("image_file_url");

                            // TODO
                            Log.e("小图上传成功返回的url:", imageFileUrl);
                            imageUrl.putOpt("small_pic", imageFileUrl);
                            String newPath = Constant.VehiclePath
                                    + imageFileUrl.substring(imageFileUrl
                                            .lastIndexOf("/") + 1);
                            File newFile = new File(newPath);
                            smallImage.renameTo(newFile);
                        }
                        imageSize = 1; // 上传大图
                        UploadUtil.getInstance().uploadFile(picDatas.get(imageNum).big_pic,
                                "image",
                                Constant.BaseUrl + "upload_image?auth_code="
                                        + Variable.auth_code,
                                new HashMap<String, String>());
                        return;
                    } else {
                        File bigImage = new File(picDatas.get(imageNum).big_pic);
                        if (bigImage.exists()) {
                            String imageFileUrl = jsonObject.getString(
                                    "image_file_url").toString();
                            imageUrl.putOpt("big_pic", imageFileUrl);
                            String newPath = Constant.VehiclePath
                                    + imageFileUrl.substring(imageFileUrl
                                            .lastIndexOf("/") + 1);
                            File newFile = new File(newPath);
                            bigImage.renameTo(newFile);
                            jsonDatas.put(imageUrl);
                        }
                        imageNum++;
                        imageSize = 0; // 上传下一张小图
                        if (imageNum < picDatas.size()) {
                            UploadUtil.getInstance().uploadFile(picDatas.get(imageNum).small_pic,
                                    "image",
                                    Constant.BaseUrl
                                            + "upload_image?auth_code="
                                            + Variable.auth_code,
                                    new HashMap<String, String>());
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (imageNum == picDatas.size()) {
                picDatas.clear();
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

    class PicData {
        String small_pic;
        String big_pic;
        String tag;
        boolean isDelete;

        public String getSmall_pic() {
            return small_pic;
        }

        public void setSmall_pic(String small_pic) {
            this.small_pic = small_pic;
        }

        public String getBig_pic() {
            return big_pic;
        }

        public void setBig_pic(String big_pic) {
            this.big_pic = big_pic;
        }

        public String getTag() {
            return tag;
        }

        public void setTag(String tag) {
            this.tag = tag;
        }

        public boolean isDelete() {
            return isDelete;
        }

        public void setDelete(boolean isDelete) {
            this.isDelete = isDelete;
        }        
    }
}
