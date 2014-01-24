package com.wise.service;

import java.util.List;
import com.baidu.mapapi.navi.BaiduMapNavigation;
import com.baidu.mapapi.navi.BaiduMapAppNotSupportNaviException;
import com.baidu.mapapi.navi.NaviPara;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.wise.customView.SlidingView;
import com.wise.data.AdressData;
import com.wise.data.CarData;
import com.wise.pubclas.Variable;
import com.wise.wawc.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 我的收藏自定义迭代器
 */
public class CollectionAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater layoutInflater;
    
    List<AdressData> adrDataList = null;

    // 天安门坐标
    double currentLat = Variable.Lat;
    double currentLon = Variable.Lon;
    // 百度大厦坐标
    double goToLat = 0d;
    double goToLon = 0d;

    public CollectionAdapter(Context context, List<AdressData> adrDataList) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        this.adrDataList = adrDataList;
    }

    public int getCount() {
        return this.adrDataList.size();
    }

    public Object getItem(int position) {
        return this.adrDataList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if(convertView == null){
            convertView = layoutInflater.inflate(R.layout.collection_list, null);
            holder = new ViewHolder();
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            holder.tv_address = (TextView) convertView.findViewById(R.id.tv_address);
            holder.tv_tel = (TextView) convertView.findViewById(R.id.tv_tel);
            holder.tv_distance = (TextView) convertView.findViewById(R.id.tv_distance);
            holder.iv_location = (ImageView) convertView.findViewById(R.id.iv_location);
            holder.iv_tel = (ImageView) convertView.findViewById(R.id.iv_tel);
            holder.rl_tel = (RelativeLayout) convertView.findViewById(R.id.rl_tel);
            holder.slidingView = (SlidingView)convertView.findViewById(R.id.sv);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        holder.slidingView.ScorllRestFast();
        final AdressData adressData = adrDataList.get(position);
        holder.tv_name.setText(adressData.getName());
        holder.tv_address.setText("地址：" + adressData.getAdress());
        //holder.tv_distance.setText(adressData.getDistance());
        if(adressData.getPhone() == null || adressData.getPhone().equals("")){
            holder.rl_tel.setVisibility(View.GONE);
        }else{
            holder.rl_tel.setVisibility(View.VISIBLE);
            holder.tv_tel.setText("电话： " + adressData.getPhone());
        }
        holder.iv_location.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // TODO 切换到导航页面
                startNavi();
            }
        });

        holder.iv_tel.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL,Uri.parse("tel:"+ adressData.getPhone()));  
                context.startActivity(intent);
            }
        });
        return convertView;
    }
    private class ViewHolder {
        TextView tv_name,tv_address,tv_tel,tv_distance;
        ImageView iv_location,iv_tel;
        RelativeLayout rl_tel;
        SlidingView slidingView;
    }
    // 刷新数据
    public void refish(List<AdressData> adrDataList) {
        this.adrDataList = adrDataList;
        CollectionAdapter.this.notifyDataSetChanged();
    }

    public void startNavi() {
        int lat = (int) (currentLat * 1E6);
        int lon = (int) (currentLon * 1E6);
        GeoPoint pt1 = new GeoPoint(lat, lon);
        lat = (int) (goToLat * 1E6);
        lon = (int) (goToLon * 1E6);
        GeoPoint pt2 = new GeoPoint(lat, lon);
        // 构建 导航参数
        NaviPara para = new NaviPara();
        para.startPoint = pt1;
        para.startName = "从这里开始";
        para.endPoint = pt2;
        para.endName = "到这里结束";

        try {
            BaiduMapNavigation.openBaiduMapNavi(para, (Activity) context);

        } catch (BaiduMapAppNotSupportNaviException e) {
            e.printStackTrace();
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("您尚未安装百度地图app或app版本过低，点击确认安装？");
            builder.setNegativeButton("取消",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            builder.setPositiveButton("确定",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            BaiduMapNavigation
                                    .GetLatestBaiduMapApp((Activity) context);
                        }
                    });
            builder.create().show();
        }
    }
}
