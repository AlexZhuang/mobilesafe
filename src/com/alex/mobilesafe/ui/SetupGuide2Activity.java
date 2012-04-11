package com.alex.mobilesafe.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;

import com.alex.mobilesafe.R;

public class SetupGuide2Activity extends Activity implements OnClickListener{
	
	private Button btn_setup2_previous;
	private Button btn_setup2_next ;
	private Button btn_setup2_bind;
	private CheckBox ck_setup2_bind;
	private SharedPreferences sp ;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setupguide2);
		
		sp = getSharedPreferences("config", Context.MODE_PRIVATE);
		
		btn_setup2_previous =(Button) findViewById(R.id.btn_setup2_previous);
		btn_setup2_next =(Button) findViewById(R.id.btn_setup2_next);
		btn_setup2_bind = (Button) findViewById(R.id.btn_setup2_bind);
		ck_setup2_bind = (CheckBox) findViewById(R.id.ck_setup2_bind);
		
		btn_setup2_previous.setOnClickListener(this);
		btn_setup2_next.setOnClickListener(this);
		btn_setup2_bind.setOnClickListener(this);
		
		String simserial = sp.getString("simserial", null);
		if(simserial!=null && !"".equals(simserial)){
			btn_setup2_bind.setText("解除绑定");
			ck_setup2_bind.setChecked(true);
		}else{
			btn_setup2_bind.setText("绑定");
			ck_setup2_bind.setChecked(false);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_setup2_previous:
			finish();;//把当前Activity从任务栈中移除
			Intent setup1Intent = new Intent(this,SetupGuide1Activity.class);
			//设置Activity切换效果
			overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
			startActivity(setup1Intent);
			break;
		case R.id.btn_setup2_next:
			finish();;//把当前Activity从任务栈中移除
			//设置Activity切换效果
			overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
			Intent setup3Intent = new Intent(this,SetupGuide3Activity.class);
			startActivity(setup3Intent);
			break;
		case R.id.btn_setup2_bind:			
			String btnText = btn_setup2_bind.getText().toString();
			Editor editor = sp.edit();			
			if(btnText.equals("绑定")){
				btn_setup2_bind.setText("解除绑定");
				ck_setup2_bind.setChecked(true);
				TelephonyManager manager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
				String simserial = manager.getSimSerialNumber();		
				editor.putString("simserial", simserial);
				editor.commit();
			}else{
				btn_setup2_bind.setText("绑定");
				ck_setup2_bind.setChecked(false);
				editor.putString("simserial", null);
				editor.commit();
			}
			break;
		}
		
		
	}
}
