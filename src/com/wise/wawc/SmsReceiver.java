package com.wise.wawc;

import cn.jpush.android.api.JPushInterface;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class SmsReceiver extends BroadcastReceiver{
	@Override
	public void onReceive(Context context, Intent intent) {
		System.out.println(intent.getAction());     
        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
        	System.out.println("JPush用户注册成功");          
        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
        	System.out.println("接受到推送下来的自定义消息");        	
        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
        	//TODO 确认消息发送，上传到自己服务器        	
        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
        	   		
        } else {
        	System.out.println("Unhandled intent - " + intent.getAction());
        }
	}
}