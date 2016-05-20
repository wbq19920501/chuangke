package cn.com.easytaxi.platform;

import com.easytaxi.etpassengersx.R;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.widget.Toast;


/**
 * 推荐给司机
 * @author Administrator
 *
 */
public class RemPassengerActiviy extends RemTaxiActiviy {
	
 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	protected String getTitlebarName() {
		// TODO Auto-generated method stub
		return "推荐给乘客";
	}

	@Override
	protected String getRemdMsg() {
		 
		return getResources().getString(R.string.remd_user_to_user);
	}
	 
	/**
	 * 需要子类实现
	 */
	protected void initConfig() {
		// TODO Auto-generated method stub
		sendRmdContentRequest(1);
	}
	
	protected int getUploadType() {

		return 0;
	}
	
	
 
	
}
