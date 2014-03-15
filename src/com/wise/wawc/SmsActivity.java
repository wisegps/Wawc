package com.wise.wawc;

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
import com.wise.sql.DBExcute;
import com.wise.sql.DBHelper;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextPaint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
/**
 * 未读消息页面
 * @author honesty
 *
 */
public class SmsActivity extends Activity implements IXListViewListener{
	private String TAG = "SmsActivity";
	private final int GET_SMS = 1;	
	private final int GET_NEXT_SMS = 2;	
	
	RelativeLayout rl_Note;
	XListView lv_sms;
	ProgressDialog Dialog = null;    //等待框    
	NewAdapter newAdapter;
	List<SmsData> smsDataList = new ArrayList<SmsData>();
	DBExcute dbExcute = new DBExcute();
	
	boolean isGetDB = true; //上拉是否继续读取数据库
    int Toal = 0; //从那条记录读起
    int pageSize = 20 ; //每次读取的记录数目
    boolean isJump;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sms);
		rl_Note = (RelativeLayout)findViewById(R.id.rl_Note);
		lv_sms = (XListView)findViewById(R.id.lv_sms);
		lv_sms.setPullLoadEnable(true);
		lv_sms.setPullRefreshEnable(true);
		lv_sms.setXListViewListener(this);
		lv_sms.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
			    Log.d(TAG, "arg2 = " + arg2);
				if(arg2 !=0 || arg2 != (smsDataList.size()+1)){
				    String Type = smsDataList.get(arg2 -1).getMsg_type();
		            if(Type.equals("0")){
		                
		            }else if(Type.equals("1")){
		                Intent intent = new Intent(SmsActivity.this, CarRemindActivity.class);
		                if(smsDataList.get(arg2 -1).getObj_id() != null){
		                    intent.putExtra("Obj_id", smsDataList.get(arg2 -1).getObj_id());
		                }          
                        startActivity(intent);
		            }else if (Type.equals("2")) {
		                //holder.tv_new_Regnum.setText("车辆故障");
		                //startActivity(new Intent(SmsActivity.this, CarRemindActivity.class));
		            }else if (Type.equals("3")){
		                //holder.tv_new_Regnum.setText("车辆报警");
		                //startActivity(new Intent(SmsActivity.this, CarRemindActivity.class));
		            }else if (Type.equals("4")){
		                startActivity(new Intent(SmsActivity.this, TrafficActivity.class));
		            }
				}
			}});
		newAdapter = new NewAdapter(this, smsDataList);
		lv_sms.setAdapter(newAdapter);
		ImageView iv_sms = (ImageView)findViewById(R.id.iv_sms);
		iv_sms.setOnClickListener(new OnClickListener() {			
			public void onClick(View v) {
			    if(isJump){
			        finish();
			    }else{
	                ActivityFactory.A.LeftMenu();
			    }
			}
		});
		Intent intent = getIntent();
		isJump = intent.getBooleanExtra("isJump", false);
		if(isJump){
		    iv_sms.setImageResource(R.drawable.nav_back);
		}else{
		    iv_sms.setImageResource(R.drawable.side_left);
		}
		if(isGetDataUrl()){
		    String url = "http://wiwc.api.wisegps.cn/customer/" + Variable.cust_id + "/notification?auth_code=" + Variable.auth_code;
		    new Thread(new NetThread.GetDataThread(handler, url, GET_SMS)).start();
		}else{
		    smsDataList.addAll(0,getSmsDatas(Toal, pageSize));
            newAdapter.notifyDataSetChanged();
            isNothingNote(false);
            if(smsDataList.size() != 0){
                int id = smsDataList.get(0).getNoti_id();
                String url = "http://wiwc.api.wisegps.cn/customer/" + Variable.cust_id + "/notification?auth_code=" 
                        + Variable.auth_code + "&max_id=" + id;
                new Thread(new NetThread.GetDataThread(handler, url, GET_SMS)).start();
            }
		}	
	}
	
	Handler handler = new Handler(){
		@Override
		public void dispatchMessage(Message msg) {
			super.dispatchMessage(msg);
			switch (msg.what) {
			case GET_SMS:
			    smsDataList.addAll(0,jsonData(msg.obj.toString()));
			    newAdapter.notifyDataSetChanged();
			    onLoad();
			    if(smsDataList.size() > 0){
			        isNothingNote(false);
			    }else{
			        isNothingNote(true);
			    }
				break;
			case GET_NEXT_SMS:
			    smsDataList.addAll(jsonData(msg.obj.toString()));
			    newAdapter.notifyDataSetChanged();
			    onLoad();
				break;
			}
		}		
	};
	
	private void isNothingNote(boolean isNote){
        if(isNote){
            rl_Note.setVisibility(View.VISIBLE);
            lv_sms.setVisibility(View.GONE);
        }else{
            rl_Note.setVisibility(View.GONE);
            lv_sms.setVisibility(View.VISIBLE);
        }
    }
	
	private void onLoad() {
	    Log.d(TAG, "停止");
		lv_sms.stopRefresh();
		lv_sms.stopLoadMore();
		lv_sms.setRefreshTime(GetSystem.GetNowTime());
	}
	public void onRefresh() {
	    if(smsDataList.size() != 0){
            int id = smsDataList.get(0).getNoti_id();
            String url = "http://wiwc.api.wisegps.cn/customer/" + Variable.cust_id + "/notification?auth_code=" 
                    + Variable.auth_code + "&max_id=" + id;
            new Thread(new NetThread.GetDataThread(handler, url, GET_SMS)).start();
        }
	}
	public void onLoadMore() {
	    if(isGetDB){//读取数据库
	        System.out.println("读取数据库");
	        smsDataList.addAll(0,getSmsDatas(Toal, pageSize));
            newAdapter.notifyDataSetChanged();
            onLoad();
        }else{//读取服务器
            System.out.println("读取服务器数据");
            if(smsDataList.size() != 0){
                int id = smsDataList.get(smsDataList.size() - 1).getNoti_id();
                String url = "http://wiwc.api.wisegps.cn/customer/" + Variable.cust_id + "/notification?auth_code=" 
                        + Variable.auth_code + "&min_id=" + id;
                new Thread(new NetThread.GetDataThread(handler, url, GET_NEXT_SMS)).start();
            }           
        }
	}
	
	private boolean isGetDataUrl(){
        String sql = "select * from " + Constant.TB_Sms + " where cust_id=?";
        int Total = dbExcute.getTotalCount(getApplicationContext(), sql, new String[]{Variable.cust_id});
        if(Total == 0){
            return true;
        }else{
            return false;
        }
    }
	
	private List<SmsData> jsonData(String result){
	    try {
	        List<SmsData> Datas = new ArrayList<SmsData>();
            JSONArray jsonArray = new JSONArray(result);
            for(int i = 0 ; i < jsonArray.length() ; i++){
                SmsData smsData = new SmsData();
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                smsData.setContent(jsonObject.getString("content"));
                smsData.setLat(jsonObject.getString("lat"));
                smsData.setLon(jsonObject.getString("lon"));
                smsData.setMsg_type(jsonObject.getString("msg_type"));
                smsData.setNoti_id(jsonObject.getInt("noti_id"));
                String Rcv_time = GetSystem.ChangeTimeZone(jsonObject.getString("rcv_time").replace("T", " ").substring(0, 19));
                smsData.setRcv_time(Rcv_time.substring(5, 16));
                String status = "";
                if(jsonObject.opt("status") == null){
                    
                }else{
                    status = jsonObject.getString("status");
                }
                String obj_id = "";
                if(jsonObject.opt("obj_id") == null){
                    
                }else{
                    obj_id = jsonObject.getString("obj_id");
                }
                smsData.setObj_id(obj_id);
                smsData.setStatus(status);
                Datas.add(smsData);
                
                ContentValues values = new ContentValues();
                values.put("cust_id", Variable.cust_id);
                values.put("noti_id", smsData.getNoti_id());
                values.put("msg_type", smsData.getMsg_type());
                values.put("content", smsData.getContent());
                values.put("rcv_time", smsData.getRcv_time());
                values.put("lat", smsData.getLat());
                values.put("lon", smsData.getLon());
                values.put("status", smsData.getStatus());
                values.put("obj_id", smsData.getObj_id());
                dbExcute.InsertDB(SmsActivity.this, values, Constant.TB_Sms); 
            }
            return Datas;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return smsDataList;
	}
	
	private List<SmsData> getSmsDatas(int start,int pageSize) {
        System.out.println("start = " + start);
        List<SmsData> datas = getPageDatas(SmsActivity.this, "select * from " + Constant.TB_Sms + " where cust_id=? order by noti_id desc limit ?,?", new String[]{Variable.cust_id,String.valueOf(start),String.valueOf(pageSize)});
        Toal += datas.size();//记录位置
        if(datas.size() == pageSize){
            //继续读取数据库
        }else{
            //数据库读取完毕
            isGetDB = false;
        }
        return datas;
    }
    
    public List<SmsData> getPageDatas(Context context,String sql,String[] whereClause){
        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, whereClause);
        List<SmsData> Datas = new ArrayList<SmsData>();
        while(cursor.moveToNext()){
            SmsData smsData = new SmsData();
            smsData.setNoti_id(cursor.getInt(cursor.getColumnIndex("noti_id")));
            smsData.setMsg_type(cursor.getString(cursor.getColumnIndex("msg_type")));
            smsData.setContent(cursor.getString(cursor.getColumnIndex("content")));
            smsData.setRcv_time(cursor.getString(cursor.getColumnIndex("rcv_time")));
            smsData.setLat(cursor.getString(cursor.getColumnIndex("lat")));
            smsData.setLon(cursor.getString(cursor.getColumnIndex("lon")));
            smsData.setStatus(cursor.getString(cursor.getColumnIndex("status")));
            smsData.setObj_id(cursor.getString(cursor.getColumnIndex("obj_id")));
            Datas.add(smsData);
        }
        cursor.close();
        db.close();
        return Datas;
    }
	
	class NewAdapter extends BaseAdapter{
	    private LayoutInflater mInflater;
	    Context mContext;
	    List<SmsData> smsDatas;
	    public NewAdapter(Context context,List<SmsData> smsDatas){
	        mInflater = LayoutInflater.from(context);
	        mContext = context;
	        this.smsDatas = smsDatas;
	    }

	    public int getCount() {
	        return smsDatas.size();
	    }

	    public Object getItem(int position) {
	        return smsDatas.get(position);
	    }

	    public long getItemId(int position) {
	        return position;
	    }

	    public View getView(final int position, View convertView, ViewGroup parent) {
	        ViewHolder holder;
	        if (convertView == null) {
	            convertView = mInflater.inflate(R.layout.item_sms, null);
	            holder = new ViewHolder();
	            holder.tv_new_content = (TextView) convertView.findViewById(R.id.tv_new_content);
	            holder.tv_new_time = (TextView)convertView.findViewById(R.id.tv_new_time);
	            holder.tv_new_Regnum = (TextView)convertView.findViewById(R.id.tv_new_Regnum);
	            convertView.setTag(holder);
	        } else {
	            holder = (ViewHolder) convertView.getTag();
	        }
	        holder.tv_new_content.setText(smsDatas.get(position).getContent());
	        holder.tv_new_time.setText(smsDatas.get(position).getRcv_time());
	        
	        TextPaint tp = holder.tv_new_content.getPaint();
	        tp.setStrokeWidth(0);
	        tp.setFakeBoldText(false);
	        
	        TextPaint tt = holder.tv_new_time.getPaint();
	        tt.setStrokeWidth(0);
	        tt.setFakeBoldText(false);
	        
	        TextPaint tr = holder.tv_new_Regnum.getPaint();
	        tr.setStrokeWidth(0);
	        tr.setFakeBoldText(false);
	        
	        String Type = smsDatas.get(position).getMsg_type();
	        if(Type.equals("0")){
                holder.tv_new_Regnum.setText("系统消息");
            }else if(Type.equals("1")){
	            holder.tv_new_Regnum.setText("车务提醒");
	        }else if (Type.equals("2")) {
	            holder.tv_new_Regnum.setText("车辆故障");
	        }else if (Type.equals("3")){
	            holder.tv_new_Regnum.setText("车辆报警");
	        }else if (Type.equals("4")){
                holder.tv_new_Regnum.setText("违章提醒");
            }
	        return convertView;
	    }
	    private class ViewHolder {
	        TextView tv_new_Regnum,tv_new_content,tv_new_time;
	    }
	}
	
	class SmsData{
	    public String lat;
	    public String lon;
	    public String rcv_time;
	    public String msg_type;
	    public String content;
	    public int noti_id;
	    public String status;
	    public String obj_id;
	    
	    public String getLat() {
	        return lat;
	    }
	    public void setLat(String lat) {
	        this.lat = lat;
	    }
	    public String getLon() {
	        return lon;
	    }
	    public void setLon(String lon) {
	        this.lon = lon;
	    }
	    public String getRcv_time() {
	        return rcv_time;
	    }
	    public void setRcv_time(String rcv_time) {
	        this.rcv_time = rcv_time;
	    }
	    public String getMsg_type() {
	        return msg_type;
	    }
	    public void setMsg_type(String msg_type) {
	        this.msg_type = msg_type;
	    }
	    public String getContent() {
	        return content;
	    }
	    public void setContent(String content) {
	        this.content = content;
	    }	    
        public int getNoti_id() {
            return noti_id;
        }
        public void setNoti_id(int noti_id) {
            this.noti_id = noti_id;
        }
        public String getStatus() {
            return status;
        }
        public void setStatus(String status) {
            this.status = status;
        }
        public String getObj_id() {
            return obj_id;
        }
        public void setObj_id(String obj_id) {
            this.obj_id = obj_id;
        }        
	}
}