package cn.com.easytaxi.message;

import com.easytaxi.etpassengersx.R;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;

public class BaseDialogActivity extends Activity{
	public String title;
	public String content;
	public String btnOkText;
	public String btnCancleText;
	public String gotoClassName;
	
	public String url;
	private Dialog dialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alarm_book);
	}
	
	/**
	 *	初始化对话框
	 */
	public void setDialog(Dialog dialog){
		this.dialog = dialog;
	}
	
	/**
	 *  显示对话框
	 */
	public void showDialog(){
		if(this.dialog != null){
			if(!dialog.isShowing()){
				dialog.show();
			}
		}
	}
	
	/**
	 * 初始化数据
	 * @param intent
	 */
	public void initData(Intent intent) {
		try{
			Bundle bundle = intent.getExtras();
			title = bundle.getString("title");
			content = bundle.getString("content");
			btnOkText = bundle.getString("btnOkText");
			btnCancleText = bundle.getString("btnCancleText");
			gotoClassName = bundle.getString("gotoClassName");
			
			url = bundle.getString("url");
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putString("title",title);
		outState.putString("content",content);
		outState.putString("btnOkText",btnOkText);
		outState.putString("btnCancleText",btnCancleText);
		outState.putString("gotoClassName",gotoClassName);
		outState.putString("url",url);
		super.onSaveInstanceState(outState);
	}


	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		try{
			if(savedInstanceState !=null){
				title = savedInstanceState.getString("title");
				content = savedInstanceState.getString("content");
				btnOkText = savedInstanceState.getString("btnOkText");
				btnCancleText = savedInstanceState.getString("btnCancleText");
				gotoClassName = savedInstanceState.getString("gotoClassName");
				url = savedInstanceState.getString("url");
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		super.onRestoreInstanceState(savedInstanceState);
	}
   
	/**
	 * 震动提醒用户
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
}
