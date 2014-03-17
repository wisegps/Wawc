package com.wise.wawc;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import com.wise.extend.CarAdapter;
import com.wise.list.XListView;
import com.wise.list.XListView.IXListViewListener;
import com.wise.pubclas.Constant;
import com.wise.pubclas.GetSystem;
import com.wise.pubclas.NetThread;
import com.wise.pubclas.Variable;
import com.wise.sql.DBExcute;
import com.wise.sql.DBHelper;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;
/**
 * 车辆违章
 * @author honesty
 */
public class TrafficActivity extends Activity implements IXListViewListener{
    private static final String TAG = "TrafficActivity";
    private static final int refresh_traffic = 2;
    private static final int load_traffic = 3;
	
    HorizontalScrollView hsv_car;
    XListView lv_activity_traffic;
    RelativeLayout rl_Note;
    LinearLayout ll_info;
    TextView tv_total_score,tv_total_fine;
    
    DBExcute dbExcute = new DBExcute();
	CarAdapter carAdapter;
	List<TrafficData> trafficDatas = new ArrayList<TrafficData>();
	TrafficAdapter trafficAdapter;

    String Car_name = "";
	boolean isGetDB = true; //上拉是否继续读取数据库
    int Toal = 0; //从那条记录读起
    int pageSize = 10 ; //每次读取的记录数目
    int total_score = 0;
    int total_fine = 0;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_traffic);
		rl_Note = (RelativeLayout)findViewById(R.id.rl_Note);
		ll_info = (LinearLayout)findViewById(R.id.ll_info);
		ImageView iv_activity_traffic_back = (ImageView)findViewById(R.id.iv_activity_traffic_back);
		iv_activity_traffic_back.setOnClickListener(onClickListener);
		tv_total_score = (TextView)findViewById(R.id.tv_total_score);
		tv_total_fine = (TextView)findViewById(R.id.tv_total_fine);
        lv_activity_traffic = (XListView)findViewById(R.id.lv_activity_traffic);
        lv_activity_traffic.setXListViewListener(this);
        lv_activity_traffic.setPullLoadEnable(true);
        lv_activity_traffic.setPullRefreshEnable(true);
        trafficAdapter = new TrafficAdapter();
        lv_activity_traffic.setAdapter(trafficAdapter);
        
        hsv_car = (HorizontalScrollView)findViewById(R.id.hsv_car);
		GridView gv_activity_traffic = (GridView)findViewById(R.id.gv_activity_traffic);
        carAdapter = new CarAdapter(TrafficActivity.this,Variable.carDatas);
        gv_activity_traffic.setAdapter(carAdapter);
        
        int px = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 120, getResources().getDisplayMetrics());
		LayoutParams params = new LayoutParams((Variable.carDatas.size() * (px + 10)+10),LayoutParams.WRAP_CONTENT);
		gv_activity_traffic.setLayoutParams(params);
		gv_activity_traffic.setColumnWidth(px);
		gv_activity_traffic.setHorizontalSpacing(10);
		gv_activity_traffic.setStretchMode(GridView.NO_STRETCH);
		gv_activity_traffic.setNumColumns(Variable.carDatas.size());
		gv_activity_traffic.setOnItemClickListener(onItemClickListener);	
		if (Variable.carDatas != null && Variable.carDatas.size() > 0) {
		    SharedPreferences preferences = getSharedPreferences(Constant.sharedPreferencesName, Context.MODE_PRIVATE);
	        int DefaultVehicleID = preferences.getInt(Constant.DefaultVehicleID, 0);
	        GetData(DefaultVehicleID);
            if(Variable.carDatas.size() == 1){
                hsv_car.setVisibility(View.GONE);
            }else{
                hsv_car.setVisibility(View.VISIBLE);
            }
        }
	}
	
	Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
            case refresh_traffic:
                trafficDatas.addAll(0, jsonTrafficData(msg.obj.toString()));
                trafficAdapter.notifyDataSetChanged();
                tv_total_score.setText(String.format(getResources().getString(R.string.total_score),total_score));
                tv_total_fine.setText(String.format(getResources().getString(R.string.total_fine),total_fine));
                onLoad();
                if(trafficDatas.size() == 0){
                    isNothingNote(true);
                }else{
                    isNothingNote(false);
                }
                break;
            case load_traffic:
                List<TrafficData> Datas = jsonTrafficData(msg.obj.toString());
                trafficDatas.addAll(Datas);
                trafficAdapter.notifyDataSetChanged();
                onLoad();
                if(trafficDatas.size() == 0){
                    isNothingNote(true);
                }else{
                    isNothingNote(false);
                }
                tv_total_score.setText(String.format(getResources().getString(R.string.total_score),total_score));
                tv_total_fine.setText(String.format(getResources().getString(R.string.total_fine),total_fine));
                if(Datas.size() == 0){
                    lv_activity_traffic.setPullLoadEnable(false);
                }
                break;
            }
        }	    
	};
	OnClickListener onClickListener = new OnClickListener() {		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.iv_activity_traffic_back:
				finish();
				break;
			}
		}
	};
	OnItemClickListener onItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
		    lv_activity_traffic.setPullLoadEnable(true);
		    GetData(arg2);
		}
	};
	private void GetData(int arg2){
	    for(int i = 0 ; i < Variable.carDatas.size() ; i++){
            Variable.carDatas.get(i).setCheck(false);
        }
        Variable.carDatas.get(arg2).setCheck(true);
        carAdapter.notifyDataSetChanged();
        trafficDatas.clear();
        Car_name = Variable.carDatas.get(arg2).getObj_name();
        Toal = 0;
        total_score = 0;
        total_fine = 0;
        boolean isUrl = isGetDataUrl(Car_name);
        lv_activity_traffic.setPullLoadEnable(true);
        if(isUrl){
            isGetDB = false;
            //从服务器读取数据
            Log.d(TAG, "从服务器读取数据");
            try {
                String url = Constant.BaseUrl + "vehicle/" + URLEncoder.encode(Car_name, "UTF-8") + "/violation?auth_code=" + Variable.auth_code;
                new Thread(new NetThread.GetDataThread(handler, url, refresh_traffic)).start();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }else{
            isGetDB = true;
            Log.d(TAG, "从本地读取数据");
            List<TrafficData> Datas = getTrafficDatas(Toal, pageSize);
            trafficDatas.addAll(Datas);
            trafficAdapter.notifyDataSetChanged();
            tv_total_score.setText(String.format(getResources().getString(R.string.total_score),total_score));
            tv_total_fine.setText(String.format(getResources().getString(R.string.total_fine),total_fine));
            if(trafficDatas.size() == 0){
                isNothingNote(true);
            }else{
                isNothingNote(false);
            }
            if(Datas.size() != pageSize){
                lv_activity_traffic.setPullLoadEnable(false);
            }
        }
	}
	private void isNothingNote(boolean isNote){
	    if(isNote){
	        rl_Note.setVisibility(View.VISIBLE);
	        ll_info.setVisibility(View.GONE);
	    }else{
	        rl_Note.setVisibility(View.GONE);
	        ll_info.setVisibility(View.VISIBLE);
	    }
	}
	/**
	 * 判断本地是否有记录
	 * @param Car_name
	 * @return
	 */
	private boolean isGetDataUrl(String Car_name){
	    String sql = "select * from " + Constant.TB_Traffic + " where Car_name=?";
	    int Total = dbExcute.getTotalCount(getApplicationContext(), sql, new String[]{Car_name});
	    if(Total == 0){
	        return true;
	    }else{
	        return false;
	    }
	}
	/**
	 * 解析json数据
	 * @param result
	 */
	private List<TrafficData> jsonTrafficData(String result){
	    List<TrafficData> Datas = new ArrayList<TrafficData>();
	    try {
	        JSONObject jsonObject1 = new JSONObject(result);
	        JSONArray jsonArray = jsonObject1.getJSONArray("data");
            for(int i = 0 ; i < jsonArray.length() ; i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String Time = GetSystem.ChangeTimeZone(jsonObject.getString("vio_time").replace("T", " ").substring(0, 19));
                TrafficData trafficData = new TrafficData();
                trafficData.setObj_id(jsonObject.getString("vio_id"));
                trafficData.setAction(jsonObject.getString("action"));
                trafficData.setLocation(jsonObject.getString("location"));
                trafficData.setDate(Time);
                trafficData.setScore(jsonObject.getInt("score"));
                trafficData.setFine(jsonObject.getInt("fine"));
                trafficData.setStatus(jsonObject.getInt("status"));
                trafficData.setCity(jsonObject.getString("city"));
                Datas.add(trafficData);
                
                ContentValues values = new ContentValues();
                values.put("obj_id", jsonObject.getString("vio_id"));
                values.put("Car_name", jsonObject.getString("obj_name"));
                values.put("create_time", Time);
                values.put("action", trafficData.getAction());
                values.put("location", trafficData.getLocation());
                values.put("score", trafficData.getScore());
                values.put("fine", trafficData.getFine());
                values.put("status", trafficData.getStatus());
                values.put("city", trafficData.getCity());
                dbExcute.InsertDB(TrafficActivity.this, values, Constant.TB_Traffic);   
                total_score += trafficData.getScore(); 
                total_fine += trafficData.getFine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
	    return Datas;
	}
	
	private List<TrafficData> getTrafficDatas(int start,int pageSize) {
        System.out.println("start = " + start);
        List<TrafficData> datas = getPageDatas(TrafficActivity.this, "select * from " + Constant.TB_Traffic + " where Car_name=? order by obj_id desc limit ?,?", new String[]{Car_name,String.valueOf(start),String.valueOf(pageSize)});
        Toal += datas.size();//记录位置
        if(datas.size() == pageSize){
            //继续读取数据库
        }else{
            //数据库读取完毕
            isGetDB = false;
        }
        return datas;
    }
	
	public List<TrafficData> getPageDatas(Context context,String sql,String[] whereClause){
        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, whereClause);
        List<TrafficData> Datas = new ArrayList<TrafficData>();
        while(cursor.moveToNext()){
            TrafficData trafficData = new TrafficData();
            trafficData.setObj_id(cursor.getString(cursor.getColumnIndex("obj_id")));
            trafficData.setAction(cursor.getString(cursor.getColumnIndex("action")));
            trafficData.setLocation(cursor.getString(cursor.getColumnIndex("location")));
            trafficData.setDate(cursor.getString(cursor.getColumnIndex("create_time")));
            trafficData.setCity(cursor.getString(cursor.getColumnIndex("city")));
            trafficData.setScore(cursor.getInt(cursor.getColumnIndex("score")));
            trafficData.setFine(cursor.getInt(cursor.getColumnIndex("fine")));
            trafficData.setStatus(cursor.getInt(cursor.getColumnIndex("status")));
            Datas.add(trafficData);
            total_score += cursor.getInt(cursor.getColumnIndex("score")); 
            total_fine += cursor.getInt(cursor.getColumnIndex("fine")); 
        }
        cursor.close();
        db.close();
        return Datas;
    }
	
	private class TrafficAdapter extends BaseAdapter{
		LayoutInflater mInflater = LayoutInflater.from(TrafficActivity.this);
		@Override
		public int getCount() {
			return trafficDatas.size();
		}
		@Override
		public Object getItem(int position) {
			return trafficDatas.get(position);
		}
		@Override
		public long getItemId(int position) {
			return position;
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.item_traffic, null);
				holder = new ViewHolder();
				holder.tv_item_traffic_data = (TextView) convertView.findViewById(R.id.tv_item_traffic_data);
				holder.tv_item_traffic_adress = (TextView)convertView.findViewById(R.id.tv_item_traffic_adress);
				holder.tv_item_traffic_content = (TextView)convertView.findViewById(R.id.tv_item_traffic_content);
				holder.tv_item_traffic_fraction = (TextView)convertView.findViewById(R.id.tv_item_traffic_fraction);
				holder.tv_item_traffic_money = (TextView)convertView.findViewById(R.id.tv_item_traffic_money);
				holder.tv_status = (TextView)convertView.findViewById(R.id.tv_status);
				holder.iv_traffic_share = (ImageView)convertView.findViewById(R.id.iv_traffic_share);
				holder.iv_traffic_help = (ImageView)convertView.findViewById(R.id.iv_traffic_help);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			final TrafficData trafficData = trafficDatas.get(position);
			if(trafficData.getStatus() == 0){
			    holder.tv_status.setText("(未处理)");
			    holder.tv_status.setTextColor(getResources().getColor(R.color.red));
			}else{
			    holder.tv_status.setText("(已处理)");
			    holder.tv_status.setTextColor(getResources().getColor(R.color.common_inactive));
			}
			holder.tv_item_traffic_data.setText(trafficData.getDate().substring(0, 16));
			holder.tv_item_traffic_adress.setText(trafficData.getLocation());
			holder.tv_item_traffic_content.setText(trafficData.getAction());
			holder.tv_item_traffic_fraction.setText("扣分: "+trafficData.getScore());
			holder.tv_item_traffic_money.setText("罚款: "+trafficData.getFine());
			holder.iv_traffic_share.setOnClickListener(new OnClickListener() {                
                @Override
                public void onClick(View v) {
                    StringBuffer sb = new StringBuffer();
                    sb.append("【违章】");
                    sb.append(trafficData.getDate());
                    sb.append("," + trafficData.getLocation());
                    sb.append("," + trafficData.getAction());
                    sb.append("," + trafficData.getScore());
                    sb.append("," + trafficData.getFine());
                    GetSystem.share(TrafficActivity.this, sb.toString(), "",0,0,"违章");
                }
            });
			holder.iv_traffic_help.setOnClickListener(new OnClickListener() {                
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(TrafficActivity.this,
                            DealAddressActivity.class);
                    intent.putExtra("Title", "车辆违章");
                    intent.putExtra("Type", 3);
                    intent.putExtra("city", trafficData.getCity());
                    startActivity(intent);
                }
            });
			return convertView;
		}	
		private class ViewHolder {
			TextView tv_item_traffic_data,tv_item_traffic_adress,tv_item_traffic_content,
						tv_item_traffic_fraction,tv_item_traffic_money,tv_status;
			ImageView iv_traffic_share,iv_traffic_help;
		}
	}
	
	private class TrafficData{
	    String obj_id;
		String date;
		String action;
		String location;
		String city;
		int score;
		int fine;
		int status;
		
        public String getObj_id() {
            return obj_id;
        }
        public void setObj_id(String obj_id) {
            this.obj_id = obj_id;
        }
        public String getDate() {
            return date;
        }
        public void setDate(String date) {
            this.date = date;
        }
        public String getAction() {
            return action;
        }
        public void setAction(String action) {
            this.action = action;
        }
        public String getLocation() {
            return location;
        }
        public void setLocation(String location) {
            this.location = location;
        }
        public int getScore() {
            return score;
        }
        public void setScore(int score) {
            this.score = score;
        }
        public int getFine() {
            return fine;
        }
        public void setFine(int fine) {
            this.fine = fine;
        }
        public int getStatus() {
            return status;
        }
        public void setStatus(int status) {
            this.status = status;
        }
        public String getCity() {
            return city;
        }
        public void setCity(String city) {
            this.city = city;
        }
        @Override
        public String toString() {
            return "TrafficData [obj_id=" + obj_id + ", date=" + date
                    + ", action=" + action + ", location=" + location
                    + ", city=" + city + ", score=" + score + ", fine=" + fine
                    + ", status=" + status + "]";
        }        
	}

    @Override
    public void onRefresh() {
        try {
            String url = Constant.BaseUrl + "vehicle/" + URLEncoder.encode(Car_name, "UTF-8") + "/violation?auth_code=" + Variable.auth_code + "&max_id=" + trafficDatas.get(0).getObj_id();
            new Thread(new NetThread.GetDataThread(handler, url, refresh_traffic)).start();
        } catch (Exception e) {
            e.printStackTrace();
        }        
    }
    @Override
    public void onLoadMore() {
        Log.d(TAG, "isGetDB = " + isGetDB);
        if(isGetDB){
            trafficDatas.addAll(getTrafficDatas(Toal, pageSize));
            trafficAdapter.notifyDataSetChanged();
        }else{
            try {
                Log.d(TAG, trafficDatas.get(trafficDatas.size() - 1).toString());
                String min_id = trafficDatas.get(trafficDatas.size() - 1).getObj_id();
                String url = Constant.BaseUrl + "vehicle/" + URLEncoder.encode(Car_name, "UTF-8") + "/violation?auth_code=" + Variable.auth_code + "&min_id=" + min_id;
                new Thread(new NetThread.GetDataThread(handler, url, load_traffic)).start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }        
    }
    private void onLoad() {
        lv_activity_traffic.stopRefresh();
        lv_activity_traffic.stopLoadMore();
    }
}