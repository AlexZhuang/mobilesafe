package com.alex.mobilesafe.ui;


import net.youmi.android.AdManager;
import net.youmi.android.AdView;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alex.mobilesafe.R;
import com.alex.moiblesafe.adapter.MainUIAdapter;

public class MainActivity extends Activity implements OnItemClickListener {
	private static final String TAG = "MainActivity";
	
   //  注意 请在程序入口点使用static代码块初始化AdManager.init 
	static {
		//				应用Id				应用密码			      广告请求间隔(s)   测试模式      
		AdManager.init("537ef88653a2993c", "b9e10bcfe994a9fb", 30, 			false);
	}
	private GridView gv_main;
	private MainUIAdapter adapter;
	// 用来持久化一些配置信息
	private SharedPreferences sp;
	
	private LinearLayout ll_main;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);	
		setContentView(R.layout.mainscreen);
		
		//初始化广告视图 
		AdView adView = new AdView(this); 
		FrameLayout.LayoutParams params = new 
		FrameLayout.LayoutParams(FrameLayout.LayoutParams.FILL_PARENT, 
		FrameLayout.LayoutParams.WRAP_CONTENT); 
		     
		//设置广告出现的位置(悬浮于屏幕右下角)      
		params.gravity=Gravity.BOTTOM|Gravity.RIGHT;  
		 //将广告视图加入Activity中 
		addContentView(adView, params);  
		 
		
		sp = this.getSharedPreferences("config", Context.MODE_PRIVATE);
		gv_main = (GridView) this.findViewById(R.id.gv_main);
		adapter = new MainUIAdapter(this);
		gv_main.setAdapter(adapter);
		gv_main.setOnItemClickListener(this);
		gv_main.setOnItemLongClickListener(new OnItemLongClickListener(){
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, final View view,
					int position, long id) {
				if(position==0){
					AlertDialog.Builder builder = new Builder(MainActivity.this);
					builder.setTitle("设置");
					builder.setMessage("请输入要更改的名称");
					final EditText et = new EditText(MainActivity.this);
					et.setHint("请输入文本");
					builder.setView(et);
					builder.setPositiveButton("确定", new OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							String updateName = et.getText().toString().trim();
							if("".equals(updateName)){
								Toast.makeText(MainActivity.this, "更改名称不能为空", 0).show();
								return ;
							}else{
								Editor editor = sp.edit();
								editor.putString("lost_name", updateName);
								editor.commit();
								TextView tv = (TextView)view.findViewById(R.id.tv_main_name);
								tv.setText(updateName);
							}
							
						}
					});
					
					builder.setNegativeButton("取消", new OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							return ;
							
						}
					});
					builder.create().show();
				}
				return false;
			}
			
		});
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		switch (position) {
		case 0:
			Log.i(TAG, "进入手机防盗页面");
			Intent lostProtectIntent = new Intent(this,LostProtectedActivity.class);
			startActivity(lostProtectIntent);
			break;
		case 1:
			Log.i(TAG, "进入通讯卫士页面");
			Intent callSmsIntent = new Intent(this,CallSmsActivity.class);
			startActivity(callSmsIntent);
			break;
		case 2:
			Log.i(TAG, "进入软件管理页面");
			Intent appManageIntent = new Intent(this,AppManagerActivity.class);
			startActivity(appManageIntent);
			break;
		case 3:
			Log.i(TAG, "进入任务管理页面");
			Intent taskManageIntent = new Intent(this,TaskManagerActivity.class);
			startActivity(taskManageIntent);
			break;
		case 4:
			Log.i(TAG, "进入流量管理页面");
			Intent TrafficManagerIntent = new Intent(this,TrafficManagerActivity.class);
			startActivity(TrafficManagerIntent);
			break;
		case 7:
			Log.i(TAG, "进入高级工具页面");
			Intent aToolsIntent = new Intent(this,AtoolsActivity.class);
			startActivity(aToolsIntent);
			break;
		case 8:
			Log.i(TAG, "进入设置中心页面");
			Intent settingCenterIntent = new Intent(this,SettingCenterActivity.class);
			startActivity(settingCenterIntent);
			break;
		}
	}

	
	

	
}
