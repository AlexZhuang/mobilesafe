package com.alex.moiblesafe.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.alex.mobilesafe.R;
import com.alex.mobilesafe.domain.ContactInfo;
import com.alex.mobilesafe.engine.ContactInfoService;

public class ContactsAdapter extends BaseAdapter {
	
	private Context context;
	private List<ContactInfo> infos;
	private static TextView tv_name =null;
	private static TextView tv_phoneNum = null;

	
	public ContactsAdapter(Context context,List<ContactInfo> infos){
		this.context=context;
		this.infos=infos;
	}

	@Override
	public int getCount() {
		
		return infos.size();
	}

	@Override
	public Object getItem(int position) {
		return infos.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view ;
		ContactInfo info = infos.get(position);
		if(convertView==null){
			view = View.inflate(context, R.layout.contact_item, null);
		}else{
			view = convertView;
		}
		tv_name = (TextView) view.findViewById(R.id.tv_contact_name);
		tv_phoneNum = (TextView) view.findViewById(R.id.tv_contact_phoneNum);
		
		System.out.println("name:"+info.getName()+";phoneNum:"+info.getPhoneNum());
		
		tv_name.setText(info.getName());
		tv_phoneNum.setText(info.getPhoneNum());
		return view;
	}
	
	
	
	
}
