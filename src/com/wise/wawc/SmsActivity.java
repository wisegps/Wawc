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
import android.widget.Toast;
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
    int pageSize = 5 ; //每次读取的记录数目
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sms);
		rl_Note = (RelativeLayout)findViewById(R.id.rl_Note);
		lv_sms = (XListView)findViewById(R.id.lv_sms);
		lv_sms.setPullLoadEnable(true);
		lv_sms.setXListViewListener(this);
		lv_sms.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
				if(arg2 !=0 || arg2 != (smsDataList.size()+1)){
					Toast.makeText(getApplicationContext(), Msg_type(smsDataList.get(arg2 - 1).getMsg_type()) + smsDataList.get(arg2-1).getContent(), Toast.LENGTH_LONG).show();
				}
			}});
		newAdapter = new NewAdapter(this, smsDataList);
		lv_sms.setAdapter(newAdapter);
		ImageView iv_sms = (ImageView)findViewById(R.id.iv_sms);
		iv_sms.setOnClickListener(new OnClickListener() {			
			public void onClick(View v) {
			    ActivityFactory.A.LeftMenu();
			}
		});
		if(isGetDataUrl()){
		    String url = "http://wiwc.api.wisegps.cn/customer/" + Variable.cust_id + "/notification?auth_code=" + Variable.auth_code;
		    new Thread(new NetThread.GetDataThread(handler, url, GET_SMS)).start();
		}else{
		    smsDataList.addAll(0,getSmsDatas(Toal, pageSize));
            newAdapter.notifyDataSetChanged();
            isNothingNote(false);
		}	
	}	
	
	private String Msg_type(String Type){
		if(Type.equals("1")){
			return "10010:";
		}else if (Type.equals("2")) {
			return "终端消息:";
		}else{
			return "平台消息:";
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
                smsData.setRcv_time((jsonObject.getString("rcv_time").replace("T", " ").substring(0, 10)));
                String status = "";
                if(jsonObject.opt("status") == null){
                    
                }else{
                    status = jsonObject.getString("status");
                }
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
	            convertView = mInflater.inflate(R.layout.new_row, null);
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
	        if(Type.equals("1")){
	            holder.tv_new_Regnum.setText("10010");
	        }else if (Type.equals("2")) {
	            holder.tv_new_Regnum.setText("终端消息");
	        }else{
	            holder.tv_new_Regnum.setText("平台消息");
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
	}
}
