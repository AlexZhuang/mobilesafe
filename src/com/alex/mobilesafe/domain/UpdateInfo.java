package com.alex.mobilesafe.domain;

public class UpdateInfo {
	private String version;
	private String description;
	private String apkurl;
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getApkurl() {
		return apkurl;
	}
	public void setApkurl(String apkurl) {
		this.apkurl = apkurl;
	}
	@Override
	public String toString() {
		return "UpdateInfo [version=" + version + ", description="
				+ description + ", apkurl=" + apkurl + "]";
	}
	
	
	
}
