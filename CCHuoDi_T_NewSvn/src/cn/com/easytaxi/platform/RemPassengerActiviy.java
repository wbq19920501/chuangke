package cn.com.easytaxi.platform;

import com.easytaxi.etpassengersx.R;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.widget.Toast;


/**
 * �Ƽ���˾��
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
		return "�Ƽ����˿�";
	}

	@Override
	protected String getRemdMsg() {
		 
		return getResources().getString(R.string.remd_user_to_user);
	}
	 
	/**
	 * ��Ҫ����ʵ��
	 */
	protected void initConfig() {
		// TODO Auto-generated method stub
		sendRmdContentRequest(1);
	}
	
	protected int getUploadType() {

		return 0;
	}
	
	
 
	
}
