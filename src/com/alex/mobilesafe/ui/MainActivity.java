package com.alex.mobilesafe.ui;


import net.youmi.android.AdManager;
import net.youmi.android.AdView;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alex.mobilesafe.R;
import com.alex.moiblesafe.adapter.MainUIAdapter;

public class MainActivity extends Activity implements OnItemClickListener {
	private static final String TAG = "MainActivity";
	
   //  ע�� ���ڳ�����ڵ�ʹ��static������ʼ��AdManager.init 
	static {
		//				Ӧ��Id				Ӧ������			      ���������(s)   ����ģʽ      
		AdManager.init("537ef88653a2993c", "b9e10bcfe994a9fb", 30, 			false);
	}
	private GridView gv_main;
	private MainUIAdapter adapter;
	// �����־û�һЩ������Ϣ
	private SharedPreferences sp;
	
	private LinearLayout ll_main;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);	
		setContentView(R.layout.mainscreen);
		
		//��ʼ�������ͼ 
		AdView adView = new AdView(this); 
		FrameLayout.LayoutParams params = new 
		FrameLayout.LayoutParams(FrameLayout.LayoutParams.FILL_PARENT, 
		FrameLayout.LayoutParams.WRAP_CONTENT); 
		     
		//���ù����ֵ�λ��(��������Ļ���½�)      
		params.gravity=Gravity.BOTTOM|Gravity.RIGHT;  
		 //�������ͼ����Activity�� 
		addContentView(adView, params);  
		 
		
		sp = this.getSharedPreferences("config", Context.MODE_PRIVATE);
		gv_main = (GridView) this.findViewById(R.id.gv_main);
		adapter = new MainUIAdapter(this);
		gv_main.setAdapter(adapter);
		gv_main.setOnItemClickListener(this);
		gv_main.setOnItemLongClickListener(new OnItemLongClickListener(){
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, final View view,
					int position, long id) {
				if(position==0){
					AlertDialog.Builder builder = new Builder(MainActivity.this);
					builder.setTitle("����");
					builder.setMessage("������Ҫ���ĵ�����");
					final EditText et = new EditText(MainActivity.this);
					et.setHint("�������ı�");
					builder.setView(et);
					builder.setPositiveButton("ȷ��", new OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							String updateName = et.getText().toString().trim();
							if("".equals(updateName)){
								Toast.makeText(MainActivity.this, "�������Ʋ���Ϊ��", 0).show();
								return ;
							}else{
								Editor editor = sp.edit();
								editor.putString("lost_name", updateName);
								editor.commit();
								TextView tv = (TextView)view.findViewById(R.id.tv_main_name);
								tv.setText(updateName);
							}
							
						}
					});
					
					builder.setNegativeButton("ȡ��", new OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							return ;
							
						}
					});
					builder.create().show();
				}
				return false;
			}
			
		});
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		switch (position) {
		case 0:
			Log.i(TAG, "�����ֻ�����ҳ��");
			Intent lostProtectIntent = new Intent(this,LostProtectedActivity.class);
			startActivity(lostProtectIntent);
			break;
		case 1:
			Log.i(TAG, "����ͨѶ��ʿҳ��");
			Intent callSmsIntent = new Intent(this,CallSmsActivity.class);
			startActivity(callSmsIntent);
			break;
		case 2:
			Log.i(TAG, "�����������ҳ��");
			Intent appManageIntent = new Intent(this,AppManagerActivity.class);
			startActivity(appManageIntent);
			break;
		case 3:
			Log.i(TAG, "�����������ҳ��");
			Intent taskManageIntent = new Intent(this,TaskManagerActivity.class);
			startActivity(taskManageIntent);
			break;
		case 4:
			Log.i(TAG, "������������ҳ��");
			Intent TrafficManagerIntent = new Intent(this,TrafficManagerActivity.class);
			startActivity(TrafficManagerIntent);
			break;
		case 7:
			Log.i(TAG, "����߼�����ҳ��");
			Intent aToolsIntent = new Intent(this,AtoolsActivity.class);
			startActivity(aToolsIntent);
			break;
		case 8:
			Log.i(TAG, "������������ҳ��");
			Intent settingCenterIntent = new Intent(this,SettingCenterActivity.class);
			startActivity(settingCenterIntent);
			break;
		}
	}

	
	

	
}
