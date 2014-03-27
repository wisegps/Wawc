package com.wise.wawc;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

/**
 * 我的账户
 * @author honesty
 */
public class AccountActivity extends FragmentActivity{
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_account);
		Fragment_account fragment_account = new Fragment_account();
		Bundle bundle=new Bundle();  
        bundle.putBoolean("isJump", true);  
        fragment_account.setArguments(bundle);  
        
		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction transaction = fragmentManager.beginTransaction(); 
		transaction.add(R.id.fm_account, fragment_account);
		transaction.commit();	
	}
}
