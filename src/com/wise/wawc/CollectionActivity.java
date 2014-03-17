package com.wise.wawc;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import com.wise.data.AdressData;
import com.wise.list.XListView;
import com.wise.list.XListView.IXListViewListener;
import com.wise.pubclas.Constant;
import com.wise.pubclas.GetSystem;
import com.wise.pubclas.NetThread;
import com.wise.pubclas.Variable;
import com.wise.service.CollectionAdapter;
import com.wise.service.CollectionAdapter.CollectionItemListener;
import com.wise.sql.DBExcute;
import android.app.Activity;
import android.content.ContentValues;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
/**
 * 我的收藏
 * @author 王庆文
 */
public class CollectionActivity extends Activity implements IXListViewListener{
    private static final String TAG = "CollectionActivity";
    private static final int frist_getdata = 1;
    private static final int load_getdata = 2;
    
    RelativeLayout rl_Note;
	private XListView lv_collection;
	private CollectionAdapter collectionAdapter;
	
	DBExcute dBExcute = new DBExcute();
	List<AdressData> adressDatas = new ArrayList<AdressData>();
	
	boolean isGetDB = true; //上拉是否继续读取数据库
	int Toal = 0; //从那条记录读起
	int pageSize = 5 ; //每次读取的记录数目
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_collection);
		rl_Note = (RelativeLayout)findViewById(R.id.rl_Note);
		
		lv_collection = (XListView) findViewById(R.id.lv_collection);        
        collectionAdapter = new CollectionAdapter(CollectionActivity.this,adressDatas);
        collectionAdapter.setCollectionItem(collectionItemListener);
		ImageView iv_menu = (ImageView) findViewById(R.id.iv_menu);
		iv_menu.setOnClickListener(onClickListener);
		
		lv_collection.setPullRefreshEnable(true);
		lv_collection.setPullLoadEnable(true);
		lv_collection.setXListViewListener(this);
		
		if(isGetDataUrl()){
            //服务器取数据
            Log.d(TAG, "服务器取数据");
            isGetDB = false;
            String url = Constant.BaseUrl + "customer/" + Variable.cust_id + "/favorite?auth_code=" + Variable.auth_code;
            new Thread(new NetThread.GetDataThread(handler, url, frist_getdata)).start();
		}else{
            Log.d(TAG, "本地取数据");
            //本地取数据
            getCollectionDatas(Toal, pageSize);
            collectionAdapter.notifyDataSetChanged();
            lv_collection.setAdapter(collectionAdapter);
            isNothingNote(false);
		}
	}
	
	Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
            case frist_getdata:
                dBExcute.DeleteDB(CollectionActivity.this, "delete from " + Constant.TB_Collection + " where Cust_id=" + Variable.cust_id);
                adressDatas.clear();
                jsonCollectionData(msg.obj.toString());
                collectionAdapter.notifyDataSetChanged();
                if(adressDatas.size() > 0){
                    isNothingNote(false);
                }
                onLoad();
                break;

            case load_getdata:
                jsonCollectionData(msg.obj.toString());
                collectionAdapter.notifyDataSetChanged();
                Log.d(TAG, "刷新完毕");
                onLoad();
                break;
            }
        }	    
	};
	
	OnClickListener onClickListener = new OnClickListener() {
        
        @Override
        public void onClick(View v) {
            switch(v.getId()){
            case R.id.iv_menu:
                ActivityFactory.A.LeftMenu();
                break;
            }
        }
    };
    
    CollectionItemListener collectionItemListener = new CollectionItemListener() {        
        @Override
        public void Delete(int position) {
            System.out.println(position);
            String url = Constant.BaseUrl + "favorite/" + adressDatas.get(position).get_id() + "?auth_code=" + Variable.auth_code;
            //删除服务器记录
            new Thread(new NetThread.DeleteThread(handler, url, 999)).start();
            //删除本地数据库
            dBExcute.DeleteDB(CollectionActivity.this, Constant.TB_Collection, "favorite_id = ?", new String[]{String.valueOf(adressDatas.get(position).get_id())});
            
            adressDatas.remove(position);
            collectionAdapter.notifyDataSetChanged();
        }

        @Override
        public void share(int position) {
            AdressData adressData = adressDatas.get(position);
            String url = "http://api.map.baidu.com/geocoder?location="
                    + adressData.getLat() + "," + adressData.getLon()
                    + "&coord_type=bd09ll&output=html";
            StringBuffer sb = new StringBuffer();
            sb.append("【地点】 ");
            sb.append(adressData.getName());
            sb.append(" 地址: " + adressData.getAdress());
            sb.append(" 电话: " + adressData.getPhone());
            sb.append(" " + url);
            GetSystem.share(CollectionActivity.this, sb.toString(), "",
                    (float) adressData.getLat(),
                    (float) adressData.getLon(),"地点");
        }
    };   

    private void isNothingNote(boolean isNote){
        if(isNote){
            rl_Note.setVisibility(View.VISIBLE);
            lv_collection.setVisibility(View.GONE);
        }else{
            rl_Note.setVisibility(View.GONE);
            lv_collection.setVisibility(View.VISIBLE);
        }
    }
    
    private boolean isGetDataUrl(){
        DBExcute dbExcute = new DBExcute();
        String sql = "select * from " + Constant.TB_Collection + " where Cust_id=?";
        int Total = dbExcute.getTotalCount(getApplicationContext(), sql, new String[]{Variable.cust_id});
        if(Total == 0){
            return true;
        }else{
            return false;
        }
    }
    
    private void jsonCollectionData(String result){
        try {
            JSONArray jsonArray = new JSONArray(result);
            for(int i = 0 ; i < jsonArray.length(); i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                AdressData adrDatas = new AdressData(); 
                adrDatas.set_id(jsonObject.getInt("favorite_id"));
                adrDatas.setAdress(jsonObject.getString("address"));
                adrDatas.setName(jsonObject.getString("name"));
                adrDatas.setPhone(jsonObject.getString("tel"));
                adrDatas.setLat(Double.parseDouble(jsonObject.getString("lat")));
                adrDatas.setLon(Double.parseDouble(jsonObject.getString("lon")));
                adressDatas.add(adrDatas);
                
                ContentValues values = new ContentValues();
                values.put("Cust_id", Variable.cust_id);
                values.put("favorite_id", adrDatas.get_id());
                values.put("name", adrDatas.getName());
                values.put("address", adrDatas.getAdress());
                values.put("tel", adrDatas.getPhone());
                values.put("lon", adrDatas.getLon());
                values.put("lat", adrDatas.getLat());
                dBExcute.InsertDB(CollectionActivity.this, values, Constant.TB_Collection);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	
	@Override
	public void onRefresh() {
	    String url = Constant.BaseUrl + "customer/" + Variable.cust_id + "/favorite?auth_code=" + Variable.auth_code;
        new Thread(new NetThread.GetDataThread(handler, url, frist_getdata)).start();
	}
	@Override
	public void onLoadMore() {
		Log.e("上拉加载","上拉加载");
		if(isGetDB){//读取数据库
		    getCollectionDatas(Toal, pageSize);
		    collectionAdapter.notifyDataSetChanged();
		    onLoad();
		}else{//读取服务器
		    System.out.println("读取服务器数据");
		    if(adressDatas.size() != 0){
		        int id = adressDatas.get(adressDatas.size() - 1).get_id();
		        String url = Constant.BaseUrl + "customer/" + Variable.cust_id + "/favorite?auth_code=" + Variable.auth_code + "&&min_id=" + id;
	            new Thread(new NetThread.GetDataThread(handler, url, load_getdata)).start();
		    }		    
		}
	}
	
	private void onLoad() {
		lv_collection.stopRefresh();
		lv_collection.stopLoadMore();
	}
	/**
	 * 
	 * @param start 从第几条读起
	 * @param pageSize 一次读取多少条
	 */
	private void getCollectionDatas(int start,int pageSize) {
	    System.out.println("start = " + start);
		List<AdressData> datas = dBExcute.getPageDatas(CollectionActivity.this, "select * from " + Constant.TB_Collection + " where Cust_id=? order by favorite_id desc limit ?,?", new String[]{Variable.cust_id,String.valueOf(start),String.valueOf(pageSize)});
		adressDatas.addAll(datas);
		Toal += datas.size();//记录位置
		if(datas.size() == pageSize){
		    //继续读取数据库
		}else{
		    //数据库读取完毕
		    isGetDB = false;
		}
	}
}