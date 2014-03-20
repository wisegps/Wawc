package com.wise.wawc;

import java.util.List;
import java.util.Map;
import com.wise.data.Article;
import com.wise.extend.MyScrollLayout;
import com.wise.pubclas.Constant;
import com.wise.pubclas.GetSystem;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

public class PicActivity extends Activity{
    private static final int get_pic = 1;
    List<Map<String,String>> imageList;
    ImageView mImageViews[];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pic);
        MyScrollLayout sh_pic = (MyScrollLayout)findViewById(R.id.sh_pic);
        
        Article article = (Article)getIntent().getSerializableExtra("article");
        imageList = article.getImageList();
        mImageViews = new ImageView[imageList.size()];    
        for(int i = 0 ; i < imageList.size() ; i++){
            View view = LayoutInflater.from(PicActivity.this).inflate(R.layout.item_pic, null);
            sh_pic.addView(view);
            ImageView imageView = (ImageView)view.findViewById(R.id.iv_pic);
            mImageViews[i] = imageView;
            Map<String,String> imageMap = imageList.get(i);
            String big_pic = imageMap.get("big_pic");
            System.out.println("big_pic = " + big_pic);
            big_pic = big_pic.substring((big_pic.lastIndexOf("/") + 1), big_pic.length());
            System.out.println("big_pic = " + big_pic);
            String path = Constant.VehiclePath + big_pic;
            System.out.println("path = " + path);
            Bitmap bitmap = BitmapFactory.decodeFile(path);           
            if(bitmap != null){
                System.out.println("bitmap不为空");
                imageView.setImageBitmap(bitmap);
            }else{
                System.out.println("bitmap为空");
            }
        }
        new Thread(new picThread()).start();
    }
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
            case get_pic:
                System.out.println("更新msg.arg1 = " +msg.arg1);
                mImageViews[msg.arg1].setImageBitmap(bitmap);
                break;
            }
        }        
    };
    Bitmap bitmap;
    class picThread extends Thread{
        @Override
        public void run() {
            super.run();
            for(int i = 0 ; i < imageList.size() ; i++){
                Map<String,String> imageMap = imageList.get(i);
                String big_pic = imageMap.get("big_pic");
                System.out.println("big_pic = " + big_pic);
                big_pic = big_pic.substring((big_pic.lastIndexOf("/") + 1), big_pic.length());
                System.out.println("big_pic = " + big_pic);
                String path = Constant.VehiclePath + big_pic;
                System.out.println("path = " + path);
                bitmap = BitmapFactory.decodeFile(path);           
                if(bitmap == null){
                    //读取图片
                    bitmap = GetSystem.getBitmapFromURL(imageMap.get("big_pic"));
                    GetSystem.saveImageSD(bitmap, Constant.VehiclePath, big_pic,100);
                    Message message = new Message();
                    message.what = get_pic;
                    message.arg1 = i;
                    handler.sendMessage(message);
                }
            }
        }
    }
}
