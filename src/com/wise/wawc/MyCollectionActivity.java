package com.wise.wawc;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.wise.data.AdressData;
import com.wise.list.XListView;
import com.wise.list.XListView.IXListViewListener;
import com.wise.pubclas.Constant;
import com.wise.pubclas.NetThread;
import com.wise.pubclas.Variable;
import com.wise.service.CollectionAdapter;
import com.wise.sql.DBExcute;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
/**
 * 我的收藏
 * @author 王庆文
 */
public class MyCollectionActivity extends Activity implements IXListViewListener{
    private static final int frist_getdata = 1;
	private XListView collectionList;
	private CollectionAdapter collectionAdapter;
	
	ProgressDialog myDialog = null;
	
	DBExcute dBExcute = new DBExcute();
	List<AdressData> adressDatas = new ArrayList<AdressData>();
	
	boolean isGetDB = true; //上拉是否继续读取数据库
	int Toal = 0; //从那条记录读起
	int pageSize = 5 ; //每次读取的记录数目
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_collection);
		collectionList = (XListView) findViewById(R.id.my_collection_list);
		Button menuBt = (Button) findViewById(R.id.my_vechile_menu);
		menuBt.setOnClickListener(onClickListener);
		Button homeBt = (Button) findViewById(R.id.my_vechile_home);
		homeBt.setOnClickListener(onClickListener);
		
		//不设置上拉加载无效
		collectionList.setPullRefreshEnable(false);
		collectionList.setPullLoadEnable(true);
		collectionList.setXListViewListener(this);
		
		if(dBExcute.getTotalCount(Constant.TB_Collection, MyCollectionActivity.this) > 0){
		    //本地取数据
	        getCollectionDatas(Toal, pageSize);
		}else{
		    //服务器取数据
		    isGetDB = false;
		    String url = Constant.BaseUrl + "customer/" + Variable.cust_id + "/favorite?auth_code=" + Variable.auth_code;
		    new Thread(new NetThread.GetDataThread(handler, url, frist_getdata)).start();
		}
        
		collectionAdapter = new CollectionAdapter(this,adressDatas);
        collectionList.setAdapter(collectionAdapter);
	}
	
	Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
            case frist_getdata:
                try {
                    
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            default:
                break;
            }
        }	    
	};
	
	OnClickListener onClickListener = new OnClickListener() {
        
        @Override
        public void onClick(View v) {
            switch(v.getId()){
            case R.id.my_vechile_home:
                ActivityFactory.A.ToHome();
                break;
            case R.id.my_vechile_menu:
                ActivityFactory.A.LeftMenu();
                break;
            default :
                return;
            }
        }
    };
	
	@Override
	public void onRefresh() {}
	@Override
	public void onLoadMore() {
		Log.e("上拉加载","上拉加载");
		if(isGetDB){//读取数据库
		    getCollectionDatas(Toal, pageSize);
		    collectionAdapter.notifyDataSetChanged();
		    onLoad();
		}else{//读取服务器
		    System.out.println("读取服务器数据");
		    
		}
	}
	
	private void onLoad() {
		collectionList.stopRefresh();
		collectionList.stopLoadMore();
	}
	/**
	 * 
	 * @param start 从第几条读起
	 * @param pageSize 一次读取多少条
	 */
	private void getCollectionDatas(int start,int pageSize) {
	    System.out.println("start = " + start);
		List<AdressData> datas = dBExcute.getPageDatas(MyCollectionActivity.this, "select * from " + Constant.TB_Collection + " where Cust_id=? order by favorite_id desc limit ?,?", new String[]{Variable.cust_id,String.valueOf(start),String.valueOf(pageSize)});
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
