package com.alex.mobilesafe.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;

import com.alex.mobilesafe.R;
import com.alex.mobilesafe.receiver.MyAdmin;

public class SetupGuide4Activity extends Activity implements OnClickListener{
	private Button btn_setup4_previous;
	private Button btn_setup4_finish;
	private CheckBox ck_setup4_isSetup;
	private SharedPreferences sp ;
	
	 @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setupguide4);
		sp = getSharedPreferences("config", Context.MODE_PRIVATE);
		btn_setup4_previous = (Button) findViewById(R.id.btn_setup4_previous);
		btn_setup4_finish = (Button) findViewById(R.id.btn_setup4_finish);
		
		btn_setup4_previous.setOnClickListener(this);
		btn_setup4_finish.setOnClickListener(this);
		
		ck_setup4_isSetup = (CheckBox)findViewById(R.id.ck_setup4_isSetup);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_setup4_previous:
			finish();
			Intent setup3Intent = new Intent(this,SetupGuide3Activity.class);
			overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
			startActivity(setup3Intent);
			break;
		case R.id.btn_setup4_finish:
			
			if(ck_setup4_isSetup.isChecked()){
				Editor editor = sp.edit();
				editor.putBoolean("isSetupAlready", true);
				editor.putBoolean("isProtecting", true);
				editor.commit();
				finish();
				//激活设备管理器
				activateDevicePolicyManager();
				
			}else{
				AlertDialog.Builder builder = new Builder(this);
				builder.setTitle("提醒");
				builder.setMessage("强烈建议开启保护，是否完成设置？");
				builder.setPositiveButton("确定", new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Editor editor = sp.edit();
						editor.putBoolean("isProtecting", false);
						editor.commit();
						finish();
					}
				});
				builder.setNegativeButton("取消", new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int which) {
						return ;
					}
				});
				builder.create().show();
			}
			break;
		}
	}

	private void activateDevicePolicyManager() {
		DevicePolicyManager manager = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
		ComponentName mAdminName = new ComponentName(this, MyAdmin.class);

		if (!manager.isAdminActive(mAdminName)) {
			Intent intent = new Intent(
					DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
			intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mAdminName);
			startActivity(intent);
		}
	}
}
