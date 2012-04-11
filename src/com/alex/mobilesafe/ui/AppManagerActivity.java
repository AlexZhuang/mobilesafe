package com.alex.mobilesafe.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.alex.mobilesafe.R;
import com.alex.mobilesafe.domain.AppInfo;
import com.alex.mobilesafe.engine.AppInfoProvider;
import com.alex.moiblesafe.adapter.AppListAdapter;

public class AppManagerActivity extends Activity implements OnClickListener {
	private static final String TAG = "AppManagerActivity";
	private LinearLayout ll_app_manager_loading;
	private ListView lv_app_manager;
	private static final int GET_ALL_APP_FINISH = 1;
	private static final int START_UNINSTALL_REQUEST_CODE = 0;
	private static final String TITLE_ALL_APPS = "所有程序";
	private static final String TITLE_USER_APPS = "用户程序";
	private List<AppInfo> appInfos;
	private AppListAdapter adapter;
	private PopupWindow localPopupWindow;
	private TextView tv_app_manager_title;
	private boolean isLoading = false;
	private List<AppInfo> currentViewAppInfos = null ;
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case GET_ALL_APP_FINISH:
				// TODO: 把数据设置给listview的数组适配器
				if(TITLE_ALL_APPS.equals(tv_app_manager_title.getText().toString())){
					adapter = new AppListAdapter(AppManagerActivity.this, appInfos);
					currentViewAppInfos= appInfos;
				}else{
					List<AppInfo> userAppInfos = getUserAppInfos();
					adapter = new AppListAdapter(AppManagerActivity.this, userAppInfos);
					currentViewAppInfos= userAppInfos;
				}
				lv_app_manager.setAdapter(adapter);
				ll_app_manager_loading.setVisibility(View.INVISIBLE);
				isLoading=false;
				break;
			}
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.app_manager);
		ll_app_manager_loading = (LinearLayout) findViewById(R.id.ll_app_manager_loading);
		
		lv_app_manager = (ListView) findViewById(R.id.lv_app_manager);
		initUIData();
		lv_app_manager.setOnItemClickListener(new MyOnItemClickListener());
		lv_app_manager.setOnTouchListener(new MyOnTouchListener());
		lv_app_manager.setOnScrollListener(new MyOnScrollListener());
		
		tv_app_manager_title = (TextView) findViewById(R.id.tv_app_manager_title);
		tv_app_manager_title.setOnClickListener(this);
	}

	private void initUIData() {
		isLoading=true;
		ll_app_manager_loading.setVisibility(View.VISIBLE);
		new Thread() {
			public void run() {
				AppInfoProvider provider = new AppInfoProvider(
						AppManagerActivity.this);
				appInfos = provider.getAllAppInfo();
				Message message = Message.obtain();
				message.what = GET_ALL_APP_FINISH;
				handler.sendMessage(message);
			};
		}.start();
	}
	
	private List<AppInfo> getUserAppInfos(){
		List<AppInfo> userAppInfos = new ArrayList<AppInfo>();
		for(AppInfo appInfo : appInfos){
			if(!appInfo.isSystemApp()){
				userAppInfos.add(appInfo);
			}
		}
		return userAppInfos;
	}

	private class MyOnItemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			dismissPopupWin();
			int[] arrayOfInt = new int[2];
			view.getLocationInWindow(arrayOfInt);
			int i = arrayOfInt[0] + 60;
			int j = arrayOfInt[1];

			View popupview = View.inflate(AppManagerActivity.this,
					R.layout.popup_item, null);
			LinearLayout ll_start = (LinearLayout) popupview
					.findViewById(R.id.ll_start);
			LinearLayout ll_uninstall = (LinearLayout) popupview
					.findViewById(R.id.ll_uninstall);
			LinearLayout ll_share = (LinearLayout) popupview
					.findViewById(R.id.ll_share);

			// 把当前条目在listview中的位置设置给view对象
			ll_share.setTag(position);
			ll_uninstall.setTag(position);
			ll_start.setTag(position);

			ll_start.setOnClickListener(AppManagerActivity.this);
			ll_uninstall.setOnClickListener(AppManagerActivity.this);
			ll_share.setOnClickListener(AppManagerActivity.this);

			LinearLayout ll = (LinearLayout) popupview
					.findViewById(R.id.ll_popup);
			ScaleAnimation sa = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f);
			sa.setDuration(200);
			localPopupWindow = new PopupWindow(popupview, 230, 70);
			// 一定要记得给popupwindow设置背景颜色
			// Drawable background = new ColorDrawable(Color.TRANSPARENT);
			Drawable background = getResources().getDrawable(
					R.drawable.local_popup_bg);
			localPopupWindow.setBackgroundDrawable(background);
			localPopupWindow.showAtLocation(view, Gravity.LEFT | Gravity.TOP,
					i, j);
			ll.startAnimation(sa);
		}

	}

	private class MyOnTouchListener implements OnTouchListener {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			dismissPopupWin();
			return false;
		}

	}

	private class MyOnScrollListener implements OnScrollListener {

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			dismissPopupWin();

		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
			dismissPopupWin();
		}

	}

	// 撤除弹出窗口,保证只有一个popupwindow的实例存在
	private void dismissPopupWin() {
		if (localPopupWindow != null) {
			localPopupWindow.dismiss();
		}
	}

	@Override
	public void onClick(View v) {
		int position=0;
		if(v.getTag()!=null){
			position = (Integer) v.getTag();
		}
		Log.i(TAG, position + "");
		AppInfo appInfo =null;
		String appName = null;
		String packageName = null;
		if(!isLoading){
			appInfo = currentViewAppInfos.get(position);
			appName = appInfo.getAppName();
			packageName = appInfo.getPackageName();
		}
		
		try {
			switch (v.getId()) {
			case R.id.tv_app_manager_title:
				if(!isLoading){
					if(TITLE_ALL_APPS.equals(tv_app_manager_title.getText().toString())){
						tv_app_manager_title.setText(TITLE_USER_APPS);
						List<AppInfo> userAppInfos = getUserAppInfos();
						adapter.setAppInfos(userAppInfos);
						adapter.notifyDataSetChanged();
						currentViewAppInfos=userAppInfos;
					}else{
						tv_app_manager_title.setText(TITLE_ALL_APPS);
						adapter.setAppInfos(appInfos);
						adapter.notifyDataSetChanged();
						currentViewAppInfos=appInfos;
					}
				}
				
				break;
			
			case R.id.ll_start:// 启动选中的应用程序
				Log.i(TAG, "start the application:" + appName);
//				PackageInfo packageInfo = getPackageManager().getPackageInfo(
//						packageName, PackageManager.GET_ACTIVITIES);
//				ActivityInfo[] activityInfos = packageInfo.activities;
//				
//				
//				ActivityInfo startActivityInfo = activityInfos[0];
//				String className = startActivityInfo.name;
//				Log.i(TAG, "packageName:" + packageName + ";className:"
//						+ className);
//				Intent startIntent = new Intent();
//				startIntent.setClassName(packageName, className);
//				startActivity(startIntent);
				
				Intent intent= getPackageManager().getLaunchIntentForPackage(packageName);
				if(intent!=null){
					startActivity(intent);
				} else{
					Toast.makeText(this, "此应用无法启动", 0).show();
				}
				dismissPopupWin();
				break;
			case R.id.ll_uninstall:// 卸载选中的应用程序

				if (appInfo.isSystemApp()) {
					// 系统应用不能删除
					dismissPopupWin();
					Toast.makeText(getApplicationContext(),
							"can not uninstall the system application", 1)
							.show();
				} else {
					Log.i(TAG, "uninstall the application:" + packageName);
					Uri uri = Uri.parse("package:" + packageName);
					Intent deleteIntent = new Intent();
					deleteIntent.setType(Intent.ACTION_DELETE);
					deleteIntent.setData(uri);
					startActivityForResult(deleteIntent,
							START_UNINSTALL_REQUEST_CODE);
				}
				dismissPopupWin();
				break;
			case R.id.ll_share:// 分享选中的应用程序
				Log.i(TAG, "share the application:" + packageName);
				Intent shareIntent = new Intent();
				shareIntent.setAction(Intent.ACTION_SEND);
				shareIntent.setType("text/plain");
				// 需要指定意图的数据类型
				shareIntent.putExtra(Intent.EXTRA_SUBJECT, "分享");
				shareIntent.putExtra(Intent.EXTRA_TEXT,
						"推荐你使用一个程序" + appInfo.getAppName());
				shareIntent = Intent.createChooser(shareIntent, "分享");
				startActivity(shareIntent);
				dismissPopupWin();
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(getApplicationContext(), "应用程序异常", 1).show();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (localPopupWindow != null) {
			localPopupWindow.dismiss();
			localPopupWindow = null;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		dismissPopupWin();
		if (requestCode == START_UNINSTALL_REQUEST_CODE) {
			initUIData();
		}
	}

}
