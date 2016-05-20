package cn.com.easytaxi.platform;

import com.easytaxi.etpassengersx.R;
import cn.com.easytaxi.book.BookBean;
import cn.com.easytaxi.common.Callback;
import cn.com.easytaxi.ui.view.CommonDialog;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.widget.TextView;
import android.widget.Toast;

public class AlarmBookActivity extends Activity {
	TextView alarm_book_text_title;
	private BookBean bookBean;
	
	private String alarmInfo="�����⣬���ж�����Ҫ����";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alarm_book);
		
		initData(savedInstanceState); 
		showDialog(getBaseContext(),getString(R.string.et_name)+"��ʾ��",alarmInfo, "���̴���", "�Ժ���˵", "cn.com.easytaxi.book.NewBookDetail");
		vibrateInfo();
	}
	
	private void initData(Bundle savedInstanceState) {
		try{
			Intent intent = getIntent();
			bookBean =(BookBean) intent.getExtras().getSerializable("book");
			alarmInfo = intent.getExtras().getString("alarmInfo");
		}catch(Exception e){
			e.printStackTrace();
			alarmInfo ="�����⣬���ж�����Ҫ����";
		}
		if(null == bookBean){
			try{
				if(savedInstanceState !=null){
					bookBean = (BookBean) savedInstanceState.getSerializable("bookBean");
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		} 
	}
	
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		if(null != bookBean){
			outState.putSerializable("bookBean", bookBean);
		}
		super.onSaveInstanceState(outState);
	}


	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		try{
			if(savedInstanceState !=null){
				bookBean = (BookBean) savedInstanceState.getSerializable("bookBean");
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		super.onRestoreInstanceState(savedInstanceState);
	}


	/**
	 * 
	 */
	public  void showDialog(final Context context,String titile,String content,String btn1,String btn2,final String gotoClassName){
			 Callback<Object> okBtnCallback = new Callback<Object>() {
					@Override
					public void handle(Object param) {
						try {
							if(null != bookBean){
								Intent intent;
								intent = new Intent(context,Class.forName(gotoClassName));
								intent.putExtra("book",bookBean);
								startActivity(intent);
							}else{
								Toast.makeText(AlarmBookActivity.this, "ϵͳ��������Դ���������ֶ�����"+getResources().getString(R.string.et_name)+"������...", Toast.LENGTH_LONG).show();
							}
							
							CommonDialog dialog= (CommonDialog)param;
							dialog.dismiss();
							
							AlarmBookActivity.this.finish();
						} catch (ClassNotFoundException e) {
							e.printStackTrace();
						}
					}
				};
				
				Callback<Object> cancelBtnCallback = new Callback<Object>() {
					@Override
					public void handle(Object param) {
						CommonDialog dialog= (CommonDialog)param;
						dialog.dismiss();
						AlarmBookActivity.this.finish();
					}
				};
				
				Dialog dialog = new CommonDialog(AlarmBookActivity.this, titile, content, btn1, btn2, R.layout.dlg_close,okBtnCallback,cancelBtnCallback);
				//����dialog������ؼ�����ʧ
				dialog.setOnKeyListener(((CommonDialog)dialog).keylistener);
				dialog.show();
		}
	
	/**
	 * �������û�
	 */
	public void vibrateInfo(){
		try{
			Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
		    long[] pattern = {200, 300, 200, 300,200, 300,600, 300, 200, 300,200, 300}; // OFF/ON/OFF/ON                
		    vibrator.vibrate(pattern, -1);
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		AlarmBookActivity.this.finish();
	}
}