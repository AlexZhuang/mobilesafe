package com.alex.mobilesafe.test;

import com.alex.mobilesafe.util.MD5Encoder;

import android.test.AndroidTestCase;

public class MD5EncoderTest extends AndroidTestCase {
	public void testEncode(){
		String result = MD5Encoder.encode("111");
		System.out.println("result:"+result);
	}
}
