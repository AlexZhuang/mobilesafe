package com.alex.mobilesafe.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.alex.mobilesafe.MyApplication;
import com.alex.mobilesafe.R;
import com.alex.mobilesafe.domain.TaskInfo;
import com.alex.mobilesafe.engine.TaskInfoProvider;
import com.alex.mobilesafe.ui.stub.MyToast;
import com.alex.mobilesafe.util.TextFormater;

public class TaskManagerActivity extends Activity {
	private TextView tv_task_count;
	private TextView tv_avail_memory;
	private ActivityManager am;
	private LinearLayout ll_task_manager_loading;
	private List<RunningAppProcessInfo> runingappinfos;
	private TaskInfoProvider taskInfoprovider;
	private List<TaskInfo> listtaskinfos;
	private long totalused = 0;
	private TaskInfoAdapter adapter;
	private List<TaskInfo> usertaskinfos;
	private List<TaskInfo> systemtaskinfos;
	private ListView lv_task_manager;
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			ll_task_manager_loading.setVisibility(View.INVISIBLE);
			long totalmemoryinfo = totalused * 1024 + getAvailMemoryInfo();
			String strtotalmemory = TextFormater.getDataSize(totalmemoryinfo);
			String text = tv_avail_memory.getText().toString() + "���ڴ�:"
					+ strtotalmemory;
			tv_avail_memory.setText(text);
			adapter = new TaskInfoAdapter();
			lv_task_manager.setAdapter(adapter);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		boolean flag = requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.task_manager);
		if (flag) {
			getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
					R.layout.task_manager_title);
		}
		am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		tv_task_count = (TextView) this.findViewById(R.id.tv_task_count);
		tv_avail_memory = (TextView) this.findViewById(R.id.tv_avail_memory);
		ll_task_manager_loading = (LinearLayout) findViewById(R.id.ll_task_manager_loading);
		lv_task_manager = (ListView)findViewById(R.id.lv_task_manager);
		
		
		lv_task_manager.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Object obj = lv_task_manager.getItemAtPosition(position);
				if (obj instanceof TaskInfo) {
					TaskInfo taskinfo = (TaskInfo) obj;
					String packname = taskinfo.getPackname();
					CheckBox cb = (CheckBox) view
							.findViewById(R.id.cb_task_checked);
//					if ("com.alex.mobilesafe".equals(packname)
//							|| "system".equals(packname)
//							|| "android.process.media".equals(packname)) {
//						cb.setVisibility(View.INVISIBLE);
//						return;
//					}

					if (taskinfo.isIschecked()) {
						taskinfo.setIschecked(false);
						// ����checkbox��״̬
						cb.setChecked(false);
					} else {
						taskinfo.setIschecked(true);
						cb.setChecked(true);
					}
				}
			}
		});
		
		lv_task_manager.setOnItemLongClickListener(new OnItemLongClickListener() {

			public boolean onItemLongClick(AdapterView<?> parent,
					View view, int position, long id) {
				Intent intent = new Intent(TaskManagerActivity.this,
						AppDetailActivity.class);

				MyApplication myapp = (MyApplication) getApplication();
				Object obj = lv_task_manager.getItemAtPosition(position);
				if (obj instanceof TaskInfo) {
					TaskInfo taskinfo = (TaskInfo) obj;
					myapp.taskinfo = taskinfo;
					startActivity(intent);

				}

				return false;
			}
		});
		
		fillData();
	}

	/**
	 * ���listview������
	 */
	private void fillData() {

		setTitleData();
		ll_task_manager_loading.setVisibility(View.VISIBLE);
		new Thread() {
			@Override
			public void run() {
				taskInfoprovider = new TaskInfoProvider(
						TaskManagerActivity.this);
				listtaskinfos = taskInfoprovider.getAllTasks(runingappinfos);
				// ֪ͨ�����������
				totalused = 0; // ���г���ռ�õ��ڴ���Ϣ kb
				for (TaskInfo taskinfo : listtaskinfos) {
					totalused += taskinfo.getMemorysize();
				}

				handler.sendEmptyMessage(0);
			}

		}.start();

	}

	/**
	 * ����title������
	 */
	private void setTitleData() {
		tv_task_count.setText("������Ŀ: " + getProcessCount());
		tv_avail_memory.setText("ʣ���ڴ�"
				+ TextFormater.getDataSize(getAvailMemoryInfo()));
	}

	/**
	 * ��ȡ��ǰ�������еĽ��̵���Ŀ
	 * 
	 * @return
	 */
	private int getProcessCount() {
		runingappinfos = am.getRunningAppProcesses();
		return runingappinfos.size();
	}

	/**
	 * ��ȡ��ǰϵͳ��ʣ��Ŀ����ڴ���Ϣ byte long
	 */
	private long getAvailMemoryInfo() {
		MemoryInfo outInfo = new ActivityManager.MemoryInfo();
		am.getMemoryInfo(outInfo);
		return outInfo.availMem;

	}
	
	
	/**
	 * ɱ������ѡ��Ľ���
	 * 
	 * @param view
	 */
	public void killTask(View view) {
		int total = 0;
		int memorysize = 0;
		for (TaskInfo taskinfo : usertaskinfos) {
			if (taskinfo.isIschecked()) {
				memorysize += taskinfo.getMemorysize();
				am.killBackgroundProcesses(taskinfo.getPackname());
				// listtaskinfos.remove(taskinfo);
				total++;
			}
		}
		for (TaskInfo taskinfo : systemtaskinfos) {
			if (taskinfo.isIschecked()) {
				memorysize += taskinfo.getMemorysize();
				am.killBackgroundProcesses(taskinfo.getPackname());
				// listtaskinfos.remove(taskinfo);
				total++;
			}
		}

		// ֪ͨ�û�ɱ���˶��ٸ�����
		String size = TextFormater.getKBDataSize(memorysize);
		// Toast.makeText(this, "ɱ����"+total+"������,�ͷ���"+size+"�ռ�", 0).show();
		MyToast.showToast(this, R.drawable.notification, "ɱ����" + total
				+ "������,�ͷ���" + size + "�ռ�");
		// ֪ͨui����
		fillData();

	}
	
	
	/**
	 * ������̹����������ý���
	 */
	public void appSetting(View view) {
		Intent intent = new Intent(this,TaskSettingActivity.class);
		startActivityForResult(intent, 200);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode==200){
			fillData();
		}
	}

	private class TaskInfoAdapter extends BaseAdapter {

		/**
		 * �ڹ��췽������������û��б��ϵͳ�����б������
		 */
		public TaskInfoAdapter() {
			usertaskinfos = new ArrayList<TaskInfo>();

			systemtaskinfos = new ArrayList<TaskInfo>();

			for (TaskInfo taskinfo : listtaskinfos) {
				if (taskinfo.isSystemapp()) {
					systemtaskinfos.add(taskinfo);
				} else {
					usertaskinfos.add(taskinfo);
				}
			}
		}

		@Override
		public int getCount() {
			SharedPreferences sp = getSharedPreferences("config",
					Context.MODE_PRIVATE);
			boolean showsystemapp = sp.getBoolean("showsystemapp", false);
			if (showsystemapp) {
				return listtaskinfos.size() + 2; // 2 �������������ǩ ������ʾ����ĸ���
			} else {
				return usertaskinfos.size() + 1;
			}
		}

		@Override
		public Object getItem(int position) {
			if (position == 0) {
				return 0;
			} else if (position <= usertaskinfos.size()) {
				return usertaskinfos.get(position - 1);
			} else if (position == usertaskinfos.size() + 1) {
				return position;
			} else if (position <= listtaskinfos.size() + 2) {
				return systemtaskinfos.get(position - usertaskinfos.size() - 2);
			} else {
				return position;
			}
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// ����Щ��Ŀ��Ϣ ��һ�·��� ϵͳ���̺��û��������ֳ���
			System.out.println("getView : " + position);

			if (position == 0) {
				TextView tv_userapp = new TextView(TaskManagerActivity.this);
				tv_userapp.setText("�û����� " + usertaskinfos.size() + "��");
				return tv_userapp;
			} else if (position <= usertaskinfos.size()) {
				int currentpositon = (position - 1);
				TaskInfo taskinfo = usertaskinfos.get(currentpositon);
				View view = View.inflate(TaskManagerActivity.this,
						R.layout.task_manager_item, null);
				ViewHolder holder = new ViewHolder();
				holder.iv = (ImageView) view.findViewById(R.id.iv_app_icon);
				holder.tv_name = (TextView) view.findViewById(R.id.tv_app_name);
				holder.tv_memory_size = (TextView) view
						.findViewById(R.id.tv_app_memory_size);
				holder.cb_task_checked = (CheckBox) view
						.findViewById(R.id.cb_task_checked);
				String packname = taskinfo.getPackname();
				if ("com.alex.mobilesafe".equals(packname)
						|| "system".equals(packname)
						|| "android.process.media".equals(packname)) {
					holder.cb_task_checked.setVisibility(View.INVISIBLE);

				} else {
					holder.cb_task_checked.setVisibility(View.VISIBLE);
				}
				holder.iv.setImageDrawable(taskinfo.getAppicon());
				holder.tv_name.setText(taskinfo.getAppname());
				holder.tv_memory_size.setText("�ڴ�ռ��: "
						+ TextFormater.getKBDataSize(taskinfo.getMemorysize()));
				holder.cb_task_checked.setChecked(taskinfo.isIschecked());
				return view;

			} else if (position == usertaskinfos.size() + 1) {
				TextView tv_systemapp = new TextView(TaskManagerActivity.this);
				tv_systemapp.setText("ϵͳ���� " + systemtaskinfos.size() + "��");
				return tv_systemapp;

			} else if (position <= listtaskinfos.size() + 2) {
				int systemposition = (position - usertaskinfos.size() - 2);
				TaskInfo taskinfo = systemtaskinfos.get(systemposition);
				View view = View.inflate(TaskManagerActivity.this,
						R.layout.task_manager_item, null);
				ViewHolder holder = new ViewHolder();
				holder.iv = (ImageView) view.findViewById(R.id.iv_app_icon);
				holder.tv_name = (TextView) view.findViewById(R.id.tv_app_name);
				holder.tv_memory_size = (TextView) view
						.findViewById(R.id.tv_app_memory_size);
				holder.cb_task_checked = (CheckBox) view
						.findViewById(R.id.cb_task_checked);
				String packname = taskinfo.getPackname();
				if ("cn.itcast.mobilesafe".equals(packname)
						|| "system".equals(packname)
						|| "android.process.media".equals(packname)) {
					holder.cb_task_checked.setVisibility(View.INVISIBLE);

				} else {
					holder.cb_task_checked.setVisibility(View.VISIBLE);
				}
				holder.iv.setImageDrawable(taskinfo.getAppicon());
				holder.tv_name.setText(taskinfo.getAppname());
				holder.tv_memory_size.setText("�ڴ�ռ��: "
						+ TextFormater.getKBDataSize(taskinfo.getMemorysize()));
				holder.cb_task_checked.setChecked(taskinfo.isIschecked());
				return view;

			} else {// �϶�����ִ��
				return null;
			}

		}

	}

	static class ViewHolder {
		public ImageView iv;
		public TextView tv_name;
		public TextView tv_memory_size;
		public CheckBox cb_task_checked;
	}

}
