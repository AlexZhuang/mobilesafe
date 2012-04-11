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
		//������ѯ�������ȡϵͳ��������ϵ�� 
		while (cursor.moveToNext()) {
			ContactInfo info = new ContactInfo();
			//��ȡ��ϵ��ID
			String id = cursor.getString(cursor.getColumnIndex("_id"));
			//��ȡ��ϵ�˵����� 
			String name = cursor.getString(cursor.getColumnIndex("display_name"));
			info.setName(name);
			
			//��ȡ��ϵ��ID���Ӧ�ĸ��ּ�¼
			Cursor dataCursor = resolver.query(
					Uri.parse("content://com.android.contacts/data"), null,
					"raw_contact_id=?", new String[] { id }, null);
			while (dataCursor.moveToNext()) {
				// ����mimetypeȷ����ϵ�˵ĵ绰����
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