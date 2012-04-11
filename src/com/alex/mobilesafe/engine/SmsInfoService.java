package com.alex.mobilesafe.engine;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.alex.mobilesafe.domain.SmsInfo;

public class SmsInfoService {
	
	private static final String TAG = "SmsInfoService";
	private Context context;

	public SmsInfoService(Context context) {
		super();
		this.context = context;
	}

	public List<SmsInfo> getAllSmsInfo() {
		List<SmsInfo> smsInfos = new ArrayList<SmsInfo>();
		ContentResolver smsResolver = context.getContentResolver();
		Uri smsUri = Uri.parse("content://sms/inbox");
		Cursor cursor = smsResolver.query(smsUri, new String[] { "_id",
				"address", "date", "type", "body" }, null, null, "date desc");
		while (cursor.moveToNext()) {
			String id = cursor.getString(0);
			String address = cursor.getString(1);
			String date = cursor.getString(2);
			int type = cursor.getInt(3);
			String body = cursor.getString(4);
			SmsInfo smsInfo = new SmsInfo(id, address, date, type, body);
			smsInfos.add(smsInfo);
		}
		cursor.close();
		return smsInfos;
	}

	/**
	 * 还原所有短信
	 * 
	 * @param path
	 *            备份文件路径
	 */
	public void restoreSmsInfo(String path,ProgressDialog pd) {
		try {
			ContentResolver resolver = context.getContentResolver();
			Uri uri = Uri.parse("content://sms/inbox");
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			XmlPullParser parser = factory.newPullParser();
			File file = new File(path);
			FileInputStream fis = new FileInputStream(file);
			parser.setInput(fis, "utf-8");
			int eventType = parser.getEventType();
			ContentValues contentValues = null;
			int valueCount=0;
			while (eventType != XmlPullParser.END_DOCUMENT) {
				switch (eventType) {
				case XmlPullParser.START_TAG:
					if("count".equals(parser.getName())){
						int count = Integer.parseInt(parser.nextText());
						pd.setMax(count);
					}else if ("sms".equals(parser.getName())) {
						contentValues = new ContentValues();
					} else if ("address".equals(parser.getName())) {
						String address = parser.nextText();
						Log.i(TAG, address);
						contentValues.put("address", address);
					} else if ("date".equals(parser.getName())) {
						String date = parser.nextText();
						Log.i(TAG, date);
						contentValues.put("date", date);
					} else if ("type".equals(parser.getName())) {
						String type = parser.nextText();
						Log.i(TAG, type);
						contentValues.put("type", type);
					} else if ("body".equals(parser.getName())) {
						String body = parser.nextText();
						Log.i(TAG, body);
						contentValues.put("body", body);
					}
					break;
				case XmlPullParser.END_TAG:
					if ("sms".equals(parser.getName())) {
						resolver.insert(uri, contentValues);
						pd.setProgress(++valueCount);
						contentValues = null;
					}
					break;
				}
				eventType = parser.next();
			}
			fis.close();
			pd.dismiss();
			Looper.prepare();
			Toast.makeText(context.getApplicationContext(), "还原成功", 1).show();
			Looper.loop();
		} catch (Exception e) {
			pd.dismiss();
			Looper.prepare();
			Toast.makeText(context.getApplicationContext(), "还原失败", 1).show();
			Looper.loop();
			e.printStackTrace();
		}
	}

}
