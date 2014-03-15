package com.wise.wawc;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.wise.list.XListView;
import com.wise.list.XListView.IXListViewListener;
import com.wise.pubclas.Constant;
import com.wise.pubclas.NetThread;
import com.wise.pubclas.Variable;
import com.wise.sql.DBExcute;
import com.wise.sql.DBHelper;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.widget.TextView;
/**
 * 我的订单
 * @author honesty
 */
public class OrderMeActivity extends Activity implements IXListViewListener{
    private static final String TAG = "OrderMeActivity";
    private static final int Get_order = 1;
    private static final int refresh = 2;
    XListView lv_activity_order_me;
    List<OrderData> orderDatas = new ArrayList<OrderData>();
    OrderAdapter orderAdapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_order_me);
		lv_activity_order_me = (XListView)findViewById(R.id.lv_activity_order_me);
		//不设置上拉加载无效
		lv_activity_order_me.setPullRefreshEnable(false);
		lv_activity_order_me.setPullLoadEnable(true);
		lv_activity_order_me.setXListViewListener(this);
        
		ImageView iv_activity_order_me_menu = (ImageView)findViewById(R.id.iv_activity_order_me_menu);
		iv_activity_order_me_menu.setOnClickListener(onClickListener);
        GetOrder();
        registerBroadcastReceiver();
	}
	OnClickListener onClickListener = new OnClickListener() {		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.iv_activity_order_me_menu:
				ActivityFactory.A.LeftMenu();
				break;
			}
		}
	};
	
	Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
            case Get_order:
                orderDatas.addAll(jsonOrder(msg.obj.toString()));
                orderAdapter = new OrderAdapter();
                lv_activity_order_me.setAdapter(orderAdapter);
                onLoad();
                break;

            case refresh:
                orderDatas.addAll(jsonOrder(msg.obj.toString()));
                orderAdapter.notifyDataSetChanged();
                onLoad();
                if(jsonOrder(msg.obj.toString()).size() == 0){
                    lv_activity_order_me.setPullLoadEnable(false);
                }
                break;
            }
        }	    
	};
	private void onLoad() {
	    lv_activity_order_me.stopRefresh();
	    lv_activity_order_me.stopLoadMore();
    }
	private void GetOrder(){
	    String url = Constant.BaseUrl + "customer/" + Variable.cust_id + "/order?auth_code="+Variable.auth_code;
	    new Thread(new NetThread.GetDataThread(handler, url, Get_order)).start();
	}
	private List<OrderData> jsonOrder(String result){
        List<OrderData> oDatas = new ArrayList<OrderData>();
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
                oDatas.add(orderData);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return oDatas;
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
	@Override
	protected void onResume() {
	    super.onResume();
	    Log.d(TAG, "onResume");
	}
	private void registerBroadcastReceiver(){
	    IntentFilter intentFilter = new IntentFilter();
	    intentFilter.addAction(Constant.A_Order);
	    registerReceiver(broadcastReceiver, intentFilter);
	}
	BroadcastReceiver broadcastReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(Constant.A_Order)){
                Log.d(TAG, "刷新订单");
                orderDatas.clear();
                GetOrder();
            }
        }	    
	};
    @Override
    public void onRefresh() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onLoadMore() {
        String Order_id = orderDatas.get(orderDatas.size()-1).getOrder_id();
        String url = Constant.BaseUrl + "customer/" + Variable.cust_id + "/order?auth_code="+Variable.auth_code + "&min_id=" + Order_id;
        new Thread(new NetThread.GetDataThread(handler, url, refresh)).start();
    }
}