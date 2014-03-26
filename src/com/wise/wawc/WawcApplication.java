package com.wise.wawc;

import java.util.LinkedList;
import java.util.List;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.baidu.mapapi.BMapManager;

/**
 * 初始化地图
 * @author honesty
 */
public class WawcApplication extends Application {
    private static final String TAG = "WawcApplication";
    private List<Activity> activityList = new LinkedList<Activity>();
    private static WawcApplication instance;
	
    private static WawcApplication mInstance = null;

    /**
     * 百度地图key
     */
    public static final String strKey = "zwIFsm9hVHYmroq923Psz3xv";
    BMapManager mBMapManager = null;
	
	@Override
    public void onCreate() {
	    super.onCreate();
		mInstance = this;
		initEngineManager(this);
	}
	
	public void initEngineManager(Context context) {
        if (mBMapManager == null) {
            Log.d(TAG, "mBMapManager实例化");
            mBMapManager = new BMapManager(context);
        }
        if (!mBMapManager.init(strKey,null)) {
            Toast.makeText(WawcApplication.getInstance().getApplicationContext(), 
                    "BMapManager  初始化错误!", Toast.LENGTH_LONG).show();
        }
    }
	
	
	public static WawcApplication getInstance() {
		return mInstance;
	}
	
	/**
	 * 关闭activity
	 * @return
	 */
	public static WawcApplication getActivityInstance(){
        if(null == instance){
            instance = new WawcApplication();
        }
        return instance;
    }
    public void addActivity(Activity activity){
        activityList.add(activity);
    }
    public void exit(){
        for(Activity activity : activityList){
            activity.finish();
        }
    }
	
}