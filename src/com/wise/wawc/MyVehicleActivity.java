package com.wise.wawc;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

/**
 * 我的爱车
 * @author 王庆文
 */
public class MyVehicleActivity extends FragmentActivity{
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_vehicle);
        Fragment_account fragment_account = new Fragment_account();
        Bundle bundle=new Bundle();  
        bundle.putBoolean("isJump", true);  
        fragment_account.setArguments(bundle);  
        
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction(); 
        transaction.add(R.id.fm_vehicle, fragment_account);
        transaction.commit();
	}
}
