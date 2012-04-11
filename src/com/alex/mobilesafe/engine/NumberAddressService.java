package com.alex.mobilesafe.engine;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.alex.mobilesafe.R;
import com.alex.mobilesafe.db.dao.AddressDao;

public class NumberAddressService {
	
	private static final String TAG = "NumberAddressService";
	private Context context;
	public NumberAddressService(Context context){
		this.context=context;
	}
	
	public String getAddress(String phoneNum){
		String cellphoneRegex = "^1[3458]\\d{9}$";//手机号码匹配字符串
		String city = null;
		SQLiteDatabase db = AddressDao.getDatabase(context.getResources().getString(R.string.phoneNumAddressDbUrl_local));
		if(phoneNum.matches(cellphoneRegex)){
			if(db.isOpen()){
				String moibleprefiex = phoneNum.substring(0, 7);
				Log.i(TAG, "moibleprefiex:"+moibleprefiex);
				Cursor cursor = db.rawQuery("select city from address_tb where _id=(select outkey from numinfo where mobileprefix=?)", new String[]{moibleprefiex});
				if(cursor.moveToFirst()){
					city = cursor.getString(0);
				}
				cursor.close();
			}
			
		}else{
			switch (phoneNum.length()) {
			case 4:
				city="模拟器";
				break;
			case 10:// 3位区号+7位电话号码
				if(db.isOpen()){
					Cursor cursor = db.rawQuery("select city from address_tb where area=?", new String[]{phoneNum.substring(0, 3)});
					if(cursor.moveToFirst()){
						city = cursor.getString(0);
					}
					cursor.close();
				}
				break;
			case 11:// 3位区号+8位电话号码 /4位区号+7位电话号码
				if(db.isOpen()){
					Cursor cursor1 = db.rawQuery("select city from address_tb where area=?", new String[]{phoneNum.substring(0, 3)});
					if(cursor1.moveToFirst()){
						city = cursor1.getString(0);
					}
					cursor1.close();
					if(city==null){
						Cursor cursor2 = db.rawQuery("select city from address_tb where area=?", new String[]{phoneNum.substring(0, 4)});
						if(cursor2.moveToFirst()){
							city = cursor2.getString(0);
						}
						cursor2.close();
					}
					
				}
				break;
			case 12:// 4位区号+8位电话号码
				if(db.isOpen()){
					Cursor cursor = db.rawQuery("select city from address_tb where area=?", new String[]{phoneNum.substring(0, 4)});
					if(cursor.moveToFirst()){
						city = cursor.getString(0);
					}
					cursor.close();
				}
				break;
			default:
				city=phoneNum+"";
				break;
			}
			
		}
		db.close();
		return city;
	}
}
