package com.wise.extend;

import java.util.ArrayList;
import java.util.List;
import com.wise.extend.AbstractSpinerAdapter.IOnItemSelectListener;
import com.wise.wawc.R;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

public class SpinerPopWindow extends PopupWindow implements OnItemClickListener {

    private Context mContext;
    private ListView mListView;
    private IOnItemSelectListener mItemSelectListener;
    private int type;
    private List<String> titleList = new ArrayList<String>();
    private int selIndex = 0;
    TitleAdapter titleAdapter;
    
    public SpinerPopWindow(Context context) {
        super(context);

        mContext = context;
        init();
    }

    public void setItemListener(IOnItemSelectListener listener) {
        mItemSelectListener = listener;
    }

    private void init() {
        View view = LayoutInflater.from(mContext).inflate(
                R.layout.spiner_window_layout, null);
        setContentView(view);
        setWidth(LayoutParams.WRAP_CONTENT);
        setHeight(LayoutParams.WRAP_CONTENT);

        setFocusable(true);
        ColorDrawable dw = new ColorDrawable(0x00);
        setBackgroundDrawable(dw);

        mListView = (ListView) view.findViewById(R.id.listview);

        titleAdapter = new TitleAdapter();
        mListView.setAdapter(titleAdapter);
        mListView.setOnItemClickListener(this);
    }

    public void refreshData(List<String> list, int selIndex) {
        this.titleList = list;
        //this.selIndex = selIndex;
        titleAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View view, int pos, long arg3) {
        dismiss();
        if (mItemSelectListener != null) {
            mItemSelectListener.onItemClick(pos, type);
            selIndex = pos;
            titleAdapter.notifyDataSetChanged();
        }
    }

    public void setType(int type) {
        this.type = type;
    }
    public class TitleAdapter extends BaseAdapter{
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        @Override
        public int getCount() {
            return titleList.size();
        }
        @Override
        public Object getItem(int position) {
            return titleList.get(position);
        }
        @Override
        public long getItemId(int position) {
            return position;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.item_title, null);
                holder = new ViewHolder();
                holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.tv_title.setText(titleList.get(position));
            if(position == selIndex){
                holder.tv_title.setTextColor(mContext.getResources().getColor(R.color.common_blue));
            }else{
                holder.tv_title.setTextColor(mContext.getResources().getColor(R.color.common));
            }
            return convertView;
        }
        private class ViewHolder {
            TextView tv_title;
        }
    }
}