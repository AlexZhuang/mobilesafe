package com.alex.moiblesafe.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.alex.mobilesafe.R;
import com.alex.mobilesafe.domain.AppInfo;
import com.alex.mobilesafe.engine.AppInfoProvider;

public class AppListAdapter extends BaseAdapter {
	
	private Context context ;
	private List<AppInfo> appInfos ;
	
	private static ImageView iv ;
	private static TextView tv;
	public AppListAdapter(Context context, List<AppInfo> appInfos){
		this.context=context;
		this.appInfos=appInfos;
	}
	
	
	
	
	public void setAppInfos(List<AppInfo> appInfos) {
		this.appInfos = appInfos;
	}




	@Override
	public int getCount() {
		return appInfos.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return appInfos.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view;
		if(convertView==null){
			view = View.inflate(context, R.layout.app_item, null);
		}else{
			view = convertView;
		}
		AppInfo appInfo = appInfos.get(position);
		iv = (ImageView)view.findViewById(R.id.iv_app_icon);
		iv.setImageDrawable(appInfo.getIcon());
		tv = (TextView)view.findViewById(R.id.tv_app_name);
		tv.setText(appInfo.getAppName());
		return view;
	}

}
