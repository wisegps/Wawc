package com.wise.wawc;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.wise.pubclas.Constant;
import com.wise.pubclas.NetThread;
import com.wise.pubclas.Variable;
import com.wise.sql.DBExcute;
import com.wise.sql.DBHelper;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
/**
 * 我的订单
 * @author honesty
 */
public class OrderMeActivity extends Activity{
    private static final String TAG = "OrderMeActivity";
    private static final int Get_order = 1;
    ListView lv_activity_order_me;
    List<OrderData> orderDatas = new ArrayList<OrderData>();
    OrderAdapter orderAdapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_order_me);
		lv_activity_order_me = (ListView)findViewById(R.id.lv_activity_order_me);
		ImageView iv_activity_order_me_menu = (ImageView)findViewById(R.id.iv_activity_order_me_menu);
		iv_activity_order_me_menu.setOnClickListener(onClickListener);
		//ImageView iv_activity_order_me_home = (ImageView)findViewById(R.id.iv_activity_order_me_home);
		//iv_activity_order_me_home.setOnClickListener(onClickListener);
		GetOrderDB();
        GetOrder();
	}
	OnClickListener onClickListener = new OnClickListener() {		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.iv_activity_order_me_menu:
				ActivityFactory.A.LeftMenu();
				break;

			//case R.id.iv_activity_order_me_home:
				//ActivityFactory.A.ToHome();
				//OrderMeActivity.this.startActivity(new Intent(OrderMeActivity.this, SelectCityActivity.class));
				//break;
			}
		}
	};
	
	Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
            case Get_order:
                jsonOrder(msg.obj.toString());
                JudgeOrder(msg.obj.toString());
                break;

            default:
                break;
            }
        }	    
	};
	boolean isHaveOrder = false;
	private void GetOrderDB(){
	    DBHelper dbHelper = new DBHelper(OrderMeActivity.this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + Constant.TB_Base
                + " where Title=? and Cust_id=?", new String[] { "Orders",Variable.cust_id });
        if (cursor.moveToFirst()) {
            String Content = cursor.getString(cursor.getColumnIndex("Content"));
            isHaveOrder = true;
            // 解析数据
            jsonOrder(Content);
        }
        cursor.close();
        db.close();
        Log.d(TAG, "GetOrderDB");
	}
	
	private void GetOrder(){
	    String url = Constant.BaseUrl + "customer/" + Variable.cust_id + "/order?auth_code="+Variable.auth_code;
	    new Thread(new NetThread.GetDataThread(handler, url, Get_order)).start();
	}
	private void jsonOrder(String result){
	    Log.d(TAG, result);
	    try {
            JSONArray jsonArray = new JSONArray(result);
            for(int i = 0 ; i < jsonArray.length() ; i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                OrderData orderData = new OrderData();
                orderData.setCreate_time(jsonObject.getString("create_time"));
                orderData.setOrder_id(jsonObject.getString("order_id"));
                orderData.setProduct_name(jsonObject.getString("product_name"));
                orderData.setQuantity(jsonObject.getString("quantity"));
                orderData.setTotal_price(jsonObject.getString("total_price"));
                orderData.setUnit_price(jsonObject.getString("unit_price"));
                orderDatas.add(orderData);
            }
            orderAdapter = new OrderAdapter();
            lv_activity_order_me.setAdapter(orderAdapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
	}
	/**
	 * 判断更新还是插入
	 * @param result
	 */
	private void JudgeOrder(String result) {
        if (isHaveOrder) {// 更新
            UpdateOrder(result, "Orders");
        } else {// 插入
            InsertOrder(result, "Orders");
        }
    }
	private void UpdateOrder(String result, String Title) {
        DBExcute dbExcute = new DBExcute();
        ContentValues values = new ContentValues();
        values.put("Content", result);
        dbExcute.UpdateDB(OrderMeActivity.this, values, Title);
    }
	private void InsertOrder(String result, String Title) {
        DBExcute dbExcute = new DBExcute();
        ContentValues values = new ContentValues();
        values.put("Cust_id", Variable.cust_id);
        values.put("Title", Title);
        values.put("Content", result);
        dbExcute.InsertDB(OrderMeActivity.this, values, Constant.TB_Base);
    }
	
	private class OrderAdapter extends BaseAdapter{
	    LayoutInflater mInflater = LayoutInflater.from(OrderMeActivity.this);
        @Override
        public int getCount() {
            return orderDatas.size();
        }
        @Override
        public Object getItem(int position) {
            return orderDatas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.item_order, null);
                holder = new ViewHolder();
                holder.tv_item_order_product_name = (TextView) convertView.findViewById(R.id.tv_item_order_product_name);
                holder.tv_item_order_unit_price = (TextView) convertView.findViewById(R.id.tv_item_order_unit_price);
                holder.tv_item_order_create_time = (TextView) convertView.findViewById(R.id.tv_item_order_create_time);
                holder.tv_item_order_quantity = (TextView) convertView.findViewById(R.id.tv_item_order_quantity);
                holder.tv_item_order_total_price = (TextView) convertView.findViewById(R.id.tv_item_order_total_price);
                convertView.setTag(holder);
            }else{
                holder = (ViewHolder) convertView.getTag();
            }
            OrderData orderData = orderDatas.get(position);
            holder.tv_item_order_product_name.setText(orderData.getProduct_name());
            holder.tv_item_order_unit_price.setText(orderData.getUnit_price());
            holder.tv_item_order_create_time.setText(orderData.getCreate_time().substring(0, 10));
            holder.tv_item_order_quantity.setText(orderData.getQuantity());
            holder.tv_item_order_total_price.setText(orderData.getTotal_price());
            return convertView;
        }
        private class ViewHolder {
            TextView tv_item_order_product_name,tv_item_order_unit_price,tv_item_order_create_time,
                        tv_item_order_quantity,tv_item_order_total_price;
            Button bt_item_order_total_logistics;
        }
	}
	
	private class OrderData{
	    String create_time;
	    String total_price;
	    String quantity;
	    String unit_price;
	    String order_id;
	    String product_name;
        public String getCreate_time() {
            return create_time;
        }
        public void setCreate_time(String create_time) {
            this.create_time = create_time;
        }
        public String getTotal_price() {
            return total_price;
        }
        public void setTotal_price(String total_price) {
            this.total_price = total_price;
        }
        public String getQuantity() {
            return quantity;
        }
        public void setQuantity(String quantity) {
            this.quantity = quantity;
        }
        public String getUnit_price() {
            return unit_price;
        }
        public void setUnit_price(String unit_price) {
            this.unit_price = unit_price;
        }
        public String getOrder_id() {
            return order_id;
        }
        public void setOrder_id(String order_id) {
            this.order_id = order_id;
        }        
        public String getProduct_name() {
            return product_name;
        }
        public void setProduct_name(String product_name) {
            this.product_name = product_name;
        }
        @Override
        public String toString() {
            return "OrderData [create_time=" + create_time + ", total_price="
                    + total_price + ", quantity=" + quantity + ", unit_price="
                    + unit_price + ", order_id=" + order_id + "]";
        }	    
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}	
}