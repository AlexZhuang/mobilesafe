package com.alex.mobilesafe.engine;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import com.alex.mobilesafe.domain.UpdateInfo;

public class UpdateInfoService {
	/**
	 * 
	 * @param urlStr XML文件路径
	 * @return UpdateInfo对象
	 * @throws Exception
	 */
	public UpdateInfo getUpdateInfo(String urlStr) throws Exception{
		URL url = new URL(urlStr);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(2000);
		conn.setRequestMethod("GET");
		UpdateInfo info = null;
		InputStream is = conn.getInputStream();
		info = UpdateInfoParser.getUpdateInfo(is);
		return info;
		
	}
}
