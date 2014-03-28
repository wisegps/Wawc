package com.wise.wawc;

import java.net.URLEncoder;
import java.util.LinkedHashSet;
import java.util.Set;
import org.json.JSONException;
import org.json.JSONObject;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qzone.QZone;
import com.wise.extend.FaceConversionUtil;
import com.wise.extend.OnViewTouchMoveListener;
import com.wise.extend.PicHorizontalScrollView;
import com.wise.extend.SlidingMenuView;
import com.wise.pubclas.Constant;
import com.wise.pubclas.GetSystem;
import com.wise.pubclas.NetThread;
import com.wise.pubclas.Variable;
import com.wise.sql.DBExcute;
import com.wise.sql.DBHelper;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 菜单界面
 * @author honesty
 */
public class MainActivity extends FragmentActivity implements TagAliasCallback {
    private static final String TAG = "MainActivity";

    private static final int Get_Pic = 2;//获取登录头像
    private static final int Bind_ID = 3; //绑定ID
    private static final int get_noti_count = 4; //获取消息提醒
    
    private FragmentManager fragmentManager;
    Fragment_account fragment_account;
    Fragment_collection fragment_collection;
    Fragment_home fragment_home;
    Fragment_setting fragment_setting;
    Fragment_sms fragment_sms;
    Fragment_vehiclefriend fragment_vehiclefriend;
    Fragment_vehicle fragment_vehicle;
    
    SlidingMenuView slidingMenuView;
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
        setContentView(R.layout.activity_main);
        JPushInterface.init(getApplicationContext());
        thread = new ParseFaceThread();
        thread.start();
        hsv_pic = (PicHorizontalScrollView) findViewById(R.id.hsv_pic);
        ActivityFactory.A = this;
        slidingMenuView = (SlidingMenuView) findViewById(R.id.sliding_menu_view);
        fragmentManager = getSupportFragmentManager();
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
        ActivityFactory.S = slidingMenuView;
        slidingMenuView.setOnViewTouchMoveListener(new OnViewTouchMoveListener() {
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
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        fragment_home = new Fragment_home();
        transaction.add(R.id.sliding_body, fragment_home); 
        transaction.commit();
        
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
                ToHome();
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
                Toast.makeText(MainActivity.this, R.string.new_version, Toast.LENGTH_SHORT).show();
                //ToCarTerminal();
                break;
            case R.id.my_orders:
                Toast.makeText(MainActivity.this, R.string.new_version, Toast.LENGTH_SHORT).show();
                //Toorders();
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
                if(Variable.carDatas == null || Variable.carDatas.size() == 0){                    
                }else{
                    SharedPreferences preferences = getSharedPreferences(Constant.sharedPreferencesName, Context.MODE_PRIVATE);
                    final int DefaultVehicleID = preferences.getInt(Constant.DefaultVehicleID, 0);
                    
                    new AlertDialog.Builder(MainActivity.this)
                    .setTitle("列表框")
                    .setItems(new String[] { "拨打电话", "位置分享" },
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,int which) {
                                    if (which == 0) {
                                        Intent intent = new Intent(Intent.ACTION_DIAL,Uri.parse("tel:"+ Variable.carDatas.get(DefaultVehicleID).getMaintain_tel()));  
                                        MainActivity.this.startActivity(intent);
                                    } else {
                                        Intent intent_help = new Intent(MainActivity.this,ShareLocationActivity.class);
                                        intent_help.putExtra("reason", "救援");
                                        intent_help.putExtra("index", DefaultVehicleID);
                                        MainActivity.this.startActivity(intent_help);
                                    }
                                }
                            }).setNegativeButton("确定", null).show();
                }                
                break;
            case R.id.tv_bx:
                SharedPreferences preferences = getSharedPreferences(Constant.sharedPreferencesName, Context.MODE_PRIVATE);
                final int DefaultVehicleID = preferences.getInt(Constant.DefaultVehicleID, 0);
                
                new AlertDialog.Builder(MainActivity.this)
                .setTitle("列表框")
                .setItems(new String[] { "拨打电话", "位置分享" },
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,int which) {
                                if (which == 0) {
                                    Intent intent = new Intent(Intent.ACTION_DIAL,Uri.parse("tel:"+ Variable.carDatas.get(DefaultVehicleID).getMaintain_tel()));  
                                    MainActivity.this.startActivity(intent);
                                } else {
                                    Intent intent_help = new Intent(MainActivity.this,ShareLocationActivity.class);
                                    intent_help.putExtra("reason", "报险");
                                    intent_help.putExtra("index", DefaultVehicleID);
                                    MainActivity.this.startActivity(intent_help);
                                }
                            }
                        }).setNegativeButton("确定", null).show();
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
                    GetSystem.saveImageSD(bimage, Constant.userIconPath, Constant.UserImage,100);
                    iv_activity_main_logo.setImageBitmap(bimage);
                }
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
        SharedPreferences preferences = getSharedPreferences(
                Constant.sharedPreferencesName, Context.MODE_PRIVATE);
        String platform = preferences.getString(Constant.platform, "");
        if(Constant.isTest){
            platform = "sina登录";
        }
        if(platform.equals("qq")){
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
        }else{
            System.out.println("sina登录");
            if(Constant.isTest){
                tv_activity_main_name.setText("Honesty_fly");
                Variable.cust_name = "Honesty_fly";
                iv_activity_main_login.setImageResource(R.drawable.side_icon_sina_press);
                iv_activity_main_arrow.setVisibility(View.VISIBLE);                
                Login("2152086902", "Honesty_fly", LocationProvince, LocationCity, 
                        "http://tp3.sinaimg.cn/2152086902/50/5637362071/1", "remark"); 
                bimage = BitmapFactory.decodeFile(Constant.userIconPath + Constant.UserImage);
                Constant.UserIconUrl = "http://tp3.sinaimg.cn/2152086902/50/5637362071/1";
                if(bimage != null){            
                    //iv_activity_main_logo.setImageBitmap(BlurImage.getRoundedCornerBitmap(bimage));
                    iv_activity_main_logo.setImageBitmap(bimage);
                }else{
                    //TODO 获取图片            
                }
                new Thread(new GetBitMapFromUrlThread("http://tp3.sinaimg.cn/2152086902/50/5637362071/1")).start();
                SharedPreferences preferences1 = getSharedPreferences(Constant.sharedPreferencesName, Context.MODE_PRIVATE);
                Variable.cust_id  = preferences1.getString(Constant.sp_cust_id, "");
                Variable.auth_code = preferences1.getString(Constant.sp_auth_code, "");
                Set<String> tagSet = new LinkedHashSet<String>();
                tagSet.add("2152086902");
                //调用JPush API设置Tag
                JPushInterface.setAliasAndTags(getApplicationContext(), null, tagSet, this);
            }else{
                tv_activity_main_name.setText(platformSina.getDb().getUserName());
                Variable.cust_name = platformSina.getDb().getUserName();
                iv_activity_main_login.setImageResource(R.drawable.side_icon_sina_press);
                iv_activity_main_arrow.setVisibility(View.VISIBLE);
                platfromIsLogin(platformSina);
                Login(platformSina.getDb().getUserId(), platformSina.getDb()
                        .getUserName(), LocationProvince, LocationCity, platformSina.getDb()
                        .getUserIcon(), "remark"); 
            }
            
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
            //iv_activity_main_logo.setImageBitmap(BlurImage.getRoundedCornerBitmap(bimage));
            iv_activity_main_logo.setImageBitmap(bimage);
        }else{
            //TODO 获取图片            
        }
        new Thread(new GetBitMapFromUrlThread(platform.getDb().getUserIcon())).start();
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
        return true;
    }

    long waitTime = 2000;
    long touchTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (slidingMenuView.getCurrentScreen() == 2) {
                slidingMenuView.snapToScreen(1);
            }else{
                if(isHome){
                    long currentTime = System.currentTimeMillis();
                    if (touchTime == 0 || (currentTime - touchTime) >= waitTime) {
                        Toast.makeText(this, "再按一次退出客户端", Toast.LENGTH_SHORT).show();
                        touchTime = currentTime;
                    } else {
                        finish();
                    }
                }else{
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
                }
            }                                   
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        ShareSDK.stopSDK(this);
        
        stopService(new Intent(MainActivity.this, LocationService.class));
        Constant.start = 0;  // 开始页
        Constant.pageSize = 10;   //每页数量
        Constant.totalPage = 0;   //数据总量
        Constant.currentPage = 0;  //当前页
        Constant.start1 = 0;  // 开始页
        Constant.pageSize1 = 10;   //每页数量
        Constant.totalPage1 = 0;   //数据总量
        Constant.currentPage1 = 0;  //当前页
        WawcApplication app = (WawcApplication)this.getApplication();
        if (app.mBMapManager != null) {
            app.mBMapManager.destroy();
            app.mBMapManager = null;
        }
        System.exit(0);
    }
    boolean isHome = true;
    
    private void setTabSelection(int index) {
        // 开启一个Fragment事务  
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        // 先隐藏掉所有的Fragment，以防止有多个Fragment显示在界面上的情况  
        hideFragments(transaction);  
        switch (index) {  
        case 0:  
            if (fragment_account == null) {  
                // 如果MessageFragment为空，则创建一个并添加到界面上  
                fragment_account = new Fragment_account();
                Bundle bundle=new Bundle();  
                bundle.putBoolean("isJump", false);  
                fragment_account.setArguments(bundle); 
                transaction.add(R.id.sliding_body, fragment_account);  
            } else {  
                // 如果MessageFragment不为空，则直接将它显示出来  
                transaction.show(fragment_account);  
            }  
            break;  
        case 1:
            if (fragment_collection == null) {  
                // 如果ContactsFragment为空，则创建一个并添加到界面上  
                fragment_collection = new Fragment_collection();  
                transaction.add(R.id.sliding_body, fragment_collection);  
            } else {  
                // 如果ContactsFragment不为空，则直接将它显示出来  
                transaction.show(fragment_collection);  
            }  
            break;  
        case 2: 
            if (fragment_home == null) {  
                // 如果NewsFragment为空，则创建一个并添加到界面上  
                fragment_home = new Fragment_home();  
                transaction.add(R.id.sliding_body, fragment_home);  
            } else {  
                // 如果NewsFragment不为空，则直接将它显示出来  
                transaction.show(fragment_home);  
            }  
            break;  
        case 3:
            if (fragment_setting == null) {  
                // 如果SettingFragment为空，则创建一个并添加到界面上  
                fragment_setting = new Fragment_setting();  
                transaction.add(R.id.sliding_body, fragment_setting);  
            } else {  
                // 如果SettingFragment不为空，则直接将它显示出来  
                transaction.show(fragment_setting);  
            }  
            break;
        case 4:
            if (fragment_sms == null) {  
                // 如果SettingFragment为空，则创建一个并添加到界面上  
                fragment_sms = new Fragment_sms();  
                transaction.add(R.id.sliding_body, fragment_sms);  
            } else {  
                // 如果SettingFragment不为空，则直接将它显示出来  
                transaction.show(fragment_sms);  
            }  
            break;
        case 5:
            if (fragment_vehiclefriend == null) {  
                // 如果SettingFragment为空，则创建一个并添加到界面上  
                fragment_vehiclefriend = new Fragment_vehiclefriend();  
                transaction.add(R.id.sliding_body, fragment_vehiclefriend);  
            } else {  
                // 如果SettingFragment不为空，则直接将它显示出来  
                transaction.show(fragment_vehiclefriend);  
            }  
            break;
        case 6:
            if (fragment_vehicle == null) {  
                // 如果SettingFragment为空，则创建一个并添加到界面上  
                fragment_vehicle = new Fragment_vehicle();  
                transaction.add(R.id.sliding_body, fragment_vehicle);  
            } else {  
                // 如果SettingFragment不为空，则直接将它显示出来  
                transaction.show(fragment_vehicle);  
            }  
            break;
        }  
        transaction.commit();  
    }
    
    private void hideFragments(FragmentTransaction transaction) {  
        if (fragment_account != null) {  
            transaction.hide(fragment_account);  
        }  
        if (fragment_collection != null) {  
            transaction.hide(fragment_collection);  
        }  
        if (fragment_home != null) {  
            transaction.hide(fragment_home);  
        }  
        if (fragment_setting != null) {  
            transaction.hide(fragment_setting);  
        }
        if (fragment_sms != null) {  
            transaction.hide(fragment_sms);  
        }
        if (fragment_vehiclefriend != null) {  
            transaction.hide(fragment_vehiclefriend);  
        }
        if (fragment_vehicle != null) {  
            transaction.hide(fragment_vehicle);  
        }
    }
    
    /**
     * 设置中心
     */
    public void ToSettingCenter() {
        isHome = false;
        setTabSelection(3);
        slidingMenuView.snapToScreen(1);
    }

    /**
     * 我的收藏
     */
    private void ToMyCollection() {
        isHome = false;
        setTabSelection(1);
        slidingMenuView.snapToScreen(1);
    }

    /**
     * 首页
     */
    private void ToHome() {
        isHome = true;
        setTabSelection(2);
        slidingMenuView.snapToScreen(1);
    }
    public void ToSms() {
        isHome = false;
        setTabSelection(4);
        slidingMenuView.snapToScreen(1);
    }
    /**
     * 个人信息
     */
    public void ToAccountHome() {
        isHome = false;
        setTabSelection(0); 
        slidingMenuView.snapToScreen(1);
    }

    /**
     * 车友圈
     */
    public void ToVehicleFriends() {
        isHome = false;
        setTabSelection(5);
        slidingMenuView.snapToScreen(1);
    }

    /**
     * 我的爱车
     */
    public void ToMyCar() {
        isHome = false;    	
		if(Variable.carDatas.size() == 0){
 	        //判断网络
 	        startActivity(new Intent(MainActivity.this,NewVehicleActivity.class));
 	    }else{
 	       setTabSelection(6);
 	      slidingMenuView.snapToScreen(1);
 	    }
    }

    /**
     * 我的终端
     */
    public void ToCarTerminal() {
        //isHome = false;
        //setTabSelection(4);
        //slidingMenuView.snapToScreen(1);
    }

    /**
     * 我的订单
     */
    public void Toorders() {
        //isHome = false;
        //setTabSelection(4);
        //slidingMenuView.snapToScreen(1);
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
            int if_alert_noti = 0;
            if(jsonObject.opt("if_alert_noti") != null){
                if_alert_noti = jsonObject.getInt("if_alert_noti");
            }
            int if_event_noti = 0;
            if(jsonObject.opt("if_event_noti") != null){
                if_event_noti = jsonObject.getInt("if_event_noti");
            }
            int if_fault_noti = 0;
            if(jsonObject.opt("if_fault_noti") != null){
                if_fault_noti = jsonObject.getInt("if_fault_noti");
            }
            int if_vio_noti = 0;
            if(jsonObject.opt("if_vio_noti") != null){
                if_vio_noti = jsonObject.getInt("if_vio_noti");
            }
            DBHelper dbHelper = new DBHelper(this);
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cursor = db.rawQuery("select * from " + Constant.TB_Account
                    + " where cust_id=?", new String[] { Variable.cust_id });

            DBExcute dbExcute = new DBExcute();
            ContentValues values = new ContentValues();
            if (jsonObject.opt("contacts") != null) {
                String contacts = jsonObject.getString("contacts");
                values.put("Consignee", contacts);
            }
            if (jsonObject.opt("address") != null) {
                String address = jsonObject.getString("address");
                values.put("Adress", address);
            }
            if (jsonObject.opt("tel") != null) {
                String tel = jsonObject.getString("tel");
                values.put("Phone", tel);
            }
            if (jsonObject.opt("annual_inspect_date") != null) {
                String annual_inspect_date = jsonObject
                        .getString("annual_inspect_date").replace("T", " ")
                        .substring(0, 19);
                values.put("annual_inspect_date", annual_inspect_date);
            }
            if (jsonObject.opt("change_date") != null) {
                String  change_date = jsonObject.getString("change_date")
                        .replace("T", " ").substring(0, 19);
                values.put("change_date", change_date);
            }
            values.put("alert", if_alert_noti);
            values.put("event", if_event_noti);
            values.put("fault", if_fault_noti);
            values.put("vio", if_vio_noti);
            if (cursor.getCount() == 0) {//插入
                values.put("cust_id", Variable.cust_id);
                dbExcute.InsertDB(MainActivity.this, values, Constant.TB_Account);
            } else {//更新
                dbExcute.UpdateDB(this, values, "cust_id=?",new String[] { Variable.cust_id }, Constant.TB_Account);
            }
            cursor.close();
            db.close();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void gotResult(int arg0, String arg1, Set<String> arg2) {
        //System.out.println("arg0 = " + arg0 + " , arg1 = " + arg1);
    }
}