package com.wise.wawc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.wise.data.CharacterParser;
import com.wise.service.SideBar;
import com.wise.service.SideBar.OnTouchingLetterChangedListener;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 选择城市
 * @author honesty
 */
public class SelectCityActivity extends Activity{
    private static final String TAG = "SelectCityActivity";
    
	ListView lv_activity_select_city;
	LinearLayout ll_activity_select_city;
	TextView tv_select_city_title;
	private TextView letterIndex = null;    //字母索引选中提示框
	private SideBar sideBar = null;         //右侧字母索引栏
	
	List<CityData> cityDatas = new ArrayList<CityData>();
	AllCityAdapter allCityAdapter;
    CharacterParser characterParser = new CharacterParser().getInstance();   //将汉字转成拼音
    private PinyinComparator comparator = new PinyinComparator();;      //根据拼音排序
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_city);
        ll_activity_select_city = (LinearLayout)findViewById(R.id.ll_activity_select_city);
        tv_select_city_title = (TextView)findViewById(R.id.tv_select_city_title);
		lv_activity_select_city = (ListView) findViewById(R.id.lv_activity_select_city);
		Intent intent = getIntent();
		String Citys = intent.getStringExtra("Citys");
		GetCityList(Citys);
		//排序
        Collections.sort(cityDatas, comparator);
        String Letter = "";
        for(int i = 0 ; i < cityDatas.size() ; i++){
            if(!Letter.equals(cityDatas.get(i).getFirst_letter())){
                Letter = cityDatas.get(i).getFirst_letter();
                
                CityData cityData = new CityData();
                cityData.setCity(Letter);
                cityData.setFirst_letter(Letter);
                cityDatas.add(i, cityData);
            }
        }

        LayoutInflater mLayoutInflater = LayoutInflater.from(SelectCityActivity.this);
        View searchView = mLayoutInflater.inflate(R.layout.search, null);
        lv_activity_select_city.addHeaderView(searchView);
        
        allCityAdapter = new AllCityAdapter(cityDatas);
        lv_activity_select_city.setAdapter(allCityAdapter);
        lv_activity_select_city.setOnItemClickListener(onItemClickListener);
        setupListView();
        letterIndex = (TextView) findViewById(R.id.dialog);
        sideBar = (SideBar) findViewById(R.id.sidrbar);
        sideBar.setTextView(letterIndex);   //选中某个拼音索引   提示框显示
        sideBar.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener() {            
            @Override
            public void onTouchingLetterChanged(String s) {
                for(int i = 0 ; i < cityDatas.size(); i++){
                    if(cityDatas.get(i).getFirst_letter().equals(s)){
                        lv_activity_select_city.setSelection(i);
                        break;
                    }
                }
            }
        });
	}
	OnItemClickListener onItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
            CityData cityData = cityDatas.get(arg2);
            if(cityData.getCity_code() != null){
                if(cityData.getCity().equals(cityData.getProvince())){
                    Toast.makeText(SelectCityActivity.this, "您选择了：" + cityData.getCity(), Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(SelectCityActivity.this, "您选择了：" + cityData.getProvince() + "/" + cityData.getCity(), Toast.LENGTH_SHORT).show();
                }
            }            
        }
    };
    String LastLetter = "A";
    private void setupListView(){
        lv_activity_select_city.setOnScrollListener(new OnScrollListener() {            
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {}            
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                    int visibleItemCount, int totalItemCount) {
                
                String letter = cityDatas.get(firstVisibleItem).getFirst_letter();
                if(letter.equals(LastLetter)){
                    
                }
                if(!letter.equals(LastLetter)){
                    //Log.d(TAG, "letter = " + letter + ", LastLetter = " + LastLetter);
                    //产生碰撞挤压效果
                    View childView = view.getChildAt(0);
                    if (childView != null) {
                        int titleHeight = ll_activity_select_city.getHeight();
                        int bottom = childView.getBottom();
                        MarginLayoutParams params = (MarginLayoutParams) ll_activity_select_city.getLayoutParams();
                        Log.d(TAG, "bottom = " + bottom + ",titleHeight = " + titleHeight);
                        if (bottom < titleHeight) {
                            float pushedDistance = bottom - titleHeight;
                            params.topMargin = (int) pushedDistance;
                            ll_activity_select_city.setLayoutParams(params);
                        } else {
                            if (params.topMargin != 0) {
                                params.topMargin = 0;
                                ll_activity_select_city.setLayoutParams(params);
                            }
                        }
                    }
                }
            }
        });
    }
	/**
	 * 解析城市列表
	 * @param Citys
	 */
	private void GetCityList(String Citys) {
	    
	    try {
            JSONArray jsonArray = new JSONArray(Citys);
            for(int i = 0 ; i < jsonArray.length() ; i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                CityData cityData = new CityData();
                if(jsonObject.opt("city_code") == null){
                    cityData.setCity_code("");
                }else{
                    cityData.setCity_code(jsonObject.getString("city_code"));
                }
                cityData.setCity(jsonObject.getString("city"));
                cityData.setProvince(jsonObject.getString("province"));
                cityData.setFirst_letter(GetFristLetter(jsonObject.getString("city")));
                cityDatas.add(cityData);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
	}
	
	private String GetFristLetter(String city){
	    String pinyin = characterParser.getSelling(city);
        String sortString = pinyin.substring(0, 1).toUpperCase();
        
        // 正则表达式，判断首字母是否是英文字母
        if(sortString.matches("[A-Z]")){
            return sortString.toUpperCase();
        }
        return "#";
	}
	
	private class PinyinComparator implements Comparator<CityData>{
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
	
	private void GetCity(String city){
		Toast.makeText(getApplicationContext(), city, Toast.LENGTH_SHORT).show();
		startActivity(new Intent(SelectCityActivity.this, MainActivity.class));
		finish();
	}

	private class AllCityAdapter extends BaseAdapter {
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
			ViewHolder holder;
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.item_select_city, null);
				holder = new ViewHolder();
				holder.tv_item_select_city = (TextView) convertView.findViewById(R.id.tv_item_select_city);
				holder.tv_item_select_city_title = (TextView) convertView.findViewById(R.id.tv_item_select_city_title);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
            if(citys.get(position).getCity_code() == null){
                holder.tv_item_select_city_title.setVisibility(View.VISIBLE);
                holder.tv_item_select_city_title.setText(citys.get(position).getFirst_letter());
                holder.tv_item_select_city.setVisibility(View.GONE);
            }else{
                holder.tv_item_select_city.setVisibility(View.VISIBLE);
                holder.tv_item_select_city.setText(citys.get(position).getCity());
                holder.tv_item_select_city_title.setVisibility(View.GONE);
            }
            holder.tv_item_select_city.setText(citys.get(position).getCity());
			return convertView;
		}
		private class ViewHolder {
			TextView tv_item_select_city,tv_item_select_city_title;
		}
	}

	private class CityData {
	    String City_code;
	    String city;
	    String Province;
	    String First_letter;
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
        @Override
        public String toString() {
            return "CityData [City_code=" + City_code + ", city=" + city
                    + ", Province=" + Province + ", First_letter="
                    + First_letter + "]";
        }        
	}
}