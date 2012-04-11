package com.alex.mobilesafe.ui;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.alex.mobilesafe.R;
import com.alex.mobilesafe.engine.DownloadFileTask;
import com.alex.mobilesafe.engine.SmsInfoService;
import com.alex.mobilesafe.service.AddressService;
import com.alex.mobilesafe.service.SmsBackupService;

public class AtoolsActivity extends Activity implements OnClickListener {
	private static final String TAG = "AtoolsActivity";
	private static final boolean SUCCESS = true;
	private static final boolean FAIL = false;
	private static String localFilePath = Environment.getExternalStorageDirectory()+"/phoneNumAddress.db";
	private TextView tv_atools_locationQuery ;
	private SharedPreferences sp;
	private ProgressDialog pd ;
	private CheckBox cb_atools_service;
	private TextView tv_atools_locationShow;
	private TextView tv_atools_locationPosition;
	private TextView tv_atools_smsBackup;
	private TextView tv_atools_smsRestore;
	private TextView tv_atools_app_lock;
	private TextView tv_atools_common_num;
	
	private Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) { 
			Bundle data = msg.getData();
			if(data.getBoolean("downloadResult")){
				Toast.makeText(AtoolsActivity.this, "download finish", 1).show();
			}else{
				Toast.makeText(AtoolsActivity.this, "download fail", 1).show();
			}
			pd.dismiss();
		}
		
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.atools);
		Log.i(TAG, "onCreate....");
		tv_atools_locationQuery = (TextView)findViewById(R.id.tv_atools_locationQuery);
		tv_atools_locationQuery.setOnClickListener(this);
		sp= getSharedPreferences("config", Context.MODE_PRIVATE);
		cb_atools_service = (CheckBox)findViewById(R.id.cb_atools_service);
		cb_atools_service.setOnCheckedChangeListener(new MyOnCheckChangedListener());
		
		boolean isOpenAddressService = sp.getBoolean("isOpenAddressService", false);
		if(isOpenAddressService){
			cb_atools_service.setChecked(true);
			startAddressService();
		}
		
		tv_atools_locationShow = (TextView) findViewById(R.id.tv_atools_locationShow);
		tv_atools_locationShow.setOnClickListener(this);
		
		tv_atools_locationPosition = (TextView) findViewById(R.id.tv_atools_locationPosition);
		tv_atools_locationPosition.setOnClickListener(this);
		
		tv_atools_smsBackup=(TextView)findViewById(R.id.tv_atools_smsBackup);
		tv_atools_smsBackup.setOnClickListener(this);
		
		tv_atools_smsRestore = (TextView)findViewById(R.id.tv_atools_smsRestore);
		tv_atools_smsRestore.setOnClickListener(this);
		
		tv_atools_app_lock = (TextView)findViewById(R.id.tv_atools_app_lock);
		tv_atools_app_lock.setOnClickListener(this);
		
		tv_atools_common_num = (TextView)findViewById(R.id.tv_atools_common_num);
		tv_atools_common_num.setOnClickListener(this);
	}
	private class MyOnCheckChangedListener implements OnCheckedChangeListener{

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			if(isChecked){				
				//判断是否存在资源文件
				if(isResourceExist()){
					Editor editor = sp.edit();
					editor.putBoolean("isOpenAddressService", true);
					editor.commit();
					startAddressService();
				}else{
					//如果不存在则从服务器下载文件public DownloadFileTask(String serverFilePath , String localFilePath , ProgressDialog pd){
					downDbFromServer();
				}			
			}else{
				Editor editor = sp.edit();
				editor.putBoolean("isOpenAddressService", false);
				editor.commit();
				Intent addressServiceIntent = new Intent(AtoolsActivity.this,AddressService.class);
				stopService(addressServiceIntent);
			}
		}
		
	}
	
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_atools_locationQuery:
			//判断是否存在资源文件
			if(isResourceExist()){
				//如果存在则跳到QueryNumberActivity页面
				Intent queryNumberIntent = new Intent(this,QueryNumberActivity.class);
				overridePendingTransition(R.anim.translate_in, R.anim.translate_out);
				startActivity(queryNumberIntent);
			}else{
				//如果不存在则从服务器下载文件public DownloadFileTask(String serverFilePath , String localFilePath , ProgressDialog pd){
				downDbFromServer();
			}
			break;
		case R.id.tv_atools_locationShow:
			Log.i(TAG, "location show style ");
			AlertDialog.Builder builder = new Builder(this);
			builder.setTitle("归属地显示风格");
			String [] items = new String[]{"半透明","活力橙","苹果绿"};
			builder.setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					Editor editor = sp.edit();
					editor.putInt("atools_location_background", which);
					editor.commit();
				}
			});
			
			builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
				
				}
			});
			builder.create().show();
			break;
		case R.id.tv_atools_locationPosition:
			Intent dragViewIntent = new Intent(AtoolsActivity.this,DragViewActivity.class);
			overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
			startActivity(dragViewIntent);
			break;
			
		case R.id.tv_atools_smsBackup:
			Intent smsBackupIntent = new Intent(this,SmsBackupService.class);
			startService(smsBackupIntent);
			break;
		case R.id.tv_atools_smsRestore:
			final ProgressDialog pd = new ProgressDialog(this);
			pd.setTitle("短信还原");
			pd.setMessage("还原中...");
			pd.setCancelable(false);
			pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			pd.show();
			final SmsInfoService smsInfoService = new SmsInfoService(this);
			new Thread(){
				public void run() {
					smsInfoService.restoreSmsInfo("/sdcard/smsbackup.xml",pd);
				};
			}.start();
			break;
		case R.id.tv_atools_app_lock:
			Intent applockIntent = new Intent(this,AppLockActivity.class);
			startActivity(applockIntent);
			break;
		case R.id.tv_atools_common_num:
			Intent commonNumIntent = new Intent(this,CommonNumActivity.class);
			startActivity(commonNumIntent);
			break;
		}
		
		
	}
	private void downDbFromServer() {
		String serverFilePath = this.getResources().getString(R.string.phoneNumAddressDbUrl);
		Log.i(TAG, "serverFilePath:"+serverFilePath);
		pd = new ProgressDialog(this);
		pd.setCancelable(false);
		pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pd.setTitle("资源文件下载");
		pd.setMessage("下载中...");
		pd.show();
		final DownloadFileTask downloadFileTask = new  DownloadFileTask(serverFilePath,localFilePath,pd);
		new Thread(){
			public void run() {
				Message message = handler.obtainMessage();
				Bundle data = new Bundle();
				try {
					downloadFileTask.downloadFile();
					data.putBoolean("downloadResult", SUCCESS);
					message.setData(data);
					handler.sendMessage(message);
				} catch (Exception e) {
					data.putBoolean("downloadResult", FAIL);
					File localFile = new File(localFilePath);
					if(localFile.exists()){
						localFile.delete();
					}
					message.setData(data);
					handler.sendMessage(message);
					e.printStackTrace();
				}
			};
		}.start();
	}
	private boolean isResourceExist() {
		File resFile = new File(localFilePath);
		return resFile.exists();
	}
	private void startAddressService() {
		Intent addressServiceIntent = new Intent(AtoolsActivity.this,AddressService.class);
		startService(addressServiceIntent);
	}
}
