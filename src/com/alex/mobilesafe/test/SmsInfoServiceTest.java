package com.alex.mobilesafe.test;

import java.util.List;

import android.test.AndroidTestCase;

import com.alex.mobilesafe.domain.SmsInfo;
import com.alex.mobilesafe.engine.SmsInfoService;

public class SmsInfoServiceTest extends AndroidTestCase {
	public void testGetAllInfo(){
		SmsInfoService service = new SmsInfoService(getContext());
		List<SmsInfo> infos = service.getAllSmsInfo();
		for(SmsInfo info:infos){
			System.out.println(info);
		}
		
	}
}
