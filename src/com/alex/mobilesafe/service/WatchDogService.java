package com.alex.mobilesafe.service;

import java.util.ArrayList;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.KeyguardManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.alex.mobilesafe.db.dao.AppLockDao;
import com.alex.mobilesafe.ui.LockScreenActivity;

public class WatchDogService extends Service {
	protected static final String TAG = "WatchDogService";
	private AppLockDao dao;
	private List<String> lockapps;
	private ActivityManager am;
	private Intent lockappintent;
	private boolean flag;
	private MyBinder myBinder;
	private KeyguardManager keyguardManager;
	private List<String> tempstopapps;

	@Override
	public IBinder onBind(Intent intent) {
		myBinder = new MyBinder();
		return myBinder;
	}
	
	public class MyBinder extends Binder implements IService {

		public void callAppProtectStart(String packname) {

			appProtectStart(packname);
		}

		public void callAppProtectStop(String packname) {
			appProtectStop(packname);

		}

	}
	/**
	 * ���¿�����Ӧ�õı���
	 * 
	 * @param packname
	 */
	public void appProtectStart(String packname) {
		if (tempstopapps.contains(packname)) {
			tempstopapps.remove(packname);
		}
	}

	/**
	 * ��ʱֹͣ��ĳ��app�ı���
	 * 
	 * @param packname
	 */
	public void appProtectStop(String packname) {
		tempstopapps.add(packname);
	}

	@Override
	public void onCreate() {
		
		getContentResolver().registerContentObserver(Uri.parse("content://com.alex.applockprovider"), true, new MyObserver(new Handler()));
		keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
		dao = new AppLockDao(this);
		tempstopapps = new ArrayList<String>();
		flag = true;
		// �õ����е�Ҫ������Ӧ�ó���
		lockapps = dao.getAllApps();
		lockappintent = new Intent(this, LockScreenActivity.class);
		// �����ǲ���������ջ�� Ҫ�ڷ������濪��activity�Ļ� �����������һ��flag
		lockappintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

		new Thread() {

			@Override
			public void run() {
				// �������Ź�
				while (flag) {
					try {
						// �ж���Ļ�Ƿ�������״̬
						if (keyguardManager.inKeyguardRestrictedInputMode()) {
							// �����ʱ�ļ���
							tempstopapps.clear();

						}
						// //lockapps ��ϢΪ���µ�
						// lockapps = dao.getAllApps();
						// �õ���ǰ�������г���İ���
						// ����ϵͳ���������ջ����Ϣ , taskinfos�ļ�������ֻ��һ��Ԫ��
						// ���ݾ��ǵ�ǰ�������еĽ��̶�Ӧ������ջ
						List<RunningTaskInfo> taskinfos = am.getRunningTasks(1);
						RunningTaskInfo currenttask = taskinfos.get(0);
						// ��ȡ��ǰ�û��ɼ���activity ���ڵĳ���İ���
						String packname = currenttask.topActivity
								.getPackageName();
						Log.i(TAG, "��ǰ����" + packname);
						if (lockapps.contains(packname)) {

							// todo : �����ǰ��Ӧ�ó��� ��Ҫ��ʱ�ı���ֹ����
							if (tempstopapps.contains(packname)) {

								// return;
								sleep(1000);
								continue;
							}
							Log.i(TAG, "��Ҫ����" + packname);
							// todo ������һ�������Ľ��� ���û���������
							lockappintent.putExtra("packname", packname);
							startActivity(lockappintent);

						} else {
							// todo ����ִ��
						}
						sleep(1000);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}.start();
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		flag = false;
	}
	
	private class MyObserver extends ContentObserver{

		public MyObserver(Handler handler) {
			super(handler);
			// TODO Auto-generated constructor stub
			
		}

		@Override
		public void onChange(boolean selfChange) {
			
			super.onChange(selfChange);
			//���¸���lockapps�������������
			Log.i("change","----------------------------------���ݿ����ݱ仯��");
			lockapps = dao.getAllApps();
		}
		
	}

}
