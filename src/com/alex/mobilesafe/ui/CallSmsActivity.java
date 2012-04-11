package com.alex.mobilesafe.ui;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alex.mobilesafe.R;
import com.alex.mobilesafe.db.dao.BlackNumberDao;

public class CallSmsActivity extends Activity {
	private ListView lv_call_sms_safe;
	private Button bt_add_black_number;
	private BlackNumberDao dao;
	private List<String> numbers;
	private CallSmsAdapter adapter;
	private static TextView tv;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.call_sms_safe);
		dao = new BlackNumberDao(this);
		lv_call_sms_safe = (ListView) this.findViewById(R.id.lv_call_sms_safe);
		// ��listviewע�������Ĳ˵�
		registerForContextMenu(lv_call_sms_safe);
		bt_add_black_number = (Button) this.findViewById(R.id.bt_add_black_number);
		
		bt_add_black_number.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				showAddNumDialog("");
			}
		});
		numbers = dao.getAllNumbers();
		//adapter = new ArrayAdapter<String>(this, R.layout.blacknumber_item, R.id.tv_blacknumber_item, numbers);
		adapter = new CallSmsAdapter();
		
		lv_call_sms_safe.setAdapter(adapter);
	
	
	
	}
	
	
	
	@Override
	protected void onResume() {
		super.onResume();
		System.out.println("callsms resume ... ");
		Intent intent = getIntent();
		String number = intent.getStringExtra("number");
		if(number!= null){
			showAddNumDialog(number);
		}
		
		
	}



	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
	                                ContextMenuInfo menuInfo) {
	  super.onCreateContextMenu(menu, v, menuInfo);
	  MenuInflater inflater = getMenuInflater();
	  inflater.inflate(R.menu.context_menu, menu);
	}
	
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
	  AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
	  int id = (int) info.id;
	  String number = numbers.get(id);
	  switch (item.getItemId()) {
	  case R.id.update_number:
		  
		  updataNumber(number);
		  break;
	  case R.id.delete_number:
		  // ��ǰ��Ŀ��id

		  dao.delete(number);
		  // ���»�ȡ����������
		  numbers = dao.getAllNumbers();
		  //  ֪ͨlistview���½���
		  adapter.notifyDataSetChanged();
		  break;

	  }
	return false;
	}
	
	
	/**
	 * ���º���������
	 * @param number
	 */
	private void updataNumber(final String oldnumber) {
		AlertDialog.Builder builder = new Builder(CallSmsActivity.this);
		builder.setTitle("���ĺ���������");
		final EditText et = new EditText(CallSmsActivity.this);
		et.setInputType(InputType.TYPE_CLASS_PHONE);
		builder.setView(et);
		builder.setPositiveButton("����", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				String newNumber =  et.getText().toString().trim();
				if(TextUtils.isEmpty(newNumber)){
					Toast.makeText(getApplicationContext(), "���������벻��Ϊ��", 1).show();
					return ;
				}else{
					dao.update(oldnumber, newNumber);
					//todo: ֪ͨlistview��������
					// ȱ��: ����ˢ������listview 
//					numbers = dao.getAllNumbers();
//					lv_call_sms_safe.setAdapter(new ArrayAdapter<String>(CallSmsActivity.this, R.layout.blacknumber_item, R.id.tv_blacknumber_item, numbers));
					numbers = dao.getAllNumbers();
					
					// ������������֪ͨlistview�������� 
					adapter.notifyDataSetChanged();
				
				}
				
			}
		});
		builder.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				
			}
		});
		builder.create().show();
		
	}
	
	
	//������Ӻ������ĶԻ���
	private void showAddNumDialog(String number) {
		AlertDialog.Builder builder = new Builder(CallSmsActivity.this);
		builder.setTitle("��Ӻ���������");
		final EditText et = new EditText(CallSmsActivity.this);
		et.setInputType(InputType.TYPE_CLASS_PHONE);
		et.setText(number);
		builder.setView(et);
		builder.setPositiveButton("���", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				String number =  et.getText().toString().trim();
				if(TextUtils.isEmpty(number)){
					Toast.makeText(getApplicationContext(), "���������벻��Ϊ��", 1).show();
					return ;
				}else{
					dao.add(number);
					//todo: ֪ͨlistview��������
					// ȱ��: ����ˢ������listview 
//							numbers = dao.getAllNumbers();
//							lv_call_sms_safe.setAdapter(new ArrayAdapter<String>(CallSmsActivity.this, R.layout.blacknumber_item, R.id.tv_blacknumber_item, numbers));
					numbers = dao.getAllNumbers();
					
					// ������������֪ͨlistview�������� 
					adapter.notifyDataSetChanged();
				
				}
				
			}
		});
		builder.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				
			}
		});
		builder.create().show();
	}


	private class CallSmsAdapter extends BaseAdapter{

		public int getCount() {
			// TODO Auto-generated method stub
			return numbers.size();
		}

		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return numbers.get(position);
		}

		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			View view ;
			if(convertView==null){
				view = View.inflate(CallSmsActivity.this, R.layout.blacknumber_item, null);
			}else{
				view = convertView;
			}
			tv = (TextView) view.findViewById(R.id.tv_blacknumber_item);
			tv.setText(numbers.get(position));
			return view;
		}
		
	}
}
