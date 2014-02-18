package com.wise.wawc;

import java.net.URLEncoder;
import java.util.HashMap;
import org.json.JSONException;
import org.json.JSONObject;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qzone.QZone;
import com.wise.extend.FaceConversionUtil;
import com.wise.extend.OnViewTouchMoveListener;
import com.wise.extend.PicHorizontalScrollView;
import com.wise.extend.SlidingMenuView;
import com.wise.pubclas.BlurImage;
import com.wise.pubclas.Constant;
import com.wise.pubclas.GetSystem;
import com.wise.pubclas.NetThread;
import com.wise.pubclas.Variable;
import com.wise.service.SaveSettingData;
import com.wise.sql.DBExcute;
import com.wise.sql.DBHelper;

import android.app.ActivityGroup;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.sqlite.SQLiteDatabase;
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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 菜单界面
 * 
 * @author honesty
 */
public class MainActivity extends ActivityGroup implements PlatformActionListener {
    private static final String TAG = "MainActivity";

    private static final int Login = 1; //登录
    private static final int Get_Pic = 2;//获取登录头像
    private static final int Bind_ID = 3; //绑定ID
    
    SlidingMenuView slidingMenuView;
    ViewGroup tabcontent;
    int Screen = 1;
    Platform platformQQ;
    Platform platformSina;
    Platform platformWhat;
    ImageView iv_activity_main_logo,iv_activity_main_login_sina,
            iv_activity_main_login_qq,iv_voice,iv_activity_main_arrow;
    TextView tv_activity_main_name;
    RelativeLayout refuel,maintain,wishCar,help,insurance,park;
    private ParseFaceThread thread = null;
    private SaveSettingData saveSettingData;
    double Multiple = 0.5;
    PicHorizontalScrollView hsv_pic;
    ImageView iv_pic;
    Bitmap bimage;

    String LocationProvince;
    String LocationCity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        thread = new ParseFaceThread();
        thread.start();

        hsv_pic = (PicHorizontalScrollView) findViewById(R.id.hsv_pic);
        ActivityFactory.A = this;
        slidingMenuView = (SlidingMenuView) findViewById(R.id.sliding_menu_view);
        
        refuel = (RelativeLayout) findViewById(R.id.rl_activity_main_oil);  //加油
        maintain = (RelativeLayout) findViewById(R.id.rl_activity_main_maintain);  //维保
        wishCar = (RelativeLayout) findViewById(R.id.rl_activity_main_wash);   //洗车
        help = (RelativeLayout) findViewById(R.id.rl_activity_main_help);   //救援
        insurance = (RelativeLayout) findViewById(R.id.rl_activity_main_safety);   //保险
        park = (RelativeLayout) findViewById(R.id.rl_activity_main_park);    //停车
        refuel.setOnClickListener(onClickListener);
        maintain.setOnClickListener(onClickListener);
        wishCar.setOnClickListener(onClickListener);
        help.setOnClickListener(onClickListener);
        insurance.setOnClickListener(onClickListener);
        park.setOnClickListener(onClickListener);
        
        
        
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
        ShowBgImage();
        //iv_pic.setImageDrawable(getResources().getDrawable(R.drawable.bg));
        //Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.bg);
        //iv_pic.setImageDrawable(BlurImage.BoxBlurFilter(bmp));
        ImageView iv_activity_home = (ImageView)findViewById(R.id.iv_activity_home);
        iv_activity_home.setOnClickListener(onClickListener);
        RelativeLayout rl_activity_main_home = (RelativeLayout) findViewById(R.id.rl_activity_main_home);
        rl_activity_main_home.setOnClickListener(onClickListener);
        iv_activity_main_login_sina = (ImageView) findViewById(R.id.iv_activity_main_login_sina);
        iv_activity_main_login_sina.setOnClickListener(onClickListener);
        iv_activity_main_login_qq = (ImageView) findViewById(R.id.iv_activity_main_login_qq);
        iv_activity_main_login_qq.setOnClickListener(onClickListener);
        iv_activity_main_logo = (ImageView) findViewById(R.id.iv_activity_main_logo);
        iv_activity_main_arrow = (ImageView) findViewById(R.id.iv_activity_main_arrow);
        tv_activity_main_name = (TextView) findViewById(R.id.tv_activity_main_name);

        // 车友圈
        TextView car_circle = (TextView) findViewById(R.id.car_circle);
        car_circle.setOnClickListener(onClickListener);
        // 我的收藏
        TextView my_collect = (TextView) findViewById(R.id.my_collect);
        my_collect.setOnClickListener(onClickListener);
        // 设置中心
        TextView setup_center = (TextView) findViewById(R.id.setup_center);
        setup_center.setOnClickListener(onClickListener);

        TextView car_remind = (TextView) findViewById(R.id.car_remind);
        car_remind.setOnClickListener(onClickListener);
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
        initSettingData();
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
                ToFriendHome();
                break;
            // 车友圈
            case R.id.car_circle:
                ToVehicleFriends();
                break;
            case R.id.car_remind:
                ToCarRemind();
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
            // 设置中心
            case R.id.setup_center:
                ToSettingCenter();
                break;
            case R.id.iv_activity_main_login_qq:
                platformQQ.setPlatformActionListener(MainActivity.this);
                platformQQ.showUser(null);
                platformWhat = platformQQ;
                break;
            case R.id.iv_activity_main_login_sina:
                platformSina.setPlatformActionListener(MainActivity.this);
                platformSina.SSOSetting(true);
                platformSina.showUser(null);
                platformWhat = platformSina;
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
            case R.id.rl_activity_main_oil:   //加油
            	voiceSerachResult("加油站");
            	Log.e("加油站","加油站");
            	break;
            case R.id.rl_activity_main_maintain:   //维保
            	voiceSerachResult("车辆维修");
            	break;
            case R.id.rl_activity_main_wash:    //洗车
            	voiceSerachResult("洗车店");
            	break;
            case R.id.rl_activity_main_help:   //救援
            	voiceSerachResult("车辆救援");
            	break;
            case R.id.rl_activity_main_safety:   //保险
            	voiceSerachResult("车辆保险");
            	break;
            case R.id.rl_activity_main_park:    //停车
            	voiceSerachResult("停车场");
            	break;
            }
        }
    };

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
            case Get_Pic:
                iv_activity_main_logo.setImageBitmap(BlurImage.getRoundedCornerBitmap(bimage));
                break;
            case Login:
                jsonLoginOk();
                break;
            case Bind_ID:
                Log.d(TAG, "绑定返回=" + msg.obj.toString());
                jsonLogin(msg.obj.toString());
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
        Variable.cust_id = preferences.getString(Constant.sp_cust_id, "");
    }
    /**
     * 登录成功
     */
    private void jsonLoginOk(){//登录成功        
        Variable.cust_name = platformWhat.getDb().getUserName();
        tv_activity_main_name.setText(platformWhat.getDb().getUserName());
        iv_activity_main_login_sina.setVisibility(View.GONE);
        iv_activity_main_login_qq.setVisibility(View.GONE);
        //绑定
        Login(platformWhat.getDb().getUserId(), platformWhat.getDb()
                .getUserName(), LocationProvince, LocationCity, platformWhat.getDb()
                .getUserIcon(), "remark");
        //获取图片
        new Thread(new GetBitMapFromUrlThread(platformWhat.getDb()
                .getUserIcon())).start();
    }
    /**
     * 解析绑定返回数据
     * @param result
     */
    private void jsonLogin(String result){
    	Log.e("用户登录返回数据",result);
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
            iv_activity_main_login_sina.setVisibility(View.GONE);
            iv_activity_main_login_qq.setVisibility(View.VISIBLE);
            iv_activity_main_login_qq.setImageResource(R.drawable.side_icon_qq_press);
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
            iv_activity_main_login_sina.setVisibility(View.VISIBLE);
            iv_activity_main_login_sina.setImageResource(R.drawable.side_icon_sina_press);
            iv_activity_main_arrow.setVisibility(View.VISIBLE);
            iv_activity_main_login_qq.setVisibility(View.GONE);
            platfromIsLogin(platformSina);
            Login(platformSina.getDb().getUserId(), platformSina.getDb()
                    .getUserName(), LocationProvince, LocationCity, platformSina.getDb()
                    .getUserIcon(), "remark");
        } else {
            System.out.println("没有登录");
            iv_activity_main_login_sina.setVisibility(View.VISIBLE);
            iv_activity_main_login_qq.setVisibility(View.VISIBLE);
        }
    }
    /**
     * 已经登录过,初始化数据
     */
    private void platfromIsLogin(Platform platform){
        System.out.println(Constant.userIconPath + Constant.UserImage);
        bimage = BitmapFactory.decodeFile(Constant.userIconPath + Constant.UserImage);
        Constant.UserIcon = bimage;
        Constant.UserIconUrl = platform.getDb().getUserIcon();
        if(bimage != null){            
            iv_activity_main_logo.setImageBitmap(BlurImage.getRoundedCornerBitmap(bimage));
        }else{
            //TODO 获取图片
            new Thread(new GetBitMapFromUrlThread(platform.getDb()
                    .getUserIcon())).start();
        }
        SharedPreferences preferences = getSharedPreferences(
                Constant.sharedPreferencesName, Context.MODE_PRIVATE);
        Variable.cust_id  = preferences.getString(Constant.sp_cust_id, "");
        Variable.auth_code = preferences.getString(Constant.sp_auth_code, "");
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
            Constant.UserIcon = bimage;
            if(bimage != null){
                GetSystem.saveImageSD(bimage, Constant.userIconPath, Constant.UserImage);
            }
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
            long currentTime = System.currentTimeMillis();
            if (touchTime == 0 || (currentTime - touchTime) >= waitTime) {
                Toast.makeText(this, "再按一次退出客户端", Toast.LENGTH_SHORT).show();
                touchTime = currentTime;
            } else {
//            	  int nPid = android.os.Process.myPid();
//            	  android.os.Process.killProcess(nPid); 
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
        DBHelper dbHelper = new DBHelper(MainActivity.this);
        SQLiteDatabase write = dbHelper.getWritableDatabase();
        write.delete(Constant.TB_VehicleFriend, "Blog_id=?", new String[]{String.valueOf(VehicleFriendActivity.minBlogId)});
        VehicleFriendActivity.minBlogId = 0;
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

    @Override
    public void onComplete(Platform arg0, int arg1, HashMap<String, Object> arg2) {
        Log.d(TAG, "登录成功" + arg0.getName());
//        Iterator iterator = arg2.entrySet().iterator();
//        while (iterator.hasNext()) {
//            Entry entry = (Entry) iterator.next();
//            System.out.println(entry.getKey() + "," + entry.getValue());
//            if (entry.getKey().equals("nickname")) {
//                Config.qqUserName = (String) entry.getValue();
//                Log.e("QQ昵称", "" + entry.getValue());
//            }
//        }
        Message message = new Message();
        message.what = Login;
        handler.sendMessage(message);
    }
    /**
     * 好友主页
     */
    public void ToFriendHome() {
        if (platformQQ.getDb().isValid() || platformSina.getDb().isValid()) {
            startActivity(new Intent(MainActivity.this, FriendHomeActivity.class));
//            Intent i = new Intent(MainActivity.this, FriendHomeActivity.class);
//            View v = getLocalActivityManager().startActivity(
//                    FriendHomeActivity.class.getName(), i).getDecorView();
//            tabcontent.removeAllViews();
//            tabcontent.addView(v);
//            slidingMenuView.snapToScreen(1);
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
     * 车务提醒
     */
    public void ToCarRemind() {
        slidingMenuView.snapToScreen(1);
        Intent i = new Intent(MainActivity.this, CarRemindActivity.class);
        View view = getLocalActivityManager().startActivity(
                CarRemindActivity.class.getName(), i).getDecorView();
        tabcontent.removeAllViews();
        tabcontent.addView(view);
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
        Intent i = new Intent(MainActivity.this, MyDevicesActivity.class);
        View view = getLocalActivityManager().startActivity(
                MyDevicesActivity.class.getName(), i).getDecorView();
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

    public void HideMenu() {
        slidingMenuView.snapToScreen(1);
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
    @Override
    public void onCancel(Platform arg0, int arg1) {
        Log.d(TAG, "登录取消");
    }

    @Override
    public void onError(Platform arg0, int arg1, Throwable arg2) {
        Log.d(TAG, "登录出错");
        arg0.removeAccount();
    }

    private void initSettingData() {
        saveSettingData = new SaveSettingData(MainActivity.this);
        Variable.againstPush = saveSettingData.getAgainstPush();
        Variable.faultPush = saveSettingData.getBugPush();
        Variable.remaindPush = saveSettingData.getTrafficDepartment();
        Variable.defaultCenter = saveSettingData.getDefaultCenter();
    }
    
    private void ShowBgImage(){
        //读取本地图片
        String Path = Constant.picPath + Constant.BgImage;
        Bitmap bgBitmap = BlurImage.decodeSampledBitmapFromPath(Path, 500, 500);
        if(bgBitmap == null){
            Log.e(TAG, "没有背景图片");
            //高斯处理
            bgBitmap = BlurImage.decodeSampledBitmapFromResource(getResources(), R.drawable.bg, 500, 500);
            bgBitmap = BlurImage.BoxBlurFilter(bgBitmap);
            GetSystem.saveImageSD(bgBitmap,Constant.picPath, Constant.BgImage);
            iv_pic.setImageBitmap(bgBitmap);
            GetSystem.displayBriefMemory(MainActivity.this);
        }else{
            iv_pic.setImageBitmap(BlurImage.decodeSampledBitmapFromPath(Path, 500, 500));
        }
    }    
    
    
    public void voiceSerachResult(String order){
    	Intent intent = new Intent(MainActivity.this, SearchMapActivity.class);
		intent.putExtra("keyWord", order);
		startActivity(intent);
    }
}
