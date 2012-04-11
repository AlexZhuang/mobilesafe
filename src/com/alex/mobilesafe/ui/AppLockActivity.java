package com.alex.mobilesafe.ui;

import java.util.List;

import android.app.Activity;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.alex.mobilesafe.R;
import com.alex.mobilesafe.db.dao.AppLockDao;
import com.alex.mobilesafe.domain.AppInfo;
import com.alex.mobilesafe.engine.AppInfoProvider;
import com.alex.moiblesafe.adapter.AppListAdapter;

public class AppLockActivity extends Activity {
	private static final String TAG = "AppLockActivity";
	private LinearLayout ll_app_manager_loading;
	private AppInfoProvider provider ;
	private List<AppInfo> appinfos ;
	private ListView lv_app_lock;
	private AppListAdapter appListAdapter;
	private List<String> lockappinfos ;
	private AppLockDao appLockDao ;
	private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			lv_app_lock.setAdapter(new AppLockAdapter());
			ll_app_manager_loading.setVisibility(View.INVISIBLE);
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.app_lock);
		ll_app_manager_loading = (LinearLayout) this.findViewById(R.id.ll_app_manager_loading);
		lv_app_lock=(ListView)findViewById(R.id.lv_app_lock);
		provider = new AppInfoProvider(this);
		initUI();
		appLockDao = new AppLockDao(this);
		lockappinfos = appLockDao.getAllApps();
		lv_app_lock.setOnItemClickListener(new MyOnItemClickListener());
	}
	
	private class MyOnItemClickListener implements OnItemClickListener{

		

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			Log.i(TAG, "item click");
	        Animation a = new TranslateAnimation(
					Animation.RELATIVE_TO_SELF, 0.0f,
					Animation.RELATIVE_TO_SELF, 0.5f,
					Animation.RELATIVE_TO_SELF, 0.0f,
					Animation.RELATIVE_TO_SELF, 0.0f);
	        a.setDuration(500);
	        view.startAnimation(a);
	        ImageView iv = (ImageView) view.findViewById(R.id.iv_app_lock_status);
			
			// 传递当前要锁定程序的包名
			AppInfo info = (AppInfo) lv_app_lock.getItemAtPosition(position);
			String packname = info.getPackageName();
			if(appLockDao.find(packname)){
				// 移除这个条目
				//appLockDao.delete(packname);
				getContentResolver().delete(Uri.parse("content://com.alex.applockprovider/delete"), null, new String[]{packname});
				lockappinfos.remove(packname);
				iv.setImageResource(R.drawable.unlock);
			}else{
				//appLockDao.add(packname);
				lockappinfos.add(packname);
				ContentValues values = new ContentValues();
				values.put("packname", packname);
				getContentResolver().insert(Uri.parse("content://com.alex.applockprovider/insert"), values);
				iv.setImageResource(R.drawable.lock);
			}
		}
		
	}
	
	private void initUI() {
		ll_app_manager_loading.setVisibility(View.VISIBLE);
		new Thread() {
			@Override
			public void run() {
				appinfos = provider.getAllAppInfo();
				handler.sendEmptyMessage(0);
			}
		}.start();
	}
	
	private class AppLockAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			
			return appinfos.size();
		}

		@Override
		public Object getItem(int position) {
			return appinfos.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = null;
			if(convertView==null){
				view = View.inflate(AppLockActivity.this, R.layout.lock_app_item, null);
			}else{
				view = convertView;
			}
			AppInfo info = appinfos.get(position);
			ImageView iv = (ImageView) view.findViewById(R.id.iv_app_icon);
			TextView tv = (TextView) view.findViewById(R.id.tv_app_name);
			ImageView iv_lock_status = (ImageView) view.findViewById(R.id.iv_app_lock_status);
			TextView tv_pack_name =  (TextView) view.findViewById(R.id.tv_app_packname);
			tv_pack_name.setText(info.getPackageName());
			if(lockappinfos.contains(info.getPackageName())){
				iv_lock_status.setImageResource(R.drawable.lock);
			}else{
				iv_lock_status.setImageResource(R.drawable.unlock);
			}
			iv.setImageDrawable(info.getIcon());
			tv.setText(info.getAppName());
			return view;
		}
		
	}
}
