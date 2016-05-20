package cn.com.easytaxi.platform;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.FilterQueryProvider;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;
import cn.com.easytaxi.AppLog;
import cn.com.easytaxi.NewNetworkRequest;
import cn.com.easytaxi.common.Callback;
import cn.com.easytaxi.onetaxi.TitleBar;
import cn.com.easytaxi.platform.common.Util;
import cn.com.easytaxi.service.MyService;
import cn.com.easytaxi.ui.ContactList;
import cn.com.easytaxi.ui.ContactList.ContactEntity;
import cn.com.easytaxi.ui.view.ChipsMultiAutoCompleteTextview;

import com.easytaxi.etpassengersx.R;

/**
 * 推荐给乘客
 * 
 * @author Administrator
 * 
 */
public class RemTaxiActiviy extends YdLocaionBaseActivity {

	private TitleBar bar;
	private ImageButton rmd_sel_num;
	private ChipsMultiAutoCompleteTextview rmd_name;
	private Button rmd_send;
	private Cursor mCursor;
	private String msm;
	private String msg;
	private TextView msg_info;
	private TextView msg_info_sum;

	//test
	private List<ContactEntity> sendContacts= new ArrayList<ContactList.ContactEntity>(12);
	private HashMap<String,ContactEntity> sendContactMap = new HashMap<String,ContactList.ContactEntity>(12);
	 
	public static final String[] CONTACT_PROJECTION = new String[] { Contacts._ID, Contacts.DISPLAY_NAME, Contacts.HAS_PHONE_NUMBER,
			Contacts.PHOTO_ID };

	class ResultDisplayer implements OnClickListener {
		String mMsg;
		String mMimeType;

		ResultDisplayer(String msg, String mimeType) {
			mMsg = msg;
			mMimeType = mimeType;
		}

		public void onClick(View v) {
			Intent intent = new Intent(RemTaxiActiviy.this, ContactList.class);
			startActivityForResult(intent, 1);
		}
	}

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.arg1 == 100) {
				// 短信推荐
				String res = (String) msg.obj;
				if(TextUtils.isEmpty(res)){
					Toast.makeText(RemTaxiActiviy.this, "网络不给力", Toast.LENGTH_SHORT).show();
//					startSendRemdInfo();
				}else{
					requestMyRmdinfo();
					//2013-05-13 13:45修改为非本地发短信	
//					startSendRemdInfo(); //deleted
				}
				uiContinue();
			}

			// 获取到了今天的推广信息
			if (msg.arg1 == 101) {
				String res = (String) msg.obj;
				
				if(TextUtils.isEmpty(res)){
					Toast.makeText(RemTaxiActiviy.this, "网络不给力", Toast.LENGTH_SHORT).show();
				}else{
					dispInfo(res);
				}
			}
			// 获取我的推广的结果
			if (msg.arg1 == 102) {
				String res = (String) msg.obj;
				
				if(TextUtils.isEmpty(res)){
					Toast.makeText(RemTaxiActiviy.this, "网络不给力", Toast.LENGTH_SHORT).show();
				}else{
					dispMyRmdInfo(res);
				}
			}
 
			
		};
	};



	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.p_remd_taxi);
		initViews();
		initListeners();
		initUserData();
		initConfig();
		
		
		if(!TextUtils.isEmpty(getCityId())){
			Intent service = new Intent(RemTaxiActiviy.this, MyService.class);	
			service.putExtra("cityId", getCityId());
			startService(service );
		}
	}
	 String myInfo = null;
	private ImageButton imageButton1_clear;
	private ImageButton imageButton2_clear;
	protected void dispMyRmdInfo(final String res) {
		
		try {
			JSONObject json = new JSONObject(res);
			
			if(json.getInt("error") == 0){
				myInfo = json.getString("msg");
			}
 
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		msg_info_sum.post(new Runnable() {
			
			@Override
			public void run() {
				msg_info_sum.setVisibility(View.VISIBLE);
				msg_info_sum.setText(myInfo);
			}
		});
		
	}

	/**
	 * 需要子类实现
	 */
	protected void initConfig() {
		// TODO Auto-generated method stub
		sendRmdContentRequest(2);
	}

	protected void dispInfo(String jsonString) {
		try {
			JSONObject json = new JSONObject(jsonString);
			msm = json.getString("msm");
			msg = json.getString("msg");

			displayMsm(msm);
			displayMsg(msg);

		} catch (JSONException e) {

			e.printStackTrace();
		}

	}

	protected void displayMsm(String msm2) {
		// TODO Auto-generated method stub

	}

	protected void displayMsg(String msg2) {

		if (msg_info != null) {
			msg_info.setText(msg2);
		}
	}

	@Override
	protected void initViews() {

		bar = new TitleBar(this);
		bar.setTitleName(getTitlebarName());
		bar.switchToCityButton();
		bar.getRightCityButton().setVisibility(View.GONE);
		bar.getRightHomeButton().setVisibility(View.GONE);
		bar.setBackCallback(new Callback<Object>() {

			@Override
			public void handle(Object param) {
				doBack();
			}
		});

		rmd_name = (ChipsMultiAutoCompleteTextview) findViewById(R.id.rmd_name);
		rmd_name.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
		rmd_sel_num = (ImageButton) findViewById(R.id.rmd_sel_num);
		rmd_send = (Button) findViewById(R.id.rmd_send);
		msg_info = (TextView) findViewById(R.id.msg_info);
		msg_info_sum = (TextView) findViewById(R.id.msg_info_sum);
		imageButton2_clear   = (ImageButton) findViewById(R.id.imageButton2_clear);
		imageButton1_clear  = (ImageButton) findViewById(R.id.imageButton1_clear);

	}

	/**
	 * 需要子类实现
	 * @return
	 */
	protected String getTitlebarName() {
		// TODO Auto-generated method stub
		return "推荐给司机";
	}

	@Override
	protected void initListeners() {

		imageButton1_clear.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				rmd_name.setText("");
				dispClearButton();
			}
		});
		imageButton2_clear.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				rmd_name.setText("");
				dispClearButton();
			}
		});
		
		rmd_name.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

				
				
				ContactEntity c = (ContactEntity) arg1.getTag();
				if (c != null) {
					AppLog.LogD(arg2 + ", id " + arg3 + "," + c.toString());
					sendContactMap.put(c.name, c);
					dispClearButton();
//					rmd_name.setChips();
//					sendContacts.add(c);
//					sendContactMap.add(c);
				}
			}
		});

		rmd_send.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				String text = rmd_name.getText().toString();
				if(TextUtils.isEmpty(text)){
					Toast.makeText(RemTaxiActiviy.this, "请输入您被推荐人号码", Toast.LENGTH_SHORT).show();
				}
				
				
//				String mobile = getUserPhoneNum();
				if (!isLogin()) {
					startRegActivity();
				} else {
					uiWait();
					uploadRemdInfo(getUploadType());

					 
				}
			}
		});
		rmd_sel_num.setOnClickListener(new ResultDisplayer("Selected phone", ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE));
	}

	/**
	 * 子类实现
	 * 
	 * @return
	 */
	protected int getUploadType() {

		return 1;
	}

	/**
	 * 乘客推荐给乘客0，乘客推荐给司机1 
	 * @param type
	 */
	protected void uploadRemdInfo(int type) {

//		String sendPeople = getPeoplenums();//rmd_name.getText().toString().replaceAll(" ", "");

		NewNetworkRequest.sendRmdInfoRequest(type, handler, getCityId(), getUserPhoneNum(),getPeoplenums());

	}
 
	protected  List<ContactEntity> getPeoplenums(){
	 
	 List<ContactEntity> contacts= new ArrayList<ContactList.ContactEntity>(12);
	 
		String[] names = rmd_name.getText().toString().replaceAll(" ", "").split(",");
		AppLog.LogD(rmd_name.getText().toString().replaceAll(" ", ""));
		StringBuilder sb =new StringBuilder();
		
		for(String name:names){
			if(!TextUtils.isEmpty(name)){
				if(TextUtils.isDigitsOnly(name) && name.matches(Util.REGEX_MOBILE)){
					sb.append(name).append(",");
					ContactEntity cc = new ContactEntity();
					cc.name = "";
					cc.phone = name;
					contacts.add(cc);
				}else{
					contacts.add(sendContactMap.get(name));
					sb.append(sendContactMap.get(name)).append(",");
				}
			}
			
			 
			
		}
		return contacts;
		
	}
	
	/**
	 * 使用个人的短信进行推荐
	 */
	protected void startSendRemdInfo() {
		
		new Thread(){
			public void run() {
				uiWait();
				List<ContactEntity> list = getPeoplenums();//rmd_name.getText().toString().replaceAll(" ", "");
			 
				for (ContactEntity ce : list) {
					if(ce == null)continue;
					String phone = ce.phone;
					
					if(!TextUtils.isEmpty(phone) && !TextUtils.isEmpty(msm) ){
						sendSms(phone, msm);
					}
				}
				uiContinue();
			};
		}.start();
		
		
	
		 
	}

	/**
	 * 需要子类实现
	 */
	protected void uiContinue() {
		
		rmd_send.post(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				rmd_send.setClickable(true);
				rmd_send.setEnabled(true);
				rmd_send.setText("推荐");
				Toast.makeText(RemTaxiActiviy.this, "服务器已接受到您的推荐信息", Toast.LENGTH_SHORT).show();
				rmd_name.setText("");
			}
		});
	}

	/**
	 * 需要子类实现
	 */
	protected void uiWait() {
		// TODO Auto-generated method stub
		
		rmd_send.post(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				AppLog.LogD("发送中");
				 rmd_send.setClickable(false);
				 rmd_send.setEnabled(false);
				 rmd_send.setText("发送中");

			}
		});
	
	}

	/**
	 * 需要子类实现
	 * @return
	 */
	protected String getRemdMsg() {
		return getResources().getString(R.string.remd_user_to_taxi);
	}

	@Override
	protected void initUserData() {

		ContentResolver content = getContentResolver();
		mCursor = content.query(Contacts.CONTENT_URI, CONTACT_PROJECTION, null, null, null);

		ContactListAdapter adapter = new ContactListAdapter(this, mCursor);
		rmd_name.setAdapter(adapter);
		requestMyRmdinfo();
	/*	rmd_name.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View view, MotionEvent event) {
				// TODO Auto-generated method stub
				float x = event.getX();
				
				if(event.getAction() == MotionEvent.ACTION_DOWN){
					
				}
				return false;
			}
		});*/
	}

	protected void sendRmdContentRequest(int type) {
		NewNetworkRequest.sendRmdContentRequest(type, handler, getCityId());
	}
	
	protected void requestMyRmdinfo(){
		String passenId = getUserPhoneNum();
		if(!TextUtils.isEmpty(passenId)){
			NewNetworkRequest.sendRmdMyinfoRequest(handler,passenId,getCityId());
		}
	}
	

	@Override
	protected void regReceiver() {

	}

	@Override
	protected void unRegReceiver() {

	}
 
	
	@Override
	protected void onDestroy() {
		if (bar != null) {
			bar.close();
		}
		if (mCursor != null) {
			mCursor.close();
		}
		super.onDestroy();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data != null) {

			Bundle extras = data.getExtras();
			List<ContactEntity> selectContactList = extras.getParcelableArrayList("selcontact");
			final int len = selectContactList.size();

			if (len > 0) {
				sendContactMap.clear();

			} else {
				return;
			}
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < len; i++) {
				ContactEntity c = selectContactList.get(i);
				AppLog.LogD(c.toString());
				sb.append(c.name).append(",");
				sendContactMap.put(c.name, c);

//				rmd_name.append(c.name+",");
//				rmd_name.setChips();
//				rmd_name.setChips();
 
			}
					
			fillAutoInfoToView(sb.toString());
		}
	}

	private void fillAutoInfoToView(String string) {
		
		rmd_name.setText("");
		rmd_name.append(string);
//		rmd_name.setChips();
		dispClearButton();
		}

	private void dispClearButton() {
		int lineCount = rmd_name.getLineCount();
		 if(lineCount ==1 ){
			 imageButton1_clear.setVisibility(View.GONE);
			 if(!TextUtils.isEmpty( rmd_name.getText().toString())){
				 imageButton2_clear.setVisibility(View.VISIBLE);
			 }else{
				 imageButton2_clear.setVisibility(View.GONE);
			 }
		 }else{
			 imageButton2_clear.setVisibility(View.GONE);
			 if(!TextUtils.isEmpty( rmd_name.getText().toString())){
				 imageButton1_clear.setVisibility(View.VISIBLE);
			 }else{
				 imageButton1_clear.setVisibility(View.GONE);
			 }
			 
			 
		 }
		
	}

	public static class ContactListAdapter extends CursorAdapter implements Filterable {

		public ContactListAdapter(Context context, Cursor c) {
			super(context, c);
			mContent = context.getContentResolver();

		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			final LayoutInflater inflater = LayoutInflater.from(context);
			View v = inflater.inflate(R.layout.contact_list_item_auto, null);
			/*
			 * TextView tvName = (TextView) v.findViewById(R.id.auto_name);
			 * TextView tvNum = (TextView) v.findViewById(R.id.auto_num);
			 * 
			 * tvName.setText(cursor.getString(COLUMN_DISPLAY_NAME));
			 * tvNum.setText(cursor.getString(COLUMN_DISPLAY_NUM));
			 */
			return v;
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			TextView tvName = (TextView) view.findViewById(R.id.auto_name);
			TextView tvNum = (TextView) view.findViewById(R.id.auto_num);
			final String name = cursor.getString(COLUMN_DISPLAY_NAME);
			tvName.setText(name);

			Cursor c = mContent.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, new String[] {
					ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER }, Phone.CONTACT_ID
					+ " = " + cursor.getInt(COLUMN_CONTACT_ID), null, null);

			if (c != null) {
				ContactEntity contact = new ContactEntity();
				contact.isSelected = false;
				contact.name = name;
				if (c.getCount() > 0) {
					c.moveToFirst();
					String number = c.getString(1);
					if (number.startsWith("+86")) {
						number = number.substring(3); // 去掉+86
					}
					number.replaceAll(" ", "");
					contact.phone = number;
					tvNum.setText(number);
				}

				view.setTag(contact);
				c.close();
				c = null;
			}

			// ((TextView) view).setText(cursor.getString(COLUMN_DISPLAY_NAME));
		}

		@Override
		public String convertToString(Cursor cursor) {
//
//			Cursor c = mContent.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, new String[] {
//					ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER }, Phone.CONTACT_ID
//					+ " = " + cursor.getInt(COLUMN_CONTACT_ID), null, null);
//			String numbe1 = "";
//			if (c != null) {
//
//				if (c.getCount() > 0) {
//					c.moveToFirst();
//					numbe1 = c.getString(1);
//					if (numbe1.startsWith("+86")) {
//						numbe1 = numbe1.substring(3); // 去掉+86
//					}
//					numbe1.replaceAll(" ", "");
//
//				}
//
//				c.close();
//				c = null;
//			}

			return cursor.getString(COLUMN_DISPLAY_NAME);
		}

		@Override
		public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
			if (constraint == null)
				return null;

			FilterQueryProvider filter = getFilterQueryProvider();
			if (filter != null) {
				return filter.runQuery(constraint);
			}
			AppLog.LogD(constraint + "");
			Uri uri = Uri.withAppendedPath(Contacts.CONTENT_FILTER_URI, Uri.encode(constraint.toString()));

			return mContent.query(uri, CONTACT_PROJECTION, null, null, null);
		}

		private ContentResolver mContent;
	}

	private static final int COLUMN_DISPLAY_NAME = 1;
	private static final int COLUMN_DISPLAY_NUM = 0;
	private static final int COLUMN_CONTACT_ID = 0;
}
