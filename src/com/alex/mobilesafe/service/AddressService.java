package com.alex.mobilesafe.service;

import java.lang.reflect.Method;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.provider.CallLog;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alex.mobilesafe.R;
import com.alex.mobilesafe.db.dao.BlackNumberDao;
import com.alex.mobilesafe.engine.NumberAddressService;
import com.alex.mobilesafe.ui.CallSmsActivity;
import com.android.internal.telephony.ITelephony;

public class AddressService extends Service {
	private static final String TAG = "AddressService";
	
	private WindowManager windowManager ;
	private View view;
	private SharedPreferences sp;
	TelephonyManager manager = null;
	MyPhoneStateListener phoneStateListener;
	private long firstRingTime ;
	private long endRingTime;
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	 
	@Override
	public void onCreate() {
		Log.i(TAG, "create AddressService...");
		manager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		phoneStateListener= new MyPhoneStateListener();
		manager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
		windowManager = (WindowManager) this.getSystemService(WINDOW_SERVICE);
		sp = getSharedPreferences("config", Context.MODE_PRIVATE);
	}
	
	private class MyPhoneStateListener extends PhoneStateListener{
		
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			switch (state) {
			case TelephonyManager.CALL_STATE_IDLE:
				endRingTime = System.currentTimeMillis();
				long  calltime = endRingTime-firstRingTime;
				Log.i(TAG,"calltime ="+calltime);
				if(firstRingTime<endRingTime && calltime<5000 && calltime >0){
					Log.i(TAG,"响一声的电话");
					endRingTime = 0;
					firstRingTime = 0;
					// 弹出来notification 通知用户这是一个骚扰电话
					showNotification(incomingNumber);
				}
				
				if(view!=null){
					windowManager.removeView(view);
					view=null;
				}
				break;

			case TelephonyManager.CALL_STATE_RINGING:
				Log.i(TAG, "ringing ...");
				firstRingTime = System.currentTimeMillis();
				BlackNumberDao blackNumDao = new BlackNumberDao(AddressService.this);
				if(blackNumDao.find(incomingNumber)){
					Log.i(TAG, "end the call ...");
					endCall(incomingNumber);
				}else{
					showLocation(incomingNumber);
				}
				break;
			}
		}

		private void endCall(String incomingNumber) {
			try {
				Method method = Class.forName("android.os.ServiceManager").getMethod("getService", String.class);
				IBinder binder = (IBinder)method.invoke(null, new Object[]{TELEPHONY_SERVICE});
				ITelephony telephony = ITelephony.Stub.asInterface(binder);
				telephony.endCall();
				//注册一个内容观察者 观察call_log的uri的信息 
				getContentResolver().registerContentObserver(CallLog.Calls.CONTENT_URI, true, new MyObserver(new Handler(),incomingNumber));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 弹出notification 通知用户添加黑名单号码
	 * @param incomingNumber
	 */
	public void showNotification(String incomingNumber) {
		//1. 获取notification的管理服务
		NotificationManager  manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		//2 把一个要想显示的notification 对象创建出来
		int icon =R.drawable.notification;
		CharSequence tickerText = "发现响一声号码";
		long when = System.currentTimeMillis();

		Notification notification = new Notification(icon, tickerText, when);
		// 3 .配置notification的一些参数
		Context context = getApplicationContext();
		CharSequence contentTitle = "响一声号码";
		CharSequence contentText = incomingNumber;
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		
		Intent notificationIntent = new Intent(this, CallSmsActivity.class);
		// 把响一声的号码 设置到intent对象里面
		notificationIntent.putExtra("number", incomingNumber);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

		notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
		
		// 4. 通过manger把notification 激活
		manager.notify(0, notification);
	}
	
	
	private class MyObserver extends ContentObserver
	{
		private String incomingnumber;
		public MyObserver(Handler handler,String incomingnumber) {
			super(handler);
			this.incomingnumber = incomingnumber;
		}

		@Override
		public void onChange(boolean selfChange) {
			super.onChange(selfChange);
			deleteCallLog(incomingnumber);
			
			//当删除了呼叫记录后 反注册内容观察者
			getContentResolver().unregisterContentObserver(this);
		}
		
	}
	
	public void showLocation(String phoneNum){
		WindowManager.LayoutParams params = new LayoutParams();
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        params.format = PixelFormat.TRANSLUCENT;
        params.type = WindowManager.LayoutParams.TYPE_TOAST;
        
        params.gravity = Gravity.LEFT | Gravity.TOP;

        params.x = sp.getInt("lastx", 0);
        params.y = sp.getInt("lasty", 0);
        
        view = View.inflate(getApplicationContext(), R.layout.show_location, null);
        LinearLayout ll = (LinearLayout) view.findViewById(R.id.ll_location);
        
        
        
        int backgroundid = sp.getInt("atools_location_background", 0);
        if(backgroundid==0){
        	ll.setBackgroundResource(R.drawable.call_locate_gray);
        }else if(backgroundid==1){
        	ll.setBackgroundResource(R.drawable.call_locate_orange);
        }else {
        	ll.setBackgroundResource(R.drawable.call_locate_green);
        }
        
        TextView tv = (TextView) view.findViewById(R.id.tv_location);
        tv.setTextSize(24);
        NumberAddressService numberAddressService = new NumberAddressService(this);
        String city = numberAddressService.getAddress(phoneNum);
        
        tv.setText(city);
        windowManager.addView(view , params);
        
	}
	
	
	/**
	 * 根据电话号码删除呼叫记录
	 * @param incomingNumber 要删除呼叫记录的号码
	 */
	public void deleteCallLog(String incomingNumber) {
		ContentResolver  resolver = getContentResolver();
		Cursor cursor = resolver.query(CallLog.Calls.CONTENT_URI, null, "number=?", new String[]{incomingNumber}, null);
		if(cursor.moveToFirst()){//查询到了呼叫记录
			String id =cursor.getString(cursor.getColumnIndex("_id"));
			resolver.delete(CallLog.Calls.CONTENT_URI, "_id=?",new String[]{id});
		}
	}
	

	@Override
	public void onDestroy() {
		manager.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
		phoneStateListener = null;
	}

}
