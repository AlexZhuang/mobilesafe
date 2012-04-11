package com.alex.mobilesafe.receiver;

import com.alex.mobilesafe.R;
import com.alex.mobilesafe.db.dao.BlackNumberDao;
import com.alex.mobilesafe.engine.GPSInfoProvider;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;

public class SMSReceiver extends BroadcastReceiver {

	private static final String TAG = "SMSReceiver";
	
	private BlackNumberDao blackNumberDao ;

	@Override
	public void onReceive(Context context, Intent intent) {
		// 获取短信的内容
		// #*location*#123456
		
		blackNumberDao = new BlackNumberDao(context);
		
		Object[] pdus = (Object[]) intent.getExtras().get("pdus");
		for (Object pdu : pdus) {
			SmsMessage sms = SmsMessage.createFromPdu((byte[]) pdu);
			String content = sms.getMessageBody();
			Log.i(TAG, "短信内容" + content);
			String sender = sms.getOriginatingAddress();
			if ("#*location*#".equals(content)) {
				// 终止广播
				abortBroadcast();
				GPSInfoProvider provider = GPSInfoProvider.getInstance(context);
				String location = provider.getLocation();
				SmsManager smsmanager = SmsManager.getDefault();
				if ("".equals(location)) {

				} else {
					Log.i(TAG, "接收者：" + sender+";定位信息："+location);
					smsmanager.sendTextMessage(sender, null, location, null,null);
				}
			}else if("#*locknow*#".equals(content)){
				DevicePolicyManager manager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
				manager.resetPassword("123", 0);
				manager.lockNow();
				abortBroadcast();
			}else if("#*wipedata*#".equals(content)){
				DevicePolicyManager manager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
				manager.wipeData(0);
				abortBroadcast();
			}else if("#*alarm*#".equals(content)){
				MediaPlayer player = MediaPlayer.create(context, R.raw.ylzs);
				player.setVolume(1.0f, 1.0f);
				player.start();
				abortBroadcast();
			}
			
			//拦截黑名单内的短信
			if(blackNumberDao.find(sender)){
				abortBroadcast();
			}
			
			//建立短信内容的匹配库 (关键字: 发票,卖房,哥,学生....办证...)
			if(content.contains("fapiao")){
				Log.i(TAG,"垃圾短信 发票");
				abortBroadcast();
			}
		}

	}

}
