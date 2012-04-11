package com.alex.mobilesafe.ui.stub;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alex.mobilesafe.R;

public class MyToast {

	/**
	 * ��ʾ�Զ������˾
	 * @param context ������
	 * @param iconid ͼ���id
	 * @param text ��ʾ���ı�
	 */
	public static void showToast(Context context,int iconid, String text){
		View view = View.inflate(context, R.layout.my_toast, null);
	   	TextView tv = (TextView) view.findViewById(R.id.tv_my_toast);
		ImageView iv = (ImageView) view.findViewById(R.id.iv_my_toast);
		iv.setImageResource(iconid);
		tv.setText(text);
		Toast toast = new Toast(context);
		toast.setDuration(0);
		toast.setView(view);
		toast.show();
	}
	
}