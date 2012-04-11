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
			String text = tv_avail_memory.getText().toString() + "总内存:"
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
						// 更改checkbox的状态
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
	 * 填充listview的数据
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
				// 通知界面更新数据
				totalused = 0; // 所有程序占用的内存信息 kb
				for (TaskInfo taskinfo : listtaskinfos) {
					totalused += taskinfo.getMemorysize();
				}

				handler.sendEmptyMessage(0);
			}

		}.start();

	}

	/**
	 * 设置title的数据
	 */
	private void setTitleData() {
		tv_task_count.setText("进程数目: " + getProcessCount());
		tv_avail_memory.setText("剩余内存"
				+ TextFormater.getDataSize(getAvailMemoryInfo()));
	}

	/**
	 * 获取当前正在运行的进程的数目
	 * 
	 * @return
	 */
	private int getProcessCount() {
		runingappinfos = am.getRunningAppProcesses();
		return runingappinfos.size();
	}

	/**
	 * 获取当前系统的剩余的可用内存信息 byte long
	 */
	private long getAvailMemoryInfo() {
		MemoryInfo outInfo = new ActivityManager.MemoryInfo();
		am.getMemoryInfo(outInfo);
		return outInfo.availMem;

	}
	
	
	/**
	 * 杀死所有选择的进程
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

		// 通知用户杀死了多少个进程
		String size = TextFormater.getKBDataSize(memorysize);
		// Toast.makeText(this, "杀死了"+total+"个进程,释放了"+size+"空间", 0).show();
		MyToast.showToast(this, R.drawable.notification, "杀死了" + total
				+ "个进程,释放了" + size + "空间");
		// 通知ui更新
		fillData();

	}
	
	
	/**
	 * 进入进程管理器的设置界面
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
		 * 在构造方法里面完成了用户列表和系统程序列表的区分
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
				return listtaskinfos.size() + 2; // 2 代表的是两个标签 用来显示程序的个数
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
			// 把这些条目信息 做一下分类 系统进程和用户进程区分出来
			System.out.println("getView : " + position);

			if (position == 0) {
				TextView tv_userapp = new TextView(TaskManagerActivity.this);
				tv_userapp.setText("用户进程 " + usertaskinfos.size() + "个");
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
				holder.tv_memory_size.setText("内存占用: "
						+ TextFormater.getKBDataSize(taskinfo.getMemorysize()));
				holder.cb_task_checked.setChecked(taskinfo.isIschecked());
				return view;

			} else if (position == usertaskinfos.size() + 1) {
				TextView tv_systemapp = new TextView(TaskManagerActivity.this);
				tv_systemapp.setText("系统进程 " + systemtaskinfos.size() + "个");
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
				holder.tv_memory_size.setText("内存占用: "
						+ TextFormater.getKBDataSize(taskinfo.getMemorysize()));
				holder.cb_task_checked.setChecked(taskinfo.isIschecked());
				return view;

			} else {// 肯定不会执行
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
