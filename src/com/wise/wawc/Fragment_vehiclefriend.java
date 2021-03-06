package com.wise.wawc;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.wise.data.Article;
import com.wise.extend.AbstractSpinerAdapter;
import com.wise.extend.SpinerPopWindow;
import com.wise.list.XListView;
import com.wise.list.XListView.IXListViewListener;
import com.wise.pubclas.Constant;
import com.wise.pubclas.GetSystem;
import com.wise.pubclas.NetThread;
import com.wise.pubclas.Variable;
import com.wise.service.MyAdapter;
import com.wise.service.MyAdapter.OnItemContentClickListener;
import com.wise.sql.DBExcute;
import com.wise.sql.DBHelper;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;
import android.widget.PopupWindow.OnDismissListener;

public class Fragment_vehiclefriend extends Fragment implements IXListViewListener, AbstractSpinerAdapter.IOnItemSelectListener{
    private ImageView menuButton = null;
    private XListView articleList = null;
    private MyAdapter myAdapter = null;
    
    private ImageView newArticle = null;
    private View saySomething;   //发表评论控件
    private TextView sendButton = null;
    private TextView commentContent = null;
    
    private MyHandler myHandler = null;
    
    private ImageView qqUserHead = null;
    private ImageView titleIcon = null;
    private TextView qqUserName = null;
    private TextView TVTitle = null;
    RelativeLayout viewTitle = null;
    private LinearLayout noArticle = null;
    
    private SpinerPopWindow mSpinerPopWindow;//文章筛选列表
    private List<String> titleList = new ArrayList<String>();
    
    
    private static final int setUserIcon = 4;
    private static final int getArticleList = 10;
    private static final int commentArticle = 18;
    private static final int articleFavorite = 20;
    public static final int newArticleResult = 67;
    private static final int loadMoreCode = 87;
    private static final int refreshCode = 89;
    private static final int loadMoreAction = 21;
    private static final int FriendType = 11;
    private static final int FriendTypeLoadMore = 33;
    private static final int refreshComments = 24;
    private static final int selectFriendType = 22;
    private int articleTypeMinBlogId = 0;
    private int articleTypeMaxBlogId = 0;
    
    private int totalNum = 0;
    private int screenWidth = 0;
    
    public static int newArticleBlogId = 0;
    private List<Article> articleDataList = new ArrayList<Article>();//  文章列表数据源
    private ProgressDialog myDialog = null;
    public static int blogId = 0;
    private  int[] blogIdArray = null;
    private boolean isLoadMore = false;
    
    //操作数据库
    private DBExcute dBExcute = null;
    
    private String commentMsg = null;
    
    public static int maxBlogId = 0;
    private String articleType = "";
    
    private int article = 1;  //文章类型
    private boolean isChickTitle = false;
    boolean bol = false;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.vehicle_friend, container, false);
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        menuButton = (ImageView) getActivity().findViewById(R.id.menu);
        menuButton.setOnClickListener(new ClickListener());
        newArticle = (ImageView) getActivity().findViewById(R.id.publish_article);
        newArticle.setOnClickListener(new ClickListener());
        saySomething = (View) getActivity().findViewById(R.id.say_something);
        sendButton = (TextView) getActivity().findViewById(R.id.btn_send);
        sendButton.setOnClickListener(new ClickListener());
        commentContent = (TextView) getActivity().findViewById(R.id.et_sendmessage);
        qqUserHead = (ImageView) getActivity().findViewById(R.id.user_head);
        qqUserHead.setOnClickListener(new ClickListener());
        qqUserName = (TextView) getActivity().findViewById(R.id.tv_qq_user_name);
        articleList = (XListView) getActivity().findViewById(R.id.article_list);
        viewTitle = (RelativeLayout) getActivity().findViewById(R.id.friend_article_title);
        noArticle = (LinearLayout) getActivity().findViewById(R.id.no_article_ll);
        articleList.setXListViewListener(this);
        screenWidth = (int) (getActivity().getWindowManager().getDefaultDisplay().getWidth()*0.5);
        
        titleIcon = (ImageView) getActivity().findViewById(R.id.title_icon);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.body_container_triangle);
        titleIcon.setImageBitmap(bitmap);
        
//      titleList.add("车友圈");
        titleList.add("同城车友");
        titleList.add("同车型车友");
        //titleList.add("附近车友");
        //titleList.add("我的收藏");
        TVTitle = (TextView) getActivity().findViewById(R.id.tv_vehicle_friend_title);
        TVTitle.setOnClickListener(new ClickListener());
        //不设置上拉加载无效
        articleList.setPullLoadEnable(true);
        
        dBExcute = new DBExcute();
        myHandler = new MyHandler();
        Message msg = new Message();
        msg.what = setUserIcon;
        myHandler.sendMessage(msg);
        myAdapter = new MyAdapter(getActivity(),saySomething,articleDataList,articleList);
        articleList.setAdapter(myAdapter);
        articleList.setOnScrollListener(new OnScrollListener() {
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                saySomething.setVisibility(View.GONE);
                myAdapter.isClick = false;
                switch (scrollState) {
                case OnScrollListener.SCROLL_STATE_FLING:
                    break;
                case OnScrollListener.SCROLL_STATE_IDLE:
                    break;
                case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                    break;
                default:
                    break;
                }
            }
            public void onScroll(AbsListView view, int firstVisibleItem,int visibleItemCount, int totalItemCount) {
            }
        });
        
        myAdapter.setOnItemContentClickListener(new OnItemContentClickListener() {            
            @Override
            public void say_somthing(int index) {
                blogId = index;
            }

			@Override
			public void Favorite(int bolg_id) {
				//更新数据库
				DBExcute dbExcute = new DBExcute();
				dbExcute.updateArticlePraises(getActivity(), Constant.TB_VehicleFriend, bolg_id, Variable.cust_name, Integer.valueOf(Variable.cust_id));
				//VehicleFriendActivity vehicleFriendActivity = new VehicleFriendActivity();
				//更新列表
				List<Article> oldArticlList = getArticleDataList();
				oldArticlList.clear();
				setArticleDataList(oldArticlList);
				List<Article> newArticlList = dbExcute.getArticlePageDatas(getActivity(), "select * from " + Constant.TB_VehicleFriend + " order by Blog_id desc limit ?,?", new String[]{String.valueOf(0),String.valueOf(Constant.start + Constant.pageSize)}, getArticleDataList());
				Variable.articleList = newArticlList;
				setArticleDataList(newArticlList);
				//myAdapter.notifyDataSetChanged();
				myAdapter.refreshDates(newArticlList);
				Toast.makeText(getActivity(), "点赞成功", 0).show();
			}
        });
        
        mSpinerPopWindow = new SpinerPopWindow(getActivity());
        mSpinerPopWindow.setItemListener(this);
        mSpinerPopWindow.setOnDismissListener(new OnDismissListener() {
            public void onDismiss() {
                if(isChickTitle){
                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.body_container_triangle);
                    titleIcon.setImageBitmap(bitmap);
                    isChickTitle = false;
                }
            }
        });
    }
    @Override
    public void onResume() {
        super.onResume();
        isLoadMore = false;
        article = Constant.articleType;
        articleSort(article,0);
    }
    class ClickListener implements OnClickListener{
        public void onClick(View v) {
            switch(v.getId()){
            case R.id.menu:
                ActivityFactory.A.LeftMenu();
                break;
            case R.id.publish_article:  //发表新文章
                Intent newArticle  = new Intent(getActivity(),NewArticleActivity.class);
                startActivityForResult(newArticle,newArticleResult);
                break;
            case R.id.user_head:    //用户资料
                Intent accountIntent = new Intent(getActivity(),AccountActivity.class);
                accountIntent.putExtra("isJump", true);
                startActivity(accountIntent);
                break;
                
            case R.id.btn_send:
                commentMsg = commentContent.getText().toString().trim();
                //发布到服务器/刷新文章内容显示/评论成功后清空编辑框/隐藏编辑框
                if("".equals(commentMsg)){
                    Toast.makeText(getActivity(), "评论类容不能为空", 0).show();
                    return;
                }else{
                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                    params.add(new BasicNameValuePair("cust_id", Variable.cust_id));
                    params.add(new BasicNameValuePair("name", Variable.cust_name));
                    params.add(new BasicNameValuePair("content", commentMsg));
                    myDialog = ProgressDialog.show(getActivity(), getString(R.string.dialog_title), getString(R.string.dialog_message));
                    myDialog.setCancelable(true);
                    new Thread(new NetThread.putDataThread(myHandler, Constant.BaseUrl + "blog/" + blogId + "/comment?auth_code=" + Variable.auth_code, params, commentArticle)).start();
                }
                break;
            case R.id.tv_vehicle_friend_title:
                if(!isChickTitle){
                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.body_container_triangle1);
                    titleIcon.setImageBitmap(bitmap);
                    isChickTitle = true;
                }
                int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,50, getResources().getDisplayMetrics());
                mSpinerPopWindow.refreshData(titleList, 0);
                mSpinerPopWindow.setWidth(screenWidth);
                mSpinerPopWindow.setHeight(px*3);               
                mSpinerPopWindow.showAsDropDown(TVTitle, (TVTitle.getWidth()-screenWidth)/2, 0);
                break;
            default:
                return;
            }
        }
    }
    @Override  //下拉刷新
    public void onRefresh() {
        List<Article> articleTempList = new ArrayList<Article>();
        articleTempList = dBExcute.getArticleTypeList(getActivity(), "select * from "+ Constant.TB_VehicleFriendType + " where Type_id=?",new String[] { String.valueOf(article) }, articleTempList);
        int[] blogIdList = new int[articleTempList.size()];
        for (int i = 0; i < articleTempList.size(); i++) {
            blogIdList[i] = articleTempList.get(i).getBlog_id();
        }
        articleTypeMaxBlogId = paiXu(blogIdList)[0];
        Log.e("Fragment_vehicleFriend","发文章之后服务器上刷新 url = " + articleType + "&max_id=" + articleTypeMaxBlogId);
        new Thread(new NetThread.GetDataThread(myHandler, articleType + "&max_id=" + articleTypeMaxBlogId, refreshCode)).start();
    }
    @Override
    public void onLoadMore() {
//      List<Article> articleTempList = new ArrayList<Article>();
//      articleTempList = dBExcute.getArticleTypeList(VehicleFriendActivity.this, "select * from " + Constant.TB_VehicleFriendType + " where Type_id=?",new String[] { String.valueOf(article) }, articleTempList);
//      int[] blogIdList = new int[articleTempList.size()];
//      for (int i = 0; i < articleTempList.size(); i++) {
//          blogIdList[i] = articleTempList.get(i).getBlog_id();
//      }
        
        
        if (!isLoadMore) {
            articleSort(article, 3);
        } else {
            if(articleDataList.size() != 0){
                int[] blogIdList = new int[articleDataList.size()];
                for (int i = 0; i < articleDataList.size(); i++) {
                    blogIdList[i] = articleDataList.get(i).getBlog_id();
                }
                articleTypeMinBlogId = paiXu(blogIdList)[blogIdList.length - 1];
                new Thread(new NetThread.GetDataThread(myHandler, articleType
                        + "&min_id=" + articleTypeMinBlogId, FriendType)).start();
            }
        }
    }
    
    private void onLoad() {
        //获取当前时间
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
        String temp = sdf.format(new Date());
        String date = temp.substring(5, 16);
        articleList.stopRefresh();
        articleList.stopLoadMore();
        articleList.setRefreshTime(date);
    }
    
    class MyHandler extends Handler{
        public void handleMessage(Message msg) {
            switch(msg.what){
            case setUserIcon:
                Bitmap bimage = BitmapFactory.decodeFile(Constant.userIconPath + Variable.cust_id + ".jpg");
                if(bimage != null){
                    qqUserHead.setImageBitmap(bimage);
                }else{
                    qqUserHead.setBackgroundResource(R.drawable.body_nothing_icon);
                    //获取图片
                    if(Constant.UserIconUrl != null){
                        new Thread(new Runnable() {
                            public void run() {
                                Bitmap userLogo = GetSystem.getBitmapFromURL(Constant.UserIconUrl);
                                if(userLogo != null){
                                    GetSystem.saveImageSD(userLogo, Constant.userIconPath, Variable.cust_id + ".jpg",100);
                                }
                            }
                        }).start();
                        Message msgs = new Message();
                        msgs.what = setUserIcon;
                        myHandler.sendMessage(msgs);
                    }else{
                        qqUserName.setText("未登录");
                    }
                }
                if(!"".equals(Variable.cust_name)){
                    qqUserName.setText(Variable.cust_name);
                }else{
                    qqUserName.setText("未登录");
                }
                break;
            case getArticleList:
                myDialog.dismiss();
                if(!"[]".equals(msg.obj.toString())){
                String temp1 = (msg.obj.toString()).replaceAll("\\\\", "");
                jsonToList(temp1);
                getArticleDatas(0);
                }
                break;
//            case newArticleResult:
//                myDialog.dismiss();
//                if(!"[]".equals(msg.obj.toString())){
//                String temp1 = (msg.obj.toString()).replaceAll("\\\\", "");
//                try {
//                    JSONArray jsonArray = new JSONArray(temp1);
//                    for(int i = 0 ; i < jsonArray.length() ; i ++){
//                        if(Integer.valueOf(jsonArray.getJSONObject(i).getString("blog_id")) == newArticleBlogId){
//                            ContentValues values = new ContentValues();
//                            values.put("Cust_id", Integer.valueOf(jsonArray.getJSONObject(i).getString("cust_id")));
//                            values.put("Blog_id", Integer.valueOf(jsonArray.getJSONObject(i).getString("blog_id")));
//                            values.put("Content", jsonArray.getJSONObject(i).toString().replaceAll("\\\\", ""));
//                            dBExcute.InsertDB(getActivity(),values,Constant.TB_VehicleFriend);
//                        }
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//                articleDataList.clear();
//                articleDataList = dBExcute.getArticleTypeList(getActivity(), "select * from " + Constant.TB_VehicleFriendType + " where Type_id = ? order by Blog_id desc limit ?,?", new String[]{String.valueOf(article),String.valueOf(0),String.valueOf(Constant.start + Constant.pageSize + 1)}, articleDataList);
//                Variable.articleList = articleDataList;
//                setArticleDataList(articleDataList);
//                myAdapter.refreshDates(articleDataList);
//                }
//                newArticleBlogId = 0;
//                break;
            case commentArticle:
                String commentResult = msg.obj.toString();
                try {
                    JSONObject jsonObject = new JSONObject(commentResult);
                    if(Integer.valueOf(jsonObject.getString("status_code")) == 0){
                        commentContent.setText("");
                        //隐藏键盘
                        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                        saySomething.setVisibility(View.GONE);
                        myAdapter.isClick = false;
                        
                        dBExcute.updateArticleComments(getActivity(), Constant.TB_VehicleFriend, blogId, commentMsg, Variable.cust_name, Integer.valueOf(Variable.cust_id));
                        articleDataList.clear();
                        
                        articleDataList = dBExcute.getArticleTypeList(getActivity(), "select * from " + Constant.TB_VehicleFriendType + " where Type_id=? order by Blog_id desc limit?,?", new String[]{String.valueOf(article),String.valueOf(0),String.valueOf(Constant.start + Constant.pageSize)}, articleDataList);
                        Variable.articleList = articleDataList;
                        setArticleDataList(articleDataList);
                        myAdapter.refreshDates(articleDataList);
                        
                        myDialog.dismiss();
                        Toast.makeText(getActivity(), "评论成功", 0).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
                //加载更多
            case loadMoreCode:
                String result = msg.obj.toString();
                if(!"[]".equals(result)){
                    jsonToList(msg.obj.toString());
                    isLoadMore = false;
                    getArticleDatas(loadMoreAction);
                }
                onLoad();   
                break;
            case FriendTypeLoadMore:
                String FriendTypeLoadMoreResult = msg.obj.toString();
                if(!"".equals(FriendTypeLoadMoreResult)){
                    jsonToList(msg.obj.toString());
                    isLoadMore = false;
                    getArticleDatas(0);
                }
                onLoad();   
                break;
            case refreshCode:  //   下拉  刷新结果处理  TODO
                if(!"[]".equals(msg.obj.toString())){
                    //  更新数据库
                    JSONArray jsonArray = null;
                    boolean isClear = false;
                    try {
                        jsonArray = new JSONArray(msg.obj.toString());
                        if(jsonArray.length() > 1){
                            for(int i = 0 ; i < jsonArray.length() ; i ++){
                                //blog_衔接上了  无需删除本地数据库
                                if(Integer.valueOf(jsonArray.getJSONObject(i).getString("blog_id")) == articleTypeMaxBlogId){
                                    isClear = true;
                                }
                            }
                            if(isClear){
                                for(int i = 0 ; i < jsonArray.length() ; i ++){
                                    if(Integer.valueOf(jsonArray.getJSONObject(i).getString("blog_id")) != articleTypeMaxBlogId){
                                        ContentValues values = new ContentValues();
                                        values.put("Cust_id", Integer.valueOf(jsonArray.getJSONObject(i).getString("cust_id")));
                                        values.put("Blog_id", Integer.valueOf(jsonArray.getJSONObject(i).getString("blog_id")));
                                        if(jsonArray.getJSONObject(i).opt("logo") != null){
                                            values.put("UserLogo", jsonArray.getJSONObject(i).getString("logo"));
                                        }else{
                                            values.put("UserLogo", "");
                                        }
                                        values.put("Content", jsonArray.getJSONObject(i).toString().replaceAll("\\\\", ""));
                                        dBExcute.InsertDB(getActivity(),values,Constant.TB_VehicleFriend);
                                        //类型表
                                        ContentValues valuesType = new ContentValues();
                                        valuesType.put("Type_id", article);
                                        valuesType.put("Blog_id", Integer.valueOf(jsonArray.getJSONObject(i).getString("blog_id")));
                                        dBExcute.InsertDB(getActivity(),valuesType,Constant.TB_VehicleFriendType);
                                    }
                                }
                            }else{
                                    //清空表
                                    DBHelper dBHelper = new DBHelper(getActivity());
                                    SQLiteDatabase reader = dBHelper.getWritableDatabase();
                                    //清空车友圈表
                                    String sql1 = "delete from "+ Constant.TB_VehicleFriend;
                                    //清空类型表
                                    String sql3 = "delete from "+ Constant.TB_VehicleFriendType + " where Type_id = " + article;
                                    reader.execSQL(sql1);
                                    reader.execSQL(sql3);
                                    reader.close();
                                    
                                    //插入新数据
                                    for(int i = 0 ; i < jsonArray.length() ; i ++){
                                        ContentValues values = new ContentValues();
                                        values.put("Cust_id", Integer.valueOf(jsonArray.getJSONObject(i).getString("cust_id")));
                                        values.put("Blog_id", Integer.valueOf(jsonArray.getJSONObject(i).getString("blog_id")));
                                        if(jsonArray.getJSONObject(i).opt("logo") != null){
                                            values.put("UserLogo", jsonArray.getJSONObject(i).getString("logo"));
                                        }else{
                                            values.put("UserLogo", "");
                                        }
                                        values.put("Content", jsonArray.getJSONObject(i).toString().replaceAll("\\\\", ""));
                                        dBExcute.InsertDB(getActivity(),values,Constant.TB_VehicleFriend);
                                        //更新类型表
                                        ContentValues  typeValue = new ContentValues();
                                        typeValue.put("Type_id", article);
                                        typeValue.put("Blog_id", Integer.valueOf(jsonArray.getJSONObject(i).getString("blog_id")));
                                        dBExcute.InsertDB(getActivity(),typeValue,Constant.TB_VehicleFriendType);
                                    }
                            }
                            //重新分页
                            articleDataList.clear();
                            setArticleDataList(articleDataList);
                            Constant.totalPage = 0;
                            Constant.start = 0;  // 开始页
                            Constant.pageSize =10;   //每页数量
                            Constant.totalPage = 0;   //数据总量
                            Constant.currentPage = 0;  //当前页
                            articleSort(1,0);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                onLoad();
                break;
            case FriendType:   
                articleList.setVisibility(View.VISIBLE);
                noArticle.setVisibility(View.GONE);
                String results = msg.obj.toString();
                try {
                    if(!"[]".equals(msg.obj.toString())){
                        DBHelper dBHelper = new DBHelper(getActivity());
                        SQLiteDatabase reader = dBHelper.getReadableDatabase();
                        SQLiteDatabase write = dBHelper.getWritableDatabase();
                        JSONArray jsonArray = new JSONArray(results);
                        int[] blogIdList = new int[jsonArray.length()];
                        for(int i = 0 ; i < jsonArray.length() ; i ++){
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            //判断车友圈文章表里面是否存在此blog_id   存在   操作类型表    不存在   将这条数据添加到车友圈文章表
                            int blog_id = Integer.valueOf(jsonObject.getString("blog_id"));
                            blogIdList[i] = blog_id;
                            Cursor cursor = reader.rawQuery("select * from " + Constant.TB_VehicleFriend + " where blog_id=?", new String[]{""+blog_id});
                            if(cursor.moveToNext()){ //存在   操作类型表 
                                ContentValues values = new ContentValues();
                                values.put("Type_id", article);
                                values.put("Blog_id", Integer.valueOf(blog_id));
                                dBExcute.InsertDB(getActivity(),values,Constant.TB_VehicleFriendType);
                            }else{     //不存在   将这条数据添加到车友圈文章表   然后添加到类型表
                                ContentValues values = new ContentValues();
                                values.put("Cust_id", Integer.valueOf(jsonArray.getJSONObject(i).getString("cust_id")));
                                values.put("Blog_id", Integer.valueOf(jsonArray.getJSONObject(i).getString("blog_id")));
                                if(jsonArray.getJSONObject(i).opt("logo") != null){
                                    values.put("UserLogo", jsonArray.getJSONObject(i).getString("logo"));
                                }else{
                                    values.put("UserLogo", "");
                                }
                                values.put("Content", jsonArray.getJSONObject(i).toString().replaceAll("\\\\", ""));
                                dBExcute.InsertDB(getActivity(),values,Constant.TB_VehicleFriend);
                                
                                ContentValues valuess = new ContentValues();
                                values.put("Type_id", article);
                                values.put("Blog_id", Integer.valueOf(blog_id));
                                write.execSQL("insert into " + Constant.TB_VehicleFriendType + "(Type_id,Blog_id) values(?,?)", new String[]{article+"",""+blog_id});
                            }
                        }
                        //查找最小blog_id
                        for(int m = 0 ; m < blogIdList.length ; m ++){
                            for(int n = 0 ; n < m ; n ++){
                                int temp = 0;
                                if(blogIdList[m] < blogIdList[n]){
                                    temp = blogIdList[m];
                                    blogIdList[m] = blogIdList[n];
                                    blogIdList[n] = temp;
                                }
                            }
                        }
                        articleTypeMinBlogId = blogIdList[0];
                        articleSort(article,0);
                    }else if(articleDataList.size() == 0){
                        articleList.setVisibility(View.GONE);
                        noArticle.setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(myDialog != null){
                    myDialog.cancel();
                }
                onLoad();
                break;
            case refreshComments:  //  TODO
                if(!"[]".equals(msg.obj.toString())){
                    try {
                        JSONArray  jsonArray = new JSONArray(msg.obj.toString());
                        for(int i = 0 ; i < jsonArray.length() ; i ++){
                            JSONObject obj = jsonArray.getJSONObject(i);
                            ContentValues contentValues  = new ContentValues();
                            contentValues.put("Blog_id", Integer.valueOf(obj.getString("blog_id")));
                            String updateTime = obj.getString("update_time");
                            String comments = "";
                            String praises = "";
                            if(obj.opt("comments") != null){
                                comments = obj.getJSONArray("comments").toString();
                            }
                            if(obj.opt("praises") != null){
                                praises = obj.getJSONArray("praises").toString();
                            }
                            dBExcute.updataComment(obj.getString("blog_id"),updateTime,comments,praises,Constant.TB_VehicleFriend,getActivity());
                        }
                        //刷新List
                        articleDataList.clear();
                        articleDataList = dBExcute.getArticleTypeList(getActivity(), "select * from " + Constant.TB_VehicleFriendType + " where Type_id=? order by Blog_id desc limit?,?", new String[]{String.valueOf(article),String.valueOf(0),String.valueOf(Constant.start + Constant.pageSize)}, articleDataList);
                        Variable.articleList = articleDataList;
                        setArticleDataList(articleDataList);
                        myAdapter.refreshDates(articleDataList);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                break;
            }
            super.handleMessage(msg);
        }
    }
    
    
    
    //解析数据
    public void jsonToList(String JSON){
        try {
        JSONArray jsonArray = new JSONArray(JSON);
        for(int i = 0 ; i < jsonArray.length() ; i ++){
            //存储到数据库
//              Cust_id text,FriendID int,Blog_id int,Content text)";
                ContentValues values = new ContentValues();
                values.put("Cust_id", Integer.valueOf(jsonArray.getJSONObject(i).getString("cust_id")));
                values.put("Blog_id", Integer.valueOf(jsonArray.getJSONObject(i).getString("blog_id")));
                if(jsonArray.getJSONObject(i).opt("logo") != null){
                    values.put("UserLogo", jsonArray.getJSONObject(i).getString("logo"));
                }else{
                    values.put("UserLogo", "");
                }
                values.put("Content", jsonArray.getJSONObject(i).toString().replaceAll("\\\\", ""));
                dBExcute.InsertDB(getActivity(),values,Constant.TB_VehicleFriend);
        }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    
    //获取数据       TODO  默认显示同车型文章
    public void getArticleDatas(int actionCode){
        totalNum = dBExcute.getTotalCount(Constant.TB_VehicleFriend, getActivity());
        if(totalNum > 0){
            //查询数据库
//          Constant.totalPage = totalNum%Constant.pageSize > 0 ? totalNum/Constant.pageSize + 1 : totalNum/Constant.pageSize;
//          if(Constant.totalPage - 1 >= Constant.currentPage){
//              Constant.start = Constant.currentPage*Constant.pageSize;
//              Constant.currentPage ++ ;
//              articleDataList = dBExcute.getArticlePageDatas(VehicleFriendActivity.this, "select * from " + Constant.TB_VehicleFriend + " order by Blog_id desc limit ?,?", new String[]{String.valueOf(Constant.start),String.valueOf(Constant.pageSize)}, articleDataList);
//              setArticleDataList(articleDataList);
//          }
//          if(Constant.totalPage == Constant.currentPage){   //数据库没有更多文章 请求服务器
//              isLoadMore = true;
//          }
            if(Constant.totalPage < Constant.pageSize){
                articleDataList.clear();
                articleDataList = dBExcute.getArticlePageDatas(getActivity(), "select * from " + Constant.TB_VehicleFriend + " order by Blog_id desc limit ?,?", new String[]{String.valueOf(Constant.start),String.valueOf(Constant.pageSize)}, articleDataList);
                Variable.articleList = articleDataList;
                myAdapter.refreshDates(articleDataList);
                isLoadMore = true;
            }else if(Constant.totalPage == Constant.pageSize){
                articleDataList.clear();
                Constant.start = Constant.currentPage*Constant.pageSize;
                Constant.currentPage ++ ;
                articleDataList = dBExcute.getArticlePageDatas(getActivity(), "select * from " + Constant.TB_VehicleFriend + " order by Blog_id desc limit ?,?", new String[]{String.valueOf(Constant.start),String.valueOf(Constant.pageSize)}, articleDataList);
                Variable.articleList = articleDataList;
                myAdapter.refreshDates(articleDataList);
            }else{
                Constant.start = Constant.currentPage*Constant.pageSize;
                Constant.currentPage ++ ;
                articleDataList = dBExcute.getArticlePageDatas(getActivity(), "select * from " + Constant.TB_VehicleFriend + " order by Blog_id desc limit ?,?", new String[]{String.valueOf(Constant.start),String.valueOf(Constant.pageSize)}, articleDataList);
                Variable.articleList = articleDataList;
                myAdapter.refreshDates(articleDataList);
            }
            if(Constant.totalPage == Constant.currentPage){
                isLoadMore = true;
            }
        }else{
            myDialog = ProgressDialog.show(getActivity(), getString(R.string.dialog_title), getString(R.string.dialog_message));
            myDialog.setCancelable(true);
            new Thread(new NetThread.GetDataThread(myHandler, Constant.BaseUrl + "customer/" + Variable.cust_id + "/blog?auth_code=" + Variable.auth_code, getArticleList)).start();
        }
        if(loadMoreAction == actionCode){
            onLoad();
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
      //文章发表成功后刷新数据库  
        if(requestCode == newArticleResult){
        	Log.e("onActivityResult687","newArticleBlogId = " + newArticleBlogId);
        	newArticleBlogId = data.getIntExtra("blog_id", 0);
            if(newArticleBlogId != 0){
//          myDialog = ProgressDialog.show(VehicleFriendActivity.this, getString(R.string.dialog_title), getString(R.string.dialog_message));
//          myDialog.setCancelable(true);
            onRefresh();
//          new Thread(new NetThread.GetDataThread(myHandler, Constant.BaseUrl + "customer/" + Variable.cust_id + "/blog?auth_code=" + Variable.auth_code, newArticleResult)).start();
            }
        }
    }
    public List<Article> getArticleDataList() {
        return articleDataList;
    }
    public void setArticleDataList(List<Article> articleDataList) {
        this.articleDataList = articleDataList;
    }
    
    public void onItemClick(int pos, int type) {
        Constant.start = 0;
        Constant.start = 0;  // 开始页
        Constant.pageSize = 10;   //每页数量
        Constant.totalPage = 0;   //数据总量
        Constant.currentPage = 0;  //当前页
        isLoadMore = false;
        if(this.articleList != null){
            this.articleDataList.clear();
        }
        if(Variable.articleList != null){
            Variable.articleList.clear();
        }
        if(pos == 0){   //同城车友
            articleSort(1,0);
            article = 1;
        }else if(pos == 1){   // 同车型的车友
            articleSort(0,0);
            article = 0;
        }
//          else if(pos == 3){    //附近车友
//          articleSort(2,0);
//          article = 2;
//          isChickTypeTile = false;
//      }else if(pos == 4){   //我的收藏
//          articleSort(3,0);
//          article = 3;
//          isChickTypeTile = false;
//      }
    }

    
    List<Article> tempList = null;
    //文章分类
    public void articleSort(int type,int action){
        if(type == 2){
            articleType = Constant.BaseUrl + "blog?auth_code=" + Variable.auth_code + "&type=" + type + "&cust_id=" + Variable.cust_id + "&lon=" + Variable.Lon + "&lat=" + Variable.Lat + "&distance=" + Variable.distance;
        }else{
            articleType = Constant.BaseUrl + "blog?auth_code=" + Variable.auth_code + "&type=" + type + "&cust_id=" + Variable.cust_id; 
        }
        //查询类型表
        totalNum = dBExcute.getTotalCount(getActivity(),"select * from " + Constant.TB_VehicleFriendType + " where Type_id=?",new String[]{String.valueOf(type)});
        if(totalNum > 0){
            Constant.totalPage = totalNum%Constant.pageSize > 0 ? totalNum/Constant.pageSize + 1 : totalNum/Constant.pageSize;
            if(totalNum < Constant.pageSize){
                articleDataList.clear();
                articleDataList = dBExcute.getArticleTypeList(getActivity(), "select * from " + Constant.TB_VehicleFriendType + " where Type_id=? order by Blog_id desc limit?,?", new String[]{String.valueOf(type),String.valueOf(Constant.start),String.valueOf(Constant.pageSize)}, articleDataList);
                
                tempList = new ArrayList<Article>();
                tempList = dBExcute.getArticleTypeList(getActivity(), "select * from " + Constant.TB_VehicleFriendType + " where Type_id=? order by Blog_id desc limit?,?", new String[]{String.valueOf(type),String.valueOf(Constant.start),String.valueOf(Constant.pageSize)}, tempList);
                System.out.println("1.tempList = " + tempList.size());
                if(tempList.size() != 0){
                    updataListDate(articleDataList,tempList);
                }
                bol = true;
                isLoadMore = true;
            }else if(totalNum == Constant.pageSize){
                articleDataList.clear();
                Constant.start = Constant.currentPage*Constant.pageSize;
                if(Constant.currentPage < Constant.totalPage){
                    Constant.currentPage ++ ;
                }
                articleDataList = dBExcute.getArticleTypeList(getActivity(), "select * from " + Constant.TB_VehicleFriendType + " where Type_id=? order by Blog_id desc limit?,?", new String[]{String.valueOf(type),String.valueOf(Constant.start),String.valueOf(Constant.pageSize)}, articleDataList);
                
                tempList = new ArrayList<Article>();   //用户处理更新评论数据的临时集合
                tempList = dBExcute.getArticleTypeList(getActivity(), "select * from " + Constant.TB_VehicleFriendType + " where Type_id=? order by Blog_id desc limit?,?", new String[]{String.valueOf(type),String.valueOf(Constant.start),String.valueOf(Constant.pageSize)}, tempList);
                System.out.println("1.tempList = " + tempList.size());
                if(tempList.size() != 0){
                    updataListDate(articleDataList,tempList);
                }
            }else{
                if(bol){
                    articleDataList.clear();
                    bol = false;
                }
                Constant.start = Constant.currentPage*Constant.pageSize;
                if(Constant.currentPage < Constant.totalPage){
                    Constant.currentPage ++ ;
                }
                articleDataList = dBExcute.getArticleTypeList(getActivity(), "select * from " + Constant.TB_VehicleFriendType + " where Type_id=? order by Blog_id desc limit?,?", new String[]{String.valueOf(type),String.valueOf(Constant.start),String.valueOf(Constant.pageSize)}, articleDataList);
                
                tempList = new ArrayList<Article>();
                tempList = dBExcute.getArticleTypeList(getActivity(), "select * from " + Constant.TB_VehicleFriendType + " where Type_id=? order by Blog_id desc limit?,?", new String[]{String.valueOf(type),String.valueOf(Constant.start),String.valueOf(Constant.pageSize)}, tempList);
                System.out.println("1.tempList = " + tempList.size());
                if(tempList.size() != 0){
                    updataListDate(articleDataList,tempList);
                }
            }
            isLoadMore = false;
            if(Constant.totalPage == Constant.currentPage){
                isLoadMore = true;
            }
            
        }else{
            myDialog = ProgressDialog.show(getActivity(), getString(R.string.dialog_title), getString(R.string.dialog_message));
            myDialog.setCancelable(true);
            new Thread(new NetThread.GetDataThread(myHandler, articleType, FriendType)).start();
        }
        if(3 == action){
            onLoad();
        }
    }
    public void  updataListDate(List<Article> articleDataList,List<Article> tempList){
        // 刷新评论相关  TODO  每页数据 最大blog_id　　 最小blog_id  最新（大） 时间
        int[] tempBlogIdList = new int[tempList.size()];
        String tempTime = "";
        for(int i = 0 ; i < tempList.size() ; i ++){
            tempBlogIdList[i] = tempList.get(i).getBlog_id();
        }
        tempTime = getMaxTime(tempList);
        String putTime = tempTime.replace(" ", "%20");
        String url = Constant.BaseUrl + "blog?auth_code=" + Variable.auth_code + "&type=" + article + "&cust_id=" + Variable.cust_id + "&min_id=" + paiXu(tempBlogIdList)[tempBlogIdList.length - 1] + "&max_id=" + paiXu(tempBlogIdList)[0] + "&update_time=" + putTime;  
        new Thread(new NetThread.GetDataThread(myHandler, url, refreshComments)).start();
        Variable.articleList = articleDataList;
        myAdapter.refreshDates(articleDataList);
    }
    
    //索引最大  blog_id最小
    public static int[] paiXu(int[] tempInt){
        for(int m = 0 ; m < tempInt.length ; m ++){
            for(int n = 0 ; n < m ; n ++){
                int temp = 0;
                if(tempInt[m] > tempInt[n]){
                    temp = tempInt[m];
                    tempInt[m] = tempInt[n];
                    tempInt[n] = temp;
                }
            }
        }
        return tempInt;
    }
    //得到最(新)大时间
    public String getMaxTime(List<Article> articleLists){
        String tempTime = "";
        String tempTime1 = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        java.util.Date begin = null;
        java.util.Date end = null;
        for(int i = 0 ; i < articleLists.size() ; i ++){
            for(int j = 0 ; j < i ; j ++){
                if("".equals(articleLists.get(i).getUpdateTime()) && !"".equals(articleLists.get(j).getUpdateTime())){
                    String str = articleLists.get(i).getUpdateTime();
                    String createTime = str.substring(0, str.indexOf(".")).replace("T"," ");
                    String str1 = articleLists.get(j).getUpdateTime();
                    String createTime1 = str1.substring(0, str.indexOf(".")).replace("T"," ");
                    try {
                        begin = sdf.parse(createTime);
                        end = sdf.parse(createTime1);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if(begin.getTime() > end.getTime()){
                        tempTime = MyAdapter.transform(articleLists.get(i).getUpdateTime());
                        tempTime1 = MyAdapter.transform(articleLists.get(j).getUpdateTime());
                        articleLists.get(j).setUpdateTime(tempTime);
                        articleLists.get(i).setUpdateTime(tempTime1);
                    }
                }
            }
        }
        String result = articleLists.get(0).getUpdateTime();
        String str = result.substring(result.lastIndexOf("."),result.length() - 1);
        String createTime = result.substring(0, result.indexOf(".")).replace("T"," ");
        String time1 =  MyAdapter.transform(createTime) + str;
        return time1;
    }
    @Override
    public void onPause() {
        super.onPause();
        
        Constant.articleType = article;
        Constant.start = 0;  // 开始页
        Constant.pageSize =10;   //每页数量
        Constant.totalPage = 0;   //数据总量
        Constant.currentPage = 0;  //当前页
    }
}
