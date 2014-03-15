package com.wise.wawc;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
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
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
/**
 * 爱车故障
 * @author honesty
 */
public class CarFaultActivity extends Activity implements IXListViewListener{
    private final String TAG = "CarFaultActivity";
    private final int get_data = 1;
    private final int refresh_data = 2;
	
	XListView lv_activity_car_fault;
	List<FaultData> faultDatas = new ArrayList<FaultData>();
	FaultAdapter faultAdapter; 
	DBExcute dbExcute = new DBExcute();
	
	String device_id = "3";
	boolean isGetDB = true; //上拉是否继续读取数据库
	int Toal = 0; //从那条记录读起
    int pageSize = 5 ; //每次读取的记录数目
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_car_fault);
		ImageView iv_activity_car_fault_back = (ImageView)findViewById(R.id.iv_activity_car_fault_back);
		iv_activity_car_fault_back.setOnClickListener(onClickListener);
		lv_activity_car_fault = (XListView)findViewById(R.id.lv_activity_car_fault);
		lv_activity_car_fault.setXListViewListener(this);
		
		faultAdapter = new FaultAdapter(CarFaultActivity.this,faultDatas);
		lv_activity_car_fault.setAdapter(faultAdapter);
		boolean isUrl = isGetDataUrl(device_id);
		if(isUrl){
		    isGetDB = false;
		    getFaultFromUrl();
		}else{
		    isGetDB = true;
		    faultDatas.addAll(getFaultDatas(Toal, pageSize));
		    faultAdapter.notifyDataSetChanged();
		}
	}
	
	OnClickListener onClickListener = new OnClickListener() {		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.iv_activity_car_fault_back:
				finish();
				break;

			default:
				break;
			}
		}
	};
	Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
            case get_data:
                faultDatas.addAll(jsonData(msg.obj.toString()));
                faultAdapter.notifyDataSetChanged();
                onLoad();
                break;

            case refresh_data:
                faultDatas.addAll(0, jsonData(msg.obj.toString()));
                faultAdapter.notifyDataSetChanged();
                onLoad();
                break;
            }
        }	    
	};
	/**
	 * 判断本地是否有记录
	 * @param device_id
	 * @return
	 */
	private boolean isGetDataUrl(String device_id){
        String sql = "select * from " + Constant.TB_Faults + " where DeviceID=?";
        int Total = dbExcute.getTotalCount(getApplicationContext(), sql, new String[]{device_id});
        if(Total == 0){
            return true;
        }else{
            return false;
        }
    }
	/**
	 * 从服务器获取数据
	 */
	private void getFaultFromUrl(){
	    String url = Constant.BaseUrl + "device/" + device_id + "/fault?auth_code=" + 
	            Variable.auth_code;
	    new Thread(new NetThread.GetDataThread(handler, url, get_data)).start();
	}
	String Data = "";
	/**
	 * 解析返回list数据
	 * @param result
	 * @return
	 */
	private List<FaultData> jsonData(String result){
	    List<FaultData> Datas = new ArrayList<FaultData>();
	    try {
            JSONArray jsonArray = new JSONArray(result);
            for(int i = 0 ; i < jsonArray.length() ; i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);                
                String create_time = jsonObject.getString("create_time");
                String fault_code = jsonObject.getString("fault_code");
                String fault_desc = jsonObject.getString("fault_desc");
                int fault_id = jsonObject.getInt("fault_id");
                
                create_time = create_time.substring(0, create_time.length() - 8).replace("T", " ");
                String[] str = GetSystem.formatDateTime(create_time);
                
                FaultData faultData = new FaultData();
                faultData.setData(str[2]);
                faultData.setTime(str[1]);
                if(Data.equals(str[0])){
                    faultData.setDataFrist(false);
                }else{
                    faultData.setDataFrist(true);
                }
                Data = str[0];
                faultData.setFault_id(fault_id);
                faultData.setFaultCode("故障码：" + fault_code);
                faultData.setFaultInfo("故障描述：" + fault_desc);
                Datas.add(faultData);
                
                ContentValues values = new ContentValues();
                values.put("DeviceID", device_id);
                values.put("fault_id", fault_id);
                values.put("fault_code", fault_code);
                values.put("fault_desc", fault_desc);
                values.put("create_time", create_time);
                dbExcute.InsertDB(CarFaultActivity.this, values, Constant.TB_Faults);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
	    return Datas;
	}
	
	private List<FaultData> getFaultDatas(int start,int pageSize) {
        System.out.println("start = " + start);
        List<FaultData> datas = getPageDatas(CarFaultActivity.this, "select * from " + Constant.TB_Faults + " where DeviceID=? order by fault_id desc limit ?,?", new String[]{device_id,String.valueOf(start),String.valueOf(pageSize)});
        Toal += datas.size();//记录位置
        if(datas.size() == pageSize){
            //继续读取数据库
        }else{
            //数据库读取完毕
            isGetDB = false;
        }
        return datas;
    }
    
    public List<FaultData> getPageDatas(Context context,String sql,String[] whereClause){
        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, whereClause);
        List<FaultData> Datas = new ArrayList<FaultData>();
        while(cursor.moveToNext()){
            FaultData faultData = new FaultData();             
            String create_time = cursor.getString(cursor.getColumnIndex("create_time"));
            String fault_code = cursor.getString(cursor.getColumnIndex("fault_code"));
            String fault_desc = cursor.getString(cursor.getColumnIndex("fault_desc"));
            int fault_id = cursor.getInt(cursor.getColumnIndex("fault_id"));
            String[] str = GetSystem.formatDateTime(create_time);
            
            faultData.setData(str[2]);
            faultData.setTime(str[1]);
            if(Data.equals(str[0])){
                faultData.setDataFrist(false);
            }else{
                faultData.setDataFrist(true);
            }
            Data = str[0];
            faultData.setFault_id(fault_id);
            faultData.setFaultCode("故障码：" + fault_code);
            faultData.setFaultInfo("故障描述：" + fault_desc);
            Datas.add(faultData);
        }
        cursor.close();
        db.close();
        return Datas;
    }
	
	private class FaultAdapter extends BaseAdapter{
		LayoutInflater mInflater;
		List<FaultData> faultDatas;
		private FaultAdapter(Context mContext,List<FaultData> faultDatas){
			mInflater = LayoutInflater.from(mContext);
			this.faultDatas = faultDatas;
		}
		@Override
		public int getCount() {
			return faultDatas.size();
		}
		@Override
		public Object getItem(int position) {
			return faultDatas.get(position);
		}
		@Override
		public long getItemId(int position) {
			return position;
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.item_car_fault, null);
				holder = new ViewHolder();
				holder.tv_item_car_fault_data = (TextView) convertView.findViewById(R.id.tv_item_car_fault_data);
				holder.tv_item_car_fault_code = (TextView)convertView.findViewById(R.id.tv_item_car_fault_code);
				holder.tv_item_car_fault_info = (TextView)convertView.findViewById(R.id.tv_item_car_fault_info);
				holder.tv_time = (TextView)convertView.findViewById(R.id.tv_time);
				holder.iv_item_car_fault_share = (ImageView)convertView.findViewById(R.id.iv_item_car_fault_share);
				holder.ll_item_car_fault = (LinearLayout)convertView.findViewById(R.id.ll_item_car_fault);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			final FaultData faultData = faultDatas.get(position);
			holder.tv_item_car_fault_data.setText(faultData.getData());
			holder.tv_item_car_fault_code.setText(faultData.getFaultCode());
			holder.tv_item_car_fault_info.setText(faultData.getFaultInfo());
			holder.tv_time.setText(faultData.getTime());
			holder.iv_item_car_fault_share.setOnClickListener(new OnClickListener() {				
				@Override
				public void onClick(View v) {
				    StringBuffer sb = new StringBuffer();
				    sb.append("【故障】");
	                sb.append(faultData.getTime());
	                sb.append("," + faultData.getFaultCode());
	                sb.append("," + faultData.getFaultInfo());
	                GetSystem.share(CarFaultActivity.this, sb.toString(), "",0,0,"故障");
				}
			});
			if(faultData.isDataFrist){
			    holder.ll_item_car_fault.setVisibility(View.VISIBLE);
				holder.tv_item_car_fault_data.setVisibility(View.VISIBLE);
			}else{
				holder.tv_item_car_fault_data.setVisibility(View.INVISIBLE);
				holder.ll_item_car_fault.setVisibility(View.GONE);
			}
			return convertView;
		}
		private class ViewHolder {
			TextView tv_item_car_fault_data,tv_item_car_fault_code,tv_item_car_fault_info,tv_time;
			ImageView iv_item_car_fault_share;
			LinearLayout ll_item_car_fault;
		}
	}
	
	private class FaultData{
	    int Fault_id;
		String Data;
		String Time;
		String FaultCode;
		String FaultInfo;
		boolean isDataFrist;
		
		public int getFault_id() {
            return Fault_id;
        }
        public void setFault_id(int fault_id) {
            Fault_id = fault_id;
        }
        public String getData() {
			return Data;
		}
		public void setData(String data) {
			Data = data;
		}
		
		public String getTime() {
            return Time;
        }
        public void setTime(String time) {
            Time = time;
        }
        public String getFaultCode() {
			return FaultCode;
		}
		public void setFaultCode(String faultCode) {
			FaultCode = faultCode;
		}
		public String getFaultInfo() {
			return FaultInfo;
		}
		public void setFaultInfo(String faultInfo) {
			FaultInfo = faultInfo;
		}
		public boolean isDataFrist() {
			return isDataFrist;
		}
		public void setDataFrist(boolean isDataFrist) {
			this.isDataFrist = isDataFrist;
		}
        @Override
        public String toString() {
            return "FaultData [Fault_id=" + Fault_id + ", Data=" + Data
                    + ", Time=" + Time + ", FaultCode=" + FaultCode
                    + ", FaultInfo=" + FaultInfo + ", isDataFrist="
                    + isDataFrist + "]";
        }        				
	}

    @Override
    public void onRefresh() {
        String url = Constant.BaseUrl + "device/" + device_id + "/fault?auth_code=" + 
                Variable.auth_code + "&max_id=" + faultDatas.get(0).getFault_id();
        System.out.println("onREfresh");
        new Thread(new NetThread.GetDataThread(handler, url, refresh_data)).start();
    }
    @Override
    public void onLoadMore() {
        if(isGetDB){
            faultDatas.addAll(getFaultDatas(Toal, pageSize));
            faultAdapter.notifyDataSetChanged();
        }else{
            int min_id = faultDatas.get(faultDatas.size() - 1).getFault_id();
            String url = Constant.BaseUrl + "device/" + device_id + "/fault?auth_code=" + 
                    Variable.auth_code + "&min_id=" + min_id;;
            new Thread(new NetThread.GetDataThread(handler, url, get_data)).start();
        }
    }
    private void onLoad() {
        lv_activity_car_fault.stopRefresh();
        lv_activity_car_fault.stopLoadMore();
    }
}