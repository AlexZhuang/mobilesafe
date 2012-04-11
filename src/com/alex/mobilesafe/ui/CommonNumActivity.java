package com.alex.mobilesafe.ui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.alex.mobilesafe.R;
import android.app.Activity;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

public class CommonNumActivity extends Activity {
	private ExpandableListView elv;
	private BaseExpandableListAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.common_num_query);
		elv = (ExpandableListView) this.findViewById(R.id.elv);

		// 判断这个commonnum.db的数据库是否被放置到了sd卡上
		// 如果不在sd卡上 要把db从asset目录拷贝到数据库
		File file = new File("/sdcard/commonnum.db");
		if (!file.exists()) {
			copyfile();
		}

		// listview 是怎么设置数据的?
		// lv.setAdapter(); ->BaseAdapter

		// elv.setAdapter ExpendAdapter ->BaseExpendAdapter

		elv.setAdapter(new MyAdapter());
	}

	private void copyfile() {
		AssetManager manager = getAssets();
		try {
			InputStream is = manager.open("commonnum.db");
			File file = new File("/sdcard/commonnum.db");
			FileOutputStream fos = new FileOutputStream(file);
			byte[] buffer = new byte[1024];
			int len = 0;

			while ((len = is.read(buffer)) != -1) {
				fos.write(buffer, 0, len);
			}
			fos.flush();
			fos.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private class MyAdapter extends BaseExpandableListAdapter {

		// 返回有多少个分组
		public int getGroupCount() {
			int count=0;
			SQLiteDatabase db = SQLiteDatabase.openDatabase("/sdcard/commonnum.db", null, SQLiteDatabase.OPEN_READONLY);
			if(db.isOpen()){
				Cursor cursor = db.rawQuery("select count(*) from classlist", null);
				if(cursor.moveToFirst()){
					count = cursor.getInt(0);
				}
				cursor.close();
				db.close();
			}
			return count;
		}

		// 返回某个分组对应的子孩子的条目个数

		public int getChildrenCount(int groupPosition) {
	
			int count=0;
			int tableindex = groupPosition+1;
			String sql = "select count(*) from table"+tableindex;
			
			SQLiteDatabase db = SQLiteDatabase.openDatabase("/sdcard/commonnum.db", null, SQLiteDatabase.OPEN_READONLY);
			if(db.isOpen()){
				Cursor cursor = db.rawQuery(sql, null);
				if(cursor.moveToFirst()){
					count = cursor.getInt(0);
				}
				cursor.close();
				db.close();
			}
			return count;

		}

		// 返回当前groupPosition 对应位置的对象
		public Object getGroup(int groupPosition) {
			// TODO Auto-generated method stub
			return null;
		}

		// 返回groupPosition第childPosition个子孩子对应的条目
		public Object getChild(int groupPosition, int childPosition) {
			// TODO Auto-generated method stub
			return null;
		}

		// 获取分组的id
		public long getGroupId(int groupPosition) {
			// TODO Auto-generated method stub
			return groupPosition;
		}

		// 获取分组中子孩子id
		public long getChildId(int groupPosition, int childPosition) {
			// TODO Auto-generated method stub
			return 0;
		}

		public boolean hasStableIds() {
			// TODO Auto-generated method stub
			return false;
		}

		//
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			TextView tv = new TextView(CommonNumActivity.this);
			String text ="";
			int currentpos = groupPosition+1;
			SQLiteDatabase db = SQLiteDatabase.openDatabase("/sdcard/commonnum.db", null, SQLiteDatabase.OPEN_READONLY);
			if(db.isOpen()){
				Cursor cursor = db.rawQuery("select name from classlist where idx=?", new String[]{currentpos+""});
				if(cursor.moveToFirst()){
					text = cursor.getString(0);
				}
				cursor.close();
				db.close();
			}
			tv.setText("             "+text);
			return tv;
		}

		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			TextView tv = new TextView(CommonNumActivity.this);
			StringBuilder sb = new StringBuilder();
			int tableindex = groupPosition+1;
			int childindex = childPosition+1;
			String sql = "select number,name from table"+tableindex;
			
			SQLiteDatabase db = SQLiteDatabase.openDatabase("/sdcard/commonnum.db", null, SQLiteDatabase.OPEN_READONLY);
			if(db.isOpen()){
				Cursor cursor = db.rawQuery(sql+ " where _id=?", new String[]{childindex+""});
				if(cursor.moveToFirst()){
				   sb.append(	cursor.getString(0)); //number
				   sb.append(":");
				   sb.append(	cursor.getString(1)); //name
					
				}
				cursor.close();
				db.close();
			}
			String text = sb.toString();
			tv.setText(text);
			return tv;
		}

		public boolean isChildSelectable(int groupPosition, int childPosition) {
			// TODO Auto-generated method stub
			return true;
		}

	}

}
