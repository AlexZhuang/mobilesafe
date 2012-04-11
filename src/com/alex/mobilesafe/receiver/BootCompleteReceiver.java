package com.alex.mobilesafe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;

public class BootCompleteReceiver extends BroadcastReceiver {
	private static final String TAG = "BootCompleteReceiver";
	private SharedPreferences sp;
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i(TAG, "�ֻ�������");
		sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		boolean isProtecting = sp.getBoolean("isProtecting", false);
		if(isProtecting){
			Log.i(TAG, "���SIM���Ƿ����...");
			TelephonyManager manager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
			String currentSimNum = manager.getSimSerialNumber();
			String saveSimNum = sp.getString("simserial", null);
			if(!currentSimNum.equals(saveSimNum)){
				Log.i(TAG, "���ͱ�������...");
				SmsManager smsManager = SmsManager.getDefault();
				String destinationAddress =	sp.getString("safeNum", "");
				smsManager.sendTextMessage(destinationAddress, null, "sim�������˸ı�,�ֻ����ܱ���", null, null);
			}
		}
	}

}
