package cn.com.easytaxi.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.easytaxi.etpassengersx.R;
import cn.com.easytaxi.client.common.ConfigUtil;

public class SetIpActivity extends Activity {

	private EditText editText1;
	private EditText editText2;
	private Button button1;
	private Button button2;
	private Button button3;
	private EditText editText3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.p_setip);
		editText1 = (EditText) findViewById(R.id.editText1);
		editText2 = (EditText) findViewById(R.id.editText2);
		editText3 = (EditText) findViewById(R.id.editText3);
		button1 = (Button) findViewById(R.id.button1);
		button2 = (Button) findViewById(R.id.button2);
		button3 = (Button) findViewById(R.id.button3);
		
		button1.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
//				ConfigUtil.TCP_PORT_03 = Integer.valueOf(editText2.getText().toString());
//				ConfigUtil.UDP_PORT_03 = Integer.valueOf(editText2.getText().toString());
//				ConfigUtil.ip = 	 (editText1.getText().toString());
//				HashMap<String, String>  map = ConfigUtil.configUtil.getMap();
//				 
				String  IP = editText1.getText().toString();
				String TCP_PORT = editText2.getText().toString();
				String UDP_PORT = editText3.getText().toString();
				
				ConfigUtil.setString("IP", IP);
				ConfigUtil.setString("TCP_PORT_30", TCP_PORT);
				ConfigUtil.setString("UDP_PORT_30", UDP_PORT);
				
				cn.com.easytaxi.platform.common.common.Config.SERVER_IP = IP;
				cn.com.easytaxi.platform.common.common.Config.SERVER_UDP_PORT =Integer.valueOf(TCP_PORT) ;
				cn.com.easytaxi.platform.common.common.Config.SERVER_TCP_PORT =Integer.valueOf(UDP_PORT) ; ;
				
				Toast.makeText(SetIpActivity.this, ConfigUtil.getString("IP")+ ","+ ConfigUtil.getInt("TCP_PORT_30") ,1000).show();
				Toast.makeText(SetIpActivity.this, cn.com.easytaxi.platform.common.common.Config.SERVER_IP   ,1000).show();
				
				
//				
//				map.put("TCP_PORT", editText2.getText().toString());
//				map.put("UDP_PORT", editText2.getText().toString());
////
//				map.put("TCP_PORT_30", editText2.getText().toString());
//				map.put("UDP_PORT_30", editText2.getText().toString());
//				
//				
//			 	map.put("IP",editText1.getText().toString());
//				map.put("IP_30",editText1.getText().toString()); 
//						
			}
		});
		
		button2.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
		button3.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				String s = "taxiserver.easytaxi.com.cn";
				int a = 13512;
				String port = "13512";
				
				String  IP = s;
				String TCP_PORT = port;
				String UDP_PORT = port;
				
				ConfigUtil.setString("IP", IP);
				ConfigUtil.setString("TCP_PORT_30", TCP_PORT);
				ConfigUtil.setString("UDP_PORT_30", UDP_PORT);
				
				cn.com.easytaxi.platform.common.common.Config.SERVER_IP = s;
				cn.com.easytaxi.platform.common.common.Config.SERVER_UDP_PORT =13512 ;
				cn.com.easytaxi.platform.common.common.Config.SERVER_TCP_PORT =13512 ;
				
				Toast.makeText(SetIpActivity.this, ConfigUtil.getString("IP")+ ","+ ConfigUtil.getInt("TCP_PORT_30") ,1000).show();
				Toast.makeText(SetIpActivity.this, cn.com.easytaxi.platform.common.common.Config.SERVER_IP   ,1000).show();
				
				
				
			
				
				//ConfigUtil.TCP_PORT_03 = a;
				//ConfigUtil.UDP_PORT_03 =a;// Integer.valueOf(editText2.getText().toString());
//				ConfigUtil.ip =s;// 	 (editText1.getText().toString());
//				HashMap<String, String>  map = ConfigUtil.configUtil.getMap();
				 
 
//
//				map.put("TCP_PORT_30", port);
//				map.put("UDP_PORT_30", port);
//				
//				
//			 	map.put("IP",s);
//				map.put("IP_30",s); 
				
			}
		});
		

	}
}
