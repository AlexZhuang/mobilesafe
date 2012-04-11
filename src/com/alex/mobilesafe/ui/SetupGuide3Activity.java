package com.alex.mobilesafe.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alex.mobilesafe.R;

public class SetupGuide3Activity extends Activity implements OnClickListener{
	private Button btn_setup3_previous;
	private Button btn_setup3_next;
	private Button btn_setup3_selectContact;
	private EditText et_setup3_selectContact;
	private SharedPreferences sp ;
	
	 @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setupguide3);
		sp=getSharedPreferences("config", Context.MODE_PRIVATE);
		
		btn_setup3_previous = (Button) findViewById(R.id.btn_setup3_previous);
		btn_setup3_next = (Button) findViewById(R.id.btn_setup3_next);
		
		btn_setup3_previous.setOnClickListener(this);
		btn_setup3_next.setOnClickListener(this);
		
		btn_setup3_selectContact=(Button) findViewById(R.id.btn_setup3_selectContact);
		btn_setup3_selectContact.setOnClickListener(this);
		
		et_setup3_selectContact= (EditText)findViewById(R.id.et_setup3_selectContact);
		
		String safeNum = sp.getString("safeNum", null);
		if(safeNum!=null || "".equals(safeNum)){
			et_setup3_selectContact.setText(safeNum);
		}
		
		Intent intent = getIntent();
		String phoneNum = intent.getStringExtra("phoneNum");
		if(phoneNum!=null){
			et_setup3_selectContact.setText(phoneNum);
		}
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_setup3_previous:
			finish();
			Intent setup2Intent = new Intent(this,SetupGuide2Activity.class);
			overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
			startActivity(setup2Intent);
			break;
		case R.id.btn_setup3_next:
			String safeNum = et_setup3_selectContact.getText().toString().trim();
			if(safeNum==null || "".equals(safeNum)){
				Toast.makeText(this, "安全号码不能为空", 0).show();
				return ;
			}else{
				Editor editor = sp.edit();
				editor.putString("safeNum", safeNum);
				editor.commit();
				finish();
				Intent setup3Intent = new Intent(this,SetupGuide4Activity.class);
				overridePendingTransition(R.anim.translate_in,R.anim.translate_out);
				startActivity(setup3Intent);
			}
			break;
		case R.id.btn_setup3_selectContact:
			Intent selectContactIntent = new Intent(this,SelectContactActivity.class);
			overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
			startActivity(selectContactIntent);
			break;
		}
	}
}
