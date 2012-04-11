package com.alex.mobilesafe.domain;

public class ContactInfo {
	private String name;
	private String phoneNum ;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPhoneNum() {
		return phoneNum;
	}
	public void setPhoneNum(String phoneNum) {
		this.phoneNum = phoneNum;
	}
	@Override
	public String toString() {
		return "ContactInfo [name=" + name + ", phoneNum=" + phoneNum + "]";
	}
	
	

}
