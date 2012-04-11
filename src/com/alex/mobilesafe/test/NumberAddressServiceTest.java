package com.alex.mobilesafe.test;

import android.test.AndroidTestCase;

import com.alex.mobilesafe.engine.NumberAddressService;

public class NumberAddressServiceTest extends AndroidTestCase {
	public void testGetAddress(){
		NumberAddressService service = new NumberAddressService(getContext());
		String city = service.getAddress("15914308619");
		System.out.println(city);
	}
	
	
}
