package com.wise.service;

import com.wise.pubclas.Constant;
import com.wise.pubclas.Variable;

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
		userData = this.context.getSharedPreferences(Constant.sharedPreferencesName, Activity.MODE_PRIVATE);
	}
	public void saveData(String defaultCenter,boolean againstPush,boolean bugPush,boolean trafficDepartment){
		editor = userData.edit();
		editor.putString(Constant.defaultCenter_key, defaultCenter);
		editor.putBoolean(Constant.againstPush_key, againstPush);
		editor.putBoolean(Constant.faultPush_key, bugPush);
		editor.putBoolean(Constant.remaindPush_key, trafficDepartment);
		editor.commit();
	}
	
	public String getDefaultCenter(){
		return userData.getString("defaultCenter", Variable.defaultCenter);
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
