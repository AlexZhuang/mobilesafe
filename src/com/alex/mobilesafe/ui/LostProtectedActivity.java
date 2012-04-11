package com.alex.mobilesafe.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alex.mobilesafe.R;
import com.alex.mobilesafe.util.MD5Encoder;

public class LostProtectedActivity extends Activity implements OnClickListener {

	private static final String TAG = "LostProtectedActivity";
	private SharedPreferences sp;
	private LayoutInflater inflater;
	private EditText et_firstEntryDialog_pwd;
	private EditText et_firstEntryDialog_confirmPwd;
	private Button btn_firstEntryDialog_confirm;
	private Button btn_firstEntryDialog_cancel;
	private String confirmPwd;
	private Dialog firstEntryDialog;
	private Dialog normalEntryDialog;
	private Button btn_normalEntryDialog_confirm;
	private Button btn_normalEntryDialog_cancel;
	private EditText et_normalEntryDialog_pwd;
	private TextView tv_reentry_setup_guide;
	private CheckBox cb_isprotecting;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sp = getSharedPreferences("config", Context.MODE_PRIVATE);
		if (!isPWDSet()) {
			Log.i(TAG, "������������Ի���");
			showFirstEntryDialog();
		} else {
			Log.i(TAG, "��������Ի���");
			showNormalEntryDialog();
		}

	}

	/**
	 * �����������ʱ��ʾ�ĶԻ���
	 */
	private void showNormalEntryDialog() {
		Log.i(TAG, "showNormalEntryDialog");
		normalEntryDialog = new Dialog(this, R.style.MyDialog);
		normalEntryDialog.setContentView(View.inflate(this,
				R.layout.normal_entry_dialog, null));

		btn_normalEntryDialog_confirm = (Button) normalEntryDialog
				.findViewById(R.id.btn_normalEntryDialog_confirm);
		btn_normalEntryDialog_cancel = (Button) normalEntryDialog
				.findViewById(R.id.btn_normalEntryDialog_cancel);
		btn_normalEntryDialog_confirm.setOnClickListener(this);
		btn_normalEntryDialog_cancel.setOnClickListener(this);

		et_normalEntryDialog_pwd = (EditText) normalEntryDialog
				.findViewById(R.id.et_normalEntryDialog_pwd);
		normalEntryDialog.show();
	}

	/**
	 * ��һ�ν������ʱ��ʾ�ĶԻ���
	 */
	private void showFirstEntryDialog() {
		Log.i(TAG, "showFirstEntryDialog");
		firstEntryDialog = new Dialog(this, R.style.MyDialog);
		firstEntryDialog.setContentView(R.layout.first_entry_dialog);
		btn_firstEntryDialog_cancel = (Button) firstEntryDialog
				.findViewById(R.id.btn_firstEntryDialog_cancel);
		btn_firstEntryDialog_cancel.setOnClickListener(this);
		btn_firstEntryDialog_confirm = (Button) firstEntryDialog
				.findViewById(R.id.btn_firstEntryDialog_confirm);
		btn_firstEntryDialog_confirm.setOnClickListener(this);
		firstEntryDialog.show();

	}

	private boolean validateFirstEntryDialogPwd() {
		et_firstEntryDialog_pwd = (EditText) firstEntryDialog
				.findViewById(R.id.et_firstEntryDialog_pwd);
		et_firstEntryDialog_confirmPwd = (EditText) firstEntryDialog
				.findViewById(R.id.et_firstEntryDialog_confirmPwd);
		String password = et_firstEntryDialog_pwd.getText().toString().trim();
		confirmPwd = et_firstEntryDialog_confirmPwd.getText().toString().trim();
		if ("".equals(password) || "".equals(confirmPwd)) {
			Toast.makeText(this, "����������", 0).show();
			return false;
		} else if (!password.equals(confirmPwd)) {
			Toast.makeText(this, "�������벻һ��,����������", 0).show();
			et_firstEntryDialog_pwd.setText("");
			et_firstEntryDialog_confirmPwd.setText("");
			return false;
		} else if (password.equals(confirmPwd)) {
			return true;
		} else {
			return false;
		}

	}

	/**
	 * �ж��Ƿ��Ѿ���������
	 * 
	 * @return �жϱ�־
	 */
	private boolean isPWDSet() {
		String password = sp.getString("password", null);
		if (password == null || "".equals(password)) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.btn_firstEntryDialog_cancel:
			Log.i(TAG, "click the cancel button ");
			firstEntryDialog.dismiss();
			break;
		case R.id.btn_firstEntryDialog_confirm:
			Log.i(TAG, "click the confirm button ");
			if (validateFirstEntryDialogPwd()) {
				Log.i(TAG, "validate success ");
				Log.i(TAG, "save password: " + confirmPwd);
				Editor editor = sp.edit();
				String savaPassword = MD5Encoder.encode(confirmPwd);
				editor.putString("password", savaPassword);
				editor.commit();
				firstEntryDialog.dismiss();
				Toast.makeText(this, "���뱣��ɹ�", 0).show();
			}
			break;
		case R.id.btn_normalEntryDialog_confirm:
			String inpuPassword = et_normalEntryDialog_pwd.getText().toString()
					.trim();
			String password = MD5Encoder.encode(inpuPassword);
			String savaPassword = sp.getString("password", null);
			if ("".equals(password)) {
				Toast.makeText(this, "���벻��Ϊ��", 0).show();
			} else if (password.equals(savaPassword)) {
				normalEntryDialog.dismiss();
				Log.i(TAG, "login success");
				
				if(isSetup()){
					Log.i(TAG, "�����ֻ�������ҳ��");
					setContentView(R.layout.lost_protected);
					cb_isprotecting = (CheckBox)findViewById(R.id.cb_isprotecting);
					boolean isProtecting = sp.getBoolean("isProtecting", false);
					cb_isprotecting.setChecked(isProtecting);
					
					cb_isprotecting.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){

						@Override
						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {
							Editor editor = sp.edit();
							if(isChecked){
								editor.putBoolean("isProtecting", true);
							}else{
								editor.putBoolean("isProtecting", false);
							}
							editor.commit();
						}
						
					});
					
					tv_reentry_setup_guide = (TextView)findViewById(R.id.tv_reentry_setup_guide);
					tv_reentry_setup_guide.setOnClickListener(new OnClickListener(){
						@Override
						public void onClick(View v) {
							Intent setup1Intent = new Intent(LostProtectedActivity.this,SetupGuide1Activity.class);
							overridePendingTransition(R.anim.translate_in, R.anim.translate_out);
							startActivity(setup1Intent);
						}
						
					});
					
				}else{
					Log.i(TAG, "�����ֻ���������ҳ��");
					Intent setup1Intent = new Intent(this,SetupGuide1Activity.class);
					startActivity(setup1Intent);
				}
				
			} else {
				et_normalEntryDialog_pwd.setText("");
				Toast.makeText(this, "�����������������", 0).show();
			}
			break;
		case R.id.btn_normalEntryDialog_cancel:
			normalEntryDialog.dismiss();
			Log.i(TAG, "cancel to login");
			finish();
			Intent mainIntent = new Intent(this,MainActivity.class);
			startActivity(mainIntent);
			break;
		}

	}
	
	/**
	 * �ж��ֻ����������Ƿ��������
	 */
	private boolean isSetup() {
		boolean isSetupAlready = sp.getBoolean("isSetupAlready", false);
		return isSetupAlready;
	}

}
