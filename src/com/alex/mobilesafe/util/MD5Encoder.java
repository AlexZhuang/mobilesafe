package com.alex.mobilesafe.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Encoder {
	public static String encode(String source){
		String result = null;
		try {
			MessageDigest md = MessageDigest.getInstance("md5");
			byte[] output = md.digest(source.getBytes());
			StringBuffer sb = new StringBuffer();
			for(int i=0;i<output.length;i++){
				String s = Integer.toHexString(0xff&output[i]);
				if(s.length()==1){
					sb.append("0"+s);
				}else{
					sb.append(s);
				}
			}
			result= sb.toString();
			
		} catch (NoSuchAlgorithmException e) {
			//since the exception won't never happen , we can process the exception here .
			//In normal case , we should always throw the exception .
			e.printStackTrace();
		}
		return result;
	}
}
