package com.alex.mobilesafe.service;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import org.xmlpull.v1.XmlSerializer;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Looper;
import android.util.Xml;
import android.widget.Toast;

import com.alex.mobilesafe.domain.SmsInfo;
import com.alex.mobilesafe.engine.SmsInfoService;

public class SmsBackupService extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		new Thread() {
			@Override
			public void run() {
				SmsInfoService smsInfoService = new SmsInfoService(
						SmsBackupService.this);
				List<SmsInfo> smsInfos = smsInfoService.getAllSmsInfo();
				XmlSerializer serializer = Xml.newSerializer();
				try {
					File file = new File("/sdcard/smsbackup.xml");
					FileOutputStream fos = new FileOutputStream(file);
					serializer.setOutput(fos, "utf-8");
					serializer.startDocument("utf-8", true);
					serializer.startTag(null, "smss");
					serializer.startTag(null, "count");
					serializer.text(smsInfos.size()+"");
					serializer.endTag(null, "count");
					for (SmsInfo smsInfo : smsInfos) {
						serializer.startTag(null, "sms");
						
						serializer.startTag(null, "id");
						serializer.text(smsInfo.getId());
						serializer.endTag(null, "id");

						serializer.startTag(null, "address");
						serializer.text(smsInfo.getAddress());
						serializer.endTag(null, "address");

						serializer.startTag(null, "date");
						serializer.text(smsInfo.getDate());
						serializer.endTag(null, "date");

						serializer.startTag(null, "type");
						serializer.text(smsInfo.getType() + "");
						serializer.endTag(null, "type");

						serializer.startTag(null, "body");
						serializer.text(smsInfo.getBody() + "");
						serializer.endTag(null, "body");

						serializer.endTag(null, "sms");
					}
					serializer.endTag(null, "smss");
					serializer.endDocument();
					fos.flush();
					fos.close();
					Looper.prepare();
					Toast.makeText(getApplicationContext(), "备份完成", 1).show();
					Looper.loop();
				} catch (Exception e) {
					e.printStackTrace();
					Looper.prepare();
					Toast.makeText(getApplicationContext(), "备份失败", 1).show();
					Looper.loop();
				}

			}
		}.start();
	}
}
