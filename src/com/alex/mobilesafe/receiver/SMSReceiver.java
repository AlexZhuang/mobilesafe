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
		// ��ȡ���ŵ�����
		// #*location*#123456
		
		blackNumberDao = new BlackNumberDao(context);
		
		Object[] pdus = (Object[]) intent.getExtras().get("pdus");
		for (Object pdu : pdus) {
			SmsMessage sms = SmsMessage.createFromPdu((byte[]) pdu);
			String content = sms.getMessageBody();
			Log.i(TAG, "��������" + content);
			String sender = sms.getOriginatingAddress();
			if ("#*location*#".equals(content)) {
				// ��ֹ�㲥
				abortBroadcast();
				GPSInfoProvider provider = GPSInfoProvider.getInstance(context);
				String location = provider.getLocation();
				SmsManager smsmanager = SmsManager.getDefault();
				if ("".equals(location)) {

				} else {
					Log.i(TAG, "�����ߣ�" + sender+";��λ��Ϣ��"+location);
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
			
			//���غ������ڵĶ���
			if(blackNumberDao.find(sender)){
				abortBroadcast();
			}
			
			//�����������ݵ�ƥ��� (�ؼ���: ��Ʊ,����,��,ѧ��....��֤...)
			if(content.contains("fapiao")){
				Log.i(TAG,"�������� ��Ʊ");
				abortBroadcast();
			}
		}

	}

}
