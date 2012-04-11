package com.alex.mobilesafe.engine;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.alex.mobilesafe.domain.ContactInfo;

public class ContactInfoService {

	private Context context;

	public ContactInfoService(Context context) {
		this.context = context;
	}

	public List<ContactInfo> getContacts() {
		ContentResolver resolver = this.context.getContentResolver();
		Cursor cursor = resolver.query(Uri.parse("content://com.android.contacts/contacts"), null,
				null, null, null);
		List<ContactInfo> infos = new ArrayList<ContactInfo>();
		//遍历查询结果，获取系统中所有联系人 
		while (cursor.moveToNext()) {
			ContactInfo info = new ContactInfo();
			//获取联系人ID
			String id = cursor.getString(cursor.getColumnIndex("_id"));
			//获取联系人的名字 
			String name = cursor.getString(cursor.getColumnIndex("display_name"));
			info.setName(name);
			
			//获取联系人ID相对应的各种记录
			Cursor dataCursor = resolver.query(
					Uri.parse("content://com.android.contacts/data"), null,
					"raw_contact_id=?", new String[] { id }, null);
			while (dataCursor.moveToNext()) {
				// 根据mimetype确定联系人的电话号码
				String type = dataCursor.getString(dataCursor.getColumnIndex("mimetype"));
				if ("vnd.android.cursor.item/phone_v2".equals(type)) {
					String number = dataCursor.getString(dataCursor
							.getColumnIndex("data1"));
					info.setPhoneNum(number);
				}
			}
			infos.add(info);
		}
		return infos;
	}
}