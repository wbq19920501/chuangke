package cn.com.easytaxi.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import cn.com.easytaxi.ETApp;
import cn.com.easytaxi.NewNetworkRequest;
import com.easytaxi.etpassengersx.R;
import cn.com.easytaxi.common.ActionLogUtil;
import cn.com.easytaxi.common.NetChecker;
import cn.com.easytaxi.onetaxi.TitleBar;
import cn.com.easytaxi.platform.YdBaseActivity;

public class SuggestionActivity extends YdBaseActivity {

	private TitleBar bar;
	private TextView suggest_app_info_version;
	private EditText suggest_info;
	private Button suggest_info_subbmit;
	private View cover_loading;
	private Handler handler;
	public static final int FEEDBACK_OK = 1000;
	
	@Override
	protected void initViews() {
		// TODO Auto-generated method stub
		suggest_app_info_version =(TextView) findViewById(R.id.suggest_app_info_version);
		suggest_info =(EditText) findViewById(R.id.suggest_info);
		suggest_info_subbmit =(Button) findViewById(R.id.suggest_info_subbmit);
		cover_loading = findViewById(R.id.cover_loading);
	}

	@Override
	protected void initListeners() {
		bar = new TitleBar(this);
		bar.setTitleName("意见反馈");
		// TODO Auto-generated method stub
		suggest_info_subbmit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String info = suggest_info.getText().toString();
				
				if(TextUtils.isEmpty(info)){
					Toast.makeText(SuggestionActivity.this, "请输入您的意见或建议", Toast.LENGTH_SHORT).show();
					
				}else{
					subbmitFeedBack(info);
					//写入日志
					ActionLogUtil.writeActionLog(SuggestionActivity.this,R.array.more_fankui_jianyi_submit,"");
				}
			}
		});
		
		
		suggest_info_subbmit.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View arg0) {
				// TODO Auto-generated method stub
				count++;
				if(count%2 == 0){
					Intent intent = new Intent(SuggestionActivity.this,SetIpActivity.class);
					startActivity(intent);
				}
				
				
				return false;
			}
		});
		
	}
	private static int count = 0 ;
	

	protected void subbmitFeedBack(String info){
		
		if(NetChecker.getInstance(getApplicationContext()).isAvailableNetwork()){
			
			cover_loading.setVisibility(View.VISIBLE);
			String cityId = getCityId();
			String passengerId = getPassengerId();
			NewNetworkRequest.feedBack(handler, cityId, passengerId, info ,  2);
		}else{
			
			Toast.makeText(SuggestionActivity.this, "网络不给力,请检查网络", Toast.LENGTH_SHORT).show();
		}

	}
	
	
	@Override
	protected void initUserData() {
		
		suggest_app_info_version.setText(ETApp.getInstance().getMobileInfo().getVerisonName());
		
		// TODO Auto-generated method stub
		handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				 if(msg.what == FEEDBACK_OK){
					 
					 cover_loading.setVisibility(View.GONE);
					 Toast.makeText(SuggestionActivity.this, "已提交服务器处理", Toast.LENGTH_SHORT).show();
					 finish();
				 }
				 
			}
		};
	}

	@Override
	protected void regReceiver() {
		// TODO Auto-generated method stub
	
	}

	@Override
	protected void unRegReceiver() {
		// TODO Auto-generated method stub

	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.p_suggest_activity);
		initViews();
		initUserData();
		initListeners();
	}

	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if(bar != null){
			bar.close();
			bar= null;
		}
		super.onDestroy();
	}
}
