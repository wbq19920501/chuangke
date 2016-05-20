package cn.com.easytaxi.drivingAgent;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import cn.com.easytaxi.ETApp;
import com.easytaxi.etpassengersx.R;
import cn.com.easytaxi.common.Callback;
import cn.com.easytaxi.onetaxi.TitleBar;
import cn.com.easytaxi.ui.BaseFragementActivity;
import cn.com.easytaxi.ui.view.CommonDialog;


/**
 * 发布代驾订单界面
 * @author Administrator
 *
 */
public class PublishDrivingAgentOrderActivity extends BaseFragementActivity{

	private TitleBar titleBar;
	private DrivingOrderPublishFragement bookPublishFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.publish_driving_agent_order);
		initViews(savedInstanceState);
		if (!ETApp.getInstance().isLogin()) {
			showRegistDialog(getBaseContext(), "提示", "请先注册以后再使用此功能", "立刻注册", "稍后再说", "cn.com.easytaxi.platform.RegisterActivity");
		}
	}


	@Override
	protected void onStart() {
		super.onStart();
	}
	
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	
	@Override
	protected void onDestroy() {

		if(titleBar != null){
			titleBar.close();
			titleBar = null;
		}
		super.onDestroy();
	}
	
	private void initViews(Bundle savedInstanceState) {
		titleBar = new TitleBar(this);
		titleBar.setTitleName("代驾");
        //添加内容
		bookPublishFragment = new DrivingOrderPublishFragement();
//		bookPublishFragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction()
                .add(R.id.content, bookPublishFragment).commit();
	}
 
	
	/**
	 * 显示注册dialog
	 */
	public  void showRegistDialog(final Context context,String titile,String content,String btn1,String btn2,final String gotoClassName){
			 Callback<Object> okBtnCallback = new Callback<Object>() {
					@Override
					public void handle(Object param) {
						try {
							Intent intent;
							intent = new Intent(context,Class.forName(gotoClassName));
							startActivity(intent);
							
							CommonDialog dialog= (CommonDialog)param;
							dialog.dismiss();
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
					}
				};
				
				Dialog dialog = new CommonDialog(PublishDrivingAgentOrderActivity.this, titile, content, btn1, btn2, R.layout.dlg_close,okBtnCallback,cancelBtnCallback);
				dialog.show();
		}
}

