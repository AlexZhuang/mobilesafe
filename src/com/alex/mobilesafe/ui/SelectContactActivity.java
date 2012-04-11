package com.alex.mobilesafe.ui;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.alex.mobilesafe.R;
import com.alex.mobilesafe.domain.ContactInfo;
import com.alex.mobilesafe.engine.ContactInfoService;
import com.alex.moiblesafe.adapter.ContactsAdapter;

public class SelectContactActivity extends Activity {
	private ListView lv_contacts;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.select_contact);
		ContactInfoService service = new ContactInfoService(getApplicationContext());
		final List<ContactInfo> infos = service.getContacts();
		lv_contacts = (ListView)findViewById(R.id.lv_contacts);
		lv_contacts.setAdapter(new ContactsAdapter(getApplicationContext(),infos));
		lv_contacts.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				String phoneNum = infos.get(position).getPhoneNum();
				Intent setup3Intent = new Intent(SelectContactActivity.this,SetupGuide3Activity.class);
				setup3Intent.putExtra("phoneNum", phoneNum);
				overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
				finish();
				startActivity(setup3Intent);
			}


			
		});
	}
}
