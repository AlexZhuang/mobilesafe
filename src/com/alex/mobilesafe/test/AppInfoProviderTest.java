package com.alex.mobilesafe.test;

import java.util.List;

import android.test.AndroidTestCase;

import com.alex.mobilesafe.domain.AppInfo;
import com.alex.mobilesafe.engine.AppInfoProvider;

public class AppInfoProviderTest extends AndroidTestCase {
	public void testGetAllAppInfo(){
		
		AppInfoProvider provider = new AppInfoProvider(getContext());
		List<AppInfo> appInfos = provider.getAllAppInfo();
		for(AppInfo info:appInfos){
			System.out.println(info);
		}
	}
}
