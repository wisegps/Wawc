package com.wise.wawc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.wise.data.CharacterParser;
import com.wise.pubclas.Constant;
import com.wise.service.ClearEditText;
import com.wise.service.SideBar;
import com.wise.service.SideBar.OnTouchingLetterChangedListener;
import com.wise.sql.DBHelper;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout.LayoutParams;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 选择城市
 * 
 * @author honesty
 */
public class SelectCityActivity extends Activity {
    private static final String TAG = "SelectCityActivity";

    ListView lv_activity_select_city;
    LinearLayout ll_activity_select_city;
    TextView tv_select_city_title, tv_activity_select_city_location;
    private TextView letterIndex = null; // 字母索引选中提示框
    private SideBar sideBar = null; // 右侧字母索引栏

    List<CityData> cityDatas = new ArrayList<CityData>();
    List<CityData> filterCityDatas = new ArrayList<CityData>();
    List<CityData> hotDatas;
    
    AllCityAdapter allCityAdapter;
    CharacterParser characterParser = new CharacterParser().getInstance(); // 将汉字转成拼音
    private PinyinComparator comparator = new PinyinComparator();; // 根据拼音排序
    String LocationCity = "";
    String Citys;
    String Hot_Citys;
    boolean isWelcome = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_city);
        ll_activity_select_city = (LinearLayout) findViewById(R.id.ll_activity_select_city);
        tv_select_city_title = (TextView) findViewById(R.id.tv_select_city_title);
        tv_activity_select_city_location = (TextView) findViewById(R.id.tv_activity_select_city_location);
        tv_activity_select_city_location.setOnClickListener(onClickListener);
        lv_activity_select_city = (ListView) findViewById(R.id.lv_activity_select_city);
        Intent intent = getIntent();
        isWelcome = intent.getBooleanExtra("Welcome", false);
        GetCity();
        
        String Citys = intent.getStringExtra("Citys");
        String Hot_Citys = intent.getStringExtra("Hot_Citys");
        cityDatas = GetCityList(Citys);
        hotDatas = GetCityList(Hot_Citys);
        
        //TODO  排序,添加热门
        ProcessCitys();
        filterCityDatas.addAll(cityDatas);
        allCityAdapter = new AllCityAdapter(filterCityDatas);
        lv_activity_select_city.setAdapter(allCityAdapter);
        lv_activity_select_city.setOnItemClickListener(lvOnItemClickListener);
        setupListView();
        letterIndex = (TextView) findViewById(R.id.dialog);
        sideBar = (SideBar) findViewById(R.id.sidrbar);
        sideBar.setTextView(letterIndex); // 选中某个拼音索引 提示框显示
        sideBar.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChanged(String s) {
                for (int i = 0; i < cityDatas.size(); i++) {
                    if (cityDatas.get(i).getFirst_letter().equals(s)) {
                        lv_activity_select_city.setSelection(i);
                        break;
                    }
                }
            }
        });
        registerBroadcastReceiver();
        startService(new Intent(SelectCityActivity.this, LocationService.class));
        ClearEditText mClearEditText = (ClearEditText) findViewById(R.id.filter_edit);
        mClearEditText.addTextChangedListener(textWatcher);
    }
    
    private void GetCity() {
        // 查询
        DBHelper dbHelper = new DBHelper(SelectCityActivity.this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        // 未来天气
        Cursor c = db.rawQuery("select * from " + Constant.TB_Base
                + " where Title=?", new String[] { "City" });
        if (c.moveToFirst()) {
            Citys = c.getString(c.getColumnIndex("Content"));
        }
        c.close();
        // 实时天气
        Cursor cursor = db.rawQuery("select * from " + Constant.TB_Base
                + " where Title=?", new String[] { "hotCity" });
        if (cursor.moveToFirst()) {
            Hot_Citys = cursor.getString(cursor.getColumnIndex("Content"));
        }
        cursor.close();
        db.close();
    }
    
    TextWatcher textWatcher = new TextWatcher() {        
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //文本框里的内容改变触发
            filterData(s.toString());
        }        
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,int after) {}        
        @Override
        public void afterTextChanged(Editable s) {}
    };
    
    private void filterData(String filterStr){
        //编辑框的内容为空的时候
        if(TextUtils.isEmpty(filterStr)){
            ll_activity_select_city.setVisibility(View.VISIBLE);
            tv_select_city_title.setText("热门城市");
            filterCityDatas.clear();
            filterCityDatas.addAll(cityDatas);
        }else{
            filterCityDatas.clear();
            ll_activity_select_city.setVisibility(View.GONE);
            for(CityData cityData : cityDatas){
                String name = cityData.getCity();
                if(name.indexOf(filterStr.toString()) != -1 || characterParser.getSelling(name).startsWith(filterStr.toString())){
                    filterCityDatas.add(cityData);
                }
            }
        }
        allCityAdapter.notifyDataSetChanged();
    }

    OnItemClickListener lvOnItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
            CityData cityData = filterCityDatas.get(arg2);
            if (cityData.getCity_code() != null) {
            	
            	
                Log.e(TAG, cityData.getCity_spell());
                SaveCityInfo(cityData);
            }
        }
    };
    OnItemClickListener gvOnItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                long arg3) {
            CityData HotCityData = hotDatas.get(arg2);
            Log.d(TAG, HotCityData.toString());
            SaveCityInfo(HotCityData);
        }
    };
    
    OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
            case R.id.tv_activity_select_city_location:
                clickLocationCity();
                break;

            default:
                break;
            }
        }
    };
    
    private void clickLocationCity(){
        if(!LocationCity.equals("")){
            for(int i = 0 ; i < cityDatas.size() ; i++){
                CityData cityData = cityDatas.get(i);
                if(cityData.Type == 1 && cityData.City_code != null){
                    if(cityData.getCity().equals(LocationCity.substring(0, (LocationCity.length() -1)))){
                        Log.d(TAG, cityData.toString());
                        SaveCityInfo(cityData);
                        break;
                    }
                }
            }
        }
    }
    /**
     * 存储城市信息
     * @param cityData
     */
    private void SaveCityInfo(CityData cityData){
        SharedPreferences preferences = getSharedPreferences(Constant.sharedPreferencesName, Context.MODE_PRIVATE);
        Editor editor = preferences.edit();
        editor.putString(Constant.LocationCity, cityData.getCity());
        editor.putString(Constant.LocationCityCode, cityData.getCity_code());
        editor.putString(Constant.LocationProvince, cityData.getProvince());
        editor.putString(Constant.LocationCityFuel, cityData.getFuel_price());
        editor.putString(Constant.FourShopParmeter, cityData.getCity_spell());
        editor.commit();
        Toast.makeText(SelectCityActivity.this, "您选择了城市：" +cityData.getCity(), Toast.LENGTH_LONG).show();
        //TODO 释放内存
        cityDatas.clear();
        filterCityDatas.clear();
        hotDatas.clear();
        System.gc();
        if(isWelcome){
            startActivity(new Intent(SelectCityActivity.this, MainActivity.class));
        }else{
            setResult(1);
        }        
        finish();
    }

    private void setupListView() {
        lv_activity_select_city.setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                    int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem != 0) {
                    String letter = cityDatas.get(firstVisibleItem)
                            .getFirst_letter();
                    String NextLetter = cityDatas.get(firstVisibleItem + 1)
                            .getFirst_letter();
                    //Log.d(TAG, "Item = " + firstVisibleItem + "letter = " + letter + ",NextLetter = " + NextLetter);
                    tv_select_city_title.setText(letter);
                    if (!letter.equals(NextLetter)) {
                        // 产生碰撞挤压效果
                        View childView = view.getChildAt(0);
                        if (childView != null) {
                            int titleHeight = ll_activity_select_city
                                    .getHeight();
                            int bottom = childView.getBottom();
                            MarginLayoutParams params = (MarginLayoutParams) ll_activity_select_city
                                    .getLayoutParams();
                            //Log.d(TAG, "bottom = " + bottom + ",titleHeight = " + titleHeight);
                            if (bottom < titleHeight) {
                                float pushedDistance = bottom - titleHeight;
                                params.topMargin = (int) pushedDistance;
                                ll_activity_select_city.setLayoutParams(params);
                            } else {
                                if (params.topMargin != 0) {
                                    params.topMargin = 0;
                                    ll_activity_select_city
                                            .setLayoutParams(params);
                                }
                            }
                        }
                    } else {
                        //Log.d(TAG, "相等");
                        MarginLayoutParams params = (MarginLayoutParams) ll_activity_select_city
                                .getLayoutParams();
                        params.topMargin = 0;
                        ll_activity_select_city.setLayoutParams(params);
                    }
                } else {

                }
            }
        });
    }

    /**
     * 解析城市列表
     * 
     * @param Citys
     */
    private List<CityData> GetCityList(String Citys) {
        List<CityData> Datas = new ArrayList<CityData>();
        try {
            JSONArray jsonArray = new JSONArray(Citys);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                CityData cityData = new CityData();
                if (jsonObject.opt("city_code") == null) {
                    cityData.setCity_code("");
                } else {
                    cityData.setCity_code(jsonObject.getString("city_code"));
                }
                cityData.setType(1);
                cityData.setCity(jsonObject.getString("city"));
                cityData.setProvince(jsonObject.getString("province"));
                cityData.setCity_spell(jsonObject.getString("spell"));
                cityData.setFirst_letter(GetFristLetter(jsonObject.getString("city")));
                if(jsonObject.opt("fuel_price") == null){
                    cityData.setFuel_price("");
                }else{
                    cityData.setFuel_price(jsonObject.getString("fuel_price"));
                }
                Datas.add(cityData);
            }
            return Datas;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return Datas;
    }

    /**
     * 给城市排序，添加热门城市
     */
    private void ProcessCitys() {
        Collections.sort(cityDatas, comparator);
        String Letter = "";
        for (int i = 0; i < cityDatas.size(); i++) {
            if (!Letter.equals(cityDatas.get(i).getFirst_letter())) {
                // 增加标题
                Letter = cityDatas.get(i).getFirst_letter();
                CityData cityData = new CityData();
                cityData.setType(1);
                cityData.setCity(Letter);
                cityData.setFirst_letter(Letter);
                cityDatas.add(i, cityData);
            }
        }

        CityData cityData = new CityData();
        cityData.setType(0);
        cityData.setCity_code("10");
        cityData.setCity("1231231231231");
        cityData.setProvince("12312312312");
        cityData.setFirst_letter("热门城市");
        cityDatas.add(0, cityData);

        CityData cityData1 = new CityData();
        cityData1.setType(1);
        cityData1.setCity("热门城市");
        cityData1.setProvince("12312312312");
        cityData1.setFirst_letter("热门城市");
        cityDatas.add(0, cityData1);

        for (int i = 0; i < cityDatas.size(); i++) {
            Log.d(TAG, cityDatas.get(i).toString());
        }
    }

    private String GetFristLetter(String city) {
        String pinyin = characterParser.getSelling(city);
        String sortString = pinyin.substring(0, 1).toUpperCase();
        // 正则表达式，判断首字母是否是英文字母
        if (sortString.matches("[A-Z]")) {
            return sortString.toUpperCase();
        }
        return "#";
    }

    private class PinyinComparator implements Comparator<CityData> {
        @Override
        public int compare(CityData o1, CityData o2) {
            if (o1.getFirst_letter().equals("@")
                    || o2.getFirst_letter().equals("#")) {
                return -1;
            } else if (o1.getFirst_letter().equals("#")
                    || o2.getFirst_letter().equals("@")) {
                return 1;
            } else {
                return o1.getFirst_letter().compareTo(o2.getFirst_letter());
            }
        }
    }

    private class AllCityAdapter extends BaseAdapter {
        private static final int VALUE_HOT = 0;
        private static final int VALUE_CITY = 1;
        List<CityData> citys;
        LayoutInflater mInflater;

        public AllCityAdapter(List<CityData> citys) {
            this.citys = citys;
            mInflater = LayoutInflater.from(SelectCityActivity.this);
        }

        @Override
        public int getCount() {
            return citys.size();
        }

        @Override
        public Object getItem(int position) {
            return citys.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            CityData cityData = citys.get(position);
            int type = getItemViewType(position);
            ViewHot hotholder = null;
            ViewCity cityHolder = null;
            if (convertView == null) {
                switch (type) {
                case VALUE_HOT:
                    hotholder = new ViewHot();
                    convertView = mInflater.inflate(R.layout.hot_city, null);
                    hotholder.gv = (GridView) convertView
                            .findViewById(R.id.gv_hot_city);
                    int px = (int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP, 30, getResources()
                                    .getDisplayMetrics());
                    LayoutParams params = new LayoutParams(
                            LayoutParams.FILL_PARENT, (hotDatas.size() / 4 + 1)
                                    * px);
                    hotholder.gv.setLayoutParams(params);
                    hotholder.gv.setAdapter(new hotAdapter());
                    hotholder.gv.setOnItemClickListener(gvOnItemClickListener);
                    convertView.setTag(hotholder);
                    break;
                case VALUE_CITY:
                    cityHolder = new ViewCity();
                    convertView = mInflater.inflate(R.layout.item_select_city,
                            null);
                    cityHolder.tv_item_select_city = (TextView) convertView
                            .findViewById(R.id.tv_item_select_city);
                    cityHolder.tv_item_select_city_title = (TextView) convertView
                            .findViewById(R.id.tv_item_select_city_title);
                    if (citys.get(position).getCity_code() == null) {
                        cityHolder.tv_item_select_city_title
                                .setVisibility(View.VISIBLE);
                        cityHolder.tv_item_select_city_title.setText(citys.get(
                                position).getFirst_letter());
                        cityHolder.tv_item_select_city.setVisibility(View.GONE);
                    } else {
                        cityHolder.tv_item_select_city
                                .setVisibility(View.VISIBLE);
                        cityHolder.tv_item_select_city.setText(citys.get(
                                position).getCity());
                        cityHolder.tv_item_select_city_title
                                .setVisibility(View.GONE);
                    }
                    convertView.setTag(cityHolder);
                    break;
                default:
                    break;
                }
            } else {
                switch (type) {
                case VALUE_HOT:
                    hotholder = (ViewHot) convertView.getTag();
                    int px = (int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP, 30, getResources()
                                    .getDisplayMetrics());
                    LayoutParams params = new LayoutParams(
                            LayoutParams.FILL_PARENT, (hotDatas.size() / 5 + 1)
                                    * px);
                    hotholder.gv.setLayoutParams(params);
                    hotholder.gv.setAdapter(new hotAdapter());
                    hotholder.gv.setOnItemClickListener(gvOnItemClickListener);
                    break;

                case VALUE_CITY:
                    cityHolder = (ViewCity) convertView.getTag();
                    if (citys.get(position).getCity_code() == null) {
                        cityHolder.tv_item_select_city_title
                                .setVisibility(View.VISIBLE);
                        cityHolder.tv_item_select_city_title.setText(citys.get(
                                position).getFirst_letter());
                        cityHolder.tv_item_select_city.setVisibility(View.GONE);
                    } else {
                        cityHolder.tv_item_select_city
                                .setVisibility(View.VISIBLE);
                        cityHolder.tv_item_select_city.setText(citys.get(
                                position).getCity());
                        cityHolder.tv_item_select_city_title
                                .setVisibility(View.GONE);
                    }
                    break;
                default:
                    break;
                }
            }
            return convertView;
        }

        @Override
        public int getItemViewType(int position) {
            CityData cityData = citys.get(position);
            int type = cityData.getType();
            return type;
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        private class ViewCity {// 城市列表
            TextView tv_item_select_city, tv_item_select_city_title;
        }

        private class ViewHot {// 热门城市
            GridView gv;
        }
    }

    private class CityData {
        int Type;
        String City_code;
        String city;
        String Province;
        String First_letter;
        String Fuel_price;
        String city_spell;

        public int getType() {
            return Type;
        }
        public void setType(int type) {
            Type = type;
        }
        public String getCity_code() {
            return City_code;
        }
        public void setCity_code(String city_code) {
            City_code = city_code;
        }
        public String getCity() {
            return city;
        }
        public void setCity(String city) {
            this.city = city;
        }
        public String getProvince() {
            return Province;
        }
        public void setProvince(String province) {
            Province = province;
        }
        public String getFirst_letter() {
            return First_letter;
        }
        public void setFirst_letter(String first_letter) {
            First_letter = first_letter;
        }
        public String getFuel_price() {
            return Fuel_price;
        }
        public void setFuel_price(String fuel_price) {
            Fuel_price = fuel_price;
        }
        public String getCity_spell() {
			return city_spell;
		}
		public void setCity_spell(String city_spell) {
			this.city_spell = city_spell;
		}
		@Override
        public String toString() {
            return "CityData [Type=" + Type + ", City_code=" + City_code
                    + ", city=" + city + ", Province=" + Province
                    + ", First_letter=" + First_letter + "]";
        }
    }

    public class hotAdapter extends BaseAdapter {
        LayoutInflater mInflater = LayoutInflater.from(SelectCityActivity.this);

        @Override
        public int getCount() {
            return hotDatas.size();
        }

        @Override
        public Object getItem(int position) {
            return hotDatas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.item_hot, null);
                holder = new ViewHolder();
                holder.tv_item_hot = (TextView) convertView
                        .findViewById(R.id.tv_item_hot);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.tv_item_hot.setText(hotDatas.get(position).getCity());
            return convertView;
        }

        private class ViewHolder {
            TextView tv_item_hot;
        }
    }

    private void registerBroadcastReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constant.A_City);
        registerReceiver(broadcastReceiver, intentFilter);
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Constant.A_City)) {
                LocationCity = intent.getStringExtra("City");
                tv_activity_select_city_location.setText(LocationCity);
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }
}
