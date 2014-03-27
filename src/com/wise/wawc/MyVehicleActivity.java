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
		Fragment_vehicle fragment_vehicle = new Fragment_vehicle();
        Bundle bundle=new Bundle();  
        bundle.putBoolean("isJump", true);  
        fragment_vehicle.setArguments(bundle);  
        
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction(); 
        transaction.add(R.id.fm_vehicle, fragment_vehicle);
        transaction.commit();
	}
}
