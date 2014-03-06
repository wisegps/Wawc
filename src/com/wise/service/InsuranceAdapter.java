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
 * 保险公司列表迭代器
 */
public class InsuranceAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater layoutInflater;
    private List<String[]> DataList;

    public InsuranceAdapter(Context context, List<String[]> DataList) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        this.DataList = DataList;
    }

    public int getCount() {
        return this.DataList.size();
    }

    public Object getItem(int position) {
        return this.DataList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if(convertView == null){
            convertView = layoutInflater.inflate(R.layout.insurance_adapter, null);
            holder = new ViewHolder();
            holder.insurance_name = (TextView) convertView.findViewById(R.id.insurance_item_name);
            holder.insurance_name.setText(DataList.get(position)[0]);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        return convertView;
    }
    private class ViewHolder {
        TextView insurance_name;
    }
}
