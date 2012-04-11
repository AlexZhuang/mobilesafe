package com.alex.mobilesafe.ui;

import com.alex.mobilesafe.R;
import com.alex.mobilesafe.service.IService;
import com.alex.mobilesafe.service.WatchDogService;
import com.alex.mobilesafe.util.MD5Encoder;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class LockScreenActivity extends Activity {
	private static final String TAG = "LockScreenActivity";
	private ImageView iv_app_lock_pwd_icon;
	private TextView tv_app_lock_pwd_name;
	private EditText et_app_lock_pwd;
	private SharedPreferences sp;
	private String realpwd;
	private IService iService;
	private MyConn myconn;
	private String packname;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.app_lock_pwd);
		myconn = new MyConn();
		Intent intent = new Intent(this,WatchDogService.class);
		bindService(intent, myconn, BIND_AUTO_CREATE);
		sp =getSharedPreferences("config", MODE_PRIVATE);
		realpwd = sp.getString("password", "");
		packname = getIntent().getStringExtra("packname");
		iv_app_lock_pwd_icon = (ImageView) this
				.findViewById(R.id.iv_app_lock_pwd_icon);
		tv_app_lock_pwd_name = (TextView) this
				.findViewById(R.id.tv_app_lock_pwd_name);
		et_app_lock_pwd = (EditText) this.findViewById(R.id.et_app_lock_pwd);
		// ��ɽ���ĳ�ʼ�� 
		ApplicationInfo appinfo;
		try {
			appinfo = getPackageManager().getPackageInfo(packname, 0).applicationInfo;

			Drawable appicon = appinfo.loadIcon(getPackageManager());
			String appname = appinfo.loadLabel(getPackageManager()).toString();
			iv_app_lock_pwd_icon.setImageDrawable(appicon);
			tv_app_lock_pwd_name.setText(appname);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * ȷ����ť��Ӧ�ĵ���¼�
	 */
	public void confirm(View view) {
		//�õ��û����������
		String password = et_app_lock_pwd.getText().toString().trim();
		
		if(TextUtils.isEmpty(password)){
			Toast.makeText(this, "���벻��Ϊ��", 1).show();
			return ;
		}else{
			
			if(MD5Encoder.encode(password).equals(realpwd)){
				// ֪ͨ���Ź� ��ʱ��ȡ�����������ı���
				iService.callAppProtectStop(packname);
				finish();
			}else{
				Toast.makeText(this, "�������", 1).show();
				return ;
			}

		}
		
		
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(event.getKeyCode()==KeyEvent.KEYCODE_BACK){
			return true; // ��ֹ�����¼��������·ַ�
		}
		return super.onKeyDown(keyCode, event);
	}
	
	
	private class MyConn implements ServiceConnection{

		public void onServiceConnected(ComponentName name, IBinder service) {
			iService = (IService)service;
			
		}

		public void onServiceDisconnected(ComponentName name) {
			
		}
		
	}
	
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unbindService(myconn);
	}
	
}
