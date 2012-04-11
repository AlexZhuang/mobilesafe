package com.alex.mobilesafe.service;

import java.util.Timer;
import java.util.TimerTask;

import com.alex.mobilesafe.R;
import com.alex.mobilesafe.receiver.LockScreenReceiver;
import com.alex.mobilesafe.util.TaskUtil;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Color;
import android.os.IBinder;
import android.widget.RemoteViews;

public class UpdateWidgetService extends Service {
	private Timer timer ;
	private TimerTask task;
	private AppWidgetManager  widgetmanager;
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		timer = new Timer();
		widgetmanager =  AppWidgetManager.getInstance(getApplicationContext());
		task = new TimerTask() {
			
			@Override
			public void run() {
				
				// 更新widget的界面
				ComponentName name = new ComponentName("com.alex.mobilesafe", "com.alex.mobilesafe.receiver.ProcessWidget");
				RemoteViews views = new RemoteViews("com.alex.mobilesafe", R.layout.process_widget);
				views.setTextViewText(R.id.process_count, "进程数目"+TaskUtil.getProcessCount(getApplicationContext()));
				views.setTextColor(R.id.process_count, Color.RED);
				views.setTextViewText(R.id.process_memory, "可用内存"+TaskUtil.getMemeorySize(getApplicationContext()));
				views.setTextColor(R.id.process_memory, Color.RED);
				Intent intent = new Intent(UpdateWidgetService.this,LockScreenReceiver.class);
				PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);
				views.setOnClickPendingIntent(R.id.btn_clear, pendingIntent);
				widgetmanager.updateAppWidget(name, views);
				
			}
		};
		timer.scheduleAtFixedRate(task, 1000, 2000);
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		timer.cancel();
		timer = null;
		task =null;
		super.onDestroy();
	}

}
