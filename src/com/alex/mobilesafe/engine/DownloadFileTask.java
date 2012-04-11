package com.alex.mobilesafe.engine;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.ProgressDialog;

public class DownloadFileTask {
	private String serverFilePath ;
	private String localFilePath ;
	private ProgressDialog pd ;
	/**
	 * 
	 * @param serverFilePath 服务器文件路径
	 * @param localFilePath 下载文件保存路径
	 * @param pd 下载进度条
	 */
	public DownloadFileTask(String serverFilePath , String localFilePath , ProgressDialog pd){
		this.serverFilePath= serverFilePath;
		this.localFilePath = localFilePath;
		this.pd=pd;
	}
	
	/**
	 * 从服务器下载文件
	 * @return 下载完成后的本地文件
	 * @throws Exception
	 */
	public File downloadFile() throws Exception{
		File localFile = new File(localFilePath);
		FileOutputStream fos = new FileOutputStream(localFile);
		URL url = new URL(serverFilePath);
		HttpURLConnection conn = (HttpURLConnection)url.openConnection();
		conn.setConnectTimeout(5000);
		conn.setRequestMethod("GET");
		if(conn.getResponseCode()==200){
			int max =  conn.getContentLength();
			pd.setMax(max);
			System.out.println("Max:"+localFile.length());
			InputStream is = conn.getInputStream();
			byte[] buf = new byte[1024];
			int len = 0 ;
			int total = 0;
			while((len=is.read(buf))!=-1){
				fos.write(buf, 0, len);
				total = total +len ;
				pd.setProgress(total);
			}
			fos.flush();
			fos.close();
			is.close();
		}
		return localFile;
		
	}
}
