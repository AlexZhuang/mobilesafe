package com.alex.mobilesafe.test;

import android.test.AndroidTestCase;

import com.alex.mobilesafe.domain.UpdateInfo;
import com.alex.mobilesafe.engine.UpdateInfoService;

public class UpdateInfoServiceTest extends AndroidTestCase{
	
	public void testGetUpdateInfo() throws Exception{
		UpdateInfoService service = new UpdateInfoService();
		UpdateInfo info = service.getUpdateInfo("http://192.168.0.100:8080/web/update.xml");
		System.out.println(info.toString());
	}
}
