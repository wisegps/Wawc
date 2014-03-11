package com.wise.wawc;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;
import org.json.JSONException;
import org.json.JSONObject;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qzone.QZone;

import com.baidu.mapapi.BMapManager;
import com.wise.extend.FaceConversionUtil;
import com.wise.extend.OnViewTouchMoveListener;
import com.wise.extend.PicHorizontalScrollView;
import com.wise.extend.SlidingMenuView;
import com.wise.pubclas.BlurImage;
import com.wise.pubclas.Constant;
import com.wise.pubclas.GetSystem;
import com.wise.pubclas.NetThread;
import com.wise.pubclas.Variable;
import android.app.ActivityGroup;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 菜单界面
 * @author honesty
 */
public class MainActivity extends ActivityGroup implements TagAliasCallback {
    private static final String TAG = "MainActivity";

    private static final int Get_Pic = 2;//获取登录头像
    private static final int Bind_ID = 3; //绑定ID
    private static final int get_noti_count = 4; //获取消息提醒
    
    SlidingMenuView slidingMenuView;
    ViewGroup tabcontent;
    int Screen = 1;
    Platform platformQQ;
    Platform platformSina;
    Platform platformWhat;
    ImageView iv_activity_main_logo,iv_activity_main_login,iv_voice,iv_activity_main_arrow;
    TextView tv_activity_main_name;
    private ParseFaceThread thread = null;
    double Multiple = 0.5;
    PicHorizontalScrollView hsv_pic;
    ImageView iv_pic,iv_noti;
    Bitmap bimage;

    String LocationProvince;
    String LocationCity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WawcApplication app = (WawcApplication)this.getApplication();
        if (app.mBMapManager == null) {
            app.mBMapManager = new BMapManager(getApplicationContext());
            app.mBMapManager.init(WawcApplication.strKey,null);
        }
        setContentView(R.layout.activity_main);
        JPushInterface.init(getApplicationContext());
        thread = new ParseFaceThread();
        thread.start();
        hsv_pic = (PicHorizontalScrollView) findViewById(R.id.hsv_pic);
        ActivityFactory.A = this;
        slidingMenuView = (SlidingMenuView) findViewById(R.id.sliding_menu_view);
        
        TextView tv_oil = (TextView)findViewById(R.id.tv_oil);
        tv_oil.setOnClickListener(onClickListener);
        TextView tv_wb = (TextView)findViewById(R.id.tv_wb);
        tv_wb.setOnClickListener(onClickListener);
        TextView tv_xc = (TextView)findViewById(R.id.tv_xc);
        tv_xc.setOnClickListener(onClickListener);
        TextView tv_jw = (TextView)findViewById(R.id.tv_jw);
        tv_jw.setOnClickListener(onClickListener);
        TextView tv_bx = (TextView)findViewById(R.id.tv_bx);
        tv_bx.setOnClickListener(onClickListener);
        TextView tv_Parking = (TextView)findViewById(R.id.tv_Parking);
        tv_Parking.setOnClickListener(onClickListener);
        final EditText et_search = (EditText)findViewById(R.id.et_search);
        et_search.setOnKeyListener(new OnKeyListener() {            
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(KeyEvent.KEYCODE_ENTER == keyCode && event.getAction() == KeyEvent.ACTION_DOWN){
                    String keyWord = et_search.getText().toString();
                    ToSearchMap(keyWord);
                    return true;
                }
                return false;
            }
        });
        
        tabcontent = (ViewGroup) slidingMenuView
                .findViewById(R.id.sliding_body);
        ActivityFactory.v = tabcontent;
        ActivityFactory.S = slidingMenuView;
        slidingMenuView
                .setOnViewTouchMoveListener(new OnViewTouchMoveListener() {
                    @Override
                    public void OnViewMove(int x) {
                        int scrollX = (int) (x * Multiple);
                        hsv_pic.scrollTo(scrollX, 0);
                    }

                    @Override
                    public void OnViewLoad(int width, int delta) {
                        int pic_width = (int) (delta * Multiple);
                        hsv_pic.SetFristScreenWidth(pic_width);
                        hsv_pic.snapToPic(0, delta, 1, 500);
                    }

                    @Override
                    public void OnViewChange(int ScrollX, int delta,int whichScreen, int duration) {
                        hsv_pic.snapToPic(ScrollX, delta, whichScreen, duration);
                    }
                });
        // 设置图片的宽高
        iv_pic = (ImageView) findViewById(R.id.iv_pic);
        iv_noti = (ImageView) findViewById(R.id.iv_noti);
        ShowBgImage();
        //iv_pic.setImageDrawable(getResources().getDrawable(R.drawable.bg));
        //Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.bg);
        //iv_pic.setImageDrawable(BlurImage.BoxBlurFilter(bmp));
        ImageView iv_activity_home = (ImageView)findViewById(R.id.iv_activity_home);
        iv_activity_home.setOnClickListener(onClickListener);
        RelativeLayout rl_activity_main_home = (RelativeLayout) findViewById(R.id.rl_activity_main_home);
        rl_activity_main_home.setOnClickListener(onClickListener);
        iv_activity_main_login = (ImageView) findViewById(R.id.iv_activity_main_login);
        iv_activity_main_logo = (ImageView) findViewById(R.id.iv_activity_main_logo);
        iv_activity_main_arrow = (ImageView) findViewById(R.id.iv_activity_main_arrow);
        tv_activity_main_name = (TextView) findViewById(R.id.tv_activity_main_name);
        // 首页
        TextView home = (TextView) findViewById(R.id.home);
        home.setOnClickListener(onClickListener);
        // 车友圈home
        TextView car_circle = (TextView) findViewById(R.id.car_circle);
        car_circle.setOnClickListener(onClickListener);
        // 我的收藏
        TextView my_collect = (TextView) findViewById(R.id.my_collect);
        my_collect.setOnClickListener(onClickListener);
        // 设置中心
        TextView setup_center = (TextView) findViewById(R.id.setup_center);
        setup_center.setOnClickListener(onClickListener);
        TextView tv_sms = (TextView) findViewById(R.id.tv_sms);
        tv_sms.setOnClickListener(onClickListener);

        TextView my_car = (TextView) findViewById(R.id.my_car);
        my_car.setOnClickListener(onClickListener);
        TextView my_terminal = (TextView) findViewById(R.id.my_terminal);
        my_terminal.setOnClickListener(onClickListener);
        TextView my_orders = (TextView) findViewById(R.id.my_orders);
        my_orders.setOnClickListener(onClickListener);
        
        iv_voice = (ImageView)findViewById(R.id.iv_voice);
        iv_voice.setOnClickListener(onClickListener);
        
        getSpData();
        
        Intent i = new Intent(MainActivity.this, HomeActivity.class);
        View v = getLocalActivityManager().startActivity(
                HomeActivity.class.getName(), i).getDecorView();
        tabcontent.removeAllViews();
        tabcontent.addView(v);
        ShareSDK.initSDK(this);
        platformQQ = ShareSDK.getPlatform(MainActivity.this, QZone.NAME);
        platformSina = ShareSDK.getPlatform(MainActivity.this, SinaWeibo.NAME);
        isLogin();
        startService(new Intent(MainActivity.this, LocationService.class));
    }
    boolean isMove = false;
    OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
            case R.id.sliding_body:
                System.out.println("点击事件");
                slidingMenuView.snapToScreen(1);
                break;
            case R.id.rl_activity_main_home:
                ToAccountHome();
                break;
            case R.id.home:
                slidingMenuView.snapToScreen(1);
                Intent i = new Intent(MainActivity.this, HomeActivity.class);
                View view = getLocalActivityManager().startActivity(
                        HomeActivity.class.getName(), i).getDecorView();
                tabcontent.removeAllViews();
                tabcontent.addView(view);
                break;
            // 车友圈
            case R.id.car_circle:
                ToVehicleFriends();
                break;
            case R.id.my_car:
                ToMyCar();
                break;
            // 我的收藏
            case R.id.my_collect:
                ToMyCollection();
                break;
            case R.id.my_terminal:
                ToCarTerminal();
                break;
            case R.id.my_orders:
                Toorders();
                break;
            //设置中心
            case R.id.setup_center:
                ToSettingCenter();
                break;
            case R.id.tv_sms:
                ToSms();
                break; 
            case R.id.iv_activity_home:
                RightMenu();
                break;
            case R.id.iv_voice:
                if(isMove){
                    isMove = false;
                    iv_voice.clearAnimation();
                }else{
                    isMove = true;
                    Animation operatingAnim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.tip);  
                    LinearInterpolator lin = new LinearInterpolator();  
                    operatingAnim.setInterpolator(lin); 
                    if (operatingAnim != null) {  
                        iv_voice.startAnimation(operatingAnim);  
                    }
                }
                break;
            case R.id.tv_oil:
                ToSearchMap("加油站");
                break;
            case R.id.tv_xc:
                ToSearchMap("洗车");
                break;
            case R.id.tv_Parking:
                ToSearchMap("停车场");
                break;
            case R.id.tv_wb:
                ToSearchMap(getString(R.string.four_s));
                break;
            case R.id.tv_jw:
                SharedPreferences preferences = getSharedPreferences(Constant.sharedPreferencesName, Context.MODE_PRIVATE);
                int DefaultVehicleID = preferences.getInt(Constant.DefaultVehicleID, 0);
                Intent intent_help = new Intent(MainActivity.this, ShareLocationActivity.class);
                intent_help.putExtra("reason", "救援 ");
                intent_help.putExtra("index", DefaultVehicleID);
                MainActivity.this.startActivity(intent_help);
                break;
            case R.id.tv_bx:
                Intent intent_Insurance = new Intent(MainActivity.this, InsuranceActivity.class);
                intent_Insurance.putExtra("isNeedPhone", true);
                startActivity(intent_Insurance);
                break;
            }
        }
    };
    
    private void ToSearchMap(String keyWord) {
        Intent intent = new Intent(MainActivity.this,
                SearchMapActivity.class);
        intent.putExtra("keyWord", keyWord);
        startActivity(intent);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
            case Get_Pic:
                if(bimage != null){
                    GetSystem.saveImageSD(bimage, Constant.userIconPath, Constant.UserImage);
                }
                iv_activity_main_logo.setImageBitmap(BlurImage.getRoundedCornerBitmap(bimage));
                break;
            case Bind_ID:
                jsonLogin(msg.obj.toString());
                GetNotiCount();
                break;
            case get_noti_count:
                jsonNoti(msg.obj.toString());
                break;
            }
        }
    };
    /**
     * 获取本地数据
     */
    private void getSpData(){
        SharedPreferences preferences = getSharedPreferences(Constant.sharedPreferencesName, Context.MODE_PRIVATE);
        LocationProvince = preferences.getString(Constant.LocationProvince, "");
        LocationCity = preferences.getString(Constant.LocationCity, "");
    }
    /**
     * 解析绑定返回数据
     * @param result
     */
    private void jsonLogin(String result){
        try {
            JSONObject jsonObject = new JSONObject(result);
            String status_code = jsonObject.getString("status_code");
            if(status_code.equals("0")){
                String auth_code = jsonObject.getString("auth_code");                
                String cust_id = jsonObject.getString("cust_id");
                Variable.auth_code = auth_code;
                Variable.cust_id = cust_id;
                sendBroadcast(new Intent(Constant.A_Login));
                
                SharedPreferences preferences = getSharedPreferences(Constant.sharedPreferencesName, Context.MODE_PRIVATE);
                Editor editor = preferences.edit();
                editor.putString(Constant.sp_cust_id, cust_id);
                editor.putString(Constant.sp_auth_code, auth_code);
                editor.commit();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void isLogin() {
        if (platformQQ.getDb().isValid()) {
            System.out.println("qq登录");
            tv_activity_main_name.setText(platformQQ.getDb().getUserName());
            Variable.cust_name = platformQQ.getDb().getUserName();
            iv_activity_main_login.setImageResource(R.drawable.side_icon_qq_press);
            iv_activity_main_arrow.setVisibility(View.VISIBLE);
            platfromIsLogin(platformQQ);
            //绑定
            Login(platformQQ.getDb().getUserId(), platformQQ.getDb()
                    .getUserName(), LocationProvince, LocationCity, platformQQ.getDb()
                    .getUserIcon(), "remark");
        } else if (platformSina.getDb().isValid()){
            System.out.println("sina登录");
            tv_activity_main_name.setText(platformSina.getDb().getUserName());
            Variable.cust_name = platformSina.getDb().getUserName();
            iv_activity_main_login.setImageResource(R.drawable.side_icon_sina_press);
            iv_activity_main_arrow.setVisibility(View.VISIBLE);
            platfromIsLogin(platformSina);
            Login(platformSina.getDb().getUserId(), platformSina.getDb()
                    .getUserName(), LocationProvince, LocationCity, platformSina.getDb()
                    .getUserIcon(), "remark");
        } else {
            System.out.println("没有登录");
        }
    }
    /**
     * 已经登录过,初始化数据
     */
    private void platfromIsLogin(Platform platform){
        System.out.println(Constant.userIconPath + Constant.UserImage);
        bimage = BitmapFactory.decodeFile(Constant.userIconPath + Constant.UserImage);
        Constant.UserIconUrl = platform.getDb().getUserIcon();
        if(bimage != null){            
            iv_activity_main_logo.setImageBitmap(BlurImage.getRoundedCornerBitmap(bimage));
        }else{
            //TODO 获取图片
            
        }
        new Thread(new GetBitMapFromUrlThread(platform.getDb()
                .getUserIcon())).start();
        SharedPreferences preferences = getSharedPreferences(Constant.sharedPreferencesName, Context.MODE_PRIVATE);
        Variable.cust_id  = preferences.getString(Constant.sp_cust_id, "");
        Variable.auth_code = preferences.getString(Constant.sp_auth_code, "");        

        System.out.println("设置推送 = " +platform.getDb().getUserId());
        Set<String> tagSet = new LinkedHashSet<String>();
        tagSet.add(platform.getDb().getUserId());
        //调用JPush API设置Tag
        JPushInterface.setAliasAndTags(getApplicationContext(), null, tagSet, this);
    }

    /**
     * 社会化登录后绑定
     * @param login_id 第三方登录返回的标识ID
     * @param cust_name 用户名称
     * @param province  省份
     * @param city 城市
     * @param logo 头像url
     * @param remark 个人说明
     */
    private void Login(String login_id, String cust_name, String province,
            String city, String logo, String remark) {
        try {
            String url = Constant.BaseUrl + "login?login_id=" + login_id
                    + "&cust_name=" + URLEncoder.encode(cust_name, "UTF-8")
                    + "&province=" + URLEncoder.encode(province, "UTF-8")
                    + "&city=" + URLEncoder.encode(city, "UTF-8") + "&logo="
                    + URLEncoder.encode(logo, "UTF-8") + "&remark="
                    + URLEncoder.encode(remark, "UTF-8");
            new Thread(new NetThread.GetDataThread(handler, url, Bind_ID)).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class ParseFaceThread extends Thread {
        public void run() {
            super.run();
            FaceConversionUtil.getInstace().getFileText(getApplication());
        }
    };

    class GetBitMapFromUrlThread extends Thread {
        String url;

        public GetBitMapFromUrlThread(String url) {
            this.url = url;
        }
        @Override
        public void run() {
            super.run();
            bimage = GetSystem.getBitmapFromURL(url);
            Message message = new Message();
            message.what = Get_Pic;
            handler.sendMessage(message);
        }
    }    

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    long waitTime = 2000;
    long touchTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (slidingMenuView.getCurrentScreen() == 1) {
                slidingMenuView.snapToScreen(0);
            }else{
                long currentTime = System.currentTimeMillis();
                if (touchTime == 0 || (currentTime - touchTime) >= waitTime) {
                    Toast.makeText(this, "再按一次退出客户端", Toast.LENGTH_SHORT).show();
                    touchTime = currentTime;
                } else {
                    finish();
                }
            }            
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.out.println("MainActivity onDestroy");
        ShareSDK.stopSDK(this);
        WawcApplication app = (WawcApplication) this.getApplication();
        if (app.mBMapManager != null) {
            app.mBMapManager.destroy();
            app.mBMapManager = null;
        }
        stopService(new Intent(MainActivity.this, LocationService.class));
        Constant.start = 0;  // 开始页
        Constant.pageSize = 10;   //每页数量
        Constant.totalPage = 0;   //数据总量
        Constant.currentPage = 0;  //当前页
        Constant.start1 = 0;  // 开始页
        Constant.pageSize1 = 10;   //每页数量
        Constant.totalPage1 = 0;   //数据总量
        Constant.currentPage1 = 0;  //当前页
        VehicleFriendActivity.newArticleBlogId = 0;
        
        //测试  车友圈刷新
        //DBHelper dbHelper = new DBHelper(MainActivity.this);
        //SQLiteDatabase write = dbHelper.getWritableDatabase();
        //write.delete(Constant.TB_VehicleFriend, "Blog_id=?", new String[]{String.valueOf(VehicleFriendActivity.minBlogId)});
        //VehicleFriendActivity.minBlogId = 0;
    }

    /**
     * 设置中心
     */
    public void ToSettingCenter() {
        Intent intent = new Intent(MainActivity.this,
                SettingCenterActivity.class);
        View vv = getLocalActivityManager().startActivity(
                SettingCenterActivity.class.getName(), intent).getDecorView();
        tabcontent.removeAllViews();
        tabcontent.addView(vv);
        slidingMenuView.snapToScreen(1);
    }

    /**
     * 我的收藏
     */
    private void ToMyCollection() {
        Intent intent = new Intent(MainActivity.this,
                MyCollectionActivity.class);
        View vv = getLocalActivityManager().startActivity(
                MyCollectionActivity.class.getName(), intent).getDecorView();
        tabcontent.removeAllViews();
        tabcontent.addView(vv);
        slidingMenuView.snapToScreen(1);
    }

    /**
     * 首页
     */
    public void ToHome() {
        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
        View vv = getLocalActivityManager().startActivity(
                HomeActivity.class.getName(), intent).getDecorView();
        tabcontent.removeAllViews();
        tabcontent.addView(vv);
        slidingMenuView.snapToScreen(1);
    }
    public void ToSms() {
        Intent intent = new Intent(MainActivity.this, SmsActivity.class);
        View vv = getLocalActivityManager().startActivity(
                SmsActivity.class.getName(), intent).getDecorView();
        tabcontent.removeAllViews();
        tabcontent.addView(vv);
        slidingMenuView.snapToScreen(1);
    }
    /**
     * 个人信息
     */
    public void ToAccountHome() {
        if (platformQQ.getDb().isValid() || platformSina.getDb().isValid()) {
            slidingMenuView.snapToScreen(1);
            Intent i = new Intent(MainActivity.this, AccountActivity.class);
            View view = getLocalActivityManager().startActivity(
                    AccountActivity.class.getName(), i).getDecorView();
            tabcontent.removeAllViews();
            tabcontent.addView(view);
        } else {
            Toast.makeText(getApplicationContext(), "请登录", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    /**
     * 车友圈
     */
    public void ToVehicleFriends() {
    	if("".equals(Variable.cust_id)){
    		Toast.makeText(getApplicationContext(), "请登录...", 0).show();
    	}else{
    		 Intent intent = new Intent(MainActivity.this,
    	                VehicleFriendActivity.class);
    	        View vv = getLocalActivityManager().startActivity(VehicleFriendActivity.class.getName(), intent).getDecorView();
    	        tabcontent.removeAllViews();
    	        tabcontent.addView(vv);
    	        slidingMenuView.snapToScreen(1);
    	}
    }

    /**
     * 我的爱车
     */
    public void ToMyCar() {
    	if("".equals(Variable.cust_id)){
    		Toast.makeText(getApplicationContext(), "请登录", 0).show();
    		return;
    	}else{
    		 if(Variable.carDatas.size() == 0){
     	        //判断网络
     	        startActivity(new Intent(MainActivity.this,NewVehicleActivity.class));
     	    }else{
     	        slidingMenuView.snapToScreen(1);
                 Intent i = new Intent(MainActivity.this, MyVehicleActivity.class);
                 View view = getLocalActivityManager().startActivity(MyVehicleActivity.class.getName(), i).getDecorView();
                 tabcontent.removeAllViews();
                 tabcontent.addView(view);
     	    }
    	}
    }

    /**
     * 我的终端
     */
    public void ToCarTerminal() {
        slidingMenuView.snapToScreen(1);
        Intent i = new Intent(MainActivity.this, DevicesActivity.class);
        View view = getLocalActivityManager().startActivity(
                DevicesActivity.class.getName(), i).getDecorView();
        tabcontent.removeAllViews();
        tabcontent.addView(view);
    }

    /**
     * 我的订单
     */
    public void Toorders() {
        slidingMenuView.snapToScreen(1);
        Intent i = new Intent(MainActivity.this, OrderMeActivity.class);
        View view = getLocalActivityManager().startActivity(
                OrderMeActivity.class.getName(), i).getDecorView();
        tabcontent.removeAllViews();
        tabcontent.addView(view);
    }
    public void LeftMenu() {
        if (slidingMenuView.getCurrentScreen() == 0) {
            slidingMenuView.snapToScreen(1);
        } else if (slidingMenuView.getCurrentScreen() == 1) {
            slidingMenuView.snapToScreen(0);
        }
    }
    public void RightMenu() {
        if (slidingMenuView.getCurrentScreen() == 2) {
            slidingMenuView.snapToScreen(1);
        } else if (slidingMenuView.getCurrentScreen() == 1) {
            slidingMenuView.snapToScreen(2);
        }
    }    
    private void ShowBgImage(){
        iv_pic.setImageResource(R.drawable.bg);
    }    
    public void voiceSerachResult(String order){
    	Intent intent = new Intent(MainActivity.this, SearchMapActivity.class);
		intent.putExtra("keyWord", order);
		startActivity(intent);
    }
    /**
     * 获取未读消息
     */
    private void GetNotiCount(){
        String url = Constant.BaseUrl + "customer/" + Variable.cust_id +"?auth_code=" + Variable.auth_code;
        new Thread(new NetThread.GetDataThread(handler, url, get_noti_count)).start();
    }
    private void jsonNoti(String result){
        try {           
            JSONObject jsonObject = new JSONObject(result);
            if(jsonObject.opt("noti_count") != null){
                int count = jsonObject.getInt("noti_count");
                if(count > 0){
                    iv_noti.setVisibility(View.VISIBLE);
                }else{
                    iv_noti.setVisibility(View.GONE);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void gotResult(int arg0, String arg1, Set<String> arg2) {
        //System.out.println("arg0 = " + arg0 + " , arg1 = " + arg1);
    }
}