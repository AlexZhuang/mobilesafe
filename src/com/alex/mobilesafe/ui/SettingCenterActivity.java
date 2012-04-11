package com.alex.mobilesafe.ui;

import com.alex.mobilesafe.R;
import com.alex.mobilesafe.service.WatchDogService;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

public class SettingCenterActivity extends Activity {
	private TextView tv_setting_applock;
	private CheckBox cb_setting_applock;
	private Intent watchdogintent;
	private SharedPreferences sp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		boolean islockserviceopen = sp.getBoolean("islockserviceopen", false);
		setContentView(R.layout.setting_center);
		cb_setting_applock = (CheckBox) this
				.findViewById(R.id.cb_setting_applock);
		tv_setting_applock = (TextView) this
				.findViewById(R.id.tv_setting_applock);
		if(islockserviceopen){
			tv_setting_applock.setText("程序锁服务已经开启");
			cb_setting_applock.setChecked(true);
		}else{
			tv_setting_applock.setText("程序锁服务已经停止");
		}
		watchdogintent = new Intent(this,WatchDogService.class);
		
		
		cb_setting_applock.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					startService(watchdogintent);
					tv_setting_applock.setText("程序锁服务已经开启");
					Editor editor = sp.edit();
					editor.putBoolean("islockserviceopen", true);
					editor.commit();
				}else{
					stopService(watchdogintent); //->ondestroy->flag false->停止子线程
					tv_setting_applock.setText("程序锁服务已经停止");
					Editor editor = sp.edit();
					editor.putBoolean("islockserviceopen", false);
					editor.commit();
				}
				
			}
		});

	}

}
