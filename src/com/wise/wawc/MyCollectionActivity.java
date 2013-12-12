package com.wise.wawc;

import com.wise.service.CollectionAdapter;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;

public class MyCollectionActivity extends Activity {
	private ListView collectionList;
	private CollectionAdapter collectionAdapter;
	private Button menuBt;
	private Button homeBt;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_collection);
		collectionList = (ListView) findViewById(R.id.my_collection_list);
		collectionAdapter = new CollectionAdapter(this);
		collectionList.setAdapter(collectionAdapter);
		menuBt = (Button) findViewById(R.id.my_vechile_menu);
		homeBt = (Button) findViewById(R.id.my_vechile_home);
		homeBt.setOnClickListener(new ClickListener());
		menuBt.setOnClickListener(new ClickListener());
	}
	
	class ClickListener implements OnClickListener{
		public void onClick(View v) {
			switch(v.getId()){
			case R.id.my_vechile_home:
				ActivityFactory.A.ToHome();
				break;
			case R.id.my_vechile_menu:
				ActivityFactory.A.LeftMenu();
				break;
			default :
				return;
			}
		}
	}
}
