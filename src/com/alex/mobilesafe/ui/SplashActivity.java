package com.alex.mobilesafe.ui;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.alex.mobilesafe.R;
import com.alex.mobilesafe.domain.UpdateInfo;
import com.alex.mobilesafe.engine.DownloadFileTask;
import com.alex.mobilesafe.engine.UpdateInfoService;

public class SplashActivity extends Activity {
	private static final String TAG = "SplashActivity";
	private LinearLayout ll_splash_main;
	private UpdateInfo updateInfo = null;
	private ProgressDialog pd;
	private Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			if(isNeedUpdate()){
				showUpdateDialog();
			}
		}
		
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 取消标题栏
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.splash);
		// 完成窗体的全屏显示 // 取消掉状态栏
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		ll_splash_main = (LinearLayout) findViewById(R.id.ll_splash_main);
		AlphaAnimation aa = new AlphaAnimation(0.0f, 1.0f);
		aa.setDuration(2000);
		ll_splash_main.startAnimation(aa);
		pd= new ProgressDialog(this);
		pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pd.setMessage("downloading...");
	
		
		new Thread(){
			@Override
			public void run() {
				try {
					Thread.sleep(2000);
					handler.sendEmptyMessage(0);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
		}.start();
	
	
	}
	/**
	 * 通过比较当前版本号和服务器版本号，判断是否需要进行更新
	 * @return 是否进行更新的标志
	 */
	private boolean isNeedUpdate(){
		//从服务器获取最新的软件版本号
		UpdateInfoService infoService = new UpdateInfoService();
		String serverUrl = this.getResources().getString(R.string.serverUrl);
		
		String serverVersion = null;
		try {
			Log.i(TAG, "start to get the server version");
			updateInfo = infoService.getUpdateInfo(serverUrl);
			serverVersion = updateInfo.getVersion();
			if(serverVersion!=null && serverVersion.equals(getVersion())){
				loadMainUI();
				return false;
			}else{
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.i(TAG, "fail to get the version number , enter the main activity directly");
			loadMainUI();
			return false;
		}
	}
	
	private void showUpdateDialog(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this); 
		builder.setTitle("升级提醒");
		builder.setIcon(R.drawable.icon5);
		builder.setMessage(updateInfo.getDescription());
		builder.setCancelable(false);
		builder.setPositiveButton("升级", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Log.i(TAG, "download the newest software");
				pd.show();
				new Thread( new DownloadFileRunnable()).start();
			}
		});
		builder.setNegativeButton("取消",  new OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Log.i(TAG, "cancel update software");
				loadMainUI();
			}
			
		});
		builder.create().show();
	}
	
	private class DownloadFileRunnable implements Runnable{

		@Override
		public void run() {		
			String serverPath = updateInfo.getApkurl();
			String localPath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/mobilesafe.apk";
	
			DownloadFileTask task = new DownloadFileTask(serverPath,localPath,pd);
			try {
				File downloadFile = task.downloadFile();
				Log.i(TAG, "download file finish");
				pd.dismiss();
				Log.i(TAG, "start to install file ");
				install(downloadFile);
			} catch (Exception e) {
				e.printStackTrace();
				Log.i(TAG, "download file failed");
				pd.dismiss();
				Toast.makeText(getApplicationContext(), "download file failed", 1).show();
				
			}
		}
		
	}
	
	/**
	 * 进入主界面
	 */
	private void loadMainUI() {
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
		finish(); // 把当前activity从任务栈里面移除

	}

	/**
	 * 获得当前应用程序的版本号
	 * 
	 * @return 版本号
	 */
	public String getVersion() {
		PackageManager manager = getApplicationContext().getPackageManager();
		String versionName = null;
		try {
			PackageInfo info = manager.getPackageInfo(getPackageName(), 0);
			versionName = info.versionName;
			return versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return "unknown version name";
		}
	}
	
	/**
	 * 安装apk
	 * @param file
	 */
	private void install(File file){
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
		finish();
		startActivity(intent);
	}

}
