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
	 * 返回当前手机里面安装的所有的程序信息的集合
	 * @return 应用程序的集合
	 */
	public List<AppInfo> getAllAppInfo(){
		List<AppInfo> myApps= new ArrayList<AppInfo>();
		AppInfo myApp = null;
		List<PackageInfo> packageInfos = packageManager.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
		for(PackageInfo packageInfo:packageInfos){
			myApp = new AppInfo();
			//<manifest package="...">...</manifest>中的package属性的值
			String packageName = packageInfo.packageName;
			myApp.setPackageName(packageName);
			ApplicationInfo appInfo = packageInfo.applicationInfo;
			//设置应用程序名：<application android:label="@string/app_name" >
			myApp.setAppName(appInfo.loadLabel(packageManager).toString());
		    //设置应用程序图标<application android:icon="@drawable/..." >
			myApp.setIcon(appInfo.loadIcon(packageManager));
			//判断是否是系统应用程序
			myApp.setSystemApp(!filterApp(appInfo));
			myApps.add(myApp);
		}
		return myApps;
	}
	
	/**
	 * 判断某个应用程序是 不是三方的应用程序
	 * @param info
	 * @return 如果是第三方应用程序则返回true，如果是系统程序则返回false
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
