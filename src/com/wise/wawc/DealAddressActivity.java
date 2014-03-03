package com.wise.wawc;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.wise.data.AdressData;
import com.wise.extend.AdressAdapter;
import com.wise.extend.AdressAdapter.OnCollectListener;
import com.wise.pubclas.Constant;
import com.wise.pubclas.GetSystem;
import com.wise.pubclas.NetThread;
import com.wise.pubclas.Variable;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
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
 * 处理地点
 * 
 * @author honesty
 */
public class DealAddressActivity extends Activity {
    private static final String TAG = "DealAddressActivity";

    private static final int get_deal = 1;
    ListView lv_activity_dealadress;
    List<AdressData> adressDatas = new ArrayList<AdressData>();
    AdressAdapter adressAdapter;
    int Type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dealadress);
        lv_activity_dealadress = (ListView) findViewById(R.id.lv_activity_dealadress);
        adressAdapter = new AdressAdapter(DealAddressActivity.this,
                adressDatas, DealAddressActivity.this);
        adressAdapter.setOnCollectListener(new OnCollectListener() {
            @Override
            public void OnCollect(int index) {
                System.out.println("收藏:" + index);
                adressDatas.get(index).setIs_collect(true);
                adressAdapter.notifyDataSetChanged();
            }

            @Override
            public void OnShare(int index) {
                AdressData adressData = adressDatas.get(index);
                String url = "http://api.map.baidu.com/geocoder?location="
                        + adressData.getLat() + "," + adressData.getLon()
                        + "&coord_type=bd09ll&output=html";
                StringBuffer sb = new StringBuffer();
                sb.append("【地点】");
                sb.append(adressData.getName());
                sb.append("," + adressData.getAdress());
                sb.append("," + adressData.getPhone());
                sb.append("," + url);
                GetSystem.share(DealAddressActivity.this, sb.toString(), "",
                        (float) adressData.getLat(),
                        (float) adressData.getLon());
            }
        });
        lv_activity_dealadress.setAdapter(adressAdapter);

        ImageView iv_activity_dealadress_back = (ImageView) findViewById(R.id.iv_activity_dealadress_back);
        iv_activity_dealadress_back.setOnClickListener(onClickListener);
        Type = getIntent().getIntExtra("Type", 1);
        Log.d(TAG, "Type = " + Type);
        GetDealAdress();
    }

    OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
            case R.id.iv_activity_dealadress_back:
                finish();
                break;

            default:
                break;
            }
        }
    };
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
            case get_deal:
                jsonDealAdress(msg.obj.toString());
                adressAdapter.notifyDataSetChanged();
                break;

            default:
                break;
            }
        }
    };

    /**
     * 获取处理地点
     */
    private void GetDealAdress() {
        SharedPreferences preferences = getSharedPreferences(
                Constant.sharedPreferencesName, Context.MODE_PRIVATE);
        String LocationCity = preferences
                .getString(Constant.LocationCity, "深圳");
        try {
            String url = Constant.BaseUrl + "location?auth_code="
                    + Variable.auth_code + "&city="
                    + URLEncoder.encode(LocationCity, "UTF-8") + "&type="
                    + Type + "&cust_id=" + Variable.cust_id;
            new Thread(new NetThread.GetDataThread(handler, url, get_deal))
                    .start();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 解析地点
     * 
     * @param result
     */
    private void jsonDealAdress(String result) {
        try {
            JSONArray jsonArray = new JSONArray(result);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                AdressData adressData = new AdressData();
                adressData.setAdress(jsonObject.getString("address"));
                adressData.setName(jsonObject.getString("name"));
                adressData.setPhone(jsonObject.getString("tel"));
                adressData.setLat(jsonObject.getDouble("lat"));
                adressData.setLon(jsonObject.getDouble("lon"));
                if (jsonObject.getString("is_collect").equals("1")) {
                    // 收藏
                    adressData.setIs_collect(true);
                } else {
                    // 未收藏
                    adressData.setIs_collect(false);
                }
                adressDatas.add(adressData);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class DealAdapter1 extends BaseAdapter {
        LayoutInflater mInflater = LayoutInflater
                .from(DealAddressActivity.this);

        @Override
        public int getCount() {
            return adressDatas.size();
        }

        @Override
        public Object getItem(int position) {
            return adressDatas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.item_deal, null);
                holder = new ViewHolder();
                holder.tv_name = (TextView) convertView
                        .findViewById(R.id.tv_name);
                holder.tv_address = (TextView) convertView
                        .findViewById(R.id.tv_address);
                holder.tv_tel = (TextView) convertView
                        .findViewById(R.id.tv_tel);
                holder.iv_Collect = (ImageView) convertView
                        .findViewById(R.id.iv_Collect);
                holder.iv_location = (ImageView) convertView
                        .findViewById(R.id.iv_location);
                holder.iv_tel = (ImageView) convertView
                        .findViewById(R.id.iv_tel);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            AdressData adressData = adressDatas.get(position);
            holder.tv_name.setText(adressData.getName());
            // holder.tv_address.setText(adressData.getAddress());
            // holder.tv_tel.setText(adressData.getTel());
            return convertView;
        }

        private class ViewHolder {
            TextView tv_name, tv_address, tv_tel;
            ImageView iv_Collect, iv_location, iv_tel;
        }
    }

    private class AdressData1 {
        private String name;
        private String address;
        private String tel;
        private double Lat;
        private double Lon;
        private boolean isCollect;
        private int id;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getTel() {
            return tel;
        }

        public void setTel(String tel) {
            this.tel = tel;
        }

        public double getLat() {
            return Lat;
        }

        public void setLat(double lat) {
            Lat = lat;
        }

        public double getLon() {
            return Lon;
        }

        public void setLon(double lon) {
            Lon = lon;
        }

        public boolean isCollect() {
            return isCollect;
        }

        public void setCollect(boolean isCollect) {
            this.isCollect = isCollect;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        @Override
        public String toString() {
            return "AdressData [name=" + name + ", address=" + address
                    + ", tel=" + tel + ", Lat=" + Lat + ", Lon=" + Lon
                    + ", isCollect=" + isCollect + ", id=" + id + "]";
        }
    }
}