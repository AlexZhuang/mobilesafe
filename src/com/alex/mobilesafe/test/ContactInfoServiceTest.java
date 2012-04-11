package com.alex.mobilesafe.test;

import android.test.AndroidTestCase;

import com.alex.mobilesafe.engine.ContactInfoService;

public class ContactInfoServiceTest extends AndroidTestCase{
	public void testGetContacts(){
		ContactInfoService service = new ContactInfoService(getContext());
		System.out.println(service.getContacts());
	}
}
