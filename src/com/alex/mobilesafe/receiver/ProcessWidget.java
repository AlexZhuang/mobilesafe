package com.alex.mobilesafe.receiver;

import com.alex.mobilesafe.service.UpdateWidgetService;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.sax.StartElementListener;

public class ProcessWidget extends AppWidgetProvider {
	
	Intent intent;
	
	
	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		super.onDeleted(context, appWidgetIds);
		intent = new Intent(context,UpdateWidgetService.class);
		context.stopService(intent);
	}

	@Override
	public void onEnabled(Context context) {
		super.onEnabled(context);
		intent = new Intent(context,UpdateWidgetService.class);
		context.startService(intent);
	}

}
