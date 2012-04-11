package com.alex.mobilesafe.ui;

import com.alex.mobilesafe.R;
import com.alex.mobilesafe.engine.NumberAddressService;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class QueryNumberActivity extends Activity implements OnClickListener {
	
	private EditText et_queryNumber_number;
	private Button btn_queryNumber_query;
	private TextView tv_queryNumber_city ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.query_number);
		et_queryNumber_number= (EditText)findViewById(R.id.et_queryNumber_number);
		btn_queryNumber_query = (Button) findViewById(R.id.btn_queryNumber_query);
		tv_queryNumber_city = (TextView)findViewById(R.id.tv_queryNumber_city);
		btn_queryNumber_query.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_queryNumber_query:
			String et_text =et_queryNumber_number.getText().toString().trim();
			if("".equals(et_text)){
				Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
		        findViewById(R.id.et_queryNumber_number).startAnimation(shake);
			}else{
				NumberAddressService service = new NumberAddressService(this);
				String city = service.getAddress(et_text);
				tv_queryNumber_city.setText("πÈ Ùµÿ–≈œ¢£∫"+city);
			}
			break;
		}
		
	}
}
