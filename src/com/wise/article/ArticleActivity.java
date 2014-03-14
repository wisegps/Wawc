package com.wise.article;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.wise.list.XListView;
import com.wise.list.XListView.IXListViewListener;
import com.wise.pubclas.Constant;
import com.wise.pubclas.GetSystem;
import com.wise.pubclas.NetThread;
import com.wise.pubclas.Variable;
import com.wise.wawc.R;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.AbsListView;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;

public class ArticleActivity extends Activity implements IXListViewListener {
    private static final String TAG = "ArticleActivity";
    private static final int RefreshData = 1;
    private static final int LoadData = 2;
    private static final int GetImage = 3;
    
    TextView tv_Memory;
    XListView lv_article;
    List<ArticleData> articleDatas = new ArrayList<ArticleData>();
    ArticleAdapter articleAdapter;

    String url = "http://wiwc.api.wisegps.cn/blog?auth_code=0d7272824d41655d5704e7c1c300a9a8&type=1&cust_id=72";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);
        tv_Memory = (TextView)findViewById(R.id.tv_Memory);
        lv_article = (XListView) findViewById(R.id.lv_article);
        articleAdapter = new ArticleAdapter(this, articleDatas);
        lv_article.setAdapter(articleAdapter);
        lv_article.setOnScrollListener(onScrollListener);
        lv_article.setXListViewListener(this);
        lv_article.setPullLoadEnable(true);
        lv_article.setPullRefreshEnable(true);
        new Thread(new NetThread.GetDataThread(handler, url, RefreshData))
                .start();
        tv_Memory.setText("内存" + Runtime.getRuntime().totalMemory() / 1024 / 1024 + "M");
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
            case RefreshData:
                articleDatas.addAll(0, jsonData(msg.obj.toString()));
                articleAdapter.notifyDataSetChanged();
                onLoad();
                break;

            case LoadData:
                articleDatas.addAll(jsonData(msg.obj.toString()));
                articleAdapter.notifyDataSetChanged();
                onLoad();
                break;
            case GetImage:
                Log.d(TAG, "GetImage");
                articleAdapter.notifyDataSetChanged();
                break;
            }
        }
    };

    OnScrollListener onScrollListener = new OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            switch (scrollState) {
            case OnScrollListener.SCROLL_STATE_IDLE:
                tv_Memory.setText("内存" + Runtime.getRuntime().totalMemory() / 1024 / 1024 + "M");
                int start = lv_article.getFirstVisiblePosition();
                int end = lv_article.getLastVisiblePosition();
                new Thread(new ImageThread(start, end)).start();
                break;

            case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                tv_Memory.setText("内存" + Runtime.getRuntime().totalMemory() / 1024 / 1024 + "M");
                break;
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem,
                int visibleItemCount, int totalItemCount) {
        }
    };
    class ImageThread extends Thread{
        int start;
        int end;
        public ImageThread(int start, int end){
            this.start = start;
            this.end = end;
        }
        @Override
        public void run() {
            super.run();
            for(int i = start ; i < end ; i++){
                if(i == articleDatas.size()){
                    break;
                }
                List<PicData> picDatas = articleDatas.get(i).getPicDatas();
                for(PicData picData : picDatas){
                    String small_pic = picData.getSmall_pic();
                    small_pic = small_pic.substring((small_pic.lastIndexOf("/") + 1), small_pic.length());
                    String path = Constant.VehiclePath + small_pic;
                    Bitmap bitmap = BitmapFactory.decodeFile(path);           
                    if(bitmap == null){
                        bitmap = GetSystem.getBitmapFromURL(picData.getSmall_pic());
                        if(bitmap == null){
                            Log.d(TAG, "bitmap为空");
                        }else{
                            GetSystem.saveImageSD(bitmap, Constant.VehiclePath, small_pic);
                            Message message = new Message();
                            message.what = GetImage;
                            handler.sendMessage(message);
                        }                        
                    }
                }
            }
        }
    }

    private List<ArticleData> jsonData(String result) {
        List<ArticleData> articleDatas = new ArrayList<ArticleData>();
        try {
            JSONArray jsonArray = new JSONArray(result);
            for (int i = 0; i < jsonArray.length(); i++) {
                ArticleData articleData = new ArticleData();

                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String create_time = jsonObject.getString("create_time");
                String logo = jsonObject.getString("logo");
                String content = jsonObject.getString("content");
                String title = jsonObject.getString("title");
                String name = jsonObject.getString("name");
                String cust_id = jsonObject.getString("cust_id");
                String blog_id = jsonObject.getString("blog_id");
                JSONArray jsonArrayPic = jsonObject.getJSONArray("pics");
                List<PicData> picDatas = new ArrayList<PicData>();
                for (int j = 0; j < jsonArrayPic.length(); j++) {
                    PicData picData = new PicData();
                    JSONObject jsonObjectPic = jsonArrayPic.getJSONObject(j);
                    String big_pic = jsonObjectPic.getString("big_pic");
                    String small_pic = jsonObjectPic.getString("small_pic");
                    picData.setBig_pic(big_pic);
                    picData.setSmall_pic(small_pic);
                    picDatas.add(picData);
                }
                articleData.setCreate_time(create_time);
                articleData.setLogo(logo);
                articleData.setContent(content);
                articleData.setTitle(title);
                articleData.setName(name);
                articleData.setCust_id(cust_id);
                articleData.setBlog_id(blog_id);
                articleData.setPicDatas(picDatas);
                articleDatas.add(articleData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return articleDatas;
    }

    @Override
    public void onRefresh() {
        Log.d(TAG, "onRefresh");
        String refreshUrl = url + "&max_id=" + articleDatas.get(0).getBlog_id();
        new Thread(new NetThread.GetDataThread(handler, refreshUrl, RefreshData))
                .start();
    }

    @Override
    public void onLoadMore() {
        Log.d(TAG, "onLoadMore");
        String loadUrl = url + "&min_id="
                + articleDatas.get(articleDatas.size()-1).getBlog_id();
        new Thread(new NetThread.GetDataThread(handler, loadUrl, LoadData)).start();
    }

    private void onLoad() {
        lv_article.stopRefresh();
        lv_article.stopLoadMore();
    }
}
