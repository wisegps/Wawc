package com.wise.service;

import com.wise.pubclas.Config;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * 保存用户的偏好设置
 * @author Mr.Wang
 */
public class SaveSettingData {
	private Context context;
	private SharedPreferences userData;
	private Editor editor;
	public SaveSettingData(Context context) {
		this.context = context;
		userData = this.context.getSharedPreferences(Config.sharedPreferencesName, Activity.MODE_PRIVATE);
	}
	public void saveData(String defaultCenter,boolean againstPush,boolean bugPush,boolean trafficDepartment){
		editor = userData.edit();
		editor.putString("defaultCenter", defaultCenter);
		editor.putBoolean("againstPush", againstPush);
		editor.putBoolean("bugPush", bugPush);
		editor.putBoolean("trafficDepartment", trafficDepartment);
		editor.commit();
	}
	
	public String getDefaultCenter(){
		return userData.getString("defaultCenter", Config.defaultCenter);
	}
	public boolean getAgainstPush(){
		return userData.getBoolean("againstPush", true);
	}
	public boolean getBugPush(){
		return userData.getBoolean("bugPush", true);
	}
	public boolean getTrafficDepartment(){
		return userData.getBoolean("trafficDepartment", true);
	}
}
