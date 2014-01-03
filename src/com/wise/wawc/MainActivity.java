package com.wise.wawc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qzone.QZone;
import com.iflytek.speech.RecognizerResult;
import com.iflytek.speech.SpeechConfig.RATE;
import com.iflytek.speech.SpeechError;
import com.iflytek.ui.RecognizerDialog;
import com.iflytek.ui.RecognizerDialogListener;
import com.wise.data.BrankModel;
import com.wise.data.CarData;
import com.wise.extend.SlidingMenuView;
import com.wise.pubclas.Config;
import com.wise.pubclas.NetThread;
import android.app.ActivityGroup;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 菜单界面
 * 
 * @author honesty
 */
public class MainActivity extends ActivityGroup implements
        PlatformActionListener {
    private static final String TAG = "MainActivity";
    private static final int Get_pic = 1;
    private static final int Login = 2;
    SlidingMenuView slidingMenuView;
    ViewGroup tabcontent;
    Platform platformQQ;
    Platform platformSina;
    ImageView iv_activity_main_logo, iv_activity_main_qq,
            iv_activity_main_sina, iv_activity_main_login_sina,
            iv_activity_main_login_qq;
    TextView tv_activity_main_name;
    View view = null;
    // 你要做什么常用命令
    private View wantRefuel; // 要加油
    private View wantMaintain; // 维保
    private View wantWash; // 洗车
    private View wantRescue; // 救援
    private View wantInsurance; // 报险
    private View wantPark; // 停车

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityFactory.A = this;
        slidingMenuView = (SlidingMenuView) findViewById(R.id.sliding_menu_view);
        ActivityFactory.S = slidingMenuView;
        tabcontent = (ViewGroup) slidingMenuView
                .findViewById(R.id.sliding_body);
        tabcontent.setOnClickListener(onClickListener);
        ActivityFactory.v = tabcontent;
        // 获取屏幕宽度
        WindowManager wm = (WindowManager) this
                .getSystemService(Context.WINDOW_SERVICE);
        int width = (int) (wm.getDefaultDisplay().getWidth() * 0.8);// 屏幕宽度
        TextView tv_activity_menu = (TextView) findViewById(R.id.tv_activity_menu);
        tv_activity_menu.setWidth(width);
        RelativeLayout rl_activity_main_home = (RelativeLayout) findViewById(R.id.rl_activity_main_home);
        rl_activity_main_home.setOnClickListener(onClickListener);
        iv_activity_main_login_sina = (ImageView) findViewById(R.id.iv_activity_main_login_sina);
        iv_activity_main_login_sina.setOnClickListener(onClickListener);
        iv_activity_main_qq = (ImageView) findViewById(R.id.iv_activity_main_qq);
        iv_activity_main_sina = (ImageView) findViewById(R.id.iv_activity_main_sina);
        iv_activity_main_login_qq = (ImageView) findViewById(R.id.iv_activity_main_login_qq);
        iv_activity_main_login_qq.setOnClickListener(onClickListener);
        iv_activity_main_logo = (ImageView) findViewById(R.id.iv_activity_main_logo);
        tv_activity_main_name = (TextView) findViewById(R.id.tv_activity_main_name);
        view = findViewById(R.id.rl_activity_main_voice);
        view.setOnClickListener(onClickListener);

        wantRefuel = findViewById(R.id.rl_activity_main_oil);
        wantMaintain = findViewById(R.id.rl_activity_main_maintain);
        wantWash = findViewById(R.id.rl_activity_main_wash);
        wantRescue = findViewById(R.id.rl_activity_main_help);
        wantInsurance = findViewById(R.id.rl_activity_main_safety);
        wantPark = findViewById(R.id.rl_activity_main_park);

        wantRefuel.setOnClickListener(onClickListener);
        wantMaintain.setOnClickListener(onClickListener);
        wantWash.setOnClickListener(onClickListener);
        wantRescue.setOnClickListener(onClickListener);
        wantInsurance.setOnClickListener(onClickListener);
        wantPark.setOnClickListener(onClickListener);

        // 车友圈
        TextView vehiclefriend = (TextView) findViewById(R.id.car_circle);
        vehiclefriend.setOnClickListener(onClickListener);
        // 我的收藏
        TextView myCollection = (TextView) findViewById(R.id.menu_my_collection);
        myCollection.setOnClickListener(onClickListener);
        // 设置中心
        TextView settingCenter = (TextView) findViewById(R.id.settting_center);
        settingCenter.setOnClickListener(onClickListener);
        TextView tv_activity_main_right = (TextView) findViewById(R.id.tv_activity_main_right);
        tv_activity_main_right.setWidth(width);// 设置菜单宽度

        LinearLayout ll_activity_main_car_remind = (LinearLayout) findViewById(R.id.ll_activity_main_car_remind);
        ll_activity_main_car_remind.setOnClickListener(onClickListener);
        LinearLayout ll_activity_main_mycar = (LinearLayout) findViewById(R.id.ll_activity_main_mycar);
        ll_activity_main_mycar.setOnClickListener(onClickListener);
        LinearLayout ll_activity_main_terminal = (LinearLayout) findViewById(R.id.ll_activity_main_terminal);
        ll_activity_main_terminal.setOnClickListener(onClickListener);
        LinearLayout ll_activity_main_orders = (LinearLayout) findViewById(R.id.ll_activity_main_orders);
        ll_activity_main_orders.setOnClickListener(onClickListener);

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
        GetData();


        //启动初始化推送
        //JPushInterface.setDebugMode(true);
        JPushInterface.init(getApplicationContext());
        // 模拟推送
        Set<String> tagSet = new LinkedHashSet<String>();
        tagSet.add("371278698");
        // 调用JPush API设置Tag
        JPushInterface.setAliasAndTags(getApplicationContext(), null, tagSet,
                new TagAliasCallback() {
                    @Override
                    public void gotResult(int arg0, String arg1,
                            Set<String> arg2) {
                        Log.d(TAG, "推送：arg0 =" + arg0 + ",arg1 = " + arg1);
                    }
                });

    }

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
            case R.id.ll_activity_main_car_remind:
                ToCarRemind();
                break;
            case R.id.ll_activity_main_mycar:
                ToMyCar();
                break;
            // 我的收藏
            case R.id.menu_my_collection:
                ToMyCollection();
                break;
            case R.id.ll_activity_main_terminal:
                ToCarTerminal();
                break;
            case R.id.ll_activity_main_orders:
                Toorders();
                break;
            // 设置中心
            case R.id.settting_center:
                ToSettingCenter();
                break;
            case R.id.iv_activity_main_login_qq:
                platformQQ.setPlatformActionListener(MainActivity.this);
                platformQQ.showUser(null);
                break;
            case R.id.iv_activity_main_login_sina:
                platformSina.setPlatformActionListener(MainActivity.this);
                platformSina.SSOSetting(true);
                platformSina.showUser(null);
                break;
            case R.id.rl_activity_main_oil: // 加油
                // startActivity(new
                // Intent(MainActivity.this,SearchResultActivity.class));
                break;
            case R.id.rl_activity_main_maintain: // 维保

                break;
            case R.id.rl_activity_main_wash: // 洗车

                break;
            case R.id.rl_activity_main_help: // 救援

                break;
            case R.id.rl_activity_main_safety: // 报险

                break;
            case R.id.rl_activity_main_park: // 停车

                break;
            default:

                return;
            }
        }
    };
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
            case Get_pic:
                iv_activity_main_logo.setImageBitmap(bimage);
                break;

            case Login:
                isLogin();
                break;
            }
        }
    };

    private void isLogin() {
        Log.d(TAG, "platformQQ.getDb().isValid() = "
                + platformQQ.getDb().isValid());
        Log.d(TAG, "platformQQ.getDb().getUserId() = "
                + platformQQ.getDb().getUserId());
        Log.d(TAG, "platformSina.getDb().isValid() = "
                + platformSina.getDb().isValid());
        Log.d(TAG, "platformSina.getDb().getUserId() = "
                + platformSina.getDb().getUserId());
        if (platformQQ.getDb().isValid()) {
            System.out.println("qq登录");
            Log.d(TAG, "platformQQ.getDb().getToken() = "
                    + platformQQ.getDb().getToken());
            tv_activity_main_name.setText(platformQQ.getDb().getUserName());
            iv_activity_main_qq.setVisibility(View.VISIBLE);
            iv_activity_main_sina.setVisibility(View.VISIBLE);
            iv_activity_main_login_sina.setVisibility(View.GONE);
            iv_activity_main_login_qq.setVisibility(View.GONE);
            new Thread(new GetBitMapFromUrlThread(platformQQ.getDb()
                    .getUserIcon())).start();
        } else if (platformSina.getDb().isValid()) {
            System.out.println("sina登录");
            Log.d(TAG, "platformSina.getDb().getToken() = "
                    + platformSina.getDb().getToken());
            tv_activity_main_name.setText(platformSina.getDb().getUserName());
            iv_activity_main_qq.setVisibility(View.VISIBLE);
            iv_activity_main_sina.setVisibility(View.VISIBLE);
            iv_activity_main_login_sina.setVisibility(View.GONE);
            iv_activity_main_login_qq.setVisibility(View.GONE);
            new Thread(new GetBitMapFromUrlThread(platformSina.getDb()
                    .getUserIcon())).start();
        } else {
            System.out.println("没有登录");
            iv_activity_main_qq.setVisibility(View.GONE);
            iv_activity_main_sina.setVisibility(View.GONE);
            iv_activity_main_login_sina.setVisibility(View.VISIBLE);
            iv_activity_main_login_qq.setVisibility(View.VISIBLE);
        }
    }

    Bitmap bimage;

    class GetBitMapFromUrlThread extends Thread {
        String url;

        public GetBitMapFromUrlThread(String url) {
            this.url = url;
        }

        @Override
        public void run() {
            super.run();
            bimage = NetThread.getBitmapFromURL(url);
            Message message = new Message();
            message.what = Get_pic;
            handler.sendMessage(message);
        }
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

    public void ToFriendHome() {
        if (platformQQ.getDb().isValid() || platformSina.getDb().isValid()) {
            Intent i = new Intent(MainActivity.this, FriendHomeActivity.class);
            View v = getLocalActivityManager().startActivity(
                    FriendHomeActivity.class.getName(), i).getDecorView();
            tabcontent.removeAllViews();
            tabcontent.addView(v);
            slidingMenuView.snapToScreen(1);
        } else {
            Toast.makeText(getApplicationContext(), "请登录", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    /**
     * 车友圈
     */
    public void ToVehicleFriends() {
        Intent intent = new Intent(MainActivity.this,
                VehicleFriendActivity.class);
        View vv = getLocalActivityManager().startActivity(
                VehicleFriendActivity.class.getName(), intent).getDecorView();
        tabcontent.removeAllViews();
        tabcontent.addView(vv);
        slidingMenuView.snapToScreen(1);
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
        slidingMenuView.snapToScreen(1);
        Intent i = new Intent(MainActivity.this, MyVehicleActivity.class);
        View view = getLocalActivityManager().startActivity(
                MyVehicleActivity.class.getName(), i).getDecorView();
        tabcontent.removeAllViews();
        tabcontent.addView(view);
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

    /**
     * 模拟车辆数据
     */
    private void GetData() {
        List<CarData> carDatas = new ArrayList<CarData>();
        for (int i = 0; i < 7; i++) {
            CarData carData = new CarData();
            carData.setCarLogo(1);
            carData.setCarNumber("粤B12345");
            carData.setCheck(false);
            carDatas.add(carData);
        }
        Config.carDatas = carDatas;
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
    }

    @Override
    public void onCancel(Platform arg0, int arg1) {
        Log.d(TAG, "登录取消");
    }

    @Override
    public void onComplete(Platform arg0, int arg1, HashMap<String, Object> arg2) {
        Log.d(TAG, "登录成功" + arg0.getName());
        Iterator iterator = arg2.entrySet().iterator();
        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();
            System.out.println(entry.getKey() + "," + entry.getValue());
        }
        Message message = new Message();
        message.what = Login;
        handler.sendMessage(message);
    }

    @Override
    public void onError(Platform arg0, int arg1, Throwable arg2) {
        Log.d(TAG, "登录出错");
        arg0.removeAccount();
    }
}
