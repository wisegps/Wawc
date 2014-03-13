package com.wise.article;

import java.util.List;
import com.wise.pubclas.Constant;
import com.wise.wawc.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

public class ArticleAdapter extends BaseAdapter{    
    Context context;
    List<ArticleData> articleDatas;
    LayoutInflater mInflater;
    public ArticleAdapter(Context context,List<ArticleData> articleDatas){
        this.context = context;
        this.articleDatas = articleDatas;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return articleDatas.size();
    }

    @Override
    public Object getItem(int position) {
        return articleDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_article, null);
            holder = new ViewHolder();
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            holder.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
            holder.iv_logo = (ImageView)convertView.findViewById(R.id.iv_logo);
            holder.gv_pic = (GridView)convertView.findViewById(R.id.gv_pic);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();   
        }
        ArticleData articleData = articleDatas.get(position);
        holder.tv_name.setText(articleData.getName());
        holder.tv_content.setText(articleData.getContent());
        String logo = Constant.userIconPath + articleData.getCust_id() + ".jpg";
        Bitmap bitmap = BitmapFactory.decodeFile(logo);           
        if(bitmap != null){
            holder.iv_logo.setImageBitmap(bitmap);
        }else{
            holder.iv_logo.setImageResource(R.drawable.body_nothing_icon);
        }
        holder.gv_pic.setAdapter(new PicAdapter(articleData.getPicDatas()));
        return convertView;
    }
    private class ViewHolder {
        TextView tv_name,tv_content;
        ImageView iv_logo;
        GridView gv_pic;
    }    
    public class PicAdapter extends BaseAdapter {
        LayoutInflater mInflater = LayoutInflater.from(context);
        List<PicData> picDatas;
        public PicAdapter(List<PicData> picDatas){
            this.picDatas = picDatas;
        }
        @Override
        public int getCount() {
            return picDatas.size();
        }

        @Override
        public Object getItem(int position) {
            return picDatas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.item_article_pic, null);
                holder = new ViewHolder();
                holder.iv_pic = (ImageView) convertView.findViewById(R.id.iv_pic);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            PicData picData = picDatas.get(position);
            String small_pic = picData.getSmall_pic();
            small_pic = small_pic.substring((small_pic.lastIndexOf("/") + 1), small_pic.length());
            small_pic = Constant.VehiclePath + small_pic;
            Bitmap bitmap = BitmapFactory.decodeFile(small_pic);
            if(bitmap != null){
                holder.iv_pic.setImageBitmap(bitmap);
            }else{
                holder.iv_pic.setImageResource(R.drawable.body_nothing_icon);
            }
            return convertView;
        }

        private class ViewHolder {
            ImageView iv_pic;
        }
    }
    OnArticleListener onArticleListener;
    public void setOnArticleListener(OnArticleListener onArticleListener){
        this.onArticleListener = onArticleListener;
    }
    public interface OnArticleListener{
        public abstract void doSomeThint(int postion);
    }
}
