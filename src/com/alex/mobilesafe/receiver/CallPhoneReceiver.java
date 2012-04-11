package com.alex.mobilesafe.receiver;

import com.alex.mobilesafe.ui.LostProtectedActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.sax.StartElementListener;
import android.util.Log;

public class CallPhoneReceiver extends BroadcastReceiver {
	
	private static final String TAG = "CallPhoneReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		String data = getResultData();
		Log.i(TAG, data);
		if("20122012".equals(data)){
			Intent lostProtectIntent = new Intent(context,LostProtectedActivity.class);
			//If set, this activity will become the start of a new task on this history stack.
			//指定要激活的Activity在自己新建的任务栈里面运行
			lostProtectIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(lostProtectIntent);
			setResultData(null);
		}
		
	}



}
