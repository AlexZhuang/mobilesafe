package com.alex.mobilesafe.engine;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.alex.mobilesafe.domain.AppInfo;

public class AppInfoProvider {
	private Context context;
	PackageManager packageManager ;
	public AppInfoProvider(Context context){
		this.context=context;
		packageManager = context.getPackageManager();
	}
	/**
	 * ���ص�ǰ�ֻ����氲װ�����еĳ�����Ϣ�ļ���
	 * @return Ӧ�ó���ļ���
	 */
	public List<AppInfo> getAllAppInfo(){
		List<AppInfo> myApps= new ArrayList<AppInfo>();
		AppInfo myApp = null;
		List<PackageInfo> packageInfos = packageManager.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
		for(PackageInfo packageInfo:packageInfos){
			myApp = new AppInfo();
			//<manifest package="...">...</manifest>�е�package���Ե�ֵ
			String packageName = packageInfo.packageName;
			myApp.setPackageName(packageName);
			ApplicationInfo appInfo = packageInfo.applicationInfo;
			//����Ӧ�ó�������<application android:label="@string/app_name" >
			myApp.setAppName(appInfo.loadLabel(packageManager).toString());
		    //����Ӧ�ó���ͼ��<application android:icon="@drawable/..." >
			myApp.setIcon(appInfo.loadIcon(packageManager));
			//�ж��Ƿ���ϵͳӦ�ó���
			myApp.setSystemApp(!filterApp(appInfo));
			myApps.add(myApp);
		}
		return myApps;
	}
	
	/**
	 * �ж�ĳ��Ӧ�ó����� ����������Ӧ�ó���
	 * @param info
	 * @return ����ǵ�����Ӧ�ó����򷵻�true�������ϵͳ�����򷵻�false
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
