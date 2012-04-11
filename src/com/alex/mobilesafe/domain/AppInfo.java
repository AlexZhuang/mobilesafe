package com.alex.mobilesafe.domain;

import android.graphics.drawable.Drawable;

public class AppInfo {
	private String appName;
	private String packageName;
	private Drawable icon;
	private boolean isSystemApp;
	public String getAppName() {
		return appName;
	}
	public void setAppName(String appName) {
		this.appName = appName;
	}
	public String getPackageName() {
		return packageName;
	}
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	public Drawable getIcon() {
		return icon;
	}
	public void setIcon(Drawable icon) {
		this.icon = icon;
	}
	public boolean isSystemApp() {
		return isSystemApp;
	}
	public void setSystemApp(boolean isSystemApp) {
		this.isSystemApp = isSystemApp;
	}
	@Override
	public String toString() {
		return "AppInfo [appName=" + appName + ", packageName=" + packageName
				+ ", icon=" + icon + ", isSystemApp=" + isSystemApp + "]";
	}
	
	

}
