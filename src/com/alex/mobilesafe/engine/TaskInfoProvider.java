package com.alex.mobilesafe.engine;

import java.util.ArrayList;
import java.util.List;

import android.R;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Debug.MemoryInfo;

import com.alex.mobilesafe.domain.TaskInfo;

public class TaskInfoProvider {
	private PackageManager pm;
	private ActivityManager am;
	private Context context;
	
	
	
	public TaskInfoProvider(Context context) {
		this.context = context;
		pm = context.getPackageManager();
		am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
	}

	public List<TaskInfo> getAllTasks( List<RunningAppProcessInfo> runingappinfos){
		List<TaskInfo> taskinfos = new ArrayList<TaskInfo>();
		for(RunningAppProcessInfo info : runingappinfos){
			TaskInfo taskinfo ;
		
			try {
				taskinfo = new TaskInfo();
				int pid = info.pid;
				taskinfo.setPid(pid);
				String packname = info.processName;
				taskinfo.setPackname(packname);
				System.out.println("packagename : "+packname);
				ApplicationInfo appinfo = pm.getPackageInfo(packname, 0).applicationInfo;
				Drawable appicon = appinfo.loadIcon(pm);

				taskinfo.setAppicon(appicon);
				
			    if(filterApp(appinfo)){
			    	taskinfo.setSystemapp(false);
			    }else{
			    	taskinfo.setSystemapp(true);
			    }
				
				String appname = appinfo.loadLabel(pm).toString();
				taskinfo.setAppname(appname);
				MemoryInfo[] memoryinfos = am.getProcessMemoryInfo(new int[]{pid});
				int memorysize = memoryinfos[0].getTotalPrivateDirty();
				taskinfo.setMemorysize(memorysize);	
				taskinfos.add(taskinfo);
				taskinfo = null;
			} catch (NameNotFoundException e) {
				e.printStackTrace();
				taskinfo = new TaskInfo();
				String packname = info.processName;
				taskinfo.setPackname(packname);
				taskinfo.setAppname(packname);
				Drawable appicon = context.getResources().getDrawable(com.alex.mobilesafe.R.drawable.ic_launcher);
				taskinfo.setAppicon(appicon);
				int pid = info.pid;
				taskinfo.setPid(pid);
				taskinfo.setSystemapp(true);
				MemoryInfo[] memoryinfos = am.getProcessMemoryInfo(new int[]{pid});
				int memorysize = memoryinfos[0].getTotalPrivateDirty();
				taskinfo.setMemorysize(memorysize);	
				taskinfos.add(taskinfo);
				taskinfo = null;
				
			}
		}
		
		
		return taskinfos;
	}

	/**
	 * �ж�ĳ��Ӧ�ó����� ����������Ӧ�ó���
	 * @param info
	 * @return 
	 */
    public boolean filterApp(ApplicationInfo info) {
        if ((info.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
            return true;
        } else if ((info.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
            return true;
        }
        return false;
    }
}

