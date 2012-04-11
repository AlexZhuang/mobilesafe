package com.alex.mobilesafe.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.alex.mobilesafe.R;

public class SetupGuide1Activity extends Activity implements OnClickListener{
	
	private Button btn_setup1_next;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setupguide1);
		btn_setup1_next = (Button)findViewById(R.id.btn_setup1_next);
		btn_setup1_next.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.btn_setup1_next:
			finish();//把当前Activity从任务栈中移除
			Intent setup2Intent = new Intent(this,SetupGuide2Activity.class);
			//设置Activity切换时候的动画效果
			//overridePendingTransition:Call immediately after one of the flavors of startActivity(Intent) or finish to specify an explicit transition animation to perform next.
			overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
			startActivity(setup2Intent);
			break;
		}
		
	}


	
	
	
}
